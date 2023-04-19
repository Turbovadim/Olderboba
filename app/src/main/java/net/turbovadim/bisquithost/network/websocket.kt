package net.turbovadim.bisquithost.network

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import net.turbovadim.bisquithost.Screens.ServerList.roundTo
import net.turbovadim.bisquithost.ServerId
import net.turbovadim.bisquithost.apiKey
import okhttp3.Request
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import javax.net.ssl.SSLSocketFactory

enum class InitWebsocket {
    INIT,
    REAUTH
}

enum class MessageType(val string: String) {
    CONSOLE_LOG("console output"),
    STATS("stats"),
    TOKEN_EXPIRING("token expiring"),
    STATUS_CHANGE("status");
}

class WebsocketViewModel : ViewModel() {

    val listState = LazyListState()

    var consoleMessages = mutableStateListOf<String>()
        private set

    var autoScroll = mutableStateOf(true)
        private set
    fun setAutoScroll(state: Boolean) {
        autoScroll.value = state
    }

    var statsArray = mutableStateOf(listOf("", "", "", ""))
        private set

    var serverState = mutableStateOf("")
        private set

    private var credentialsArray = mutableListOf<String>()
    lateinit var webSocketClient: WebSocketClient

    fun generateWebsocketKey(callFun: InitWebsocket) {
        credentialsArray.clear()
        val request = Request.Builder()
            .url("https://mgr.bisquit.host/api/client/servers/$ServerId/websocket")
            .header("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val websocketConnectOBJ = JSONObject(response.body!!.string())

            val websocketCurrentURI = websocketConnectOBJ.getJSONObject("data").getString("socket")
            val websocketAuthKey = websocketConnectOBJ.getJSONObject("data").getString("token")
            credentialsArray += websocketCurrentURI
            credentialsArray += websocketAuthKey
            when (callFun) {
                InitWebsocket.INIT -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        serverWebsocketInit()
                    }
                }
                InitWebsocket.REAUTH -> {
                    onTokenExpiring()
                }
            }
        }
    }

    private fun serverWebsocketInit() {
        viewModelScope.launch(Dispatchers.IO) {
            val websocketURI = URI(credentialsArray[0])
            val websocketToken = credentialsArray[1]

            createWebSocketClient(websocketURI, websocketToken)

            val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
            webSocketClient.setSocketFactory(socketFactory)
            webSocketClient.addHeader("Origin", "https://mgr.bisquit.host")
            webSocketClient.connect()
        }
    }

    private fun createWebSocketClient(websocketURI: URI?, token: String)  {
        webSocketClient = object : WebSocketClient(websocketURI) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                println("Websocket: Connected")
                webSocketClient.send("""{"event":"auth","args":["$token"]}""")
                webSocketClient.send("""{"event":"send logs","args":[null]}""")
            }
            override fun onMessage(message: String?) {
                println("Websocket: onMessage: $message")
                websocketMessageHandler(message!!)
            }
            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                println("Websocket: Closed $reason")
            }
            override fun onError(ex: Exception?) {
                println("Websocket: Error ${ex?.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::webSocketClient.isInitialized) {
            webSocketClient.close()
        }
    }

    fun closeWebsocket() {
        if (::webSocketClient.isInitialized) {
            webSocketClient.close()
        }
    }

    fun websocketMessageHandler(message: String) {
        val messageOBJ = JSONObject(message)

        when (messageOBJ.getString("event")) {
            MessageType.CONSOLE_LOG.string -> {
                val args = messageOBJ.getJSONArray("args").getString(0)
                onConsoleLog(args)
            }
            MessageType.STATS.string -> {
                val args = messageOBJ.getJSONArray("args").getString(0)
                onStatsReceive(args)
            }
            MessageType.TOKEN_EXPIRING.string -> {
                generateWebsocketKey(InitWebsocket.REAUTH)
            }
            MessageType.STATUS_CHANGE.string ->  {
                val args = messageOBJ.getJSONArray("args").getString(0)
                onStatusChange(args)
            }
        }
    }

    private fun onStatusChange(args: String) {
        serverState.value = args
    }

    private fun onConsoleLog(args: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = args.replace(
                Regex("(\\u003e....\\r|\\x1b\\[[0-9;]*m|\\u001b\\[K|\\u001b\\[\\?1h\\u001b=\\u001b\\[\\?2004h)"),
                ""
            )

            val testArray = consoleMessages
            if (testArray.size >= 400) {
                testArray.removeFirst()
            }

            testArray.add(result)

            consoleMessages = testArray
            if (autoScroll.value) {
                listState.scrollToItem(consoleMessages.size-1)
            }
        }
    }

    private fun onTokenExpiring() {
        val token = credentialsArray[1]
        webSocketClient.send("""{"event":"auth","args":["$token"]}""")
    }

    private fun onStatsReceive(args: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val arg = JSONObject(args)
            val cpuStat = (arg.getDouble("cpu_absolute").roundTo(2)).toString() + "%"

            val rawMBRamStat = arg.getDouble("memory_bytes") / 1024 / 1024
            val rawGBRamStat = rawMBRamStat / 1024
            val ramStat = if (rawGBRamStat < 1) {
                (rawMBRamStat).roundTo(2).toString() + "MB"
            } else {
                (rawGBRamStat.roundTo(2)).toString() + "GB"
            }

            val rawMBDiskStat = arg.getDouble("disk_bytes") / 1024 / 1024
            val rawGBDiskStat = rawMBDiskStat / 1024
            val diskStat = if (rawGBDiskStat < 1) {
                (rawMBDiskStat).roundTo(2).toString() + "MB"
            } else {
                (rawGBDiskStat.roundTo(2)).toString() + "GB"
            }


            val totalUptime = arg.getLong("uptime") / 1000
            val secUptime = totalUptime % 60
            val minUptime = totalUptime / 60 % 60
            val hourUptime = totalUptime / 60 / 60 % 24
            val dayUptime = totalUptime / 60 / 60 / 24
            val uptime = if (dayUptime.toInt() != 0) {
                dayUptime.toString() + "д " + hourUptime.toString() + "ч " + minUptime.toString() + "м"
            } else {
                hourUptime.toString() + "ч " + minUptime.toString() + "м " + secUptime.toString() + "c"
            }
            statsArray.value = listOf(cpuStat, ramStat, diskStat, uptime).toMutableList()
        }
    }
}