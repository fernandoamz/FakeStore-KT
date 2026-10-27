package com.example.fakestore.feature.products.llm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fakestore.domain.model.ChatMessage
import com.example.fakestore.domain.model.ChatRole
import com.example.fakestore.feature.products.cart.CartViewModel
import com.example.fakestore.feature.products.components.AppBottomBar
import com.example.fakestore.feature.products.components.BottomTab
import com.example.fakestore.feature.products.components.ScaffoldZeroInsets
import kotlinx.coroutines.launch

private val AssistantAccent = Color(0xFF3483FA)
private val SuggestionBorder = Color(0xFFD0D5DD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LLMScreen(
    viewModel: LLMViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    onCameraClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cartUiState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val listState = rememberLazyListState()

    val startVoiceInput = rememberVoiceInputLauncher(
        onResult = { spoken ->
            inputText = spoken
        },
        onError = { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    )

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.messages.size, uiState.isSending) {
        if (uiState.messages.isNotEmpty() || uiState.isSending) {
            listState.animateScrollToItem(
                index = (uiState.messages.size + if (uiState.isSending) 1 else 0).coerceAtLeast(1) - 1
            )
        }
    }

    fun handlePrompt(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty()) return
        inputText = ""
        viewModel.sendMessage(trimmed)
    }

    Scaffold(
        contentWindowInsets = ScaffoldZeroInsets,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            LLMTopBar(
                onBackClick = onBackClick,
                onNotificationClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Sin notificaciones nuevas")
                    }
                },
                onHistoryClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Historial en memoria de esta sesión")
                    }
                },
                onNewChatClick = {
                    inputText = ""
                    viewModel.clearConversation()
                }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (uiState.showEmptyState) {
                    EmptyAssistantState(
                        suggestions = uiState.suggestions,
                        onSuggestionClick = ::handlePrompt,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ChatMessagesList(
                        messages = uiState.messages,
                        isSending = uiState.isSending,
                        listState = listState,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            LLMInputBar(
                value = inputText,
                onValueChange = { inputText = it },
                enabled = !uiState.isSending,
                onCameraClick = onCameraClick,
                onMicClick = startVoiceInput,
                onSend = { handlePrompt(inputText) }
            )

            if (!isKeyboardOpen) {
                AppBottomBar(
                    currentRoute = BottomTab.LLM.route,
                    cartItemCount = cartUiState.totalItems,
                    onTabSelected = onTabSelected
                )
            }
        }
    }
}

@Composable
private fun EmptyAssistantState(
    suggestions: List<AssistantSuggestion>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "¿Qué buscas hoy?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 32.sp
        )

        Text(
            text = "Te ayudo a encontrar productos del catálogo, comparar precios y decidir qué comprar.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        suggestions.forEach { suggestion ->
            SuggestionButton(
                emoji = suggestion.emoji,
                text = suggestion.label,
                onClick = { onSuggestionClick(suggestion.prompt) }
            )
        }
    }
}

@Composable
private fun ChatMessagesList(
    messages: List<ChatMessage>,
    isSending: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = messages,
            key = { it.id }
        ) { message ->
            ChatBubble(message = message)
        }

        if (isSending) {
            item(key = "typing") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = AssistantAccent
                            )
                            Text(
                                text = "Pensando...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == ChatRole.USER
    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isUser) AssistantAccent else MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.widthIn(max = if (isUser) 320.dp else 360.dp)
        ) {
            Text(
                text = if (isUser) {
                    AnnotatedString(message.content)
                } else {
                    parseAssistantMarkdown(message.content)
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = textColor,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun LLMTopBar(
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onNewChatClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = AssistantAccent,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = "Asistente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        Row {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onHistoryClick) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Historial",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onNewChatClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Nueva conversación",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SuggestionButton(
    emoji: String,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, SuggestionBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LLMInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    onCameraClick: () -> Unit,
    onMicClick: () -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCameraClick, enabled = enabled) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cámara",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = "¿En qué te ayudo?",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            enabled = enabled,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(AssistantAccent),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { if (enabled) onSend() }),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (value.isNotBlank()) {
                        TextButton(onClick = onSend, enabled = enabled) {
                            Text("Enviar", color = AssistantAccent, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        IconButton(onClick = onMicClick, enabled = enabled) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Micrófono",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
