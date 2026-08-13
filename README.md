# Galaxy Watch Google Wallet Remap

A minimal, event-driven Wear OS app that remaps the reserved Samsung Pay button
on a Samsung Galaxy Watch to Google Wallet.

- Short press: performs the normal Back action.
- Long press: opens Google Wallet.

The accessibility service consumes only `KEYCODE_STEM_PRIMARY`. It does not
monitor Samsung Pay, poll, read `logcat`, hold a wake lock, use the network or
sensors, schedule work, or run a timer while idle. A single delayed callback
exists only while the button is physically held.

## Prerequisites

Google Wallet must be installed under its standard Wear OS package name:
`com.google.android.apps.walletnfcrel`.

Samsung Pay launches from a separate Samsung system handler even when the app
consumes the key event. Disable Samsung Pay for the current watch user with ADB:

```shell
adb shell pm disable-user --user 0 com.samsung.android.samsungpay.gear
```

This is reversible and does not delete Samsung Pay data. Restore it with:

```shell
adb shell pm enable --user 0 com.samsung.android.samsungpay.gear
```

## Toolchain

- Android Gradle Plugin 9.3.0
- Gradle 9.5.0
- AGP built-in Kotlin with Compose compiler 2.3.21
- compile/target SDK 37; minimum SDK 30 for Galaxy Watch4
- Compose BOM 2026.06.00
- Wear Compose Material 3 1.6.2

JDK 17 is required. On Apple Silicon macOS:

```shell
brew install openjdk@17
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export ANDROID_HOME="/opt/homebrew/share/android-commandlinetools"
```

## Build and install

From the `GalaxyWatchGoogleWalletRemap` directory:

```shell
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Open **Galaxy Watch Google Wallet Remap**, select **Accessibility settings**,
and enable the service. If Wear OS blocks the sideloaded accessibility service
as a restricted setting:

```shell
adb shell appops set com.galaxywatch.googlewalletremap ACCESS_RESTRICTED_SETTINGS allow
```

Because this project has a new package identity, disable the old **GW4 Wallet
Remap** accessibility service before enabling this one.

## Disable or uninstall

Disable the service under the watch's accessibility settings to restore normal
key handling. To uninstall over ADB:

```shell
adb uninstall com.galaxywatch.googlewalletremap
```
