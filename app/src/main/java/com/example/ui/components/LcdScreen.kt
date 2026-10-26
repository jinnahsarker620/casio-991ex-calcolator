package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AngleUnit
import com.example.model.BaseMode
import com.example.model.CalculatorState

/**
 * Authentic Greenish LCD Dot-Matrix Display for CASIO fx-991EX CLASSWIZ.
 */
@Composable
fun LcdScreen(
    state: CalculatorState,
    onSelectMenuItem: ((Int) -> Unit)? = null,
    onSelectMatrix: ((String) -> Unit)? = null,
    onSetMatrixDimensions: ((Int, Int) -> Unit)? = null,
    onSelectMatrixCell: ((Int, Int) -> Unit)? = null,
    onMatrixOp: ((String) -> Unit)? = null,
    onToggleMatrixOptn: ((Boolean) -> Unit)? = null,
    onDefineMatrix: ((String) -> Unit)? = null,
    onSetMatrixDimSelection: ((Int) -> Unit)? = null,
    onConfirmMatrixRows: (() -> Unit)? = null,
    onConfirmMatrixCols: (() -> Unit)? = null,
    onSetMatrixStage: ((com.example.model.MatrixStage) -> Unit)? = null,
    onSetMatrixCalcExpr: ((String) -> Unit)? = null,
    onRunMatrixCalc: (() -> Unit)? = null,
    onSelectVector: ((String) -> Unit)? = null,
    onDefineVector: ((String) -> Unit)? = null,
    onSetVectorDimSelection: ((Int) -> Unit)? = null,
    onConfirmVectorDim: (() -> Unit)? = null,
    onSelectVectorComponent: ((Int) -> Unit)? = null,
    onSetVectorStage: ((com.example.model.VectorStage) -> Unit)? = null,
    onSetVectorCalcExpr: ((String) -> Unit)? = null,
    onRunVectorCalc: (() -> Unit)? = null,
    onToggleVectorOptn: ((Boolean) -> Unit)? = null,
    onSelectEqnCoeff: ((Int) -> Unit)? = null,
    onSelectOptnSubmenu: ((String) -> Unit)? = null,
    onInsertOptnSnippet: ((String) -> Unit)? = null,
    onCloseOptn: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Blinking cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    // Automatically scroll to cursor position when formula changes
    LaunchedEffect(state.formula, state.cursorIndex) {
        if (state.cursorIndex > 10) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    // Outer dark bezel with metallic bevel
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E2124),
                        Color(0xFF141618),
                        Color(0xFF0F1012)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF42474E), Color(0xFF222428))
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(6.dp)
    ) {
        // Inner LCD screen (Greenish dot-matrix tone)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(138.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF9BB68F))
                .border(1.dp, Color(0xFF7E9B72), RoundedCornerShape(4.dp))
                .drawBehind {
                    // Subtle LCD glass reflection line at the top
                    drawLine(
                        color = Color(0x33FFFFFF),
                        start = Offset(4f, 2f),
                        end = Offset(size.width - 4f, 2f),
                        strokeWidth = 2f
                    )
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            when (state.lcdMode) {
                com.example.model.LcdMode.MENU -> {
                    LcdMenuDisplay(
                        selectedMenuItem = state.selectedMenuItem,
                        onSelectMenuItem = onSelectMenuItem
                    )
                }
                com.example.model.LcdMode.MATRIX -> {
                    val currentMatrixData = state.matrixStore[state.currentMatrixName] ?: state.matrixA
                    LcdMatrixDisplay(
                        showOptn = state.showMatrixOptn,
                        currentMatrixName = state.currentMatrixName,
                        matrix = currentMatrixData,
                        rows = state.matrixRows,
                        cols = state.matrixCols,
                        activeRow = state.matrixActiveRow,
                        activeCol = state.matrixActiveCol,
                        detDisplay = state.matrixDetDisplay,
                        matrixStage = state.matrixStage,
                        matrixDimSelection = state.matrixDimSelection,
                        matrixCalcExpr = state.matrixCalcExpr,
                        matrixCalcResult = state.matrixCalcResult,
                        matrixStore = state.matrixStore,
                        onSelectMatrix = onSelectMatrix,
                        onSetDimensions = onSetMatrixDimensions,
                        onSelectCell = onSelectMatrixCell,
                        onMatrixOp = onMatrixOp,
                        onToggleOptn = onToggleMatrixOptn,
                        onDefineMatrix = onDefineMatrix,
                        onSetMatrixDimSelection = onSetMatrixDimSelection,
                        onConfirmMatrixRows = onConfirmMatrixRows,
                        onConfirmMatrixCols = onConfirmMatrixCols,
                        onSetMatrixStage = onSetMatrixStage,
                        onSetMatrixCalcExpr = onSetMatrixCalcExpr,
                        onRunMatrixCalc = onRunMatrixCalc
                    )
                }
                com.example.model.LcdMode.VECTOR -> {
                    val currentVecData = state.vectorStore[state.currentVectorName] ?: listOf("0", "0", "0")
                    LcdVectorDisplay(
                        showOptn = state.showVectorOptn,
                        currentVectorName = state.currentVectorName,
                        vector = currentVecData,
                        dim = state.vectorDim,
                        activeIndex = state.vectorActiveIndex,
                        vectorStage = state.vectorStage,
                        vectorDimSelection = state.vectorDimSelection,
                        vectorCalcExpr = state.vectorCalcExpr,
                        vectorCalcResult = state.vectorCalcResult,
                        vectorStore = state.vectorStore,
                        onSelectVector = onSelectVector,
                        onDefineVector = onDefineVector,
                        onSetDimSelection = onSetVectorDimSelection,
                        onConfirmDim = onConfirmVectorDim,
                        onSelectComponent = onSelectVectorComponent,
                        onSetVectorStage = onSetVectorStage,
                        onSetVectorCalcExpr = onSetVectorCalcExpr,
                        onRunVectorCalc = onRunVectorCalc,
                        onToggleOptn = onToggleVectorOptn
                    )
                }
                com.example.model.LcdMode.EQUATION -> {
                    if (state.showCalcOptn) {
                        LcdCalcOptnScreen(
                            currentMode = 10,
                            submenu = state.calcOptnSubmenu,
                            onSelectSubmenu = { sub -> onSelectOptnSubmenu?.invoke(sub) },
                            onInsertSnippet = { snippet -> onInsertOptnSnippet?.invoke(snippet) },
                            onClose = { onCloseOptn?.invoke() }
                        )
                    } else {
                        LcdEquationDisplay(
                            coeffs = state.eqnCoeffs,
                            activeIndex = state.eqnActiveIndex,
                            resultDisplay = state.eqnResultDisplay,
                            onSelectCoeff = onSelectEqnCoeff
                        )
                    }
                }
                com.example.model.LcdMode.CALC -> {
                    if (state.showCalcOptn) {
                        LcdCalcOptnScreen(
                            currentMode = state.currentMode,
                            submenu = state.calcOptnSubmenu,
                            onSelectSubmenu = { sub -> onSelectOptnSubmenu?.invoke(sub) },
                            onInsertSnippet = { snippet -> onInsertOptnSnippet?.invoke(snippet) },
                            onClose = { onCloseOptn?.invoke() }
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                        // Top status bar indicators
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // [S] Shift badge (Prominent yellow)
                                StatusBadge(
                                    text = "S",
                                    isActive = state.isShiftActive,
                                    activeBg = Color(0xFFFFCC00),
                                    activeFg = Color.Black
                                )

                                // [A] Alpha badge (Prominent pink)
                                StatusBadge(
                                    text = "A",
                                    isActive = state.isAlphaActive,
                                    activeBg = Color(0xFFFF2D75),
                                    activeFg = Color.White
                                )

                                // [M] Memory badge
                                val hasMemory = (state.variables["M"] ?: 0.0) != 0.0
                                StatusBadge(
                                    text = "M",
                                    isActive = hasMemory,
                                    activeBg = Color(0xFF243423),
                                    activeFg = Color(0xFFA6BEA0)
                                )

                                // Angle Unit [D] / [R] / [G]
                                val angleText = when (state.angleUnit) {
                                    AngleUnit.DEG -> "D"
                                    AngleUnit.RAD -> "R"
                                    AngleUnit.GRAD -> "G"
                                }
                                StatusBadge(
                                    text = angleText,
                                    isActive = true,
                                    activeBg = Color(0xFF243423),
                                    activeFg = Color(0xFFA6BEA0)
                                )

                                // Base Mode (DEC/HEX/BIN/OCT) if active
                                if (state.baseMode != BaseMode.COMP) {
                                    StatusBadge(
                                        text = state.baseMode.symbol,
                                        isActive = true,
                                        activeBg = Color(0xFF0277BD),
                                        activeFg = Color.White
                                    )
                                }

                                // Natural Math display badge
                                StatusBadge(
                                    text = "MATH",
                                    isActive = true,
                                    activeBg = Color.Transparent,
                                    activeFg = Color(0xFF223221)
                                )
                            }

                            // Replay history and status indicators on the right
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (state.statusMessage.isNotEmpty()) {
                                    Text(
                                        text = state.statusMessage,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8B2236),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                if (state.history.isNotEmpty()) {
                                    Text(
                                        text = "▲▼",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF223221)
                                    )
                                }
                            }
                        }

                        // Middle: Formula row with Natural Textbook Display (vertical fraction, powers without ^, etc.)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(vertical = 1.dp)
                                .horizontalScroll(scrollState),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            NaturalFormulaRow(
                                formula = state.formula,
                                cursorIndex = state.cursorIndex,
                                isEvaluated = state.isEvaluated,
                                cursorAlpha = cursorAlpha
                            )
                        }

                        // Bottom: Result row (Right aligned, natural vertical fraction or large LCD font)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 2.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            NaturalResultRow(
                                result = if (state.isError) state.errorMessage else state.resultDisplay,
                                isError = state.isError
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun LcdMenuDisplay(
    selectedMenuItem: Int = 4,
    onSelectMenuItem: ((Int) -> Unit)?
) {
    val modeName = when (selectedMenuItem) {
        1 -> "1: Calculate"
        2 -> "2: Complex"
        3 -> "3: Base-N"
        4 -> "4: Matrix"
        5 -> "5: Vector"
        6 -> "6: Statistics"
        7 -> "7: Distribution"
        8 -> "8: Spreadsheet"
        9 -> "9: Table"
        10 -> "A: Equation/Func"
        11 -> "B: Inequality"
        12 -> "C: Ratio"
        else -> "1: Calculate"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header with solid underline border matching Casio style
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MAIN MENU",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF142012)
                )
                Text(
                    text = "▶ $modeName",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF142012)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(Color(0xFF142012))
            )
        }

        // 4 Rows of 3 items (Official Casio fx-991EX ClassWiz 12 application modes)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.5.dp)
        ) {
            // Row 1: 1:Calculate, 2:Complex, 3:Base-N
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LcdMenuItem(text = "1:Calculate", isSelected = (selectedMenuItem == 1), onClick = { onSelectMenuItem?.invoke(1) })
                LcdMenuItem(text = "2:Complex", isSelected = (selectedMenuItem == 2), onClick = { onSelectMenuItem?.invoke(2) })
                LcdMenuItem(text = "3:Base-N", isSelected = (selectedMenuItem == 3), onClick = { onSelectMenuItem?.invoke(3) })
            }
            // Row 2: 4:Matrix, 5:Vector, 6:Statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LcdMenuItem(text = "4:Matrix", isSelected = (selectedMenuItem == 4), onClick = { onSelectMenuItem?.invoke(4) })
                LcdMenuItem(text = "5:Vector", isSelected = (selectedMenuItem == 5), onClick = { onSelectMenuItem?.invoke(5) })
                LcdMenuItem(text = "6:Statistics", isSelected = (selectedMenuItem == 6), onClick = { onSelectMenuItem?.invoke(6) })
            }
            // Row 3: 7:Distribution, 8:Spreadsheet, 9:Table
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LcdMenuItem(text = "7:Distribution", isSelected = (selectedMenuItem == 7), onClick = { onSelectMenuItem?.invoke(7) })
                LcdMenuItem(text = "8:Spreadsheet", isSelected = (selectedMenuItem == 8), onClick = { onSelectMenuItem?.invoke(8) })
                LcdMenuItem(text = "9:Table", isSelected = (selectedMenuItem == 9), onClick = { onSelectMenuItem?.invoke(9) })
            }
            // Row 4: A:Equation/Func, B:Inequality, C:Ratio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LcdMenuItem(text = "A:Equation", isSelected = (selectedMenuItem == 10), onClick = { onSelectMenuItem?.invoke(10) })
                LcdMenuItem(text = "B:Inequality", isSelected = (selectedMenuItem == 11), onClick = { onSelectMenuItem?.invoke(11) })
                LcdMenuItem(text = "C:Ratio", isSelected = (selectedMenuItem == 12), onClick = { onSelectMenuItem?.invoke(12) })
            }
        }

        // Footer hint
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[1-9, A-C] [=]Select",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = "[AC] Exit",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630)
            )
        }
    }
}

