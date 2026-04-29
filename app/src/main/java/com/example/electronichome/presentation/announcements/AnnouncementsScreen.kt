package com.example.electronichome.presentation.announcements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.electronichome.domain.model.AnnouncementCategoryUi
import com.example.electronichome.domain.model.AnnouncementResponse

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AnnouncementsScreen(
    viewModel: AnnouncementsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh  = viewModel::load
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title        = { Text("Объявления") },
                windowInsets = WindowInsets.statusBars,
                colors       = TopAppBarDefaults.topAppBarColors(
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
            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    CategoryFilter(
                        selected = state.selectedCategory,
                        onSelect = viewModel::selectCategory
                    )
                }

                if (state.announcements.isEmpty() && !state.isLoading) {
                    item {
                        Box(
                            modifier         = Modifier
                                .fillParentMaxWidth()
                                .padding(top = 80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = "Объявлений нет",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(state.announcements) { item ->
                    AnnouncementCard(
                        item     = item,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
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
}

@Composable
private fun CategoryFilter(
    selected: AnnouncementCategoryUi,
    onSelect: (AnnouncementCategoryUi) -> Unit
) {
    LazyRow(
        contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(AnnouncementCategoryUi.entries) { cat ->
            val isSelected = cat == selected
            FilterChip(
                selected = isSelected,
                onClick  = { onSelect(cat) },
                label    = {
                    Text(
                        text = cat.label,
                        fontSize = 13.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor    = MaterialTheme.colorScheme.primary,
                    selectedLabelColor        = Color.White
                )
            )
        }
    }
}

@Composable
private fun AnnouncementCard(
    item: AnnouncementResponse,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (item.category) {
        "IMPORTANT" -> Color(0xFFE53935)
        "NEWS"      -> Color(0xFF1E88E5)
        "TIPS"      -> Color(0xFFC29800)
        else        -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier  = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD5E0EC)
        )
    ) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment   = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl != null) {
                    AsyncImage(
                        model             = item.imageUrl,
                        contentDescription = item.title,
                        contentScale      = ContentScale.Crop,
                        modifier          = Modifier.fillMaxSize()
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = categoryColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text       = item.categoryLabel,
                            fontSize   = 10.sp,
                            color      = categoryColor,
                            fontWeight = FontWeight.Medium,
                            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text     = item.createdAt.take(10),
                        fontSize = 10.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text       = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text     = item.description,
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}