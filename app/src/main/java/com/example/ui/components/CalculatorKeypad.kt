package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorState
import com.example.ui.KeyAction

enum class KeyColorType {
    WHITE,
    BLACK,
    BLUE
}

/**
 * 1:1 Exact Replica of CASIO fx-991EX CLASSWIZ button positions, secondary markings, and layout.
 *
 * UNIFORM BUTTON DIMENSIONS:
 * All rectangular keycaps are strictly 42dp high (min-height 42dp, max-height 42dp).
 * No button is taller than any other.
 *
 * EXACT DISPLAY MAPPINGS:
 * - SHIFT + sin  -> inserts "sin⁻¹("
 * - SHIFT + cos  -> inserts "cos⁻¹("
 * - SHIFT + tan  -> inserts "tan⁻¹("
 * - SHIFT + √   -> inserts "∛(" (Unicode U+221B cube root)
 * - SHIFT + x²   -> inserts "³" (x³)
 * - SHIFT + x^   -> inserts "^(1/" (ⁿ√)
 * - SHIFT + log  -> inserts "10^("
 * - SHIFT + ln   -> inserts "e^("
 * - SHIFT + (-)  -> inserts "log("
 * - SHIFT + x⁻¹  -> inserts "!"
 * - SHIFT + Ans  -> inserts "%"
 * - SHIFT + ×10^x-> inserts "π"
 */
