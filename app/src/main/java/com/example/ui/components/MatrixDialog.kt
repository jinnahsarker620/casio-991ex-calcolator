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
import com.example.engine.AdvancedMathEngine
import com.example.engine.MathEvaluator

@Composable
fun MatrixDialog(
    onInsertResult: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // STORAGE - EX memory storage (MatA, MatB, MatC, MatD, MatAns)
    var selectedMat by remember { mutableStateOf("MatA") }
    var size by remember { mutableStateOf(2) } // 2x2 or 3x3

    val matrixMemory = remember {
        mutableStateMapOf(
            "MatA" to mutableStateListOf(
                mutableStateListOf("1", "2", "0"),
                mutableStateListOf("3", "4", "0"),
                mutableStateListOf("0", "0", "1")
            ),
            "MatB" to mutableStateListOf(
                mutableStateListOf("2", "0", "0"),
                mutableStateListOf("1", "3", "0"),
                mutableStateListOf("0", "0", "1")
            ),
            "MatC" to mutableStateListOf(
                mutableStateListOf("1", "1", "0"),
                mutableStateListOf("2", "2", "0"),
                mutableStateListOf("0", "0", "1")
            ),
            "MatD" to mutableStateListOf(
                mutableStateListOf("0", "0", "0"),
                mutableStateListOf("0", "0", "0"),
                mutableStateListOf("0", "0", "0")
            ),
            "MatAns" to mutableStateListOf(
                mutableStateListOf("0", "0", "0"),
                mutableStateListOf("0", "0", "0"),
                mutableStateListOf("0", "0", "0")
            )
        )
    }

    var output by remember { mutableStateOf("0") }

    fun getMatrix(name: String): AdvancedMathEngine.Matrix {
        val grid = matrixMemory[name] ?: return AdvancedMathEngine.Matrix(size, size, List(size) { List(size) { 0.0 } })
        val parsed = List(size) { r ->
            List(size) { c ->
                grid[r][c].toDoubleOrNull() ?: 0.0
            }
        }
        return AdvancedMathEngine.Matrix(size, size, parsed)
    }

    fun saveToMatAns(mat: AdvancedMathEngine.Matrix) {
        val ansGrid = matrixMemory["MatAns"] ?: return
        for (r in 0 until minOf(size, mat.rows)) {
            for (c in 0 until minOf(size, mat.cols)) {
                ansGrid[r][c] = MathEvaluator.formatDecimal(mat.data[r][c])
            }
        }
    }

    fun calculate(op: String) {
        try {
            val currMat = getMatrix(selectedMat)
            when (op) {
                "Det" -> {
                    val d = currMat.determinant()
                    output = "Det($selectedMat) = ${MathEvaluator.formatDecimal(d)}"
                    val singleMat = AdvancedMathEngine.Matrix(1, 1, listOf(listOf(d)))
                    saveToMatAns(singleMat)
                }
                "Inv" -> {
                    val inv = currMat.inverse()
                    val str = inv.data.joinToString("\n") { row ->
                        row.joinToString("  ") { MathEvaluator.formatDecimal(it) }
                    }
                    output = "Inv($selectedMat) =\n$str"
                    saveToMatAns(inv)
                }
                "MatA×MatB" -> {
                    val a = getMatrix("MatA")
                    val b = getMatrix("MatB")
                    val mul = a * b
                    val str = mul.data.joinToString("\n") { row ->
                        row.joinToString("  ") { MathEvaluator.formatDecimal(it) }
                    }
                    output = "MatA × MatB =\n$str"
                    saveToMatAns(mul)
                }
                "Trn" -> {
                    val trn = currMat.transpose()
                    val str = trn.data.joinToString("\n") { row ->
                        row.joinToString("  ") { MathEvaluator.formatDecimal(it) }
                    }
                    output = "Trn($selectedMat) =\n$str"
                    saveToMatAns(trn)
                }
            }
        } catch (e: Exception) {
            output = "Error: ${e.message}"
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MATRIX - $selectedMat",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD600),
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "Mode 4: MatA, MatB, MatC, MatD & MatAns",
                            fontSize = 10.sp,
                            color = Color(0xFF9EA3AD)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF2C323D))

                // Matrix Selection Tabs: MatA, MatB, MatC, MatD, MatAns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("MatA", "MatB", "MatC", "MatD", "MatAns").forEach { m ->
                        val isSel = selectedMat == m
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFF0277BD) else Color(0xFF252932))
                                .border(1.dp, if (isSel) Color(0xFF40C4FF) else Color(0xFF3B414E), RoundedCornerShape(6.dp))
                                .clickable { selectedMat = m }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(m, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 10.sp)
                        }
                    }
                }

                // Dimension Switcher (2x2 vs 3x3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(2 to "2 × 2", 3 to "3 × 3").forEach { (s, label) ->
                        val isSelected = size == s
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF0277BD) else Color(0xFF252932))
                                .border(1.dp, if (isSelected) Color(0xFF40C4FF) else Color(0xFF3B414E), RoundedCornerShape(6.dp))
                                .clickable { size = s }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                // Matrix Grid Input
                val currentGrid = matrixMemory[selectedMat] ?: matrixMemory["MatA"]!!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF20232A))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("$selectedMat Elements ($size×$size):", fontSize = 11.sp, color = Color(0xFFFFD600), fontWeight = FontWeight.Bold)
                    for (r in 0 until size) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (c in 0 until size) {
                                OutlinedTextField(
                                    value = currentGrid[r][c],
                                    onValueChange = { currentGrid[r][c] = it },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    textStyle = LocalTextStyle.current.copy(
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF00E5FF),
                                        unfocusedBorderColor = Color(0xFF3B414E)
                                    ),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                // Output Buttons: Det, Inv, MatA×MatB, Trn
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Det" to "Det", "Inv" to "Inv", "MatA×MatB" to "MatA×MatB", "Trn" to "Trn").forEach { (op, label) ->
                        Button(
                            onClick = { calculate(op) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp)
                        ) {
                            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Output display box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF101216))
                        .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "Output:\n$output",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA6BEA0),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Save: MatAns",
                            fontSize = 10.sp,
                            color = Color(0xFFFFCC00),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Insert to calc & Close Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val ins = if (output.contains("=")) output.substringAfter("=").trim().substringBefore("\n") else selectedMat
                            onInsertResult(ins)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("INSERT TO CALC", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CLOSE", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
