package com.dw1demo

data class Instruction(
    val address: Int, // Original memory address (e.g., 1128)
    val opcode: String,
    val args: List<String>,
    val lineNumber: Int
) {
    override fun toString(): String {
        return "[$address] Line $lineNumber: $opcode $args"
    }
}
