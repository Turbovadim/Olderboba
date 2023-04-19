package net.turbovadim.bisquithost.network.Requests

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.turbovadim.bisquithost.apiKey
import net.turbovadim.bisquithost.network.client
import java.io.IOException

@Serializable
data class RawServerStats(
    val attributes: ServerStatsAttributes
)
@Serializable
data class ServerStatsAttributes(
    val current_state: String,
    val resources: ServerStatsStats
)
@Serializable
data class ServerStatsStats(
    val memory_bytes: Long,
    val cpu_absolute: Float,
    val disk_bytes: Long,
)

fun getServerStats(serverId: String): RawServerStats {
    val request = okHttpGetRequest("https://mgr.bisquit.host/api/client/servers/$serverId/resources", apiKey)

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val format = Json { ignoreUnknownKeys = true }
        return format.decodeFromString(response.body!!.string())
    }
}

data class FormattedServerStats(
    var current_state: String,
    val ram: Int,
    val cpu: Int,
    val disk: Int
)

fun formatServerStats(rawServerStats: RawServerStats): FormattedServerStats {
    return FormattedServerStats(
        current_state = rawServerStats.attributes.current_state,
        ram = (rawServerStats.attributes.resources.memory_bytes / 1024 / 1024).toInt(),
        cpu = rawServerStats.attributes.resources.cpu_absolute.toInt(),
        disk = (rawServerStats.attributes.resources.disk_bytes / 1024 / 1024).toInt(),
    )
}
