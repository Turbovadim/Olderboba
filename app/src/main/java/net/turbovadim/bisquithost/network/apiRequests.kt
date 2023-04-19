package net.turbovadim.bisquithost.network

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import coil.network.HttpException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.turbovadim.bisquithost.Name
import net.turbovadim.bisquithost.TotalServers
import net.turbovadim.bisquithost.Username
import net.turbovadim.bisquithost.apiKey
import net.turbovadim.bisquithost.network.Requests.okHttpGetRequest
import net.turbovadim.bisquithost.network.Requests.okHttpPostRequest
import net.turbovadim.bisquithost.serverAttributes
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

val client = OkHttpClient()

data class ServerAttributes (
    val name: String,
    val ram: Int,
    val cpu: Int,
    val disk: Int,
    val id: String,
    val sftpUrl: String,
    val backupsLimit: Int,
    val isSuspended: Boolean,
)

class RequestsVM : ViewModel() {

    var expanded = mutableStateOf(false)
        private set

    fun setExpanded(state: Boolean) {
        expanded.value = state
    }

    var isLoaded = mutableStateOf(false)
        private set

    fun setIsLoaded(state: Boolean) {
        isLoaded.value = state
    }

    fun listServers() {
        isLoaded.value = false
        val request = okHttpGetRequest("https://mgr.bisquit.host/api/client", apiKey)

        clearSavedLimits()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val localListServerOBJ = JSONObject(response.body!!.string())

            TotalServers = localListServerOBJ.getJSONObject("meta").getJSONObject("pagination").getInt("total")
            CoroutineScope(Dispatchers.Default).launch {
                listServerParser(localListServerOBJ)
            }
        }
    }

    private fun listServerParser(jObject: JSONObject) {
        repeat(TotalServers) { index ->
            val jsonAttributes =
                jObject.getJSONArray("data").getJSONObject(index).getJSONObject("attributes")
            val jsonLimits = jsonAttributes.getJSONObject("limits")

            serverAttributes += ServerAttributes(
                name = jsonAttributes.getString("name"),
                ram = jsonLimits.getInt("memory"),
                cpu = jsonLimits.getInt("cpu"),
                disk = jsonLimits.getInt("disk"),
                id = jsonAttributes.getString("identifier"),
                sftpUrl = jsonAttributes.getJSONObject("sftp_details").getString("ip"),
                backupsLimit = jsonAttributes.getJSONObject("feature_limits").getInt("backups"),
                isSuspended = jsonAttributes.getBoolean("is_suspended")
            )
        }
        setIsLoaded(true)
    }

    fun sendCommand(serverId: String, command: String) {
        val fCmd = if (command != "") command else "/"
        val postBody = """{"command": "$fCmd"}"""
        val request = okHttpPostRequest("https://mgr.bisquit.host/api/client/servers/$serverId/command", apiKey, postBody)

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw HttpException(response)
        }
    }

    fun renameServer(serverId: String, name: String, description: String) {
        val postBody = """{"name": "$name", "description": "$description"}"""
        val request = okHttpPostRequest("https://mgr.bisquit.host/api/client/servers/$serverId/settings/rename", apiKey, postBody)

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            Name = name
        }
    }

    fun changePowerState(serverId: String, state: String) {
        val postBody = """{"signal": "$state"}"""
        val request = okHttpPostRequest("https://mgr.bisquit.host/api/client/servers/$serverId/power", apiKey, postBody)

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
        }
    }

    fun getAccount() {
        val request = okHttpGetRequest("https://mgr.bisquit.host/api/client/account", apiKey)

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val localOBJ = JSONObject(response.body!!.string())
            val attributes = localOBJ.getJSONObject("attributes")
            val username = attributes.getString("username")
            Username = username
        }
    }

    private fun clearSavedLimits() {
        serverAttributes.clear()
    }
}

fun uploadFile(url: String, file: File) {
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("files", "logo-square.png",
            file.asRequestBody())
        .build()

    val request = Request.Builder()
        .header("Accept", "application/json")
//        .header("Content-Type", "application/json")
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        println(response.body!!.string())
    }
}

fun getSignedUrl(serverId: String): String {
    val request = okHttpGetRequest("https://mgr.bisquit.host/api/client/servers/$serverId/files/upload", apiKey)
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val response2 = response.body!!.string()
        return JSONObject(response2).getJSONObject("attributes").getString("url")
    }
}
