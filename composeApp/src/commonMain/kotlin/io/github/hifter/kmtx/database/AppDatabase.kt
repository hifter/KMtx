package io.github.hifter.kmtx.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.hifter.kmtx.database.dao.StringKvDao
import io.github.hifter.kmtx.database.dao.UserIdDao
import io.github.hifter.kmtx.database.entities.StringKvEntity
import io.github.hifter.kmtx.database.entities.UserIdEntity
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [StringKvEntity::class, UserIdEntity::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stringKvDao(): StringKvDao
    abstract fun useridKvDao(): UserIdDao
}
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase> = getDatabaseBuilder()
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
expect fun getDatabaseBuilder():RoomDatabase.Builder<AppDatabase>