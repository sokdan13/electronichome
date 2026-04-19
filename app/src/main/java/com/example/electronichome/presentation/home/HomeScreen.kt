package com.example.electronichome.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    val state     by viewModel.state.collectAsState()
    val primaryId by viewModel.primaryId.collectAsState()

    val primaryApt = state.apartments.firstOrNull { it.id == primaryId }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                if (primaryApt != null) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text       = primaryApt.apartment,
                            fontSize   = 64.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White,
                            lineHeight = 64.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text     = "квартира",
                            fontSize = 18.sp,
                            color    = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                    Text(
                        text = buildString {
                            append("г. ${primaryApt.city}, ${primaryApt.street}, ")
                            append("д. ${primaryApt.house}")
                            primaryApt.building?.let { append(", корп. $it") }
                            append(", эт. ${primaryApt.floor}")
                        },
                        fontSize = 13.sp,
                        color    = Color.White.copy(alpha = 0.8f)
                    )
                    primaryApt.accountNumber?.let {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text     = "Л/С: $it",
                            fontSize = 13.sp,
                            color    = Color.White.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    Text(
                        text       = "Электронный дом",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Text(
                        text     = if (state.apartments.isEmpty())
                            "Добавьте квартиру в разделе Профиль → Квартиры"
                        else "Выберите основную квартиру в разделе Квартиры",
                        fontSize = 13.sp,
                        color    = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = "Быстрые действия",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}