package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class ConverterCategory(val displayName: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMP("Temp"),
    CURRENCY("Currency"),
    AREA("Area"),
    SPEED("Speed")
}

data class UnitDef(val code: String, val label: String, val factorToBase: Double = 1.0)

val LENGTH_UNITS = listOf(
    UnitDef("mm", "Millimeter (mm)", 0.001),
    UnitDef("cm", "Centimeter (cm)", 0.01),
    UnitDef("m", "Meter (m)", 1.0),
    UnitDef("km", "Kilometer (km)", 1000.0),
    UnitDef("inch", "Inch (in)", 0.0254),
    UnitDef("ft", "Feet (ft)", 0.3048),
    UnitDef("mile", "Mile (mi)", 1609.344)
)

val WEIGHT_UNITS = listOf(
    UnitDef("mg", "Milligram (mg)", 0.001),
    UnitDef("g", "Gram (g)", 1.0),
    UnitDef("kg", "Kilogram (kg)", 1000.0),
    UnitDef("ton", "Metric Ton (ton)", 1000000.0),
    UnitDef("lb", "Pound (lb)", 453.59237),
    UnitDef("oz", "Ounce (oz)", 28.349523125)
)

val TEMP_UNITS = listOf(
    UnitDef("C", "Celsius (°C)"),
    UnitDef("F", "Fahrenheit (°F)"),
    UnitDef("K", "Kelvin (K)")
)

// Offline fixed rates against base USD
val CURRENCY_UNITS = listOf(
    UnitDef("USD", "US Dollar (USD)", 1.0),
    UnitDef("BDT", "Bangladeshi Taka (BDT)", 122.5),
    UnitDef("INR", "Indian Rupee (INR)", 86.8),
    UnitDef("EUR", "Euro (EUR)", 0.92)
)

// Area with Bangladesh local units: 1 acre = 4046.856 m2, 1 bigha = 1337.8 m2, 1 decimal = 40.4686 m2
val AREA_UNITS = listOf(
    UnitDef("m2", "Square Meter (m²)", 1.0),
    UnitDef("km2", "Square Kilometer (km²)", 1000000.0),
    UnitDef("acre", "Acre (acre)", 4046.8564224),
    UnitDef("bigha", "Bigha (বিঘা)", 1337.803776),
    UnitDef("decimal", "Decimal / Shatak (শতাংশ)", 40.468564224)
)

val SPEED_UNITS = listOf(
    UnitDef("m/s", "Meter per second (m/s)", 1.0),
    UnitDef("km/h", "Kilometer per hour (km/h)", 1.0 / 3.6),
    UnitDef("mph", "Mile per hour (mph)", 0.44704)
)

/**
 * Unit Converter Page (SHIFT + 8 / CONV) designed as a dark popup overlay.
 */
