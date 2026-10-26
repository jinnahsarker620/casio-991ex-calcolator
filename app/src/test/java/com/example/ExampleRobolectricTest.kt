package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AngleUnit
import com.example.ui.CalculatorViewModel
import com.example.ui.KeyAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("fx-991EX", appName)
    }

    @Test
    fun `viewModel calculations work`() {
        val vm = CalculatorViewModel()
        vm.onCharInput("sin(")
        vm.onCharInput("30")
        vm.onCharInput(")")
        vm.onAction(KeyAction.Equals)

        // ClassWiz Natural Display outputs exact fraction 1/2 first
        assertEquals("1/2", vm.state.value.resultDisplay)
        assertEquals(0.5, vm.state.value.numericResult ?: 0.0, 1e-6)

        // Pressing S<=>D toggles to decimal 0.5
        vm.onAction(KeyAction.SdToggle)
        assertEquals("0.5", vm.state.value.resultDisplay)
    }

    @Test
    fun `const button workflow opens dialog and inserts value`() {
        val vm = CalculatorViewModel()
        vm.onAction(KeyAction.Shift)
        assertTrue(vm.state.value.isShiftActive)

        // Trigger Const
        vm.onAction(KeyAction.Const)
        assertTrue(vm.state.value.showConstDialog)

        // Select constant g
        vm.insertConstant("9.80665")
        assertEquals(false, vm.state.value.showConstDialog)
        assertEquals("9.80665", vm.state.value.formula)
    }

    @Test
    fun `conv button workflow opens unit converter and inserts result`() {
        val vm = CalculatorViewModel()
        vm.onAction(KeyAction.Shift)
        assertTrue(vm.state.value.isShiftActive)

        // Trigger Conv
        vm.onAction(KeyAction.Conv)
        assertTrue(vm.state.value.showConvDialog)

        // Insert result from converter
        vm.insertConversionResult("100")
        assertEquals(false, vm.state.value.showConvDialog)
        assertEquals("100", vm.state.value.formula)
    }

    @Test
    fun `shift and alpha automatically deactivate after one input`() {
        val vm = CalculatorViewModel()
        vm.onAction(KeyAction.Shift)
        assertTrue(vm.state.value.isShiftActive)

        // Using shift function
        vm.onCharInput("∛(")
        assertEquals(false, vm.state.value.isShiftActive)
        assertEquals("∛(", vm.state.value.formula)

        // Alpha activation
        vm.onAction(KeyAction.Alpha)
        assertTrue(vm.state.value.isAlphaActive)

        // Using alpha
        vm.onAction(KeyAction.Variable("D"))
        assertEquals(false, vm.state.value.isAlphaActive)
    }
}
