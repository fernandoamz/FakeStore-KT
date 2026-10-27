package com.example.fakestore.feature.products.llm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.util.Locale

/**
 * Speech-to-text via the system RecognizerIntent UI.
 * Requests RECORD_AUDIO when needed, then fills [onResult] with the best match.
 */
@Composable
fun rememberVoiceInputLauncher(
    onResult: (String) -> Unit,
    onError: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val latestOnResult by rememberUpdatedState(onResult)
    val latestOnError by rememberUpdatedState(onError)
    var pendingLaunch by remember { mutableStateOf(false) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            return@rememberLauncherForActivityResult
        }
        val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val text = matches?.firstOrNull()?.trim().orEmpty()
        if (text.isEmpty()) {
            latestOnError("No se entendió el audio. Intenta de nuevo.")
        } else {
            latestOnResult(text)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingLaunch) {
            pendingLaunch = false
            launchSpeechRecognizer(
                context = context,
                speechLauncher = speechLauncher::launch,
                onError = latestOnError
            )
        } else if (!granted) {
            pendingLaunch = false
            latestOnError("Se necesita permiso de micrófono para dictar.")
        }
    }

    return {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            launchSpeechRecognizer(
                context = context,
                speechLauncher = speechLauncher::launch,
                onError = latestOnError
            )
        } else {
            pendingLaunch = true
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}

private fun launchSpeechRecognizer(
    context: android.content.Context,
    speechLauncher: (Intent) -> Unit,
    onError: (String) -> Unit
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "MX").toLanguageTag())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "¿En qué te ayudo?")
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
    }
    if (intent.resolveActivity(context.packageManager) == null) {
        onError("Reconocimiento de voz no disponible en este dispositivo.")
        return
    }
    try {
        speechLauncher(intent)
    } catch (_: Exception) {
        onError("No se pudo abrir el micrófono.")
    }
}
