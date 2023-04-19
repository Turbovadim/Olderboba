package net.turbovadim.bisquithost.Screens.ServerControl

sealed class ServerControlPagesList(val route: String) {
    object ConsolePage : ServerControlPagesList("ConsolePage")
    object SettingsPage : ServerControlPagesList("SettingsPage")
    object FilesPage : ServerControlPagesList("FilesPage")
    object BackupsPage : ServerControlPagesList("BackupsPage")
}