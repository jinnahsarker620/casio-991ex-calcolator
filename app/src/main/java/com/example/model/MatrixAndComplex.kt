package com.example.model

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Complex number support (a + bi) for CASIO ClassWiz Mode 2.
 */
data class Complex(val re: Double, val im: Double = 0.0) {
    operator fun plus(other: Complex) = Complex(re + other.re, im + other.im)
    operator fun minus(other: Complex) = Complex(re - other.re, im - other.im)
    operator fun times(other: Complex) = Complex(
        re * other.re - im * other.im,
        re * other.im + im * other.re
    )
    operator fun div(other: Complex): Complex {
        val den = other.re * other.re + other.im * other.im
        if (den == 0.0) throw ArithmeticException("Division by zero")
        return Complex(
            (re * other.re + im * other.im) / den,
            (im * other.re - re * other.im) / den
        )
    }

    fun abs() = sqrt(re * re + im * im)
    fun arg() = atan2(im, re)
    fun conj() = Complex(re, -im)

    override fun toString(): String {
        val rStr = if (abs(re) < 1e-11) "0" else formatNum(re)
        val iStr = if (abs(im) < 1e-11) "0" else formatNum(im)

        return when {
            abs(im) < 1e-11 -> rStr
            abs(re) < 1e-11 -> if (im == 1.0) "i" else if (im == -1.0) "-i" else "${iStr}i"
            im > 0 -> "$rStr+${if (im == 1.0) "" else iStr}i"
            else -> "$rStr-${if (im == -1.0) "" else formatNum(-im)}i"
        }
    }

    private fun formatNum(v: Double): String {
        val longVal = v.toLong()
        return if (v == longVal.toDouble()) longVal.toString() else String.format(java.util.Locale.US, "%.6g", v)
    }

    companion object {
        val ZERO = Complex(0.0, 0.0)
        val ONE = Complex(1.0, 0.0)
        val I = Complex(0.0, 1.0)
    }
}

/**
 * Matrix class supporting up to 4x4 operations (Det, Inv, Trn, +, ×).
 */
data class Matrix(val rows: Int, val cols: Int, val data: List<List<Double>>) {
    init {
        require(rows > 0 && cols > 0)
        require(data.size == rows && data.all { it.size == cols })
    }

    operator fun plus(other: Matrix): Matrix {
        require(rows == other.rows && cols == other.cols) { "Matrix dimensions must match for addition" }
        val res = List(rows) { r ->
            List(cols) { c -> data[r][c] + other.data[r][c] }
        }
        return Matrix(rows, cols, res)
    }

    operator fun minus(other: Matrix): Matrix {
        require(rows == other.rows && cols == other.cols) { "Matrix dimensions must match for subtraction" }
        val res = List(rows) { r ->
            List(cols) { c -> data[r][c] - other.data[r][c] }
        }
        return Matrix(rows, cols, res)
    }

    operator fun times(other: Matrix): Matrix {
        require(cols == other.rows) { "Matrix multiplication dimensions mismatch: ${cols} != ${other.rows}" }
        val res = List(rows) { r ->
            List(other.cols) { c ->
                var sum = 0.0
                for (k in 0 until cols) {
                    sum += data[r][k] * other.data[k][c]
                }
                sum
            }
        }
        return Matrix(rows, other.cols, res)
    }

    fun transpose(): Matrix {
        val res = List(cols) { c ->
            List(rows) { r -> data[r][c] }
        }
        return Matrix(cols, rows, res)
    }

    fun determinant(): Double {
        require(rows == cols) { "Determinant only defined for square matrices" }
        return when (rows) {
            1 -> data[0][0]
            2 -> data[0][0] * data[1][1] - data[0][1] * data[1][0]
            3 -> {
                data[0][0] * (data[1][1] * data[2][2] - data[1][2] * data[2][1]) -
                data[0][1] * (data[1][0] * data[2][2] - data[1][2] * data[2][0]) +
                data[0][2] * (data[1][0] * data[2][1] - data[1][1] * data[2][0])
            }
            4 -> {
                var det = 0.0
                for (c in 0 until 4) {
                    val sub = subMatrix(0, c)
                    val sign = if (c % 2 == 0) 1.0 else -1.0
                    det += sign * data[0][c] * sub.determinant()
                }
                det
            }
            else -> throw IllegalArgumentException("Matrix size too large")
        }
    }

    fun inverse(): Matrix {
        require(rows == cols) { "Inverse only defined for square matrices" }
        val det = determinant()
        if (abs(det) < 1e-12) throw ArithmeticException("Matrix is singular (det = 0)")

        if (rows == 1) return Matrix(1, 1, listOf(listOf(1.0 / det)))
        if (rows == 2) {
            val inv = listOf(
                listOf(data[1][1] / det, -data[0][1] / det),
                listOf(-data[1][0] / det, data[0][0] / det)
            )
            return Matrix(2, 2, inv)
        }

        // Cofactor matrix transpose
        val adj = List(rows) { r ->
            List(cols) { c ->
                val sub = subMatrix(c, r) // transposed row and col
                val sign = if ((r + c) % 2 == 0) 1.0 else -1.0
                (sign * sub.determinant()) / det
            }
        }
        return Matrix(rows, cols, adj)
    }

    private fun subMatrix(excludingRow: Int, excludingCol: Int): Matrix {
        val res = data.filterIndexed { r, _ -> r != excludingRow }.map { row ->
            row.filterIndexed { c, _ -> c != excludingCol }
        }
        return Matrix(rows - 1, cols - 1, res)
    }

    override fun toString(): String {
        return data.joinToString("\n") { row ->
            row.joinToString("  ") { String.format(java.util.Locale.US, "%.4g", it) }
        }
    }

    companion object {
        fun identity(n: Int) = Matrix(n, n, List(n) { r -> List(n) { c -> if (r == c) 1.0 else 0.0 } })
        fun zero(r: Int, c: Int) = Matrix(r, c, List(r) { List(c) { 0.0 } })
    }
}
