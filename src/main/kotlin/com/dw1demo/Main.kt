package com.dw1demo

import com.dw1demo.modularizer.ScriptModularizer
import com.dw1demo.parser.ScriptParser
import com.dw1demo.visualizer.ScriptVisualizer
import java.io.File

fun main() {
    val scriptFile = File("raw_data/Script US.txt")
    val scriptsDir = File("scripts")
    val manifestFile = File("scripts/manifest.json")
    val dashboardFile = File("scripts_dashboard.html")

    if (!scriptFile.exists()) {
        println("Error: ${scriptFile.absolutePath} not found.")
        return
    }

    if (!manifestFile.exists()) {
        println("Parsing ${scriptFile.name}...")
        val parser = ScriptParser()
        val scriptData = parser.parse(scriptFile)
        println("Parsed ${scriptData.scripts.size} scripts.")

        println("Modularizing...")
        val modularizer = ScriptModularizer()
        val manifest = modularizer.modularize(scriptData, scriptsDir)
        println("Generated ${manifest.entries.size} script files.")

        println("Generating dashboard...")
        val visualizer = ScriptVisualizer()
        visualizer.generateDashboard(manifest, scriptsDir, dashboardFile)
        println("Dashboard saved to ${dashboardFile.absolutePath}")
    } else {
        println("Manifest already exists. Delete ${manifestFile.absolutePath} to regenerate.")
    }
}