package cn.fd.ratziel.common.element.registry

import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.element.service.ElementRegistry
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.io.getInstance
import taboolib.common.platform.Awake
import taboolib.common.platform.function.severe
import java.util.function.Supplier

/**
 * ElementRegister
 *
 * @author TheFloodDragon
 * @since 2023/8/14 15:50
 */
@Awake
class ElementRegister : ClassVisitor(0) {

    override fun visitStart(clazz: Class<*>, instance: Supplier<*>?) {
        if (clazz.isAnnotationPresent(NewElement::class.java)) {
            val anno = clazz.getAnnotation(NewElement::class.java)
            try {
                val etype = ElementType(anno.space, anno.name, anno.alias)
                /**
                 * 处理器
                 */
                if (ElementHandler::class.java.isAssignableFrom(clazz)) {
                    // 获取实例
                    val handler = clazz.asSubclass(ElementHandler::class.java).getInstance(true)!!.get()
                    ElementRegistry.register(etype, handler, anno.priority)
                } else ElementRegistry.register(etype)
            } catch (e: Exception) {
                severe("Unable to register element form class $clazz!")
                e.printStackTrace()
            }
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.INIT
    }

}
