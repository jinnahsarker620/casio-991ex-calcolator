package com.example.model

enum class LcdMode {
    CALC,
    MENU,
    MATRIX,
    VECTOR,
    EQUATION
}

enum class MatrixStage {
    MDEF, // Define Matrix: 1:MatA 2:MatB 3:MatC 4:MatD
    ROW,  // Number of Rows? Select 1-4
    COL,  // Number of Columns? Select 1-4
    INP,  // Interactive Grid Input: [0] 0 ...
    CALC  // Matrix Calculations: Expr, Det, Trn, A+B, A-B, A*B
}

enum class VectorStage {
    MDEF, // Define Vector: 1:VctA 2:VctB 3:VctC 4:VctD
    VDIM, // Vector Dimension? Select 2 or 3
    INP,  // Interactive Component Input: x1, x2, x3
    CALC  // Vector Calculations: Expr, Dot, Cross, Angle, UnitV, Magnitude
}

data class HistoryItem(
    val formula: String,
    val result: String,
    val numericValue: Double?
)

data class CalculatorState(
    val formula: String = "",
    val cursorIndex: Int = 0,
    val resultDisplay: String = "0",
    val numericResult: Double? = 0.0,
    val isEvaluated: Boolean = true,
    val isShiftActive: Boolean = false,
    val isAlphaActive: Boolean = false,
    val isStoActive: Boolean = false,
    val isRclActive: Boolean = false,
    val angleUnit: AngleUnit = AngleUnit.DEG,
    val baseMode: BaseMode = BaseMode.COMP,
    val currentMode: Int = 1, // 1: Calculate, 2: Complex, 3: Base-N, 4: Matrix, 5: Vector, 6: Stat, 7: Table, 8: Eqn, 9: Verify
    val currentModeName: String = "Calculate",
    val lcdMode: LcdMode = LcdMode.CALC, // Inside-LCD screen mode: CALC, MENU, MATRIX, EQUATION
    val selectedMenuItem: Int = 4, // Highlighted option in MAIN MENU (1..9), defaults to 4:Matrix
    // Inside-LCD Matrix Mode state: MatA, MatB, MatC, MatD
    val matrixStore: Map<String, List<List<String>>> = mapOf(
        "MatA" to listOf(listOf("1", "2"), listOf("3", "4")),
        "MatB" to listOf(listOf("2", "0"), listOf("1", "3")),
        "MatC" to listOf(listOf("1", "0"), listOf("0", "1")),
        "MatD" to listOf(listOf("0", "0"), listOf("0", "0"))
    ),
    val currentMatrixName: String = "MatA",
    val matrixRows: Int = 2,
    val matrixCols: Int = 2,
    val matrixStage: MatrixStage = MatrixStage.INP,
    val matrixDimSelection: Int = 3,
    val matrixCalcExpr: String = "",
    val matrixCalcResult: String = "",
    val showMatrixOptn: Boolean = false,
    val matrixA: List<List<String>> = listOf(listOf("1", "2"), listOf("3", "4")),
    val matrixActiveRow: Int = 0,
    val matrixActiveCol: Int = 0,
    val matrixDetDisplay: String = "Det(MatA) = -2",
    // Inside-LCD Vector Mode state: VctA, VctB, VctC, VctD
    val vectorStore: Map<String, List<String>> = mapOf(
        "VctA" to listOf("1", "2", "3"),
        "VctB" to listOf("2", "0", "1"),
        "VctC" to listOf("1", "-1", "2"),
        "VctD" to listOf("0", "0", "0")
    ),
    val currentVectorName: String = "VctA",
    val vectorDim: Int = 3,
    val vectorStage: VectorStage = VectorStage.MDEF,
    val vectorDimSelection: Int = 3,
    val vectorActiveIndex: Int = 0,
    val vectorCalcExpr: String = "VctA•VctB",
    val vectorCalcResult: String = "",
    val showVectorOptn: Boolean = false,
    // Inside-LCD Equation Mode state: a1 X + b1 Y = c1, a2 X + b2 Y = c2
    val eqnCoeffs: List<String> = listOf("1", "1", "2", "1", "-1", "0"),
    val eqnActiveIndex: Int = 0,
    val eqnType: String = "SIMUL2", // "SIMUL2", "POLY2", "POLY3"
    val eqnResultDisplay: String = "X=1, Y=1",
    val sdCycleIndex: Int = 0,
    val variables: Map<String, Double> = mapOf(
        "A" to 0.0, "B" to 0.0, "C" to 0.0,
        "D" to 0.0, "E" to 0.0, "F" to 0.0,
        "x" to 0.0, "y" to 0.0, "M" to 0.0
    ),
    val ans: Double = 0.0,
    val history: List<HistoryItem> = emptyList(),
    val historyIndex: Int = -1,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val engOffset: Int = 0,
    val showMenuDialog: Boolean = false,
    val showVariablesDialog: Boolean = false,
    val showModeMenuDialog: Boolean = false,
    val showSetupDialog: Boolean = false,
    val showOptnDialog: Boolean = false,
    val showCalcOptn: Boolean = false,
    val calcOptnSubmenu: String = "",
    val isCalcPromptActive: Boolean = false,
    val calcPromptVar: String = "x",
    val calcPromptInput: String = "",
    val showHscMathGuideDialog: Boolean = false,
    val showQrDialog: Boolean = false,
    val showMatrixDialog: Boolean = false,
    val showVectorDialog: Boolean = false,
    val showEquationDialog: Boolean = false,
    val showStatisticsDialog: Boolean = false,
    val showTableDialog: Boolean = false,
    val showDistributionDialog: Boolean = false,
    val showInequalityDialog: Boolean = false,
    val showRatioDialog: Boolean = false,
    val showSpreadsheetDialog: Boolean = false,
    val showComplexDialog: Boolean = false,
    val showConstDialog: Boolean = false,
    val showConvDialog: Boolean = false,
    val statusMessage: String = ""
)

