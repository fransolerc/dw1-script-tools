package com.dw1demo

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.io.File

class GameCore : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var gameEngine: GameEngine

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        
        val scriptFile = File("raw_data/Script US.txt")
        val scriptsDir = File("scripts")
        val manifestFile = File("scripts/manifest.json")

        gameEngine = GameEngine()

        // 1. Check if we need to migrate/modularize
        if (!manifestFile.exists() && scriptFile.exists()) {
            println("Manifest not found. Starting migration and modularization...")
            val parser = ScriptParser()
            val scriptData = parser.parse(scriptFile)
            
            val modularizer = ScriptModularizer()
            val manifest = modularizer.modularize(scriptData, scriptsDir)
            
            val visualizer = ScriptVisualizer()
            visualizer.generateDashboard(manifest, scriptsDir, File("scripts_dashboard.html"))
            
            println("Modularization and Dashboard generation complete.")
        }

        // 2. Load manifest and start
        if (manifestFile.exists()) {
            gameEngine.loadManifest(manifestFile, scriptsDir)
            gameEngine.startScript(0, 1238) // Jump to Jijimon's questions
            println("Engine initialized with Modular JSON scripts.")
        } else {
            println("Error: No script manifest found!")
        }
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (::gameEngine.isInitialized && gameEngine.isRunning && (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.X))) {
            gameEngine.update()
        }

        batch.begin()
        font.draw(batch, "Digimon World Remake Engine - JSON Mode", 20f, 580f)
        font.draw(batch, "Press [SPACE] or [X] to execute next instruction", 20f, 560f)
        
        if (::gameEngine.isInitialized) {
            val scriptId = gameEngine.currentScript?.id ?: -1
            val sectionId = gameEngine.currentSection?.id ?: -1
            
            font.draw(batch, "Script: $scriptId | Section: $sectionId | PC: ${gameEngine.pc}", 20f, 520f)
            font.draw(batch, "Last Action: ${gameEngine.lastLog}", 20f, 480f)
            
            font.draw(batch, "--- Game State ---", 20f, 440f)
            font.draw(batch, "Loaded Digimons: ${gameEngine.loadedDigimons}", 20f, 420f)
            font.draw(batch, "PStats: ${gameEngine.pStats.size} entries", 20f, 400f)
        }

        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
