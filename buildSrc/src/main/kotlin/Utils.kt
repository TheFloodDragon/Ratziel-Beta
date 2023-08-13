import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.bukkit.configuration.file.YamlConfiguration.loadConfiguration as loadConfig

val currentISODate: String
    get() = isoInstantFormat.format(System.currentTimeMillis())

val systemUserName: String
    get() = System.getProperty("user.name")

val systemOS: String
    get() = System.getProperty("os.name").lowercase()

val systemIP: String
    get() = URL("http://ipinfo.io/ip").readText()

fun getLatestRelease(repoOwner: String, repoName: String): String {
    val url = URL("https://api.github.com/repos/$repoOwner/$repoName/releases/latest")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("Accept", "application/vnd.github+json")

    if (connection.responseCode != 200) {
        error("Failed to retrieve the latest release")
    }

    val reader = BufferedReader(InputStreamReader(connection.inputStream))
    val response = reader.readText()
    reader.close()

    val index = response.indexOf("\"tag_name\":") + "\"tag_name\":".length + 1
    val tagName = response.substring(index, response.indexOf(",", index))
    return tagName.replace("\"", "")
}

/**
 * 合并两个YAML文件
 * ps: 还是BukkitAPI好用
 * @param merger 合并者
 * @param merged 被合并的
 */
fun mergeYaml(merger: File, merged: File, out: File) {
    loadConfig(if (out.exists()) out else merger).apply {
        loadConfig(merged).let {
            it.getKeys(false).forEach { k ->
                set(k, it.get(k))
                setInlineComments(k, it.getInlineComments(k))
                setComments(k, it.getComments(k))
            }
        }
        save(out)
    }
}