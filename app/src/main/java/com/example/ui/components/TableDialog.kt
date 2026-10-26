package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class TableRowData(val x: Double, val fx: String)

@Composable
fun TableDialog(
    onDismiss: () -> Unit
) {
    var fxFormula by remember { mutableStateOf("2x² - 3x + 1") }
    var startVal by remember { mutableStateOf("-2") }
    var endVal by remember { mutableStateOf("5") }
    var stepVal by remember { mutableStateOf("1") }

    val evaluator = remember { MathEvaluator() }
    var tableRows by remember { mutableStateOf<List<TableRowData>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    fun generateTable() {
        try {
            val start = startVal.toDoubleOrNull() ?: 0.0
            val end = endVal.toDoubleOrNull() ?: 10.0
            val step = stepVal.toDoubleOrNull() ?: 1.0

            if (step <= 0 || end < start) {
                errorMessage = "Invalid range: end must be >= start, step > 0"
                return
            }
            if ((end - start) / step > 100) {
                errorMessage = "Too many steps (max 100)"
                return
            }

            val list = mutableListOf<TableRowData>()
            var currentX = start
            while (currentX <= end + 1e-9) {
                val subExpr = fxFormula.replace(Regex("""\b[xX]\b"""), "($currentX)")
                val eval = evaluator.evaluate(subExpr)
                val resStr = when (eval) {
                    is MathEvaluator.EvalResult.Success -> eval.standardFormatted
                    is MathEvaluator.EvalResult.Error -> "ERROR"
                }
                list.add(TableRowData(currentX, resStr))
                currentX += step
            }
            tableRows = list
            errorMessage = ""
        } catch (e: Exception) {
            errorMessage = e.message ?: "Evaluation Error"
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
                            text = "TABLE GENERATOR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFF5252),
                            fontFamily = FontFamily.SansSerif
                        )
                        Text("Mode 9: Function Table f(x)", fontSize = 10.sp, color = Color(0xFF9EA3AD))
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF2C323D))

                Text("f(x) Formula:", fontSize = 11.sp, color = Color(0xFFFFCC00), fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = fxFormula,
                    onValueChange = { fxFormula = it },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = startVal, onValueChange = { startVal = it }, label = { Text("Start") }, modifier = Modifier.weight(1f).height(50.dp), singleLine = true)
                    OutlinedTextField(value = endVal, onValueChange = { endVal = it }, label = { Text("End") }, modifier = Modifier.weight(1f).height(50.dp), singleLine = true)
                    OutlinedTextField(value = stepVal, onValueChange = { stepVal = it }, label = { Text("Step") }, modifier = Modifier.weight(1f).height(50.dp), singleLine = true)
                }

                Button(
                    onClick = { generateTable() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("GENERATE TABLE", fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color(0xFFFF5252), fontSize = 11.sp)
                }

                if (tableRows.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF20232A))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("x", fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF), fontSize = 12.sp)
                        Text("f(x)", fontWeight = FontWeight.Bold, color = Color(0xFFFFCC00), fontSize = 12.sp)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF101216))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(tableRows) { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(MathEvaluator.formatDecimal(row.x), color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text(row.fx, color = Color(0xFFA6BEA0), fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
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
