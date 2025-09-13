package io.github.hifter.kmtx.module

import java.net.InetAddress

actual fun getPlatformDeviceInfo(): String {
    return try {
        "KMtx-${InetAddress.getLocalHost().hostName}"
    } catch (e: Exception) {
        "Unknown JVM Device"
    }
}