@Composable
private fun LcdMenuItem(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (isSelected) Color(0xFF142012) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 3.dp, vertical = 1.dp)
    ) {
        Text(
            text = text,
            fontSize = 8.8.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = if (isSelected) Color(0xFF9BB68F) else Color(0xFF142012)
        )
    }
}

@Composable
private fun LcdMatrixDisplay(
    showOptn: Boolean,
    currentMatrixName: String,
    matrix: List<List<String>>,
    rows: Int,
    cols: Int,
    activeRow: Int,
    activeCol: Int,
    detDisplay: String,
    matrixStage: com.example.model.MatrixStage,
    matrixDimSelection: Int,
    matrixCalcExpr: String,
    matrixCalcResult: String,
    matrixStore: Map<String, List<List<String>>>,
    onSelectMatrix: ((String) -> Unit)?,
    onSetDimensions: ((Int, Int) -> Unit)?,
    onSelectCell: ((Int, Int) -> Unit)?,
    onMatrixOp: ((String) -> Unit)?,
    onToggleOptn: ((Boolean) -> Unit)?,
    onDefineMatrix: ((String) -> Unit)?,
    onSetMatrixDimSelection: ((Int) -> Unit)?,
    onConfirmMatrixRows: (() -> Unit)?,
    onConfirmMatrixCols: (() -> Unit)?,
    onSetMatrixStage: ((com.example.model.MatrixStage) -> Unit)?,
    onSetMatrixCalcExpr: ((String) -> Unit)?,
    onRunMatrixCalc: (() -> Unit)?
) {
    if (showOptn) {
        LcdMatrixOptnScreen(
            currentMatrixName = currentMatrixName,
            rows = rows,
            cols = cols,
            onSetDimensions = onSetDimensions,
            onSelectMatrix = onSelectMatrix,
            onDefineMatrix = {
                onDefineMatrix?.invoke(currentMatrixName)
                onToggleOptn?.invoke(false)
            },
            onSelectOp = { expr ->
                onSetMatrixCalcExpr?.invoke(expr)
                onRunMatrixCalc?.invoke()
                onToggleOptn?.invoke(false)
            },
            onCloseOptn = { onToggleOptn?.invoke(false) }
        )
    } else {
        when (matrixStage) {
            com.example.model.MatrixStage.MDEF -> {
                LcdMatrixDefScreen(
                    onSelectMatrix = { name ->
                        onDefineMatrix?.invoke(name)
                    },
                    onExit = { onToggleOptn?.invoke(false) }
                )
            }
            com.example.model.MatrixStage.ROW -> {
                LcdMatrixDimPickerScreen(
                    title = "Mat${currentMatrixName.removePrefix("Mat")}",
                    prompt = "Number of Rows?",
                    selectedDim = matrixDimSelection,
                    onSelectDim = onSetMatrixDimSelection,
                    onConfirm = onConfirmMatrixRows,
                    onBack = { onSetMatrixStage?.invoke(com.example.model.MatrixStage.MDEF) }
                )
            }
            com.example.model.MatrixStage.COL -> {
                LcdMatrixDimPickerScreen(
                    title = "Mat${currentMatrixName.removePrefix("Mat")} ${rows}x",
                    prompt = "Number of Columns?",
                    selectedDim = matrixDimSelection,
                    onSelectDim = onSetMatrixDimSelection,
                    onConfirm = onConfirmMatrixCols,
                    onBack = { onSetMatrixStage?.invoke(com.example.model.MatrixStage.ROW) }
                )
            }
            com.example.model.MatrixStage.INP -> {
                MatrixGrid(
                    currentMatrixName = currentMatrixName,
                    matrix = matrix,
                    rows = rows,
                    cols = cols,
                    activeRow = activeRow,
                    activeCol = activeCol,
                    detDisplay = detDisplay,
                    onSelectMatrix = onSelectMatrix,
                    onSelectCell = onSelectCell,
                    onMatrixOp = onMatrixOp,
                    onOpenOptn = { onToggleOptn?.invoke(true) }
                )
            }
            com.example.model.MatrixStage.CALC -> {
                LcdMatrixCalcScreen(
                    matrixStore = matrixStore,
                    expr = matrixCalcExpr,
                    result = matrixCalcResult.ifEmpty { detDisplay },
                    onOpenOptn = { onToggleOptn?.invoke(true) },
                    onRunCalc = onRunMatrixCalc,
                    onBackToInput = { onSetMatrixStage?.invoke(com.example.model.MatrixStage.INP) }
                )
            }
        }
    }
}

