package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Natural Textbook Display (Natural-V.P.A.M.) renderer for CASIO fx-991EX ClassWiz.
 *
 * Renders:
 * 1. Radicals with continuous horizontal overline (vinculum) spanning across enclosed numbers/content (e.g. √333, √(333)).
 * 2. 2-story vertical fractions with horizontal fraction bar and D-pad UP/DOWN cursor navigation.
 * 3. Elevated superscripts for powers (e.g. 5³ without '^' symbol).
 * 4. Inverse trigonometric functions as sin⁻¹(, cos⁻¹(, tan⁻¹(.
 */

/**
 * Formats math text into an AnnotatedString where power expressions like "^3", "²", "³"
 * are converted into smaller elevated superscripts with no '^' symbol.
 */
fun formatAnnotatedMathText(
    text: String,
    fontSize: TextUnit = 18.sp,
    color: Color = Color(0xFF1B281B),
    fontFamily: FontFamily = FontFamily.Monospace,
    fontWeight: FontWeight = FontWeight.Bold
): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        val len = text.length
        while (i < len) {
            when {
                // Power operator '^': render whatever follows as elevated superscript
                text[i] == '^' -> {
                    i++ // Skip '^'
                    if (i < len && text[i] == '(') {
                        i++ // Skip '('
                        val expStart = i
                        while (i < len && text[i] != ')') {
                            i++
                        }
                        val expContent = text.substring(expStart, i).ifEmpty { "□" }
                        if (i < len && text[i] == ')') i++ // Skip ')'
                        withStyle(
                            SpanStyle(
                                baselineShift = BaselineShift(0.6f),
                                fontSize = fontSize * 0.65f,
                                fontWeight = FontWeight.Bold,
                                color = color,
                                fontFamily = fontFamily
                            )
                        ) {
                            append(expContent)
                        }
                    } else {
                        val expStart = i
                        while (i < len && (text[i].isDigit() || text[i].isLetter() || text[i] == '.' || text[i] == '-' || text[i] == '⁻' || text[i] == '¹')) {
                            i++
                        }
                        val expContent = if (expStart == i) "□" else text.substring(expStart, i)
                        withStyle(
                            SpanStyle(
                                baselineShift = BaselineShift(0.6f),
                                fontSize = fontSize * 0.65f,
                                fontWeight = FontWeight.Bold,
                                color = color,
                                fontFamily = fontFamily
                            )
                        ) {
                            append(expContent)
                        }
                    }
                }
                text[i] == '²' -> {
                    withStyle(
                        SpanStyle(
                            baselineShift = BaselineShift(0.6f),
                            fontSize = fontSize * 0.65f,
                            fontWeight = FontWeight.Bold,
                            color = color,
                            fontFamily = fontFamily
                        )
                    ) {
                        append("2")
                    }
                    i++
                }
                text[i] == '³' -> {
                    withStyle(
                        SpanStyle(
                            baselineShift = BaselineShift(0.6f),
                            fontSize = fontSize * 0.65f,
                            fontWeight = FontWeight.Bold,
                            color = color,
                            fontFamily = fontFamily
                        )
                    ) {
                        append("3")
                    }
                    i++
                }
                text[i] == '⁻' && i + 1 < len && text[i + 1] == '¹' -> {
                    withStyle(
                        SpanStyle(
                            baselineShift = BaselineShift(0.6f),
                            fontSize = fontSize * 0.65f,
                            fontWeight = FontWeight.Bold,
                            color = color,
                            fontFamily = fontFamily
                        )
                    ) {
                        append("-1")
                    }
                    i += 2
                }
                text.startsWith("sqrt(", i) -> {
                    append("√(")
                    i += 5
                }
                text.startsWith("cbrt(", i) -> {
                    append("³√(")
                    i += 5
                }
                text.startsWith("asin(", i) -> {
                    append("sin⁻¹(")
                    i += 5
                }
                text.startsWith("acos(", i) -> {
                    append("cos⁻¹(")
                    i += 5
                }
                text.startsWith("atan(", i) -> {
                    append("tan⁻¹(")
                    i += 5
                }
                text.startsWith("*10^", i) -> {
                    append("×10^")
                    i += 4
                }
                text[i] == '*' -> {
                    append("×")
                    i++
                }
                else -> {
                    append(text[i])
                    i++
                }
            }
        }
    }
}

/**
 * Radical box that draws the radical symbol on the left and a continuous horizontal
 * overline (vinculum) spanning across the enclosed content width.
 */
@Composable
fun RadicalBox(
    isCube: Boolean,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF1B281B),
    fontSize: TextUnit = 17.sp,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.padding(horizontal = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isCube) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(end = 1.dp)
            ) {
                Text(
                    text = "3",
                    fontSize = (fontSize.value * 0.55f).sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = lineColor,
                    modifier = Modifier.offset(x = 1.dp, y = (-2).dp)
                )
                Text(
                    text = "√",
                    fontSize = (fontSize.value * 1.15f).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = lineColor
                )
            }
        } else {
            Text(
                text = "√",
                fontSize = (fontSize.value * 1.15f).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = lineColor,
                modifier = Modifier.padding(end = 1.dp)
            )
        }

        // Inside content under the continuous horizontal vinculum
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 12.dp)
                .drawBehind {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, 2.dp.toPx()),
                        end = Offset(size.width, 2.dp.toPx()),
                        strokeWidth = 1.8.dp.toPx()
                    )
                }
                .padding(top = 3.dp, start = 1.5.dp, end = 2.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            content()
        }
    }
}

