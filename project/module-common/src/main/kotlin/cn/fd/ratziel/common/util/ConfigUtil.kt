package cn.fd.ratziel.common.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import taboolib.common.platform.function.severe
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

object ConfigUtil {

    fun loadFromFile(file: File): Configuration? {
        // 文件类型
        val type = Configuration.getTypeFromExtension(file.extension)
        // 加载 (不支持HOCON类型)
        if (type != Type.HOCON)
            return Configuration.loadFromFile(file)
        else
            severe("Unsupported file type 'HOCON' !")
        return null
    }

    fun serializeToJson(conf: Configuration): JsonElement {
        // 改变成 Json
        conf.changeType(Type.JSON)
        return Json.parseToJsonElement(conf.saveToString())
    }

}