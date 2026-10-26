package com.example.engine

import com.example.model.AngleUnit
import com.example.model.BaseMode
import com.example.model.Fraction
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

class MathEvaluator {

    sealed class EvalResult {
        data class Success(
            val value: Double,
            val standardFormatted: String,
            val sdOptions: List<String>
        ) : EvalResult()

        data class Error(val message: String) : EvalResult()
    }

    fun evaluate(
        expression: String,
        angleUnit: AngleUnit = AngleUnit.DEG,
        baseMode: BaseMode = BaseMode.COMP,
        variables: Map<String, Double> = emptyMap(),
        ans: Double = 0.0
    ): EvalResult {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            return EvalResult.Success(0.0, "0", listOf("0"))
        }

        // Base-N evaluation mode
        if (baseMode != BaseMode.COMP) {
            return evaluateBaseN(trimmed, baseMode)
        }

        // Polar coordinate conversion: pol(x, y) -> r, θ
        if (trimmed.startsWith("pol(", ignoreCase = true) && trimmed.endsWith(")")) {
            return evalPol(trimmed, angleUnit, ans, variables)
        }
        // Rectangular coordinate conversion: rec(r, θ) -> x, y
        if (trimmed.startsWith("rec(", ignoreCase = true) && trimmed.endsWith(")")) {
            return evalRec(trimmed, angleUnit, ans, variables)
        }

        // Complex evaluation mode (Mode 2 CMPLX or expressions containing unit 'i' / complex functions / polar angle)
        if (isComplexExpression(trimmed)) {
            return evaluateComplex(trimmed, angleUnit, ans, variables)
        }

