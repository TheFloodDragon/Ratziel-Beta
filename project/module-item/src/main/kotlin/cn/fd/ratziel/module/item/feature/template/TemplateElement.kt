package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import taboolib.common.platform.function.warning
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
    val templates: MutableMap<String, Template> = ConcurrentHashMap()

    override suspend fun handle(elements: Collection<Element>) {
        val parsed = elements.mapNotNull { TemplateParser.parse(it) }
        // 直接更新模板表
        templates.clear(); templates.putAll(parsed.associateBy { it.name })
        // 所有模板解析完成后, 触发下其元素的继承, 提前完成嵌套模板的基础
        parsed.forEach { it.element }
    }

    /**
     * 根据名称寻找模板, 无法找到时警告
     */
    @JvmStatic
    fun findBy(name: String): Template? {
        val template = templates[name]
        if (template == null) {
            warning("Unknown element named '$name' which is to be inherited!")
            return null
        } else return template
    }

}