package io.github.hifter.kmtx.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "userid",
    indices = [Index(value = ["userid"], unique = true)])
data class UserIdEntity(
    @PrimaryKey val userIndex: Int,
    val userid: String
)