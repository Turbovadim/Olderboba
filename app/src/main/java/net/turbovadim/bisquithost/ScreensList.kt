package net.turbovadim.bisquithost

sealed class ScreensList(val route: String) {
    object StartingPage : ScreensList("StartingPage")
    object ServerListScreen : ScreensList("ServerListScreen")
    object ServerControlScreen : ScreensList("ServerControlScreen")
    object BadConnectionScreen : ScreensList("BadConnectionScreen")

    fun withArgs(vararg args: Any): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}