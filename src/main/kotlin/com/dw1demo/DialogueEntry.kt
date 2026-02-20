package com.dw1demo

data class DialogueEntry(
    val address: Int,
    val speakerId: Int, // El ID numérico del hablante (si se conoce)
    val text: String
)
