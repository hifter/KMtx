package io.github.hifter.kmtx.UI

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import io.github.hifter.kmtx.module.AppStateModule
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

val TAG = "UserLoadListScreen"
@Composable
fun UserLoadListScreen(){
    var tipText by remember { mutableStateOf("") }
    MaterialTheme{
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 400.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserLoadList()
        }
    }
}
@Composable
fun UserLoadList() {
    val itemsSize = 50.dp
    val itemsPadSize = 3.dp
    val itemContentSize = itemsSize - itemsPadSize*2

    val originalList by AppStateModule.userClientStateList.collectAsState(initial = emptyList())
    var tempList by remember { mutableStateOf(originalList.toList()) }
    var isReordering by remember { mutableStateOf(false) }

    LaunchedEffect(originalList) {
        if (!isReordering) {
            tempList = originalList.toList()
        }
    }
    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        tempList = tempList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        isReordering = true
        Napier.d(tag = TAG, message = "Reordering from ${from.index} to ${to.index}")
    }
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = lazyListState
        ) {
            items(tempList, key = { it.userId }) { item ->
                ReorderableItem(reorderableState, key = item.userId) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)
                    Surface(shadowElevation = elevation) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(0.6f),
                                text = item.userId,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            when {
                                item.client != null && item.isLoaded -> {
                                    Icon(
                                        modifier = Modifier.size(itemContentSize),
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Success",
                                        tint = Color.Green
                                    )
                                }

                                item.client != null && !item.isLoaded -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(itemContentSize),
                                        color = Color.Gray,
                                        strokeWidth = 2.dp
                                    )
                                }
                                item.client == null && item.isLoaded -> {
                                    Icon(
                                        modifier = Modifier.size(itemContentSize),
                                        imageVector = Icons.Default.Error,
                                        contentDescription = "Error",
                                        tint = Color.Red
                                    )
                                }
                                else -> { // item.client == null && !item.isLoaded
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(itemContentSize),
                                        color = Color.Gray,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                            Icon(
                                modifier = Modifier.size(itemContentSize)
                                    .draggableHandle(),
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = "DragHandle",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
        if (isReordering) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    AppStateModule.reorderUsers(tempList.map { it.userId })
                    isReordering = false
                }) {
                    Text("确认")
                }
                Button(onClick = {
                    tempList = originalList.toList()
                    isReordering = false
                }) {
                    Text("取消")
                }
            }
        }
    }
}