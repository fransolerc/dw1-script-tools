package com.dw1demo

import java.io.File

class ScriptParser {

    fun parse(file: File): List<Instruction> {
        val instructions = mutableListOf<Instruction>()
        
        if (!file.exists()) return instructions

        file.useLines { lines ->
            lines.forEachIndexed { index, line ->
                val trimmedLine = line.trim()
                
                // Ignore empty lines or comments/labels that are not direct instructions
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("==") || trimmedLine.startsWith("Section_")) {
                    return@forEachIndexed
                }

                val parts = trimmedLine.split("\\s+".toRegex())
                if (parts.size < 2) {
                    return@forEachIndexed // Needs at least address and opcode
                }

                // The first token is the address, the second is the opcode
                // Example: "001128 setScript 1 0"
                val address = parts[0].toIntOrNull() ?: -1
                val opcode = parts[1]
                val args = if (parts.size > 2) parts.subList(2, parts.size) else emptyList()

                instructions.add(Instruction(address, opcode, args, index + 1))
            }
        }
        return instructions
    }
}
