package com.example.electronichome.presentation.screens

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
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToApartments: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onLogout: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.Start
            ) {
                val initials = user?.displayName
                    ?.split(" ")
                    ?.mapNotNull { it.firstOrNull()?.toString() }
                    ?.take(2)
                    ?.joinToString("") ?: "?"

                Surface(
                    shape    = MaterialTheme.shapes.extraLarge,
                    color    = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text       = initials,
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text       = user?.displayName ?: "Пользователь",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                Text(
                    text  = user?.email ?: "",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            ProfileMenuItem(
                icon    = Icons.Outlined.Home,
                title   = "Мои квартиры",
                subtitle = "Управление квартирами и статусы",
                onClick = onNavigateToApartments
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuItem(
                icon     = Icons.Outlined.Home,
                title    = "Мои заявки",
                subtitle = "История обращений",
                onClick  = onNavigateToRequests
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileMenuItem(
                icon     = Icons.Outlined.Notifications,
                title    = "Уведомления",
                subtitle = "Настройки оповещений",
                onClick  = {}
            )
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick  = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
            )
        ) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Выйти из аккаунта")
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 15.sp
                )
                Text(
                    text     = subtitle,
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector        = Icons.Outlined.Home,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}