package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KeyAction

/**
 * Centered Chrome D-PAD container housing 4 directional arrow buttons (▲, ▼, ◀, ▶)
 * and center REPLAY hub, perfectly aligned and unified.
 */
@Composable
fun ReplayPad(
    onAction: (KeyAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    fun performClick(action: KeyAction) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        onAction(action)
    }

    // Outer unified chrome container
    Box(
        modifier = modifier
            .size(94.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            // Chrome metallic radial gradient
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF7A8391),
                        Color(0xFF4C5462),
                        Color(0xFF2B303A),
                        Color(0xFF181B22)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFF8B94A2),
                        Color(0xFFFFFFFF),
                        Color(0xFF5A616E),
                        Color(0xFFFFFFFF)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // UP BUTTON (▲)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
                .width(42.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { performClick(KeyAction.CursorUp) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▲",
                fontSize = 13.sp,
                color = Color(0xFFF0F4F8),
                fontWeight = FontWeight.Black
            )
        }

        // DOWN BUTTON (▼)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
                .width(42.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { performClick(KeyAction.CursorDown) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▼",
                fontSize = 13.sp,
                color = Color(0xFFF0F4F8),
                fontWeight = FontWeight.Black
            )
        }

        // LEFT BUTTON (◀)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp)
                .width(28.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { performClick(KeyAction.CursorLeft) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "◀",
                fontSize = 12.sp,
                color = Color(0xFFF0F4F8),
                fontWeight = FontWeight.Black
            )
        }

        // RIGHT BUTTON (▶)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp)
                .width(28.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { performClick(KeyAction.CursorRight) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▶",
                fontSize = 12.sp,
                color = Color(0xFFF0F4F8),
                fontWeight = FontWeight.Black
            )
        }

        // CENTER CHROME HUB with inscribed REPLAY
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF383E4A),
                            Color(0xFF22262F),
                            Color(0xFF13151A)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF868F9E),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "REPLAY",
                fontSize = 6.5.sp,
                color = Color(0xFFBAC2CF),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp
            )
        }
    }
}
