package com.dw1demo

import com.badlogic.gdx.utils.Json
import java.io.File

class GameEngine {
    // Game State
    val pStats = mutableMapOf<Int, Int>()
    val loadedDigimons = mutableListOf<Int>()
    var currentScriptId = 0
    
    // Script Data
    private var manifest: ScriptManifest? = null
    private var scriptsDir: File? = null
    var currentScript: ScriptBlock? = null
    var currentSection: SectionBlock? = null
    
    // Execution State
    private var addressMap: Map<Int, Int> = emptyMap() // Maps address to index in current section
    var pc = 0 // Program Counter (index within current section)
    var isRunning = false
    var lastLog = "" // To show on screen

    fun loadManifest(manifestFile: File, scriptsFolder: File) {
        try {
            val json = Json()
            this.manifest = json.fromJson(ScriptManifest::class.java, manifestFile.readText())
            this.scriptsDir = scriptsFolder
            log("Manifest loaded: ${manifest?.entries?.size} script entries.")
        } catch (e: Exception) {
            log("Error loading manifest: ${e.message}")
        }
    }

    fun startScript(scriptId: Int, sectionId: Int = 0) {
        val entry = manifest?.entries?.get(scriptId.toString())
        if (entry == null) {
            log("Error: Script $scriptId not found in manifest.")
            return
        }

        // Lazy load script file
        val scriptFile = File(scriptsDir, entry.fileName)
        if (scriptFile.exists()) {
            try {
                val json = Json()
                currentScript = json.fromJson(ScriptBlock::class.java, scriptFile.readText())
                startSection(sectionId)
            } catch (e: Exception) {
                log("Error loading script file ${entry.fileName}: ${e.message}")
            }
        } else {
            log("Error: Script file ${entry.fileName} not found.")
        }
    }

    fun startSection(sectionId: Int) {
        val section = currentScript?.sections?.get(sectionId.toString())
        if (section == null) {
            log("Error: Section $sectionId not found in script ${currentScript?.id}.")
            isRunning = false
            return
        }
        currentSection = section
        // Rebuild address map for the current section
        addressMap = section.instructions.mapIndexed { index, instr -> instr.address to index }.toMap()
        pc = 0
        isRunning = true
        log("Started Script ${currentScript?.id}, Section $sectionId")
    }

    fun update() {
        val section = currentSection
        if (!isRunning || section == null || pc >= section.instructions.size) {
            isRunning = false
            return
        }

        val instruction = section.instructions[pc]
        executeInstruction(instruction)
    }

    private fun executeInstruction(instruction: Instruction) {
        var nextPc = pc + 1

        when (instruction.opcode) {
            "setScript" -> {
                // Historically setScript 1 0 might mean "I am script 1, section 0"
                // but in the engine we use startScript/startSection to navigate.
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
            "loadDigimon" -> {
                if (instruction.args.isNotEmpty()) {
                    val digimonId = instruction.args[0].toIntOrNull()
                    if (digimonId != null) {
                        loadedDigimons.add(digimonId)
                        log("Loading Digimon model ID: $digimonId")
                    }
                }
            }
            "if" -> {
                val targetAddress = instruction.args.lastOrNull()?.toIntOrNull()
                if (targetAddress != null) {
                    val jumpIndex = addressMap[targetAddress]
                    if (jumpIndex != null) {
                        log("IF condition. Jumping to $targetAddress")
                        nextPc = jumpIndex
                    } else {
                        log("Warning: IF target $targetAddress not found in current section.")
                    }
                }
            }
            "jumpTo" -> {
                val targetAddress = instruction.args[0].toIntOrNull()
                if (targetAddress != null) {
                    val jumpIndex = addressMap[targetAddress]
                    if (jumpIndex != null) {
                        log("Jumping to address: $targetAddress")
                        nextPc = jumpIndex
                    } else {
                        log("Warning: Jump target $targetAddress not found in current section.")
                    }
                }
            }
            "endSection" -> {
                log("End of section reached.")
                isRunning = false
            }
            "showTextbox" -> {
                log("Dialogue: ${instruction.args.joinToString(" ")}")
            }
            else -> {

            }
        }
        
        pc = nextPc
    }


    private fun log(message: String) {
        println(message)
        lastLog = message
    }
    
    fun getCurrentInstruction(): String {
        val section = currentSection
        if (section != null && pc < section.instructions.size) {
            return section.instructions[pc].toString()
        }
        return "End"
    }
}
