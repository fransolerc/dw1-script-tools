package com.dw1demo

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.io.File

class GameCore : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var gameEngine: GameEngine
    private lateinit var instructions: List<Instruction>

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont() // Use default font for now
        
        // Load script
        // Note: In a real LibGDX game, you'd use Gdx.files.internal("Script US.txt")
        // But for now, let's stick to java.io.File for simplicity if the file is in the project root
        // Or better, move the script to an 'assets' folder.
        // Let's try to load it from the project root for now.
        var scriptFile = File("Script US.txt")
        if (!scriptFile.exists()) {
            scriptFile = File("../Script US.txt")
        }

        if (scriptFile.exists()) {
            val parser = ScriptParser()
            instructions = parser.parse(scriptFile)
            gameEngine = GameEngine()
            
            // Execute initial instructions
            println("Executing initial script...")
            gameEngine.execute(instructions.take(20))
        } else {
            println("Error: Script US.txt not found!")
        }
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f) // Dark blue background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        font.draw(batch, "Digimon World Remake Engine", 20f, 460f)
        font.draw(batch, "Script loaded: ${if (::instructions.isInitialized) instructions.size else 0} instructions", 20f, 440f)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
