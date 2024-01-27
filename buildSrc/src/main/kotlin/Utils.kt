import org.gradle.api.Project
import java.net.URL

val currentISODate: String
    get() = isoInstantFormat.format(System.currentTimeMillis())

val systemUserName: String
    get() = System.getProperty("user.name")

val systemOS: String
    get() = System.getProperty("os.name").lowercase()

val systemIP: String
    get() = URL("http://ipinfo.io/ip").readText()

val String.escapedVersion
    get() = this.replace(Regex("[._-]"), "")

fun Project.getProperty(vararg names: String): String? {
    names.forEach { name ->
        this.findProperty(name)?.let { return it.toString() }
    }; return null
}