package io.github.hifter.kmtx

import android.app.Application
import io.github.aakira.napier.BuildConfig
import io.github.hifter.kmtx.module.InitModule
import java.lang.ref.WeakReference

class MyApp : Application() {
    companion object {
        private lateinit var instance: MyApp
        fun getInstance(): MyApp {
            return instance
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        InitModule.init()
    }
}