@Composable
fun CalculatorKeypad(
    state: CalculatorState,
    onInput: (String) -> Unit,
    onAction: (KeyAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    fun haptic() {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // ================= UPPER CONTROL CLUSTER =================
        // Row with labels: SHIFT ALPHA | MENU SETUP ON
        // Next: [ROUND 45px SHIFT] [ROUND 45px ALPHA] [D-PAD CHROME 4-piece] [ROUND 40px SETUP] [ROUND 40px ON]
        // Next: OPTN | CALC | [D-PAD center] | ∫□ | x
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT SIDE: (SHIFT, ALPHA) and under them (OPTN, CALC)
            Column(
                modifier = Modifier.weight(1.85f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Row 1 & 2 Left: Labels & Round 45px Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // SHIFT Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "SHIFT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFCC00), // Brilliant Yellow
                            letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                        RoundButton(
                            size = 45.dp,
                            testTag = "key_shift"
                        ) {
                            haptic()
                            onAction(KeyAction.Shift)
                        }
                    }

                    // ALPHA Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "ALPHA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF2D75), // Vibrant Pink
                            letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                        RoundButton(
                            size = 45.dp,
                            testTag = "key_alpha"
                        ) {
                            haptic()
                            onAction(KeyAction.Alpha)
                        }
                    }
                }

                // Black Keys: OPTN & CALC (height: 44dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // OPTN (under QR label)
                    CasioKey(
                        label = "OPTN",
                        yellowLabel = "QR",
                        pinkLabel = null,
                        colorType = KeyColorType.BLACK,
                        modifier = Modifier.weight(1f),
                        testTag = "key_optn"
                    ) {
                        haptic()
                        if (state.isShiftActive) onAction(KeyAction.QrCode) else onAction(KeyAction.OptnMenu)
                    }

                    // CALC (under SOLVE = label)
                    CasioKey(
                        label = "CALC",
                        yellowLabel = "SOLVE",
                        pinkLabel = "=",
                        colorType = KeyColorType.BLACK,
                        modifier = Modifier.weight(1f),
                        testTag = "key_calc"
                    ) {
                        haptic()
                        when {
                            state.isShiftActive -> onAction(KeyAction.Solve)
                            state.isAlphaActive -> onInput("=")
                            else -> onAction(KeyAction.CalcPrompt)
                        }
                    }
                }
            }

            // CENTER: Centered Chrome D-PAD container
            Box(
                modifier = Modifier
                    .weight(1.35f)
                    .padding(horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                ReplayPad(onAction = onAction)
            }

            // RIGHT SIDE: (MENU/SETUP, ON) and under them (integral, x)
            Column(
                modifier = Modifier.weight(1.85f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Row 1 & 2 Right: Labels & Round 40px Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // MENU / SETUP Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = "MENU",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00B0FF), // Blue
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "SETUP",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCC00), // Yellow
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                        RoundButton(
                            size = 40.dp,
                            testTag = "key_menu"
                        ) {
                            haptic()
                            if (state.isShiftActive) onAction(KeyAction.SetupMenu) else onAction(KeyAction.ModeMenu)
                        }
                    }

                    // ON Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "ON",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00B0FF), // Blue
                            fontFamily = FontFamily.SansSerif
                        )
                        RoundButton(
                            size = 40.dp,
                            testTag = "key_on"
                        ) {
                            haptic()
                            onAction(KeyAction.On)
                        }
                    }
                }

                // Black Keys: ∫□ & x (height: 42dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // integral template (under d/dx : label)
                    CasioKey(
                        label = "∫□",
                        yellowLabel = "d/dx",
                        pinkLabel = ":",
                        colorType = KeyColorType.BLACK,
                        modifier = Modifier.weight(1f),
                        testTag = "key_integral"
                    ) {
                        haptic()
                        when {
                            state.isShiftActive -> onAction(KeyAction.InsertDerivative)
                            state.isAlphaActive -> onInput(":")
                            else -> onAction(KeyAction.InsertIntegral)
                        }
                    }

                    // x (under ; x label)
                    CasioKey(
                        label = "x",
                        yellowLabel = ";",
                        pinkLabel = "x",
                        colorType = KeyColorType.BLACK,
                        modifier = Modifier.weight(1f),
                        testTag = "key_x"
                    ) {
                        haptic()
                        when {
                            state.isShiftActive -> onInput(";")
                            state.isAlphaActive -> onAction(KeyAction.Variable("x"))
                            else -> onAction(KeyAction.Variable("x"))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // ================= BLACK KEYS (6 COLUMNS) =================
        // Row 4: fraction-a/b | sqrt | x² | x^n | log | ln
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.5.dp)
        ) {
            // Col 1: fraction template
            CasioKey(
                label = "■/□",
                yellowLabel = "■ ■/□",
                pinkLabel = null,
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_fraction"
            ) {
                haptic()
                if (state.isShiftActive) onAction(KeyAction.SdToggle) else onAction(KeyAction.InsertFraction)
            }

            // Col 2: sqrt (Shift -> ∛ cube root!)
            CasioKey(
                label = "√□",
                yellowLabel = "³√",
                pinkLabel = null,
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_sqrt"
            ) {
                haptic()
                if (state.isShiftActive) onInput("³√(") else onInput("√(")
            }

            // Col 3: x² (Shift -> x³)
            CasioKey(
                label = "x²",
                yellowLabel = "x³",
                blueLabel = "DEC",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_square"
            ) {
                haptic()
                if (state.isShiftActive) onInput("³") else onInput("²")
            }

            // Col 4: x^n (Shift -> ⁿ√)
            CasioKey(
                label = "x^□",
                yellowLabel = "ⁿ√",
                blueLabel = "HEX",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_power"
            ) {
                haptic()
                if (state.isShiftActive) onInput("^(1/") else onInput("^")
            }

            // Col 5: log_□□ (Shift -> 10^()
            CasioKey(
                label = "log_□□",
                yellowLabel = "10^□",
                blueLabel = "BIN",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_log"
            ) {
                haptic()
                if (state.isShiftActive) onInput("10^(") else onAction(KeyAction.InsertLogBase)
            }

            // Col 6: ln (Shift -> e^()
            CasioKey(
                label = "ln",
                yellowLabel = "e^□",
                blueLabel = "OCT",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_ln"
            ) {
                haptic()
                if (state.isShiftActive) onInput("e^(") else onInput("ln(")
            }
        }

        // Row 5: (-) | °'" | x^-1 | sin | cos | tan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.5.dp)
        ) {
            // Col 1: (-) (Shift -> log, Alpha -> A)
            CasioKey(
                label = "(-)",
                yellowLabel = "log",
                pinkLabel = "A",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_negative"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onInput("log(")
                    state.isAlphaActive -> onAction(KeyAction.Variable("A"))
                    else -> onInput("-")
                }
            }

            // Col 2: ° ' " (Shift -> FACT, Alpha -> B)
            CasioKey(
                label = "° ' \"",
                yellowLabel = "FACT",
                pinkLabel = "B",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_dms"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onInput("°")
                    state.isAlphaActive -> onAction(KeyAction.Variable("B"))
                    else -> onInput("°")
                }
            }

            // Col 3: x⁻¹ (Shift -> x!, Alpha -> C)
            CasioKey(
                label = "x⁻¹",
                yellowLabel = "x!",
                pinkLabel = "C",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_inverse"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onInput("!")
                    state.isAlphaActive -> onAction(KeyAction.Variable("C"))
                    else -> onInput("⁻¹")
                }
            }

            // Col 4: sin (Shift -> sin⁻¹()
            CasioKey(
                label = "sin",
                yellowLabel = "sin⁻¹",
                pinkLabel = "D",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_sin"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onInput("sin⁻¹(")
                    state.isAlphaActive -> onAction(KeyAction.Variable("D"))
                    else -> onInput("sin(")
                }
            }

            // Col 5: cos (Shift -> cos⁻¹()
            CasioKey(
                label = "cos",
                yellowLabel = "cos⁻¹",
                pinkLabel = "E",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_cos"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onInput("cos⁻¹(")
                    state.isAlphaActive -> onAction(KeyAction.Variable("E"))
                    else -> onInput("cos(")
                }
            }

            // Col 6: tan (Shift -> tan⁻¹()
            CasioKey(
                label = "tan",
                yellowLabel = "tan⁻¹",
                pinkLabel = "F",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_tan"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onInput("tan⁻¹(")
                    state.isAlphaActive -> onAction(KeyAction.Variable("F"))
                    else -> onInput("tan(")
                }
            }
        }

        // Row 6: STO | ENG | ( | ) | S⇔D | M+
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.5.dp)
        ) {
            // Col 1: STO (Shift -> RECALL)
            CasioKey(
                label = "STO",
                yellowLabel = "RECALL",
                pinkLabel = null,
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_sto"
            ) {
                haptic()
                if (state.isShiftActive) onAction(KeyAction.Rcl) else onAction(KeyAction.Sto)
            }

            // Col 2: ENG (Shift -> i, in Mode 2 -> i, else ENG)
            CasioKey(
                label = "ENG",
                yellowLabel = "i",
                pinkLabel = "←",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_eng"
            ) {
                haptic()
                if (state.isShiftActive || state.currentMode == 2) onInput("i") else onAction(KeyAction.Eng)
            }

            // Col 3: ( (Shift -> Abs, Alpha -> :)
            CasioKey(
                label = "(",
                yellowLabel = "Abs",
                pinkLabel = ":",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_paren_open"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onInput("abs(")
                    state.isAlphaActive -> onInput(":")
                    else -> onInput("(")
                }
            }

            // Col 4: ) (Shift -> ,, Alpha -> x)
            CasioKey(
                label = ")",
                yellowLabel = ",",
                pinkLabel = "x",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_paren_close"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onInput(",")
                    state.isAlphaActive -> onAction(KeyAction.Variable("x"))
                    else -> onInput(")")
                }
            }

            // Col 5: S⇔D (Shift -> a b/c ⇔ d/c, Alpha -> y)
            CasioKey(
                label = "S⇔D",
                yellowLabel = "a b/c",
                pinkLabel = "y",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_sd"
            ) {
                haptic()
                when {
                    state.isAlphaActive -> onAction(KeyAction.Variable("y"))
                    else -> onAction(KeyAction.SdToggle)
                }
            }

            // Col 6: M+ (Shift -> M-, Alpha -> M)
            CasioKey(
                label = "M+",
                yellowLabel = "M-",
                pinkLabel = "M",
                colorType = KeyColorType.BLACK,
                modifier = Modifier.weight(1f),
                testTag = "key_m_plus"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onAction(KeyAction.MMinus)
                    state.isAlphaActive -> onAction(KeyAction.Variable("M"))
                    else -> onAction(KeyAction.MPlus)
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // ================= NUMBER PAD (5 COLUMNS: WHITE + BLUE DEL/AC) =================
        // All buttons strictly 42dp height for uniform perfection
        // Row 7: 7 | 8 | 9 | DEL-blue | AC-blue
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.5.dp)
        ) {
            // 7 (Shift -> CONST)
            CasioKey(
                label = "7",
                yellowLabel = "CONST",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_7"
            ) {
                haptic()
                if (state.isShiftActive) onAction(KeyAction.Const) else onInput("7")
            }

            // 8 (Shift -> CONV)
            CasioKey(
                label = "8",
                yellowLabel = "CONV",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_8"
            ) {
                haptic()
                if (state.isShiftActive) onAction(KeyAction.Conv) else onInput("8")
            }

            // 9 (Shift -> RESET)
            CasioKey(
                label = "9",
                yellowLabel = "RESET",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_9"
            ) {
                haptic()
                if (state.isShiftActive) onAction(KeyAction.Ac) else onInput("9")
            }

            // DEL (Royal Blue; Shift -> INS)
            CasioKey(
                label = "DEL",
                yellowLabel = "INS",
                pinkLabel = "UNDO",
                colorType = KeyColorType.BLUE,
                modifier = Modifier.weight(1f),
                testTag = "key_del"
            ) {
                haptic()
                onAction(KeyAction.Del)
            }

            // AC (Royal Blue; Shift -> OFF)
            CasioKey(
                label = "AC",
                yellowLabel = "OFF",
                colorType = KeyColorType.BLUE,
                modifier = Modifier.weight(1f),
                testTag = "key_ac"
            ) {
                haptic()
                onAction(KeyAction.Ac)
            }
        }

        // Row 8: 4 | 5 | 6 | × | ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.5.dp)
        ) {
            CasioKey(
                label = "4",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_4"
            ) {
                haptic()
                onInput("4")
            }

            CasioKey(
                label = "5",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_5"
            ) {
                haptic()
                onInput("5")
            }

            CasioKey(
                label = "6",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_6"
            ) {
                haptic()
                onInput("6")
            }

            // × (White key; Shift -> nPr)
            CasioKey(
                label = "×",
                yellowLabel = "nPr",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_multiply"
            ) {
                haptic()
                if (state.isShiftActive) onInput("npr(") else onInput("×")
            }

            // ÷ (White key; Shift -> nCr)
            CasioKey(
                label = "÷",
                yellowLabel = "nCr",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_divide"
            ) {
                haptic()
                if (state.isShiftActive) onInput("ncr(") else onInput("÷")
            }
        }

        // Row 9: 1 | 2 | 3 | + | -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.5.dp)
        ) {
            CasioKey(
                label = "1",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_1"
            ) {
                haptic()
                onInput("1")
            }

            CasioKey(
                label = "2",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_2"
            ) {
                haptic()
                onInput("2")
            }

            CasioKey(
                label = "3",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_3"
            ) {
                haptic()
                onInput("3")
            }

            // + (White key; Shift -> Pol)
            CasioKey(
                label = "+",
                yellowLabel = "Pol",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_plus"
            ) {
                haptic()
                if (state.isShiftActive) onInput("pol(") else onInput("+")
            }

            // - (White key; Shift -> Rec)
            CasioKey(
                label = "-",
                yellowLabel = "Rec",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_minus"
            ) {
                haptic()
                if (state.isShiftActive) onInput("rec(") else onInput("-")
            }
        }

        // Row 10: 0 | . | ×10^x | Ans | =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.5.dp)
        ) {
            CasioKey(
                label = "0",
                yellowLabel = "Rnd",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_0"
            ) {
                haptic()
                if (state.isShiftActive) onInput("rnd(") else onInput("0")
            }

            CasioKey(
                label = ".",
                yellowLabel = "Ran#",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_dot"
            ) {
                haptic()
                if (state.isShiftActive) onInput("ran#") else onInput(".")
            }

            // ×10^x (White key with π on shift and e on alpha)
            CasioKey(
                label = "×10^x",
                yellowLabel = "π",
                pinkLabel = "e",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_exp"
            ) {
                haptic()
                when {
                    state.isShiftActive -> onInput("π")
                    state.isAlphaActive -> onInput("e")
                    else -> onInput("×10^")
                }
            }

            // Ans (White key with % on shift)
            CasioKey(
                label = "Ans",
                yellowLabel = "%",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_ans"
            ) {
                haptic()
                if (state.isShiftActive) onInput("%") else onInput("Ans")
            }

            // = (White key in photo with bold black symbol)
            CasioKey(
                label = "=",
                colorType = KeyColorType.WHITE,
                modifier = Modifier.weight(1f),
                testTag = "key_equals"
            ) {
                haptic()
                onAction(KeyAction.Equals)
            }
        }
    }
}

