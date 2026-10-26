package com.example.model

import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Rational representation for exact fraction display and S<=>D toggling.
 */
data class Fraction(val num: Long, val den: Long) {
    init {
        require(den != 0L) { "Denominator cannot be zero" }
    }

    val isInteger: Boolean get() = den == 1L || num % den == 0L

    fun toDouble(): Double = num.toDouble() / den.toDouble()

    fun simplify(): Fraction {
        if (num == 0L) return Fraction(0L, 1L)
        val gcdVal = gcd(abs(num), abs(den))
        val sign = if (den < 0) -1 else 1
        return Fraction((num / gcdVal) * sign, abs(den) / gcdVal)
    }

    /**
     * Formats as improper fraction "a/b"
     */
    fun toStandardString(): String {
        val s = simplify()
        return if (s.den == 1L) s.num.toString() else "${s.num}/${s.den}"
    }

    /**
     * Formats as mixed fraction "w a/b" e.g. "2 1/3"
     */
    fun toMixedString(): String {
        val s = simplify()
        if (s.den == 1L) return s.num.toString()
        val whole = s.num / s.den
        val remainder = abs(s.num % s.den)
        return if (whole != 0L && remainder != 0L) {
            "$whole $remainder/${s.den}"
        } else {
            "${s.num}/${s.den}"
        }
    }

    companion object {
        fun gcd(a: Long, b: Long): Long {
            var x = a
            var y = b
            while (y != 0L) {
                val t = y
                y = x % y
                x = t
            }
            return if (x < 0) -x else x
        }

        /**
         * Convert a decimal number to its closest reasonable fraction using continued fractions.
         */
        fun fromDouble(value: Double, maxDenominator: Long = 100000L): Fraction? {
            if (value.isNaN() || value.isInfinite()) return null
            if (abs(value) > 1e12) return null

            val sign = if (value < 0) -1L else 1L
            val positiveVal = abs(value)

            var h1 = 1L
            var h0 = 0L
            var k1 = 0L
            var k0 = 1L

            var x = positiveVal
            val maxIterations = 30
            var iter = 0

            while (iter < maxIterations) {
                val a = x.toLong()
                val h2 = a * h1 + h0
                val k2 = a * k1 + k0

                if (k2 > maxDenominator || k2 < 0L || h2 < 0L) break

                h0 = h1
                h1 = h2
                k0 = k1
                k1 = k2

                val diff = x - a.toDouble()
                if (abs(diff) < 1e-9) break

                x = 1.0 / diff
                iter++
            }

            if (k1 == 0L) return null
            val approx = h1.toDouble() / k1.toDouble()
            if (abs(approx - positiveVal) > 1e-6) return null

            return Fraction(sign * h1, k1).simplify()
        }
    }
}
