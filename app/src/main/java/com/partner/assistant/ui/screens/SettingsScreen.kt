package com.partner.assistant.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.partner.assistant.data.PartnerDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    database: PartnerDatabase,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("partner_prefs", Context.MODE_PRIVATE)
    var listeningEnabled by remember { mutableStateOf(prefs.getBoolean("listening_enabled", false)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Permissions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Toggle Section
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enable Voice Listening", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Microphone integration arrives in Phase 3.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = listeningEnabled,
                        onCheckedChange = {
                            listeningEnabled = it
                            prefs.edit().putBoolean("listening_enabled", it).apply()
                        }
                    )
                }
            }

            Text(
                text = "Privacy Dashboard",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Partner requests permissions only when you enable the feature. No gallery access is ever requested.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Permission List
            PermissionItem(
                title = "Microphone",
                description = "Required to hear the 'Hey Partner' wake word.",
                permission = Manifest.permission.RECORD_AUDIO,
                context = context
            )
            PermissionItem(
                title = "Contacts",
                description = "Required to call contacts by name (e.g., 'Call Mom').",
                permission = Manifest.permission.READ_CONTACTS,
                context = context
            )
            PermissionItem(
                title = "Phone",
                description = "Required to place outgoing calls directly.",
                permission = Manifest.permission.CALL_PHONE,
                context = context
            )
        }
    }
}

@Composable
fun PermissionItem(
    title: String,
    description: String,
    permission: String,
    context: Context
) {
    // Check if permission is granted (will be false until we implement Phase 3-7)
    val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isGranted) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Granted",
                tint = Color(0xFF4CAF50)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Not Requested",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}