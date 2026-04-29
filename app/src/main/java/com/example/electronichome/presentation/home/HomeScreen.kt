package com.example.electronichome.presentation.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.electronichome.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.electronichome.presentation.apartments.ApartmentsViewModel
import com.example.electronichome.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ApartmentsViewModel = hiltViewModel()
) {
    val state     by viewModel.state.collectAsState()
    val primaryId by viewModel.primaryId.collectAsState()

    val primaryApt = state.apartments.firstOrNull { it.id == primaryId }
    val context = LocalContext.current
    val dispatcherPhoneNumber = "+71234567890"

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                if (primaryApt != null) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text       = primaryApt.apartment,
                            fontSize   = 90.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White,
                            lineHeight = 90.sp
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
                        Spacer(Modifier.height(3.dp))
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
                text     = "Быстрые действия",
                style    = MaterialTheme.typography.titleSmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    title = "Передать показания",
                    icon = R.drawable.ic_meters,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Meters.route) }
                )
                QuickActionCard(
                    title = "Мои заявки",
                    icon = R.drawable.ic_requests,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Requests.route) }
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    title   = "Выдача гостевого пропуска",
                    icon    = R.drawable.ic_qr,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.GuestPass.route) }
                )
                QuickActionCard(
                    title   = "Мои квартиры",
                    icon    = R.drawable.ic_apartment,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Apartments.route) }
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$dispatcherPhoneNumber")
                }
                context.startActivity(intent)
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFD5E0EC)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Позвонить",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Позвонить в диспетчерскую",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
@Composable
fun QuickActionCard(
    title: String,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick   = onClick,
        modifier  = modifier.aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD5E0EC)
        )
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint               = Color.Unspecified,
                modifier           = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text       = title,
                style      = MaterialTheme.typography.labelMedium,
                textAlign  = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}