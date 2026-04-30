package com.example.electronichome.presentation.management

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.electronichome.presentation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementHomeScreen(navController: NavController,onLogout: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text       = user?.displayName ?: user?.email ?: "УК",
                    fontSize   = 13.sp,
                    color      = Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = "Управляющая компания",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text       = "Управление",
                style      = MaterialTheme.typography.titleSmall,
                color      = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ManagementMenuCard(
                icon     = Icons.Outlined.Home,
                title    = "Заявки на квартиры",
                subtitle = "Подтверждение и выдача лицевых счетов",
                color    = MaterialTheme.colorScheme.primary,
                onClick  = { navController.navigate(Screen.ManagementApartments.route) }
            )

            ManagementMenuCard(
                icon     = Icons.Outlined.Home,
                title    = "Заявки жильцов",
                subtitle = "Управление обращениями и статусами",
                color    = Color(0xFF1E88E5),
                onClick  = { navController.navigate(Screen.ManagementRequests.route) }
            )
            ManagementMenuCard(
                icon     = Icons.Outlined.Home,
                title    = "Завершение работы",
                subtitle = "Выйти из аккаунта",
                color    = Color(0xFFE51E3C),
                onClick  = onLogout
            )
        }
    }
}

@Composable
private fun ManagementMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD5E0EC)
        )
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color  = color.copy(alpha = 0.12f),
                shape  = MaterialTheme.shapes.medium,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = icon,
                        contentDescription = null,
                        tint               = color,
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(
                    text  = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}