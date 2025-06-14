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
     * 全静态模式
     */
    var fullStaticMode: Boolean = false
        private set

    /**
     * 静态属性
     */
    var staticProperty: JsonElement? = null
        private set

    // TODO 完成下这个
//    /**
//     * 是否可变
//     */
//    var mutable: Boolean = true
//        private set

    init {
        initialize()
    }

    private fun initialize() {
        // 静态物品字段的属性
        val property = (element as? JsonObject)?.get("static") ?: return

        if (property is JsonPrimitive) {
            // 启用静态物品模式
            this.enabled = true
            // 全静态模式: static 是否为 true
            this.fullStaticMode = property.booleanOrNull == true
        } else if (property is JsonObject) {
            // 选项调控静态模式的启用 (默认启用)
            this.enabled = boolean(property, "enabled") ?: true
            // 选项调控全静态模式的启用 (默认禁用)
            this.fullStaticMode = boolean(property, "full-static") ?: false
//            this.mutable = boolean(property, "mutable") ?: false
        }

        // 全静态模式和半静态模式的属性
        if (this.fullStaticMode) {
            this.staticProperty = element
        } else {
            this.staticProperty = property
        }
    }

    private fun boolean(property: JsonObject, vararg names: String): Boolean? {
        return (property.getBy(*names) as? JsonPrimitive)?.booleanOrNull
    }

}