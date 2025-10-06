package io.github.hifter.kmtx.navigation

import kotlinx.serialization.Serializable

// 顶层导航路由
@Serializable
sealed class Route {
    @Serializable
    object LoginScreen : Route()
//    和LoginScreen界面一样，只是打开方式不一样，会叠加在上方。可返回前一个页面
    @Serializable
    object AddNewUserLoginScreen : Route()
    @Serializable
    object UserLoadListScreen : Route()
    @Serializable
    object MainScreen : Route()
    @Serializable
    object HomeScreen: Route()
    @Serializable
    object ContactsScreen: Route()
    @Serializable
    object TimelineScreen: Route()
    @Serializable
    object MoreScreen: Route()
}

// 导航控制器接口
expect class NavigationController {
    fun navigateTo(route: Route)
    fun popBackStack()
}