/**
 * Renders an arbitrary base logarithm block with subscript base box and argument value box.
 * The base box dynamically auto-expands with number of digits.
 * D-pad moves focus & cursor between base and value boxes.
 */
@Composable
fun LogBaseBox(
    base: String,
    value: String,
    isBaseFocus: Boolean,
    isValueFocus: Boolean,
    baseCursorOffset: Int,
    valueCursorOffset: Int,
    cursorAlpha: Float,
    fontSize: TextUnit = 17.sp,
    textColor: Color = Color(0xFF1B281B),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // "log" text
        Text(
            text = "log",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            modifier = Modifier.padding(bottom = 2.dp, end = 1.dp)
        )

        // Subscript base box (auto-expanding width based on number of digits)
        val baseBoxWidth = ((base.length.coerceAtLeast(1)) * 9).dp + 12.dp
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = baseBoxWidth)
                .height((fontSize.value * 1.05f).dp)
                .padding(bottom = 0.5.dp, end = 2.dp)
                .border(
                    width = if (isBaseFocus) 1.5.dp else 0.8.dp,
                    color = if (isBaseFocus) textColor else textColor.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(2.dp)
                )
                .background(
                    if (isBaseFocus) textColor.copy(alpha = 0.08f) else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
                .padding(horizontal = 3.dp, vertical = 0.5.dp),
            contentAlignment = Alignment.Center
        ) {
            if (base.isEmpty()) {
                if (isBaseFocus) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height((fontSize.value * 0.75f).dp)
                            .background(textColor.copy(alpha = cursorAlpha))
                    )
                } else {
                    Text(
                        text = "□",
                        fontSize = (fontSize.value * 0.75f).sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = textColor.copy(alpha = 0.5f)
                    )
                }
            } else if (isBaseFocus && baseCursorOffset >= 0) {
                val rel = baseCursorOffset.coerceIn(0, base.length)
                val b = base.substring(0, rel)
                val a = base.substring(rel)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (b.isNotEmpty()) {
                        Text(
                            text = b,
                            fontSize = (fontSize.value * 0.75f).sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = textColor
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height((fontSize.value * 0.75f).dp)
                            .background(textColor.copy(alpha = cursorAlpha))
                    )
                    if (a.isNotEmpty()) {
                        Text(
                            text = a,
                            fontSize = (fontSize.value * 0.75f).sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = textColor
                        )
                    }
                }
            } else {
                Text(
                    text = base,
                    fontSize = (fontSize.value * 0.75f).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = textColor
                )
            }
        }

        // Opening parenthesis
        Text(
            text = "(",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        // Argument value box
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 14.dp)
                .height((fontSize.value * 1.25f).dp)
                .padding(bottom = 1.dp)
                .border(
                    width = if (isValueFocus) 1.5.dp else 0.5.dp,
                    color = if (isValueFocus) textColor else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
                .background(
                    if (isValueFocus) textColor.copy(alpha = 0.06f) else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
                .padding(horizontal = 3.dp, vertical = 0.5.dp),
            contentAlignment = Alignment.Center
        ) {
            if (value.isEmpty()) {
                if (isValueFocus) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height((fontSize.value * 1.0f).dp)
                            .background(textColor.copy(alpha = cursorAlpha))
                    )
                } else {
                    Text(
                        text = "□",
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = textColor.copy(alpha = 0.5f)
                    )
                }
            } else if (isValueFocus && valueCursorOffset >= 0) {
                val rel = valueCursorOffset.coerceIn(0, value.length)
                val b = value.substring(0, rel)
                val a = value.substring(rel)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (b.isNotEmpty()) {
                        Text(
                            text = formatAnnotatedMathText(b, fontSize = fontSize, color = textColor),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = textColor
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height((fontSize.value * 1.0f).dp)
                            .background(textColor.copy(alpha = cursorAlpha))
                    )
                    if (a.isNotEmpty()) {
                        Text(
                            text = formatAnnotatedMathText(a, fontSize = fontSize, color = textColor),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = textColor
                        )
                    }
                }
            } else {
                Text(
                    text = formatAnnotatedMathText(value, fontSize = fontSize, color = textColor),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = textColor
                )
            }
        }

        // Closing parenthesis
        Text(
            text = ")",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

/**
 * Generic calculus box with blinking cursor and focus border highlight.
 */
@Composable
fun CalculusBox(
    text: String,
    isFocus: Boolean,
    cursorOffset: Int,
    cursorAlpha: Float,
    fontSize: TextUnit,
    textColor: Color,
    minWidth: Dp = 16.dp,
    height: Dp? = null,
    emptyBoxSymbol: String = "□",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth)
            .then(if (height != null) Modifier.height(height) else Modifier)
            .border(
                width = if (isFocus) 1.5.dp else 0.8.dp,
                color = if (isFocus) textColor else textColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(2.dp)
            )
            .background(
                if (isFocus) textColor.copy(alpha = 0.08f) else Color.Transparent,
                shape = RoundedCornerShape(2.dp)
            )
            .padding(horizontal = 3.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        if (text.isEmpty()) {
            if (isFocus) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height((fontSize.value * 1.0f).dp)
                        .background(textColor.copy(alpha = cursorAlpha))
                )
            } else {
                Text(
                    text = emptyBoxSymbol,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = textColor.copy(alpha = 0.5f)
                )
            }
        } else if (isFocus && cursorOffset >= 0) {
            val rel = cursorOffset.coerceIn(0, text.length)
            val before = text.substring(0, rel)
            val after = text.substring(rel)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (before.isNotEmpty()) {
                    Text(
                        text = formatAnnotatedMathText(before, fontSize = fontSize, color = textColor),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = textColor
                    )
                }
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height((fontSize.value * 1.0f).dp)
                        .background(textColor.copy(alpha = cursorAlpha))
                )
                if (after.isNotEmpty()) {
                    Text(
                        text = formatAnnotatedMathText(after, fontSize = fontSize, color = textColor),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = textColor
                    )
                }
            }
        } else {
            Text(
                text = formatAnnotatedMathText(text, fontSize = fontSize, color = textColor),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = textColor
            )
        }
    }
}