@Composable
private fun LcdMatrixDefScreen(
    onSelectMatrix: (String) -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Define Matrix",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF142012)
                )
                Text(
                    text = "fx-991EX",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF334630)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(Color(0xFF142012))
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("1:MatA", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectMatrix("MatA") })
                Text("2:MatB", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectMatrix("MatB") })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("3:MatC", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectMatrix("MatC") })
                Text("4:MatD", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectMatrix("MatD") })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(">1 for MatA (3x3)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
            Text("[AC] Exit", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF334630), modifier = Modifier.clickable { onExit() })
        }
    }
}

@Composable
private fun LcdMatrixDimPickerScreen(
    title: String,
    prompt: String,
    selectedDim: Int,
    onSelectDim: ((Int) -> Unit)?,
    onConfirm: (() -> Unit)?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = prompt,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            (1..4).forEach { i ->
                val isSel = (i == selectedDim)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSel) Color(0xFF142012) else Color.Transparent)
                        .clickable { onSelectDim?.invoke(i) }
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSel) "▶ $i. $i [■]" else "  $i. $i",
                        fontSize = 9.5.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSel) Color(0xFF9BB68F) else Color(0xFF142012)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[=]=SELECT",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012),
                modifier = Modifier.clickable { onConfirm?.invoke() }
            )
            Text(
                text = "[AC]=Back",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630),
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}

