package cn.fd.ratziel.common.element

import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.type.ElementService
import cn.fd.ratziel.core.element.type.NewElement
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
                /**
                 * 处理器
                 */
                val handlers =
                    if (ElementHandler::class.java.isAssignableFrom(clazz)) {
                        // 获取实例
                        listOf(clazz.asSubclass(ElementHandler::class.java).getInstance(true)!!.get())
                    } else emptyList()
                /**
                 * 注册
                 */
                ElementService.registerElementType(
                    space = anno.space,
                    name = anno.name,
                    alias = anno.alias,
                    handlers
                )
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