/**
 * Circular Silver Metallic Key:
 * .round-btn { width:45px; height:45px; border-radius:50%; background: radial-gradient(circle at 30% 30%, #eee, #aaa, #666); box-shadow: inset 0 1px 3px #fff, 0 2px 4px #000; }
 */
@Composable
fun RoundButton(
    size: Dp,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            // Exact radial-gradient(circle at 30% 30%, #eee, #aaa, #666)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFEEEEEE),
                        Color(0xFFAAAAAA),
                        Color(0xFF666666)
                    ),
                    center = Offset(size.value * 0.30f * 2.5f, size.value * 0.30f * 2.5f)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFF444444))
                ),
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // Inner concentric highlight rim
        Box(
            modifier = Modifier
                .size(size - 8.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD6DBE4),
                            Color(0xFFA8B0BE),
                            Color(0xFF7E8694)
                        )
                    )
                )
                .border(0.5.dp, Color(0x66FFFFFF), CircleShape)
        )
    }
}

/**
 * Standard Casio Keycap:
 * Strict uniform height: 42dp across all keys!
 * Clear, high-contrast secondary labels:
 * - Yellow = SHIFT (#FFCC00)
 * - Pink = ALPHA (#FF2D75)
 * - Minimum height 15dp to prevent clipping and text overlap
 * - Font size: 10sp, bold, letter-spacing: 0.5sp
 */