@Composable
private fun LcdMatrixCalcScreen(
    matrixStore: Map<String, List<List<String>>>,
    expr: String,
    result: String,
    onOpenOptn: () -> Unit,
    onRunCalc: (() -> Unit)?,
    onBackToInput: () -> Unit
) {
    val mA = matrixStore["MatA"]
    val mB = matrixStore["MatB"]
    val strA = mA?.let { "${it.size}x${it.firstOrNull()?.size ?: 0}" } ?: "-"
    val strB = mB?.let { "${it.size}x${it.firstOrNull()?.size ?: 0}" } ?: "-"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MATRIX CALC",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = "MatA=$strA MatB=$strB",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 1.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Expr: ${expr.ifEmpty { "..." }}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            if (result.isNotEmpty()) {
                Text(
                    text = result,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF142012)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF223221))
                        .clickable { onOpenOptn() }
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text("OPTN=Menu", fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFFFFCC00))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x33FFFFFF))
                        .clickable { onRunCalc?.invoke() }
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text("CALC=Run", fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
                }
            }
            Text(
                text = "[AC] Clear",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630),
                modifier = Modifier.clickable { onBackToInput() }
            )
        }
    }
}

@Composable
private fun LcdMatrixOptnScreen(
    currentMatrixName: String,
    rows: Int,
    cols: Int,
    onSetDimensions: ((Int, Int) -> Unit)?,
    onSelectMatrix: ((String) -> Unit)?,
    onDefineMatrix: (() -> Unit)? = null,
    onSelectOp: ((String) -> Unit)? = null,
    onCloseOptn: () -> Unit
) {
    var rowMenuExpanded by remember { mutableStateOf(false) }
    var colMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OPTN - MATRIX",
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = "EXIT=Back",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630),
                modifier = Modifier.clickable { onCloseOptn() }
            )
        }

        // Line 1: Define Matrix
        Text(
            text = "1:Define Matrix [Open]",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF142012),
            modifier = Modifier.clickable { onDefineMatrix?.invoke() }
        )

        // Line 2: Matrix Name & Dimension Pickers
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "2:$currentMatrixName",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )

            // Row dropdown box
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, Color(0xFF7E9B72), RoundedCornerShape(2.dp))
                        .clickable { rowMenuExpanded = true }
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "Row: $rows ▼",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF142012)
                    )
                }
                DropdownMenu(
                    expanded = rowMenuExpanded,
                    onDismissRequest = { rowMenuExpanded = false }
                ) {
                    (1..4).forEach { r ->
                        DropdownMenuItem(
                            text = { Text("$r", fontSize = 12.sp, fontFamily = FontFamily.Monospace) },
                            onClick = {
                                onSetDimensions?.invoke(r, cols)
                                rowMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Col dropdown box
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, Color(0xFF7E9B72), RoundedCornerShape(2.dp))
                        .clickable { colMenuExpanded = true }
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "Col: $cols ▼",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF142012)
                    )
                }
                DropdownMenu(
                    expanded = colMenuExpanded,
                    onDismissRequest = { colMenuExpanded = false }
                ) {
                    (1..4).forEach { c ->
                        DropdownMenuItem(
                            text = { Text("$c", fontSize = 12.sp, fontFamily = FontFamily.Monospace) },
                            onClick = {
                                onSetDimensions?.invoke(rows, c)
                                colMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "[${rows}x${cols}■]",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
        }

        // Line 3: MatB, Det, Trn
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("3:MatB", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectMatrix?.invoke("MatB") })
            Text("4:Det(MatA)", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp?.invoke("Det(MatA)") })
            Text("5:Trn(MatA)", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp?.invoke("Trn(MatA)") })
        }

        // Line 4: Operations: A+B, A-B, A*B, 7*A
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("6:A+B", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp?.invoke("MatA+MatB") })
            Text("7:A-B", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp?.invoke("MatA-MatB") })
            Text("8:A×B", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp?.invoke("MatA*MatB") })
            Text("9:7×A", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp?.invoke("7*MatA") })
        }

        // Line 5: Select=OK  EXIT=Back
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select=OK",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012),
                modifier = Modifier.clickable { onCloseOptn() }
            )
            Text(
                text = "EXIT=Back",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630),
                modifier = Modifier.clickable { onCloseOptn() }
            )
        }
    }
}

