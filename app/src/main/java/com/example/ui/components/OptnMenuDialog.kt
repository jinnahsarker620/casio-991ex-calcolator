package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

data class OptnItem(
    val title: String,
    val description: String,
    val insertSnippet: String,
    val category: String
)

val OPTN_CALCULUS_ITEMS = listOf(
    // Mode 2: Complex functions
    OptnItem("1: arg", "Argument / Angle: arg(z)", "arg(", "Complex"),
    OptnItem("2: Conjg", "Conjugate: conjg(z)", "conjg(", "Complex"),
    OptnItem("3: ReP", "Real Part: ReP(z)", "ReP(", "Complex"),
    OptnItem("4: ImP", "Imaginary Part: ImP(z)", "ImP(", "Complex"),
    OptnItem("5: ▶r∠θ", "Convert to Polar Form", "▶r∠θ", "Complex"),
    OptnItem("6: ▶a+bi", "Convert to Rectangular Form", "▶a+bi", "Complex"),
    OptnItem("7: ∠", "Angle symbol for polar notation", "∠", "Complex"),

    // Mode 4: Matrix
    OptnItem("1: Define Matrix", "Open Matrix Definition & Memory", "MATRIX_DEF", "Matrix"),
    OptnItem("2: MatA", "Matrix variable MatA", "MatA", "Matrix"),
    OptnItem("3: MatB", "Matrix variable MatB", "MatB", "Matrix"),
    OptnItem("4: MatC", "Matrix variable MatC", "MatC", "Matrix"),
    OptnItem("5: MatD", "Matrix variable MatD", "MatD", "Matrix"),
    OptnItem("6: MatAns", "Matrix answer memory", "MatAns", "Matrix"),
    OptnItem("7: Det", "Determinant: Det(", "Det(", "Matrix"),
    OptnItem("8: Trn", "Transpose: Trn(", "Trn(", "Matrix"),

    // Mode 5: Vector
    OptnItem("1: Define Vector", "Open Vector Definition & Memory", "VECTOR_DEF", "Vector"),
    OptnItem("2: VctA", "Vector variable VctA", "VctA", "Vector"),
    OptnItem("3: VctB", "Vector variable VctB", "VctB", "Vector"),
    OptnItem("4: VctC", "Vector variable VctC", "VctC", "Vector"),
    OptnItem("5: VctAns", "Vector answer memory", "VctAns", "Vector"),
    OptnItem("6: Dot", "Dot product: Dot(", "Dot(", "Vector"),
    OptnItem("7: Angle", "Angle between vectors: Angle(", "Angle(", "Vector"),
    OptnItem("8: UnitVct", "Unit vector: UnitVct(", "UnitVct(", "Vector"),

    // 1. Calculus & Advanced
    OptnItem("∫□ dx", "Numerical Integration: ∫(f(x), a, b)", "∫(", "Calculus"),
    OptnItem("d/dx", "Numerical Derivative: d/dx(f(x), x₀)", "d/dx(", "Calculus"),
    OptnItem("Σ□", "Summation: Σ(f(x), start, end)", "Σ(", "Calculus"),
    OptnItem("Abs", "Absolute value: abs(x)", "abs(", "Calculus"),
    OptnItem("logₐ(b)", "Log with base: log(b, x)", "log(", "Calculus"),

    // 2. Hyperbolic functions
    OptnItem("sinh", "Hyperbolic sine: sinh(x)", "sinh(", "Hyperbolic"),
    OptnItem("cosh", "Hyperbolic cosine: cosh(x)", "cosh(", "Hyperbolic"),
    OptnItem("tanh", "Hyperbolic tangent: tanh(x)", "tanh(", "Hyperbolic"),
    OptnItem("sinh⁻¹", "Inverse hyperbolic sine", "asinh(", "Hyperbolic"),
    OptnItem("cosh⁻¹", "Inverse hyperbolic cosine", "acosh(", "Hyperbolic"),
    OptnItem("tanh⁻¹", "Inverse hyperbolic tangent", "atanh(", "Hyperbolic"),

    // 3. Probability & Coordinates
    OptnItem("nPr", "Permutation: nPr(n, r)", "npr(", "Probability"),
    OptnItem("nCr", "Combination: nCr(n, r)", "ncr(", "Probability"),
    OptnItem("Pol", "Rectangular to Polar (r, θ)", "pol(", "Coordinate"),
    OptnItem("Rec", "Polar to Rectangular (x, y)", "rec(", "Coordinate"),
    OptnItem("Ran#", "Random uniform number [0, 1)", "ran#", "Probability"),
    OptnItem("RanInt", "Random integer [a, b]", "ranInt(", "Probability"),

    // 4. Angle units
    OptnItem("° (Degree)", "Forces degree unit", "°", "Angle Unit"),
    OptnItem("r (Radian)", "Forces radian unit", "r", "Angle Unit"),
    OptnItem("g (Gradian)", "Forces gradian unit", "g", "Angle Unit")
)

@Composable
fun OptnMenuDialog(
    currentMode: Int = 1,
    onInsertSnippet: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialCat = when (currentMode) {
        2 -> "Complex"
        4 -> "Matrix"
        5 -> "Vector"
        else -> "Calculus"
    }
    var selectedCategory by remember(currentMode) { mutableStateOf(initialCat) }
    val categories = when (currentMode) {
        2 -> listOf("Complex", "Calculus", "Hyperbolic", "Angle Unit")
        4 -> listOf("Matrix", "Calculus", "Angle Unit")
        5 -> listOf("Vector", "Calculus", "Angle Unit")
        else -> listOf("Calculus", "Hyperbolic", "Probability", "Angle Unit")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF16181C),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
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
                            text = "OPTN FUNCTIONS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFCC00), // Yellow accent
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Calculus, Hyperbolics & Advanced Tools",
                            fontSize = 10.sp,
                            color = Color(0xFF00E5FF)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF2A2E36))

                // Category selector tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF0277BD) else Color(0xFF22262E))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF40C4FF) else Color(0xFF363B47),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFFB0B7C3),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Function items list
                val filteredItems = OPTN_CALCULUS_ITEMS.filter { it.category == selectedCategory }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredItems) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF20232A))
                                .border(1.dp, Color(0xFF323742), RoundedCornerShape(6.dp))
                                .clickable {
                                    onInsertSnippet(item.insertSnippet)
                                    onDismiss()
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFCC00),
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = item.description,
                                    fontSize = 10.sp,
                                    color = Color(0xFFA0A7B5)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF0277BD))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "INSERT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF37474F)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("CLOSE", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
