package cn.fd.utilities.core.element.api

import cn.fd.utilities.core.element.ElementType
import cn.fd.utilities.core.element.parser.ElementHandler
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.reflex.Reflex.Companion.getProperty
import java.util.function.Supplier

/**
 * ElementRegisterLoader
 *
 * @author: TheFloodDragon
 * @since 2023/8/14 15:50
 */
@Awake
class ElementRegisterLoader : ClassVisitor(0) {

    override fun visitStart(clazz: Class<*>, instance: Supplier<*>?) {
        if (clazz.isAnnotationPresent(ElementRegister::class.java) && clazz.isAssignableFrom(ElementHandler::class.java)) {
            val anno = clazz.getAnnotation(ElementRegister::class.java)
            val name = anno.getProperty<Array<String>>("name")
            println(name)
            // 创建 ElementType
            name?.let {
                println(ElementType(it, instance?.get() as ElementHandler))
            }
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.INIT
    }

}