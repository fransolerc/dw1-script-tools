package com.dw1demo.modularizer

import com.dw1demo.model.ManifestEntry
import com.dw1demo.model.ScriptData
import com.dw1demo.model.ScriptManifest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.collections.iterator

class ScriptModularizer {

    private val json = Json { prettyPrint = true }

    fun modularize(scriptData: ScriptData, outputDir: File): ScriptManifest {
        if (!outputDir.exists()) outputDir.mkdirs()

        val entries = mutableMapOf<String, ManifestEntry>()

        for ((id, block) in scriptData.scripts) {
            val fileName = "script_$id.json"
            val file = File(outputDir, fileName)
            file.writeText(json.encodeToString(block))

            val previews = block.sections.values
                .flatMap { it.instructions }
                .filter { it.opcode == "showTextbox" && it.args.isNotEmpty() }
                .take(3)
                .map { it.args[0].take(50) }

            entries[id] = ManifestEntry(
                id = block.id,
                info = block.info,
                dialoguePreviews = previews,
                sectionCount = block.sections.size,
                fileName = fileName
            )
        }

        val manifest = ScriptManifest(entries)
        val manifestFile = File(outputDir, "manifest.json")
        manifestFile.writeText(json.encodeToString(manifest))

        return manifest
    }
}