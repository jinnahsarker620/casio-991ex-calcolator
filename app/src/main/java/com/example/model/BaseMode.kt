package com.example.model

enum class BaseMode(val symbol: String, val radix: Int, val prefix: String) {
    COMP("COMP", 10, ""),     // Normal calculation mode
    DEC("DEC", 10, "d"),
    HEX("HEX", 16, "h"),
    BIN("BIN", 2, "b"),
    OCT("OCT", 8, "o");

    fun format(value: Long): String = when (this) {
        COMP, DEC -> value.toString(10)
        HEX -> value.toString(16).uppercase()
        BIN -> value.toString(2)
        OCT -> value.toString(8)
    }

    fun parse(text: String): Long {
        val clean = text.trim().removePrefix("0x").removePrefix("0b").removePrefix("0o")
        return clean.toLong(radix)
    }
}
