import org.gradle.api.Project
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

val Project.githubRepo
    get() = try {
        this.getProperty("githubRepo") ?: System.getenv("GITHUB_REPO")
    } catch (ex: NullPointerException) {
        null
    } ?: "TheFloodDragon/Ratziel-Beta"

val Project.githubToken: String
    get() = (this.getProperty("githubKey", "githubToken") ?: System.getenv("GITHUB_TOKEN")).toString()

val Project.githubUser
    get() = (this.getProperty("githubUsername", "githubUser") ?: System.getenv("GITHUB_USERNAME")).toString()

val Project.githubOwner get() = this.githubRepo.split('/')[0]

val Project.githubRepoName get() = this.githubRepo.split('/')[1]

fun getLatestRelease(repoOwner: String, repoName: String, overwrite: String? = null, fallback: String? = null): String =
    overwrite ?: try {
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
        tagName.replace("\"", "")
    } catch (e: Exception) {
        e.printStackTrace()
        fallback ?: throw e
    }