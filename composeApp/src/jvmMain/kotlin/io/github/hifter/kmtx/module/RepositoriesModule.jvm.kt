package io.github.hifter.kmtx.module

import androidx.room.Room
import net.folivo.trixnity.client.store.repository.room.TrixnityRoomDatabase
import net.folivo.trixnity.client.store.repository.room.createRoomRepositoriesModule
import net.folivo.trixnity.core.model.UserId
import org.koin.core.module.Module
import java.nio.file.Paths

actual class RepositoriesModule {
    actual companion object {
        actual fun getRepositoriesModule(userId: UserId): Module {
            val databaseName = "matrix_$userId.db"
            val dbPath = Paths.get(System.getProperty("user.home"), ".KMtx", databaseName).toString()
            return createRoomRepositoriesModule(Room.databaseBuilder<TrixnityRoomDatabase>(
                name = dbPath
            ))
        }

        actual fun getMediaStoreModule(): Module {
            TODO("Not yet implemented")
        }
    }
}