/**
 * Natural Textbook Definite Integral Box: ∫ with stacked upper/lower limit boxes,
 * function expression box, and trailing italic dx.
 */
@Composable
fun IntegralBox(
    intFunc: String,
    lowLimit: String,
    upLimit: String,
    isFuncFocus: Boolean,
    isLowFocus: Boolean,
    isUpFocus: Boolean,
    funcCursorOffset: Int,
    lowCursorOffset: Int,
    upCursorOffset: Int,
    cursorAlpha: Float,
    fontSize: TextUnit = 17.sp,
    textColor: Color = Color(0xFF1B281B),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tall Integral Symbol '∫'
        Text(
            text = "∫",
            fontSize = (fontSize.value * 2.3f).sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            color = textColor,
            modifier = Modifier
                .offset(y = (-1).dp)
                .padding(end = 1.dp)
        )

        // Column with Upper limit (top) and Lower limit (bottom)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height((fontSize.value * 2.5f).dp)
                .padding(end = 3.dp)
        ) {
            // Upper limit box (b)
            CalculusBox(
                text = upLimit,
                isFocus = isUpFocus,
                cursorOffset = upCursorOffset,
                cursorAlpha = cursorAlpha,
                fontSize = (fontSize.value * 0.72f).sp,
                textColor = textColor,
                minWidth = 14.dp,
                height = (fontSize.value * 0.95f).dp
            )

            // Lower limit box (a)
            CalculusBox(
                text = lowLimit,
                isFocus = isLowFocus,
                cursorOffset = lowCursorOffset,
                cursorAlpha = cursorAlpha,
                fontSize = (fontSize.value * 0.72f).sp,
                textColor = textColor,
                minWidth = 14.dp,
                height = (fontSize.value * 0.95f).dp
            )
        }

        // Integrand Function Expression Box f(x)
        CalculusBox(
            text = intFunc,
            isFocus = isFuncFocus,
            cursorOffset = funcCursorOffset,
            cursorAlpha = cursorAlpha,
            fontSize = fontSize,
            textColor = textColor,
            minWidth = 20.dp,
            height = (fontSize.value * 1.35f).dp
        )

        // Trailing italic "dx"
        Text(
            text = "dx",
            fontSize = fontSize,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}

/**
 * Natural Textbook Numerical Differentiation Box: d/dx(diffFunc)|_{x=xPoint}
 */
