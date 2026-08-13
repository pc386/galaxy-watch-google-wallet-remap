package com.galaxywatch.googlewalletremap

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text

class MainActivity : ComponentActivity() {
    private val serviceEnabled = mutableStateOf(false)
    private val walletAvailable = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                WalletRemapScreen(
                    serviceEnabled = serviceEnabled,
                    walletAvailable = walletAvailable,
                    openAccessibilitySettings = {
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val expectedService = ComponentName(this, WalletRemapAccessibilityService::class.java)
        val accessibilityManager = getSystemService(AccessibilityManager::class.java)

        serviceEnabled.value = accessibilityManager
            .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any { enabledService ->
                val serviceInfo = enabledService.resolveInfo.serviceInfo
                ComponentName(serviceInfo.packageName, serviceInfo.name) == expectedService
            }

        walletAvailable.value = packageManager
            .getLaunchIntentForPackage(GOOGLE_WALLET_PACKAGE) != null
    }

    private companion object {
        const val GOOGLE_WALLET_PACKAGE = "com.google.android.apps.walletnfcrel"
    }
}

@Composable
private fun WalletRemapScreen(
    serviceEnabled: State<Boolean>,
    walletAvailable: State<Boolean>,
    openAccessibilitySettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.screen_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(
                if (serviceEnabled.value) R.string.service_enabled else R.string.service_disabled,
            ),
            color = if (serviceEnabled.value) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = stringResource(
                if (walletAvailable.value) R.string.wallet_available else R.string.wallet_unavailable,
            ),
            color = if (walletAvailable.value) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = openAccessibilitySettings,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.open_accessibility_settings))
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.remap_instructions),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.power_note),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}
