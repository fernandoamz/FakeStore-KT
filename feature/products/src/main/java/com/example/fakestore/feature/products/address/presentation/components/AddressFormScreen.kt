package com.example.fakestore.feature.products.address.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fakestore.feature.products.address.presentation.components.AddressTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressFormScreen(
    initialPostalCode: String,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AddressViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agrega tu dirección") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Información de ubicación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                AddressTextField(initialPostalCode, { }, "Código Postal", keyboardType = KeyboardType.Number)
            }
            item {
                AddressTextField(state.state, { viewModel.onFieldChanged(AddressField.STATE, it) }, "Estado / Provincia")
            }
            item {
                AddressTextField(state.city, { viewModel.onFieldChanged(AddressField.CITY, it) }, "Ciudad / Municipio")
            }
            item {
                AddressTextField(state.colony, { viewModel.onFieldChanged(AddressField.COLONY, it) }, "Colonia / Barrio")
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Calle y Número", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                AddressTextField(state.street, { viewModel.onFieldChanged(AddressField.STREET, it) }, "Calle")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        AddressTextField(state.exteriorNumber, { viewModel.onFieldChanged(AddressField.EXT_NUMBER, it) }, "Nº Exterior")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AddressTextField(state.interiorNumber, { viewModel.onFieldChanged(AddressField.INT_NUMBER, it) }, "Nº Interior", isOptional = true)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Contacto y Referencias", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                AddressTextField(state.recipientName, { viewModel.onFieldChanged(AddressField.RECIPIENT, it) }, "Nombre del destinatario")
            }
            item {
                AddressTextField(state.phoneNumber, { viewModel.onFieldChanged(AddressField.PHONE, it) }, "Teléfono de contacto", keyboardType = KeyboardType.Phone)
            }
            item {
                AddressTextField(state.references, { viewModel.onFieldChanged(AddressField.REFERENCES, it) }, "Referencias (ej. color de casa, portón)", isOptional = true)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.saveAddress()
                        onSaveSuccess()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Guardar dirección", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
