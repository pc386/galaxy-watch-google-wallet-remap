package com.galaxywatch.googlewalletremap

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ForegroundAppPolicyTest {
    @Test
    fun passesStemButtonThroughForSamsungHealthOnWear() {
        assertTrue(
            ForegroundAppPolicy.shouldPassThroughStemButton(
                "com.samsung.android.wear.shealth",
            ),
        )
    }

    @Test
    fun passesStemButtonThroughForSamsungHealthStandardPackage() {
        assertTrue(
            ForegroundAppPolicy.shouldPassThroughStemButton(
                "com.sec.android.app.shealth",
            ),
        )
    }

    @Test
    fun stillRemapsForOtherAndUnknownForegroundApps() {
        assertFalse(
            ForegroundAppPolicy.shouldPassThroughStemButton(
                "com.google.android.apps.walletnfcrel",
            ),
        )
        assertFalse(ForegroundAppPolicy.shouldPassThroughStemButton(null))
    }
}
