package com.example.electronichome.presentation.guestpass

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.model.PassDuration
import com.example.electronichome.utils.generateQrBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestPassScreen(
    apartment: ApartmentResponse,
    onNavigateBack: () -> Unit,
    viewModel: GuestPassViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title        = { Text("Гостевой пропуск") },
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
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = state.activePass,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { activePass ->
                if (activePass != null) {
                    ActivePassContent(
                        token      = activePass.token,
                        secondsLeft = state.secondsLeft,
                        apartment  = apartment
                    )
                } else {
                    CreatePassContent(
                        selectedDuration = state.selectedDuration,
                        isLoading        = state.isLoading,
                        error            = state.error,
                        onSelectDuration = viewModel::selectDuration,
                        onCreatePass     = { viewModel.createPass(apartment.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivePassContent(
    token: String,
    secondsLeft: Long,
    apartment: ApartmentResponse
) {
    val qrBitmap = remember(token) { generateQrBitmap(token) }
    val minutes  = secondsLeft / 60
    val seconds  = secondsLeft % 60

    val timerColor = when {
        secondsLeft > 300 -> Color(0xFF43A047)
        secondsLeft > 60  -> Color(0xFFF57C00)
        else              -> Color(0xFFE53935)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text       = "кв. ${apartment.apartment}",
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text  = "${apartment.street}, д. ${apartment.house}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Image(
                bitmap             = qrBitmap.asImageBitmap(),
                contentDescription = "QR пропуск",
                modifier           = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(8.dp))


        Surface(
            color = timerColor.copy(alpha = 0.12f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier            = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text       = "%02d:%02d".format(minutes, seconds),
                    fontSize   = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color      = timerColor
                )
                Text(
                    text  = "до истечения пропуска",
                    fontSize = 12.sp,
                    color = timerColor.copy(alpha = 0.7f)
                )
            }
        }

        Text(
            text      = "Покажите QR-код на входе",
            fontSize  = 13.sp,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CreatePassContent(
    selectedDuration: PassDuration,
    isLoading: Boolean,
    error: String?,
    onSelectDuration: (PassDuration) -> Unit,
    onCreatePass: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier            = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector        = Icons.Outlined.Lock,
            contentDescription = null,
            modifier           = Modifier.size(80.dp),
            tint               = MaterialTheme.colorScheme.primary
        )

        Text(
            text       = "Гостевой пропуск",
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text      = "Выберите срок действия пропуска.\nГость покажет QR-код на входе.",
            fontSize  = 14.sp,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier              = Modifier.fillMaxWidth()
        ) {
            PassDuration.entries.forEach { duration ->
                val isSelected = selectedDuration == duration
                FilterChip(
                    selected = isSelected,
                    onClick  = { onSelectDuration(duration) },
                    label    = {
                        Text(
                            text      = duration.label,
                            textAlign = TextAlign.Center,
                            fontSize  = 13.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor     = Color.White
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Button(
            onClick  = onCreatePass,
            enabled  = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color       = Color.White
                )
            } else {
                Text("Выдать пропуск", fontSize = 16.sp)
            }
        }
    }
}