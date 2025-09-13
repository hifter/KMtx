package io.github.hifter.kmtx.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.hifter.kmtx.database.entities.StringKvEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StringKvDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: StringKvEntity)

    @Update
    suspend fun update(entity: StringKvEntity)

    @Query("SELECT * FROM string_kv WHERE stringKey = :key")
    suspend fun getByKey(key: String): StringKvEntity?

    @Query("SELECT * FROM string_kv")
    fun getAllAsFlow(): Flow<List<StringKvEntity>>

    @Query("DELETE FROM string_kv WHERE stringKey = :key")
    suspend fun deleteByKey(key: String)

    suspend fun upsert(entity: StringKvEntity) {
        val existing = getByKey(entity.stringKey)
        if (existing != null) {
            update(entity)
        } else {
            insert(entity)
        }
    }
    suspend fun getValue(key: String,defaultValue: String = ""): String{
        return getByKey(key)?.stringValue ?: defaultValue
    }
    @Query("SELECT stringValue FROM string_kv WHERE stringKey = :key")
    fun getValueFlow(key: String): Flow<String?>
    suspend fun setValue(key: String,value: String): Unit{
        upsert(StringKvEntity(key,value))
    }
}