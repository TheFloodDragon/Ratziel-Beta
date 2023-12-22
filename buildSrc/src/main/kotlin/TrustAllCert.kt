import org.gradle.api.Plugin
import org.gradle.api.Project
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * TrustAllCert - 信任所有证书
 *
 * @author TheFloodDragon
 * @since 2023/12/22 21:17
 */
class TrustAllCert : Plugin<Project> {

    override fun apply(target: Project) {
        val nullTrustManager = object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
            override fun getAcceptedIssuers(): Array<out X509Certificate>? = null
        }
        val nullHostnameVerifier = HostnameVerifier { _, _ -> true }
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, arrayOf(nullTrustManager), null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier(nullHostnameVerifier)
        println("[Warning] You have trust all certifications, please take care!")
    }

}