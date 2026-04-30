package com.example.electronichome.presentation.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.electronichome.domain.model.RequestCategoryUi
import com.example.electronichome.domain.model.RequestResponse

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ManagementRequestsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ManagementViewModel = hiltViewModel()
) {
    val state            = viewModel.state.collectAsState().value
    val pullRefreshState = rememberPullRefreshState(state.isLoading, viewModel::loadRequests)

    var filterStatus          by remember { mutableStateOf("ALL") }
    var showInProgressDialog  by remember { mutableStateOf<RequestResponse?>(null) }

    val filtered = if (filterStatus == "ALL") state.requests
    else state.requests.filter { it.status == filterStatus }

    Scaffold(
        topBar = {
            TopAppBar(
                title        = { Text("Заявки жильцов") },
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
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filters = listOf(
                            "ALL"         to "Все",
                            "PENDING"     to "В обработке",
                            "IN_PROGRESS" to "В работе",
                            "DONE"        to "Выполнены"
                        )
                        items(filters) { (key, label) ->
                            FilterChip(
                                selected = filterStatus == key,
                                onClick  = { filterStatus = key },
                                label    = { Text(label, fontSize = 13.sp) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor     = Color.White
                                )
                            )
                        }
                    }
                }

                items(filtered) { req ->
                    ManagementRequestCard(
                        req          = req,
                        onInProgress = { showInProgressDialog = req },
                        onDone       = { viewModel.markDone(req.id) }
                    )
                    Spacer(Modifier.height(8.dp))
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

    showInProgressDialog?.let { req ->
        TakeInProgressDialog(
            onConfirm = { dueDate ->
                viewModel.takeInProgress(req.id, dueDate)
                showInProgressDialog = null
            },
            onDismiss = { showInProgressDialog = null }
        )
    }
}

@Composable
private fun ManagementRequestCard(
    req: RequestResponse,
    onInProgress: () -> Unit,
    onDone: () -> Unit
) {
    val cat = RequestCategoryUi.entries.firstOrNull { it.key == req.category }
    val (statusColor, statusText) = when (req.status) {
        "PENDING"     -> MaterialTheme.colorScheme.secondary to "В обработке"
        "IN_PROGRESS" -> Color(0xFF1E88E5) to "В работе"
        "DONE"        -> Color(0xFF43A047) to "Выполнена"
        else          -> MaterialTheme.colorScheme.onSurfaceVariant to req.status
    }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD5E0EC)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier              = Modifier.weight(1f)
                ) {
                    Text(
                        text       = cat?.label ?: req.category,
                        fontWeight = FontWeight.Medium,
                        fontSize   = 14.sp,
                        modifier   = Modifier.weight(1f)
                    )
                }
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text       = statusText,
                        fontSize   = 11.sp,
                        color      = statusColor,
                        fontWeight = FontWeight.Medium,
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            req.description?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            req.dueDate?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Срок: $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            when (req.status) {
                "PENDING" -> {
                    Button(
                        onClick  = onInProgress,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Взять в работу")
                    }
                }
                "IN_PROGRESS" -> {
                    Button(
                        onClick  = onDone,
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF43A047)
                        )
                    ) {
                        Icon(Icons.Outlined.CheckCircle, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Отметить выполненной")
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun TakeInProgressDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var dueDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Взять в работу") },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text  = "Укажите предварительный срок выполнения",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value         = dueDate,
                    onValueChange = { dueDate = it },
                    label         = { Text("Дата (ГГГГ-ММ-ДД)") },
                    placeholder   = { Text("2024-12-31") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = { onConfirm(dueDate) },
                enabled  = dueDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))
            ) { Text("Подтвердить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}