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
    private lateinit var instructions: List<Instruction>

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        
        var scriptFile = File("Script US.txt")
        if (!scriptFile.exists()) {
            scriptFile = File("../Script US.txt")
        }

        if (scriptFile.exists()) {
            val parser = ScriptParser()
            instructions = parser.parse(scriptFile)
            gameEngine = GameEngine()
            gameEngine.loadScript(instructions)
            println("Script loaded successfully.")
            
            // Extract dialogues for analysis
            val extractor = DialogueExtractor()
            val outputFile = File("dialogues.json")
            extractor.extractDialogues(scriptFile, outputFile)
            
        } else {
            println("Error: Script US.txt not found!")
        }
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // INPUT HANDLING
        // Solo avanzamos si se pulsa ESPACIO o X
        if (::gameEngine.isInitialized && gameEngine.isRunning) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                gameEngine.update()
            }
        }

        batch.begin()
        font.draw(batch, "Digimon World Remake Engine - Debug Mode", 20f, 580f)
        font.draw(batch, "Press [SPACE] or [X] to execute next instruction", 20f, 560f)
        
        if (::instructions.isInitialized) {
            if (::gameEngine.isInitialized) {
                font.draw(batch, "PC: ${gameEngine.pc} / ${instructions.size}", 20f, 520f)
                font.draw(batch, "Instruction: ${gameEngine.getCurrentInstruction()}", 20f, 500f)
                font.draw(batch, "Last Action: ${gameEngine.lastLog}", 20f, 480f)
                
                font.draw(batch, "--- Game State ---", 20f, 440f)
                font.draw(batch, "Loaded Digimons: ${gameEngine.loadedDigimons}", 20f, 420f)
                font.draw(batch, "Active Script ID: ${gameEngine.currentScriptId}", 20f, 400f)
            }
        } else {
            font.draw(batch, "Script NOT loaded!", 20f, 560f)
        }

        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
