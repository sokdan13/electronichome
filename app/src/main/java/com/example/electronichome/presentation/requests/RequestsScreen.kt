package com.example.electronichome.presentation.requests

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.model.RequestCategoryUi
import com.example.electronichome.domain.model.RequestCreateDto
import com.example.electronichome.domain.model.RequestResponse

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun RequestsScreen(
    apartment: ApartmentResponse,
    onNavigateBack: () -> Unit,
    viewModel: RequestsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value
    var showForm by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh  = viewModel::loadRequests
    )

    LaunchedEffect(state.isSubmitSuccess) {
        if (state.isSubmitSuccess) {
            showForm = false
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои заявки") },
                windowInsets = WindowInsets.statusBars,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showForm = !showForm }) {
                        Icon(
                            if (showForm) Icons.Outlined.Close else Icons.Outlined.Add,
                            null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    AnimatedVisibility(visible = showForm) {
                        RequestForm(
                            apartment  = apartment,
                            isLoading  = state.isLoading,
                            error      = state.error,
                            onSubmit   = { dto -> viewModel.submitRequest(dto) },
                            onDismiss  = { showForm = false }
                        )
                    }
                }

                if (state.requests.isEmpty() && !showForm) {
                    item {
                        Box(
                            modifier          = Modifier.fillParentMaxSize(),
                            contentAlignment  = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text  = "Заявок пока нет",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                TextButton(onClick = { showForm = true }) {
                                    Text("Создать заявку")
                                }
                            }
                        }
                    }
                }

                items(state.requests) { req ->
                    RequestCard(req)
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
}

@Composable
private fun RequestForm(
    apartment: ApartmentResponse,
    isLoading: Boolean,
    error: String?,
    onSubmit: (RequestCreateDto) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<RequestCategoryUi?>(null) }
    var description      by remember { mutableStateOf("") }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD5E0EC)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text       = "Новая заявка",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 16.sp
            )
            Text(
                text  = buildString {
                    append("кв. ${apartment.apartment} · ${apartment.street}, д. ${apartment.house}")
                    if (!apartment.building.isNullOrBlank()) {
                        append(", корп. ${apartment.building}")
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text  = "Категория",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RequestCategoryUi.entries.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                else Color.Transparent
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = cat.label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            OutlinedTextField(
                value       = description,
                onValueChange = { description = it },
                label       = { Text("Описание проблемы (необязательно)") },
                minLines    = 3,
                maxLines    = 5,
                modifier    = Modifier.fillMaxWidth()
            )

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick  = onDismiss,
                    modifier = Modifier.weight(1f)
                ) { Text("Отмена") }

                Button(
                    onClick  = {
                        selectedCategory?.let { cat ->
                            onSubmit(
                                RequestCreateDto(
                                    apartmentId = apartment.id,
                                    category    = cat.key,
                                    description = description.trim().ifBlank { null }
                                )
                            )
                        }
                    },
                    enabled  = selectedCategory != null && !isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color       = Color.White
                        )
                    } else {
                        Text("Отправить")
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestCard(req: RequestResponse) {
    val (statusColor, statusIcon) = when (req.status) {
        "PENDING"     -> MaterialTheme.colorScheme.secondary to Icons.Outlined.Home
        "IN_PROGRESS" -> Color(0xFF1E88E5) to Icons.Outlined.Build
        "DONE"        -> Color(0xFF43A047) to Icons.Outlined.CheckCircle
        else          -> MaterialTheme.colorScheme.onSurfaceVariant to Icons.Outlined.Info
    }

    val cat = RequestCategoryUi.entries.firstOrNull { it.key == req.category }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD5E0EC)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment      = Alignment.CenterVertically,
                horizontalArrangement  = Arrangement.SpaceBetween,
                modifier               = Modifier.fillMaxWidth()
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
                    Row(
                        modifier              = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector        = statusIcon,
                            contentDescription = null,
                            tint               = statusColor,
                            modifier           = Modifier.size(12.dp)
                        )
                        Text(
                            text      = req.statusLabel,
                            fontSize  = 11.sp,
                            color     = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            req.description?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            req.dueDate?.let {
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.DateRange,
                        null,
                        Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text  = "Срок: $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text  = req.createdAt.take(10),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}