package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.engine.MathEvaluator
import kotlin.math.abs
import kotlin.math.sqrt

@Composable
fun EquationDialog(
    onDismiss: () -> Unit
) {
    // Submode: "SELECT", "SIMUL", "POLY"
    var subMode by remember { mutableStateOf("SELECT") }

    // Simultaneous Equation Memory: a1, b1, C1 and a2, b2, C2
    var a1 by remember { mutableStateOf("1") }
    var b1 by remember { mutableStateOf("1") }
    var c1 by remember { mutableStateOf("2") }
    var a2 by remember { mutableStateOf("1") }
    var b2 by remember { mutableStateOf("-1") }
    var c2 by remember { mutableStateOf("0") }

    // Polynomial Equation Memory: a, b, c for aX² + bX + c = 0
    var polyA by remember { mutableStateOf("1") }
    var polyB by remember { mutableStateOf("-3") }
    var polyC by remember { mutableStateOf("2") }

    var eqResult by remember { mutableStateOf("") }

    fun solveSimul2() {
        val a1Val = a1.toDoubleOrNull() ?: 1.0
        val b1Val = b1.toDoubleOrNull() ?: 0.0
        val c1Val = c1.toDoubleOrNull() ?: 0.0

        val a2Val = a2.toDoubleOrNull() ?: 1.0
        val b2Val = b2.toDoubleOrNull() ?: 0.0
        val c2Val = c2.toDoubleOrNull() ?: 0.0

        val det = a1Val * b2Val - a2Val * b1Val
        if (abs(det) < 1e-12) {
            eqResult = "No Solution / Infinite Solutions"
            return
        }

        val x = (c1Val * b2Val - c2Val * b1Val) / det
        val y = (a1Val * c2Val - a2Val * c1Val) / det
        eqResult = "X = ${MathEvaluator.formatDecimal(x)}\nY = ${MathEvaluator.formatDecimal(y)}"
    }

    fun solvePoly2() {
        val a = polyA.toDoubleOrNull() ?: 1.0
        val b = polyB.toDoubleOrNull() ?: 0.0
        val c = polyC.toDoubleOrNull() ?: 0.0

        if (abs(a) < 1e-12) {
            if (abs(b) < 1e-12) {
                eqResult = "No Solution"
            } else {
                val x = -c / b
                eqResult = "X = ${MathEvaluator.formatDecimal(x)}"
            }
            return
        }

        val d = b * b - 4 * a * c
        if (d < 0) {
            val realPart = -b / (2 * a)
            val imagPart = sqrt(-d) / (2 * a)
            val realStr = MathEvaluator.formatDecimal(realPart)
            val imagStr = MathEvaluator.formatDecimal(abs(imagPart))
            eqResult = "X₁ = $realStr + ${imagStr}i\nX₂ = $realStr - ${imagStr}i"
        } else {
            val x1 = (-b + sqrt(d)) / (2 * a)
            val x2 = (-b - sqrt(d)) / (2 * a)
            eqResult = "X₁ = ${MathEvaluator.formatDecimal(x1)}\nX₂ = ${MathEvaluator.formatDecimal(x2)}"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF16181C),
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .border(1.5.dp, Color(0xFF383D48), RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EQUATION / FUNC",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE040FB),
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "fx-991EX ClassWiz Equation Solver",
                            fontSize = 10.sp,
                            color = Color(0xFF9EA3AD)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF2C323D))

                when (subMode) {
                    "SELECT" -> {
                        // Submode 1: Select between Simul and Polynomial
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Select Equation Type:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD600)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        subMode = "SIMUL"
                                        eqResult = ""
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("1: Simul Equation", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("a₁x + b₁y = c₁", fontSize = 9.sp, color = Color(0xFF80D8FF))
                                    }
                                }

                                Button(
                                    onClick = {
                                        subMode = "POLY"
                                        eqResult = ""
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("2: Polynomial", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("aX² + bX + c = 0", fontSize = 9.sp, color = Color(0xFFEA80FC))
                                    }
                                }
                            }
                        }
                    }

                    "SIMUL" -> {
                        // Submode 2: Simultaneous Linear Equation (2 Unknowns)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Simultaneous Equation (2 Unknowns)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E5FF)
                                )
                                TextButton(onClick = { subMode = "SELECT" }) {
                                    Text("◀ Back", color = Color(0xFFFFD600), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "a₁X + b₁Y = C₁\na₂X + b₂Y = C₂",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFA5ACB8)
                            )

                            // Equation 1
                            Text("Equation 1:", fontSize = 10.sp, color = Color(0xFFFFCC00), fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedTextField(
                                    value = a1,
                                    onValueChange = { a1 = it },
                                    label = { Text("a₁") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = b1,
                                    onValueChange = { b1 = it },
                                    label = { Text("b₁") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = c1,
                                    onValueChange = { c1 = it },
                                    label = { Text("C₁") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    singleLine = true
                                )
                            }

                            // Equation 2
                            Text("Equation 2:", fontSize = 10.sp, color = Color(0xFFFFCC00), fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedTextField(
                                    value = a2,
                                    onValueChange = { a2 = it },
                                    label = { Text("a₂") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = b2,
                                    onValueChange = { b2 = it },
                                    label = { Text("b₂") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = c2,
                                    onValueChange = { c2 = it },
                                    label = { Text("C₂") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    singleLine = true
                                )
                            }

                            // Solve Button
                            Button(
                                onClick = { solveSimul2() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("= Solve", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    "POLY" -> {
                        // Submode 3: Polynomial Equation (ax² + bx + c = 0)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Polynomial: aX² + bX + c = 0",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE040FB)
                                )
                                TextButton(onClick = { subMode = "SELECT" }) {
                                    Text("◀ Back", color = Color(0xFFFFD600), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = polyA,
                                    onValueChange = { polyA = it },
                                    label = { Text("a") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = polyB,
                                    onValueChange = { polyB = it },
                                    label = { Text("b") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = polyC,
                                    onValueChange = { polyC = it },
                                    label = { Text("c") },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    singleLine = true
                                )
                            }

                            // Solve Button
                            Button(
                                onClick = { solvePoly2() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("= Solve", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // Result Box (ClassWiz LCD green display style)
                if (eqResult.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF101216))
                            .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Solution:",
                                fontSize = 10.sp,
                                color = Color(0xFFFFCC00),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = eqResult,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA6BEA0),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Close Button
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
