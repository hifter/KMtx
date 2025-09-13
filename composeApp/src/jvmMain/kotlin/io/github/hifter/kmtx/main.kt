package io.github.hifter.kmtx

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.hifter.kmtx.UI.*
import io.github.hifter.kmtx.module.InitModule

fun main() = application {
    InitModule.init()
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMtx",
    ) {
        LoginScreen()
    }
}