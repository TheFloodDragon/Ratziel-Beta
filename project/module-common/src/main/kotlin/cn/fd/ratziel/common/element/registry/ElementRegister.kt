package cn.fd.ratziel.common.element.registry

import cn.fd.ratziel.common.element.ElementRegistry
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.ElementType
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.reflex.ReflexClass

/**
 * ElementRegister
 *
 * @author TheFloodDragon
 * @since 2023/8/14 15:50
 */
@Awake
class ElementRegister : ClassVisitor(0) {

    override fun visitStart(clazz: ReflexClass) {
        val anno = clazz.getAnnotationIfPresent(NewElement::class.java) ?: return
        val type = ElementType(
            anno.enumName("space", "ratziel"),
            anno.enumName("name"),
            anno.property<List<String>>("alias", emptyList()).toTypedArray()
        )
        if (clazz.hasInterface(ElementHandler::class.java)) {
            // 获取实例
            val handler = findInstance(clazz) as ElementHandler
            ElementRegistry.register(type, handler)
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.INIT
    }

}
