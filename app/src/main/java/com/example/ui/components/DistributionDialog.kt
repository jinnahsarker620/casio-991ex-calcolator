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
fun DistributionDialog(
    onDismiss: () -> Unit
) {
    var distType by remember { mutableStateOf(0) } // 0: Normal PD, 1: Normal CD, 2: Inverse Normal
    var xVal by remember { mutableStateOf("0") }
    var meanVal by remember { mutableStateOf("0") }
    var sigmaVal by remember { mutableStateOf("1") }
    var areaVal by remember { mutableStateOf("0.5") }

    var resultText by remember { mutableStateOf("") }

    fun calculate() {
        try {
            val mu = meanVal.toDoubleOrNull() ?: 0.0
            val sigma = sigmaVal.toDoubleOrNull() ?: 1.0

            resultText = when (distType) {
                0 -> {
                    val x = xVal.toDoubleOrNull() ?: 0.0
                    val pdf = AdvancedMathEngine.Distribution.normalPdf(x, mu, sigma)
                    "Normal PD: P(x) = ${MathEvaluator.formatDecimal(pdf)}"
                }
                1 -> {
                    val x = xVal.toDoubleOrNull() ?: 0.0
                    val cdf = AdvancedMathEngine.Distribution.normalCdf(x, mu, sigma)
                    "Normal CD: P(X ≤ x) = ${MathEvaluator.formatDecimal(cdf)}"
                }
                2 -> {
                    val area = areaVal.toDoubleOrNull() ?: 0.5
                    val inv = AdvancedMathEngine.Distribution.inverseNormalCdf(area, mu, sigma)
                    "Inv Normal: x = ${MathEvaluator.formatDecimal(inv)}"
                }
                else -> ""
            }
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
                            text = "DISTRIBUTION",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00B0FF),
                            fontFamily = FontFamily.SansSerif
                        )
                        Text("Mode 7: Normal PD, Normal CD & Inverse", fontSize = 10.sp, color = Color(0xFF9EA3AD))
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF2C323D))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Normal PD", "Normal CD", "Inverse Normal").forEachIndexed { idx, label ->
                        val isSelected = distType == idx
                        Button(
                            onClick = {
                                distType = idx
                                resultText = ""
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF0277BD) else Color(0xFF22262E)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp)
                        ) {
                            Text(label, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                        }
                    }
                }

                if (distType == 2) {
                    OutlinedTextField(value = areaVal, onValueChange = { areaVal = it }, label = { Text("Area (0..1)") }, modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true)
                } else {
                    OutlinedTextField(value = xVal, onValueChange = { xVal = it }, label = { Text("x Value") }, modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = meanVal, onValueChange = { meanVal = it }, label = { Text("μ (Mean)") }, modifier = Modifier.weight(1f).height(50.dp), singleLine = true)
                    OutlinedTextField(value = sigmaVal, onValueChange = { sigmaVal = it }, label = { Text("σ (Std Dev)") }, modifier = Modifier.weight(1f).height(50.dp), singleLine = true)
                }

                Button(
                    onClick = { calculate() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("CALCULATE ( = )", fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (resultText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF101216))
                            .border(1.dp, Color(0xFF00B0FF), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = resultText,
                            fontSize = 12.sp,
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