        return try {
            val normalized = preprocessExpression(trimmed, ans, variables)
            val value = parseAndEval(normalized, angleUnit, variables)
            if (value.isNaN() || value.isInfinite()) {
                EvalResult.Error("Math ERROR")
            } else {
                val cleanedValue = cleanFloatArtifacts(value)
                val sdOptions = generateSdOptions(cleanedValue, angleUnit)
                EvalResult.Success(
                    value = cleanedValue,
                    standardFormatted = sdOptions.firstOrNull() ?: formatDecimal(cleanedValue),
                    sdOptions = sdOptions
                )
            }
        } catch (e: ArithmeticException) {
            EvalResult.Error("Math ERROR")
        } catch (e: IllegalArgumentException) {
            EvalResult.Error(e.message ?: "Syntax ERROR")
        } catch (e: Exception) {
            EvalResult.Error("Syntax ERROR")
        }
    }

    private fun isComplexExpression(expr: String): Boolean {
        if (expr.contains("arg(", ignoreCase = true) ||
            expr.contains("conj(", ignoreCase = true) ||
            expr.contains("conjg(", ignoreCase = true) ||
            expr.contains("ReP(", ignoreCase = true) ||
            expr.contains("ImP(", ignoreCase = true) ||
            expr.contains("∠")
        ) {
            return true
        }

        val stripped = expr
            .replace("asin", "", ignoreCase = true)
            .replace("asinh", "", ignoreCase = true)
            .replace("sinh", "", ignoreCase = true)
            .replace("sin", "", ignoreCase = true)
            .replace("integral", "", ignoreCase = true)
            .replace("diff", "", ignoreCase = true)
            .replace("dim", "", ignoreCase = true)
            .replace("ranInt", "", ignoreCase = true)
            .replace("min", "", ignoreCase = true)
            .replace("max", "", ignoreCase = true)
            .replace("bin", "", ignoreCase = true)
            .replace("π", "")

        return Regex("""(?:\b|(?<=\d|\))|[+\-*/^(]|^)[iI](?:\b|(?=[+\-*/^)]|$))""").containsMatchIn(stripped)
    }

    private fun evaluateComplex(
        expression: String,
        angleUnit: AngleUnit,
        ans: Double,
        variables: Map<String, Double>
    ): EvalResult {
        return try {
            var s = expression
                .replace("Ans", "($ans)")
                .replace("×10^", "*10^")
                .replace("x10^", "*10^")
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
                .replace("–", "-")
                .replace("π", "(${Math.PI})")
                .replace("²", "^2")
                .replace("³", "^3")
                .replace("⁻¹", "^(-1)")
                .replace("conjg(", "conj(", ignoreCase = true)
                .replace("ReP(", "rep(", ignoreCase = true)
                .replace("ImP(", "imp(", ignoreCase = true)

            val openCount = s.count { it == '(' }
            val closeCount = s.count { it == ')' }
            if (openCount > closeCount) {
                s += ")".repeat(openCount - closeCount)
            }

            val parser = ComplexParser(s, angleUnit, variables)
            val z = parser.parse()
            val cleanedRe = cleanFloatArtifacts(z.re)
            val cleanedIm = cleanFloatArtifacts(z.im)
            val cleanZ = AdvancedMathEngine.Complex(cleanedRe, cleanedIm)

            val standard = cleanZ.formatStandard()
            val polar = cleanZ.formatPolar()
            val sdOptions = if (abs(cleanZ.im) < 1e-10) {
                val realOptions = generateSdOptions(cleanZ.re, angleUnit)
                if (realOptions.contains(standard)) realOptions else listOf(standard) + realOptions
            } else {
                listOf(standard, polar)
            }

            EvalResult.Success(
                value = cleanZ.magnitude(),
                standardFormatted = standard,
                sdOptions = sdOptions
            )
        } catch (e: ArithmeticException) {
            EvalResult.Error("Math ERROR")
        } catch (e: IllegalArgumentException) {
            EvalResult.Error(e.message ?: "Syntax ERROR")
        } catch (e: Exception) {
            EvalResult.Error("Syntax ERROR")
        }
    }

    private class ComplexParser(
        private val input: String,
        private val angleUnit: AngleUnit,
        private val variables: Map<String, Double>
    ) {
        private var pos = 0
        private val len = input.length

        fun parse(): AdvancedMathEngine.Complex {
            val res = parseAddSub()
            skipWhitespace()
            if (pos < len) throw IllegalArgumentException("Syntax ERROR")
            return res
        }

        private fun skipWhitespace() {
            while (pos < len && input[pos].isWhitespace()) pos++
        }

        private fun peek(): Char? {
            skipWhitespace()
            return if (pos < len) input[pos] else null
        }

        private fun parseAddSub(): AdvancedMathEngine.Complex {
            var left = parseMulDiv()
            while (true) {
                val ch = peek() ?: break
                if (ch == '+' || ch == '-') {
                    pos++
                    val right = parseMulDiv()
                    left = if (ch == '+') left + right else left - right
                } else {
                    break
                }
            }
            return left
        }

        private fun parseMulDiv(): AdvancedMathEngine.Complex {
            var left = parsePolar()
            while (true) {
                val ch = peek() ?: break
                if (ch == '*' || ch == '/') {
                    pos++
                    val right = parsePolar()
                    left = if (ch == '*') left * right else left / right
                } else if (ch == '(' || (ch.isLetter() && ch != 'E' && ch != 'e') || ch.isDigit() || ch == 'i' || ch == 'I') {
                    val right = parsePolar()
                    left = left * right
                } else {
                    break
                }
            }
            return left
        }

        private fun parsePolar(): AdvancedMathEngine.Complex {
            var left = parsePower()
            while (peek() == '∠') {
                pos++
                val angleExpr = parsePower()
                val angleVal = angleExpr.re
                val rad = if (angleUnit == AngleUnit.RAD) angleVal else Math.toRadians(angleVal)
                val r = left.magnitude()
                left = AdvancedMathEngine.Complex(r * cos(rad), r * sin(rad))
            }
            return left
        }

        private fun parsePower(): AdvancedMathEngine.Complex {
            var left = parseUnary()
            while (peek() == '^') {
                pos++
                val exp = parseUnary()
                left = left.pow(exp.re)
            }
            return left
        }

        private fun parseUnary(): AdvancedMathEngine.Complex {
            skipWhitespace()
            val ch = peek()
            if (ch == '+') {
                pos++
                return parseUnary()
            }
            if (ch == '-') {
                pos++
                val next = parseUnary()
                return AdvancedMathEngine.Complex(-next.re, -next.im)
            }
            return parsePrimary()
        }

        private fun parsePrimary(): AdvancedMathEngine.Complex {
            skipWhitespace()
            val ch = peek() ?: throw IllegalArgumentException("Syntax ERROR")

            if (ch == '(') {
                pos++
                val res = parseAddSub()
                skipWhitespace()
                if (peek() == ')') {
                    pos++
                }
                return res
            }

            if ((ch == 'i' || ch == 'I') && (pos + 1 >= len || !input[pos + 1].isLetter())) {
                pos++
                return AdvancedMathEngine.Complex(0.0, 1.0)
            }

            if (ch.isLetter() || ch == '_') {
                val start = pos
                while (pos < len && (input[pos].isLetter() || input[pos].isDigit() || input[pos] == '_')) {
                    pos++
                }
                val id = input.substring(start, pos)
                skipWhitespace()

                if (peek() == '(') {
                    pos++
                    val arg = parseAddSub()
                    skipWhitespace()
                    if (peek() == ')') pos++

                    return when (id.lowercase()) {
                        "arg" -> {
                            val a = arg.argument()
                            val degOrRad = if (angleUnit == AngleUnit.RAD) a else Math.toDegrees(a)
                            AdvancedMathEngine.Complex(degOrRad, 0.0)
                        }
                        "conj", "conjg" -> arg.conjugate()
                        "rep" -> AdvancedMathEngine.Complex(arg.re, 0.0)
                        "imp" -> AdvancedMathEngine.Complex(arg.im, 0.0)
                        "abs" -> AdvancedMathEngine.Complex(arg.magnitude(), 0.0)
                        "sin" -> {
                            val r = if (angleUnit == AngleUnit.RAD) arg.re else Math.toRadians(arg.re)
                            val y = arg.im
                            AdvancedMathEngine.Complex(sin(r) * cosh(y), cos(r) * sinh(y))
                        }
                        "cos" -> {
                            val r = if (angleUnit == AngleUnit.RAD) arg.re else Math.toRadians(arg.re)
                            val y = arg.im
                            AdvancedMathEngine.Complex(cos(r) * cosh(y), -sin(r) * sinh(y))
                        }
                        "tan" -> {
                            val s = ComplexParser("sin($arg)", angleUnit, variables).parse()
                            val c = ComplexParser("cos($arg)", angleUnit, variables).parse()
                            s / c
                        }
                        "sqrt" -> {
                            if (arg.im == 0.0 && arg.re < 0) {
                                AdvancedMathEngine.Complex(0.0, sqrt(-arg.re))
                            } else {
                                arg.pow(0.5)
                            }
                        }
                        "ln" -> {
                            AdvancedMathEngine.Complex(ln(arg.magnitude()), arg.argument())
                        }
                        "log" -> {
                            val lnZ = AdvancedMathEngine.Complex(ln(arg.magnitude()), arg.argument())
                            lnZ / AdvancedMathEngine.Complex(ln(10.0), 0.0)
                        }
                        else -> throw IllegalArgumentException("Unknown function: $id")
                    }
                }

                val varVal = variables[id] ?: variables[id.uppercase()]
                if (varVal != null) {
                    return AdvancedMathEngine.Complex(varVal, 0.0)
                }
                throw IllegalArgumentException("Unknown variable: $id")
            }

            if (ch.isDigit() || ch == '.') {
                val start = pos
                var hasDot = ch == '.'
                pos++
                while (pos < len) {
                    val c = input[pos]
                    if (c.isDigit()) {
                        pos++
                    } else if (c == '.' && !hasDot) {
                        hasDot = true
                        pos++
                    } else if ((c == 'e' || c == 'E') && pos + 1 < len && (input[pos + 1].isDigit() || input[pos + 1] == '+' || input[pos + 1] == '-')) {
                        pos++
                        if (input[pos] == '+' || input[pos] == '-') pos++
                        while (pos < len && input[pos].isDigit()) pos++
                    } else {
                        break
                    }
                }
                val numStr = input.substring(start, pos)
                val v = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")

                if (pos < len && (input[pos] == 'i' || input[pos] == 'I') && (pos + 1 >= len || !input[pos + 1].isLetter())) {
                    pos++
                    return AdvancedMathEngine.Complex(0.0, v)
                }

                return AdvancedMathEngine.Complex(v, 0.0)
            }

            throw IllegalArgumentException("Unexpected character: $ch")
        }
    }

    private fun evaluateBaseN(expression: String, baseMode: BaseMode): EvalResult {
        return try {
            val clean = expression.replace(" ", "")
            val value: Long = when {
                clean.contains("+") -> {
                    val parts = clean.split("+")
                    parts.sumOf { baseMode.parse(it) }
                }
                clean.contains("×") || clean.contains("*") -> {
                    val parts = clean.split(Regex("[×*]"))
                    parts.map { baseMode.parse(it) }.reduce { acc, v -> acc * v }
                }
                clean.contains("-") && !clean.startsWith("-") -> {
                    val parts = clean.split("-")
                    var res = baseMode.parse(parts[0])
                    for (i in 1 until parts.size) {
                        res -= baseMode.parse(parts[i])
                    }
                    res
                }
                clean.contains("÷") || clean.contains("/") -> {
                    val parts = clean.split(Regex("[÷/]"))
                    val a = baseMode.parse(parts[0])
                    val b = baseMode.parse(parts[1])
                    if (b == 0L) throw ArithmeticException("Division by zero")
                    a / b
                }
                clean.contains("AND", ignoreCase = true) -> {
                    val parts = clean.split(Regex("AND", RegexOption.IGNORE_CASE))
                    baseMode.parse(parts[0]) and baseMode.parse(parts[1])
                }
                clean.contains("OR", ignoreCase = true) -> {
                    val parts = clean.split(Regex("OR", RegexOption.IGNORE_CASE))
                    baseMode.parse(parts[0]) or baseMode.parse(parts[1])
                }
                clean.contains("XOR", ignoreCase = true) -> {
                    val parts = clean.split(Regex("XOR", RegexOption.IGNORE_CASE))
                    baseMode.parse(parts[0]) xor baseMode.parse(parts[1])
                }
                clean.startsWith("NOT", ignoreCase = true) -> {
                    val operand = baseMode.parse(clean.substring(3))
                    operand.inv()
                }
                else -> baseMode.parse(clean)
            }

            val decStr = value.toString(10)
            val hexStr = value.toString(16).uppercase()
            val binStr = value.toString(2)
            val octStr = value.toString(8)

            val currentStr = when (baseMode) {
                BaseMode.DEC -> decStr
                BaseMode.HEX -> hexStr
                BaseMode.BIN -> binStr
                BaseMode.OCT -> octStr
                else -> decStr
            }

            EvalResult.Success(
                value = value.toDouble(),
                standardFormatted = currentStr,
                sdOptions = listOf(currentStr, "Dec: $decStr", "Hex: $hexStr", "Bin: $binStr", "Oct: $octStr")
            )
        } catch (e: Exception) {
            EvalResult.Error("Syntax ERROR")
        }
    }

    private fun preprocessExpression(expr: String, ans: Double, variables: Map<String, Double>): String {
        var s = expr
            .replace("Ans", "($ans)")
            .replace("×10^", "*10^")
            .replace("x10^", "*10^")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("–", "-")
            .replace("sin⁻¹", "asin")
            .replace("cos⁻¹", "acos")
            .replace("tan⁻¹", "atan")
        // Bare roots with numbers e.g. √333 -> sqrt(333), ³√27 -> cbrt(27)
        s = s.replace(Regex("""(?:³√|∛)\s*(\d+(?:\.\d+)?)"""), "cbrt($1)")
        s = s.replace(Regex("""√\s*(\d+(?:\.\d+)?)"""), "sqrt($1)")

        s = s
            .replace("³√", "cbrt")
            .replace("∛", "cbrt")
            .replace("√", "sqrt")
            .replace("⁻¹", "^(-1)")
            .replace("⁰", "^0")
            .replace("¹", "^1")
            .replace("²", "^2")
            .replace("³", "^3")
            .replace("⁴", "^4")
            .replace("⁵", "^5")
            .replace("⁶", "^6")
            .replace("⁷", "^7")
            .replace("⁸", "^8")
            .replace("⁹", "^9")
            .replace("π", "(${Math.PI})")

        // Check for incomplete log templates like log(,) or log(5,) or log(,25)
        if (Regex("""log\(\s*,""").containsMatchIn(s) || Regex("""log\([^,]+,\s*\)""").containsMatchIn(s)) {
            throw IllegalArgumentException("Syntax ERROR")
        }

        // Handle edge cases for fractions like /3 or 5/
        if (s.startsWith("/")) {
            s = if (ans != 0.0) "($ans)$s" else "0$s"
        }
        if (s.endsWith("/")) {
            s = "${s}1"
        }

        // Handle DMS degrees-minutes-seconds: 35°15'30"
        s = handleDmsNotation(s)

        // Insert implicit multiplication: e.g. 2sin -> 2*sin, 3(4) -> 3*(4), ) ( -> )*(
        s = insertImplicitMultiplication(s)

        // Automatically close unclosed parentheses at the end of the expression
        val openCount = s.count { it == '(' }
        val closeCount = s.count { it == ')' }
        if (openCount > closeCount) {
            s += ")".repeat(openCount - closeCount)
        }

        return s
    }

    private fun handleDmsNotation(expr: String): String {
        val dmsPattern = Regex("""(\d+(?:\.\d+)?)[°](\s*(\d+(?:\.\d+)?)['′])?(\s*(\d+(?:\.\d+)?)(?:["″]|''))?""")
        return dmsPattern.replace(expr) { match ->
            val deg = match.groupValues[1].toDoubleOrNull() ?: 0.0
            val min = match.groupValues[3].takeIf { it.isNotEmpty() }?.toDoubleOrNull() ?: 0.0
            val sec = match.groupValues[5].takeIf { it.isNotEmpty() }?.toDoubleOrNull() ?: 0.0
            val total = deg + (min / 60.0) + (sec / 3600.0)
            "($total)"
        }
    }

    private fun insertImplicitMultiplication(expr: String): String {
        val sb = StringBuilder()
        val len = expr.length
        for (i in 0 until len) {
            val c = expr[i]
            sb.append(c)
            if (i < len - 1) {
                val next = expr[i + 1]
                val isCurrentDigitOrParen = c.isDigit() || c == ')' || c == 'e' || c == 'x' || c == 'X'
                val isNextLetterOrParen = next == '(' || (next.isLetter() && next != 'e')
                if (isCurrentDigitOrParen && isNextLetterOrParen) {
                    val isDdx = (c == 'x' || c == 'X') && i >= 3 && expr.substring(i - 3, i + 1).equals("d/dx", ignoreCase = true)
                    if (!isDdx) {
                        sb.append('*')
                    }
                } else if (c == ')' && (next.isDigit() || next.isLetter() || next == '(')) {
                    sb.append('*')
                } else if ((c == 'x' || c == 'X') && (next.isDigit() || next == '(')) {
                    val isDdx = i >= 3 && expr.substring(i - 3, i + 1).equals("d/dx", ignoreCase = true)
                    if (!isDdx) {
                        sb.append('*')
                    }
                } else if (c.isDigit() && next == 'e' && (i + 2 < len && !expr[i + 2].isLetter())) {
                    sb.append('*')
                }
            }
        }
        return sb.toString()
    }

    // Mathematical parser & evaluator
    private fun parseAndEval(expr: String, angleUnit: AngleUnit, variables: Map<String, Double>): Double {
        val trimmed = expr.trim()

        // Check for integration syntax: ∫(expr, a, b) or integral(expr, a, b)
        if (trimmed.startsWith("∫(") || trimmed.startsWith("integral(")) {
            return evalIntegral(trimmed, angleUnit, variables)
        }

        // Check for numerical differentiation: d/dx(expr, point) or diff(expr, point)
        if (trimmed.startsWith("d/dx(") || trimmed.startsWith("diff(")) {
            return evalDerivative(trimmed, angleUnit, variables)
        }

        // Check for summation: Σ(expr, a, b) or sum(expr, a, b)
        if (trimmed.startsWith("Σ(") || trimmed.startsWith("sum(")) {
            return evalSummation(trimmed, angleUnit, variables)
        }

        val tokens = tokenize(trimmed)
        val parser = Parser(tokens, angleUnit, variables)
        return parser.parseExpression()
    }

    private fun evalIntegral(expr: String, angleUnit: AngleUnit, variables: Map<String, Double>): Double {
        val inner = expr.substring(expr.indexOf('(') + 1, expr.lastIndexOf(')'))
        val parts = splitTopLevel(inner, ',')
        if (parts.size < 3) throw IllegalArgumentException("Syntax ERROR: expected ∫(f(x), a, b)")

        val fnExpr = parts[0].trim()
        val aStr = parts[1].trim()
        val bStr = parts[2].trim()
        if (fnExpr.isEmpty() || aStr.isEmpty() || bStr.isEmpty()) {
            throw IllegalArgumentException("Syntax ERROR: incomplete integral")
        }

        val a = parseAndEval(aStr, angleUnit, variables)
        val b = parseAndEval(bStr, angleUnit, variables)

        if (a == b) return 0.0

        // Simpson's 1/3 composite rule with 300 intervals for high precision
        val n = 300
        val h = (b - a) / n
        var sum = evalAtX(fnExpr, a, angleUnit, variables) + evalAtX(fnExpr, b, angleUnit, variables)

        for (i in 1 until n) {
            val x = a + i * h
            val factor = if (i % 2 == 0) 2.0 else 4.0
            sum += factor * evalAtX(fnExpr, x, angleUnit, variables)
        }
        val result = (h / 3.0) * sum
        return cleanFloatArtifacts(result)
    }

    private fun evalDerivative(expr: String, angleUnit: AngleUnit, variables: Map<String, Double>): Double {
        val inner = expr.substring(expr.indexOf('(') + 1, expr.lastIndexOf(')'))
        val parts = splitTopLevel(inner, ',')
        if (parts.size != 2) throw IllegalArgumentException("Syntax ERROR: expected d/dx(f(x), point)")

        val fnExpr = parts[0].trim()
        val x0Str = parts[1].trim()
        if (fnExpr.isEmpty() || x0Str.isEmpty()) {
            throw IllegalArgumentException("Syntax ERROR: incomplete derivative")
        }
        val x0 = parseAndEval(x0Str, angleUnit, variables)

        // 5-point symmetric stencil: (-f(x0+2h) + 8f(x0+h) - 8f(x0-h) + f(x0-2h)) / (12h) with h = 1e-5
        val h = 1e-5
        val fP2 = evalAtX(fnExpr, x0 + 2 * h, angleUnit, variables)
        val fP1 = evalAtX(fnExpr, x0 + h, angleUnit, variables)
        val fM1 = evalAtX(fnExpr, x0 - h, angleUnit, variables)
        val fM2 = evalAtX(fnExpr, x0 - 2 * h, angleUnit, variables)

        val deriv = (-fP2 + 8.0 * fP1 - 8.0 * fM1 + fM2) / (12.0 * h)
        return cleanFloatArtifacts(deriv)
    }

    private fun evalSummation(expr: String, angleUnit: AngleUnit, variables: Map<String, Double>): Double {
        val inner = expr.substring(expr.indexOf('(') + 1, expr.lastIndexOf(')'))
        val parts = splitTopLevel(inner, ',')
        if (parts.size != 3) throw IllegalArgumentException("Syntax ERROR: expected Σ(f(x), a, b)")

        val fnExpr = parts[0].trim()
        val start = parseAndEval(parts[1].trim(), angleUnit, variables).toLong()
        val end = parseAndEval(parts[2].trim(), angleUnit, variables).toLong()

        if (end < start || end - start > 100000) throw ArithmeticException("Summation limit out of range")
        var sum = 0.0
        for (k in start..end) {
            sum += evalAtX(fnExpr, k.toDouble(), angleUnit, variables)
        }
        return cleanFloatArtifacts(sum)
    }

    private fun evalAtX(fnExpr: String, xVal: Double, angleUnit: AngleUnit, vars: Map<String, Double>): Double {
        val updatedVars = vars.toMutableMap()
        updatedVars["x"] = xVal
        updatedVars["X"] = xVal
        val sub = fnExpr.replace(Regex("""\b[xX]\b"""), "($xVal)")
        return parseAndEval(sub, angleUnit, updatedVars)
    }

    private fun evalPol(expr: String, angleUnit: AngleUnit, ans: Double, variables: Map<String, Double>): EvalResult {
        val inner = expr.substring(expr.indexOf('(') + 1, expr.lastIndexOf(')'))
        val parts = splitTopLevel(inner, ',')
        if (parts.size != 2) return EvalResult.Error("Syntax ERROR: expected pol(x, y)")
        return try {
            val xVal = parseAndEval(preprocessExpression(parts[0], ans, variables), angleUnit, variables)
            val yVal = parseAndEval(preprocessExpression(parts[1], ans, variables), angleUnit, variables)
            val r = sqrt(xVal * xVal + yVal * yVal)
            val rad = atan2(yVal, xVal)
            val theta = if (angleUnit == AngleUnit.RAD) rad else Math.toDegrees(rad)
            val rClean = cleanFloatArtifacts(r)
            val thClean = cleanFloatArtifacts(theta)
            val thetaUnit = if (angleUnit == AngleUnit.RAD) "r" else "°"
            val display = "r=${formatDecimal(rClean)}, θ=${formatDecimal(thClean)}$thetaUnit"
            EvalResult.Success(
                value = rClean,
                standardFormatted = display,
                sdOptions = listOf(display, "r=${formatDecimal(rClean)}", "θ=${formatDecimal(thClean)}")
            )
        } catch (e: Exception) {
            EvalResult.Error("Math ERROR")
        }
    }

    private fun evalRec(expr: String, angleUnit: AngleUnit, ans: Double, variables: Map<String, Double>): EvalResult {
        val inner = expr.substring(expr.indexOf('(') + 1, expr.lastIndexOf(')'))
        val parts = splitTopLevel(inner, ',')
        if (parts.size != 2) return EvalResult.Error("Syntax ERROR: expected rec(r, θ)")
        return try {
            val rVal = parseAndEval(preprocessExpression(parts[0], ans, variables), angleUnit, variables)
            val thVal = parseAndEval(preprocessExpression(parts[1], ans, variables), angleUnit, variables)
            val rad = if (angleUnit == AngleUnit.RAD) thVal else Math.toRadians(thVal)
            val x = rVal * cos(rad)
            val y = rVal * sin(rad)
            val xClean = cleanFloatArtifacts(x)
            val yClean = cleanFloatArtifacts(y)
            val display = "x=${formatDecimal(xClean)}, y=${formatDecimal(yClean)}"
            EvalResult.Success(
                value = xClean,
                standardFormatted = display,
                sdOptions = listOf(display, "x=${formatDecimal(xClean)}", "y=${formatDecimal(yClean)}")
            )
        } catch (e: Exception) {
            EvalResult.Error("Math ERROR")
        }
    }

    private fun splitTopLevel(s: String, delimiter: Char): List<String> {
        val list = mutableListOf<String>()
        var depth = 0
        var current = StringBuilder()
        for (c in s) {
            when (c) {
                '(' -> depth++
                ')' -> depth--
                delimiter -> {
                    if (depth == 0) {
                        list.add(current.toString())
                        current = StringBuilder()
                        continue
                    }
                }
            }
            current.append(c)
        }
        if (current.isNotEmpty()) list.add(current.toString())
        return list
    }

    // Tokenizer
    private sealed class Token {
        data class Number(val value: Double) : Token()
        data class Operator(val op: Char) : Token()
        data class Identifier(val name: String) : Token()
        object OpenParen : Token()
        object CloseParen : Token()
        object Comma : Token()
        object Exclamation : Token()
        object Percent : Token()
    }

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val len = input.length

        while (i < len) {
            val c = input[i]
            when {
                c.isWhitespace() -> i++
                c in "+-*/^" -> {
                    tokens.add(Token.Operator(c))
                    i++
                }
                c == '(' -> {
                    tokens.add(Token.OpenParen)
                    i++
                }
                c == ')' -> {
                    tokens.add(Token.CloseParen)
                    i++
                }
                c == ',' -> {
                    tokens.add(Token.Comma)
                    i++
                }
                c == '!' -> {
                    tokens.add(Token.Exclamation)
                    i++
                }
                c == '%' -> {
                    tokens.add(Token.Percent)
                    i++
                }
                c.isDigit() || (c == '.' && i + 1 < len && input[i + 1].isDigit()) -> {
                    val start = i
                    var hasDot = c == '.'
                    var hasExp = false
                    i++
                    while (i < len) {
                        val ch = input[i]
                        if (ch.isDigit()) {
                            i++
                        } else if (ch == '.' && !hasDot && !hasExp) {
                            hasDot = true
                            i++
                        } else if ((ch == 'E' || ch == 'e') && !hasExp) {
                            hasExp = true
                            i++
                            if (i < len && (input[i] == '+' || input[i] == '-')) {
                                i++
                            }
                        } else {
                            break
                        }
                    }
                    val numStr = input.substring(start, i)
                    val value = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")
                    tokens.add(Token.Number(value))
                }
                c.isLetter() || c == '_' -> {
                    val start = i
                    while (i < len && (input[i].isLetter() || input[i].isDigit() || input[i] == '_')) {
                        i++
                    }
                    val name = input.substring(start, i)
                    tokens.add(Token.Identifier(name))
                }
                else -> {
                    throw IllegalArgumentException("Unexpected character: $c")
                }
            }
        }
        return tokens
    }

    private class Parser(
        private val tokens: List<Token>,
        private val angleUnit: AngleUnit,
        private val variables: Map<String, Double>
    ) {
        private var pos = 0

        fun parseExpression(): Double = parseAddSub()

        private fun peek(): Token? = if (pos < tokens.size) tokens[pos] else null
        private fun consume(): Token = tokens[pos++]

        private fun parseAddSub(): Double {
            var left = parseMulDiv()
            while (true) {
                val next = peek()
                if (next is Token.Operator && (next.op == '+' || next.op == '-')) {
                    consume()
                    val right = parseMulDiv()
                    left = if (next.op == '+') left + right else left - right
                } else {
                    break
                }
            }
            return left
        }

        private fun parseMulDiv(): Double {
            var left = parsePower()
            while (true) {
                val next = peek()
                if (next is Token.Operator && (next.op == '*' || next.op == '/')) {
                    consume()
                    val right = parsePower()
                    if (next.op == '/') {
                        if (abs(right) < 1e-15) throw ArithmeticException("Division by zero")
                        left /= right
                    } else {
                        left *= right
                    }
                } else {
                    break
                }
            }
            return left
        }

        private fun parsePower(): Double {
            var base = parsePostfix()
            if (peek() is Token.Operator && (peek() as Token.Operator).op == '^') {
                consume()
                val exp = parsePower() // right-associative
                base = base.pow(exp)
            }
            return base
        }

        private fun parsePostfix(): Double {
            var value = parseUnary()
            while (true) {
                when (peek()) {
                    is Token.Exclamation -> {
                        consume()
                        value = factorial(value)
                    }
                    is Token.Percent -> {
                        consume()
                        value /= 100.0
                    }
                    else -> break
                }
            }
            return value
        }

        private fun parseUnary(): Double {
            val token = peek()
            if (token is Token.Operator) {
                if (token.op == '+') {
                    consume()
                    return parseUnary()
                } else if (token.op == '-') {
                    consume()
                    return -parseUnary()
                }
            }
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            val token = peek() ?: throw IllegalArgumentException("Unexpected end of expression")
            when (token) {
                is Token.Number -> {
                    consume()
                    return token.value
                }
                is Token.OpenParen -> {
                    consume()
                    val expr = parseAddSub()
                    if (peek() !is Token.CloseParen) throw IllegalArgumentException("Missing closing parenthesis")
                    consume()
                    return expr
                }
                is Token.Identifier -> {
                    consume()
                    val name = token.name.lowercase()
                    if (peek() is Token.OpenParen) {
                        consume() // '('
                        val arg = parseAddSub()
                        val secondArg = if (peek() is Token.Comma) {
                            consume()
                            parseAddSub()
                        } else null
                        if (peek() !is Token.CloseParen) throw IllegalArgumentException("Missing closing parenthesis for $name")
                        consume() // ')'
                        return evaluateFunction(name, arg, secondArg)
                    } else {
                        return evaluateConstantOrVariable(token.name)
                    }
                }
                else -> throw IllegalArgumentException("Unexpected token in expression: $token")
            }
        }

        private fun evaluateFunction(name: String, arg: Double, secondArg: Double?): Double {
            return when (name) {
                "sin" -> {
                    val rad = angleUnit.toRadians(arg)
                    cleanTrigResult(sin(rad), rad, isSine = true)
                }
                "cos" -> {
                    val rad = angleUnit.toRadians(arg)
                    cleanTrigResult(cos(rad), rad, isSine = false)
                }
                "tan" -> {
                    val rad = angleUnit.toRadians(arg)
                    val c = cos(rad)
                    if (abs(c) < 1e-12) throw ArithmeticException("Tangent undefined")
                    cleanFloatArtifacts(tan(rad))
                }
                "asin", "arcsin" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error")
                    angleUnit.fromRadians(asin(arg))
                }
                "acos", "arccos" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error")
                    angleUnit.fromRadians(acos(arg))
                }
                "atan", "arctan" -> {
                    angleUnit.fromRadians(atan(arg))
                }
                "sinh" -> sinh(arg)
                "cosh" -> cosh(arg)
                "tanh" -> tanh(arg)
                "asinh" -> ln(arg + sqrt(arg * arg + 1.0))
                "acosh" -> {
                    if (arg < 1.0) throw ArithmeticException("Domain error")
                    ln(arg + sqrt(arg * arg - 1.0))
                }
                "atanh" -> {
                    if (abs(arg) >= 1.0) throw ArithmeticException("Domain error")
                    0.5 * ln((1.0 + arg) / (1.0 - arg))
                }
                "sqrt" -> {
                    if (arg < 0) throw ArithmeticException("Math ERROR")
                    sqrt(arg)
                }
                "cbrt" -> cbrt(arg)
                "log" -> {
                    if (secondArg != null) {
                        val baseVal = arg
                        val valueVal = secondArg
                        if (baseVal <= 0.0 || abs(baseVal - 1.0) < 1e-12) throw ArithmeticException("Base error")
                        if (valueVal <= 0.0) throw ArithmeticException("Domain error")
                        log10(valueVal) / log10(baseVal)
                    } else {
                        if (arg <= 0.0) throw ArithmeticException("Domain error")
                        log10(arg)
                    }
                }
                "ln" -> {
                    if (arg <= 0) throw ArithmeticException("Domain error")
                    ln(arg)
                }
                "exp" -> exp(arg)
                "abs" -> abs(arg)
                "npr" -> {
                    val n = arg.roundToLong()
                    val r = (secondArg ?: 1.0).roundToLong()
                    if (n < 0 || r < 0 || r > n) throw ArithmeticException("Math ERROR")
                    var res = 1.0
                    for (k in 0 until r) res *= (n - k)
                    res
                }
                "ncr" -> {
                    val n = arg.roundToLong()
                    val r = (secondArg ?: 1.0).roundToLong()
                    if (n < 0 || r < 0 || r > n) throw ArithmeticException("Math ERROR")
                    val effectiveR = min(r, n - r)
                    var res = 1.0
                    for (k in 1..effectiveR) {
                        res = res * (n - k + 1) / k
                    }
                    res
                }
                else -> throw IllegalArgumentException("Unknown function: $name")
            }
        }

        private fun evaluateConstantOrVariable(name: String): Double {
            return when (name.lowercase()) {
                "pi" -> Math.PI
                "e" -> Math.E
                else -> {
                    variables[name]
                        ?: variables[name.uppercase()]
                        ?: variables[name.lowercase()]
                        ?: 0.0
                }
            }
        }

        private fun factorial(v: Double): Double {
            if (v < 0 || abs(v - v.roundToLong()) > 1e-9) throw ArithmeticException("Math ERROR")
            val n = v.roundToLong()
            if (n > 170) throw ArithmeticException("Overflow")
            var res = 1.0
            for (i in 2..n) res *= i
            return res
        }

        private fun cleanTrigResult(value: Double, rad: Double, isSine: Boolean): Double {
            val pi = Math.PI
            val norm = (rad % (2 * pi) + 2 * pi) % (2 * pi)
            if (isSine) {
                if (abs(norm) < 1e-10 || abs(norm - pi) < 1e-10 || abs(norm - 2 * pi) < 1e-10) return 0.0
                if (abs(norm - pi / 2) < 1e-10) return 1.0
                if (abs(norm - 3 * pi / 2) < 1e-10) return -1.0
            } else {
                if (abs(norm - pi / 2) < 1e-10 || abs(norm - 3 * pi / 2) < 1e-10) return 0.0
                if (abs(norm) < 1e-10 || abs(norm - 2 * pi) < 1e-10) return 1.0
                if (abs(norm - pi) < 1e-10) return -1.0
            }
            return cleanFloatArtifacts(value)
        }
    }

    companion object {
        fun cleanFloatArtifacts(value: Double): Double {
            if (abs(value) < 1e-13) return 0.0
            val rounded = value.roundToLong()
            if (abs(value - rounded) < 1e-11) return rounded.toDouble()
            return BigDecimal(value).setScale(12, RoundingMode.HALF_UP).toDouble()
        }

        fun formatDecimal(value: Double): String {
            if (value.isNaN()) return "Math ERROR"
            if (value.isInfinite()) return if (value > 0) "∞" else "-∞"

            val clean = cleanFloatArtifacts(value)
            val absVal = abs(clean)

            // Very large or very small -> scientific notation
            if (absVal != 0.0 && (absVal >= 1e10 || absVal < 1e-5)) {
                val symbols = DecimalFormatSymbols(Locale.US)
                val df = DecimalFormat("0.##########E0", symbols)
                return df.format(clean).replace("E", "×10^")
            }

            // Normal fixed/integer format
            val longVal = clean.toLong()
            return if (clean == longVal.toDouble()) {
                longVal.toString()
            } else {
                val symbols = DecimalFormatSymbols(Locale.US)
                val df = DecimalFormat("#,##0.##########", symbols)
                df.isGroupingUsed = false
                df.format(clean)
            }
        }

        fun formatEngineering(value: Double, offsetShift: Int = 0): String {
            if (value == 0.0 || value.isNaN() || value.isInfinite()) return formatDecimal(value)
            val sign = if (value < 0) "-" else ""
            val absVal = abs(value)
            val log10Val = log10(absVal)
            var exp3 = (floor(log10Val / 3.0) * 3).toInt() + offsetShift * 3
            val mantissa = absVal / 10.0.pow(exp3.toDouble())
            val symbols = DecimalFormatSymbols(Locale.US)
            val df = DecimalFormat("0.#####", symbols)
            val mStr = df.format(mantissa)
            return if (exp3 == 0) "$sign$mStr" else "$sign$mStr×10^$exp3"
        }

        fun toDmsString(decimalDegrees: Double): String {
            val sign = if (decimalDegrees < 0) "-" else ""
            val totalSec = (abs(decimalDegrees) * 3600.0).roundToLong()
            val deg = totalSec / 3600
            val min = (totalSec % 3600) / 60
            val sec = totalSec % 60
            return "$sign$deg°$min′$sec″"
        }

        fun generateSdOptions(value: Double, angleUnit: AngleUnit): List<String> {
            val options = mutableListOf<String>()

            // 1. Exact fraction if available
            val frac = Fraction.fromDouble(value)
            if (frac != null && !frac.isInteger) {
                options.add(frac.toStandardString())
                val mixed = frac.toMixedString()
                if (mixed != frac.toStandardString()) {
                    options.add(mixed)
                }
            }

            // 2. Pi multiples (e.g. 1.57079632679 -> π/2)
            if (abs(value) > 1e-6) {
                val piMultiple = value / Math.PI
                val piFrac = Fraction.fromDouble(piMultiple, maxDenominator = 200)
                if (piFrac != null) {
                    val piStr = when {
                        piFrac.num == 1L && piFrac.den == 1L -> "π"
                        piFrac.num == -1L && piFrac.den == 1L -> "-π"
                        piFrac.den == 1L -> "${piFrac.num}π"
                        piFrac.num == 1L -> "π/${piFrac.den}"
                        piFrac.num == -1L -> "-π/${piFrac.den}"
                        else -> "${piFrac.num}π/${piFrac.den}"
                    }
                    if (!options.contains(piStr)) {
                        options.add(piStr)
                    }
                }
            }

            // 3. Square root representation: check if value^2 is a small clean rational
            val sq = value * value
            val sqFrac = Fraction.fromDouble(sq, maxDenominator = 20)
            if (sqFrac != null && sqFrac.den == 1L && sqFrac.num in 2..50) {
                val sqrtVal = sqrt(sqFrac.num.toDouble())
                if (abs(sqrtVal - abs(value)) < 1e-6) {
                    val prefix = if (value < 0) "-" else ""
                    val rootStr = "${prefix}√${sqFrac.num}"
                    if (!options.contains(rootStr)) {
                        options.add(rootStr)
                    }
                }
            }

            // 4. Standard decimal
            val decStr = formatDecimal(value)
            if (!options.contains(decStr)) {
                options.add(decStr)
            }

            // 5. Degree-Minute-Second DMS format
            if (angleUnit == AngleUnit.DEG && abs(value) < 36000) {
                val dmsStr = toDmsString(value)
                if (!options.contains(dmsStr)) {
                    options.add(dmsStr)
                }
            }

            return if (options.isEmpty()) listOf(formatDecimal(value)) else options
        }
    }
}
