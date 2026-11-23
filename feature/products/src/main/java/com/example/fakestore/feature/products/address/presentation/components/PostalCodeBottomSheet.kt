package com.example.fakestore.feature.products.address.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostalCodeBottomSheet(
    onDismissRequest: () -> Unit,
    onConfirmPostalCode: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    var postalCode by remember { mutableStateOf("") }
    val isValid = postalCode.length == 5

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            PostalCodeHeader()

            PostalCodeInput(
                value = postalCode,
                onValueChange = { postalCode = it },
                errorMessage = if (postalCode.isNotEmpty() && !isValid) "El código postal debe tener 5 dígitos" else null,
                isError = postalCode.isNotEmpty() && !isValid
            )

            Button(
                onClick = {
                    if (isValid) {
                        onConfirmPostalCode(postalCode)
                        onDismissRequest()
                    }
                },
                enabled = isValid,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Continuar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
