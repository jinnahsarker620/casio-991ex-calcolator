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
fun RatioDialog(
    onDismiss: () -> Unit
) {
    var ratioType by remember { mutableStateOf(0) } // 0: A:B = X:D, 1: A:B = C:X
    var a by remember { mutableStateOf("2") }
    var b by remember { mutableStateOf("3") }
    var other by remember { mutableStateOf("9") } // D or C

    var resultText by remember { mutableStateOf("") }

    fun calculate() {
        try {
            val aVal = a.toDoubleOrNull() ?: 1.0
            val bVal = b.toDoubleOrNull() ?: 1.0
            val oVal = other.toDoubleOrNull() ?: 1.0

            val xVal = if (ratioType == 0) {
                AdvancedMathEngine.RatioSolver.solveXOverD(aVal, bVal, oVal)
            } else {
                AdvancedMathEngine.RatioSolver.solveCOverX(aVal, bVal, oVal)
            }
            resultText = "X = ${MathEvaluator.formatDecimal(xVal)}"
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
                            text = "RATIO SOLVER",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFAB40),
                            fontFamily = FontFamily.SansSerif
                        )
                        Text("Mode 12: A:B = X:D or A:B = C:X", fontSize = 10.sp, color = Color(0xFF9EA3AD))
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF2C323D))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("A : B = X : D", "A : B = C : X").forEachIndexed { idx, label ->
                        val isSelected = ratioType == idx
                        Button(
                            onClick = {
                                ratioType = idx
                                resultText = ""
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF0277BD) else Color(0xFF22262E)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = a, onValueChange = { a = it }, label = { Text("A") }, modifier = Modifier.weight(1f).height(50.dp), singleLine = true)
                    OutlinedTextField(value = b, onValueChange = { b = it }, label = { Text("B") }, modifier = Modifier.weight(1f).height(50.dp), singleLine = true)
                    OutlinedTextField(
                        value = other,
                        onValueChange = { other = it },
                        label = { Text(if (ratioType == 0) "D" else "C") },
                        modifier = Modifier.weight(1f).height(50.dp),
                        singleLine = true
                    )
                }

                Button(
                    onClick = { calculate() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("SOLVE RATIO ( = )", fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (resultText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF101216))
                            .border(1.dp, Color(0xFFFFAB40), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = resultText,
                            fontSize = 14.sp,
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
