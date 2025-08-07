package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import java.util.concurrent.ConcurrentHashMap

/**
 * TemplateElement
 *
 * @author TheFloodDragon
 * @since 2025/5/10 16:47
 */
@NewElement("template")
@ElementConfig(parallel = false)
object TemplateElement : ElementHandler {

    /**
     * 解析过的模板表
     */
    val templates: MutableMap<String, Template> = ConcurrentHashMap()

    /**
     * 原始模板表
     */
    val rawTemplates: MutableMap<String, Element> = ConcurrentHashMap()

    /** 集群处理标记 **/
    private var isAssemblageHandling = false

    override fun handle(element: Element) {
        // 更新原始模板表 (自动重载要用)
        if (!isAssemblageHandling) {
            templates.remove(element.name)
            rawTemplates[element.name] = element
        }
        // 跳过被提前解析的
        if (templates.containsKey(element.name)) return
        // 添加解析过的
        val template = Template(element, TemplateParser.findParent(element))
        // findParent 解析了模板的父模板, 也一并注册了
        template.asChain().forEach { templates[it.element.name] = it }
    }

    override fun onStart(elements: Collection<Element>) {
        // 清理
        rawTemplates.clear(); templates.clear()
        // 加入到原始模板表
        elements.forEach { rawTemplates[it.name] = it }
        // 标记集群处理开始
        isAssemblageHandling = true
    }

    override fun onEnd() {
        // 标记集群处理结束
        isAssemblageHandling = false
    }

}