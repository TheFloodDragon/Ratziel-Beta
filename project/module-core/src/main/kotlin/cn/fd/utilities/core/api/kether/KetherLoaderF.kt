package cc.trixey.invero.core.script.loader

import cn.fd.utilities.core.api.kether.KetherParserF
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
        if (method.isAnnotationPresent(KetherParserF::class.java) && method.returnType == ScriptActionParser::class.java) {
            val parser = (if (instance == null) method.invokeStatic() else method.invoke(instance.get()))
            val annotation = method.getAnnotation(KetherParserF::class.java)
            val name = annotation.property<Array<String>>("name") ?: emptyArray()
            val namespace = annotation.property<Boolean>("keNameSpace") ?: false

            registerParser(parser as ScriptActionParser<*>, name, if (namespace) "kether" else "fdutilities", true)
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

}