@Composable
fun DerivativeBox(
    diffFunc: String,
    xPoint: String,
    isDiffFuncFocus: Boolean,
    isPointFocus: Boolean,
    diffFuncCursorOffset: Int,
    pointCursorOffset: Int,
    cursorAlpha: Float,
    fontSize: TextUnit = 17.sp,
    textColor: Color = Color(0xFF1B281B),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vertical fraction d/dx followed by "("
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(end = 1.dp)
        ) {
            Text(
                text = "d",
                fontSize = (fontSize.value * 0.72f).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = textColor
            )
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(1.2.dp)
                    .background(textColor)
            )
            Text(
                text = "dx",
                fontSize = (fontSize.value * 0.72f).sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = textColor
            )
        }

        Text(
            text = "(",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            modifier = Modifier.padding(end = 1.dp)
        )

        // Function expression box
        CalculusBox(
            text = diffFunc,
            isFocus = isDiffFuncFocus,
            cursorOffset = diffFuncCursorOffset,
            cursorAlpha = cursorAlpha,
            fontSize = fontSize,
            textColor = textColor,
            minWidth = 20.dp,
            height = (fontSize.value * 1.35f).dp
        )

        Text(
            text = ")",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            modifier = Modifier.padding(horizontal = 1.dp)
        )

        // Tall vertical evaluation bar '|'
        Box(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .width(1.5.dp)
                .height((fontSize.value * 1.8f).dp)
                .background(textColor)
        )

        // Subscript "x="
        Text(
            text = "x=",
            fontSize = (fontSize.value * 0.72f).sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            modifier = Modifier
                .offset(y = 4.dp)
                .padding(end = 1.dp)
        )

        // Evaluation point box (at bottom subscript)
        CalculusBox(
            text = xPoint,
            isFocus = isPointFocus,
            cursorOffset = pointCursorOffset,
            cursorAlpha = cursorAlpha,
            fontSize = (fontSize.value * 0.75f).sp,
            textColor = textColor,
            minWidth = 16.dp,
            height = (fontSize.value * 1.0f).dp,
            modifier = Modifier.offset(y = 4.dp)
        )
    }
}

/**
 * Renders a segment of formula text, recognizing radicals (with continuous vinculum overline),
 * arbitrary-base logarithm blocks, definite integrals, and numerical derivatives,
 * placing the blinking cursor accurately inside or outside.
 */
