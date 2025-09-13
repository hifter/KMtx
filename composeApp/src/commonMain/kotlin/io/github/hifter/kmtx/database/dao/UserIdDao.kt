package io.github.hifter.kmtx.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.github.aakira.napier.Napier
import io.github.hifter.kmtx.database.entities.UserIdEntity
import kotlinx.coroutines.flow.Flow

const val TAG = "UserIdDao"
@Dao
interface UserIdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UserIdEntity)

    @Update
    suspend fun update(entity: UserIdEntity)

    @Query("SELECT * FROM userid WHERE userIndex = :key")
    suspend fun getByKey(key: Int): UserIdEntity?

    @Query("SELECT * FROM userid ORDER BY userIndex ASC")
    fun getAllAsFlow(): Flow<List<UserIdEntity>>

    @Query("DELETE FROM userid WHERE userIndex = :key")
    suspend fun deleteByKey(key: Int)

    @Query("SELECT COUNT(*) FROM userid")
    suspend fun getUserCount(): Int

    @Query("SELECT * FROM userid WHERE userid = :userid")
    suspend fun getByUserid(userid: String): UserIdEntity?

    suspend fun addUserid(userid: String): Boolean {
        return try {
            val entity = UserIdEntity(getUserCount() + 1, userid)
            if (getByUserid(userid) != null) {
                Napier.e(tag = TAG, message = "Userid $userid already exists")
                false
            } else {
                insert(entity)
                Napier.d(tag = TAG, message = "Userid $userid inserted successfully")
                true
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "Failed to insert userid $userid: ${e.message}")
            false
        }
    }
    @Query("DELETE FROM userid WHERE userid = :userid")
    suspend fun deleteByUserid(userid: String)

    @Query("SELECT * FROM userid ORDER BY userIndex ASC")
    suspend  fun getAllUsersOrdered(): List<UserIdEntity>

    @Transaction
    suspend fun deleteAndReorder(userid: String) {
        deleteByUserid(userid)
        val remainingUsers = getAllUsersOrdered()
        for ((index, entity) in remainingUsers.withIndex()) {
            val newIndex = index + 1
            if (entity.userIndex != newIndex) {
                update(entity.copy(userIndex = newIndex))
            }
        }
    }
}