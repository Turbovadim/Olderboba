package net.turbovadim.bisquithost.Screens.StartingPage

sealed class GuideSteps(val route: String) {
    object MainPage : GuideSteps("MainPage")
    object FirstPage : GuideSteps("FirstPage")
    object SecondPage : GuideSteps("SecondPage")
    object ThirdPage : GuideSteps("ThirdPage")
    object FourthPage : GuideSteps("FourthPage")
}