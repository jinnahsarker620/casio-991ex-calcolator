package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.engine.MathEvaluator
import com.example.model.AngleUnit
import com.example.model.BaseMode
import com.example.model.CalculatorState
import com.example.model.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {

    private val evaluator = MathEvaluator()

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onCharInput(text: String) {
        if (_state.value.showMatrixOptn) {
            when (text) {
                "1" -> { setMatrixStage(com.example.model.MatrixStage.MDEF); toggleMatrixOptn(false) }
                "2" -> { selectMatrix("MatA"); toggleMatrixOptn(false) }
                "3" -> { selectMatrix("MatB"); toggleMatrixOptn(false) }
                "4" -> { setMatrixCalcExpr("Det(MatA)"); toggleMatrixOptn(false); runMatrixCalc() }
                "5" -> { setMatrixCalcExpr("Trn(MatA)"); toggleMatrixOptn(false); runMatrixCalc() }
                "6" -> { setMatrixCalcExpr("Inv(MatA)"); toggleMatrixOptn(false); runMatrixCalc() }
                "7" -> { setMatrixCalcExpr("MatA+MatB"); toggleMatrixOptn(false); runMatrixCalc() }
                "8" -> { setMatrixCalcExpr("MatA-MatB"); toggleMatrixOptn(false); runMatrixCalc() }
                "9" -> { setMatrixCalcExpr("MatA*MatB"); toggleMatrixOptn(false); runMatrixCalc() }
            }
            return
        }

        if (_state.value.showCalcOptn) {
            handleCalcOptnInput(text)
            return
        }

        if (_state.value.isCalcPromptActive) {
            if (text.all { it.isDigit() || it == '.' || it == '-' }) {
                _state.update { it.copy(calcPromptInput = it.calcPromptInput + text) }
                return
            }
        }

        if (_state.value.lcdMode == com.example.model.LcdMode.MENU || _state.value.showModeMenuDialog) {
            val num = when (text.uppercase()) {
                "1" -> 1
                "2" -> 2
                "3" -> 3
                "4" -> 4
                "5" -> 5
                "6" -> 6
                "7" -> 7
                "8" -> 8
                "9" -> 9
                "A", "(-)", "-" -> 10
                "B", "°", "°'\"" -> 11
                "C", "X" -> 12
                else -> null
            }
            if (num != null) {
                selectLcdMenuItem(num)
                return
            }
        }

        if (_state.value.lcdMode == com.example.model.LcdMode.MATRIX) {
            when (_state.value.matrixStage) {
                com.example.model.MatrixStage.MDEF -> {
                    when (text) {
                        "1" -> defineMatrix("MatA")
                        "2" -> defineMatrix("MatB")
                        "3" -> defineMatrix("MatC")
                        "4" -> defineMatrix("MatD")
                    }
                    return
                }
                com.example.model.MatrixStage.ROW -> {
                    if (text in listOf("1", "2", "3", "4")) {
                        setMatrixDimSelection(text.toInt())
                        return
                    }
                }
                com.example.model.MatrixStage.COL -> {
                    if (text in listOf("1", "2", "3", "4")) {
                        setMatrixDimSelection(text.toInt())
                        return
                    }
                }
                com.example.model.MatrixStage.INP -> {
                    if (text == "-") {
                        negateMatrixCell()
                        return
                    }
                    if (text.all { it.isDigit() || it == '.' || it == '-' }) {
                        updateMatrixCell(_state.value.matrixActiveRow, _state.value.matrixActiveCol, text)
                        return
                    }
                }
                com.example.model.MatrixStage.CALC -> {
                    when (text) {
                        "+" -> { setMatrixCalcExpr("MatA+MatB"); runMatrixCalc(); return }
                        "-" -> { setMatrixCalcExpr("MatA-MatB"); runMatrixCalc(); return }
                        "×", "*" -> { setMatrixCalcExpr("MatA*MatB"); runMatrixCalc(); return }
                        "7" -> { setMatrixCalcExpr("7*MatA"); runMatrixCalc(); return }
                        "1" -> { setMatrixStage(com.example.model.MatrixStage.MDEF); return }
                        "2" -> { selectMatrix("MatA"); setMatrixStage(com.example.model.MatrixStage.INP); return }
                        "3" -> { selectMatrix("MatB"); setMatrixStage(com.example.model.MatrixStage.INP); return }
                    }
                }
            }
        }

        if (_state.value.lcdMode == com.example.model.LcdMode.VECTOR) {
            if (_state.value.showVectorOptn) {
                when (text) {
                    "1" -> { setVectorStage(com.example.model.VectorStage.MDEF); toggleVectorOptn(false); return }
                    "2" -> { setVectorStage(com.example.model.VectorStage.INP); toggleVectorOptn(false); return }
                    "3" -> { selectVector("VctA"); setVectorStage(com.example.model.VectorStage.INP); toggleVectorOptn(false); return }
                    "4" -> { selectVector("VctB"); setVectorStage(com.example.model.VectorStage.INP); toggleVectorOptn(false); return }
                    "5" -> { setVectorCalcExpr("VctA•VctB"); runVectorCalc(); toggleVectorOptn(false); return }
                    "6" -> { setVectorCalcExpr("VctA×VctB"); runVectorCalc(); toggleVectorOptn(false); return }
                    "7" -> { setVectorCalcExpr("Angle(VctA,VctB)"); runVectorCalc(); toggleVectorOptn(false); return }
                    "8" -> { setVectorCalcExpr("UnitV(VctA)"); runVectorCalc(); toggleVectorOptn(false); return }
                    "9" -> { setVectorCalcExpr("Abs(VctA)"); runVectorCalc(); toggleVectorOptn(false); return }
                }
                return
            }

            when (_state.value.vectorStage) {
                com.example.model.VectorStage.MDEF -> {
                    when (text) {
                        "1" -> defineVector("VctA")
                        "2" -> defineVector("VctB")
                        "3" -> defineVector("VctC")
                        "4" -> defineVector("VctD")
                    }
                    return
                }
                com.example.model.VectorStage.VDIM -> {
                    if (text in listOf("2", "3")) {
                        setVectorDimSelection(text.toInt())
                        return
                    }
                }
                com.example.model.VectorStage.INP -> {
                    if (text == "-") {
                        negateVectorComponent()
                        return
                    }
                    if (text.all { it.isDigit() || it == '.' || it == '-' }) {
                        updateVectorComponent(_state.value.vectorActiveIndex, text)
                        return
                    }
                }
                com.example.model.VectorStage.CALC -> {
                    when (text) {
                        "+" -> { setVectorCalcExpr("VctA+VctB"); runVectorCalc(); return }
                        "-" -> { setVectorCalcExpr("VctA-VctB"); runVectorCalc(); return }
                        "×", "*" -> { setVectorCalcExpr("VctA×VctB"); runVectorCalc(); return }
                        "•", "." -> { setVectorCalcExpr("VctA•VctB"); runVectorCalc(); return }
                        "1" -> { setVectorStage(com.example.model.VectorStage.MDEF); return }
                        "2" -> { selectVector("VctA"); setVectorStage(com.example.model.VectorStage.INP); return }
                        "3" -> { selectVector("VctB"); setVectorStage(com.example.model.VectorStage.INP); return }
                        "5" -> { setVectorCalcExpr("VctA•VctB"); runVectorCalc(); return }
                        "6" -> { setVectorCalcExpr("VctA×VctB"); runVectorCalc(); return }
                        "7" -> { setVectorCalcExpr("Angle(VctA,VctB)"); runVectorCalc(); return }
                        "8" -> { setVectorCalcExpr("UnitV(VctA)"); runVectorCalc(); return }
                        "9" -> { setVectorCalcExpr("Abs(VctA)"); runVectorCalc(); return }
                    }
                }
            }
            return
        }

        if (_state.value.lcdMode == com.example.model.LcdMode.EQUATION) {
            if (text.all { it.isDigit() || it == '.' || it == '-' }) {
                updateEqnCoeff(_state.value.eqnActiveIndex, text)
                return
            }
        }

        _state.update { curr ->
            // If just evaluated and inputting a new digit/function, start fresh unless it's an operator
            val isOperator = text in listOf("+", "-", "×", "÷", "^", "²", "³", "⁻¹", "!")
            val startingFormula = if (curr.isEvaluated && !isOperator) "" else curr.formula
            val startingCursor = if (curr.isEvaluated && !isOperator) 0 else curr.cursorIndex

            val left = startingFormula.substring(0, startingCursor)
            val right = startingFormula.substring(startingCursor)
            val newFormula = left + text + right
            val newCursor = startingCursor + text.length

            curr.copy(
                formula = newFormula,
                cursorIndex = newCursor,
                isEvaluated = false,
                isError = false,
                errorMessage = "",
                isShiftActive = false,
                isAlphaActive = false,
                statusMessage = if (curr.currentMode == 2) "CMPLX" else if (curr.baseMode != BaseMode.COMP) curr.baseMode.symbol else ""
            )
        }
    }

    fun onAction(action: KeyAction) {
        when (action) {
            KeyAction.Shift -> toggleShift()
            KeyAction.Alpha -> toggleAlpha()
            KeyAction.On -> {
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, showMatrixOptn = false) }
                resetCalculator()
            }
            KeyAction.Ac -> {
                if (_state.value.isCalcPromptActive) {
                    _state.update { it.copy(isCalcPromptActive = false, calcPromptInput = "") }
                } else if (_state.value.showCalcOptn) {
                    if (_state.value.calcOptnSubmenu.isNotEmpty()) {
                        _state.update { it.copy(calcOptnSubmenu = "") }
                    } else {
                        _state.update { it.copy(showCalcOptn = false, calcOptnSubmenu = "") }
                    }
                } else if (_state.value.showMatrixOptn) {
                    _state.update { it.copy(showMatrixOptn = false) }
                } else if (_state.value.showVectorOptn) {
                    _state.update { it.copy(showVectorOptn = false) }
                } else if (_state.value.lcdMode == com.example.model.LcdMode.MATRIX) {
                    if (_state.value.matrixCalcExpr.isNotEmpty()) {
                        _state.update { it.copy(matrixCalcExpr = "", matrixCalcResult = "") }
                    } else {
                        _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC) }
                    }
                } else if (_state.value.lcdMode == com.example.model.LcdMode.VECTOR) {
                    if (_state.value.vectorCalcExpr.isNotEmpty()) {
                        _state.update { it.copy(vectorCalcExpr = "", vectorCalcResult = "") }
                    } else {
                        _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC) }
                    }
                } else if (_state.value.lcdMode != com.example.model.LcdMode.CALC) {
                    _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC) }
                } else {
                    allClear()
                }
            }
            KeyAction.Del -> {
                if (_state.value.isCalcPromptActive) {
                    _state.update { it.copy(calcPromptInput = it.calcPromptInput.dropLast(1)) }
                    return
                }
                when (_state.value.lcdMode) {
                    com.example.model.LcdMode.MATRIX -> deleteMatrixCellChar()
                    com.example.model.LcdMode.VECTOR -> deleteVectorComponentChar()
                    com.example.model.LcdMode.EQUATION -> deleteEqnCoeffChar()
                    else -> deleteAtCursor()
                }
            }
            KeyAction.Equals -> {
                if (_state.value.isCalcPromptActive) {
                    confirmCalcPrompt()
                    return
                }
                if (_state.value.showMatrixOptn) {
                    _state.update { it.copy(showMatrixOptn = false) }
                } else if (_state.value.showVectorOptn) {
                    _state.update { it.copy(showVectorOptn = false) }
                } else {
                    when (_state.value.lcdMode) {
                        com.example.model.LcdMode.MENU -> selectLcdMenuItem(_state.value.selectedMenuItem)
                        com.example.model.LcdMode.MATRIX -> {
                            when (_state.value.matrixStage) {
                                com.example.model.MatrixStage.MDEF -> defineMatrix(_state.value.currentMatrixName)
                                com.example.model.MatrixStage.ROW -> confirmMatrixRows()
                                com.example.model.MatrixStage.COL -> confirmMatrixCols()
                                com.example.model.MatrixStage.INP -> advanceMatrixCell()
                                com.example.model.MatrixStage.CALC -> runMatrixCalc()
                            }
                        }
                        com.example.model.LcdMode.VECTOR -> {
                            when (_state.value.vectorStage) {
                                com.example.model.VectorStage.MDEF -> defineVector(_state.value.currentVectorName)
                                com.example.model.VectorStage.VDIM -> confirmVectorDim()
                                com.example.model.VectorStage.INP -> advanceVectorComponent()
                                com.example.model.VectorStage.CALC -> runVectorCalc()
                            }
                        }
                        com.example.model.LcdMode.EQUATION -> solveEquation()
                        else -> calculate()
                    }
                }
            }
            KeyAction.SdToggle -> cycleSdFormat()
            KeyAction.InsertFraction -> insertFraction()
            KeyAction.InsertLogBase -> insertLogBase()
            KeyAction.InsertIntegral -> insertIntegral()
            KeyAction.InsertDerivative -> insertDerivative()
            KeyAction.CursorLeft -> {
                when (_state.value.lcdMode) {
                    com.example.model.LcdMode.MENU -> navigateMenuCursor(-1)
                    com.example.model.LcdMode.MATRIX -> {
                        if (_state.value.matrixStage == com.example.model.MatrixStage.INP) {
                            selectMatrixCell(_state.value.matrixActiveRow, (_state.value.matrixActiveCol - 1).coerceAtLeast(0))
                        }
                    }
                    com.example.model.LcdMode.VECTOR -> {
                        when (_state.value.vectorStage) {
                            com.example.model.VectorStage.VDIM -> setVectorDimSelection(2)
                            com.example.model.VectorStage.INP -> selectVectorComponent((_state.value.vectorActiveIndex - 1).coerceAtLeast(0))
                            else -> {}
                        }
                    }
                    com.example.model.LcdMode.EQUATION -> selectEqnCoeff((_state.value.eqnActiveIndex - 1).coerceAtLeast(0))
                    else -> moveCursor(-1)
                }
            }
            KeyAction.CursorRight -> {
                when (_state.value.lcdMode) {
                    com.example.model.LcdMode.MENU -> navigateMenuCursor(1)
                    com.example.model.LcdMode.MATRIX -> {
                        if (_state.value.matrixStage == com.example.model.MatrixStage.INP) {
                            selectMatrixCell(_state.value.matrixActiveRow, (_state.value.matrixActiveCol + 1).coerceAtMost(_state.value.matrixCols - 1))
                        }
                    }
                    com.example.model.LcdMode.VECTOR -> {
                        when (_state.value.vectorStage) {
                            com.example.model.VectorStage.VDIM -> setVectorDimSelection(3)
                            com.example.model.VectorStage.INP -> selectVectorComponent((_state.value.vectorActiveIndex + 1).coerceAtMost(_state.value.vectorDim - 1))
                            else -> {}
                        }
                    }
                    com.example.model.LcdMode.EQUATION -> selectEqnCoeff((_state.value.eqnActiveIndex + 1).coerceAtMost(5))
                    else -> moveCursor(1)
                }
            }
            KeyAction.CursorUp -> {
                when (_state.value.lcdMode) {
                    com.example.model.LcdMode.MENU -> navigateMenuCursor(-3)
                    com.example.model.LcdMode.MATRIX -> {
                        when (_state.value.matrixStage) {
                            com.example.model.MatrixStage.ROW, com.example.model.MatrixStage.COL -> {
                                setMatrixDimSelection((_state.value.matrixDimSelection - 1).coerceAtLeast(1))
                            }
                            com.example.model.MatrixStage.INP -> {
                                selectMatrixCell((_state.value.matrixActiveRow - 1).coerceAtLeast(0), _state.value.matrixActiveCol)
                            }
                            else -> {}
                        }
                    }
                    com.example.model.LcdMode.VECTOR -> {
                        when (_state.value.vectorStage) {
                            com.example.model.VectorStage.VDIM -> setVectorDimSelection(2)
                            com.example.model.VectorStage.INP -> selectVectorComponent((_state.value.vectorActiveIndex - 1).coerceAtLeast(0))
                            else -> {}
                        }
                    }
                    com.example.model.LcdMode.EQUATION -> selectEqnCoeff((_state.value.eqnActiveIndex - 3).coerceAtLeast(0))
                    else -> handleCursorVertical(-1)
                }
            }
            KeyAction.CursorDown -> {
                when (_state.value.lcdMode) {
                    com.example.model.LcdMode.MENU -> navigateMenuCursor(3)
                    com.example.model.LcdMode.MATRIX -> {
                        when (_state.value.matrixStage) {
                            com.example.model.MatrixStage.ROW, com.example.model.MatrixStage.COL -> {
                                setMatrixDimSelection((_state.value.matrixDimSelection + 1).coerceAtMost(4))
                            }
                            com.example.model.MatrixStage.INP -> {
                                selectMatrixCell((_state.value.matrixActiveRow + 1).coerceAtMost(_state.value.matrixRows - 1), _state.value.matrixActiveCol)
                            }
                            else -> {}
                        }
                    }
                    com.example.model.LcdMode.VECTOR -> {
                        when (_state.value.vectorStage) {
                            com.example.model.VectorStage.VDIM -> setVectorDimSelection(3)
                            com.example.model.VectorStage.INP -> selectVectorComponent((_state.value.vectorActiveIndex + 1).coerceAtMost(_state.value.vectorDim - 1))
                            else -> {}
                        }
                    }
                    com.example.model.LcdMode.EQUATION -> selectEqnCoeff((_state.value.eqnActiveIndex + 3).coerceAtMost(5))
                    else -> handleCursorVertical(1)
                }
            }
            KeyAction.MenuSetup -> {
                if (_state.value.isShiftActive) toggleSetup(true) else toggleModeMenu(true)
            }
            KeyAction.ModeMenu -> toggleModeMenu(true)
            KeyAction.SetupMenu -> toggleSetup(true)
            KeyAction.OptnMenu -> {
                if (_state.value.showCalcOptn) {
                    _state.update { it.copy(showCalcOptn = false, calcOptnSubmenu = "", showOptnDialog = false) }
                } else if (_state.value.lcdMode == com.example.model.LcdMode.MATRIX) {
                    toggleMatrixOptn()
                } else if (_state.value.lcdMode == com.example.model.LcdMode.VECTOR) {
                    toggleVectorOptn()
                } else {
                    _state.update { it.copy(showCalcOptn = true, calcOptnSubmenu = "", showOptnDialog = false, isShiftActive = false) }
                }
            }
            KeyAction.QrCode -> toggleQr(true)
            KeyAction.Const -> toggleConstDialog(true)
            KeyAction.Conv -> toggleConvDialog(true)
            KeyAction.Eng -> {
                if (_state.value.isAlphaActive || _state.value.currentMode == 2) {
                    onCharInput("i")
                } else {
                    handleEng()
                }
            }
            KeyAction.Sto -> startSto()
            KeyAction.Rcl -> startRcl()
            KeyAction.MPlus -> handleMPlus(subtract = false)
            KeyAction.MMinus -> handleMPlus(subtract = true)
            is KeyAction.Variable -> handleVariable(action.name)
            is KeyAction.SetAngleUnit -> setAngleUnit(action.unit)
            is KeyAction.SetBaseMode -> setBaseMode(action.mode)
            is KeyAction.SelectMode -> selectMode(action.mode)
            KeyAction.OpenMatrixDialog -> selectLcdMenuItem(4)
            KeyAction.OpenVectorDialog -> selectLcdMenuItem(5)
            KeyAction.OpenEquationDialog -> toggleEquationDialog(true)
            KeyAction.OpenStatisticsDialog -> toggleStatisticsDialog(true)
            KeyAction.OpenTableDialog -> toggleTableDialog(true)
            KeyAction.OpenDistributionDialog -> toggleDistributionDialog(true)
            KeyAction.OpenRatioDialog -> toggleRatioDialog(true)
            KeyAction.CalcPrompt -> handleCalcPromptKey()
            KeyAction.Solve -> solveEquationForX()
            KeyAction.ToggleHscGuide -> toggleHscMathGuide()
        }
    }

    private fun toggleShift() {
        _state.update { it.copy(isShiftActive = !it.isShiftActive, isAlphaActive = false, statusMessage = if (!it.isShiftActive) "SHIFT" else "") }
    }

    private fun toggleAlpha() {
        _state.update { it.copy(isAlphaActive = !it.isAlphaActive, isShiftActive = false, statusMessage = if (!it.isAlphaActive) "ALPHA" else "") }
    }

    fun toggleModeMenu(show: Boolean) {
        _state.update {
            if (show) {
                val newLcdMode = if (it.lcdMode == com.example.model.LcdMode.MENU) com.example.model.LcdMode.CALC else com.example.model.LcdMode.MENU
                it.copy(lcdMode = newLcdMode, showModeMenuDialog = false, isShiftActive = false)
            } else {
                it.copy(lcdMode = com.example.model.LcdMode.CALC, showModeMenuDialog = false, isShiftActive = false)
            }
        }
    }

    fun navigateMenuCursor(delta: Int) {
        _state.update { curr ->
            val zeroIndexed = (curr.selectedMenuItem - 1 + delta).let { ((it % 12) + 12) % 12 }
            curr.copy(selectedMenuItem = zeroIndexed + 1)
        }
    }

    fun selectLcdMenuItem(index: Int) {
        _state.update { it.copy(selectedMenuItem = index) }
        when (index) {
            1 -> {
                selectMode(1)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, showModeMenuDialog = false) }
            }
            2 -> {
                selectMode(2)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, statusMessage = "CMPLX", showModeMenuDialog = false) }
            }
            3 -> {
                selectMode(3)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, baseMode = BaseMode.DEC, statusMessage = "DEC", showModeMenuDialog = false) }
            }
            4 -> {
                _state.update {
                    it.copy(
                        lcdMode = com.example.model.LcdMode.MATRIX,
                        currentMode = 4,
                        currentModeName = "Matrix",
                        matrixStage = com.example.model.MatrixStage.MDEF,
                        statusMessage = "MAT",
                        showModeMenuDialog = false,
                        showMatrixDialog = false
                    )
                }
                computeMatrixDet()
            }
            5 -> {
                _state.update {
                    it.copy(
                        lcdMode = com.example.model.LcdMode.VECTOR,
                        currentMode = 5,
                        currentModeName = "Vector",
                        vectorStage = com.example.model.VectorStage.MDEF,
                        statusMessage = "VCT",
                        showModeMenuDialog = false,
                        showVectorDialog = false
                    )
                }
                runVectorCalc()
            }
            6 -> {
                selectMode(6)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, statusMessage = "STAT", showModeMenuDialog = false, showStatisticsDialog = true) }
            }
            7 -> {
                selectMode(7)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, statusMessage = "DIST", showModeMenuDialog = false, showDistributionDialog = true) }
            }
            8 -> {
                selectMode(8)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, statusMessage = "SPREAD", showModeMenuDialog = false, showSpreadsheetDialog = true) }
            }
            9 -> {
                selectMode(9)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, statusMessage = "TABLE", showModeMenuDialog = false, showTableDialog = true) }
            }
            10 -> {
                _state.update {
                    it.copy(
                        lcdMode = com.example.model.LcdMode.EQUATION,
                        currentMode = 10,
                        currentModeName = "Equation/Func",
                        statusMessage = "EQN",
                        showModeMenuDialog = false,
                        showEquationDialog = false
                    )
                }
                solveEquation()
            }
            11 -> {
                selectMode(11)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, statusMessage = "INEQ", showModeMenuDialog = false, showInequalityDialog = true) }
            }
            12 -> {
                selectMode(12)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, statusMessage = "RATIO", showModeMenuDialog = false, showRatioDialog = true) }
            }
            else -> {
                selectMode(index)
                _state.update { it.copy(lcdMode = com.example.model.LcdMode.CALC, showModeMenuDialog = false) }
            }
        }
    }

    fun selectMatrix(name: String) {
        _state.update {
            val mat = it.matrixStore[name] ?: listOf(listOf("1", "0"), listOf("0", "1"))
            val r = mat.size
            val c = mat.firstOrNull()?.size ?: 2
            it.copy(
                currentMatrixName = name,
                matrixRows = r,
                matrixCols = c,
                matrixA = if (name == "MatA") mat else it.matrixA,
                matrixActiveRow = 0,
                matrixActiveCol = 0,
                matrixStage = com.example.model.MatrixStage.INP
            )
        }
        computeMatrixDet()
    }

    fun defineMatrix(name: String) {
        _state.update {
            val mat = it.matrixStore[name] ?: listOf(listOf("1", "0"), listOf("0", "1"))
            it.copy(
                currentMatrixName = name,
                matrixRows = mat.size,
                matrixCols = mat.firstOrNull()?.size ?: 2,
                matrixDimSelection = mat.size,
                matrixStage = com.example.model.MatrixStage.ROW
            )
        }
    }

    fun setMatrixDimSelection(dim: Int) {
        _state.update { it.copy(matrixDimSelection = dim.coerceIn(1, 4)) }
    }

    fun confirmMatrixRows() {
        _state.update {
            val r = it.matrixDimSelection.coerceIn(1, 4)
            it.copy(matrixRows = r, matrixStage = com.example.model.MatrixStage.COL, matrixDimSelection = it.matrixCols)
        }
    }

    fun confirmMatrixCols() {
        _state.update {
            val r = it.matrixRows
            val c = it.matrixDimSelection.coerceIn(1, 4)
            val newMat = List(r) { List(c) { "0" } }
            val newStore = it.matrixStore.toMutableMap()
            newStore[it.currentMatrixName] = newMat
            it.copy(
                matrixCols = c,
                matrixStore = newStore,
                matrixA = if (it.currentMatrixName == "MatA") newMat else it.matrixA,
                matrixActiveRow = 0,
                matrixActiveCol = 0,
                matrixStage = com.example.model.MatrixStage.INP
            )
        }
        computeMatrixDet()
    }

    fun advanceMatrixCell() {
        val curr = _state.value
        val r = curr.matrixActiveRow
        val c = curr.matrixActiveCol
        if (c + 1 < curr.matrixCols) {
            _state.update { it.copy(matrixActiveCol = c + 1) }
        } else if (r + 1 < curr.matrixRows) {
            _state.update { it.copy(matrixActiveRow = r + 1, matrixActiveCol = 0) }
        } else {
            _state.update { it.copy(matrixStage = com.example.model.MatrixStage.CALC, matrixCalcExpr = "Det(${it.currentMatrixName})") }
            runMatrixCalc()
        }
    }

    fun negateMatrixCell() {
        val curr = _state.value
        val curMat = curr.matrixStore[curr.currentMatrixName] ?: curr.matrixA
        val r = curr.matrixActiveRow
        val c = curr.matrixActiveCol
        val curVal = curMat.getOrNull(r)?.getOrNull(c) ?: "0"
        val neg = if (curVal.startsWith("-")) curVal.substring(1) else if (curVal == "0") "0" else "-$curVal"
        val newMat = curMat.mapIndexed { row, rList ->
            rList.mapIndexed { col, cell ->
                if (row == r && col == c) neg else cell
            }
        }
        val newStore = curr.matrixStore.toMutableMap()
        newStore[curr.currentMatrixName] = newMat
        _state.update {
            it.copy(matrixStore = newStore, matrixA = if (it.currentMatrixName == "MatA") newMat else it.matrixA)
        }
        computeMatrixDet()
    }

    fun setMatrixStage(stage: com.example.model.MatrixStage) {
        _state.update { it.copy(matrixStage = stage) }
    }

    fun setMatrixCalcExpr(expr: String) {
        _state.update { it.copy(matrixCalcExpr = expr) }
    }

    private fun formatMatrixResult(m: List<List<Double>>): String {
        return m.joinToString("\n") { row ->
            "[ " + row.joinToString(" ") { v ->
                val str = if (v == v.toLong().toDouble()) "${v.toLong()}" else "%.2f".format(v)
                str.padStart(4, ' ')
            } + " ]"
        }
    }

    fun runMatrixCalc() {
        _state.update { curr ->
            try {
                val mA = curr.matrixStore["MatA"]?.map { row -> row.map { it.toDoubleOrNull() ?: 0.0 } }
                val mB = curr.matrixStore["MatB"]?.map { row -> row.map { it.toDoubleOrNull() ?: 0.0 } }
                val expr = curr.matrixCalcExpr
                var resultStr = ""
                when {
                    expr == "7*MatA" && mA != null -> {
                        val mat = com.example.engine.AdvancedMathEngine.Matrix(mA.size, mA[0].size, mA) * 7.0
                        resultStr = formatMatrixResult(mat.data)
                    }
                    expr == "MatA+MatB" && mA != null && mB != null -> {
                        val matA = com.example.engine.AdvancedMathEngine.Matrix(mA.size, mA[0].size, mA)
                        val matB = com.example.engine.AdvancedMathEngine.Matrix(mB.size, mB[0].size, mB)
                        val res = matA + matB
                        resultStr = formatMatrixResult(res.data)
                    }
                    expr == "MatA-MatB" && mA != null && mB != null -> {
                        val matA = com.example.engine.AdvancedMathEngine.Matrix(mA.size, mA[0].size, mA)
                        val matB = com.example.engine.AdvancedMathEngine.Matrix(mB.size, mB[0].size, mB)
                        val res = matA - matB
                        resultStr = formatMatrixResult(res.data)
                    }
                    (expr == "MatA*MatB" || expr == "MatA×MatB") && mA != null && mB != null -> {
                        val matA = com.example.engine.AdvancedMathEngine.Matrix(mA.size, mA[0].size, mA)
                        val matB = com.example.engine.AdvancedMathEngine.Matrix(mB.size, mB[0].size, mB)
                        val res = matA * matB
                        resultStr = formatMatrixResult(res.data)
                    }
                    (expr == "MatB*MatA" || expr == "MatB×MatA") && mA != null && mB != null -> {
                        val matA = com.example.engine.AdvancedMathEngine.Matrix(mA.size, mA[0].size, mA)
                        val matB = com.example.engine.AdvancedMathEngine.Matrix(mB.size, mB[0].size, mB)
                        val res = matB * matA
                        resultStr = formatMatrixResult(res.data)
                    }
                    expr.startsWith("Det(MatA)") && mA != null -> {
                        val matA = com.example.engine.AdvancedMathEngine.Matrix(mA.size, mA[0].size, mA)
                        val det = matA.determinant()
                        val detStr = if (det == det.toLong().toDouble()) "${det.toLong()}" else "%.3f".format(det)
                        resultStr = "Det(MatA) = $detStr"
                    }
                    expr.startsWith("Det(MatB)") && mB != null -> {
                        val matB = com.example.engine.AdvancedMathEngine.Matrix(mB.size, mB[0].size, mB)
                        val det = matB.determinant()
                        val detStr = if (det == det.toLong().toDouble()) "${det.toLong()}" else "%.3f".format(det)
                        resultStr = "Det(MatB) = $detStr"
                    }
                    expr.startsWith("Trn(MatA)") && mA != null -> {
                        val matA = com.example.engine.AdvancedMathEngine.Matrix(mA.size, mA[0].size, mA)
                        resultStr = formatMatrixResult(matA.transpose().data)
                    }
                    expr.startsWith("Trn(MatB)") && mB != null -> {
                        val matB = com.example.engine.AdvancedMathEngine.Matrix(mB.size, mB[0].size, mB)
                        resultStr = formatMatrixResult(matB.transpose().data)
                    }
                    (expr.startsWith("Inv(MatA)") || expr == "MatA⁻¹" || expr == "MatA^-1") && mA != null -> {
                        val matA = com.example.engine.AdvancedMathEngine.Matrix(mA.size, mA[0].size, mA)
                        val inv = matA.inverse()
                        resultStr = formatMatrixResult(inv.data)
                    }
                    (expr.startsWith("Inv(MatB)") || expr == "MatB⁻¹" || expr == "MatB^-1") && mB != null -> {
                        val matB = com.example.engine.AdvancedMathEngine.Matrix(mB.size, mB[0].size, mB)
                        val inv = matB.inverse()
                        resultStr = formatMatrixResult(inv.data)
                    }
                    else -> {
                        val curMat = curr.matrixStore[curr.currentMatrixName] ?: curr.matrixA
                        val mat = com.example.engine.AdvancedMathEngine.Matrix(curMat.size, curMat[0].size, curMat.map { r -> r.map { it.toDoubleOrNull() ?: 0.0 } })
                        val det = mat.determinant()
                        val detStr = if (det == det.toLong().toDouble()) "${det.toLong()}" else "%.3f".format(det)
                        resultStr = "Det(${curr.currentMatrixName}) = $detStr"
                    }
                }
                curr.copy(matrixCalcResult = resultStr, matrixDetDisplay = resultStr)
            } catch (e: Exception) {
                curr.copy(matrixCalcResult = "Error: Size mismatch", matrixDetDisplay = "Error: Size mismatch")
            }
        }
    }

    fun selectMatrixCell(row: Int, col: Int) {
        _state.update { it.copy(matrixActiveRow = row.coerceIn(0, it.matrixRows - 1), matrixActiveCol = col.coerceIn(0, it.matrixCols - 1)) }
    }

    fun toggleMatrixOptn(show: Boolean? = null) {
        _state.update { curr ->
            curr.copy(showMatrixOptn = show ?: !curr.showMatrixOptn)
        }
    }

    fun setMatrixDimensions(rows: Int, cols: Int) {
        val clampedR = rows.coerceIn(2, 4)
        val clampedC = cols.coerceIn(2, 4)
        _state.update { curr ->
            val curMat = curr.matrixStore[curr.currentMatrixName] ?: curr.matrixA
            val newMat = List(clampedR) { r ->
                List(clampedC) { c ->
                    curMat.getOrNull(r)?.getOrNull(c) ?: if (r == c) "1" else "0"
                }
            }
            val newStore = curr.matrixStore.toMutableMap()
            newStore[curr.currentMatrixName] = newMat
            curr.copy(
                matrixRows = clampedR,
                matrixCols = clampedC,
                matrixStore = newStore,
                matrixA = if (curr.currentMatrixName == "MatA") newMat else curr.matrixA,
                matrixActiveRow = 0,
                matrixActiveCol = 0
            )
        }
        computeMatrixDet()
    }

    fun updateMatrixCell(row: Int, col: Int, text: String) {
        _state.update { curr ->
            val curMat = curr.matrixStore[curr.currentMatrixName] ?: curr.matrixA
            val curVal = curMat.getOrNull(row)?.getOrNull(col) ?: "0"
            val updated = if (curVal == "0" && text != ".") text else curVal + text
            val newMat = curMat.mapIndexed { r, rList ->
                rList.mapIndexed { c, cell ->
                    if (r == row && c == col) updated else cell
                }
            }
            val newStore = curr.matrixStore.toMutableMap()
            newStore[curr.currentMatrixName] = newMat
            curr.copy(
                matrixStore = newStore,
                matrixA = if (curr.currentMatrixName == "MatA") newMat else curr.matrixA
            )
        }
        computeMatrixDet()
    }

    fun deleteMatrixCellChar() {
        _state.update { curr ->
            val curMat = curr.matrixStore[curr.currentMatrixName] ?: curr.matrixA
            val r = curr.matrixActiveRow
            val c = curr.matrixActiveCol
            val curVal = curMat.getOrNull(r)?.getOrNull(c) ?: "0"
            val updated = if (curVal.length <= 1) "0" else curVal.dropLast(1)
            val newMat = curMat.mapIndexed { row, rList ->
                rList.mapIndexed { col, cell ->
                    if (row == r && col == c) updated else cell
                }
            }
            val newStore = curr.matrixStore.toMutableMap()
            newStore[curr.currentMatrixName] = newMat
            curr.copy(
                matrixStore = newStore,
                matrixA = if (curr.currentMatrixName == "MatA") newMat else curr.matrixA
            )
        }
        computeMatrixDet()
    }

    fun computeMatrixDet() {
        _state.update { curr ->
            val curMat = curr.matrixStore[curr.currentMatrixName] ?: curr.matrixA
            val r = curMat.size
            val c = curMat.firstOrNull()?.size ?: 0
            if (r != c) {
                curr.copy(matrixDetDisplay = "Det: Sq only")
            } else {
                try {
                    val data = curMat.map { row -> row.map { it.toDoubleOrNull() ?: 0.0 } }
                    val mat = com.example.engine.AdvancedMathEngine.Matrix(r, c, data)
                    val det = mat.determinant()
                    val detStr = if (det == det.toLong().toDouble()) "${det.toLong()}" else "%.3f".format(det)
                    curr.copy(matrixDetDisplay = "Det(${curr.currentMatrixName}) = $detStr")
                } catch (e: Exception) {
                    curr.copy(matrixDetDisplay = "Det: Error")
                }
            }
        }
    }

    fun computeMatrixTranspose() {
        _state.update { curr ->
            val curMat = curr.matrixStore[curr.currentMatrixName] ?: curr.matrixA
            val r = curMat.size
            val c = curMat.firstOrNull()?.size ?: 0
            val trnRows = List(c) { tr ->
                List(r) { tc ->
                    curMat[tc][tr]
                }
            }
            val preview = trnRows.joinToString(";") { it.joinToString(",") }
            curr.copy(matrixDetDisplay = "Trn: [$preview]")
        }
    }

    fun computeMatrixInverse() {
        _state.update { curr ->
            try {
                val curMat = curr.matrixStore[curr.currentMatrixName] ?: curr.matrixA
                val r = curMat.size
                val c = curMat.firstOrNull()?.size ?: 0
                if (r != c || r > 3) {
                    curr.copy(matrixDetDisplay = "Inv: 2x2 or 3x3 only")
                } else {
                    val data = curMat.map { row -> row.map { it.toDoubleOrNull() ?: 0.0 } }
                    val m = com.example.engine.AdvancedMathEngine.Matrix(r, c, data)
                    val inv = m.inverse()
                    val preview = inv.data.joinToString(";") { row ->
                        row.joinToString(",") { formatMatrixNum(it) }
                    }
                    curr.copy(matrixDetDisplay = "Inv: [$preview]")
                }
            } catch (e: Exception) {
                curr.copy(matrixDetDisplay = "Math ERROR (Det=0)")
            }
        }
    }

    fun computeMatrixAdd() {
        _state.update { curr ->
            try {
                val mA = curr.matrixStore["MatA"] ?: curr.matrixA
                val mB = curr.matrixStore["MatB"] ?: listOf(listOf("0", "0"), listOf("0", "0"))
                if (mA.size != mB.size || (mA.firstOrNull()?.size != mB.firstOrNull()?.size)) {
                    curr.copy(matrixDetDisplay = "A+B: Dim error")
                } else {
                    val r = mA.size
                    val c = mA[0].size
                    val matA = com.example.engine.AdvancedMathEngine.Matrix(r, c, mA.map { row -> row.map { it.toDoubleOrNull() ?: 0.0 } })
                    val matB = com.example.engine.AdvancedMathEngine.Matrix(r, c, mB.map { row -> row.map { it.toDoubleOrNull() ?: 0.0 } })
                    val sum = matA + matB
                    val preview = sum.data.joinToString(";") { row ->
                        row.joinToString(",") { formatMatrixNum(it) }
                    }
                    curr.copy(matrixDetDisplay = "A+B: [$preview]")
                }
            } catch (e: Exception) {
                curr.copy(matrixDetDisplay = "Error")
            }
        }
    }

    fun computeMatrixMul() {
        _state.update { curr ->
            try {
                val mA = curr.matrixStore["MatA"] ?: curr.matrixA
                val mB = curr.matrixStore["MatB"] ?: listOf(listOf("0", "0"), listOf("0", "0"))
                val rA = mA.size
                val cA = mA[0].size
                val rB = mB.size
                val cB = mB[0].size
                if (cA != rB) {
                    curr.copy(matrixDetDisplay = "A×B: Dim mismatch")
                } else {
                    val matA = com.example.engine.AdvancedMathEngine.Matrix(rA, cA, mA.map { row -> row.map { it.toDoubleOrNull() ?: 0.0 } })
                    val matB = com.example.engine.AdvancedMathEngine.Matrix(rB, cB, mB.map { row -> row.map { it.toDoubleOrNull() ?: 0.0 } })
                    val prod = matA * matB
                    val preview = prod.data.joinToString(";") { row ->
                        row.joinToString(",") { formatMatrixNum(it) }
                    }
                    curr.copy(matrixDetDisplay = "A×B: [$preview]")
                }
            } catch (e: Exception) {
                curr.copy(matrixDetDisplay = "Error")
            }
        }
    }

    private fun formatMatrixNum(v: Double): String {
        return if (v == v.toLong().toDouble()) "${v.toLong()}" else "%.2f".format(v)
    }

    // ==================== VECTOR METHODS ====================
    fun selectVector(name: String) {
        _state.update {
            val vec = it.vectorStore[name] ?: listOf("0", "0", "0")
            it.copy(
                currentVectorName = name,
                vectorDim = vec.size,
                vectorActiveIndex = 0,
                vectorStage = com.example.model.VectorStage.INP
            )
        }
        runVectorCalc()
    }

    fun defineVector(name: String) {
        _state.update {
            val vec = it.vectorStore[name] ?: listOf("0", "0", "0")
            it.copy(
                currentVectorName = name,
                vectorDim = vec.size,
                vectorDimSelection = vec.size,
                vectorStage = com.example.model.VectorStage.VDIM
            )
        }
    }

    fun setVectorDimSelection(dim: Int) {
        _state.update { it.copy(vectorDimSelection = dim.coerceIn(2, 3)) }
    }

    fun confirmVectorDim() {
        _state.update {
            val dim = it.vectorDimSelection.coerceIn(2, 3)
            val newVec = List(dim) { "0" }
            val newStore = it.vectorStore.toMutableMap()
            newStore[it.currentVectorName] = newVec
            it.copy(
                vectorDim = dim,
                vectorStore = newStore,
                vectorActiveIndex = 0,
                vectorStage = com.example.model.VectorStage.INP
            )
        }
        runVectorCalc()
    }

    fun updateVectorComponent(index: Int, text: String) {
        _state.update { curr ->
            val curVec = curr.vectorStore[curr.currentVectorName] ?: listOf("0", "0", "0")
            val currentVal = curVec.getOrNull(index) ?: "0"
            val newVal = if (currentVal == "0" && text != ".") text else currentVal + text
            val newVec = curVec.mapIndexed { idx, cell ->
                if (idx == index) newVal else cell
            }
            val newStore = curr.vectorStore.toMutableMap()
            newStore[curr.currentVectorName] = newVec
            curr.copy(vectorStore = newStore)
        }
        runVectorCalc()
    }

    fun deleteVectorComponentChar() {
        _state.update { curr ->
            val curVec = curr.vectorStore[curr.currentVectorName] ?: listOf("0", "0", "0")
            val idx = curr.vectorActiveIndex
            val currentVal = curVec.getOrNull(idx) ?: "0"
            val newVal = if (currentVal.length <= 1) "0" else currentVal.dropLast(1)
            val newVec = curVec.mapIndexed { i, cell ->
                if (i == idx) newVal else cell
            }
            val newStore = curr.vectorStore.toMutableMap()
            newStore[curr.currentVectorName] = newVec
            curr.copy(vectorStore = newStore)
        }
        runVectorCalc()
    }

    fun negateVectorComponent() {
        _state.update { curr ->
            val curVec = curr.vectorStore[curr.currentVectorName] ?: listOf("0", "0", "0")
            val idx = curr.vectorActiveIndex
            val curVal = curVec.getOrNull(idx) ?: "0"
            val neg = if (curVal.startsWith("-")) curVal.substring(1) else if (curVal == "0") "0" else "-$curVal"
            val newVec = curVec.mapIndexed { i, cell ->
                if (i == idx) neg else cell
            }
            val newStore = curr.vectorStore.toMutableMap()
            newStore[curr.currentVectorName] = newVec
            curr.copy(vectorStore = newStore)
        }
        runVectorCalc()
    }

    fun advanceVectorComponent() {
        val curr = _state.value
        val idx = curr.vectorActiveIndex
        if (idx + 1 < curr.vectorDim) {
            _state.update { it.copy(vectorActiveIndex = idx + 1) }
        } else {
            _state.update { it.copy(vectorStage = com.example.model.VectorStage.CALC, vectorCalcExpr = "VctA•VctB") }
            runVectorCalc()
        }
    }

    fun selectVectorComponent(index: Int) {
        _state.update { it.copy(vectorActiveIndex = index.coerceIn(0, it.vectorDim - 1)) }
    }

    fun setVectorStage(stage: com.example.model.VectorStage) {
        _state.update { it.copy(vectorStage = stage) }
    }

    fun setVectorCalcExpr(expr: String) {
        _state.update { it.copy(vectorCalcExpr = expr) }
    }

    fun toggleVectorOptn(show: Boolean? = null) {
        _state.update { curr ->
            curr.copy(showVectorOptn = show ?: !curr.showVectorOptn)
        }
    }

    private fun formatVectorDouble(v: com.example.engine.AdvancedMathEngine.Vector3, dim: Int): String {
        val f = { num: Double ->
            if (num == num.toLong().toDouble()) "${num.toLong()}" else "%.2f".format(num)
        }
        return if (dim == 2) {
            "[ ${f(v.x)}  ${f(v.y)} ]"
        } else {
            "[ ${f(v.x)}  ${f(v.y)}  ${f(v.z)} ]"
        }
    }

    fun runVectorCalc() {
        _state.update { curr ->
            try {
                val vA = run {
                    val raw = curr.vectorStore["VctA"] ?: listOf("1", "2", "3")
                    com.example.engine.AdvancedMathEngine.Vector3(
                        raw.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
                        raw.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
                        raw.getOrNull(2)?.toDoubleOrNull() ?: 0.0
                    )
                }
                val vB = run {
                    val raw = curr.vectorStore["VctB"] ?: listOf("2", "0", "1")
                    com.example.engine.AdvancedMathEngine.Vector3(
                        raw.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
                        raw.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
                        raw.getOrNull(2)?.toDoubleOrNull() ?: 0.0
                    )
                }
                val expr = curr.vectorCalcExpr
                val dim = curr.vectorDim
                var resultStr = ""
                when {
                    expr == "VctA•VctB" || expr.contains("•") || expr.contains("Dot") -> {
                        val dot = vA.dot(vB)
                        val dotStr = if (dot == dot.toLong().toDouble()) "${dot.toLong()}" else "%.2f".format(dot)
                        resultStr = "VctA • VctB = $dotStr"
                    }
                    expr == "VctA×VctB" || expr.contains("×") || expr.contains("Cross") -> {
                        val cross = vA.cross(vB)
                        resultStr = "VctA × VctB =\n${formatVectorDouble(cross, 3)}"
                    }
                    expr == "VctA+VctB" -> {
                        val add = vA + vB
                        resultStr = "VctA + VctB =\n${formatVectorDouble(add, dim)}"
                    }
                    expr == "VctA-VctB" -> {
                        val sub = vA - vB
                        resultStr = "VctA - VctB =\n${formatVectorDouble(sub, dim)}"
                    }
                    expr.startsWith("Angle") -> {
                        val deg = vA.angleWith(vB)
                        resultStr = "Angle = ${"%.2f".format(deg)}°"
                    }
                    expr.startsWith("UnitV") -> {
                        val unit = vA.unit()
                        resultStr = "UnitV(VctA) =\n${formatVectorDouble(unit, dim)}"
                    }
                    expr.startsWith("Abs(VctA)") || expr == "|VctA|" -> {
                        val mag = vA.magnitude()
                        val magStr = if (mag == mag.toLong().toDouble()) "${mag.toLong()}" else "%.3f".format(mag)
                        resultStr = "|VctA| = $magStr"
                    }
                    expr.startsWith("Abs(VctB)") || expr == "|VctB|" -> {
                        val mag = vB.magnitude()
                        val magStr = if (mag == mag.toLong().toDouble()) "${mag.toLong()}" else "%.3f".format(mag)
                        resultStr = "|VctB| = $magStr"
                    }
                    else -> {
                        val curVec = curr.vectorStore[curr.currentVectorName] ?: listOf("0", "0", "0")
                        val v = com.example.engine.AdvancedMathEngine.Vector3(
                            curVec.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
                            curVec.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
                            curVec.getOrNull(2)?.toDoubleOrNull() ?: 0.0
                        )
                        val mag = v.magnitude()
                        val magStr = if (mag == mag.toLong().toDouble()) "${mag.toLong()}" else "%.3f".format(mag)
                        resultStr = "|${curr.currentVectorName}| = $magStr"
                    }
                }
                curr.copy(vectorCalcResult = resultStr)
            } catch (e: Exception) {
                curr.copy(vectorCalcResult = "Vector Error")
            }
        }
    }

    fun selectEqnCoeff(index: Int) {
        _state.update { it.copy(eqnActiveIndex = index.coerceIn(0, 5)) }
    }

    fun updateEqnCoeff(index: Int, text: String) {
        _state.update { curr ->
            val cur = curr.eqnCoeffs.getOrNull(index) ?: "0"
            val updated = if (cur == "0" && text != ".") text else cur + text
            val newCoeffs = curr.eqnCoeffs.toMutableList()
            if (index in newCoeffs.indices) {
                newCoeffs[index] = updated
            }
            curr.copy(eqnCoeffs = newCoeffs)
        }
    }

    fun deleteEqnCoeffChar() {
        _state.update { curr ->
            val idx = curr.eqnActiveIndex
            val cur = curr.eqnCoeffs.getOrNull(idx) ?: "0"
            val updated = if (cur.length <= 1) "0" else cur.dropLast(1)
            val newCoeffs = curr.eqnCoeffs.toMutableList()
            if (idx in newCoeffs.indices) {
                newCoeffs[idx] = updated
            }
            curr.copy(eqnCoeffs = newCoeffs)
        }
    }

    fun solveEquation() {
        _state.update { curr ->
            try {
                when (curr.eqnType) {
                    "POLY2" -> {
                        val a = curr.eqnCoeffs.getOrNull(0)?.toDoubleOrNull() ?: 1.0
                        val b = curr.eqnCoeffs.getOrNull(1)?.toDoubleOrNull() ?: -5.0
                        val c = curr.eqnCoeffs.getOrNull(2)?.toDoubleOrNull() ?: 6.0
                        val roots = com.example.engine.EquationSolver.solveQuadratic(a, b, c)
                        val r1 = roots[0]
                        val r2 = roots[1]
                        val r1Str = if (r1.imag == 0.0) {
                            if (r1.real == r1.real.toLong().toDouble()) "${r1.real.toLong()}" else "%.4f".format(r1.real).trimEnd('0').trimEnd('.')
                        } else {
                            val sign = if (r1.imag >= 0) "+" else "-"
                            val imVal = kotlin.math.abs(r1.imag)
                            val imStr = if (imVal == 1.0) "i" else if (imVal == imVal.toLong().toDouble()) "${imVal.toLong()}i" else "%.4fi".format(imVal)
                            "${if (r1.real == 0.0) "" else "%.4f ".format(r1.real)}$sign$imStr"
                        }
                        val r2Str = if (r2.imag == 0.0) {
                            if (r2.real == r2.real.toLong().toDouble()) "${r2.real.toLong()}" else "%.4f".format(r2.real).trimEnd('0').trimEnd('.')
                        } else {
                            val sign = if (r2.imag >= 0) "+" else "-"
                            val imVal = kotlin.math.abs(r2.imag)
                            val imStr = if (imVal == 1.0) "i" else if (imVal == imVal.toLong().toDouble()) "${imVal.toLong()}i" else "%.4fi".format(imVal)
                            "${if (r2.real == 0.0) "" else "%.4f ".format(r2.real)}$sign$imStr"
                        }
                        val vx = -b / (2.0 * a)
                        val vy = c - (b * b) / (4.0 * a)
                        val vxStr = if (vx == vx.toLong().toDouble()) "${vx.toLong()}" else "%.3f".format(vx)
                        val vyStr = if (vy == vy.toLong().toDouble()) "${vy.toLong()}" else "%.3f".format(vy)
                        curr.copy(eqnResultDisplay = "x₁=$r1Str, x₂=$r2Str\nMin/Max: ($vxStr, $vyStr)")
                    }
                    "POLY3" -> {
                        val a = curr.eqnCoeffs.getOrNull(0)?.toDoubleOrNull() ?: 1.0
                        val b = curr.eqnCoeffs.getOrNull(1)?.toDoubleOrNull() ?: -6.0
                        val c = curr.eqnCoeffs.getOrNull(2)?.toDoubleOrNull() ?: 11.0
                        val d = curr.eqnCoeffs.getOrNull(3)?.toDoubleOrNull() ?: -6.0
                        val roots = com.example.engine.EquationSolver.solveCubic(a, b, c, d)
                        val resStr = roots.mapIndexed { idx, root ->
                            val rStr = if (root == root.toLong().toDouble()) "${root.toLong()}" else "%.4f".format(root).trimEnd('0').trimEnd('.')
                            "x${idx + 1}=$rStr"
                        }.joinToString(", ")
                        curr.copy(eqnResultDisplay = resStr)
                    }
                    else -> { // SIMUL2
                        val a1 = curr.eqnCoeffs.getOrNull(0)?.toDoubleOrNull() ?: 1.0
                        val b1 = curr.eqnCoeffs.getOrNull(1)?.toDoubleOrNull() ?: 1.0
                        val c1 = curr.eqnCoeffs.getOrNull(2)?.toDoubleOrNull() ?: 2.0
                        val a2 = curr.eqnCoeffs.getOrNull(3)?.toDoubleOrNull() ?: 1.0
                        val b2 = curr.eqnCoeffs.getOrNull(4)?.toDoubleOrNull() ?: -1.0
                        val c2 = curr.eqnCoeffs.getOrNull(5)?.toDoubleOrNull() ?: 0.0

                        val res = com.example.engine.EquationSolver.solveLinear2(a1, b1, c1, a2, b2, c2)
                        val xStr = if (res.x == res.x.toLong().toDouble()) "${res.x.toLong()}" else "%.3f".format(res.x)
                        val yStr = if (res.y == res.y.toLong().toDouble()) "${res.y.toLong()}" else "%.3f".format(res.y)
                        curr.copy(eqnResultDisplay = "X=$xStr, Y=$yStr")
                    }
                }
            } catch (e: Exception) {
                curr.copy(eqnResultDisplay = "No Solution")
            }
        }
    }


    fun toggleSetup(show: Boolean) {
        _state.update { it.copy(showSetupDialog = show, isShiftActive = false) }
    }

    fun toggleOptn(show: Boolean = true) {
        _state.update { it.copy(showCalcOptn = show, calcOptnSubmenu = "", showOptnDialog = false, isShiftActive = false) }
    }

    fun setCalcOptnSubmenu(submenu: String) {
        _state.update { it.copy(calcOptnSubmenu = submenu) }
    }

    fun insertSnippetAndCloseOptn(snippet: String) {
        if (snippet == "▶r∠θ" || snippet == "▶a+bi") {
            cycleSdFormat()
            _state.update { it.copy(showCalcOptn = false, calcOptnSubmenu = "") }
            return
        }
        _state.update { curr ->
            val isOperator = snippet in listOf("+", "-", "×", "÷", "^", "²", "³", "⁻¹", "!", "°", "r", "g")
            val startingFormula = if (curr.isEvaluated && !isOperator) "" else curr.formula
            val startingCursor = if (curr.isEvaluated && !isOperator) 0 else curr.cursorIndex

            val left = startingFormula.substring(0, startingCursor)
            val right = startingFormula.substring(startingCursor)
            val newFormula = left + snippet + right
            val newCursor = startingCursor + snippet.length

            curr.copy(
                formula = newFormula,
                cursorIndex = newCursor,
                isEvaluated = false,
                isError = false,
                errorMessage = "",
                isShiftActive = false,
                isAlphaActive = false,
                showCalcOptn = false,
                calcOptnSubmenu = "",
                showOptnDialog = false
            )
        }
    }

    fun handleCalcOptnInput(text: String) {
        val curr = _state.value
        val mode = curr.currentMode
        when (mode) {
            2 -> { // Complex Mode
                when (text) {
                    "1" -> insertSnippetAndCloseOptn("i")
                    "2" -> insertSnippetAndCloseOptn("conjg(")
                    "3" -> insertSnippetAndCloseOptn("reP(")
                    "4" -> insertSnippetAndCloseOptn("imP(")
                    "5" -> insertSnippetAndCloseOptn("arg(")
                    "6" -> {
                        cycleSdFormat()
                        _state.update { it.copy(showCalcOptn = false, calcOptnSubmenu = "") }
                    }
                    "7" -> {
                        cycleSdFormat()
                        _state.update { it.copy(showCalcOptn = false, calcOptnSubmenu = "") }
                    }
                    "8" -> insertSnippetAndCloseOptn("∠")
                }
            }
            3 -> { // Base-N Mode
                when (text) {
                    "1" -> insertSnippetAndCloseOptn(" and ")
                    "2" -> insertSnippetAndCloseOptn(" or ")
                    "3" -> insertSnippetAndCloseOptn(" xor ")
                    "4" -> insertSnippetAndCloseOptn(" xnor ")
                    "5" -> insertSnippetAndCloseOptn("not(")
                    "6" -> insertSnippetAndCloseOptn("Neg(")
                }
            }
            6 -> { // Statistics Mode
                when (text) {
                    "1" -> insertSnippetAndCloseOptn("StatType")
                    "2" -> insertSnippetAndCloseOptn("1-Var")
                    "3" -> insertSnippetAndCloseOptn("Σx")
                }
            }
            9 -> { // Table Mode
                when (text) {
                    "1" -> insertSnippetAndCloseOptn("TableRange")
                    "2" -> insertSnippetAndCloseOptn("g(x)=")
                }
            }
            10 -> { // Equation Mode: 1:Simul, 2:Poly Degree 2 (Quadratic), 3:Poly Degree 3 (Cubic)
                when (text) {
                    "1" -> {
                        _state.update {
                            it.copy(
                                eqnType = "SIMUL2",
                                eqnCoeffs = listOf("1", "1", "2", "1", "-1", "0"),
                                eqnActiveIndex = 0,
                                showCalcOptn = false,
                                calcOptnSubmenu = ""
                            )
                        }
                    }
                    "2" -> {
                        _state.update {
                            it.copy(
                                eqnType = "POLY2",
                                eqnCoeffs = listOf("1", "-5", "6"),
                                eqnActiveIndex = 0,
                                showCalcOptn = false,
                                calcOptnSubmenu = ""
                            )
                        }
                    }
                    "3" -> {
                        _state.update {
                            it.copy(
                                eqnType = "POLY3",
                                eqnCoeffs = listOf("1", "-6", "11", "-6"),
                                eqnActiveIndex = 0,
                                showCalcOptn = false,
                                calcOptnSubmenu = ""
                            )
                        }
                    }
                }
            }
            else -> { // Mode 1 (Calculate) and default
                when (curr.calcOptnSubmenu) {
                    "" -> {
                        when (text) {
                            "1" -> _state.update { it.copy(calcOptnSubmenu = "HYP") }
                            "2" -> _state.update { it.copy(calcOptnSubmenu = "OTHER") }
                            "3" -> _state.update { it.copy(calcOptnSubmenu = "ANGLE") }
                            "4" -> _state.update { it.copy(calcOptnSubmenu = "ENG") }
                        }
                    }
                    "HYP" -> {
                        when (text) {
                            "1" -> insertSnippetAndCloseOptn("sinh(")
                            "2" -> insertSnippetAndCloseOptn("cosh(")
                            "3" -> insertSnippetAndCloseOptn("tanh(")
                            "4" -> insertSnippetAndCloseOptn("sinh⁻¹(")
                            "5" -> insertSnippetAndCloseOptn("cosh⁻¹(")
                            "6" -> insertSnippetAndCloseOptn("tanh⁻¹(")
                        }
                    }
                    "OTHER" -> {
                        when (text) {
                            "1" -> insertSnippetAndCloseOptn("abs(")
                            "2" -> insertSnippetAndCloseOptn("Ran#")
                            "3" -> insertSnippetAndCloseOptn("RanInt#(1,6)")
                            "4" -> insertSnippetAndCloseOptn("gcd(")
                            "5" -> insertSnippetAndCloseOptn("lcm(")
                            "6" -> insertSnippetAndCloseOptn("int(")
                        }
                    }
                    "ANGLE" -> {
                        when (text) {
                            "1" -> insertSnippetAndCloseOptn("°")
                            "2" -> insertSnippetAndCloseOptn("r")
                            "3" -> insertSnippetAndCloseOptn("g")
                        }
                    }
                    "ENG" -> {
                        when (text) {
                            "1" -> insertSnippetAndCloseOptn("m")
                            "2" -> insertSnippetAndCloseOptn("μ")
                            "3" -> insertSnippetAndCloseOptn("n")
                            "4" -> insertSnippetAndCloseOptn("p")
                            "5" -> insertSnippetAndCloseOptn("k")
                            "6" -> insertSnippetAndCloseOptn("M")
                            "7" -> insertSnippetAndCloseOptn("G")
                            "8" -> insertSnippetAndCloseOptn("T")
                        }
                    }
                }
            }
        }
    }

    fun toggleQr(show: Boolean) {
        _state.update { it.copy(showQrDialog = show, isShiftActive = false) }
    }

    fun toggleMatrixDialog(show: Boolean) {
        _state.update { it.copy(showMatrixDialog = show, isShiftActive = false) }
    }

    fun toggleVectorDialog(show: Boolean) {
        _state.update { it.copy(showVectorDialog = show, isShiftActive = false) }
    }

    fun toggleEquationDialog(show: Boolean) {
        _state.update { it.copy(showEquationDialog = show, isShiftActive = false) }
    }

    fun toggleStatisticsDialog(show: Boolean) {
        _state.update { it.copy(showStatisticsDialog = show, isShiftActive = false) }
    }

    fun toggleTableDialog(show: Boolean) {
        _state.update { it.copy(showTableDialog = show, isShiftActive = false) }
    }

    fun toggleDistributionDialog(show: Boolean) {
        _state.update { it.copy(showDistributionDialog = show, isShiftActive = false) }
    }

    fun toggleRatioDialog(show: Boolean) {
        _state.update { it.copy(showRatioDialog = show, isShiftActive = false) }
    }

    fun toggleMenu(show: Boolean) {
        toggleModeMenu(show)
    }

    fun toggleVariablesDialog(show: Boolean) {
        _state.update { it.copy(showVariablesDialog = show) }
    }

    fun toggleConstDialog(show: Boolean) {
        _state.update { it.copy(showConstDialog = show, isShiftActive = false) }
    }

    fun toggleConvDialog(show: Boolean) {
        _state.update { it.copy(showConvDialog = show, isShiftActive = false) }
    }

    fun insertConstant(value: String) {
        toggleConstDialog(false)
        onCharInput(value)
    }

    fun insertConversionResult(value: String) {
        toggleConvDialog(false)
        onCharInput(value)
    }

    fun selectMode(mode: Int) {
        val (modeName, status) = when (mode) {
            1 -> "Calculate" to ""
            2 -> "Complex" to "CMPLX"
            3 -> "Base-N" to "DEC"
            4 -> "Matrix" to "MAT"
            5 -> "Vector" to "VCT"
            6 -> "Statistics" to "STAT"
            7 -> "Distribution" to "DIST"
            8 -> "Spreadsheet" to "SPREAD"
            9 -> "Table" to "TABLE"
            10 -> "Equation/Func" to "EQN"
            11 -> "Inequality" to "INEQ"
            12 -> "Ratio" to "RATIO"
            else -> "Calculate" to ""
        }
        _state.update {
            it.copy(
                currentMode = mode,
                currentModeName = modeName,
                showModeMenuDialog = false,
                baseMode = if (mode == 3) BaseMode.DEC else BaseMode.COMP,
                showMatrixDialog = (mode == 4),
                showVectorDialog = (mode == 5),
                showStatisticsDialog = (mode == 6),
                showDistributionDialog = (mode == 7),
                showSpreadsheetDialog = (mode == 8),
                showTableDialog = (mode == 9),
                showEquationDialog = (mode == 10),
                showInequalityDialog = (mode == 11),
                showRatioDialog = (mode == 12),
                statusMessage = status
            )
        }
    }

    private fun setAngleUnit(unit: AngleUnit) {
        _state.update { curr ->
            val updated = curr.copy(angleUnit = unit, showSetupDialog = false)
            if (updated.formula.isNotEmpty()) {
                reEvaluate(updated)
            } else {
                updated
            }
        }
    }

    private fun setBaseMode(mode: BaseMode) {
        _state.update {
            it.copy(
                baseMode = mode,
                formula = "",
                cursorIndex = 0,
                resultDisplay = "0",
                numericResult = 0.0,
                showSetupDialog = false,
                showModeMenuDialog = false,
                statusMessage = if (mode != BaseMode.COMP) mode.symbol else ""
            )
        }
    }

    private fun allClear() {
        _state.update {
            it.copy(
                formula = "",
                cursorIndex = 0,
                resultDisplay = "0",
                numericResult = 0.0,
                isEvaluated = true,
                isError = false,
                errorMessage = "",
                isShiftActive = false,
                isAlphaActive = false,
                isStoActive = false,
                isRclActive = false,
                engOffset = 0,
                sdCycleIndex = 0,
                statusMessage = ""
            )
        }
    }

    private fun resetCalculator() {
        allClear()
    }

    private fun deleteAtCursor() {
        _state.update { curr ->
            if (curr.cursorIndex <= 0 || curr.formula.isEmpty()) {
                return@update curr
            }

            val formula = curr.formula
            val pos = curr.cursorIndex

            // If cursor is right after "log(" and followed by ",)", delete the entire "log(,)"
            if (pos >= 4 && formula.substring(pos - 4, pos) == "log(" && formula.substring(pos).startsWith(",)")) {
                val newFormula = formula.substring(0, pos - 4) + formula.substring(pos + 2)
                return@update curr.copy(
                    formula = newFormula,
                    cursorIndex = pos - 4,
                    isEvaluated = false,
                    isError = false,
                    errorMessage = ""
                )
            }

            // If cursor is inside empty integral template "∫(|,,)" -> delete entire "∫(,,)"
            if (pos >= 2 && formula.substring(pos - 2, pos) == "∫(" && formula.substring(pos).startsWith(",,)")) {
                val newFormula = formula.substring(0, pos - 2) + formula.substring(pos + 3)
                return@update curr.copy(
                    formula = newFormula,
                    cursorIndex = pos - 2,
                    isEvaluated = false,
                    isError = false,
                    errorMessage = ""
                )
            }

            // If cursor is inside empty derivative template "d/dx(|,)" -> delete entire "d/dx(,)"
            if (pos >= 5 && formula.substring(pos - 5, pos) == "d/dx(" && formula.substring(pos).startsWith(",)")) {
                val newFormula = formula.substring(0, pos - 5) + formula.substring(pos + 2)
                return@update curr.copy(
                    formula = newFormula,
                    cursorIndex = pos - 5,
                    isEvaluated = false,
                    isError = false,
                    errorMessage = ""
                )
            }

            // If cursor is right after comma in templates, jump cursor before comma without deleting comma delimiter
            if (formula.getOrNull(pos - 1) == ',') {
                val logIdx = formula.lastIndexOf("log(", pos)
                val intIdx = formula.lastIndexOf("∫(", pos)
                val diffIdx = formula.lastIndexOf("d/dx(", pos)
                if (logIdx >= 0 || intIdx >= 0 || diffIdx >= 0) {
                    return@update curr.copy(
                        cursorIndex = pos - 1,
                        isEvaluated = false,
                        isError = false,
                        errorMessage = ""
                    )
                }
            }

            // Detect multi-char tokens ending at cursor
            val multiTokens = listOf(
                "sin⁻¹(", "cos⁻¹(", "tan⁻¹(",
                "sin(", "cos(", "tan(",
                "sinh(", "cosh(", "tanh(",
                "log(,)", "log(", "ln(", "∫(,,)", "∫(", "d/dx(,)", "d/dx(", "√(", "sqrt(", "cbrt(", "∛(", "³√(", "10^(", "e^(",
                "×10^", "Ans"
            )

            var deleteCount = 1
            for (tok in multiTokens) {
                if (pos >= tok.length && formula.substring(pos - tok.length, pos) == tok) {
                    deleteCount = tok.length
                    break
                }
            }

            val newFormula = formula.substring(0, pos - deleteCount) + formula.substring(pos)
            val newCursor = (pos - deleteCount).coerceAtLeast(0)

            curr.copy(
                formula = newFormula,
                cursorIndex = newCursor,
                isEvaluated = false,
                isError = false,
                errorMessage = ""
            )
        }
    }

    private fun moveCursor(delta: Int) {
        _state.update { curr ->
            val formula = curr.formula
            var newPos = curr.cursorIndex + delta

            // Smart box jumping across comma separators in integral, derivative, log
            if (delta > 0 && curr.cursorIndex < formula.length) {
                if (formula[curr.cursorIndex] == ',') {
                    newPos = (curr.cursorIndex + 1).coerceAtMost(formula.length)
                }
            } else if (delta < 0 && curr.cursorIndex > 0) {
                if (formula[curr.cursorIndex - 1] == ',') {
                    newPos = (curr.cursorIndex - 1).coerceAtLeast(0)
                }
            }

            val clampedPos = newPos.coerceIn(0, formula.length)
            curr.copy(cursorIndex = clampedPos)
        }
    }

    fun insertFraction() {
        _state.update { curr ->
            val startingFormula = if (curr.isEvaluated) "" else curr.formula
            val startingCursor = if (curr.isEvaluated) 0 else curr.cursorIndex

            if (startingFormula.isEmpty()) {
                // Empty formula: create vertical fraction template "/" with cursor in numerator (index 0)
                curr.copy(
                    formula = "/",
                    cursorIndex = 0,
                    isEvaluated = false,
                    isError = false,
                    errorMessage = ""
                )
            } else {
                // Insert "/" at cursor position and place cursor into denominator!
                val left = startingFormula.substring(0, startingCursor)
                val right = startingFormula.substring(startingCursor)
                val newFormula = "$left/$right"
                curr.copy(
                    formula = newFormula,
                    cursorIndex = startingCursor + 1,
                    isEvaluated = false,
                    isError = false,
                    errorMessage = ""
                )
            }
        }
    }

    fun insertLogBase() {
        _state.update { curr ->
            val startingFormula = if (curr.isEvaluated) "" else curr.formula
            val startingCursor = if (curr.isEvaluated) 0 else curr.cursorIndex

            val left = startingFormula.substring(0, startingCursor)
            val right = startingFormula.substring(startingCursor)
            val newFormula = left + "log(,)" + right
            val newCursor = startingCursor + 4 // Placed inside base

            curr.copy(
                formula = newFormula,
                cursorIndex = newCursor,
                isEvaluated = false,
                isError = false,
                errorMessage = "",
                isShiftActive = false,
                isAlphaActive = false,
                statusMessage = ""
            )
        }
    }

    fun insertIntegral() {
        _state.update { curr ->
            val startingFormula = if (curr.isEvaluated) "" else curr.formula
            val startingCursor = if (curr.isEvaluated) 0 else curr.cursorIndex

            val left = startingFormula.substring(0, startingCursor)
            val right = startingFormula.substring(startingCursor)
            val newFormula = left + "∫(,,)" + right
            val newCursor = startingCursor + 2 // Placed inside integrand function box

            curr.copy(
                formula = newFormula,
                cursorIndex = newCursor,
                isEvaluated = false,
                isError = false,
                errorMessage = "",
                isShiftActive = false,
                isAlphaActive = false,
                statusMessage = ""
            )
        }
    }

    fun insertDerivative() {
        _state.update { curr ->
            val startingFormula = if (curr.isEvaluated) "" else curr.formula
            val startingCursor = if (curr.isEvaluated) 0 else curr.cursorIndex

            val left = startingFormula.substring(0, startingCursor)
            val right = startingFormula.substring(startingCursor)
            val newFormula = left + "d/dx(,)" + right
            val newCursor = startingCursor + 5 // Placed inside function expression box

            curr.copy(
                formula = newFormula,
                cursorIndex = newCursor,
                isEvaluated = false,
                isError = false,
                errorMessage = "",
                isShiftActive = false,
                isAlphaActive = false,
                statusMessage = ""
            )
        }
    }

    private fun handleCursorVertical(direction: Int) {
        _state.update { curr ->
            val formula = curr.formula

            // 1. If formula contains integral: "∫(intFunc,lowLimit,upLimit)"
            val intIdx = formula.indexOf("∫(")
            if (intIdx >= 0 && !curr.isEvaluated) {
                val comma1 = formula.indexOf(',', intIdx + 2)
                val comma2 = if (comma1 >= 0) formula.indexOf(',', comma1 + 1) else -1
                val closeIdx = formula.indexOf(')', intIdx + 2)
                val endIdx = if (closeIdx >= 0) closeIdx else formula.length

                if (comma1 in (intIdx + 2) until endIdx && comma2 in (comma1 + 1) until endIdx) {
                    if (direction < 0) {
                        // UP:
                        if (curr.cursorIndex <= comma1) {
                            // From intFunc -> go to upLimit
                            return@update curr.copy(cursorIndex = comma2 + 1)
                        } else if (curr.cursorIndex in (comma1 + 1)..comma2) {
                            // From lowLimit -> go to upLimit
                            return@update curr.copy(cursorIndex = comma2 + 1)
                        }
                    } else {
                        // DOWN:
                        if (curr.cursorIndex <= comma1) {
                            // From intFunc -> go to lowLimit
                            return@update curr.copy(cursorIndex = comma1 + 1)
                        } else if (curr.cursorIndex > comma2) {
                            // From upLimit -> go to lowLimit
                            return@update curr.copy(cursorIndex = comma1 + 1)
                        }
                    }
                }
            }

            // 2. If formula contains derivative: "d/dx(diffFunc,xPoint)"
            val diffIdx = formula.indexOf("d/dx(")
            if (diffIdx >= 0 && !curr.isEvaluated) {
                val comma = formula.indexOf(',', diffIdx + 5)
                val closeIdx = formula.indexOf(')', diffIdx + 5)
                val endIdx = if (closeIdx >= 0) closeIdx else formula.length
                if (comma in (diffIdx + 5) until endIdx) {
                    if (direction < 0) {
                        // UP: move from xPoint to diffFunc
                        if (curr.cursorIndex > comma) {
                            return@update curr.copy(cursorIndex = comma)
                        }
                    } else {
                        // DOWN: move from diffFunc to xPoint
                        if (curr.cursorIndex <= comma) {
                            return@update curr.copy(cursorIndex = comma + 1)
                        }
                    }
                }
            }

            // 3. If formula contains a fraction and is currently being edited
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
            if (slashIndex >= 0 && !curr.isEvaluated) {
                if (direction < 0) {
                    // UP button pressed: Move focus to Numerator
                    if (curr.cursorIndex > slashIndex) {
                        return@update curr.copy(cursorIndex = slashIndex)
                    }
                } else {
                    // DOWN button pressed: Move focus to Denominator
                    if (curr.cursorIndex <= slashIndex) {
                        val denPos = if (curr.cursorIndex == slashIndex) {
                            (slashIndex + 1).coerceAtMost(formula.length)
                        } else {
                            formula.length
                        }
                        return@update curr.copy(cursorIndex = denPos)
                    }
                }
            }

            // 4. If formula contains log with base: "log(base,value)"
            val logIdx = formula.indexOf("log(")
            if (logIdx >= 0 && !curr.isEvaluated) {
                val commaIdx = formula.indexOf(',', logIdx + 4)
                val closeIdx = formula.indexOf(')', logIdx + 4)
                val endIdx = if (closeIdx >= 0) closeIdx else formula.length
                if (commaIdx in (logIdx + 4) until endIdx) {
                    if (direction < 0) {
                        // UP: Move focus from Value to Base
                        if (curr.cursorIndex > commaIdx) {
                            return@update curr.copy(cursorIndex = commaIdx)
                        }
                    } else {
                        // DOWN: Move focus from Base to Value
                        if (curr.cursorIndex <= commaIdx) {
                            return@update curr.copy(cursorIndex = commaIdx + 1)
                        }
                    }
                }
            }

            // Fallback: Replay history if history exists
            if (curr.history.isNotEmpty()) {
                val newIndex = if (curr.historyIndex == -1) {
                    if (direction < 0) curr.history.size - 1 else 0
                } else {
                    (curr.historyIndex + direction).coerceIn(0, curr.history.size - 1)
                }

                val item = curr.history[newIndex]
                return@update curr.copy(
                    formula = item.formula,
                    cursorIndex = item.formula.length,
                    resultDisplay = item.result,
                    numericResult = item.numericValue,
                    historyIndex = newIndex,
                    isEvaluated = true,
                    isError = false,
                    errorMessage = ""
                )
            }

            curr
        }
    }

    private fun replayHistory(direction: Int) {
        _state.update { curr ->
            if (curr.history.isEmpty()) return@update curr

            val newIndex = if (curr.historyIndex == -1) {
                if (direction < 0) curr.history.size - 1 else 0
            } else {
                (curr.historyIndex + direction).coerceIn(0, curr.history.size - 1)
            }

            val item = curr.history[newIndex]
            curr.copy(
                formula = item.formula,
                cursorIndex = item.formula.length,
                resultDisplay = item.result,
                numericResult = item.numericValue,
                historyIndex = newIndex,
                isEvaluated = true,
                isError = false,
                errorMessage = ""
            )
        }
    }

    private fun calculate() {
        val curr = _state.value
        if (curr.formula.isEmpty()) return

        val result = evaluator.evaluate(
            expression = curr.formula,
            angleUnit = curr.angleUnit,
            baseMode = curr.baseMode,
            variables = curr.variables,
            ans = curr.ans
        )

        when (result) {
            is MathEvaluator.EvalResult.Success -> {
                val newHistory = curr.history + HistoryItem(
                    formula = curr.formula,
                    result = result.standardFormatted,
                    numericValue = result.value
                )
                _state.update {
                    it.copy(
                        resultDisplay = result.standardFormatted,
                        numericResult = result.value,
                        ans = result.value,
                        isEvaluated = true,
                        isError = false,
                        errorMessage = "",
                        history = newHistory,
                        historyIndex = -1,
                        sdCycleIndex = 0,
                        engOffset = 0,
                        isShiftActive = false,
                        isAlphaActive = false
                    )
                }
            }
            is MathEvaluator.EvalResult.Error -> {
                _state.update {
                    it.copy(
                        isError = true,
                        errorMessage = result.message,
                        resultDisplay = result.message,
                        isEvaluated = true,
                        isShiftActive = false,
                        isAlphaActive = false
                    )
                }
            }
        }
    }

    private fun reEvaluate(curr: CalculatorState): CalculatorState {
        val res = evaluator.evaluate(
            expression = curr.formula,
            angleUnit = curr.angleUnit,
            baseMode = curr.baseMode,
            variables = curr.variables,
            ans = curr.ans
        )
        return when (res) {
            is MathEvaluator.EvalResult.Success -> curr.copy(
                resultDisplay = res.standardFormatted,
                numericResult = res.value
            )
            is MathEvaluator.EvalResult.Error -> curr
        }
    }

    private fun cycleSdFormat() {
        val curr = _state.value
        val num = curr.numericResult ?: return
        val options = MathEvaluator.generateSdOptions(num, curr.angleUnit)
        if (options.isEmpty()) return

        val nextIndex = (curr.sdCycleIndex + 1) % options.size
        _state.update {
            it.copy(
                sdCycleIndex = nextIndex,
                resultDisplay = options[nextIndex],
                isShiftActive = false,
                isAlphaActive = false
            )
        }
    }

    private fun handleEng() {
        val curr = _state.value
        val num = curr.numericResult ?: return
        val newOffset = curr.engOffset + 1
        val engText = MathEvaluator.formatEngineering(num, newOffset)
        _state.update {
            it.copy(
                engOffset = newOffset,
                resultDisplay = engText,
                isShiftActive = false,
                isAlphaActive = false
            )
        }
    }

    private fun startSto() {
        _state.update {
            it.copy(isStoActive = true, isRclActive = false, statusMessage = "STO [A-F, x, y, M]")
        }
    }

    private fun startRcl() {
        _state.update {
            it.copy(isRclActive = true, isStoActive = false, statusMessage = "RCL [A-F, x, y, M]")
        }
    }

    private fun handleVariable(name: String) {
        val curr = _state.value
        when {
            curr.isStoActive -> {
                // Store current answer/result to variable
                val valToStore = curr.numericResult ?: 0.0
                val updatedVars = curr.variables.toMutableMap()
                updatedVars[name] = valToStore
                _state.update {
                    it.copy(
                        variables = updatedVars,
                        isStoActive = false,
                        isShiftActive = false,
                        isAlphaActive = false,
                        statusMessage = "$name = ${MathEvaluator.formatDecimal(valToStore)}"
                    )
                }
            }
            curr.isRclActive -> {
                // Insert recalled variable value into formula
                val recalled = curr.variables[name] ?: 0.0
                val formatted = MathEvaluator.formatDecimal(recalled)
                onCharInput(formatted)
                _state.update {
                    it.copy(
                        isRclActive = false,
                        isShiftActive = false,
                        isAlphaActive = false,
                        statusMessage = "RCL $name"
                    )
                }
            }
            else -> {
                // Insert variable name into formula
                onCharInput(name)
            }
        }
    }

    private fun handleMPlus(subtract: Boolean) {
        val curr = _state.value
        val valueToAdd = curr.numericResult ?: 0.0
        val effective = if (subtract) -valueToAdd else valueToAdd
        val currentM = curr.variables["M"] ?: 0.0
        val newM = currentM + effective
        val updatedVars = curr.variables.toMutableMap()
        updatedVars["M"] = newM

        _state.update {
            it.copy(
                variables = updatedVars,
                isShiftActive = false,
                statusMessage = if (subtract) "M-" else "M+"
            )
        }
    }
}

