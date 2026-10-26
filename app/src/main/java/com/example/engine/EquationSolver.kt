package com.example.engine

import kotlin.math.*

object EquationSolver {

    data class Linear2Result(val x: Double, val y: Double)
    data class Linear3Result(val x: Double, val y: Double, val z: Double)
    data class PolyRoot(val real: Double, val imag: Double = 0.0)

    fun solveLinear2(a1: Double, b1: Double, c1: Double, a2: Double, b2: Double, c2: Double): Linear2Result {
        val det = a1 * b2 - a2 * b1
        if (abs(det) < 1e-12) throw ArithmeticException("Infinite or no solutions (det = 0)")
        val x = (c1 * b2 - c2 * b1) / det
        val y = (a1 * c2 - a2 * c1) / det
        return Linear2Result(x, y)
    }

    fun solveLinear3(
        a1: Double, b1: Double, c1: Double, d1: Double,
        a2: Double, b2: Double, c2: Double, d2: Double,
        a3: Double, b3: Double, c3: Double, d3: Double
    ): Linear3Result {
        val det = a1 * (b2 * c3 - b3 * c2) - b1 * (a2 * c3 - a3 * c2) + c1 * (a2 * b3 - a3 * b2)
        if (abs(det) < 1e-12) throw ArithmeticException("Infinite or no solutions (det = 0)")

        val detX = d1 * (b2 * c3 - b3 * c2) - b1 * (d2 * c3 - d3 * c2) + c1 * (d2 * b3 - d3 * b2)
        val detY = a1 * (d2 * c3 - d3 * c2) - d1 * (a2 * c3 - a3 * c2) + c1 * (a2 * d3 - a3 * d2)
        val detZ = a1 * (b2 * d3 - b3 * d2) - b1 * (a2 * d3 - a3 * d2) + d1 * (a2 * b3 - a3 * b2)

        return Linear3Result(detX / det, detY / det, detZ / det)
    }

    fun solveQuadratic(a: Double, b: Double, c: Double): List<PolyRoot> {
        if (abs(a) < 1e-12) throw IllegalArgumentException("Not a quadratic equation (a = 0)")
        val disc = b * b - 4 * a * c
        return if (disc >= 0) {
            val sq = sqrt(disc)
            listOf(
                PolyRoot((-b + sq) / (2 * a)),
                PolyRoot((-b - sq) / (2 * a))
            )
        } else {
            val sq = sqrt(-disc)
            listOf(
                PolyRoot(-b / (2 * a), sq / (2 * a)),
                PolyRoot(-b / (2 * a), -sq / (2 * a))
            )
        }
    }

    fun solveCubic(a: Double, b: Double, c: Double, d: Double): List<Double> {
        if (abs(a) < 1e-12) return solveQuadratic(b, c, d).map { it.real }

        // Depressed cubic t^3 + pt + q = 0
        val p = (3 * a * c - b * b) / (3 * a * a)
        val q = (2 * b * b * b - 9 * a * b * c + 27 * a * a * d) / (27 * a * a * a)
        val delta = (q * q / 4.0) + (p * p * p / 27.0)
        val offset = -b / (3 * a)

        return if (delta > 0) {
            val u = cbrt(-q / 2.0 + sqrt(delta))
            val v = cbrt(-q / 2.0 - sqrt(delta))
            listOf(u + v + offset)
        } else if (abs(delta) < 1e-12) {
            val u = cbrt(-q / 2.0)
            listOf(2 * u + offset, -u + offset)
        } else {
            val r = sqrt(-p * p * p / 27.0)
            val phi = acos(-q / (2 * r))
            val m = 2 * cbrt(r)
            listOf(
                m * cos(phi / 3.0) + offset,
                m * cos((phi + 2 * Math.PI) / 3.0) + offset,
                m * cos((phi + 4 * Math.PI) / 3.0) + offset
            )
        }
    }
}
