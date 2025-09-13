package io.github.hifter.kmtx.module

import android.provider.Settings
import io.github.hifter.kmtx.MyApp

actual fun getPlatformDeviceInfo(): String {
    val deviceName = Settings.Global.getString(MyApp.getInstance().contentResolver, Settings.Global.DEVICE_NAME) ?: "Unknown Android Device"
    return "KMtx-$deviceName"
}