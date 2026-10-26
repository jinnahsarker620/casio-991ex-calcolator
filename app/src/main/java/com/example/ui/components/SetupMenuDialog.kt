package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AngleUnit
import com.example.model.BaseMode
import com.example.model.CalculatorState
import com.example.ui.KeyAction

/**
 * CASIO ClassWiz SETUP & MENU popup allowing the user to select
 * Calculation Mode, Angle Unit (DEG/RAD/GRAD), Base-N (DEC/HEX/BIN/OCT),
 * and view stored variables.
 */
@Composable
fun SetupMenuDialog(
    state: CalculatorState,
    onDismiss: () -> Unit,
    onAction: (KeyAction) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E2126),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.5.dp, Color(0xFF3E434D), RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MENU / SETUP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFF333842))

                Text(
                    text = "Angle Unit (Trigonometry):",
                    fontSize = 12.sp,
                    color = Color(0xFFFBC02D),
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AngleUnit.values().forEach { unit ->
                        val isSelected = state.angleUnit == unit
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF0277BD) else Color(0xFF2B2F38))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF40C4FF) else Color(0xFF454B57),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    onAction(KeyAction.SetAngleUnit(unit))
                                    onDismiss()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${unit.symbol}: ${unit.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Calculation / Base-N Mode:",
                    fontSize = 12.sp,
                    color = Color(0xFFFBC02D),
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BaseMode.values().forEach { mode ->
                        val isSelected = state.baseMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF2E7D32) else Color(0xFF2B2F38))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF81C784) else Color(0xFF454B57),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    onAction(KeyAction.SetBaseMode(mode))
                                    onDismiss()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.symbol,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Variables / Memory view
                Text(
                    text = "Stored Memory Variables:",
                    fontSize = 12.sp,
                    color = Color(0xFFFF2D75),
                    fontWeight = FontWeight.SemiBold
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 130.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF14161A))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.variables.entries.toList()) { entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${entry.key} :",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF4081),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = entry.value.toString(),
                                fontSize = 12.sp,
                                color = Color(0xFFA6BEA0),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF37474F)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
