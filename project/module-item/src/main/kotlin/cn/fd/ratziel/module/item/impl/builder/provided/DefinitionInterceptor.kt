package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.script.block.ScriptBlockBuilder
import kotlinx.serialization.json.JsonObject

/**
 * DefinitionInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/10 19:49
 */
object DefinitionInterceptor : ItemInterceptor {

    override fun intercept(element: Element, context: ArgumentContext): ItemData? {
        val property = element.property as? JsonObject ?: return null
        val define = property["define"] as? JsonObject ?: return null

        // 获取定义上下文
        val definition = context.definition()

        for ((name, script) in define) {
            val result = ScriptBlockBuilder.build(script).execute(context) ?: continue
            // 扔到 definition 中
            definition[name] = result
        }

        // 定义解释器只负责解析定义, 不返回数据
        return null
    }

    /**
     * 获取定义上下文
     */
    fun ArgumentContext.definition(): DefinitionContext {
        return this.popOrNull(DefinitionContext::class.java)
            ?: DefinitionContext().also { this.add(it) } // 没有就创建并加入到上下文中
    }

}