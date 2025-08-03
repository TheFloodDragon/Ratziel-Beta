package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.functional.replenish
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.builder.ItemStream
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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

    inner class StreamGenerator(
        val baseStream: NativeItemStream,
        val streamHandler: suspend (ItemStream) -> Unit,
    ) {

        init {
            runBlocking {
                // 纯静态物品模式处理
                if (fullStaticMode) {
                    // 直接将静态属性应用到基流
                    applyStaticProperty(baseStream)
                }
            }
        }

        /**
         * 获取静态物品流处理任务
         */
        fun stream(): Deferred<NativeItemStream> = streamGenerating

        /**
         * 物品流生成
         *
         * @param replenish 每获取一次补充一次
         */
        private val streamGenerating: Deferred<NativeItemStream> by replenish {
            ItemElement.scope.async {
                val stream = baseStream.copy()
                // 静态物品模式启用, 并且不是全静态模式
                if (enabled && !fullStaticMode) {
                    applyStaticProperty(stream) // 应用静态属性
                }
                return@async stream
            }
        }

        /**
         * 应用静态属性 (使用静态的配置处理流)
         */
        private suspend fun applyStaticProperty(stream: ItemStream) {
            // 原始元素
            val origin = stream.fetchElement()
            // 生成静态物品
            stream.updateElement(staticContent ?: return)
            // 好戏开场: 使用静态配置处理流数据
            streamHandler(stream)
            // 换回去
            stream.updateElement(origin)
        }

    }

    companion object {

        @JvmStatic
        fun fromStream(baseStream: ItemStream): StaticStrategy = runBlocking { StaticStrategy(baseStream.fetchElement()) }

    }

}