@Composable
fun MathSegmentView(
    text: String,
    startOffset: Int,
    cursorIndex: Int,
    isEvaluated: Boolean,
    cursorAlpha: Float,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 17.sp,
    textColor: Color = Color(0xFF1B281B)
) {
    if (text.isEmpty()) {
        val hasCursor = !isEvaluated && cursorIndex == startOffset
        if (hasCursor) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height((fontSize.value * 1.1f).dp)
                    .background(textColor.copy(alpha = cursorAlpha))
            )
        }
        return
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var idx = 0
        val len = text.length

        while (idx < len) {
            // Check for definite integral: "∫(" or "integral(" with 2 commas
            val isIntegral = (text.startsWith("∫(", idx) || text.startsWith("integral(", idx)) && run {
                val prefixLen = if (text.startsWith("∫(", idx)) 2 else 9
                var d = 1
                var cc = 0
                for (j in (idx + prefixLen) until len) {
                    if (text[j] == '(') d++
                    else if (text[j] == ')') { d--; if (d == 0) break }
                    else if (text[j] == ',' && d == 1) cc++
                }
                cc >= 2
            }

            // Check for derivative: "d/dx(" or "diff(" with comma
            val isDerivative = (text.startsWith("d/dx(", idx) || text.startsWith("diff(", idx)) && run {
                val prefixLen = 5
                var d = 1
                var hasComma = false
                for (j in (idx + prefixLen) until len) {
                    if (text[j] == '(') d++
                    else if (text[j] == ')') { d--; if (d == 0) break }
                    else if (text[j] == ',' && d == 1) { hasComma = true; break }
                }
                hasComma
            }

            // Check for log with base: "log(" followed by comma before matching ")"
            val isLogBase = !isIntegral && !isDerivative && text.startsWith("log(", idx) && run {
                var d = 1
                var hasComma = false
                for (j in (idx + 4) until len) {
                    if (text[j] == '(') d++
                    else if (text[j] == ')') {
                        d--
                        if (d == 0) break
                    } else if (text[j] == ',' && d == 1) {
                        hasComma = true
                        break
                    }
                }
                hasComma
            }

            // Check for radical start: "³√(", "∛(", "√(", "³√", "∛", "√"
            val isCubeWithParen = !isIntegral && !isDerivative && (text.startsWith("³√(", idx) || text.startsWith("∛(", idx) || text.startsWith("cbrt(", idx))
            val isSqrtWithParen = !isIntegral && !isDerivative && (text.startsWith("√(", idx) || text.startsWith("sqrt(", idx))
            val isCubeBare = !isCubeWithParen && !isIntegral && !isDerivative && (text.startsWith("³√", idx) || text.startsWith("∛", idx))
            val isSqrtBare = !isSqrtWithParen && !isIntegral && !isDerivative && text[idx] == '√'

            if (isIntegral) {
                val prefixLen = if (text.startsWith("∫(", idx)) 2 else 9
                var depth = 1
                var comma1 = -1
                var comma2 = -1
                var closeIdx = -1
                for (j in (idx + prefixLen) until len) {
                    if (text[j] == '(') depth++
                    else if (text[j] == ')') {
                        depth--
                        if (depth == 0) {
                            closeIdx = j
                            break
                        }
                    } else if (text[j] == ',' && depth == 1) {
                        if (comma1 == -1) comma1 = j
                        else if (comma2 == -1) comma2 = j
                    }
                }

                val fnEnd = if (comma1 >= 0) comma1 else len
                val lowEnd = if (comma2 >= 0) comma2 else len
                val upEnd = if (closeIdx >= 0) closeIdx else len

                val intFuncStr = text.substring(idx + prefixLen, fnEnd)
                val lowLimitStr = if (comma1 >= 0) text.substring(comma1 + 1, lowEnd) else ""
                val upLimitStr = if (comma2 >= 0) text.substring(comma2 + 1, upEnd) else ""
                val fullEnd = if (closeIdx >= 0) closeIdx + 1 else upEnd

                val globalFuncStart = startOffset + idx + prefixLen
                val globalFuncEnd = startOffset + fnEnd
                val globalLowStart = startOffset + comma1 + 1
                val globalLowEnd = startOffset + lowEnd
                val globalUpStart = startOffset + comma2 + 1
                val globalUpEnd = startOffset + upEnd

                val isFuncFocus = !isEvaluated && (cursorIndex in globalFuncStart..globalFuncEnd)
                val isLowFocus = !isEvaluated && (cursorIndex in globalLowStart..globalLowEnd)
                val isUpFocus = !isEvaluated && (cursorIndex in globalUpStart..globalUpEnd)

                val funcCursorRel = if (isFuncFocus) (cursorIndex - globalFuncStart).coerceIn(0, intFuncStr.length) else -1
                val lowCursorRel = if (isLowFocus) (cursorIndex - globalLowStart).coerceIn(0, lowLimitStr.length) else -1
                val upCursorRel = if (isUpFocus) (cursorIndex - globalUpStart).coerceIn(0, upLimitStr.length) else -1

                IntegralBox(
                    intFunc = intFuncStr,
                    lowLimit = lowLimitStr,
                    upLimit = upLimitStr,
                    isFuncFocus = isFuncFocus,
                    isLowFocus = isLowFocus,
                    isUpFocus = isUpFocus,
                    funcCursorOffset = funcCursorRel,
                    lowCursorOffset = lowCursorRel,
                    upCursorOffset = upCursorRel,
                    cursorAlpha = cursorAlpha,
                    fontSize = fontSize,
                    textColor = textColor
                )

                idx = fullEnd
            } else if (isDerivative) {
                val prefixLen = 5
                var depth = 1
                var commaIdx = -1
                var closeIdx = -1
                for (j in (idx + prefixLen) until len) {
                    if (text[j] == '(') depth++
                    else if (text[j] == ')') {
                        depth--
                        if (depth == 0) {
                            closeIdx = j
                            break
                        }
                    } else if (text[j] == ',' && depth == 1) {
                        commaIdx = j
                    }
                }

                val diffEnd = if (commaIdx >= 0) commaIdx else len
                val pointEnd = if (closeIdx >= 0) closeIdx else len

                val diffFuncStr = text.substring(idx + prefixLen, diffEnd)
                val xPointStr = if (commaIdx >= 0) text.substring(commaIdx + 1, pointEnd) else ""
                val fullEnd = if (closeIdx >= 0) closeIdx + 1 else pointEnd

                val globalDiffStart = startOffset + idx + prefixLen
                val globalDiffEnd = startOffset + diffEnd
                val globalPointStart = startOffset + commaIdx + 1
                val globalPointEnd = startOffset + pointEnd

                val isDiffFocus = !isEvaluated && (cursorIndex in globalDiffStart..globalDiffEnd)
                val isPointFocus = !isEvaluated && (cursorIndex in globalPointStart..globalPointEnd)

                val diffCursorRel = if (isDiffFocus) (cursorIndex - globalDiffStart).coerceIn(0, diffFuncStr.length) else -1
                val pointCursorRel = if (isPointFocus) (cursorIndex - globalPointStart).coerceIn(0, xPointStr.length) else -1

                DerivativeBox(
                    diffFunc = diffFuncStr,
                    xPoint = xPointStr,
                    isDiffFuncFocus = isDiffFocus,
                    isPointFocus = isPointFocus,
                    diffFuncCursorOffset = diffCursorRel,
                    pointCursorOffset = pointCursorRel,
                    cursorAlpha = cursorAlpha,
                    fontSize = fontSize,
                    textColor = textColor
                )

                idx = fullEnd
            } else if (isLogBase) {
                var depth = 1
                var commaIdx = -1
                var closeIdx = -1
                for (j in (idx + 4) until len) {
                    if (text[j] == '(') depth++
                    else if (text[j] == ')') {
                        depth--
                        if (depth == 0) {
                            closeIdx = j
                            break
                        }
                    } else if (text[j] == ',' && depth == 1) {
                        commaIdx = j
                    }
                }

                val baseStr = text.substring(idx + 4, commaIdx)
                val valueEnd = if (closeIdx >= 0) closeIdx else len
                val valueStr = text.substring(commaIdx + 1, valueEnd)
                val fullEnd = if (closeIdx >= 0) closeIdx + 1 else valueEnd

                val globalBaseStart = startOffset + idx + 4
                val globalBaseEnd = startOffset + commaIdx
                val globalValStart = startOffset + commaIdx + 1
                val globalValEnd = startOffset + valueEnd

                val isBaseFocus = !isEvaluated && (cursorIndex in globalBaseStart..globalBaseEnd)
                val isValueFocus = !isEvaluated && (cursorIndex in globalValStart..globalValEnd)

                val baseCursorRel = if (isBaseFocus) (cursorIndex - globalBaseStart).coerceIn(0, baseStr.length) else -1
                val valueCursorRel = if (isValueFocus) (cursorIndex - globalValStart).coerceIn(0, valueStr.length) else -1

                LogBaseBox(
                    base = baseStr,
                    value = valueStr,
                    isBaseFocus = isBaseFocus,
                    isValueFocus = isValueFocus,
                    baseCursorOffset = baseCursorRel,
                    valueCursorOffset = valueCursorRel,
                    cursorAlpha = cursorAlpha,
                    fontSize = fontSize,
                    textColor = textColor
                )

                idx = fullEnd
            } else if (isCubeWithParen || isSqrtWithParen || isCubeBare || isSqrtBare) {
                val isCube = isCubeWithParen || isCubeBare
                val symbolLen = when {
                    text.startsWith("cbrt(", idx) -> 5
                    text.startsWith("sqrt(", idx) -> 5
                    text.startsWith("³√(", idx) -> 3
                    text.startsWith("∛(", idx) -> 2
                    text.startsWith("√(", idx) -> 2
                    text.startsWith("³√", idx) -> 2
                    text.startsWith("∛", idx) -> 1
                    else -> 1 // '√'
                }

                val hasParen = isCubeWithParen || isSqrtWithParen
                val innerStart = idx + symbolLen
                var innerEnd = innerStart

                if (hasParen) {
                    var depth = 1
                    while (innerEnd < len && depth > 0) {
                        if (text[innerEnd] == '(') depth++
                        else if (text[innerEnd] == ')') depth--
                        if (depth > 0) innerEnd++
                    }
                } else {
                    while (innerEnd < len && (text[innerEnd].isDigit() || text[innerEnd] == '.' || text[innerEnd].isLetter())) {
                        innerEnd++
                    }
                }

                val innerContent = text.substring(innerStart, innerEnd)
                val fullRadicalEnd = if (hasParen && innerEnd < len && text[innerEnd] == ')') innerEnd + 1 else innerEnd

                // Global positions for cursor tracking
                val globalInnerStart = startOffset + innerStart
                val globalInnerEnd = startOffset + innerEnd
                val cursorInRadical = !isEvaluated && (cursorIndex in globalInnerStart..globalInnerEnd)

                RadicalBox(
                    isCube = isCube,
                    lineColor = textColor,
                    fontSize = fontSize
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (innerContent.isEmpty() && !cursorInRadical) {
                            Text(
                                text = "□",
                                fontSize = fontSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = textColor.copy(alpha = 0.5f)
                            )
                        } else if (cursorInRadical) {
                            val relPos = (cursorIndex - globalInnerStart).coerceIn(0, innerContent.length)
                            val b = innerContent.substring(0, relPos)
                            val a = innerContent.substring(relPos)

                            if (b.isNotEmpty()) {
                                Text(
                                    text = formatAnnotatedMathText(b, fontSize = fontSize, color = textColor),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = textColor
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height((fontSize.value * 1.1f).dp)
                                    .background(textColor.copy(alpha = cursorAlpha))
                            )
                            if (a.isNotEmpty()) {
                                Text(
                                    text = formatAnnotatedMathText(a, fontSize = fontSize, color = textColor),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = textColor
                                )
                            }
                        } else {
                            Text(
                                text = formatAnnotatedMathText(innerContent, fontSize = fontSize, color = textColor),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = textColor
                            )
                        }
                    }
                }

                idx = fullRadicalEnd
            } else {
                // Non-special text: read characters until next radical or log-base or integral or derivative or end
                val textStart = idx
                while (idx < len && text[idx] != '√' && text[idx] != '∛' &&
                       !text.startsWith("³√", idx) && !text.startsWith("sqrt(", idx) && !text.startsWith("cbrt(", idx) &&
                       !text.startsWith("∫(", idx) && !text.startsWith("integral(", idx) &&
                       !text.startsWith("d/dx(", idx) && !text.startsWith("diff(", idx)) {
                    if (text.startsWith("log(", idx)) {
                        // Check if this log has a comma (arbitrary base log)
                        var d = 1
                        var hasC = false
                        for (j in (idx + 4) until len) {
                            if (text[j] == '(') d++
                            else if (text[j] == ')') { d--; if (d == 0) break }
                            else if (text[j] == ',' && d == 1) { hasC = true; break }
                        }
                        if (hasC) break
                    }
                    idx++
                }

                val plain = text.substring(textStart, idx)
                val globalPlainStart = startOffset + textStart
                val globalPlainEnd = startOffset + idx
                val hasCursor = !isEvaluated && (cursorIndex in globalPlainStart..globalPlainEnd)

                if (hasCursor) {
                    val relCursor = (cursorIndex - globalPlainStart).coerceIn(0, plain.length)
                    val before = plain.substring(0, relCursor)
                    val after = plain.substring(relCursor)

                    if (before.isNotEmpty()) {
                        Text(
                            text = formatAnnotatedMathText(before, fontSize = fontSize, color = textColor),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = textColor
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height((fontSize.value * 1.1f).dp)
                            .background(textColor.copy(alpha = cursorAlpha))
                    )
                    if (after.isNotEmpty()) {
                        Text(
                            text = formatAnnotatedMathText(after, fontSize = fontSize, color = textColor),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = textColor
                        )
                    }
                } else {
                    Text(
                        text = formatAnnotatedMathText(plain, fontSize = fontSize, color = textColor),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = textColor
                    )
                }
            }
        }
    }
}

