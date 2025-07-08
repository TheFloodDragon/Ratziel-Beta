package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.json.getBy
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull

/**
 * StaticStrategy - 静态物品策略
 *
 * @author TheFloodDragon
 * @since 2025/6/14 19:18
 */
class StaticStrategy(val element: JsonElement) {

    /**
     * 静态物品是否启用
     */
    var enabled: Boolean = false
        private set

    /**
     * 静态元素内容
     */
    var staticContent: JsonElement? = null
        private set

    /**
     * 全静态模式 (如果是全静态模式，则整个原始元素都是静态内容)
     */
    var fullStaticMode: Boolean = false
        private set

    init {
        // 静态物品字段的属性
        val property = (element as? JsonObject)?.get("static")

        if (property is JsonPrimitive) {
            // 启用静态物品模式
            enabled = property.booleanOrNull == true
            // 全静态模式
            if (enabled) fullStaticMode = true
        } else if (property is JsonObject) {
            // 选项调控静态模式的启用 (默认启用)
            enabled = boolean(property, "enabled") ?: true
            // 选项调控全静态模式的启用 (默认禁用)
            if (enabled) fullStaticMode = boolean(property, "full-static") ?: false
        }

        if (enabled) {
            // 如果是全静态模式，则整个原始元素都是静态内容, 反则就是 static 节点下的内容
            staticContent = if (fullStaticMode) element else property
        }
    }

    private fun boolean(property: JsonObject, vararg names: String): Boolean? {
        return (property.getBy(*names) as? JsonPrimitive)?.booleanOrNull
    }

}