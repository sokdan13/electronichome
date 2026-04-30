package com.example.electronichome.presentation.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import com.example.electronichome.domain.model.ApproveRequest
import com.example.electronichome.domain.model.ApartmentResponse

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ManagementApartmentsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ManagementViewModel = hiltViewModel()
) {
    val state            = viewModel.state.collectAsState().value
    val pullRefreshState = rememberPullRefreshState(state.isLoading, viewModel::loadApartments)

    var showApproveDialog by remember { mutableStateOf<ApartmentResponse?>(null) }
    var showRejectDialog  by remember { mutableStateOf<ApartmentResponse?>(null) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) viewModel.clearMessages()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title        = { Text("Заявки на квартиры") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            if (state.apartments.isEmpty() && !state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text  = "Новых заявок нет",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.apartments) { apt ->
                    ManagementApartmentCard(
                        apt       = apt,
                        onApprove = { showApproveDialog = apt },
                        onReject  = { showRejectDialog  = apt }
                    )
                }
            }

            PullRefreshIndicator(
                refreshing   = state.isLoading,
                state        = pullRefreshState,
                modifier     = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }

    showApproveDialog?.let { apt ->
        ApproveApartmentDialog(
            apt       = apt,
            onConfirm = { req ->
                viewModel.approveApartment(apt.id, req)
                showApproveDialog = null
            },
            onDismiss = { showApproveDialog = null }
        )
    }

    showRejectDialog?.let { apt ->
        RejectApartmentDialog(
            onConfirm = { note ->
                viewModel.rejectApartment(apt.id, note)
                showRejectDialog = null
            },
            onDismiss = { showRejectDialog = null }
        )
    }
}

@Composable
private fun ManagementApartmentCard(
    apt: ApartmentResponse,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD5E0EC)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = buildString {
                    append("${apt.street}, д. ${apt.house}")
                    apt.building?.let { append(", корп. $it") }
                    append(", кв. ${apt.apartment}")
                },
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "г. ${apt.city} · эт. ${apt.floor}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text  = "Пользователь: ${apt.userId}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick  = onReject,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Outlined.Close, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Отклонить")
                }
                Button(
                    onClick  = onApprove,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Outlined.Check, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Подтвердить")
                }
            }
        }
    }
}

@Composable
private fun ApproveApartmentDialog(
    apt: ApartmentResponse,
    onConfirm: (ApproveRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var city      by remember { mutableStateOf(apt.city) }
    var street    by remember { mutableStateOf(apt.street) }
    var house     by remember { mutableStateOf(apt.house) }
    var building  by remember { mutableStateOf(apt.building ?: "") }
    var floor     by remember { mutableStateOf(apt.floor.toString()) }
    var apartment by remember { mutableStateOf(apt.apartment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title  = { Text("Подтвердить квартиру") },
        text   = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text  = "Проверьте и при необходимости скорректируйте адрес:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value         = city,
                    onValueChange = { city = it },
                    label         = { Text("Город") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value         = street,
                    onValueChange = { street = it },
                    label         = { Text("Улица") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value         = house,
                        onValueChange = { house = it },
                        label         = { Text("Дом") },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value         = building,
                        onValueChange = { building = it },
                        label         = { Text("Корп.") },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value         = floor,
                        onValueChange = { floor = it },
                        label         = { Text("Этаж") },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value         = apartment,
                        onValueChange = { apartment = it },
                        label         = { Text("Кв.") },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    ApproveRequest(
                        city      = city.ifBlank { null },
                        street    = street.ifBlank { null },
                        house     = house.ifBlank { null },
                        building  = building.ifBlank { null },
                        floor     = floor.toIntOrNull(),
                        apartment = apartment.ifBlank { null }
                    )
                )
            }) { Text("Подтвердить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

@Composable
private fun RejectApartmentDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title  = { Text("Отклонить квартиру") },
        text   = {
            OutlinedTextField(
                value         = note,
                onValueChange = { note = it },
                label         = { Text("Причина отклонения") },
                minLines      = 2,
                modifier      = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick  = { onConfirm(note) },
                enabled  = note.isNotBlank(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Отклонить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}