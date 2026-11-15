package com.partner.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.partner.assistant.data.PartnerDatabase
import com.partner.assistant.ui.navigation.PartnerNavHost
import com.partner.assistant.ui.theme.PartnerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PartnerTheme {
                val context = LocalContext.current
                val database = remember { PartnerDatabase.getDatabase(context) }
                PartnerNavHost(database = database)
            }
        }
    }
}