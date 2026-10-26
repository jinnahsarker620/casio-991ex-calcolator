package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.AdvancedMathEngine
import com.example.engine.MathEvaluator

@Composable
fun StatisticsDialog(
    onDismiss: () -> Unit
) {
    var rawInput by remember { mutableStateOf("12, 15, 18, 22, 25, 30, 35") }
    var resultText by remember { mutableStateOf("") }

    fun calculate() {
        try {
            val list = rawInput.split(Regex("[,\\s]+"))
                .mapNotNull { it.toDoubleOrNull() }
            if (list.isEmpty()) {
                resultText = "Please enter comma or space separated numbers."
                return
            }
            val res = AdvancedMathEngine.calculate1VarStats(list)
            resultText = """
                n = ${res.n}
                x̄ (mean) = ${MathEvaluator.formatDecimal(res.mean)}
                Σx = ${MathEvaluator.formatDecimal(res.sumX)}
                Σx² = ${MathEvaluator.formatDecimal(res.sumX2)}
                σx = ${MathEvaluator.formatDecimal(res.sigmaX)}
                sx = ${MathEvaluator.formatDecimal(res.sX)}
                min(X) = ${MathEvaluator.formatDecimal(res.minX)}
                Q₁ = ${MathEvaluator.formatDecimal(res.q1)}
                Med = ${MathEvaluator.formatDecimal(res.median)}
                Q₃ = ${MathEvaluator.formatDecimal(res.q3)}
                max(X) = ${MathEvaluator.formatDecimal(res.maxX)}
            """.trimIndent()
        } catch (e: Exception) {
            resultText = "Error: ${e.message}"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF16181C),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .border(1.5.dp, Color(0xFF383D48), RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "STATISTICS (1-VAR)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF7C4DFF),
                            fontFamily = FontFamily.SansSerif
                        )
                        Text("Mode 6: Mean, Σx, Std Dev & Quartiles", fontSize = 10.sp, color = Color(0xFF9EA3AD))
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF2C323D))

                Text("Enter Data List (comma or space separated):", fontSize = 11.sp, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = rawInput,
                    onValueChange = { rawInput = it },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    singleLine = true
                )

                Button(
                    onClick = { calculate() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("CALCULATE STATS", fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (resultText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF101216))
                            .border(1.dp, Color(0xFF7C4DFF), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = resultText,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA6BEA0),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("CLOSE", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
