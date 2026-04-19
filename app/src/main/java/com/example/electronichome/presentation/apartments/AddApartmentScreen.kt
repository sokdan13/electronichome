package com.example.electronichome.presentation.apartments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.electronichome.domain.model.ApartmentRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddApartmentScreen(
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ApartmentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var city     by remember { mutableStateOf("") }
    var street   by remember { mutableStateOf("") }
    var house    by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("") }
    var floor    by remember { mutableStateOf("") }
    var apartment by remember { mutableStateOf("") }

    LaunchedEffect(state.isAddSuccess) {
        if (state.isAddSuccess) onSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить квартиру") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Город *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = street,
                onValueChange = { street = it },
                label = { Text("Улица *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = house,
                    onValueChange = { house = it },
                    label = { Text("Дом *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = building,
                    onValueChange = { building = it },
                    label = { Text("Корпус") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = floor,
                    onValueChange = { floor = it },
                    label = { Text("Этаж *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = apartment,
                    onValueChange = { apartment = it },
                    label = { Text("Квартира *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            state.error?.let {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    viewModel.addApartment(
                        ApartmentRequest(
                            city      = city.trim(),
                            street    = street.trim(),
                            house     = house.trim(),
                            building  = building.trim().ifBlank { null },
                            floor     = floor.toIntOrNull() ?: 0,
                            apartment = apartment.trim()
                        )
                    )
                },
                enabled = !state.isLoading &&
                        city.isNotBlank() && street.isNotBlank() &&
                        house.isNotBlank() && floor.isNotBlank() &&
                        apartment.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("Отправить заявку")
                }
            }

            Text(
                text = "После отправки УК проверит данные и подтвердит квартиру",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}