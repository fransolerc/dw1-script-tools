package com.dw1demo.parser

import com.dw1demo.model.Instruction
import com.dw1demo.model.ScriptBlock
import com.dw1demo.model.ScriptData
import com.dw1demo.model.SectionBlock
import java.io.File

class ScriptParser {

    private val opcodesThatTakeRawText = setOf("showTextbox")

    fun parse(file: File): ScriptData {
        if (!file.exists()) return ScriptData()

        val state = ParseState()

        file.useLines { lines ->
            lines.forEachIndexed { index, line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isNotEmpty()) {
                    processLine(trimmedLine, index, state)
                }
            }
        }

        state.flushCurrentScript()
        return ScriptData(state.scripts.toMap())
    }

    private fun processLine(line: String, index: Int, state: ParseState) {
        when {
            line.startsWith("==") -> state.startNewScript(line)
            line.startsWith("Section_") -> state.startNewSection(line)
            else -> parseInstruction(line, index, state)
        }
    }

    private fun parseInstruction(line: String, index: Int, state: ParseState) {
        val parts = line.split("\\s+".toRegex())
        if (parts.size < 2) return

        val address = parts[0].toIntOrNull() ?: return
        val opcode = parts[1]
        val args = extractArgs(line, opcode, parts)

        state.addInstruction(Instruction(address, opcode, args, index + 1))
    }

    private fun extractArgs(line: String, opcode: String, parts: List<String>): List<String> = when {
        opcode in opcodesThatTakeRawText && parts.size > 2 -> listOf(line.substringAfter(opcode).trim())
        parts.size > 2 -> parts.subList(2, parts.size)
        else -> emptyList()
    }

    private fun buildScriptBlock(id: Int, info: String, sections: Map<String, List<Instruction>>): ScriptBlock {
        return ScriptBlock(
            id = id,
            info = info,
            sections = sections.mapValues { (sectionId, instrs) ->
                SectionBlock(sectionId.toInt(), instrs)
            }
        )
    }

    private inner class ParseState {
        val scripts = mutableMapOf<String, ScriptBlock>()
        private var currentScriptId = -1
        private var currentScriptInfo = ""
        private var currentSectionId = -1
        private val currentSections = mutableMapOf<String, MutableList<Instruction>>()

        fun startNewScript(line: String) {
            flushCurrentScript()
            val parts = line.split("\\s+".toRegex())
            currentScriptId = parts.getOrNull(3)?.toIntOrNull() ?: -1
            currentScriptInfo = line
            currentSectionId = -1
            currentSections.clear()
        }

        fun startNewSection(line: String) {
            currentSectionId = line.substringAfter("_").substringBefore(":").toIntOrNull() ?: -1
            currentSections[currentSectionId.toString()] = mutableListOf()
        }

        fun addInstruction(instruction: Instruction) {
            currentSections[currentSectionId.toString()]?.add(instruction)
        }

        fun flushCurrentScript() {
            if (currentScriptId < 0) return
            scripts[currentScriptId.toString()] = buildScriptBlock(
                id = currentScriptId,
                info = currentScriptInfo,
                sections = currentSections.mapValues { it.value.toList() }
            )
        }
    }
}