@Composable
private fun LcdVectorDisplay(
    showOptn: Boolean,
    currentVectorName: String,
    vector: List<String>,
    dim: Int,
    activeIndex: Int,
    vectorStage: com.example.model.VectorStage,
    vectorDimSelection: Int,
    vectorCalcExpr: String,
    vectorCalcResult: String,
    vectorStore: Map<String, List<String>>,
    onSelectVector: ((String) -> Unit)?,
    onDefineVector: ((String) -> Unit)?,
    onSetDimSelection: ((Int) -> Unit)?,
    onConfirmDim: (() -> Unit)?,
    onSelectComponent: ((Int) -> Unit)?,
    onSetVectorStage: ((com.example.model.VectorStage) -> Unit)?,
    onSetVectorCalcExpr: ((String) -> Unit)?,
    onRunVectorCalc: (() -> Unit)?,
    onToggleOptn: ((Boolean) -> Unit)?
) {
    if (showOptn) {
        LcdVectorOptnScreen(
            currentVectorName = currentVectorName,
            onDefineVector = {
                onDefineVector?.invoke(currentVectorName)
                onToggleOptn?.invoke(false)
            },
            onSelectVector = { name ->
                onSelectVector?.invoke(name)
                onToggleOptn?.invoke(false)
            },
            onSelectOp = { expr ->
                onSetVectorCalcExpr?.invoke(expr)
                onRunVectorCalc?.invoke()
                onToggleOptn?.invoke(false)
            },
            onCloseOptn = { onToggleOptn?.invoke(false) }
        )
    } else {
        when (vectorStage) {
            com.example.model.VectorStage.MDEF -> {
                LcdVectorDefScreen(
                    onSelectVector = { name ->
                        onDefineVector?.invoke(name)
                    },
                    onExit = { onToggleOptn?.invoke(false) }
                )
            }
            com.example.model.VectorStage.VDIM -> {
                LcdVectorDimPickerScreen(
                    vectorName = currentVectorName,
                    selectedDim = vectorDimSelection,
                    onSelectDim = onSetDimSelection,
                    onConfirm = onConfirmDim,
                    onBack = { onSetVectorStage?.invoke(com.example.model.VectorStage.MDEF) }
                )
            }
            com.example.model.VectorStage.INP -> {
                LcdVectorInpScreen(
                    vectorName = currentVectorName,
                    vector = vector,
                    dim = dim,
                    activeIndex = activeIndex,
                    onSelectComponent = onSelectComponent,
                    onOpenOptn = { onToggleOptn?.invoke(true) }
                )
            }
            com.example.model.VectorStage.CALC -> {
                LcdVectorCalcScreen(
                    vectorStore = vectorStore,
                    expr = vectorCalcExpr,
                    result = vectorCalcResult,
                    onOpenOptn = { onToggleOptn?.invoke(true) },
                    onRunCalc = onRunVectorCalc,
                    onBackToInput = { onSetVectorStage?.invoke(com.example.model.VectorStage.INP) }
                )
            }
        }
    }
}

@Composable
private fun LcdVectorDefScreen(
    onSelectVector: (String) -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Define Vector",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF142012)
                )
                Text(
                    text = "fx-991EX",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF334630)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(Color(0xFF142012))
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("1:VctA", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectVector("VctA") })
                Text("2:VctB", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectVector("VctB") })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("3:VctC", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectVector("VctC") })
                Text("4:VctD", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectVector("VctD") })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(">1 for VctA (3D)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
            Text("[AC] Exit", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF334630), modifier = Modifier.clickable { onExit() })
        }
    }
}

@Composable
private fun LcdVectorDimPickerScreen(
    vectorName: String,
    selectedDim: Int,
    onSelectDim: ((Int) -> Unit)?,
    onConfirm: (() -> Unit)?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = vectorName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = "Dimension (2~3)?",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            listOf(2, 3).forEach { d ->
                val isSel = (d == selectedDim)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSel) Color(0xFF142012) else Color.Transparent)
                        .clickable { onSelectDim?.invoke(d) }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSel) "▶ $d. $d [■]" else "  $d. $d",
                        fontSize = 10.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSel) Color(0xFF9BB68F) else Color(0xFF142012)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[=]=SELECT",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012),
                modifier = Modifier.clickable { onConfirm?.invoke() }
            )
            Text(
                text = "[AC]=Back",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630),
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}

@Composable
private fun LcdVectorInpScreen(
    vectorName: String,
    vector: List<String>,
    dim: Int,
    activeIndex: Int,
    onSelectComponent: ((Int) -> Unit)?,
    onOpenOptn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$vectorName ($dim-dim)",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF223221))
                    .clickable { onOpenOptn() }
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "OPTN",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFFCC00)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (i in 0 until dim) {
                val isSel = (i == activeIndex)
                val valStr = vector.getOrNull(i) ?: "0"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSel) Color(0xFF142012) else Color.Transparent)
                        .clickable { onSelectComponent?.invoke(i) }
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSel) "▶ x${i + 1} = [ $valStr ]" else "  x${i + 1} = $valStr",
                        fontSize = 9.5.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSel) Color(0xFF9BB68F) else Color(0xFF142012)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[=] Next/Done",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = "(-) Negate",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630)
            )
        }
    }
}

