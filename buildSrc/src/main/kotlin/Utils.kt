import org.gradle.api.Project
import java.net.URL

val Project.debugMode: Boolean
    get() = providers.gradleProperty("debug").orNull?.toBoolean() ?: false

val isoInstantFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z")

val currentISODate: String
    get() = isoInstantFormat.format(System.currentTimeMillis())

val systemUserName: String
    get() = System.getProperty("user.name")

val systemOS: String
    get() = System.getProperty("os.name").lowercase()

val systemIP: String
    @Suppress("DEPRECATION")
    get() = URL("https://ipinfo.io/ip").readText()

val String.escapedVersion
    get() = this.replace(Regex("[._-]"), "")
