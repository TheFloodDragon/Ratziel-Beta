package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import kotlinx.serialization.json.JsonElement
import java.util.concurrent.ConcurrentHashMap

/**
 * TemplateElement
 *
 * @author TheFloodDragon
 * @since 2025/5/10 16:47
 */
@NewElement("template")
object TemplateElement : ElementHandler {

    /**
     * 模板表
     */
    val templateMap: MutableMap<String, JsonElement> = ConcurrentHashMap()

    override fun handle(element: Element) {
        // 加入到模板表里
        templateMap[element.name] = element.property
    }

    override fun onStart(elements: Collection<Element>) {
        // 清空表
        templateMap.clear()
    }

}