package com.example.electronichome.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.presentation.apartments.ApartmentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ApartmentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val approvedApt = state.apartments.firstOrNull { it.status == "APPROVED" }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                if (approvedApt != null) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                        Text(
                            text = approvedApt.apartment,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 64.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "квартира",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text(
                        text = buildString {
                            append("г. ${approvedApt.city}, ${approvedApt.street}, ")
                            append("д. ${approvedApt.house}")
                            approvedApt.building?.let { append(", корп. $it") }
                            append(", эт. ${approvedApt.floor}")
                        },
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    approvedApt.accountNumber?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Л/С: $it",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    Text(
                        text = "Электронный дом",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (state.apartments.isEmpty()) "Добавьте квартиру в разделе «Квартиры»"
                        else "Квартира ожидает подтверждения УК",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Быстрые действия",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}