sealed class KeyAction {
    object Shift : KeyAction()
    object Alpha : KeyAction()
    object On : KeyAction()
    object Ac : KeyAction()
    object Del : KeyAction()
    object Equals : KeyAction()
    object SdToggle : KeyAction()
    object InsertFraction : KeyAction()
    object InsertLogBase : KeyAction()
    object InsertIntegral : KeyAction()
    object InsertDerivative : KeyAction()
    object CursorLeft : KeyAction()
    object CursorRight : KeyAction()
    object CursorUp : KeyAction()
    object CursorDown : KeyAction()
    object MenuSetup : KeyAction()
    object ModeMenu : KeyAction()
    object SetupMenu : KeyAction()
    object OptnMenu : KeyAction()
    object QrCode : KeyAction()
    object Const : KeyAction()
    object Conv : KeyAction()
    object Eng : KeyAction()
    object Sto : KeyAction()
    object Rcl : KeyAction()
    object MPlus : KeyAction()
    object MMinus : KeyAction()
    data class Variable(val name: String) : KeyAction()
    data class SetAngleUnit(val unit: AngleUnit) : KeyAction()
    data class SetBaseMode(val mode: BaseMode) : KeyAction()
    data class SelectMode(val mode: Int) : KeyAction()
    object OpenMatrixDialog : KeyAction()
    object OpenVectorDialog : KeyAction()
    object OpenEquationDialog : KeyAction()
    object OpenStatisticsDialog : KeyAction()
    object OpenTableDialog : KeyAction()
    object OpenDistributionDialog : KeyAction()
    object OpenRatioDialog : KeyAction()
    object CalcPrompt : KeyAction()
    object Solve : KeyAction()
    object ToggleHscGuide : KeyAction()
}
