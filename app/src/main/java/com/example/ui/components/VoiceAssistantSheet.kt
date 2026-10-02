package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.PrimaryIndigo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VoiceAssistantSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSubmitCommand: (String) -> Unit,
    confirmationPrompt: String?,
    onConfirmAction: () -> Unit,
    onDismissConfirmation: () -> Unit
) {
    if (!isVisible) return

    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var spokenText by remember { mutableStateOf("") }
    var inputQuery by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("Tap the microphone to speak, or choose an example below.") }

    var speechRecognizer by remember {
        mutableStateOf<SpeechRecognizer?>(null)
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                if (speechRecognizer == null && SpeechRecognizer.isRecognitionAvailable(context)) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                }
                startSpeechListening(context, speechRecognizer, { isListening = it }, { spokenText = it }, { statusText = it })
            } catch (_: Throwable) {
                statusText = "Microphone unavailable. You can tap examples or type below."
            }
        } else {
            statusText = "Microphone permission is off. You can tap examples or type below."
        }
    }

    DisposableEffect(Unit) {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            }
        } catch (_: Throwable) {
            speechRecognizer = null
        }

        onDispose {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.destroy()
            } catch (_: Throwable) {}
            speechRecognizer = null
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("voice_assistant_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "What can I help with?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Microphone Action
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(if (isListening) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            if (isListening) {
                                try {
                                    speechRecognizer?.stopListening()
                                } catch (_: Throwable) {}
                                isListening = false
                                statusText = "Tap mic to speak"
                            } else {
                                try {
                                    if (speechRecognizer == null && SpeechRecognizer.isRecognitionAvailable(context)) {
                                        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                                    }
                                    startSpeechListening(context, speechRecognizer, { isListening = it }, { spokenText = it }, { statusText = it })
                                } catch (_: Throwable) {
                                    statusText = "Microphone unavailable. Use suggestions or type below."
                                }
                            }
                        } else {
                            recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                    .testTag("mic_toggle_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Microphone",
                    tint = if (isListening) Color.White else PrimaryIndigo,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (spokenText.isNotBlank()) "\"$spokenText\"" else statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (spokenText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                MomOSPrimaryButton(
                    text = "Run Command",
                    onClick = {
                        onSubmitCommand(spokenText)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Suggestions Header
            Text(
                text = "Tap any example or speak naturally:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Example Suggestion Chips as defined in specification
            val suggestions = listOf(
                "Add milk to my shopping list",
                "Remind me to call Geetesh at 7",
                "What's on today?",
                "Add ₹250 for groceries",
                "Show my shopping list",
                "Call Dad"
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { suggestion ->
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onSubmitCommand(suggestion) }
                            .testTag("suggestion_chip"),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Fallback input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MomOSTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = "Or type here (e.g. 'Add tomatoes')",
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    testTag = "voice_text_input"
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            onSubmitCommand(inputQuery)
                            inputQuery = ""
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryIndigo)
                        .testTag("send_command_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Safety Confirmation Dialog (e.g. for phone call)
    if (confirmationPrompt != null) {
        AlertDialog(
            onDismissRequest = onDismissConfirmation,
            title = {
                Text(
                    text = "Confirmation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = confirmationPrompt,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmAction,
                    modifier = Modifier.testTag("confirm_action_button")
                ) {
                    Text(
                        text = "Call",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissConfirmation,
                    modifier = Modifier.testTag("cancel_action_button")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun startSpeechListening(
    context: Context,
    speechRecognizer: SpeechRecognizer?,
    setListening: (Boolean) -> Unit,
    setSpokenText: (String) -> Unit,
    setStatusText: (String) -> Unit
) {
    if (speechRecognizer == null) {
        setStatusText("Speech recognizer unavailable. You can tap suggestions or type.")
        return
    }

    try {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                setListening(true)
                setStatusText("Listening... Speak now.")
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                setListening(false)
            }
            override fun onError(error: Int) {
                setListening(false)
                setStatusText("Tap mic to try speaking again or use suggestions below.")
            }
            override fun onResults(results: Bundle?) {
                setListening(false)
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val firstMatch = matches?.firstOrNull() ?: ""
                if (firstMatch.isNotBlank()) {
                    setSpokenText(firstMatch)
                    setStatusText("Heard: \"$firstMatch\"")
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull() ?: ""
                if (partial.isNotBlank()) {
                    setSpokenText(partial)
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    } catch (_: Throwable) {
        setListening(false)
        setStatusText("Microphone unavailable. Please use suggestions or type.")
    }
}
