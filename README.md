# Galaxy Watch Google Wallet Remap

A minimal Wear OS app that remaps the Samsung Pay hardware-button shortcut to
Google Wallet.

- **Short press:** performs the normal Back action.
- **Long press:** opens Google Wallet.
- **In Samsung Health:** leaves the button untouched so its long-press workout
  controls continue to work.

Tested on a **Samsung Galaxy Watch Ultra (2025)**.

## Requirements

- A Samsung Galaxy Watch running Wear OS.
- Google Wallet installed on the watch.
- A computer with [Android SDK Platform Tools](https://developer.android.com/tools/releases/platform-tools).
- The watch and computer connected to the same network.
- The latest APK from the [GitHub Releases page](https://github.com/pc386/galaxy-watch-google-wallet-remap/releases/latest).

## Install and set up

### 1. Enable developer options on the watch

1. Open **Settings** on the watch.
2. Go to **About watch → Software information**.
3. Tap **Software version** several times until Developer mode is enabled.
4. Return to Settings and open **Developer options**.
5. Enable **ADB debugging**.
6. Enable **Wireless debugging**.

Menu names can vary slightly between Wear OS versions.

### 2. Pair the watch with ADB

Open **Wireless debugging → Pair new device** on the watch. It displays an IP
address, pairing port, and pairing code.

On the computer, run:

```shell
adb pair WATCH_IP:PAIRING_PORT
```

Enter the pairing code shown on the watch when prompted. For example:

```shell
adb pair 192.168.1.191:42919
```

### 3. Connect to the watch

Return to the main **Wireless debugging** screen. Use the connection port shown
there—not the temporary pairing port:

```shell
adb connect WATCH_IP:CONNECTION_PORT
adb devices
```

The watch should appear with the state `device`.

### 4. Install the APK

Download `galaxy-watch-google-wallet-remap-v1.1.apk` from the Releases page, open
a terminal in its download directory, and run:

```shell
adb install -r galaxy-watch-google-wallet-remap-v1.1.apk
```

The command should finish with `Success`.

If ADB reports more than one device, copy the desired identifier from
`adb devices` and specify it explicitly:

```shell
adb -s 'DEVICE_IDENTIFIER' install -r galaxy-watch-google-wallet-remap-v1.1.apk
```

### 5. Disable Samsung Pay on the watch

Samsung launches Pay from a separate system handler, even when the remapper
handles the button. Disable Samsung Pay for the current watch user:

```shell
adb shell pm disable-user --user 0 com.samsung.android.samsungpay.gear
```

This is reversible and does not delete Samsung Pay data.

With multiple connected devices, use:

```shell
adb -s 'DEVICE_IDENTIFIER' shell pm disable-user --user 0 com.samsung.android.samsungpay.gear
```

### 6. Enable the remapping service

1. Open **Galaxy Watch Google Wallet Remap** from the watch app launcher.
2. Tap **Accessibility settings**.
3. Open **Installed apps** or **Installed services**.
4. Select **Galaxy Watch Google Wallet Remap**.
5. Enable the service and confirm the warning.
6. Return to the app and confirm it shows **Remapping enabled** and
   **Google Wallet found**.

If Wear OS blocks the sideloaded accessibility service as a restricted setting,
run this command and then try enabling it again:

```shell
adb shell appops set com.galaxywatch.googlewalletremap ACCESS_RESTRICTED_SETTINGS allow
```

### 7. Test the button

- Briefly press the Samsung Pay button: it should perform Back.
- Hold the Samsung Pay button for approximately 650 ms: Google Wallet should
  open without Samsung Pay appearing.

After setup, you can disable **Wireless debugging** and **ADB debugging** on the
watch. The remapper does not require an ADB connection during normal use.

## Restore Samsung Pay or uninstall

Disable **Galaxy Watch Google Wallet Remap** in the watch's accessibility
settings before restoring Samsung Pay:

```shell
adb shell pm enable --user 0 com.samsung.android.samsungpay.gear
```

To uninstall the remapper:

```shell
adb uninstall com.galaxywatch.googlewalletremap
```

## Battery use

The service is event-driven. It listens for `KEYCODE_STEM_PRIMARY` and minimal
window-state changes needed to recognize when Samsung Health is foreground. It
does not read screen contents, poll, read `logcat`, acquire a wake lock, access
the network or sensors, schedule background work, or run a timer while idle. A
single delayed callback exists only while the hardware button is physically
held.

## Build from source

The project requires JDK 17, Android SDK Platform 37, and Android Build Tools
36.0.0.

```shell
./gradlew :app:assembleDebug
```

The debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```