/**
 * 2-story vertical fraction with horizontal bar line and MathSegmentView in numerator and denominator.
 */
@Composable
fun VerticalFraction(
    numerator: String,
    denominator: String,
    numOffset: Int,
    denOffset: Int,
    cursorIndex: Int,
    isEvaluated: Boolean,
    cursorAlpha: Float,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 15.sp,
    textColor: Color = Color(0xFF1B281B),
    lineColor: Color = Color(0xFF1B281B)
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Numerator
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.5.dp),
            contentAlignment = Alignment.Center
        ) {
            val numHasCursor = !isEvaluated && cursorIndex in numOffset..(numOffset + numerator.length)
            if (numerator.isEmpty() && !numHasCursor) {
                Text(
                    text = "□",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = textColor.copy(alpha = 0.5f)
                )
            } else {
                MathSegmentView(
                    text = numerator,
                    startOffset = numOffset,
                    cursorIndex = cursorIndex,
                    isEvaluated = isEvaluated,
                    cursorAlpha = cursorAlpha,
                    fontSize = fontSize,
                    textColor = textColor
                )
            }
        }

        // Horizontal fraction line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minWidth = 18.dp)
                .height(1.8.dp)
                .background(lineColor)
        )

        // Denominator
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.5.dp),
            contentAlignment = Alignment.Center
        ) {
            val denHasCursor = !isEvaluated && cursorIndex in denOffset..(denOffset + denominator.length)
            if (denominator.isEmpty() && !denHasCursor) {
                Text(
                    text = "□",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = textColor.copy(alpha = 0.5f)
                )
            } else {
                MathSegmentView(
                    text = denominator,
                    startOffset = denOffset,
                    cursorIndex = cursorIndex,
                    isEvaluated = isEvaluated,
                    cursorAlpha = cursorAlpha,
                    fontSize = fontSize,
                    textColor = textColor
                )
            }
        }
    }
}

