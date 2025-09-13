package cn.fd.ratziel.common.element.registry

import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.ElementType
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.common.platform.function.severe
import taboolib.library.reflex.ReflexClass

/**
 * ElementRegistrar
 *
 * @author TheFloodDragon
 * @since 2023/8/14 15:50
 */
@Awake
class ElementRegistrar : ClassVisitor(0) {

    override fun visitStart(clazz: ReflexClass) {
        val anno = clazz.getAnnotationIfPresent(NewElement::class.java) ?: return
        val type = ElementType(
            anno.enumName("space", "ratziel"),
            anno.enumName("name"),
            anno.property<List<String>>("alias", emptyList()).toTypedArray()
        )
        // 获取实例并注册处理器
        val handler = findInstance(clazz) as? ElementHandler ?: return
        ElementRegistry.register(type, handler)
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.INIT
    }

}
