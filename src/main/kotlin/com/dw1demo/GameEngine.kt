package com.dw1demo

class GameEngine {
    // Simulated game state
    private val pStats = mutableMapOf<Int, Int>() // Persistent variables (pstat)
    private val loadedDigimons = mutableListOf<Int>() // Digimons loaded in memory
    private var currentScriptId = 0

    fun execute(instructions: List<Instruction>) {
        println("\n--- Starting script execution ---")
        
        // Create address map for quick jumps
        // Key: Original address (e.g., 1128), Value: Instruction index in the list (e.g., 0)
        val addressMap = instructions.mapIndexed { index, instr -> instr.address to index }.toMap()

        var pc = 0 // Program Counter (current instruction index)
        
        while (pc < instructions.size) {
            val instruction = instructions[pc]

            var jumpTarget: Int? = null

            when (instruction.opcode) {
                "setScript" -> {
                    if (instruction.args.isNotEmpty()) {
                        currentScriptId = instruction.args[0].toIntOrNull() ?: 0
                        println("Script ID set to: $currentScriptId")
                    }
                }
                "setPStat" -> {
                    if (instruction.args.size >= 2) {
                        val id = instruction.args[0].toIntOrNull()
                        val value = instruction.args[1].toIntOrNull()
                        if (id != null && value != null) {
                            pStats[id] = value
                            println("PStat[$id] = $value")
                        }
                    }
                }
                "setBGM" -> {
                    if (instruction.args.isNotEmpty()) {
                        println("Playing BGM ID: ${instruction.args[0]}")
                    }
                }
                "storeDate" -> {
                     if (instruction.args.isNotEmpty()) {
                        println("Storing date/value: ${instruction.args[0]}")
                    }
                }
                "loadDigimon" -> {
                    if (instruction.args.isNotEmpty()) {
                        val digimonId = instruction.args[0].toIntOrNull()
                        if (digimonId != null) {
                            loadedDigimons.add(digimonId)
                            println("Loading Digimon model ID: $digimonId")
                        }
                    }
                }
                "setDigimon" -> {
                     if (instruction.args.size >= 3) {
                        println("Configuring Digimon ${instruction.args[0]} in slot ${instruction.args[1]} with value ${instruction.args[2]}")
                    }
                }
                "if" -> {
                    // Format: if pstat(107) < 7 OR pstat(107) >= 19 then 1186
                    // The last argument is usually the jump address if the condition is met
                    val targetAddressStr = instruction.args.lastOrNull()
                    val targetAddress = targetAddressStr?.toIntOrNull()
                    
                    if (targetAddress != null) {
                        println("IF condition found. (Evaluation pending). Jumping to $targetAddress by default for testing.")
                        // HERE WOULD BE THE REAL EVALUATION LOGIC
                        // For now, we force the jump to test the mechanism
                        jumpTarget = targetAddress
                    }
                }
                "jumpTo" -> {
                    val targetAddress = instruction.args[0].toIntOrNull()
                    if (targetAddress != null) {
                        println("Jumping to address: $targetAddress")
                        jumpTarget = targetAddress
                    }
                }
                "endSection" -> {
                    println("End of section reached.")
                    return // Terminate execution
                }
                else -> {
                    println("Unknown command: ${instruction.opcode} ${instruction.args}")
                }
            }

            if (jumpTarget != null) {
                val newPc = addressMap[jumpTarget]
                if (newPc != null) {
                    pc = newPc
                    continue // Jump to the start of the loop with the new PC
                } else {
                    println("ERROR: Jump address $jumpTarget not found in loaded script.")
                }
            }

            pc++
        }
        println("--- End of execution ---")
    }
}
