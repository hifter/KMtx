package io.github.hifter.kmtx.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "string_kv")
data class StringKvEntity(
    @PrimaryKey val stringKey: String,
    val stringValue: String
)