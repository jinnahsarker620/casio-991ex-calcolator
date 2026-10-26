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

data class ScientificConstant(
    val symbol: String,
    val name: String,
    val insertValue: String,
    val displayValue: String,
    val unit: String
)

val SCIENTIFIC_CONSTANTS = listOf(
    ScientificConstant("π", "Pi (Circle ratio)", "π", "3.14159265359", ""),
    ScientificConstant("e", "Euler's number", "e", "2.71828182846", ""),
    ScientificConstant("g", "Standard gravity", "9.80665", "9.80665", "m/s²"),
    ScientificConstant("c", "Speed of light in vacuum", "299792458", "299792458", "m/s"),
    ScientificConstant("h", "Planck constant", "6.62607015×10^(-34)", "6.62607×10⁻³⁴", "J·s"),
    ScientificConstant("G", "Gravitational constant", "6.67430×10^(-11)", "6.67430×10⁻¹¹", "N·m²/kg²"),
    ScientificConstant("N_A", "Avogadro constant", "6.02214076×10^(23)", "6.02214×10²³", "mol⁻¹"),
    ScientificConstant("R", "Molar gas constant", "8.314462618", "8.31446", "J/(mol·K)"),
    ScientificConstant("k", "Boltzmann constant", "1.380649×10^(-23)", "1.38065×10⁻²³", "J/K"),
    ScientificConstant("e_c", "Elementary charge", "1.602176634×10^(-19)", "1.60218×10⁻¹⁹", "C"),
    ScientificConstant("m_e", "Electron mass", "9.1093837×10^(-31)", "9.10938×10⁻³¹", "kg"),
    ScientificConstant("m_p", "Proton mass", "1.67262192×10^(-27)", "1.67262×10⁻²⁷", "kg"),
    ScientificConstant("m_n", "Neutron mass", "1.6749275×10^(-27)", "1.67493×10⁻²⁷", "kg"),
    ScientificConstant("atm", "Standard atmosphere", "101325", "101325", "Pa"),
    ScientificConstant("σ", "Stefan-Boltzmann constant", "5.670374419×10^(-8)", "5.67037×10⁻⁸", "W/(m²·K⁴)"),
    ScientificConstant("μ₀", "Magnetic constant", "1.25663706×10^(-6)", "1.25664×10⁻⁶", "N/A²"),
    ScientificConstant("ε₀", "Electric constant", "8.85418781×10^(-12)", "8.85419×10⁻¹²", "F/m")
)

/**
 * Scientific Constants (SHIFT + 7 / CONST) selection modal for CASIO ClassWiz.
 */
@Composable
fun ConstantsDialog(
    onSelectConstant: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SCIENTIFIC_CONSTANTS
        } else {
            val q = searchQuery.trim().lowercase()
            SCIENTIFIC_CONSTANTS.filter {
                it.symbol.lowercase().contains(q) ||
                it.name.lowercase().contains(q) ||
                it.displayValue.contains(q)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B1E24),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.5.dp, Color(0xFF3E4450), RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SCIENTIFIC CONSTANTS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBC02D), // ClassWiz golden yellow
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Tap any constant to insert into formula",
                            fontSize = 11.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, symbol...", fontSize = 12.sp, color = Color(0xFF757575)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFBC02D),
                        unfocusedBorderColor = Color(0xFF3E4450),
                        focusedContainerColor = Color(0xFF121418),
                        unfocusedContainerColor = Color(0xFF121418)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // List of Constants
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF242831))
                                .border(1.dp, Color(0xFF333845), RoundedCornerShape(8.dp))
                                .clickable {
                                    onSelectConstant(item.insertValue)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.symbol,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00E5FF),
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = item.name,
                                        fontSize = 12.sp,
                                        color = Color(0xFFE0E0E0),
                                        maxLines = 1
                                    )
                                }
                                if (item.unit.isNotEmpty()) {
                                    Text(
                                        text = "Unit: ${item.unit}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF9E9E9E)
                                    )
                                }
                            }

                            // Value display
                            Text(
                                text = item.displayValue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFA6BEA0), // Greenish LCD style
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