@Composable
private fun LcdVectorCalcScreen(
    vectorStore: Map<String, List<String>>,
    expr: String,
    result: String,
    onOpenOptn: () -> Unit,
    onRunCalc: (() -> Unit)?,
    onBackToInput: () -> Unit
) {
    val vA = vectorStore["VctA"]
    val vB = vectorStore["VctB"]
    val strA = vA?.let { "${it.size}D" } ?: "-"
    val strB = vB?.let { "${it.size}D" } ?: "-"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VECTOR CALC",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = "VctA=$strA VctB=$strB",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 1.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Expr: ${expr.ifEmpty { "..." }}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            if (result.isNotEmpty()) {
                Text(
                    text = result,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF142012)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF223221))
                        .clickable { onOpenOptn() }
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text("OPTN=Menu", fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFFFFCC00))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x33FFFFFF))
                        .clickable { onRunCalc?.invoke() }
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text("CALC=Run", fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
                }
            }
            Text(
                text = "[AC] Edit",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630),
                modifier = Modifier.clickable { onBackToInput() }
            )
        }
    }
}

@Composable
private fun LcdVectorOptnScreen(
    currentVectorName: String,
    onDefineVector: () -> Unit,
    onSelectVector: (String) -> Unit,
    onSelectOp: (String) -> Unit,
    onCloseOptn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OPTN - VECTOR",
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = "EXIT=Back",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630),
                modifier = Modifier.clickable { onCloseOptn() }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1:Define", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onDefineVector() })
                Text("2:Edit", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectVector(currentVectorName) })
                Text("3:VctA", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectVector("VctA") })
                Text("4:VctB", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectVector("VctB") })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("5:Dot", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp("VctA•VctB") })
                Text("6:Cross", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp("VctA×VctB") })
                Text("7:Angle", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp("Angle(VctA,VctB)") })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("8:UnitV", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp("UnitV(VctA)") })
                Text("9:Abs", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp("Abs(VctA)") })
                Text("+:Add", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012), modifier = Modifier.clickable { onSelectOp("VctA+VctB") })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Press 1-9 to select", fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
            Text("[AC] Cancel", fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF334630), modifier = Modifier.clickable { onCloseOptn() })
        }
    }
}

@Composable
private fun MatrixGrid(
    currentMatrixName: String,
    matrix: List<List<String>>,
    rows: Int,
    cols: Int,
    activeRow: Int,
    activeCol: Int,
    detDisplay: String,
    onSelectMatrix: ((String) -> Unit)?,
    onSelectCell: ((Int, Int) -> Unit)?,
    onMatrixOp: ((String) -> Unit)?,
    onOpenOptn: () -> Unit
) {
    val cellWidth = when (cols) {
        4 -> 28.dp
        3 -> 38.dp
        else -> 54.dp
    }
    val cellHeight = when (rows) {
        4 -> 17.dp
        3 -> 20.dp
        else -> 23.dp
    }
    val cellFontSize = when {
        cols >= 4 -> 8.5.sp
        cols == 3 -> 9.5.sp
        else -> 11.sp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 1.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Row 1: Matrix selector tabs, OPTN button & quick ops
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                listOf("MatA", "MatB", "MatC", "MatD").forEach { name ->
                    val isSelected = (name == currentMatrixName)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSelected) Color(0xFF223221) else Color(0x33FFFFFF))
                            .clickable { onSelectMatrix?.invoke(name) }
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 8.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) Color(0xFFA6BEA0) else Color(0xFF142012)
                        )
                    }
                }
            }

            // OPTN & quick operation chips
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF223221))
                        .clickable { onOpenOptn() }
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "OPTN",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFCC00)
                    )
                }

                listOf("Det", "Trn", "Inv").forEach { op ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0x22142012))
                            .clickable { onMatrixOp?.invoke(op) }
                            .padding(horizontal = 2.5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = op,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF142012)
                        )
                    }
                }
            }
        }

        // Row 2: Matrix with brackets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[",
                fontSize = if (rows >= 4) 24.sp else 28.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF142012),
                modifier = Modifier.padding(end = 2.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (r in 0 until rows) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (c in 0 until cols) {
                            val isSelected = (r == activeRow && c == activeCol)
                            val valText = matrix.getOrNull(r)?.getOrNull(c) ?: "0"
                            Box(
                                modifier = Modifier
                                    .width(cellWidth)
                                    .height(cellHeight)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isSelected) Color(0xFF223221) else Color(0x22FFFFFF))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF142012) else Color(0xFF869E81),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .clickable { onSelectCell?.invoke(r, c) }
                                    .padding(horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = valText,
                                    fontSize = cellFontSize,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected) Color(0xFFA6BEA0) else Color(0xFF142012),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = "]",
                fontSize = if (rows >= 4) 24.sp else 28.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF142012),
                modifier = Modifier.padding(start = 2.dp)
            )
        }

        // Bottom Result line (Det / Trn / Inv / A+B / A×B) & AC hint
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Row:${activeRow + 1} Col:${activeCol + 1} [=]Next [AC]Back",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630)
            )
            Text(
                text = detDisplay,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
        }
    }
}

