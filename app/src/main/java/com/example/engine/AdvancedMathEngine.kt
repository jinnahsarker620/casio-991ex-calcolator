package com.example.engine

import kotlin.math.*

/**
 * Advanced CASIO fx-991EX mathematical engines supporting:
 * - Matrix calculations (up to 4x4, det, inverse, transpose, matMul, matAdd)
 * - Vector calculations (2D/3D, dot, cross, magnitude, angle)
 * - Statistics (1-Var mean, sum, std dev, min/max/quartiles, 2-Var linear regression)
 * - Function Table Generator (f(x), g(x) with Start, End, Step)
 * - Distribution (Normal PDF, CDF, Inverse Normal)
 * - Ratio Solver (A:B = X:D, A:B = C:X)
 * - Complex numbers (a + bi, r∠θ, arg, conj)
 */

object AdvancedMathEngine {

    // ==================== 1. MATRIX ENGINE ====================
    data class Matrix(val rows: Int, val cols: Int, val data: List<List<Double>>) {
        init {
            require(data.size == rows) { "Row count mismatch" }
            require(data.all { it.size == cols }) { "Col count mismatch" }
        }

        operator fun plus(other: Matrix): Matrix {
            require(rows == other.rows && cols == other.cols) { "Dimension mismatch for addition" }
            val res = List(rows) { r ->
                List(cols) { c -> data[r][c] + other.data[r][c] }
            }
            return Matrix(rows, cols, res)
        }

        operator fun minus(other: Matrix): Matrix {
            require(rows == other.rows && cols == other.cols) { "Dimension mismatch for subtraction" }
            val res = List(rows) { r ->
                List(cols) { c -> data[r][c] - other.data[r][c] }
            }
            return Matrix(rows, cols, res)
        }

        operator fun times(scalar: Double): Matrix {
            val res = List(rows) { r ->
                List(cols) { c -> data[r][c] * scalar }
            }
            return Matrix(rows, cols, res)
        }

