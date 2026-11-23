package com.example.fakestore.feature.products.address.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PostalCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= 5 && newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        placeholder = { Text("Ej. 28869") },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError,
        supportingText = errorMessage?.let { { Text(it) } },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}
