package cn.fd.ratziel.common.element.registry

import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.element.service.ElementRegistry
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.common.platform.function.severe
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
        try {
            val type = ElementType(
                anno.property<String>("space", "ratziel"),
                anno.property<String>("name")!!,
                anno.property<List<String>>("alias", emptyList()).toTypedArray()
            )
            if (clazz.hasInterface(ElementHandler::class.java)) {
                // 获取实例
                val handler = findInstance(clazz) as ElementHandler
                ElementRegistry.register(type, handler, anno.property<Byte>("priority", 0))
            } else ElementRegistry.register(type)
        } catch (e: Exception) {
            severe("Unable to register element form class $clazz!")
            e.printStackTrace()
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.INIT
    }

}
