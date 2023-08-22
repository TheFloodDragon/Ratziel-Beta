package cn.fd.utilities.core.element.type

import cn.fd.utilities.core.element.api.ElementHandler
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.io.getInstance
import taboolib.common.platform.Awake
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
                /**
                 * 处理器
                 */
                val handlers =
                    if (ElementHandler::class.java.isAssignableFrom(clazz)) {
                        // 获取实例
                        setOf(
                            if (instance == null)
                                clazz.asSubclass(ElementHandler::class.java).getInstance(true)!!.get()
                            else instance.get() as ElementHandler
                        )
                    } else emptySet()
                /**
                 * 注册
                 */
                ElementService.registerElementType(
                    space = anno.space,
                    name = anno.name,
                    alias = anno.alias.toSet(),
                    handlers
                )
            } catch (e: Exception) {
                e.printStackTrace()
                error("Unable to register element form class $clazz!")
            }
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.INIT
    }

}
