package com.example.electronichome.presentation.apartments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.electronichome.domain.model.ApartmentResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentsScreen(
    onAddApartment: () -> Unit,
    viewModel: ApartmentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои квартиры") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddApartment,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Добавить", tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.apartments.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Квартир пока нет", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onAddApartment) { Text("Добавить квартиру") }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.apartments) { apt ->
                            ApartmentCard(apt)
                        }
                    }
                }
            }

            state.error?.let {
                Snackbar(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Text(it)
                }
            }
        }
    }
}

@Composable
fun ApartmentCard(apt: ApartmentResponse) {
    val (statusColor, statusText) = when (apt.status) {
        "APPROVED" -> MaterialTheme.colorScheme.primary to "Подтверждено"
        "REJECTED" -> MaterialTheme.colorScheme.error to "Отклонено"
        else       -> MaterialTheme.colorScheme.secondary to "Ожидает проверки"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (apt.status == "APPROVED") apt.apartment else "—",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (apt.status == "APPROVED") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = buildString {
                        append("${apt.street}, д. ${apt.house}")
                        apt.building?.let { append(", корп. $it") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "эт. ${apt.floor} · ${apt.city}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                apt.accountNumber?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Л/С: $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                apt.rejectionNote?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Причина: $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Surface(
                color = statusColor.copy(alpha = 0.12f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}