@Composable
private fun LcdEquationDisplay(
    coeffs: List<String>,
    activeIndex: Int,
    resultDisplay: String,
    onSelectCoeff: ((Int) -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 1.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "anX + bnY = cn",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = "[=] Solve  [AC] Exit",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630)
            )
        }

        // Two equations with editable coefficient chips
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Eq 1: a1 X + b1 Y = c1 (indices 0, 1, 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text("{ ", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
                EqnCoeffChip(text = coeffs.getOrNull(0) ?: "1", isSelected = activeIndex == 0) { onSelectCoeff?.invoke(0) }
                Text("X+", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
                EqnCoeffChip(text = coeffs.getOrNull(1) ?: "1", isSelected = activeIndex == 1) { onSelectCoeff?.invoke(1) }
                Text("Y=", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
                EqnCoeffChip(text = coeffs.getOrNull(2) ?: "2", isSelected = activeIndex == 2) { onSelectCoeff?.invoke(2) }
            }

            // Eq 2: a2 X + b2 Y = c2 (indices 3, 4, 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text("{ ", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
                EqnCoeffChip(text = coeffs.getOrNull(3) ?: "1", isSelected = activeIndex == 3) { onSelectCoeff?.invoke(3) }
                Text("X+", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
                EqnCoeffChip(text = coeffs.getOrNull(4) ?: "-1", isSelected = activeIndex == 4) { onSelectCoeff?.invoke(4) }
                Text("Y=", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF142012))
                EqnCoeffChip(text = coeffs.getOrNull(5) ?: "0", isSelected = activeIndex == 5) { onSelectCoeff?.invoke(5) }
            }
        }

        // Result bottom row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = resultDisplay,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
        }
    }
}

@Composable
private fun EqnCoeffChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(42.dp)
            .height(22.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (isSelected) Color(0xFF223221) else Color(0x22FFFFFF))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Color(0xFF142012) else Color(0xFF869E81),
                shape = RoundedCornerShape(3.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (isSelected) Color(0xFFA6BEA0) else Color(0xFF142012),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun StatusBadge(
    text: String,
    isActive: Boolean,
    activeBg: Color,
    activeFg: Color
) {
    if (isActive) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(activeBg)
                .padding(horizontal = 3.dp, vertical = 0.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = activeFg
            )
        }
    } else {
        Text(
            text = text,
            fontSize = 8.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif,
            color = Color(0x33243423)
        )
    }
}

