package io.github.hifter.kmtx.UI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.hifter.kmtx.database.StringKvKeys
import io.github.hifter.kmtx.database.getRoomDatabase
import io.github.hifter.kmtx.module.AppStateModule
import io.github.hifter.kmtx.module.UserClientState
import kotlinx.coroutines.runBlocking
import net.folivo.trixnity.client.MatrixClient

@Composable
fun MainScreen() {
    val TAG = "KMtxMainScreen"
    val curClientState by AppStateModule.curClientState.collectAsState()
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 400.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (curClientState) {
                null -> {
                    Text(text = "No client state available", style = MaterialTheme.typography.titleLarge)
                }
                is UserClientState -> {
                    Text(
                        text = "${curClientState!!.userId} 登陆成功\n deviceid = ${curClientState!!.client?.deviceId} \n " +
                                "getValue(cruUserid) = ${
                                    runBlocking {
                                        getRoomDatabase().stringKvDao().getValue(StringKvKeys.curUserid)
                                    }
                                }",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}