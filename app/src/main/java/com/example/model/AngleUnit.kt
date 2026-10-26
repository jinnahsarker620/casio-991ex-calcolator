package com.example.model

enum class AngleUnit(val symbol: String, val displayName: String) {
    DEG("D", "Degree"),
    RAD("R", "Radian"),
    GRAD("G", "Gradian");

    fun toRadians(value: Double): Double = when (this) {
        DEG -> Math.toRadians(value)
        RAD -> value
        GRAD -> value * Math.PI / 200.0
    }

    fun fromRadians(radians: Double): Double = when (this) {
        DEG -> Math.toDegrees(radians)
        RAD -> radians
        GRAD -> radians * 200.0 / Math.PI
    }
}
