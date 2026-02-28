package com.dw1demo.model

import kotlinx.serialization.Serializable

@Serializable
data class Instruction(
    val address: Int = -1,
    val opcode: String = "",
    val args: List<String> = emptyList(),
    val lineNumber: Int = -1
) {
    override fun toString(): String = "[$address] Line $lineNumber: $opcode $args"
}

@Serializable
data class SectionBlock(
    val id: Int = -1,
    val instructions: List<Instruction> = emptyList()
)

@Serializable
data class ScriptBlock(
    val id: Int = -1,
    val info: String = "",
    val sections: Map<String, SectionBlock> = emptyMap()
)

@Serializable
data class ScriptData(
    val scripts: Map<String, ScriptBlock> = emptyMap()
)