@Composable
private fun LcdCalcOptnScreen(
    currentMode: Int,
    submenu: String,
    onSelectSubmenu: (String) -> Unit,
    onInsertSnippet: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val title = when (currentMode) {
                2 -> "OPTN - COMPLEX"
                3 -> "OPTN - BASE-N"
                6 -> "OPTN - STATISTICS"
                9 -> "OPTN - TABLE"
                10 -> "OPTN - EQUATION"
                else -> when (submenu) {
                    "HYP" -> "OPTN - Hyperbolic"
                    "OTHER" -> "OPTN - Other Func"
                    "ANGLE" -> "OPTN - Angle Unit"
                    "ENG" -> "OPTN - Eng Symbol"
                    else -> "OPTN - CALCULATE"
                }
            }
            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF142012)
            )
            Text(
                text = if (submenu.isNotEmpty()) "[AC]:Back" else "[AC]:Exit",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF334630),
                modifier = Modifier.clickable { onClose() }
            )
        }

        // Content items based on mode & submenu
        when (currentMode) {
            2 -> { // Complex Mode
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LcdOptnItem(label = "1:i", modifier = Modifier.weight(1f)) { onInsertSnippet("i") }
                        LcdOptnItem(label = "2:Conjg", modifier = Modifier.weight(1f)) { onInsertSnippet("conjg(") }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LcdOptnItem(label = "3:Real(reP)", modifier = Modifier.weight(1f)) { onInsertSnippet("reP(") }
                        LcdOptnItem(label = "4:Imag(imP)", modifier = Modifier.weight(1f)) { onInsertSnippet("imP(") }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LcdOptnItem(label = "5:arg", modifier = Modifier.weight(1f)) { onInsertSnippet("arg(") }
                        LcdOptnItem(label = "6:▶r∠θ", modifier = Modifier.weight(1f)) { onInsertSnippet("▶r∠θ") }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LcdOptnItem(label = "7:▶a+bi", modifier = Modifier.weight(1f)) { onInsertSnippet("▶a+bi") }
                        LcdOptnItem(label = "8:∠", modifier = Modifier.weight(1f)) { onInsertSnippet("∠") }
                    }
                }
            }
            3 -> { // Base-N Mode
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LcdOptnItem(label = "1:and", modifier = Modifier.weight(1f)) { onInsertSnippet(" and ") }
                        LcdOptnItem(label = "2:or", modifier = Modifier.weight(1f)) { onInsertSnippet(" or ") }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LcdOptnItem(label = "3:xor", modifier = Modifier.weight(1f)) { onInsertSnippet(" xor ") }
                        LcdOptnItem(label = "4:xnor", modifier = Modifier.weight(1f)) { onInsertSnippet(" xnor ") }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LcdOptnItem(label = "5:Not", modifier = Modifier.weight(1f)) { onInsertSnippet("not(") }
                        LcdOptnItem(label = "6:Neg", modifier = Modifier.weight(1f)) { onInsertSnippet("Neg(") }
                    }
                }
            }
            6 -> { // Statistics Mode
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LcdOptnItem(label = "1:Select Type", modifier = Modifier.fillMaxWidth()) { onInsertSnippet("StatType") }
                    LcdOptnItem(label = "2:1-Variable Data", modifier = Modifier.fillMaxWidth()) { onInsertSnippet("1-Var") }
                    LcdOptnItem(label = "3:Summary (Σx)", modifier = Modifier.fillMaxWidth()) { onInsertSnippet("Σx") }
                }
            }
            9 -> { // Table Mode
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LcdOptnItem(label = "1:Table Range", modifier = Modifier.fillMaxWidth()) { onInsertSnippet("TableRange") }
                    LcdOptnItem(label = "2:Define g(x)", modifier = Modifier.fillMaxWidth()) { onInsertSnippet("g(x)=") }
                }
            }
            10 -> { // Equation Mode
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LcdOptnItem(label = "1:Simul Equation", modifier = Modifier.fillMaxWidth()) { onInsertSnippet("SIMUL") }
                    LcdOptnItem(label = "2:Polynomial", modifier = Modifier.fillMaxWidth()) { onInsertSnippet("POLY") }
                    LcdOptnItem(label = "3:Solve Equation", modifier = Modifier.fillMaxWidth()) { onInsertSnippet("SOLVE") }
                }
            }
            else -> { // Mode 1: Calculate
                when (submenu) {
                    "" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "1:Hyperbolic Func", modifier = Modifier.weight(1f)) { onSelectSubmenu("HYP") }
                                LcdOptnItem(label = "2:Other Func", modifier = Modifier.weight(1f)) { onSelectSubmenu("OTHER") }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "3:Angle Unit", modifier = Modifier.weight(1f)) { onSelectSubmenu("ANGLE") }
                                LcdOptnItem(label = "4:Engineer Symbol", modifier = Modifier.weight(1f)) { onSelectSubmenu("ENG") }
                            }
                        }
                    }
                    "HYP" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "1:sinh", modifier = Modifier.weight(1f)) { onInsertSnippet("sinh(") }
                                LcdOptnItem(label = "2:cosh", modifier = Modifier.weight(1f)) { onInsertSnippet("cosh(") }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "3:tanh", modifier = Modifier.weight(1f)) { onInsertSnippet("tanh(") }
                                LcdOptnItem(label = "4:sinh⁻¹", modifier = Modifier.weight(1f)) { onInsertSnippet("sinh⁻¹(") }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "5:cosh⁻¹", modifier = Modifier.weight(1f)) { onInsertSnippet("cosh⁻¹(") }
                                LcdOptnItem(label = "6:tanh⁻¹", modifier = Modifier.weight(1f)) { onInsertSnippet("tanh⁻¹(") }
                            }
                        }
                    }
                    "OTHER" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "1:Abs", modifier = Modifier.weight(1f)) { onInsertSnippet("abs(") }
                                LcdOptnItem(label = "2:Ran#", modifier = Modifier.weight(1f)) { onInsertSnippet("Ran#") }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "3:RanInt", modifier = Modifier.weight(1f)) { onInsertSnippet("RanInt#(1,6)") }
                                LcdOptnItem(label = "4:gcd", modifier = Modifier.weight(1f)) { onInsertSnippet("gcd(") }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "5:lcm", modifier = Modifier.weight(1f)) { onInsertSnippet("lcm(") }
                                LcdOptnItem(label = "6:Int", modifier = Modifier.weight(1f)) { onInsertSnippet("int(") }
                            }
                        }
                    }
                    "ANGLE" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "1:° (Degree)", modifier = Modifier.weight(1f)) { onInsertSnippet("°") }
                                LcdOptnItem(label = "2:r (Radian)", modifier = Modifier.weight(1f)) { onInsertSnippet("r") }
                            }
                            LcdOptnItem(label = "3:g (Gradian)", modifier = Modifier.fillMaxWidth(0.5f)) { onInsertSnippet("g") }
                        }
                    }
                    "ENG" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "1:m(10⁻³)", modifier = Modifier.weight(1f)) { onInsertSnippet("m") }
                                LcdOptnItem(label = "2:μ(10⁻⁶)", modifier = Modifier.weight(1f)) { onInsertSnippet("μ") }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "3:n(10⁻⁹)", modifier = Modifier.weight(1f)) { onInsertSnippet("n") }
                                LcdOptnItem(label = "4:p(10⁻¹²)", modifier = Modifier.weight(1f)) { onInsertSnippet("p") }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "5:k(10³)", modifier = Modifier.weight(1f)) { onInsertSnippet("k") }
                                LcdOptnItem(label = "6:M(10⁶)", modifier = Modifier.weight(1f)) { onInsertSnippet("M") }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                LcdOptnItem(label = "7:G(10⁹)", modifier = Modifier.weight(1f)) { onInsertSnippet("G") }
                                LcdOptnItem(label = "8:T(10¹²)", modifier = Modifier.weight(1f)) { onInsertSnippet("T") }
                            }
                        }
                    }
                }
            }
        }

        // Footer prompt
        Text(
            text = when (currentMode) {
                2, 3 -> "[1-${if (currentMode == 2) "8" else "6"}]=Select  [AC]=Exit"
                6 -> "[1-3]=Select  [AC]=Exit"
                9 -> "[1-2]=Select  [AC]=Exit"
                10 -> "[1-3]=Select  [AC]=Exit"
                else -> when (submenu) {
                    "" -> "[1-4]=Select  [AC]=Exit"
                    "HYP", "OTHER" -> "[1-6]=Select  [AC]=Back"
                    "ANGLE" -> "[1-3]=Select  [AC]=Back"
                    "ENG" -> "[1-8]=Select  [AC]=Back"
                    else -> "[1-4]=Select  [AC]=Exit"
                }
            },
            fontSize = 8.5.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF334630)
        )
    }
}

@Composable
private fun LcdOptnItem(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0x22FFFFFF))
            .border(1.dp, Color(0xFF7E9B72), RoundedCornerShape(3.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF142012)
        )
    }
}
