package com.example.electronichome.presentation.meters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.model.MeterReadingResponse
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
private val MONTHS = (1..12).map {
    it to Month.of(it).getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru"))
        .replaceFirstChar { c -> c.uppercase() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetersScreen(
    apartment: ApartmentResponse,
    onNavigateBack: () -> Unit,
    viewModel: MetersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(apartment.id) {
        viewModel.loadReadings(apartment.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Показания счётчиков") },
                windowInsets = WindowInsets.statusBars,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ApartmentHeader(apartment)
            Spacer(Modifier.height(16.dp))
            MonthYearPicker(
                selectedMonth = state.selectedMonth,
                selectedYear  = state.selectedYear,
                onMonthChange = viewModel::setMonth,
                onYearChange  = viewModel::setYear
            )
            Spacer(Modifier.height(20.dp))
            MeterSection(
                title = "Горячая вода",
                unit  = "м³",
                color = Color(0xFFE53935),
                value = state.hotWater,
                onChange = { viewModel.updateField("hotWater", it) }
            )
            MeterSection(
                title = "Холодная вода",
                unit  = "м³",
                color = Color(0xFF1E88E5),
                value = state.coldWater,
                onChange = { viewModel.updateField("coldWater", it) }
            )
            MeterSection(
                title = "Отопление",
                unit  = "Гкал",
                color = Color(0xFFF57C00),
                value = state.heating,
                onChange = { viewModel.updateField("heating", it) }
            )
            Text(
                text     = "Электричество",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            MeterSection(
                title = "День (Т1)",
                unit  = "кВт·ч",
                color = Color(0xFF8E24AA),
                value = state.elecDay,
                onChange = { viewModel.updateField("elecDay", it) }
            )
            MeterSection(
                title = "Ночь (Т2)",
                unit  = "кВт·ч",
                color = Color(0xFF5E35B1),
                value = state.elecNight,
                onChange = { viewModel.updateField("elecNight", it) }
            )
            MeterSection(
                title = "Пик (Т3)",
                unit  = "кВт·ч",
                color = Color(0xFF3949AB),
                value = state.elecPeak,
                onChange = { viewModel.updateField("elecPeak", it) }
            )
            Spacer(Modifier.height(8.dp))

            state.error?.let {
                Card(
                    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text     = it,
                        color    = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick  = { viewModel.submit(apartment.id) },
                enabled  = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color       = Color.White
                    )
                } else {
                    Text("Передать показания", fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick  = viewModel::toggleArchive,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    if (state.showArchive) Icons.Outlined.KeyboardArrowUp
                    else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Архивные показания")
            }

            if (state.showArchive) {
                Spacer(Modifier.height(8.dp))
                ArchiveSection(readings = state.readings)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ApartmentHeader(apartment: ApartmentResponse) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text       = apartment.accountNumber ?: "—",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Text(
                text     = buildString {
                    append("г. ${apartment.city}, ${apartment.street}, ")
                    append("д. ${apartment.house}")
                    apartment.building?.let { append(", корп. $it") }
                    append(", кв. ${apartment.apartment}")
                },
                fontSize = 12.sp,
                color    = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun MonthYearPicker(
    selectedMonth: Int,
    selectedYear: Int,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit
) {
    var showMonthMenu by remember { mutableStateOf(false) }
    val currentYear = java.time.LocalDate.now().year
    val years = (currentYear - 2..currentYear).toList().reversed()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick  = { showMonthMenu = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(MONTHS.first { it.first == selectedMonth }.second)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Outlined.KeyboardArrowDown, null, Modifier.size(16.dp))
            }
            DropdownMenu(
                expanded        = showMonthMenu,
                onDismissRequest = { showMonthMenu = false }
            ) {
                MONTHS.forEach { (num, name) ->
                    DropdownMenuItem(
                        text    = { Text(name) },
                        onClick = { onMonthChange(num); showMonthMenu = false },
                        leadingIcon = if (num == selectedMonth) ({
                            Icon(Icons.Outlined.Check, null, Modifier.size(16.dp))
                        }) else null
                    )
                }
            }
        }

        var showYearMenu by remember { mutableStateOf(false) }
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick  = { showYearMenu = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedYear.toString())
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Outlined.KeyboardArrowDown, null, Modifier.size(16.dp))
            }
            DropdownMenu(
                expanded        = showYearMenu,
                onDismissRequest = { showYearMenu = false }
            ) {
                years.forEach { year ->
                    DropdownMenuItem(
                        text    = { Text(year.toString()) },
                        onClick = { onYearChange(year); showYearMenu = false },
                        leadingIcon = if (year == selectedYear) ({
                            Icon(Icons.Outlined.Check, null, Modifier.size(16.dp))
                        }) else null
                    )
                }
            }
        }
    }
}

@Composable
private fun MeterSection(
    title: String,
    unit: String,
    color: Color,
    value: String,
    onChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = unit,  fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
        Spacer(Modifier.width(12.dp))
        MeterInput(value = value, color = color, onChange = onChange)
    }
}

@Composable
private fun MeterInput(
    value: String,
    color: Color,
    onChange: (String) -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    val parts = value.padStart(5, ' ').take(5)

    BasicTextField(
        value         = value,
        onValueChange = onChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        cursorBrush   = SolidColor(color),
        singleLine    = true,
        decorationBox = {
            Row(
                modifier = Modifier
                    .border(1.5.dp, color.copy(alpha = 0.4f), shape)
                    .clip(shape)
                    .background(color.copy(alpha = 0.04f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                parts.forEachIndexed { index, char ->
                    val isSeparator = value.contains('.') && index == value.indexOf('.').coerceAtMost(4)
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 44.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (char.isDigit()) color.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text      = if (char == ' ') "" else char.toString(),
                            fontSize  = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color     = if (char.isDigit()) color else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun ArchiveSection(readings: List<MeterReadingResponse>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text       = "Показания прошлых месяцев",
            fontWeight = FontWeight.SemiBold,
            fontSize   = 15.sp,
            modifier   = Modifier.padding(bottom = 8.dp)
        )
        if (readings.isEmpty()) {
            Text(
                text     = "Нет архивных данных",
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        } else {
            readings.forEach { reading ->
                ArchiveCard(reading)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ArchiveCard(reading: MeterReadingResponse) {
    val monthName = MONTHS.first { it.first == reading.month }.second

    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text       = "$monthName ${reading.year}",
                fontWeight = FontWeight.Medium,
                fontSize   = 14.sp
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier                = Modifier.fillMaxWidth(),
                horizontalArrangement   = Arrangement.SpaceBetween
            ) {
                ArchiveValue("ГВС",  reading.hotWater,  Color(0xFFE53935), "м³")
                ArchiveValue("ХВС",  reading.coldWater, Color(0xFF1E88E5), "м³")
                ArchiveValue("Отоп", reading.heating,   Color(0xFFF57C00), "Гкал")
                ArchiveValue("Т1",   reading.elecDay,   Color(0xFF8E24AA), "кВт")
                ArchiveValue("Т2",   reading.elecNight, Color(0xFF5E35B1), "кВт")
                ArchiveValue("Т3",   reading.elecPeak,  Color(0xFF3949AB), "кВт")
            }
        }
    }
}

@Composable
private fun ArchiveValue(
    label: String,
    value: Double?,
    color: Color,
    unit: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = color, fontWeight = FontWeight.Medium)
        Text(
            text     = value?.let { "%.1f".format(it) } ?: "—",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color    = MaterialTheme.colorScheme.onSurface
        )
        Text(text = unit, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}