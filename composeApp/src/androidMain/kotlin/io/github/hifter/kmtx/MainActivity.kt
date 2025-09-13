package io.github.hifter.kmtx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.hifter.kmtx.UI.*
import io.github.hifter.kmtx.module.AppStateModule
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val curClientState by AppStateModule.curClientState.collectAsStateWithLifecycle()
            val userClientStateList by AppStateModule.userClientStateList.collectAsState()
            if (curClientState == null && userClientStateList.isEmpty()) {
                LoginScreen()
            } else {
                UserLoadListScreen()
            }
        }
    }
}