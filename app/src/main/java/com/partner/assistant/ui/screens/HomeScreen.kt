package com.partner.assistant.ui.screens

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.partner.assistant.commands.CommandParser
import com.partner.assistant.data.NoteEntity
import com.partner.assistant.data.PartnerDatabase
import com.partner.assistant.device.ActionHandler
import com.partner.assistant.ui.components.GlowingMic
import com.partner.assistant.ui.state.ListeningStatus
import com.partner.assistant.ui.theme.*
import com.partner.assistant.voice.SpeechToTextManager
import com.partner.assistant.voice.TextToSpeechManager
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    database: PartnerDatabase,
    listeningStatus: ListeningStatus,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = database.partnerDao()

    var recognizedText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }

    val ttsManager = remember { TextToSpeechManager(context) }
    val actionHandler = remember { ActionHandler(context) }
    val commandParser = remember { CommandParser() }

    val sttManager = remember {
        SpeechToTextManager(
            context = context,
            onResult = { text ->
                recognizedText = text
                isListening = false
                val command = commandParser.parse(text)

                when (command) {
                    "ACTION_FLASHLIGHT" -> {
                        val isOn = actionHandler.toggleFlashlight()
                        ttsManager.speak(if (isOn) "Flashlight on." else "Flashlight off.")
                    }

                    "ACTION_VOLUME" -> {
                        val level = commandParser.extractVolumeLevel(text)
                        val response = actionHandler.setVolume(level)
                        ttsManager.speak(response)
                    }

                    "ACTION_OPEN_YOUTUBE" -> {
                        val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                        if (intent != null) {
                            context.startActivity(intent)
                            ttsManager.speak("Opening YouTube.")
                        } else {
                            ttsManager.speak("YouTube is not installed.")
                        }
                    }

                    "ACTION_CALL" -> {
                        val number = commandParser.extractNumber(text)
                        if (number.isNotEmpty()) {
                            ttsManager.speak(actionHandler.makeCall(number))
                        } else {
                            ttsManager.speak("Please provide a number to call.")
                        }
                    }

                    "ACTION_ALARM" -> {
                        val hour = commandParser.extractHour(text)
                        ttsManager.speak(actionHandler.setAlarm(hour))
                    }

                    "ACTION_NOTE" -> {
                        val noteContent = commandParser.extractNote(text)
                        if (noteContent.isNotEmpty()) {
                            scope.launch {
                                dao.insertNote(NoteEntity(content = noteContent))
                                ttsManager.speak("I saved a note: $noteContent")
                            }
                        } else {
                            ttsManager.speak("What would you like me to note?")
                        }
                    }

                    "ACTION_GREETING" -> {
                        ttsManager.speak("Hello. How can I help you?")
                    }

                    else -> {
                        ttsManager.speak("I heard you say: $text, but I don't know that command.")
                    }
                }
            },
            onError = { error ->
                recognizedText = "Error: $error"
                isListening = false
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isListening = true
            sttManager.startListening()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
            sttManager.destroy()
        }
    }

    // --- UI Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Menu action later */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = TextPrimary)
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Titles
        Text("Partner", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Your Personal AI Assistant", fontSize = 16.sp, color = TextSecondary)

        Spacer(modifier = Modifier.weight(1f))

        // Glowing Mic Animation
        GlowingMic(isListening = isListening)

        Spacer(modifier = Modifier.height(32.dp))

        Text("Hey Partner", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = TextPrimary)

        Text(
            text = if (recognizedText.isNotEmpty()) "\"$recognizedText\"" else "How can I help you today?",
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Listen Button
        Button(
            onClick = {
                val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

                // Phase 4 Security Check
                if (keyguardManager.isKeyguardLocked) {
                    ttsManager.speak("Please unlock your phone first.")
                    return@Button
                }

                val hasPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    isListening = true
                    recognizedText = ""
                    sttManager.startListening()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, NeonPurple)
        ) {
            Text(
                text = if (isListening) "Listening..." else "Say \"Hey Partner\" to start",
                color = TextPrimary,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}