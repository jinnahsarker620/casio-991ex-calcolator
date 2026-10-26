package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

data class ModeMenuItem(
    val id: Int,
    val title: String,
    val sub: String,
    val iconSymbol: String,
    val badgeColor: Color = Color(0xFF00E5FF)
)

val CLASSWIZ_MODES = listOf(
    ModeMenuItem(1, "Calculate", "General calculations, calculus", "1: +−×÷", Color(0xFF00E5FF)),
    ModeMenuItem(2, "Complex", "a+bi, r∠θ, arg, conj", "2: i", Color(0xFFFF4081)),
    ModeMenuItem(3, "Base-N", "HEX / BIN / OCT / DEC", "3: 1010", Color(0xFF00E676)),
    ModeMenuItem(4, "Matrix", "MatA..MatD, Det, Trn, Inv", "4: [::]", Color(0xFFFFD600)),
    ModeMenuItem(5, "Vector", "Dot, Cross, Angle, UnitV", "5: →u", Color(0xFFFF9100)),
    ModeMenuItem(6, "Statistics", "1-Var & 2-Var Regression", "6: Σx", Color(0xFF7C4DFF)),
    ModeMenuItem(7, "Distribution", "Normal, Binomial, Poisson", "7: N(μ,σ)", Color(0xFF00B0FF)),
    ModeMenuItem(8, "Spreadsheet", "5-col × 45-row grid", "8: ⊞", Color(0xFF26A69A)),
    ModeMenuItem(9, "Table", "f(x), g(x) Value tables", "9: f(x)", Color(0xFFFF5252)),
    ModeMenuItem(10, "Equation/Func", "Simultaneous & Polynomial", "A: ax²", Color(0xFFE040FB)),
    ModeMenuItem(11, "Inequality", "Polynomial inequalities", "B: < >", Color(0xFF40C4FF)),
    ModeMenuItem(12, "Ratio", "A:B = X:D / A:B = C:X", "C: A:B", Color(0xFFFFAB40))
)

@Composable
fun ModeMenuDialog(
    currentMode: Int,
    onSelectMode: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF181A1F),
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .border(1.5.dp, Color(0xFF3A404D), RoundedCornerShape(14.dp))
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
                            text = "MODE MENU",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E5FF),
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "fx-991EX CLASSWIZ 552 Functions",
                            fontSize = 10.sp,
                            color = Color(0xFFFFCC00),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF2C323D))

                // Authentic ClassWiz LCD Display Mode Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFB8C8B8))
                        .border(1.dp, Color(0xFF6B7F6B), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "1:Calculate  2:Complex",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF1B281B)
                            )
                            Text(
                                text = "3:Base-N",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF1B281B)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "4:Matrix     5:Vector",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF1B281B)
                            )
                            Text(
                                text = "6:Table",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF1B281B)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "7:Equation   8:Inequal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF1B281B)
                            )
                            Text(
                                text = "9:Verify",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF1B281B)
                            )
                        }
                    }
                }

                // Quick direct keypad selection buttons [1]-[9], [A], [B], [C]
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "PRESS 1-9 OR A-C TO SELECT MODE:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFCC00),
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            1 to "1", 2 to "2", 3 to "3", 4 to "4",
                            5 to "5", 6 to "6"
                        ).forEach { (id, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (id == currentMode) Color(0xFF0277BD) else Color(0xFF262A34))
                                    .border(1.dp, if (id == currentMode) Color(0xFF40C4FF) else Color(0xFF3E4554), RoundedCornerShape(4.dp))
                                    .clickable {
                                        onSelectMode(id)
                                        onDismiss()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (id == currentMode) Color.White else Color(0xFF00E5FF)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            7 to "7", 8 to "8", 9 to "9",
                            10 to "A", 11 to "B", 12 to "C"
                        ).forEach { (id, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (id == currentMode) Color(0xFF0277BD) else Color(0xFF262A34))
                                    .border(1.dp, if (id == currentMode) Color(0xFF40C4FF) else Color(0xFF3E4554), RoundedCornerShape(4.dp))
                                    .clickable {
                                        onSelectMode(id)
                                        onDismiss()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (id == currentMode) Color.White else Color(0xFFFFD600)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF2C323D))

                // 12-Item Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(CLASSWIZ_MODES) { mode ->
                        val isSelected = mode.id == currentMode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF0277BD), Color(0xFF01579B))
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF252932), Color(0xFF1E2128))
                                        )
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF40C4FF) else Color(0xFF383E4C),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onSelectMode(mode.id)
                                    onDismiss()
                                }
                                .padding(horizontal = 6.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                // Symbol badge
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF121418))
                                        .border(1.dp, mode.badgeColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val badgeText = when (mode.id) {
                                        10 -> "A"
                                        11 -> "B"
                                        12 -> "C"
                                        else -> mode.id.toString()
                                    }
                                    Text(
                                        text = badgeText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = mode.badgeColor
                                    )
                                }

                                Text(
                                    text = mode.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )

                                Text(
                                    text = mode.sub,
                                    fontSize = 8.5.sp,
                                    color = Color(0xFFA5ACB8),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
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
                    Text("CLOSE", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}
