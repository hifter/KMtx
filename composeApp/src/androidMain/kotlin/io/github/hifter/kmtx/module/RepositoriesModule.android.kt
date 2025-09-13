package io.github.hifter.kmtx.module

import androidx.room.Room
import io.github.hifter.kmtx.MyApp
import kotlinx.coroutines.Dispatchers
import net.folivo.trixnity.client.media.okio.createOkioMediaStoreModule
import net.folivo.trixnity.client.store.repository.room.TrixnityRoomDatabase
import net.folivo.trixnity.client.store.repository.room.createRoomRepositoriesModule
import net.folivo.trixnity.core.model.UserId
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import kotlin.jvm.java

actual class RepositoriesModule {
    actual companion object {
        actual fun getRepositoriesModule(userId: UserId): Module {
            val databaseName = "matrix_$userId.db"
            return createRoomRepositoriesModule(
                databaseBuilder = Room.databaseBuilder(
                    MyApp.getInstance(),
                    TrixnityRoomDatabase::class.java,
                    databaseName
                )
            )
        }
        actual fun getMediaStoreModule(): Module {
            return createOkioMediaStoreModule(basePath =  MyApp.getInstance().filesDir.absolutePath.toPath().resolve("media"),
                fileSystem = FileSystem.SYSTEM,
                coroutineContext = Dispatchers.IO
            )
        }
    }
}