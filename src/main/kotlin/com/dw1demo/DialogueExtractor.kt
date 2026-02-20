package com.dw1demo

import java.io.File
import java.io.FileWriter
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter

class DialogueExtractor {

    fun extractDialogues(scriptFile: File, outputFile: File) {
        val dialogues = mutableListOf<DialogueEntry>()
        var currentSpeakerId = -1 // -1 indica hablante desconocido o por defecto

        if (!scriptFile.exists()) {
            println("Error: Script file not found at ${scriptFile.absolutePath}")
            return
        }

        scriptFile.useLines { lines ->
            lines.forEach { line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("==") || trimmedLine.startsWith("Section_")) {
                    return@forEach
                }

                val parts = trimmedLine.split("\\s+".toRegex())
                if (parts.size < 2) return@forEach

                // Extraer dirección (primer token)
                val address = parts[0].toIntOrNull() ?: -1
                val opcode = parts[1]

                when (opcode) {
                    "setDialogOwner" -> {
                        // setDialogOwner <id>
                        if (parts.size > 2) {
                            currentSpeakerId = parts[2].toIntOrNull() ?: -1
                        }
                    }
                    "showTextbox" -> {
                        // showTextbox <texto...>
                        // El texto empieza después del opcode y puede contener espacios
                        val textStartIndex = trimmedLine.indexOf("showTextbox") + "showTextbox".length
                        if (textStartIndex < trimmedLine.length) {
                            val text = trimmedLine.substring(textStartIndex).trim()
                            // Limpiar caracteres de escape comunes si es necesario (ej: \n)
                            val cleanText = text.replace("\\n", "\n")
                            
                            dialogues.add(DialogueEntry(address, currentSpeakerId, cleanText))
                        }
                    }
                }
            }
        }

        // Guardar en JSON usando LibGDX Json
        val json = Json()
        json.setOutputType(JsonWriter.OutputType.json)
        val jsonString = json.prettyPrint(dialogues)
        
        try {
            FileWriter(outputFile).use { writer ->
                writer.write(jsonString)
            }
            println("Extracted ${dialogues.size} dialogues to ${outputFile.absolutePath}")
        } catch (e: Exception) {
            println("Error writing dialogues to file: ${e.message}")
        }
    }
}
