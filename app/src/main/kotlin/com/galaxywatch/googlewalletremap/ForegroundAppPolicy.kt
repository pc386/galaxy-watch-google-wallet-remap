package com.galaxywatch.googlewalletremap

internal object ForegroundAppPolicy {
    private val samsungHealthPackages = setOf(
        "com.samsung.android.wear.shealth",
        "com.sec.android.app.shealth",
    )

    fun shouldPassThroughStemButton(packageName: CharSequence?): Boolean =
        packageName?.toString() in samsungHealthPackages
}
