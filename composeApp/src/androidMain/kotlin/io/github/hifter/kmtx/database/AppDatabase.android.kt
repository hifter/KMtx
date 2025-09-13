package io.github.hifter.kmtx.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.hifter.kmtx.MyApp

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appContext = MyApp.getInstance()
    val dbFile = appContext.getDatabasePath("appDatabase.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}