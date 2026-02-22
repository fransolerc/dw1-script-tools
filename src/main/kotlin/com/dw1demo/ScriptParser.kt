package com.dw1demo

import java.io.File

class ScriptParser {

    fun parse(file: File): ScriptData {
        val scriptData = ScriptData()
        var currentScript: ScriptBlock? = null
        var currentSection: SectionBlock? = null
        
        if (!file.exists()) return scriptData

        file.useLines { lines ->
            lines.forEachIndexed { index, line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isEmpty()) return@forEachIndexed

                // Identify Script ID header
                // Example: "== Script ID 0 == 800"
                if (trimmedLine.startsWith("==")) {
                    val parts = trimmedLine.split("\\s+".toRegex())
                    val id = parts.getOrNull(3)?.toIntOrNull() ?: -1
                    val info = trimmedLine
                    currentScript = ScriptBlock(id, info)
                    scriptData.scripts[id.toString()] = currentScript
                    currentSection = null // Reset section on new script
                    return@forEachIndexed
                }

                // Identify Section marker
                // Example: "Section_0:"
                if (trimmedLine.startsWith("Section_")) {
                    val id = trimmedLine.substringAfter("_").substringBefore(":").toIntOrNull() ?: -1
                    if (currentScript != null) {
                        currentSection = SectionBlock(id)
                        currentScript.sections[id.toString()] = currentSection!!
                    }
                    return@forEachIndexed
                }

                // Standard Instruction
                val parts = trimmedLine.split("\\s+".toRegex())
                if (parts.size < 2) return@forEachIndexed

                val address = parts[0].toIntOrNull() ?: -1
                val opcode = parts[1]
                val args = if (parts.size > 2) parts.subList(2, parts.size).toMutableList() else listOf<String>()

                val instruction = Instruction(address, opcode, args, index + 1)
                
                // Add to current section if exists, otherwise to a default "unsectioned" block? 
                // In this dump, everything should be in a section.
                currentSection?.instructions?.add(instruction)
            }
        }
        return scriptData
    }
}
