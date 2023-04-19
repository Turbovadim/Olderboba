package net.turbovadim.bisquithost.Screens.ServerControl.Backups

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import net.turbovadim.bisquithost.apiKey
import net.turbovadim.bisquithost.network.client
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class BackupsData(
    val total_backups: Int?,
    val backups: MutableList<BackupArrayItem>?
)

data class BackupArrayItem (
    val uuid: String,
    val name: String,
    val bytes: Int,
    val is_locked: Boolean,
    val is_completed: Boolean,
)

@Serializable
data class MyASS (
    val data: List<MyASS2>
)

@Serializable
data class MyASS2 (
    val attributes: TestMyAss
)

@Serializable
data class TestMyAss (
    val uuid: String,
    val name: String,
    val bytes: Int,
    val is_locked: Boolean,
    val created_at: String,
    val completed_at: String
)

class BackupsVM : ViewModel() {

    init {
        println("MyVM: BACK $this")
    }

    var reloadBackupsList = mutableStateOf(false)
        private set
    fun reloadBackupsListFun() {
        reloadBackupsList.value = !reloadBackupsList.value
    }

    fun listBackups(serverId: String): BackupsData {
        var returnBackupsData = BackupsData(null, null)
        val request = Request.Builder()
            .url("https://mgr.bisquit.host/api/client/servers/$serverId/backups")
            .header("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBoba = response.body!!.string()

            val localOBJ = JSONObject(responseBoba)

//            val format = Json { ignoreUnknownKeys = true }
//            val decode = format.decodeFromString<MyASS>(responceBoba)
//            val sdfsdf = decode.data[0].attributes
//            println(decode)

            val meta = localOBJ.getJSONObject("meta")
            val totalBackups = meta.getInt("backup_count")
            val backupItemArray = mutableListOf<BackupArrayItem>()

            if (totalBackups != 0) {
                val data = localOBJ.getJSONArray("data")
                repeat(data.length()) { index ->
                    val backupOBJ = data.getJSONObject(index)
                    val attributes = backupOBJ.getJSONObject("attributes")
                    val uuid = attributes.getString("uuid")
                    val name = attributes.getString("name")
                    val bytes = attributes.getInt("bytes")
                    val isLocked = attributes.getBoolean("is_locked")
                    val isCompleted = attributes.getString("completed_at") != "null"

                    backupItemArray += BackupArrayItem(
                        uuid = uuid,
                        name = name,
                        bytes = bytes,
                        is_locked = isLocked,
                        is_completed = isCompleted,
                    )
                }
            }
            returnBackupsData = BackupsData(
                total_backups = totalBackups,
                backups = backupItemArray
            )
        }
        return returnBackupsData
    }

    fun createBackup(serverId: String): Boolean {
        val request = Request.Builder()
            .url("https://mgr.bisquit.host/api/client/servers/$serverId/backups/")
            .header("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post("".toRequestBody())
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    fun downloadBackup(serverId: String, uuid: String): String {
        val request = Request.Builder()
            .url("https://mgr.bisquit.host/api/client/servers/$serverId/backups/$uuid/download")
            .header("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return JSONObject(response.body!!.string()).getJSONObject("attributes").getString("url")
        }
    }

    fun deleteBackup(serverId: String, uuid: String): Boolean {
        val request = Request.Builder()
            .url("https://mgr.bisquit.host/api/client/servers/$serverId/backups/$uuid")
            .header("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    suspend fun autoRefresh() {
        delay(7000)
        reloadBackupsListFun()
        autoRefresh()
    }
}