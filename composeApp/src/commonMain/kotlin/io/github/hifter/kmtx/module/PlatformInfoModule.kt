package io.github.hifter.kmtx.module

import io.github.aakira.napier.Napier

class PlatformInfoModule {
    companion object{
        fun getDeviceInfo(): String{
            return getPlatformDeviceInfo()
        }
    }
}
expect fun getPlatformDeviceInfo(): String