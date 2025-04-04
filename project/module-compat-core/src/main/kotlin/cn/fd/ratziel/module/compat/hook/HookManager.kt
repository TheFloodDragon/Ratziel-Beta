package cn.fd.ratziel.module.compat.hook

import cn.fd.ratziel.module.compat.CompatibleClassLoader
import taboolib.common.ClassAppender
import taboolib.common.LifeCycle
import taboolib.common.TabooLib
import taboolib.common.platform.Awake
import taboolib.common.platform.function.console
import taboolib.library.reflex.ClassAnnotation
import taboolib.library.reflex.ClassMethod
import taboolib.library.reflex.ReflexClass
import taboolib.module.lang.sendLang
import java.util.*

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
     * 默认CompatibleClassLoader实例
     */
    val hookClassLoader = CompatibleClassLoader(this::class.java, ClassAppender.getClassLoader())

    /**
     * 注册钩子
     */
    fun register(hook: PluginHook) {
        // Add to Registry
        registry[hook.pluginName] = hook
        // Add ClassProvider
        if (hook is ManagedPluginHook) hook.bindProvider?.let { hookClassLoader.addProvider(it) }
    }

    /**
     * 取消注册钩子
     */
    fun unregister(hook: PluginHook) {
        // Remove from Registry
        registry.remove(hook.pluginName)
        // Remove ClassProvider
        if (hook is ManagedPluginHook) hook.bindProvider?.let { hookClassLoader.removeProvider(it) }
    }

    @Awake(LifeCycle.LOAD)
    fun hookInject() {
        // 在 LOAD, ENABLE, ACTIVE, DISABLE 时分别注入
        for (lifeCycle in arrayOf(LifeCycle.LOAD, LifeCycle.ENABLE, LifeCycle.ACTIVE, LifeCycle.DISABLE)) {
            TabooLib.registerLifeCycleTask(lifeCycle, 10) {
                for (hook in registry.values) {
                    // 未挂钩成功的直接跳过
                    if (!hook.isHooked) continue
                    // 获取要注入的类
                    val classes: List<Class<*>> = if (hook is ManagedPluginHook) {
                        // 加载受托管的类(强制自加载)
                        hook.managedClasses.map {
                            hookClassLoader.loadClass(it, resolve = false, forceSelfLoad = true)
                        }
                    } else Collections.singletonList(hook::class.java)

                    for (clazz in classes) {
                        inject(clazz) { reflexClass, method, anno ->
                            // 生命周期不匹配时返回
                            if (anno.property<LifeCycle>("lifeCycle") != lifeCycle) return@inject
                            // 尝试执行, 错误时输出信息并取消注册
                            try {
                                if (method.isStatic) {
                                    method.invokeStatic()
                                } else {
                                    val instance = reflexClass.getInstance {
                                        Class.forName(it, false, clazz::class.java.classLoader)
                                    }
                                    method.invoke(instance!!)
                                }
                            } catch (ex: Exception) {
                                unregister(hook) // 取消注册
                                console().sendLang("Plugin-Compat-Failed", hook.pluginName)
                                ex.printStackTrace()
                            }
                        }
                    }
                    // ACTIVE 时输出成功信息
                    if (lifeCycle == LifeCycle.ACTIVE) {
                        console().sendLang("Plugin-Compat-Hooked", hook.pluginName)
                    }
                }
            }
        }
    }

    /**
     * 通过 [HookInject] 对类进行依赖注入
     */
    fun inject(clazz: Class<*>, action: (ReflexClass, ClassMethod, ClassAnnotation) -> Unit) {
        val reflexClass = ReflexClass.of(clazz, false)
        for (method in reflexClass.structure.methods) {
            val anno = method.getAnnotationIfPresent(HookInject::class.java) ?: continue
            action(reflexClass, method, anno)
        }
    }

}
