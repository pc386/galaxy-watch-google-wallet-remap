package com.galaxywatch.googlewalletremap

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityEvent

class WalletRemapAccessibilityService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private var foregroundPackageName: String? = null
    private var stemPressed = false
    private var longPressTriggered = false
    private var passThroughCurrentPress = false

    private val triggerLongPress = Runnable {
        if (stemPressed) {
            longPressTriggered = true
            launchGoogleWallet()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(LOG_TAG, "Remapping service connected")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode != KeyEvent.KEYCODE_STEM_PRIMARY) return false

        return when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (event.repeatCount == 0) {
                    resetPressState()

                    passThroughCurrentPress =
                        ForegroundAppPolicy.shouldPassThroughStemButton(foregroundPackageName)
                    if (passThroughCurrentPress) {
                        Log.i(LOG_TAG, "Passing stem button through to Samsung Health")
                        return false
                    }

                    stemPressed = true
                    handler.postDelayed(triggerLongPress, longPressDelayMillis())
                }

                !passThroughCurrentPress
            }

            KeyEvent.ACTION_UP -> {
                if (passThroughCurrentPress) {
                    resetPressState()
                    return false
                }

                if (!stemPressed) return false

                stemPressed = false
                handler.removeCallbacks(triggerLongPress)

                if (!longPressTriggered) {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                }

                longPressTriggered = false
                true
            }

            else -> !passThroughCurrentPress
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        event.packageName?.toString()?.let { foregroundPackageName = it }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        resetPressState()
        super.onDestroy()
    }

    private fun resetPressState() {
        handler.removeCallbacks(triggerLongPress)
        stemPressed = false
        longPressTriggered = false
        passThroughCurrentPress = false
    }

    private fun longPressDelayMillis(): Long = maxOf(
        ViewConfiguration.getLongPressTimeout().toLong(),
        MINIMUM_LONG_PRESS_MILLIS,
    )

    private fun launchGoogleWallet() {
        val intent = packageManager.getLaunchIntentForPackage(GOOGLE_WALLET_PACKAGE)
        if (intent == null) {
            Log.w(LOG_TAG, "Google Wallet is not installed")
            return
        }

        try {
            startActivity(intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            })
            Log.i(LOG_TAG, "Opened Google Wallet")
        } catch (exception: RuntimeException) {
            Log.e(LOG_TAG, "Unable to open Google Wallet", exception)
        }
    }

    private companion object {
        const val LOG_TAG = "WalletRemap"
        const val GOOGLE_WALLET_PACKAGE = "com.google.android.apps.walletnfcrel"
        const val MINIMUM_LONG_PRESS_MILLIS = 650L
    }
}
