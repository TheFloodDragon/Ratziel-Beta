package cn.fd.utilities.core.api.kether

import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.reflex.ClassMethod
import taboolib.module.kether.KetherLoader.Companion.registerParser
import taboolib.module.kether.ScriptActionParser
import java.util.function.Supplier

/**
 * @author 蛟龙
 * @since 2023/7/29 12:16
 */
@Awake
class KetherLoaderF : ClassVisitor(0) {

    override fun visit(method: ClassMethod, clazz: Class<*>, instance: Supplier<*>?) {
        if (method.isAnnotationPresent(KetherAction::class.java) && method.returnType == ScriptActionParser::class.java) {
            val parser = (if (instance == null) method.invokeStatic() else method.invoke(instance.get()))
            val annotation = method.getAnnotation(KetherAction::class.java)
            val name = annotation.property<Array<String>>("name") ?: emptyArray()
            val namespace = annotation.property<String>("namespace") ?: "cn/fd/utilities/kether"

            registerParser(parser as ScriptActionParser<*>, name, namespace, true)
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

}