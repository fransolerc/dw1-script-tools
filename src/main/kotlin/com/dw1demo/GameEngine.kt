package com.dw1demo

class GameEngine {
    // Game State
    val pStats = mutableMapOf<Int, Int>()
    val loadedDigimons = mutableListOf<Int>()
    var currentScriptId = 0
    
    // Execution State
    private var instructions: List<Instruction> = emptyList()
    private var addressMap: Map<Int, Int> = emptyMap()
    var pc = 0 // Program Counter
    var isRunning = false
    var lastLog = "" // To show on screen

    fun loadScript(newInstructions: List<Instruction>) {
        instructions = newInstructions
        addressMap = instructions.mapIndexed { index, instr -> instr.address to index }.toMap()
        pc = 0
        isRunning = true
        pStats.clear()
        loadedDigimons.clear()
        println("Script loaded: ${instructions.size} instructions.")
    }

    fun update() {
        if (!isRunning || pc >= instructions.size) {
            isRunning = false
            return
        }

        val instruction = instructions[pc]
        executeInstruction(instruction)
        
        // Advance PC if not modified by jump
        // (We'll handle jumps inside executeInstruction by modifying 'pc' directly if needed, 
        // but for now let's assume simple sequential execution + manual jump handling)
        // Ideally, executeInstruction returns the next PC or we check if it changed.
    }

    private fun executeInstruction(instruction: Instruction) {
        // Default next step
        var nextPc = pc + 1

        when (instruction.opcode) {
            "setScript" -> {
                if (instruction.args.isNotEmpty()) {
                    currentScriptId = instruction.args[0].toIntOrNull() ?: 0
                    log("Script ID set to: $currentScriptId")
                }
            }
            "setPStat" -> {
                if (instruction.args.size >= 2) {
                    val id = instruction.args[0].toIntOrNull()
                    val value = instruction.args[1].toIntOrNull()
                    if (id != null && value != null) {
                        pStats[id] = value
                        log("PStat[$id] = $value")
                    }
                }
            }
            "setBGM" -> {
                if (instruction.args.isNotEmpty()) {
                    log("Playing BGM ID: ${instruction.args[0]}")
                }
            }
            "storeDate" -> {
                 if (instruction.args.isNotEmpty()) {
                    log("Storing date/value: ${instruction.args[0]}")
                }
            }
            "loadDigimon" -> {
                if (instruction.args.isNotEmpty()) {
                    val digimonId = instruction.args[0].toIntOrNull()
                    if (digimonId != null) {
                        loadedDigimons.add(digimonId)
                        log("Loading Digimon model ID: $digimonId")
                    }
                }
            }
            "setDigimon" -> {
                 if (instruction.args.size >= 3) {
                    log("Configuring Digimon ${instruction.args[0]} in slot ${instruction.args[1]}")
                }
            }
            "if" -> {
                val targetAddressStr = instruction.args.lastOrNull()
                val targetAddress = targetAddressStr?.toIntOrNull()
                
                if (targetAddress != null) {
                    log("IF condition. Jumping to $targetAddress (Testing)")
                    val jumpIndex = addressMap[targetAddress]
                    if (jumpIndex != null) {
                        nextPc = jumpIndex
                    }
                }
            }
            "jumpTo" -> {
                val targetAddress = instruction.args[0].toIntOrNull()
                if (targetAddress != null) {
                    log("Jumping to address: $targetAddress")
                    val jumpIndex = addressMap[targetAddress]
                    if (jumpIndex != null) {
                        nextPc = jumpIndex
                    }
                }
            }
            "endSection" -> {
                log("End of section reached.")
                isRunning = false
            }
            else -> {
                // log("Unknown: ${instruction.opcode}")
            }
        }
        
        pc = nextPc
    }

    private fun log(message: String) {
        println(message)
        lastLog = message
    }
    
    fun getCurrentInstruction(): String {
        if (pc < instructions.size) {
            return instructions[pc].toString()
        }
        return "End"
    }
}
