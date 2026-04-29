package com.example.electronichome.presentation.apartments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ApartmentsScreen(
    onAddApartment: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ApartmentsViewModel = hiltViewModel()
) {
    val state     by viewModel.state.collectAsState()
    val primaryId by viewModel.primaryId.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh  = { viewModel.loadApartments() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои квартиры") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onAddApartment,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Outlined.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.apartments) { apt ->
                    ApartmentCard(
                        apt       = apt,
                        isPrimary = apt.id == primaryId,
                        onSetPrimary = {
                            if (apt.status == "APPROVED") viewModel.setPrimary(apt.id)
                        }
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = state.isLoading,
                state      = pullRefreshState,
                modifier   = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ApartmentCard(
    apt: ApartmentResponse,
    isPrimary: Boolean,
    onSetPrimary: () -> Unit
) {
    val (statusColor, statusText) = when (apt.status) {
        "APPROVED" -> MaterialTheme.colorScheme.primary to "Подтверждено"
        "REJECTED" -> MaterialTheme.colorScheme.error to "Отклонено"
        else       -> MaterialTheme.colorScheme.secondary to "Ожидает"
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        border    = if (isPrimary) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text       = apt.apartment,
                            fontSize   = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color      = if (apt.status == "APPROVED")
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text     = "кв.",
                            fontSize = 14.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text  = buildString {
                            append("${apt.street}, д. ${apt.house}")
                            apt.building?.let { append(", корп. $it") }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text  = "эт. ${apt.floor} · ${apt.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = statusColor.copy(alpha = 0.12f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text     = statusText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style    = MaterialTheme.typography.labelSmall,
                            color    = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (isPrimary) {
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text     = "Основная",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style    = MaterialTheme.typography.labelSmall,
                                color    = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            apt.accountNumber?.let {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "Л/С: $it",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (apt.status == "APPROVED" && !isPrimary) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick  = onSetPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сделать основной")
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD5E0EC)
        )
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