package io.github.hifter.kmtx.module

import androidx.compose.runtime.mutableStateListOf
import io.github.aakira.napier.Napier
import io.github.hifter.kmtx.database.StringKvKeys
import io.github.hifter.kmtx.database.getRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.folivo.trixnity.client.MatrixClient
import net.folivo.trixnity.client.fromStore
import net.folivo.trixnity.core.model.UserId

object AppStateModule {
    const val TAG = "KMtxAppStateModule"
    private val _userClientStateList = MutableStateFlow<List<UserClientState>>(emptyList())
    val userClientStateList: StateFlow<List<UserClientState>> = _userClientStateList.asStateFlow()
    private val _curClientState = MutableStateFlow<UserClientState?>(null)
    val curClientState: StateFlow<UserClientState?> = _curClientState.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val clientCache = mutableMapOf<String, MatrixClient?>()
    val appDatabase = getRoomDatabase()

    fun addUser(userid: String, client: MatrixClient? = null) {
        if (userClientStateList.value.any { it.userId == userid })
            switchUser(userid)
        else {
            scope.launch(Dispatchers.IO) {
                if (client != null) {
                    clientCache[userid] = client
                }
                appDatabase.useridKvDao().addUserid(userid)
                switchUser(userid)
            }
        }
    }
    fun deleteUser(userid: String) {
        scope.launch(Dispatchers.IO) {
            appDatabase.useridKvDao().deleteAndReorder(userid)
            clientCache.remove(userid)
        }
    }
    fun switchUser(userid: String) {
        scope.launch(Dispatchers.IO) {
            appDatabase.stringKvDao().setValue(StringKvKeys.curUserid, userid)
        }
    }
    init{
        scope.launch {
            appDatabase.useridKvDao().getAllAsFlow()
                .distinctUntilChanged()
                .map { entities ->
                    entities.map { entity ->
                        loadOrGetClientState(entity.userid)
                    }
                }
                .collect { newList ->
                    _userClientStateList.value = newList
                }
        }
        scope.launch {
            combine(
                userClientStateList,
                appDatabase.stringKvDao().getValueFlow(StringKvKeys.curUserid)
            ) { list, curUserid ->
                list.find { it.userId == curUserid } // Find matching user in list
            }.collect { _curClientState.value = it }
        }
    }
    private suspend fun loadOrGetClientState(userid: String): UserClientState {
        if (clientCache.containsKey(userid)) {
            val client = clientCache[userid]
            return UserClientState(
                userId = userid,
                client = client,
                isLoaded = true,
                error = if (client == null) "Failed to load client" else null
            )
        }
        val result = MatrixClient.fromStore(
            RepositoriesModule.getRepositoriesModule(UserId(userid)),
            RepositoriesModule.getMediaStoreModule()
        )
        return result.fold(
            onSuccess = { client ->
                clientCache[userid] = client
                UserClientState(
                    userId = userid,
                    client = client,
                    isLoaded = true,
                    error = if (client == null) "Client is null" else null
                )
            },
            onFailure = { throwable ->
                clientCache[userid] = null
                Napier.e(tag = TAG, message = "Failed to load client for $userid: ${throwable.message}")
                UserClientState(
                    userId = userid,
                    client = null,
                    isLoaded = true,
                    error = throwable.message
                )
            }
        )
    }
    fun reorderUsers(newOrder: List<String>) {
        scope.launch(Dispatchers.IO) {
            try {
                val dao = appDatabase.useridKvDao()
                val entities = dao.getAllUsersOrdered()
                for ((index, entity) in entities.withIndex()) {
                    val newIndex = newOrder.indexOf(entity.userid) + 1
                    if (newIndex > 0 && newIndex != entity.userIndex) {
                        dao.update(entity.copy(userIndex = newIndex))
                    }
                }
                Napier.d(tag = TAG, message = "Users reordered successfully: $newOrder")
            } catch (e: Exception) {
                Napier.e(tag = TAG, message = "Failed to reorder users: ${e.message}")
            }
        }
    }
    fun shutdown(){
        scope.cancel()
        clientCache.clear()
        _userClientStateList.value = emptyList()
        _curClientState.value = null

        // 可选：用NonCancellable确保关键清理完成，即使取消中
        scope.launch(NonCancellable) {
            // e.g., appDatabase.close() 如果Room支持；或保存final状态
            Napier.i(tag = TAG, message = "AppStateModule shutdown complete")
        }
    }
}
fun <T> MutableList<T>.replace(old: T, new: T) {
    val index = indexOf(old)
    if (index != -1) {
        this[index] = new
    }
}
data class UserClientState(
    val userId: String,
    val client: MatrixClient?,
    val isLoaded: Boolean = false,
    val error: String? = null
)