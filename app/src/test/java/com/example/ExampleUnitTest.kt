package com.example

import com.example.engine.MathEvaluator
import com.example.model.AngleUnit
import com.example.model.BaseMode
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class ExampleUnitTest {

    private val evaluator = MathEvaluator()

    @Test
    fun testTrigonometryInDegreeMode() {
        // sin(30) in DEG = 0.5
        val sin30 = evaluator.evaluate("sin(30)", AngleUnit.DEG)
        assertTrue(sin30 is MathEvaluator.EvalResult.Success)
        assertEquals(0.5, (sin30 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // cos(60) in DEG = 0.5
        val cos60 = evaluator.evaluate("cos(60)", AngleUnit.DEG)
        assertTrue(cos60 is MathEvaluator.EvalResult.Success)
        assertEquals(0.5, (cos60 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // tan(45) in DEG = 1.0
        val tan45 = evaluator.evaluate("tan(45)", AngleUnit.DEG)
        assertTrue(tan45 is MathEvaluator.EvalResult.Success)
        assertEquals(1.0, (tan45 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // asin(0.5) in DEG = 30°
        val asinVal = evaluator.evaluate("sin⁻¹(0.5)", AngleUnit.DEG)
        assertTrue(asinVal is MathEvaluator.EvalResult.Success)
        assertEquals(30.0, (asinVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // acos(0.5) in DEG = 60°
        val acosVal = evaluator.evaluate("cos⁻¹(0.5)", AngleUnit.DEG)
        assertTrue(acosVal is MathEvaluator.EvalResult.Success)
        assertEquals(60.0, (acosVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // atan(1.0) in DEG = 45°
        val atanVal = evaluator.evaluate("tan⁻¹(1)", AngleUnit.DEG)
        assertTrue(atanVal is MathEvaluator.EvalResult.Success)
        assertEquals(45.0, (atanVal as MathEvaluator.EvalResult.Success).value, 1e-6)
    }

    @Test
    fun testPowersRootsAndFactorial() {
        // sqrt(16) = 4
        val sqrtVal = evaluator.evaluate("sqrt(16)")
        assertTrue(sqrtVal is MathEvaluator.EvalResult.Success)
        assertEquals(4.0, (sqrtVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // cbrt(27) = 3 and ∛(27) = 3
        val cbrtVal = evaluator.evaluate("∛(27)")
        assertTrue(cbrtVal is MathEvaluator.EvalResult.Success)
        assertEquals(3.0, (cbrtVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        val cbrtVal2 = evaluator.evaluate("³√(64)")
        assertTrue(cbrtVal2 is MathEvaluator.EvalResult.Success)
        assertEquals(4.0, (cbrtVal2 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 5² = 25
        val sqVal = evaluator.evaluate("5²")
        assertTrue(sqVal is MathEvaluator.EvalResult.Success)
        assertEquals(25.0, (sqVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 2³ = 8
        val cubeVal = evaluator.evaluate("2³")
        assertTrue(cubeVal is MathEvaluator.EvalResult.Success)
        assertEquals(8.0, (cubeVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 2^4 = 16
        val powVal = evaluator.evaluate("2^4")
        assertTrue(powVal is MathEvaluator.EvalResult.Success)
        assertEquals(16.0, (powVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 4⁻¹ = 0.25
        val invVal = evaluator.evaluate("4⁻¹")
        assertTrue(invVal is MathEvaluator.EvalResult.Success)
        assertEquals(0.25, (invVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 5! = 120
        val factVal = evaluator.evaluate("5!")
        assertTrue(factVal is MathEvaluator.EvalResult.Success)
        assertEquals(120.0, (factVal as MathEvaluator.EvalResult.Success).value, 1e-6)
    }

    @Test
    fun testLogLnAndConstants() {
        // log(1000) = 3
        val logVal = evaluator.evaluate("log(1000)")
        assertTrue(logVal is MathEvaluator.EvalResult.Success)
        assertEquals(3.0, (logVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // ln(e) = 1
        val lnVal = evaluator.evaluate("ln(e)")
        assertTrue(lnVal is MathEvaluator.EvalResult.Success)
        assertEquals(1.0, (lnVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 10^3 = 1000
        val exp10 = evaluator.evaluate("10^(3)")
        assertTrue(exp10 is MathEvaluator.EvalResult.Success)
        assertEquals(1000.0, (exp10 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 2 * π
        val piVal = evaluator.evaluate("2*π")
        assertTrue(piVal is MathEvaluator.EvalResult.Success)
        assertEquals(2 * Math.PI, (piVal as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 50% = 0.5
        val pctVal = evaluator.evaluate("50%")
        assertTrue(pctVal is MathEvaluator.EvalResult.Success)
        assertEquals(0.5, (pctVal as MathEvaluator.EvalResult.Success).value, 1e-6)
    }

    @Test
    fun testPrecedenceAndParentheses() {
        val expr = evaluator.evaluate("2 + 3 * (4 - 1)")
        assertTrue(expr is MathEvaluator.EvalResult.Success)
        assertEquals(11.0, (expr as MathEvaluator.EvalResult.Success).value, 1e-6)
    }

    @Test
    fun testSdOptions() {
        // 0.75 -> 3/4
        val res = evaluator.evaluate("3/4")
        assertTrue(res is MathEvaluator.EvalResult.Success)
        val opts = (res as MathEvaluator.EvalResult.Success).sdOptions
        assertTrue(opts.contains("3/4"))
        assertTrue(opts.contains("0.75"))
    }

    @Test
    fun testScientificConstantsEvaluation() {
        // g = 9.80665
        val gEval = evaluator.evaluate("9.80665 * 2")
        assertTrue(gEval is MathEvaluator.EvalResult.Success)
        assertEquals(19.6133, (gEval as MathEvaluator.EvalResult.Success).value, 1e-4)

        // c = 299792458
        val cEval = evaluator.evaluate("299792458")
        assertTrue(cEval is MathEvaluator.EvalResult.Success)
        assertEquals(299792458.0, (cEval as MathEvaluator.EvalResult.Success).value, 1e-1)
    }

    @Test
    fun testNaturalTextbookDisplayFormulas() {
        // 5³ = 125
        val pow3 = evaluator.evaluate("5³")
        assertTrue(pow3 is MathEvaluator.EvalResult.Success)
        assertEquals(125.0, (pow3 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 5^3 = 125
        val powHat = evaluator.evaluate("5^3")
        assertTrue(powHat is MathEvaluator.EvalResult.Success)
        assertEquals(125.0, (powHat as MathEvaluator.EvalResult.Success).value, 1e-6)

        // 5/3 standard formatted fraction = 5/3
        val fracRes = evaluator.evaluate("5/3")
        assertTrue(fracRes is MathEvaluator.EvalResult.Success)
        assertEquals(1.6666666667, (fracRes as MathEvaluator.EvalResult.Success).value, 1e-4)
        assertEquals("5/3", (fracRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // √(9) = 3
        val sqrtRes = evaluator.evaluate("√(9)")
        assertTrue(sqrtRes is MathEvaluator.EvalResult.Success)
        assertEquals(3.0, (sqrtRes as MathEvaluator.EvalResult.Success).value, 1e-6)

        // ³√(8) = 2
        val cbrtRes = evaluator.evaluate("³√(8)")
        assertTrue(cbrtRes is MathEvaluator.EvalResult.Success)
        assertEquals(2.0, (cbrtRes as MathEvaluator.EvalResult.Success).value, 1e-6)
    }

    @Test
    fun testBareRootsAndFractions() {
        // Bare √333 without parentheses
        val sqrt333 = evaluator.evaluate("√333")
        assertTrue(sqrt333 is MathEvaluator.EvalResult.Success)
        assertEquals(18.24828759, (sqrt333 as MathEvaluator.EvalResult.Success).value, 1e-4)

        // Bare ³√27 without parentheses
        val cbrt27 = evaluator.evaluate("³√27")
        assertTrue(cbrt27 is MathEvaluator.EvalResult.Success)
        assertEquals(3.0, (cbrt27 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // Fractions with leading slash
        val fracEdge = evaluator.evaluate("/3")
        assertTrue(fracEdge is MathEvaluator.EvalResult.Success)
        assertEquals(0.0, (fracEdge as MathEvaluator.EvalResult.Success).value, 1e-6)
    }

    @Test
    fun testArbitraryBaseLogarithm() {
        // log_5(25) = 2
        val log5_25 = evaluator.evaluate("log(5,25)")
        assertTrue(log5_25 is MathEvaluator.EvalResult.Success)
        assertEquals(2.0, (log5_25 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // log_7(49) = 2
        val log7_49 = evaluator.evaluate("log(7,49)")
        assertTrue(log7_49 is MathEvaluator.EvalResult.Success)
        assertEquals(2.0, (log7_49 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // log_2(8) = 3
        val log2_8 = evaluator.evaluate("log(2,8)")
        assertTrue(log2_8 is MathEvaluator.EvalResult.Success)
        assertEquals(3.0, (log2_8 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // log_10(1000) = 3
        val log10_1000 = evaluator.evaluate("log(10,1000)")
        assertTrue(log10_1000 is MathEvaluator.EvalResult.Success)
        assertEquals(3.0, (log10_1000 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // log_5(1) = 0
        val log5_1 = evaluator.evaluate("log(5,1)")
        assertTrue(log5_1 is MathEvaluator.EvalResult.Success)
        assertEquals(0.0, (log5_1 as MathEvaluator.EvalResult.Success).value, 1e-6)

        // log_1(5) -> Math ERROR (base cannot be 1)
        val log1_5 = evaluator.evaluate("log(1,5)")
        assertTrue(log1_5 is MathEvaluator.EvalResult.Error)
        assertEquals("Math ERROR", (log1_5 as MathEvaluator.EvalResult.Error).message)

        // log_0(5) -> Math ERROR
        val log0_5 = evaluator.evaluate("log(0,5)")
        assertTrue(log0_5 is MathEvaluator.EvalResult.Error)
        assertEquals("Math ERROR", (log0_5 as MathEvaluator.EvalResult.Error).message)

        // Incomplete log templates -> Syntax ERROR
        val logEmpty = evaluator.evaluate("log(,)")
        assertTrue(logEmpty is MathEvaluator.EvalResult.Error)

        val logEmptyBase = evaluator.evaluate("log(,25)")
        assertTrue(logEmptyBase is MathEvaluator.EvalResult.Error)

        val logEmptyVal = evaluator.evaluate("log(5,)")
        assertTrue(logEmptyVal is MathEvaluator.EvalResult.Error)
    }

    @Test
    fun testDefiniteIntegration() {
        // ∫(x^2, 0, 1) = 1/3 ≈ 0.3333333333
        val intX2 = evaluator.evaluate("∫(x^2,0,1)")
        assertTrue(intX2 is MathEvaluator.EvalResult.Success)
        assertEquals(1.0 / 3.0, (intX2 as MathEvaluator.EvalResult.Success).value, 1e-4)
        assertTrue(intX2.sdOptions.contains("1/3"))

        // ∫(2x, 0, 3) = 9
        val int2x = evaluator.evaluate("∫(2x,0,3)")
        assertTrue(int2x is MathEvaluator.EvalResult.Success)
        assertEquals(9.0, (int2x as MathEvaluator.EvalResult.Success).value, 1e-4)

        // ∫(3x² + 2x, 1, 2) = (2³ + 2² - (1³ + 1²)) = 8 + 4 - 2 = 10
        val intPoly = evaluator.evaluate("∫(3x² + 2x,1,2)")
        assertTrue(intPoly is MathEvaluator.EvalResult.Success)
        assertEquals(10.0, (intPoly as MathEvaluator.EvalResult.Success).value, 1e-4)

        // Same lower and upper limit: ∫(x^3, 2, 2) = 0
        val intZero = evaluator.evaluate("∫(x^3,2,2)")
        assertTrue(intZero is MathEvaluator.EvalResult.Success)
        assertEquals(0.0, (intZero as MathEvaluator.EvalResult.Success).value, 1e-6)

        // Incomplete integral templates -> Syntax ERROR
        val intEmpty = evaluator.evaluate("∫(,,)")
        assertTrue(intEmpty is MathEvaluator.EvalResult.Error)

        val intNoLimits = evaluator.evaluate("∫(x^2,,)")
        assertTrue(intNoLimits is MathEvaluator.EvalResult.Error)

        val intNoFunc = evaluator.evaluate("∫(,0,1)")
        assertTrue(intNoFunc is MathEvaluator.EvalResult.Error)
    }

    @Test
    fun testNumericalDifferentiation() {
        // d/dx(x^2, 3) = 2 * 3 = 6
        val diffX2 = evaluator.evaluate("d/dx(x^2,3)")
        assertTrue(diffX2 is MathEvaluator.EvalResult.Success)
        assertEquals(6.0, (diffX2 as MathEvaluator.EvalResult.Success).value, 1e-4)

        // d/dx(x³, 2) = 3 * 2² = 12
        val diffX3 = evaluator.evaluate("d/dx(x³,2)")
        assertTrue(diffX3 is MathEvaluator.EvalResult.Success)
        assertEquals(12.0, (diffX3 as MathEvaluator.EvalResult.Success).value, 1e-4)

        // d/dx(5x + 4, 10) = 5
        val diffLinear = evaluator.evaluate("d/dx(5x+4,10)")
        assertTrue(diffLinear is MathEvaluator.EvalResult.Success)
        assertEquals(5.0, (diffLinear as MathEvaluator.EvalResult.Success).value, 1e-4)

        // d/dx(sin(x), 0) in RAD = cos(0) = 1
        val diffSin = evaluator.evaluate("d/dx(sin(x),0)", AngleUnit.RAD)
        assertTrue(diffSin is MathEvaluator.EvalResult.Success)
        assertEquals(1.0, (diffSin as MathEvaluator.EvalResult.Success).value, 1e-4)

        // Incomplete derivative templates -> Syntax ERROR
        val diffEmpty = evaluator.evaluate("d/dx(,)")
        assertTrue(diffEmpty is MathEvaluator.EvalResult.Error)

        val diffNoPoint = evaluator.evaluate("d/dx(x^2,)")
        assertTrue(diffNoPoint is MathEvaluator.EvalResult.Error)

        val diffNoFunc = evaluator.evaluate("d/dx(,3)")
        assertTrue(diffNoFunc is MathEvaluator.EvalResult.Error)
    }

    @Test
    fun testComplexNumbersEvaluation() {
        // (2+3i) + (1-i) = 3 + 2i
        val addRes = evaluator.evaluate("(2+3i) + (1-i)")
        assertTrue(addRes is MathEvaluator.EvalResult.Success)
        assertEquals("3 + 2i", (addRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // (1+i) * (1-i) = 2
        val mulRes = evaluator.evaluate("(1+i) * (1-i)")
        assertTrue(mulRes is MathEvaluator.EvalResult.Success)
        assertEquals("2", (mulRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // i^2 = -1
        val iSqRes = evaluator.evaluate("i^2")
        assertTrue(iSqRes is MathEvaluator.EvalResult.Success)
        assertEquals("-1", (iSqRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // (3+4i) / (1-2i) = -1 + 2i
        val divRes = evaluator.evaluate("(3+4i) / (1-2i)")
        assertTrue(divRes is MathEvaluator.EvalResult.Success)
        assertEquals("-1 + 2i", (divRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // arg(1+i) in DEG = 45°
        val argRes = evaluator.evaluate("arg(1+i)", AngleUnit.DEG)
        assertTrue(argRes is MathEvaluator.EvalResult.Success)
        assertEquals("45", (argRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // conj(3+4i) = 3 - 4i
        val conjRes = evaluator.evaluate("conj(3+4i)")
        assertTrue(conjRes is MathEvaluator.EvalResult.Success)
        assertEquals("3 - 4i", (conjRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // ReP(5-2i) = 5
        val repRes = evaluator.evaluate("ReP(5-2i)")
        assertTrue(repRes is MathEvaluator.EvalResult.Success)
        assertEquals("5", (repRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // ImP(5-2i) = -2
        val impRes = evaluator.evaluate("ImP(5-2i)")
        assertTrue(impRes is MathEvaluator.EvalResult.Success)
        assertEquals("-2", (impRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // 2∠90 in DEG = 2i
        val polarRes = evaluator.evaluate("2∠90", AngleUnit.DEG)
        assertTrue(polarRes is MathEvaluator.EvalResult.Success)
        assertEquals("2i", (polarRes as MathEvaluator.EvalResult.Success).standardFormatted)

        // abs(3+4i) = 5
        val absRes = evaluator.evaluate("abs(3+4i)")
        assertTrue(absRes is MathEvaluator.EvalResult.Success)
        assertEquals("5", (absRes as MathEvaluator.EvalResult.Success).standardFormatted)
    }

    @Test
    fun testMatrixAndVectorEngine() {
        val matA = com.example.engine.AdvancedMathEngine.Matrix(2, 2, listOf(listOf(1.0, 2.0), listOf(3.0, 4.0)))
        val matB = com.example.engine.AdvancedMathEngine.Matrix(2, 2, listOf(listOf(2.0, 0.0), listOf(1.0, 3.0)))

        // Det(MatA) = -2
        assertEquals(-2.0, matA.determinant(), 1e-6)

        // MatA * MatB = [[4, 6], [10, 12]]
        val mul = matA * matB
        assertEquals(4.0, mul.data[0][0], 1e-6)
        assertEquals(6.0, mul.data[0][1], 1e-6)
        assertEquals(10.0, mul.data[1][0], 1e-6)
        assertEquals(12.0, mul.data[1][1], 1e-6)

        // Inv(MatA)
        val inv = matA.inverse()
        assertEquals(-2.0, inv.data[0][0], 1e-6)
        assertEquals(1.0, inv.data[0][1], 1e-6)
        assertEquals(1.5, inv.data[1][0], 1e-6)
        assertEquals(-0.5, inv.data[1][1], 1e-6)

        // Vector: VctA = (1, 2, 3), VctB = (4, 5, 6)
        val vA = com.example.engine.AdvancedMathEngine.Vector3(1.0, 2.0, 3.0)
        val vB = com.example.engine.AdvancedMathEngine.Vector3(4.0, 5.0, 6.0)

        // Dot = 32
        assertEquals(32.0, vA.dot(vB), 1e-6)

        // Cross = (-3, 6, -3)
        val cr = vA.cross(vB)
        assertEquals(-3.0, cr.x, 1e-6)
        assertEquals(6.0, cr.y, 1e-6)
        assertEquals(-3.0, cr.z, 1e-6)

        // Angle between (1, 0, 0) and (0, 1, 0) = 90 deg
        val vX = com.example.engine.AdvancedMathEngine.Vector3(1.0, 0.0, 0.0)
        val vY = com.example.engine.AdvancedMathEngine.Vector3(0.0, 1.0, 0.0)
        assertEquals(90.0, vX.angleWith(vY), 1e-6)
    }

    @Test
    fun testEquationSolverEngine() {
        // Simul 2x2: X + Y = 2, X - Y = 0 => X = 1, Y = 1
        val res = com.example.engine.EquationSolver.solveLinear2(1.0, 1.0, 2.0, 1.0, -1.0, 0.0)
        assertEquals(1.0, res.x, 1e-6)
        assertEquals(1.0, res.y, 1e-6)

        // Quadratic: X² - 3X + 2 = 0 => roots 2 and 1
        val rootsReal = com.example.engine.EquationSolver.solveQuadratic(1.0, -3.0, 2.0)
        assertEquals(2.0, rootsReal[0].real, 1e-6)
        assertEquals(0.0, rootsReal[0].imag, 1e-6)
        assertEquals(1.0, rootsReal[1].real, 1e-6)
        assertEquals(0.0, rootsReal[1].imag, 1e-6)

        // Quadratic with complex roots: X² + 1 = 0 => 0 ± 1i
        val rootsComplex = com.example.engine.EquationSolver.solveQuadratic(1.0, 0.0, 1.0)
        assertEquals(0.0, rootsComplex[0].real, 1e-6)
        assertEquals(1.0, rootsComplex[0].imag, 1e-6)
        assertEquals(0.0, rootsComplex[1].real, 1e-6)
        assertEquals(-1.0, rootsComplex[1].imag, 1e-6)
    }

    @Test
    fun testInsideLcdModesAndKeypadNavigation() {
        val vm = com.example.ui.CalculatorViewModel()

        // Initially in CALC mode
        assertEquals(com.example.model.LcdMode.CALC, vm.state.value.lcdMode)

        // Press MENU key -> switches to LcdMode.MENU
        vm.onAction(com.example.ui.KeyAction.ModeMenu)
        assertEquals(com.example.model.LcdMode.MENU, vm.state.value.lcdMode)

        // Arrow navigation in MENU: move cursor left to 3, then right to 4
        vm.onAction(com.example.ui.KeyAction.CursorLeft)
        assertEquals(3, vm.state.value.selectedMenuItem)
        vm.onAction(com.example.ui.KeyAction.CursorRight)
        assertEquals(4, vm.state.value.selectedMenuItem)

        // Pressing '=' selects the highlighted menu item (4: Matrix)
        vm.onAction(com.example.ui.KeyAction.Equals)
        assertEquals(com.example.model.LcdMode.MATRIX, vm.state.value.lcdMode)
        assertEquals(4, vm.state.value.currentMode)
        assertTrue(vm.state.value.matrixDetDisplay.contains("Det"))

        // Test Transpose, Inverse, Add, Mul
        vm.computeMatrixTranspose()
        assertTrue(vm.state.value.matrixDetDisplay.startsWith("Trn:"))

        vm.computeMatrixInverse()
        assertTrue(vm.state.value.matrixDetDisplay.startsWith("Inv:"))

        vm.computeMatrixAdd()
        assertTrue(vm.state.value.matrixDetDisplay.startsWith("A+B:"))

        vm.computeMatrixMul()
        assertTrue(vm.state.value.matrixDetDisplay.startsWith("A×B:"))

        // Switch to MatB
        vm.selectMatrix("MatB")
        assertEquals("MatB", vm.state.value.currentMatrixName)
        assertTrue(vm.state.value.matrixDetDisplay.contains("Det(MatB)"))

        // Pressing AC returns to CALC mode
        vm.onAction(com.example.ui.KeyAction.Ac)
        assertEquals(com.example.model.LcdMode.CALC, vm.state.value.lcdMode)

        // Press MENU and then 'A' -> enters EQUATION/FUNC mode (Mode 10) on the LCD screen
        vm.onAction(com.example.ui.KeyAction.ModeMenu)
        vm.onCharInput("A")
        assertEquals(com.example.model.LcdMode.EQUATION, vm.state.value.lcdMode)
        assertEquals(10, vm.state.value.currentMode)

        // Pressing '=' solves the equation: X=1, Y=1 (for defaults X+Y=2, X-Y=0)
        vm.onAction(com.example.ui.KeyAction.Equals)
        assertEquals("X=1, Y=1", vm.state.value.eqnResultDisplay)

        // Pressing AC exits back to CALC mode
        vm.onAction(com.example.ui.KeyAction.Ac)
        assertEquals(com.example.model.LcdMode.CALC, vm.state.value.lcdMode)
    }

    @Test
    fun testMatrixOptnInLcdScreenAndDimensions() {
        val vm = com.example.ui.CalculatorViewModel()

        // 1. Enter Matrix mode via Menu item 4
        vm.onAction(com.example.ui.KeyAction.ModeMenu)
        vm.onCharInput("4")
        assertEquals(com.example.model.LcdMode.MATRIX, vm.state.value.lcdMode)
        assertFalse(vm.state.value.showMatrixOptn)

        // 2. Press OPTN key -> showMatrixOptn becomes true (displayed inside LCD screen)
        vm.onAction(com.example.ui.KeyAction.OptnMenu)
        assertTrue(vm.state.value.showMatrixOptn)

        // 3. Set dimensions to 3x3
        vm.setMatrixDimensions(3, 3)
        assertEquals(3, vm.state.value.matrixRows)
        assertEquals(3, vm.state.value.matrixCols)
        val matA3x3 = vm.state.value.matrixStore["MatA"]
        assertNotNull(matA3x3)
        assertEquals(3, matA3x3!!.size)
        assertEquals(3, matA3x3[0].size)

        // 4. In OPTN screen, pressing '3' selects MatB and closes OPTN screen
        vm.onCharInput("3")
        assertFalse(vm.state.value.showMatrixOptn)
        assertEquals("MatB", vm.state.value.currentMatrixName)

        // 5. Open OPTN again, set dimensions to 4x4
        vm.onAction(com.example.ui.KeyAction.OptnMenu)
        assertTrue(vm.state.value.showMatrixOptn)
        vm.setMatrixDimensions(4, 4)
        assertEquals(4, vm.state.value.matrixRows)
        assertEquals(4, vm.state.value.matrixCols)

        // Close OPTN with AC
        vm.onAction(com.example.ui.KeyAction.Ac)
        assertFalse(vm.state.value.showMatrixOptn)
        assertEquals(com.example.model.LcdMode.MATRIX, vm.state.value.lcdMode)
    }

    @Test
    fun testMatrixExWorkflowAndCalculations() {
        val vm = com.example.ui.CalculatorViewModel()

        // 1. Enter Matrix mode from MENU
        vm.onAction(com.example.ui.KeyAction.ModeMenu)
        vm.onCharInput("4")
        assertEquals(com.example.model.LcdMode.MATRIX, vm.state.value.lcdMode)
        assertEquals(com.example.model.MatrixStage.MDEF, vm.state.value.matrixStage)

        // 2. Select 1 for MatA -> transitions to ROW
        vm.onCharInput("1")
        assertEquals(com.example.model.MatrixStage.ROW, vm.state.value.matrixStage)
        assertEquals("MatA", vm.state.value.currentMatrixName)

        // 3. Set Rows to 3 and press '=' -> transitions to COL
        vm.onCharInput("3")
        assertEquals(3, vm.state.value.matrixDimSelection)
        vm.onAction(com.example.ui.KeyAction.Equals)
        assertEquals(com.example.model.MatrixStage.COL, vm.state.value.matrixStage)
        assertEquals(3, vm.state.value.matrixRows)

        // 4. Set Cols to 3 and press '=' -> transitions to INP
        vm.onCharInput("3")
        assertEquals(3, vm.state.value.matrixDimSelection)
        vm.onAction(com.example.ui.KeyAction.Equals)
        assertEquals(com.example.model.MatrixStage.INP, vm.state.value.matrixStage)
        assertEquals(3, vm.state.value.matrixCols)

        // 5. Fill cells or test operations
        vm.setMatrixCalcExpr("MatA+MatB")
        vm.runMatrixCalc()
        assertTrue(vm.state.value.matrixCalcResult.isNotEmpty())

        vm.setMatrixCalcExpr("7*MatA")
        vm.runMatrixCalc()
        assertTrue(vm.state.value.matrixCalcResult.isNotEmpty())

        vm.setMatrixCalcExpr("Det(MatA)")
        vm.runMatrixCalc()
        assertTrue(vm.state.value.matrixCalcResult.contains("Det"))

        vm.setMatrixCalcExpr("Trn(MatA)")
        vm.runMatrixCalc()
        assertTrue(vm.state.value.matrixCalcResult.isNotEmpty())
    }

    @Test
    fun testVectorInLcdWorkflow() {
        val vm = com.example.ui.CalculatorViewModel()

        // 1. Enter Vector mode from MENU
        vm.onAction(com.example.ui.KeyAction.ModeMenu)
        vm.onCharInput("5")
        assertEquals(com.example.model.LcdMode.VECTOR, vm.state.value.lcdMode)
        assertEquals(com.example.model.VectorStage.MDEF, vm.state.value.vectorStage)

        // 2. Select 1 for VctA -> transitions to VDIM
        vm.onCharInput("1")
        assertEquals(com.example.model.VectorStage.VDIM, vm.state.value.vectorStage)
        assertEquals("VctA", vm.state.value.currentVectorName)

        // 3. Set dimension to 3 and press '=' -> transitions to INP
        vm.onCharInput("3")
        assertEquals(3, vm.state.value.vectorDimSelection)
        vm.onAction(com.example.ui.KeyAction.Equals)
        assertEquals(com.example.model.VectorStage.INP, vm.state.value.vectorStage)
        assertEquals(3, vm.state.value.vectorDim)
        assertEquals(0, vm.state.value.vectorActiveIndex)

        // 4. Input components: x1 = 1, x2 = 2, x3 = 3
        vm.onCharInput("1")
        vm.onAction(com.example.ui.KeyAction.Equals)
        assertEquals(1, vm.state.value.vectorActiveIndex)

        vm.onCharInput("2")
        vm.onAction(com.example.ui.KeyAction.Equals)
        assertEquals(2, vm.state.value.vectorActiveIndex)

        vm.onCharInput("3")
        // Pressing '=' at last component transitions to CALC
        vm.onAction(com.example.ui.KeyAction.Equals)
        assertEquals(com.example.model.VectorStage.CALC, vm.state.value.vectorStage)

        // 5. Test vector calculations
        vm.setVectorCalcExpr("VctA•VctB")
        vm.runVectorCalc()
        // VctA = [1, 2, 3], VctB = [2, 0, 1] -> dot = 1*2 + 2*0 + 3*1 = 5
        assertTrue(vm.state.value.vectorCalcResult.contains("5"))

        vm.setVectorCalcExpr("VctA×VctB")
        vm.runVectorCalc()
        // cross = [2*1 - 3*0, 3*2 - 1*1, 1*0 - 2*2] = [2, 5, -4]
        assertTrue(vm.state.value.vectorCalcResult.contains("2"))
        assertTrue(vm.state.value.vectorCalcResult.contains("5"))
        assertTrue(vm.state.value.vectorCalcResult.contains("-4"))

        vm.setVectorCalcExpr("Abs(VctA)")
        vm.runVectorCalc()
        // |VctA| = sqrt(1+4+9) = sqrt(14) ≈ 3.742
        assertTrue(vm.state.value.vectorCalcResult.contains("3.742"))

        // 6. Test Vector OPTN menu
        vm.onAction(com.example.ui.KeyAction.OptnMenu)
        assertTrue(vm.state.value.showVectorOptn)

        // Press '5' for Dot product from OPTN menu
        vm.onCharInput("5")
        assertFalse(vm.state.value.showVectorOptn)
        assertEquals("VctA•VctB", vm.state.value.vectorCalcExpr)
        assertTrue(vm.state.value.vectorCalcResult.contains("5"))

        // Test AC in CALC mode clears expr
        vm.onAction(com.example.ui.KeyAction.Ac)
        assertEquals("", vm.state.value.vectorCalcExpr)

        // Pressing AC again when expr is blank exits to CALC
        vm.onAction(com.example.ui.KeyAction.Ac)
        assertEquals(com.example.model.LcdMode.CALC, vm.state.value.lcdMode)
    }

    @Test
    fun testAuthentic12MenuModes() {
        val vm = com.example.ui.CalculatorViewModel()

        // Test all 12 shortcuts in MENU (1..9, A, B, C)
        val modesToTest = listOf(
            "1" to (1 to "Calculate"),
            "2" to (2 to "Complex"),
            "3" to (3 to "Base-N"),
            "4" to (4 to "Matrix"),
            "5" to (5 to "Vector"),
            "6" to (6 to "Statistics"),
            "7" to (7 to "Distribution"),
            "8" to (8 to "Spreadsheet"),
            "9" to (9 to "Table"),
            "A" to (10 to "Equation/Func"),
            "B" to (11 to "Inequality"),
            "C" to (12 to "Ratio")
        )

        for ((key, expected) in modesToTest) {
            vm.onAction(com.example.ui.KeyAction.ModeMenu)
            vm.onCharInput(key)
            assertEquals("Key $key mode mismatch", expected.first, vm.state.value.currentMode)
            assertEquals("Key $key name mismatch", expected.second, vm.state.value.currentModeName)
        }

        // Test arrow navigation across all 12 items
        vm.onAction(com.example.ui.KeyAction.ModeMenu)
        assertEquals(com.example.model.LcdMode.MENU, vm.state.value.lcdMode)
        // Navigate left until item 1
        while (vm.state.value.selectedMenuItem != 1) {
            vm.onAction(com.example.ui.KeyAction.CursorLeft)
        }
        assertEquals(1, vm.state.value.selectedMenuItem)

        // Cursor down (+3) moves down a row: 1 -> 4 -> 7 -> 10 -> 1
        vm.onAction(com.example.ui.KeyAction.CursorDown)
        assertEquals(4, vm.state.value.selectedMenuItem)
        vm.onAction(com.example.ui.KeyAction.CursorDown)
        assertEquals(7, vm.state.value.selectedMenuItem)
        vm.onAction(com.example.ui.KeyAction.CursorDown)
        assertEquals(10, vm.state.value.selectedMenuItem)
        vm.onAction(com.example.ui.KeyAction.CursorDown)
        assertEquals(1, vm.state.value.selectedMenuItem)
    }

    @Test
    fun testInLcdOptnCalculateMode() {
        val vm = com.example.ui.CalculatorViewModel()
        assertEquals(1, vm.state.value.currentMode)
        assertFalse(vm.state.value.showCalcOptn)
        assertFalse(vm.state.value.showOptnDialog)

        // Press OPTN -> in-LCD OPTN opens directly inside the LCD screen, NO external dialog
        vm.onAction(com.example.ui.KeyAction.OptnMenu)
        assertTrue(vm.state.value.showCalcOptn)
        assertFalse(vm.state.value.showOptnDialog)
        assertEquals("", vm.state.value.calcOptnSubmenu)

        // Press "1" on keypad -> opens Hyperbolic Func submenu inside LCD
        vm.onCharInput("1")
        assertTrue(vm.state.value.showCalcOptn)
        assertEquals("HYP", vm.state.value.calcOptnSubmenu)

        // Press "1" on keypad (sinh) -> inserts sinh( into formula and closes in-LCD OPTN
        vm.onCharInput("1")
        assertFalse(vm.state.value.showCalcOptn)
        assertEquals("sinh(", vm.state.value.formula)

        // Open OPTN again and test AC key closes in-LCD OPTN
        vm.onAction(com.example.ui.KeyAction.OptnMenu)
        assertTrue(vm.state.value.showCalcOptn)
        vm.onAction(com.example.ui.KeyAction.Ac)
        assertFalse(vm.state.value.showCalcOptn)
    }

    @Test
    fun testInLcdOptnComplexAndBaseNModes() {
        val vm = com.example.ui.CalculatorViewModel()

        // 1. Complex mode (Mode 2)
        vm.onAction(com.example.ui.KeyAction.ModeMenu)
        vm.onCharInput("2")
        assertEquals(2, vm.state.value.currentMode)

        // Press OPTN -> in-LCD OPTN for Complex mode
        vm.onAction(com.example.ui.KeyAction.OptnMenu)
        assertTrue(vm.state.value.showCalcOptn)
        assertFalse(vm.state.value.showOptnDialog)

        // Select 1 (i)
        vm.onCharInput("1")
        assertFalse(vm.state.value.showCalcOptn)
        assertTrue(vm.state.value.formula.contains("i"))

        // 2. Base-N mode (Mode 3)
        vm.onAction(com.example.ui.KeyAction.ModeMenu)
        vm.onCharInput("3")
        assertEquals(3, vm.state.value.currentMode)

        // Press OPTN -> in-LCD OPTN for Base-N mode
        vm.onAction(com.example.ui.KeyAction.OptnMenu)
        assertTrue(vm.state.value.showCalcOptn)
        assertFalse(vm.state.value.showOptnDialog)

        // Select 1 (and)
        vm.onCharInput("1")
        assertFalse(vm.state.value.showCalcOptn)
        assertTrue(vm.state.value.formula.contains("and"))
    }
}


