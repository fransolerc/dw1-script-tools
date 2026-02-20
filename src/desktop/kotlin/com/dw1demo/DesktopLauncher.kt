package com.dw1demo

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("Digimon World 1 Remake Engine")
    config.setWindowedMode(800, 600)
    config.useVsync(true)
    Lwjgl3Application(GameCore(), config)
}
