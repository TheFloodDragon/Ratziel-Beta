package cn.fd.ratziel.kether

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
@Deprecated("Need a new design")
class KetherRegister : ClassVisitor(0) {

    override fun visit(method: ClassMethod, clazz: Class<*>, instance: Supplier<*>?) {
        if (method.isAnnotationPresent(NewKetherAction::class.java) && method.returnType == ScriptActionParser::class.java) {
            val annotation = method.getAnnotation(NewKetherAction::class.java)
            val name = annotation.property<Array<String>>("value") ?: emptyArray()
            val namespace = annotation.property<Array<String>>("namespace") ?: emptyArray()
            val shared = annotation.property<Boolean>("shared") ?: true

            // 获取语句对象
            val parser = if (instance != null) {
                method.invoke(instance.get())
            } else {
                method.invokeStatic()
            } as ScriptActionParser<*>

            // 批量注册
            namespace.forEach {
                registerParser(parser, name, it, shared)
            }
        }
    }


// TODO 等啥时候决定搞NeoKether吧
//
//    /**
//     * 注册属性
//     * */
//    override fun visitStart(clazz: Class<*>, instance: Supplier<*>?) {
//        if (clazz.isAnnotationPresent(NewKetherProperty::class.java) && BacikalGenericProperty::class.java.isAssignableFrom(
//                clazz
//            )
//        ) {
//            // 加载注解
//            val annotation = clazz.getAnnotation(NewKetherProperty::class.java)
//
//            // 获取属性对象
//            val property = if (instance != null) {
//                instance.get()
//            } else {
//                try {
//                    clazz.getDeclaredConstructor().newInstance()
//                } catch (ex: Exception) {
//                    ex.printStackTrace(); return
//                }
//            } as BacikalGenericProperty<*>
//
//            registerProperty(property, annotation.bind.java, annotation.shared)
//
//        }
//    }

    override fun getLifeCycle() = LifeCycle.LOAD

}