/**
 * Natural formula row display with support for:
 * - Square and cube radicals with continuous vinculum overline (√333)
 * - 2-story vertical fractions (5/3) with D-pad UP/DOWN navigation
 * - Elevated powers (5³)
 * - Accurate blinking cursor
 */
@Composable
fun NaturalFormulaRow(
    formula: String,
    cursorIndex: Int,
    isEvaluated: Boolean,
    cursorAlpha: Float,
    modifier: Modifier = Modifier
) {
    val cursor = cursorIndex.coerceIn(0, formula.length)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (formula.isEmpty()) {
            if (!isEvaluated) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(18.dp)
                        .background(Color(0xFF1B281B).copy(alpha = cursorAlpha))
                )
            }
            return@Row
        }

        // Check if there is a top-level fraction in the formula (not inside d/dx or parentheses)
        val slashIndex = run {
            var depth = 0
            for (i in formula.indices) {
                val ch = formula[i]
                if (ch == '(') depth++
                else if (ch == ')') { if (depth > 0) depth-- }
                else if (ch == '/' && depth == 0) {
                    val isDiff = (i >= 1 && formula.substring(i - 1).startsWith("d/dx"))
                    if (!isDiff) return@run i
                }
            }
            -1
        }
        if (slashIndex >= 0) {
            val numerator = formula.substring(0, slashIndex)
            val denominator = formula.substring(slashIndex + 1)

            VerticalFraction(
                numerator = numerator,
                denominator = denominator,
                numOffset = 0,
                denOffset = slashIndex + 1,
                cursorIndex = cursor,
                isEvaluated = isEvaluated,
                cursorAlpha = cursorAlpha,
                fontSize = 15.sp,
                textColor = Color(0xFF1B281B),
                lineColor = Color(0xFF1B281B)
            )
        } else {
            // No fraction: render formula directly using MathSegmentView
            MathSegmentView(
                text = formula,
                startOffset = 0,
                cursorIndex = cursor,
                isEvaluated = isEvaluated,
                cursorAlpha = cursorAlpha,
                fontSize = 18.sp,
                textColor = Color(0xFF1B281B)
            )
        }
    }
}