@Composable
fun UnitConverterDialog(
    initialValue: String = "1",
    onInsertResult: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ConverterCategory.LENGTH) }
    var inputValue by remember { mutableStateOf(if (initialValue.isEmpty() || initialValue == "0") "1" else initialValue) }

    // Units for current category
    val currentUnits = remember(selectedCategory) {
        when (selectedCategory) {
            ConverterCategory.LENGTH -> LENGTH_UNITS
            ConverterCategory.WEIGHT -> WEIGHT_UNITS
            ConverterCategory.TEMP -> TEMP_UNITS
            ConverterCategory.CURRENCY -> CURRENCY_UNITS
            ConverterCategory.AREA -> AREA_UNITS
            ConverterCategory.SPEED -> SPEED_UNITS
        }
    }

    var fromUnit by remember(selectedCategory) { mutableStateOf(currentUnits[0]) }
    var toUnit by remember(selectedCategory) {
        mutableStateOf(if (currentUnits.size > 1) currentUnits[1] else currentUnits[0])
    }

    // Conversion calculation
    val resultString = remember(inputValue, fromUnit, toUnit, selectedCategory) {
        val inputDouble = inputValue.toDoubleOrNull() ?: 0.0

        val converted = when (selectedCategory) {
            ConverterCategory.TEMP -> {
                convertTemperature(inputDouble, fromUnit.code, toUnit.code)
            }
            ConverterCategory.CURRENCY -> {
                // Currency rate relative to USD: base = val / fromRate; to = base * toRate
                val fromRate = fromUnit.factorToBase
                val toRate = toUnit.factorToBase
                (inputDouble / fromRate) * toRate
            }
            else -> {
                val inBase = inputDouble * fromUnit.factorToBase
                inBase / toUnit.factorToBase
            }
        }

        formatResult(converted)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, Color(0xFF383E4C), RoundedCornerShape(16.dp)),
            color = Color(0xFF131519)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // ================= TOP BAR =================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF00E5FF))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Back",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Unit Converter",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF),
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 0.5.sp
                    )

                    // Spacer to balance back button
                    Spacer(modifier = Modifier.width(60.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ================= TABS =================
                ScrollableTabRow(
                    selectedTabIndex = selectedCategory.ordinal,
                    containerColor = Color(0xFF1D2128),
                    contentColor = Color(0xFFFBC02D),
                    edgePadding = 6.dp,
                    divider = {}
                ) {
                    ConverterCategory.values().forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Tab(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            text = {
                                Text(
                                    text = cat.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFFFBC02D) else Color(0xFF9E9E9E)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ================= FROM SECTION =================
                Text(
                    text = "FROM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF383E4C),
                            focusedContainerColor = Color(0xFF1A1D23),
                            unfocusedContainerColor = Color(0xFF1A1D23)
                        )
                    )

                    UnitDropdown(
                        units = currentUnits,
                        selectedUnit = fromUnit,
                        onUnitSelected = { fromUnit = it },
                        modifier = Modifier.weight(1.4f)
                    )
                }

                // Swap Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            val temp = fromUnit
                            fromUnit = toUnit
                            toUnit = temp
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2B313D))
                            .border(1.dp, Color(0xFF4C5566), RoundedCornerShape(20.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap units",
                            tint = Color(0xFFFBC02D)
                        )
                    }
                }

                // ================= TO SECTION =================
                Text(
                    text = "TO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF2D75),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Result Display Box
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF222822)) // Greenish LCD tint
                            .border(1.dp, Color(0xFF869E81), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = resultString,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA6BEA0), // LCD green
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }

                    UnitDropdown(
                        units = currentUnits,
                        selectedUnit = toUnit,
                        onUnitSelected = { toUnit = it },
                        modifier = Modifier.weight(1.4f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Formula summary explanation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B1E24))
                        .border(1.dp, Color(0xFF2C313C), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Conversion Summary:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9E9E9E)
                        )
                        Text(
                            text = "$inputValue ${fromUnit.code} = $resultString ${toUnit.code}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF00E5FF),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Buttons: Insert to Calculator & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF454B57)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onInsertResult(resultString) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Use in Calculator", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun UnitDropdown(
    units: List<UnitDef>,
    selectedUnit: UnitDef,
    onUnitSelected: (UnitDef) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF242831))
                .border(1.dp, Color(0xFF383E4C), RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedUnit.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = "▼",
                    fontSize = 10.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color(0xFF1E2128))
                .border(1.dp, Color(0xFF383E4C))
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = unit.label,
                            fontSize = 13.sp,
                            color = if (unit == selectedUnit) Color(0xFF00E5FF) else Color.White,
                            fontWeight = if (unit == selectedUnit) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun convertTemperature(value: Double, fromCode: String, toCode: String): Double {
    val kelvin = when (fromCode) {
        "C" -> value + 273.15
        "F" -> (value - 32.0) * 5.0 / 9.0 + 273.15
        else -> value
    }
    return when (toCode) {
        "C" -> kelvin - 273.15
        "F" -> (kelvin - 273.15) * 9.0 / 5.0 + 32.0
        else -> kelvin
    }
}

private fun formatResult(value: Double): String {
    if (value.isNaN() || value.isInfinite()) return "0"
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("#,##0.######", symbols)
    df.isGroupingUsed = false
    return df.format(value)
}
