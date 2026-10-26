package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Realistic solar cell panel found on the top-right of the CASIO fx-991EX CLASSWIZ.
 */
@Composable
fun SolarPanel(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .width(96.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E1719),
                            Color(0xFF332024),
                            Color(0xFF271A1E),
                            Color(0xFF140E10)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF4A4446),
                    shape = RoundedCornerShape(4.dp)
                )
                .drawBehind {
                    // Draw 4 solar grid cells with fine metallic separators
                    val cellWidth = size.width / 4f
                    for (i in 1..3) {
                        drawLine(
                            color = Color(0xFF6E5E63),
                            start = Offset(cellWidth * i, 2f),
                            end = Offset(cellWidth * i, size.height - 2f),
                            strokeWidth = 1f
                        )
                    }
                    // Subtle glossy glare highlight across top-left to bottom-right
                    drawLine(
                        color = Color(0x33FFFFFF),
                        start = Offset(4f, 2f),
                        end = Offset(size.width - 10f, 2f),
                        strokeWidth = 2f
                    )
                }
        )
        Text(
            text = "TWO WAY POWER",
            fontSize = 7.sp,
            color = Color(0xFF8E8E93),
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(top = 2.dp, end = 2.dp)
        )
    }
}
