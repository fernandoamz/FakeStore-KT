package com.example.fakestore.feature.products.address.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class AddressViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState.asStateFlow()

    fun onPostalCodeChanged(code: String) {
        val filtered = code.filter { it.isDigit() }.take(5)
        val isValid = filtered.length == 5
        _uiState.update { it.copy(postalCode = filtered, isPostalCodeValid = isValid) }
    }

    fun onFieldChanged(field: AddressField, value: String) {
        _uiState.update {
            when (field) {
                AddressField.STATE -> it.copy(state = value)
                AddressField.CITY -> it.copy(city = value)
                AddressField.COLONY -> it.copy(colony = value)
                AddressField.STREET -> it.copy(street = value)
                AddressField.EXT_NUMBER -> it.copy(exteriorNumber = value)
                AddressField.INT_NUMBER -> it.copy(interiorNumber = value)
                AddressField.REFERENCES -> it.copy(references = value)
                AddressField.RECIPIENT -> it.copy(recipientName = value)
                AddressField.PHONE -> it.copy(phoneNumber = value)
            }
        }
    }

    fun saveAddress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(800) // Simulate persistence
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}

enum class AddressField {
    STATE, CITY, COLONY, STREET, EXT_NUMBER, INT_NUMBER, REFERENCES, RECIPIENT, PHONE
}