@Composable
fun CasioKey(
    label: String,
    yellowLabel: String? = null,
    pinkLabel: String? = null,
    blueLabel: String? = null,
    colorType: KeyColorType,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Upper Secondary Marking Row - Strictly fixed 14dp height across ALL buttons!
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .padding(horizontal = 1.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (yellowLabel != null) {
                    val yellowFontSize = when {
                        yellowLabel.length >= 6 -> 8.sp
                        yellowLabel.length >= 5 -> 8.5.sp
                        else -> 9.5.sp
                    }
                    Text(
                        text = yellowLabel,
                        fontSize = yellowFontSize,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFCC00), // High-contrast #FFCC00
                        letterSpacing = 0.2.sp,
                        fontFamily = FontFamily.SansSerif,
                        maxLines = 1,
                        softWrap = false
                    )
                }
                if (blueLabel != null) {
                    Text(
                        text = blueLabel,
                        fontSize = 8.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF), // Cyan/Blue
                        letterSpacing = 0.2.sp,
                        fontFamily = FontFamily.SansSerif,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            if (pinkLabel != null) {
                Text(
                    text = pinkLabel,
                    fontSize = if (pinkLabel.length > 2) 8.5.sp else 9.5.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF2D75), // Vibrant #FF2D75
                    letterSpacing = 0.2.sp,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        // Keycap Surface - STRICTLY 44dp uniform height across all keys!
        val (bgBrush, borderColor, textColor, fontSize) = when (colorType) {
            KeyColorType.WHITE -> {
                Quadruple(
                    Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF3F5F9), Color(0xFFDFE4ED))),
                    Color(0xFFB0B7C4),
                    Color(0xFF14171E),
                    if (label.length > 3) 11.sp else 16.sp
                )
            }
            KeyColorType.BLUE -> {
                Quadruple(
                    Brush.verticalGradient(listOf(Color(0xFF039BE5), Color(0xFF0277BD), Color(0xFF01579B))),
                    Color(0xFF40C4FF),
                    Color.White,
                    13.sp
                )
            }
            KeyColorType.BLACK -> {
                Quadruple(
                    Brush.verticalGradient(listOf(Color(0xFF353942), Color(0xFF22262E), Color(0xFF16181D))),
                    Color(0xFF4A515E),
                    Color.White,
                    if (label.length > 5) 9.sp else if (label.length > 3) 10.sp else 12.5.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp) // Strictly 44dp uniform height across ALL buttons!
                .shadow(2.dp, RoundedCornerShape(5.dp))
                .clip(RoundedCornerShape(5.dp))
                .background(bgBrush)
                .border(1.dp, borderColor, RoundedCornerShape(5.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
