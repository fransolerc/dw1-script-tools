package com.dw1demo

data class Instruction(
    val address: Int = -1, // Original memory address (e.g., 1128)
    val opcode: String = "",
    val args: List<String> = emptyList(),
    val lineNumber: Int = -1
) {
    override fun toString(): String {
        return "[$address] Line $lineNumber: $opcode $args"
    }
}
