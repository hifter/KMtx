package io.github.hifter.kmtx.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.hifter.kmtx.module.AppStateModule
import io.github.hifter.kmtx.navigation.NavigationController
import io.github.hifter.kmtx.navigation.Route.*

@Composable
fun MainScreen(navigationController: NavigationController) {
    val TAG = "KMtxMainScreen"
    val curClientState by AppStateModule.curClientState.collectAsState()
    val navController = rememberNavController()
    navigationController.mainScreenNavController = navController
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 400.dp)
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NavHost(navController = navController, startDestination = HomeScreen) {
                    composable<HomeScreen> {
                        TmpScreen()
                    }
                    composable<ContactsScreen> {
                        Text("ContactsScreen")
                    }
                    composable<TimelineScreen> {
                        Text("TimelineScreen")
                    }
                    composable<MoreScreen> {
                        Text("MoreScreen")
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(
                    icon = Icons.Default.Home,
                    label = "Home",
                    onClick = { navigationController.navigateTo(HomeScreen) }
                )
                NavButton(
                    icon = Icons.Default.Contacts,
                    label = "Contacts",
                    onClick = {navigationController.navigateTo(ContactsScreen)}
                )
                NavButton(
                    icon = Icons.Default.Timeline,
                    label = "Timeline",
                    onClick = { navigationController.navigateTo(TimelineScreen) }
                )
                NavButton(
                    icon = Icons.Default.MoreHoriz,
                    label = "More",
                    onClick = { navigationController.navigateTo(MoreScreen) }
                )
            }
        }
    }
}
@Composable
fun NavButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}