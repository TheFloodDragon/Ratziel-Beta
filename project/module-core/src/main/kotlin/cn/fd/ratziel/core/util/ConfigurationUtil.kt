package cn.fd.ratziel.core.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type

/**
 * 将配置转为 Json 节点。
 */
fun Configuration.toJsonElement(): JsonElement {
    changeType(Type.JSON)
    return Json.parseToJsonElement(saveToString())
}
