package io.github.hifter.kmtx.navigation

import androidx.navigation.NavController
import io.github.aakira.napier.Napier

actual class NavigationController(
    private val navController: NavController
) {
    val TAG = "KMtx-NavigationController"
    lateinit var mainScreenNavController: NavController

    actual fun navigateTo(route: Route) {
        if (route.isTopLevelRoute()) {
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        } else if (route.isMainScreenRoute()) {
            if (::mainScreenNavController.isInitialized) {
                mainScreenNavController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            } else {
                Napier.w(tag = TAG, message =  "MainScreenNavController is not initialized yet")
            }
        }else {
            Napier.w(tag = TAG, message = "Unknown route: $route")

        }
    }
    actual fun popBackStack() {
        navController.popBackStack()
    }
}
fun Route.isTopLevelRoute(): Boolean = when (this) {
    is Route.LoginScreen, is Route.UserLoadListScreen, is Route.MainScreen -> true
    else -> false
}
fun Route.isMainScreenRoute(): Boolean = when (this) {
    is Route.HomeScreen, is Route.ContactsScreen, is Route.TimelineScreen, is Route.MoreScreen -> true
    else -> false
}