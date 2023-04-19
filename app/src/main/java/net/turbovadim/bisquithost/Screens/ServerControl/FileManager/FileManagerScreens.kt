package net.turbovadim.bisquithost.Screens.ServerControl.FileManager

sealed class FileManagerScreens(val route: String) {
    object FileManager : FileManagerScreens("FileManager")
    object EditFile : FileManagerScreens("EditFile")
    object ViewImage : FileManagerScreens("ViewImage")
}