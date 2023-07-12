package tb.common.platform.service

import taboolib.common.platform.PlatformService
import java.io.File

@PlatformService
interface PlatformIO {

    val pluginId: String

    val pluginVersion: String

    val isPrimaryThread: Boolean

    fun <T> server(): T

    fun info(vararg message: Any?)

    fun severe(vararg message: Any?)

    fun warning(vararg message: Any?)

    fun releaseResourceFile(path: String, replace: Boolean = false): File

    //修改部分——添加
    fun releaseResourceFile(resource: String, target: String = resource, replace: Boolean = false): File

    fun getJarFile(): File

    fun getDataFolder(): File

    fun getPlatformData(): Map<String, Any>
}