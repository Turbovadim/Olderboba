package net.turbovadim.bisquithost.Screens.ServerList

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ServerListVM : ViewModel() {
    var currentCard = mutableStateOf("")
        private set
    fun changeCurrentCard(state: String) {
        currentCard.value = state
    }
}