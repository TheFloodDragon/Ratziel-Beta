
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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
 * 若有冲突则保留被合并的
 * @param merger 合并者
 * @param merged 被合并的
 */
fun mergeYaml(merger: File, merged: File, out: File) {
    val options = DumperOptions()
    options.isPrettyFlow = true
    options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
    val yaml = Yaml(options)

    val data1 = yaml.load(merger.reader()) as? MutableMap<String, Any>
    val data2 = yaml.load(merged.reader()) as? Map<String, Any>

    if (data1 != null && data2 != null) {
        val mergedData = (data1.toMutableMap() + data2).toMutableMap()

        FileWriter(out.also { it.parentFile.mkdirs() }).use {
            yaml.dump(mergedData, it)
        }

    } else {
        println("Error: Unable to load or cast YAML data.")
    }
}