/**
 * Natural result display renderer with support for vertical fractions and radicals in result:
 * - Simple fraction "5/3" -> vertical fraction 5 over 3
 * - Negative fraction "-5/3" -> minus followed by vertical fraction
 * - Mixed fraction "1 2/3" -> whole 1 followed by vertical fraction 2 over 3
 * - Radicals with vinculum in result e.g. √3, 2√3
 * - Normal decimal or integer -> large LCD font
 */
@Composable
fun NaturalResultRow(
    result: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    if (isError) {
        Text(
            text = result,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF851A1A),
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = modifier
        )
        return
    }

    // Check if result is a fraction
    if (result.contains('/') && !result.contains("ERROR")) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (result.contains(' ')) {
                // Mixed fraction: e.g. "1 2/3"
                val whole = result.substringBefore(' ')
                val frac = result.substringAfter(' ')
                val num = frac.substringBefore('/')
                val den = frac.substringAfter('/')

                Text(
                    text = whole,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF141F14),
                    modifier = Modifier.padding(end = 4.dp)
                )
                VerticalFraction(
                    numerator = num,
                    denominator = den,
                    numOffset = 0,
                    denOffset = 0,
                    cursorIndex = -1,
                    isEvaluated = true,
                    cursorAlpha = 0f,
                    fontSize = 16.sp,
                    textColor = Color(0xFF141F14),
                    lineColor = Color(0xFF141F14)
                )
            } else {
                // Simple fraction: e.g. "5/3" or "-5/3"
                val isNegative = result.startsWith('-')
                val clean = if (isNegative) result.substring(1).trim() else result
                val num = clean.substringBefore('/')
                val den = clean.substringAfter('/')

                if (isNegative) {
                    Text(
                        text = "−",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF141F14),
                        modifier = Modifier.padding(end = 3.dp)
                    )
                }
                VerticalFraction(
                    numerator = num,
                    denominator = den,
                    numOffset = 0,
                    denOffset = 0,
                    cursorIndex = -1,
                    isEvaluated = true,
                    cursorAlpha = 0f,
                    fontSize = 17.sp,
                    textColor = Color(0xFF141F14),
                    lineColor = Color(0xFF141F14)
                )
            }
        }
    } else if (result.contains('√')) {
        // Result contains radical symbol
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            MathSegmentView(
                text = result,
                startOffset = 0,
                cursorIndex = -1,
                isEvaluated = true,
                cursorAlpha = 0f,
                fontSize = 22.sp,
                textColor = Color(0xFF141F14)
            )
        }
    } else {
        // Standard large LCD font for integers, decimals, scientific notation
        val formattedAnnotated = formatAnnotatedMathText(
            result,
            fontSize = if (result.length > 14) 20.sp else 24.sp,
            color = Color(0xFF141F14)
        )
        Text(
            text = formattedAnnotated,
            fontSize = if (result.length > 14) 20.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF141F14),
            textAlign = TextAlign.End,
            letterSpacing = 1.sp,
            maxLines = 1,
            modifier = modifier
        )
    }
}
