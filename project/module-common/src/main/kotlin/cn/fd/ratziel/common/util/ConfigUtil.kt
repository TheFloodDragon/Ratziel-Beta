package cn.fd.ratziel.common.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type

object ConfigUtil {

    fun serializeToJson(conf: Configuration): JsonElement {
        // 改变成 Json
        conf.changeType(Type.JSON)
        return Json.parseToJsonElement(conf.saveToString())
    }

}