package cn.fd.ratziel.compat.hook

import cn.fd.ratziel.compat.CompatibleClassLoader
import taboolib.common.LifeCycle
import taboolib.common.platform.function.console
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.library.reflex.ReflexClass
import taboolib.module.lang.sendLang
import java.util.function.Consumer

/**
 * HookManager
 *
 * @author TheFloodDragon
 * @since 2024/2/15 16:44
 */
object HookManager {

    /**
     * 钩子注册表
     */
    val registry: HashMap<String, PluginHook> = hashMapOf()

    /**
     * 注册钩子
     */
    fun register(hook: PluginHook) {
        // 添加进注册表
        registry[hook.pluginName] = hook
        // 挂钩提示
        if (hook.isHooked()) console().sendLang("Plugin-Compat-Hooked", hook.pluginName)
    }

    /**
     * 创建一个钩子Map
     */
    fun hookMapOf(vararg pairs: Pair<Int, String>, resolve: Boolean = true, precedence: ClassLoader? = null) = HookMap().apply {
        pairs.forEach {
            this[it.first] = findLinked(it.second, resolve, precedence)
        }
    }

    /**
     * 获取链接类 (通过 [CompatibleClassLoader.instance] )
     */
    @Deprecated("precedence 暂未使用")
    fun findLinked(className: String, resolve: Boolean, precedence: ClassLoader?) = CompatibleClassLoader.instance.loadClass(className, resolve)

    /**
     * 通过反射对有注解 [HookInject] 的方法进行调用
     */
    fun hookWithAnno(clazz: Class<*>, function: Consumer<Pair<LifeCycle, Runnable>>) {
        ReflexClass.of(clazz, false).structure.methods.forEach {
            // 获取注解, runCatching避免NoSuchElementException
            val anno = kotlin.runCatching { it.getAnnotation(HookInject::class.java) }.getOrNull()
            // 有这样的注解
            if (anno != null) {
                function.accept(anno.property("lifeCycle", LifeCycle.ENABLE) to Runnable { it.invoke(clazz.unsafeInstance()) })
            }
        }
    }

    fun hookWithAnno(clazz: Class<*>, lifeCycle: LifeCycle) = hookWithAnno(clazz) { if (it.first == lifeCycle) it.second.run() }

}