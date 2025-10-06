package io.github.hifter.kmtx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.hifter.kmtx.UI.*
import io.github.hifter.kmtx.module.AppStateModule
import io.github.hifter.kmtx.navigation.NavigationController
import io.github.hifter.kmtx.navigation.Route.*
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.Serializable



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navigationController = NavigationController(navController)
            val curClientState by AppStateModule.curClientState.collectAsStateWithLifecycle()
            val userClientStateList by AppStateModule.userClientStateList.collectAsState()
            LaunchedEffect(curClientState, userClientStateList) {
                when {
                    curClientState == null && userClientStateList.isEmpty() -> {
                        navController.navigate(LoginScreen) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                    else -> {
                        navController.navigate(MainScreen) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }
            NavHost(navController = navController, startDestination = LoginScreen) {
                composable<LoginScreen> {
                    LoginScreen()
                }
                composable<UserLoadListScreen> {
                    UserLoadListScreen()
                }
                composable<MainScreen> {
                    MainScreen(navigationController)
                }
            }
//            if (curClientState == null && userClientStateList.isEmpty()) {
//                LoginScreen()
//            } else {
//                MainScreen()
////                UserLoadListScreen()
//            }
        }
    }
}