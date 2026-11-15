package com.partner.assistant.device

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.provider.AlarmClock
import android.util.Log
import androidx.core.net.toUri // Import for toUri()

class ActionHandler(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var isFlashlightOn = false

    fun toggleFlashlight(): Boolean {
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            if (cameraId != null) {
                isFlashlightOn = !isFlashlightOn
                cameraManager.setTorchMode(cameraId, isFlashlightOn)
                return isFlashlightOn
            }
        } catch (e: Exception) {
            Log.e("ActionHandler", "Flashlight error", e)
        }
        return false
    }

    fun setVolume(level: Int): String {
        try {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val targetVolume = (maxVolume * (level / 100.0)).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
            return "Volume set to $level percent."
        } catch (e: Exception) {
            return "Failed to change volume."
        }
    }

    fun makeCall(number: String): String {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$number".toUri() // Using the KTX extension
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            // Check if the device actually has a dialer app
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                return "Opening dialer for $number."
            } else {
                return "No phone app found on this device."
            }
        } catch (e: Exception) {
            Log.e("ActionHandler", "Call error", e)
            return "Could not open the dialer."
        }
    }

    fun setAlarm(hour: Int, minute: Int = 0): String {
        try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, "Partner Alarm")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            // Check if the device actually has a clock app
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                return "Setting alarm for $hour o'clock."
            } else {
                return "No clock app found on this emulator."
            }
        } catch (e: Exception) {
            Log.e("ActionHandler", "Alarm error", e)
            return "Could not set the alarm."
        }
    }
    // Opens Bluetooth settings
    fun openBluetoothSettings(): String {
        try {
            val intent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return "Opening Bluetooth settings."
        } catch (e: Exception) {
            return "Could not open Bluetooth settings."
        }
    }

    // Opens the Alarm screen
    fun openAlarmApp(): String {
        try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, 7) // Default to 7 AM
                putExtra(AlarmClock.EXTRA_MINUTES, 0)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return "Opening Alarm."
        } catch (e: Exception) {
            return "No clock app found."
        }
    }

    // Toggles between Normal and Vibrate
    fun toggleSoundMode(): String {
        return try {
            val currentMode = audioManager.ringerMode
            if (currentMode == AudioManager.RINGER_MODE_NORMAL) {
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                "Mode: Vibrate"
            } else {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                "Mode: Normal"
            }
        } catch (e: Exception) {
            "Cannot change sound mode (Permission needed)"
        }
    }

    // Opens YouTube or another app
    fun openApp(packageName: String): String {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            "Opening app."
        } else {
            "App not installed."
        }
    }

    // Get Battery Level
    fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
        return batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
}
