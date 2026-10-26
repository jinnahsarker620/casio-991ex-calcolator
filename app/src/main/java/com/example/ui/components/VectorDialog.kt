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
fun VectorDialog(
    onDismiss: () -> Unit
) {
    // STORAGE - EX memory storage (VctA, VctB, VctC, VctAns)
    var selectedVct by remember { mutableStateOf("VctA") }

    val vectorMemory = remember {
        mutableStateMapOf(
            "VctA" to mutableStateListOf("1", "2", "3"),
            "VctB" to mutableStateListOf("4", "5", "6"),
            "VctC" to mutableStateListOf("0", "1", "0"),
            "VctAns" to mutableStateListOf("0", "0", "0")
        )
    }

    var output by remember { mutableStateOf("0") }

    fun getVector(name: String): AdvancedMathEngine.Vector3 {
        val list = vectorMemory[name] ?: return AdvancedMathEngine.Vector3(0.0, 0.0, 0.0)
        return AdvancedMathEngine.Vector3(
            list[0].toDoubleOrNull() ?: 0.0,
            list[1].toDoubleOrNull() ?: 0.0,
            list[2].toDoubleOrNull() ?: 0.0
        )
    }

    fun saveToVctAns(v: AdvancedMathEngine.Vector3) {
        val ans = vectorMemory["VctAns"] ?: return
        ans[0] = MathEvaluator.formatDecimal(v.x)
        ans[1] = MathEvaluator.formatDecimal(v.y)
        ans[2] = MathEvaluator.formatDecimal(v.z)
    }

    fun calculate(op: String) {
        try {
            val vA = getVector("VctA")
            val vB = getVector("VctB")
            val vCurr = getVector(selectedVct)

            when (op) {
                "Dot" -> {
                    val d = vA.dot(vB)
                    output = "VctA • VctB = ${MathEvaluator.formatDecimal(d)}"
                    saveToVctAns(AdvancedMathEngine.Vector3(d, 0.0, 0.0))
                }
                "Cross" -> {
                    val cr = vA.cross(vB)
                    output = "VctA × VctB =\n(${MathEvaluator.formatDecimal(cr.x)}, ${MathEvaluator.formatDecimal(cr.y)}, ${MathEvaluator.formatDecimal(cr.z)})"
                    saveToVctAns(cr)
                }
                "Angle" -> {
                    val ang = vA.angleWith(vB)
                    output = "Angle(VctA, VctB) = ${MathEvaluator.formatDecimal(ang)}°"
                }
                "|V|" -> {
                    val mag = vCurr.magnitude()
                    output = "|$selectedVct| = ${MathEvaluator.formatDecimal(mag)}"
                    saveToVctAns(AdvancedMathEngine.Vector3(mag, 0.0, 0.0))
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
                            text = "VECTOR - $selectedVct",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFF9100),
                            fontFamily = FontFamily.SansSerif
                        )
                        Text("Mode 5: VctA, VctB, VctC & VctAns (Dot, Cross, Angle)", fontSize = 10.sp, color = Color(0xFF9EA3AD))
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF2C323D))

                // Vector Selection Tabs: VctA, VctB, VctC, VctAns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("VctA", "VctB", "VctC", "VctAns").forEach { v ->
                        val isSel = selectedVct == v
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFF0277BD) else Color(0xFF252932))
                                .border(1.dp, if (isSel) Color(0xFF40C4FF) else Color(0xFF3B414E), RoundedCornerShape(6.dp))
                                .clickable { selectedVct = v }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(v, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }

                // Vector Elements Input
                val currentVec = vectorMemory[selectedVct] ?: vectorMemory["VctA"]!!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF20232A))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("$selectedVct Components (x₁, x₂, x₃):", fontSize = 11.sp, color = Color(0xFFFF9100), fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (i in 0 until 3) {
                            OutlinedTextField(
                                value = currentVec[i],
                                onValueChange = { currentVec[i] = it },
                                modifier = Modifier.weight(1f).height(48.dp),
                                textStyle = LocalTextStyle.current.copy(
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                label = { Text("x${i + 1}", fontSize = 9.sp, color = Color(0xFF9EA3AD)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00E5FF),
                                    unfocusedBorderColor = Color(0xFF3B414E)
                                ),
                                singleLine = true
                            )
                        }
                    }
                }

                // Operations: Dot, Cross, Angle, |V|
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Dot" to "Dot •", "Cross" to "Cross ×", "Angle" to "Angle", "|V|" to "|V|").forEach { (op, label) ->
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
                            text = "Save: VctAns",
                            fontSize = 10.sp,
                            color = Color(0xFFFFCC00),
                            fontWeight = FontWeight.SemiBold
                        )
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
