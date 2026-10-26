package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.LcdScreen
import com.example.ui.components.SetupMenuDialog
import com.example.ui.components.SolarPanel

/**
 * 100% replica of the CASIO fx-991EX CLASSWIZ scientific calculator.
 */
@Composable
fun ClassWizScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0D0E11),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0D0E11)),
            contentAlignment = Alignment.TopCenter
        ) {
            // Calculator body container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 480.dp)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .shadow(12.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF23262D),
                                Color(0xFF1B1D22),
                                Color(0xFF131518),
                                Color(0xFF0F1113)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF454B57), Color(0xFF1E2127))
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ================= TOP BRANDING & SOLAR PANEL =================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Brand: CASIO (Blue), fx-991EX (silver), CLASSWIZ (pink)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        // Blue CASIO Logo
                        Text(
                            text = "CASIO",
                            color = Color(0xFF1976D2), // Iconic Casio Royal Blue
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.5.sp
                        )

                        // fx-991EX CLASSWIZ
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "fx-991EX",
                                color = Color(0xFFE0E3E8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "CLASSWIZ",
                                color = Color(0xFFFF2D75), // Vibrant signature pink
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Subtitle
                        Text(
                            text = "Natural-V.P.A.M.",
                            color = Color(0xFF9EA3AD),
                            fontSize = 7.5.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Top Right Solar Panel
                    SolarPanel()
                }

                // ================= GREENISH LCD SCREEN =================
                LcdScreen(
                    state = state,
                    onSelectMenuItem = { viewModel.selectLcdMenuItem(it) },
                    onSelectMatrix = { viewModel.selectMatrix(it) },
                    onSetMatrixDimensions = { r, c -> viewModel.setMatrixDimensions(r, c) },
                    onSelectMatrixCell = { r, c -> viewModel.selectMatrixCell(r, c) },
                    onMatrixOp = { op ->
                        when (op) {
                            "Det" -> viewModel.computeMatrixDet()
                            "Trn" -> viewModel.computeMatrixTranspose()
                            "Inv" -> viewModel.computeMatrixInverse()
                            "A+B" -> viewModel.computeMatrixAdd()
                            "A×B" -> viewModel.computeMatrixMul()
                        }
                    },
                    onToggleMatrixOptn = { viewModel.toggleMatrixOptn(it) },
                    onDefineMatrix = { viewModel.defineMatrix(it) },
                    onSetMatrixDimSelection = { viewModel.setMatrixDimSelection(it) },
                    onConfirmMatrixRows = { viewModel.confirmMatrixRows() },
                    onConfirmMatrixCols = { viewModel.confirmMatrixCols() },
                    onSetMatrixStage = { viewModel.setMatrixStage(it) },
                    onSetMatrixCalcExpr = { viewModel.setMatrixCalcExpr(it); viewModel.runMatrixCalc() },
                    onRunMatrixCalc = { viewModel.runMatrixCalc() },
                    onSelectVector = { viewModel.selectVector(it) },
                    onDefineVector = { viewModel.defineVector(it) },
                    onSetVectorDimSelection = { viewModel.setVectorDimSelection(it) },
                    onConfirmVectorDim = { viewModel.confirmVectorDim() },
                    onSelectVectorComponent = { viewModel.selectVectorComponent(it) },
                    onSetVectorStage = { viewModel.setVectorStage(it) },
                    onSetVectorCalcExpr = { viewModel.setVectorCalcExpr(it); viewModel.runVectorCalc() },
                    onRunVectorCalc = { viewModel.runVectorCalc() },
                    onToggleVectorOptn = { viewModel.toggleVectorOptn(it) },
                    onSelectEqnCoeff = { idx -> viewModel.selectEqnCoeff(idx) },
                    onSelectOptnSubmenu = { viewModel.setCalcOptnSubmenu(it) },
                    onInsertOptnSnippet = { viewModel.insertSnippetAndCloseOptn(it) },
                    onCloseOptn = { viewModel.toggleOptn(false) },
                    modifier = Modifier.fillMaxWidth()
                )

                // ================= KEYPAD (ALL 9 ROWS) =================
                CalculatorKeypad(
                    state = state,
                    onInput = { viewModel.onCharInput(it) },
                    onAction = { viewModel.onAction(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
            }

            // 12-Mode Menu dialog (MENU button)
            if (state.showModeMenuDialog) {
                com.example.ui.components.ModeMenuDialog(
                    currentMode = state.currentMode,
                    onSelectMode = { viewModel.selectMode(it) },
                    onDismiss = { viewModel.toggleModeMenu(false) }
                )
            }

            // Setup menu dialog (SHIFT + MENU / SETUP)
            if (state.showSetupDialog) {
                SetupMenuDialog(
                    state = state,
                    onDismiss = { viewModel.toggleSetup(false) },
                    onAction = { viewModel.onAction(it) }
                )
            }

            // QR Code dialog (SHIFT + OPTN / QR)
            if (state.showQrDialog) {
                com.example.ui.components.QrDialog(
                    formula = state.formula,
                    result = state.resultDisplay,
                    onDismiss = { viewModel.toggleQr(false) }
                )
            }

            // Matrix Calculator dialog (Mode 4)
            if (state.showMatrixDialog) {
                com.example.ui.components.MatrixDialog(
                    onInsertResult = { viewModel.onCharInput(it) },
                    onDismiss = { viewModel.toggleMatrixDialog(false) }
                )
            }

            // Vector Calculator dialog (Mode 5)
            if (state.showVectorDialog) {
                com.example.ui.components.VectorDialog(
                    onDismiss = { viewModel.toggleVectorDialog(false) }
                )
            }

            // Equation Solver dialog (Mode 10)
            if (state.showEquationDialog) {
                com.example.ui.components.EquationDialog(
                    onDismiss = { viewModel.toggleEquationDialog(false) }
                )
            }

            // Statistics dialog (Mode 6)
            if (state.showStatisticsDialog) {
                com.example.ui.components.StatisticsDialog(
                    onDismiss = { viewModel.toggleStatisticsDialog(false) }
                )
            }

            // Table Generator dialog (Mode 9)
            if (state.showTableDialog) {
                com.example.ui.components.TableDialog(
                    onDismiss = { viewModel.toggleTableDialog(false) }
                )
            }

            // Distribution dialog (Mode 7)
            if (state.showDistributionDialog) {
                com.example.ui.components.DistributionDialog(
                    onDismiss = { viewModel.toggleDistributionDialog(false) }
                )
            }

            // Ratio Solver dialog (Mode 12)
            if (state.showRatioDialog) {
                com.example.ui.components.RatioDialog(
                    onDismiss = { viewModel.toggleRatioDialog(false) }
                )
            }

            // Scientific Constants dialog (SHIFT + 7 / CONST)
            if (state.showConstDialog) {
                com.example.ui.components.ConstantsDialog(
                    onSelectConstant = { viewModel.insertConstant(it) },
                    onDismiss = { viewModel.toggleConstDialog(false) }
                )
            }

            // Unit Converter popup overlay (SHIFT + 8 / CONV)
            if (state.showConvDialog) {
                val initialVal = if (state.numericResult != null && state.numericResult != 0.0) {
                    com.example.engine.MathEvaluator.formatDecimal(state.numericResult!!)
                } else if (state.formula.toDoubleOrNull() != null) {
                    state.formula
                } else {
                    "1"
                }
                com.example.ui.components.UnitConverterDialog(
                    initialValue = initialVal,
                    onInsertResult = { viewModel.insertConversionResult(it) },
                    onDismiss = { viewModel.toggleConvDialog(false) }
                )
            }
        }
    }
}