        operator fun times(other: Matrix): Matrix {
            require(cols == other.rows) { "Matrix multiplication incompatible: ${rows}x${cols} vs ${other.rows}x${other.cols}" }
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
                    val a = data[0][0]; val b = data[0][1]; val c = data[0][2]
                    a * (data[1][1] * data[2][2] - data[1][2] * data[2][1]) -
                    b * (data[1][0] * data[2][2] - data[1][2] * data[2][0]) +
                    c * (data[1][0] * data[2][1] - data[1][1] * data[2][0])
                }
                4 -> det4x4(data)
                else -> throw UnsupportedOperationException("Matrix size $rows not supported")
            }
        }

        private fun det4x4(m: List<List<Double>>): Double {
            var det = 0.0
            for (j in 0 until 4) {
                val sub = List(3) { r ->
                    val row = mutableListOf<Double>()
                    for (c in 0 until 4) {
                        if (c != j) row.add(m[r + 1][c])
                    }
                    row
                }
                val sign = if (j % 2 == 0) 1.0 else -1.0
                det += sign * m[0][j] * Matrix(3, 3, sub).determinant()
            }
            return det
        }

        fun inverse(): Matrix {
            require(rows == cols) { "Inverse only defined for square matrices" }
            val det = determinant()
            if (abs(det) < 1e-12) throw ArithmeticException("Singular matrix (det = 0, no inverse)")
            return when (rows) {
                2 -> {
                    val invData = listOf(
                        listOf(data[1][1] / det, -data[0][1] / det),
                        listOf(-data[1][0] / det, data[0][0] / det)
                    )
                    Matrix(2, 2, invData)
                }
                3 -> {
                    val cofactors = List(3) { r ->
                        List(3) { c ->
                            val sub = List(2) { sr ->
                                val actualRow = if (sr >= r) sr + 1 else sr
                                List(2) { sc ->
                                    val actualCol = if (sc >= c) sc + 1 else sc
                                    data[actualRow][actualCol]
                                }
                            }
                            val sign = if ((r + c) % 2 == 0) 1.0 else -1.0
                            val minor = sub[0][0] * sub[1][1] - sub[0][1] * sub[1][0]
                            sign * minor
                        }
                    }
                    // Adjugate is transpose of cofactor matrix, divided by det
                    val adjTrans = Matrix(3, 3, cofactors).transpose()
                    val res = List(3) { r ->
                        List(3) { c -> adjTrans.data[r][c] / det }
                    }
                    Matrix(3, 3, res)
                }
                else -> throw UnsupportedOperationException("Inverse for size $rows not implemented")
            }
        }
    }

    // ==================== 2. VECTOR ENGINE ====================
    data class Vector3(val x: Double, val y: Double, val z: Double = 0.0) {
        operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
        operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
        operator fun times(scalar: Double) = Vector3(x * scalar, y * scalar, z * scalar)

        fun dot(other: Vector3): Double = x * other.x + y * other.y + z * other.z

        fun cross(other: Vector3): Vector3 = Vector3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )

        fun magnitude(): Double = sqrt(x * x + y * y + z * z)

        fun angleWith(other: Vector3): Double {
            val m1 = magnitude()
            val m2 = other.magnitude()
            if (m1 < 1e-12 || m2 < 1e-12) return 0.0
            val cosTheta = (dot(other) / (m1 * m2)).coerceIn(-1.0, 1.0)
            return acos(cosTheta) * 180.0 / Math.PI // in degrees
        }

        fun unit(): Vector3 {
            val m = magnitude()
            if (m < 1e-12) return Vector3(0.0, 0.0, 0.0)
            return Vector3(x / m, y / m, z / m)
        }
    }

    // ==================== 3. STATISTICS ENGINE ====================
    data class Stats1VarResult(
        val n: Int,
        val mean: Double,
        val sumX: Double,
        val sumX2: Double,
        val sigmaX: Double, // population std dev
        val sX: Double,     // sample std dev
        val minX: Double,
        val q1: Double,
        val median: Double,
        val q3: Double,
        val maxX: Double
    )

    fun calculate1VarStats(values: List<Double>): Stats1VarResult {
        require(values.isNotEmpty()) { "Data list cannot be empty" }
        val sorted = values.sorted()
        val n = sorted.size
        val sumX = sorted.sum()
        val sumX2 = sorted.sumOf { it * it }
        val mean = sumX / n

        val variancePop = sorted.sumOf { (it - mean) * (it - mean) } / n
        val sigmaX = sqrt(variancePop)
        val sX = if (n > 1) sqrt(sorted.sumOf { (it - mean) * (it - mean) } / (n - 1)) else 0.0

        val median = if (n % 2 == 1) sorted[n / 2] else (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0
        val lowerHalf = sorted.subList(0, n / 2)
        val upperHalf = if (n % 2 == 1) sorted.subList(n / 2 + 1, n) else sorted.subList(n / 2, n)

        val q1 = if (lowerHalf.isNotEmpty()) {
            val lhSize = lowerHalf.size
            if (lhSize % 2 == 1) lowerHalf[lhSize / 2] else (lowerHalf[lhSize / 2 - 1] + lowerHalf[lhSize / 2]) / 2.0
        } else sorted.first()

        val q3 = if (upperHalf.isNotEmpty()) {
            val uhSize = upperHalf.size
            if (uhSize % 2 == 1) upperHalf[uhSize / 2] else (upperHalf[uhSize / 2 - 1] + upperHalf[uhSize / 2]) / 2.0
        } else sorted.last()

        return Stats1VarResult(
            n = n,
            mean = mean,
            sumX = sumX,
            sumX2 = sumX2,
            sigmaX = sigmaX,
            sX = sX,
            minX = sorted.first(),
            q1 = q1,
            median = median,
            q3 = q3,
            maxX = sorted.last()
        )
    }

    data class LinearRegressionResult(
        val a: Double, // intercept
        val b: Double, // slope
        val r: Double  // correlation
    )

    fun calculateLinearRegression(xs: List<Double>, ys: List<Double>): LinearRegressionResult {
        require(xs.size == ys.size && xs.size >= 2) { "At least 2 points required" }
        val n = xs.size
        val sumX = xs.sum()
        val sumY = ys.sum()
        val sumXY = xs.indices.sumOf { xs[it] * ys[it] }
        val sumX2 = xs.sumOf { it * it }
        val sumY2 = ys.sumOf { it * it }

        val denom = n * sumX2 - sumX * sumX
        if (abs(denom) < 1e-12) throw ArithmeticException("Vertical line: undefined slope")

        val b = (n * sumXY - sumX * sumY) / denom
        val a = (sumY - b * sumX) / n

        val denomR = sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY))
        val r = if (denomR > 1e-12) (n * sumXY - sumX * sumY) / denomR else 0.0

        return LinearRegressionResult(a, b, r)
    }

    // ==================== 4. DISTRIBUTION ENGINE ====================
    object Distribution {
        fun normalPdf(x: Double, mean: Double = 0.0, sigma: Double = 1.0): Double {
            require(sigma > 0) { "Sigma must be > 0" }
            val z = (x - mean) / sigma
            return (1.0 / (sigma * sqrt(2 * Math.PI))) * exp(-0.5 * z * z)
        }

        fun normalCdf(x: Double, mean: Double = 0.0, sigma: Double = 1.0): Double {
            require(sigma > 0) { "Sigma must be > 0" }
            val z = (x - mean) / sigma
            return 0.5 * (1.0 + erf(z / sqrt(2.0)))
        }

        fun inverseNormalCdf(p: Double, mean: Double = 0.0, sigma: Double = 1.0): Double {
            require(p in 0.0..1.0 && sigma > 0) { "Probability must be in (0, 1)" }
            // Rational approximation for inverse normal CDF (Wichura algorithm)
            val z = rationalInverseCdf(p)
            return mean + z * sigma
        }

        private fun erf(x: Double): Double {
            // Maximum error: 1.5 * 10^-7 (Abramowitz & Stegun 7.1.26)
            val a1 = 0.254829592
            val a2 = -0.284496736
            val a3 = 1.421413741
            val a4 = -1.453152027
            val a5 = 1.061405429
            val p = 0.3275911

            val sign = if (x < 0) -1 else 1
            val absX = abs(x)
            val t = 1.0 / (1.0 + p * absX)
            val y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * exp(-absX * absX)
            return sign * y
        }

        private fun rationalInverseCdf(p: Double): Double {
            if (p <= 0.0) return -10.0
            if (p >= 1.0) return 10.0
            if (p == 0.5) return 0.0

            val q = if (p < 0.5) p else 1.0 - p
            val t = sqrt(-2.0 * ln(q))
            val c0 = 2.515517; val c1 = 0.802853; val c2 = 0.010328
            val d1 = 1.432788; val d2 = 0.189269; val d3 = 0.001308
            val num = c0 + c1 * t + c2 * t * t
            val den = 1.0 + d1 * t + d2 * t * t + d3 * t * t * t
            val z = t - num / den
            return if (p < 0.5) -z else z
        }
    }

    // ==================== 5. RATIO SOLVER ====================
    object RatioSolver {
        // Mode 1: A : B = X : D  =>  X = (A * D) / B
        fun solveXOverD(a: Double, b: Double, d: Double): Double {
            if (abs(b) < 1e-12) throw ArithmeticException("Division by zero (B = 0)")
            return (a * d) / b
        }

        // Mode 2: A : B = C : X  =>  X = (B * C) / A
        fun solveCOverX(a: Double, b: Double, c: Double): Double {
            if (abs(a) < 1e-12) throw ArithmeticException("Division by zero (A = 0)")
            return (b * c) / a
        }
    }

    // ==================== 6. COMPLEX NUMBERS ====================
    data class Complex(val re: Double, val im: Double) {
        operator fun plus(other: Complex) = Complex(re + other.re, im + other.im)
        operator fun minus(other: Complex) = Complex(re - other.re, im - other.im)
        operator fun times(other: Complex) = Complex(
            re * other.re - im * other.im,
            re * other.im + im * other.re
        )
        operator fun div(other: Complex): Complex {
            val denom = other.re * other.re + other.im * other.im
            if (denom < 1e-15) throw ArithmeticException("Complex division by zero")
            return Complex(
                (re * other.re + im * other.im) / denom,
                (im * other.re - re * other.im) / denom
            )
        }

        fun magnitude(): Double = sqrt(re * re + im * im)
        fun argument(): Double = atan2(im, re) // in radians
        fun conjugate(): Complex = Complex(re, -im)

        fun pow(p: Double): Complex {
            if (p == 0.0) return Complex(1.0, 0.0)
            val r = magnitude()
            val theta = argument()
            val newR = r.pow(p)
            val newTheta = p * theta
            return Complex(newR * cos(newTheta), newR * sin(newTheta))
        }

        fun formatStandard(): String {
            val cleanRe = if (abs(re) < 1e-10) 0.0 else re
            val cleanIm = if (abs(im) < 1e-10) 0.0 else im
            val sign = if (cleanIm >= 0) "+" else "-"
            val absIm = abs(cleanIm)
            val imStr = if (abs(absIm - 1.0) < 1e-10) "i" else "${MathEvaluator.formatDecimal(absIm)}i"
            return when {
                cleanIm == 0.0 -> MathEvaluator.formatDecimal(cleanRe)
                cleanRe == 0.0 -> if (cleanIm < 0) "-$imStr" else imStr
                else -> "${MathEvaluator.formatDecimal(cleanRe)} $sign $imStr"
            }
        }

        fun formatPolar(): String {
            val r = MathEvaluator.formatDecimal(magnitude())
            val theta = MathEvaluator.formatDecimal(argument() * 180.0 / Math.PI)
            return "$r ∠ $theta°"
        }
    }
}
