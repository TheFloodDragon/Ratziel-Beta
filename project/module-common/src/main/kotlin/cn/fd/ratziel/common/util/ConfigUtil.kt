package cn.fd.ratziel.common.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type

fun Configuration.serializeToJson(): JsonElement {
    this.changeType(Type.JSON)
    return Json.parseToJsonElement(this.saveToString())
}