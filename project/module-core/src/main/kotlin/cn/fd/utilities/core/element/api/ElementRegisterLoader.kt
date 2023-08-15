package cn.fd.utilities.core.element.api

import cn.fd.utilities.core.element.ElementType
import cn.fd.utilities.core.element.parser.ElementHandler
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
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
        if (clazz.isAnnotationPresent(ElementRegister::class.java) && ElementHandler::class.java.isAssignableFrom(clazz)) {
            val anno = clazz.getAnnotation(ElementRegister::class.java)
            val handler =
                if (instance == null)
                    clazz.asSubclass(ElementHandler::class.java).newInstance()
                else instance.get() as ElementHandler
            println(ElementType(anno.name, handler))
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.INIT
    }

}