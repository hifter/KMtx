package io.github.hifter.kmtx.module

import net.folivo.trixnity.client.MatrixClient

actual class AppStateModule {
    actual companion object {
        actual var clientList: List<MatrixClient>
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var curClient: MatrixClient?
            get() = TODO("Not yet implemented")
            set(value) {}
    }
}