package com.partner.assistant.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partner.assistant.device.ActionHandler
import com.partner.assistant.ui.theme.*

data class ToolItem(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun ToolsScreen() {
    val context = LocalContext.current
    val actionHandler = remember { ActionHandler(context) }

    // State for the Device Info dialog
    var showDeviceInfo by remember { mutableStateOf(false) }

    val tools = listOf(
        ToolItem("Alarm & Timer", Icons.Filled.Alarm, NeonPurple),
        ToolItem("Flashlight", Icons.Filled.FlashOn, NeonBlue),
        ToolItem("Screenshot", Icons.Filled.CameraAlt, NeonPink),
        ToolItem("Bluetooth", Icons.Filled.Bluetooth, Color(0xFF4CAF50)),
        ToolItem("Music Control", Icons.Filled.MusicNote, Color(0xFFE91E63)),
        ToolItem("Sound Mode", Icons.Filled.VolumeUp, Color(0xFFFF9800)),
        ToolItem("Open App", Icons.Filled.Apps, Color(0xFF9C27B0)),
        ToolItem("Device Info", Icons.Filled.BatteryFull, Color(0xFF00BCD4))
    )

    // Device Info Dialog
    if (showDeviceInfo) {
        AlertDialog(
            onDismissRequest = { showDeviceInfo = false },
            title = { Text("Device Info", color = TextPrimary) },
            text = {
                Text("Battery Level: ${actionHandler.getBatteryLevel()}%", color = TextSecondary)
            },
            confirmButton = {
                TextButton(onClick = { showDeviceInfo = false }) {
                    Text("Close", color = NeonPurple)
                }
            },
            containerColor = SurfaceDark
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text(
            text = "Tools",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tools) { tool ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable {
                            // Wire up the actions here!
                            when (tool.title) {
                                "Flashlight" -> actionHandler.toggleFlashlight()
                                "Alarm & Timer" -> {
                                    val msg = actionHandler.openAlarmApp()
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                                "Bluetooth" -> {
                                    val msg = actionHandler.openBluetoothSettings()
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                                "Sound Mode" -> {
                                    val msg = actionHandler.toggleSoundMode()
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                                "Open App" -> {
                                    val msg = actionHandler.openApp("com.google.android.youtube")
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                                "Device Info" -> {
                                    showDeviceInfo = true
                                }
                                else -> {
                                    Toast.makeText(context, "${tool.title} coming soon!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, tool.color.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.title,
                            tint = tool.color,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = tool.title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}