package net.turbovadim.bisquithost.network

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import net.turbovadim.bisquithost.apiKey
import net.turbovadim.bisquithost.network.Requests.okHttpGetRequest
import net.turbovadim.bisquithost.network.Requests.okHttpPostRequest
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class FileManagerAttributes (
    val name: String,
    val mode_bits: String,
    val size: Int,
    val file_icon: ImageVector,
    val icon_color: Color,
    val is_file: Boolean,
    val is_image: Boolean,
    val is_editable: Boolean,
    val mimetype: String,
    val created_at: String,
    val modified_at: String
)

class FileManagerVM : ViewModel() {
    var refreshFiles = mutableStateOf(true)
        private set
    fun callRefreshFiles() {
        refreshFiles.value = !refreshFiles.value
    }
}

fun getFiles(serverId: String, directory: String? = ""): List<FileManagerAttributes> {

    val filesList = mutableListOf<FileManagerAttributes>()
    val request = okHttpGetRequest("https://mgr.bisquit.host/api/client/servers/$serverId/files/list?directory=$directory", apiKey)

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val localOBJ = JSONObject(response.body!!.string())

        val arrayLength = localOBJ.getJSONArray("data").length()
        repeat(arrayLength) {
            val attributes = localOBJ.getJSONArray("data").getJSONObject(it).getJSONObject("attributes")
            val name = attributes.getString("name")
            val modeBits = attributes.getString("mode_bits")
            val size = attributes.getInt("size")
            val isFile = attributes.getBoolean("is_file")
            val mimetype = attributes.getString("mimetype")
            val createdAt = attributes.getString("created_at")
            val modifiedAt = attributes.getString("modified_at")
            val isImage = mimetype.contains("image", true)
            val isEditable = mimetype.contains("text/plain", true) || mimetype.contains("application/json", true)

            val fileIcon: ImageVector
            val iconColor: Color

            if (
                !isFile
            ) {
                iconColor = Color(0xFFECBE17)
                fileIcon = Icons.Rounded.Folder
            } else if (
                mimetype.contains("text/plain", true) ||
                mimetype.contains("application/json", true))
            {
                iconColor = Color(0xFFFFFFFF)
                fileIcon = Icons.Rounded.Description
            } else if (
                mimetype.contains("application/zip", true) ||
                mimetype.contains("application/gzip", true)
            ) {
                iconColor = Color(0xFFECBE17)
                fileIcon = Icons.Rounded.FolderZip
            } else if (
                isImage
            ) {
                iconColor = Color(0xFF179FD5)
                fileIcon = Icons.Rounded.Image
            } else {
                iconColor = Color(0xFFACACAC)
                fileIcon = Icons.Rounded.Plagiarism
            }

            filesList += FileManagerAttributes(
                name = name,
                mode_bits = modeBits,
                size = size,
                file_icon = fileIcon,
                is_file = isFile,
                icon_color = iconColor,
                is_image = isImage,
                is_editable = isEditable,
                mimetype = mimetype,
                created_at = createdAt,
                modified_at = modifiedAt
            )

        }
    }
    return filesList
}

fun downloadFile(serverId: String, directory: String, file: String): String {
    val request = okHttpGetRequest("https://mgr.bisquit.host/api/client/servers/$serverId/files/download?file=$directory$file", apiKey)

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val localOBJ = JSONObject(response.body!!.string())
        val attributes = localOBJ.getJSONObject("attributes")
        return attributes.getString("url")
    }
}

fun getFileContent(serverId: String, directory: String, file: String): String {
    val request = okHttpGetRequest("https://mgr.bisquit.host/api/client/servers/$serverId/files/contents?file=$directory$file", apiKey)

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return response.body?.string()!!
    }
}

fun editFileContent(serverId: String, directory: String, file: String, content: String): String {
    val request = okHttpPostRequest("https://mgr.bisquit.host/api/client/servers/$serverId/files/write?file=$directory$file", apiKey, content, "text/plain")

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return response.body?.string()!!
    }
}

fun deleteFile(serverId: String, directory: String, file: String): Boolean {
    val postBody =
        """
            {
              "root": "/$directory",
              "files": [
                "$file"
              ]
            }
        """.trimIndent()
    val request = okHttpPostRequest("https://mgr.bisquit.host/api/client/servers/$serverId/files/delete", apiKey, postBody)

    client.newCall(request).execute().use { response ->
        if (response.isSuccessful) {
            return true
        } else {
            throw IOException("Unexpected code $response")
        }
    }
}

fun createFolder(serverId: String, directory: String, name: String): Boolean {
    val postBody =
        """
            {
            "root": "/$directory",
            "name": "$name"
            }
        """.trimIndent()
    val request = Request.Builder()
        .url("https://mgr.bisquit.host/api/client/servers/$serverId/files/create-folder")
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(postBody.toRequestBody())
        .build()

    client.newCall(request).execute().use { response ->
        if (response.isSuccessful) {
            return true
        } else {
            throw IOException("Unexpected code $response")
        }
    }
}

fun renameFile(serverId: String, directory: String, file: String, name: String): Boolean {
    val postBody =
        """
        {
          "root": "/$directory",
          "files": [
            {
              "from": "$file",
              "to": "$name"
            }
          ]
        }
        """.trimIndent()
    val request = Request.Builder()
        .url("https://mgr.bisquit.host/api/client/servers/$serverId/files/rename")
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $apiKey")
        .put(postBody.toRequestBody())
        .build()

    client.newCall(request).execute().use { response ->
        if (response.isSuccessful) {
            return true
        } else {
            throw IOException("Unexpected code $response")
        }
    }
}