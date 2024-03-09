package cn.fd.ratziel.compat.hook

import cn.fd.ratziel.compat.CompatibleClassLoader
import taboolib.common.LifeCycle
import taboolib.common.TabooLib
import taboolib.common.classloader.IsolatedClassLoader
import taboolib.common.platform.Awake
import taboolib.common.platform.function.console
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.library.reflex.ReflexClass
import taboolib.module.lang.sendLang
import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.JarURLConnection
import java.net.URISyntaxException
import java.util.*
import java.util.function.BiConsumer
import java.util.jar.JarFile

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
    val hookClassLoader = CompatibleClassLoader(this::class.java, IsolatedClassLoader.INSTANCE.parent)

    /**
     * 注册钩子
     */
    fun register(hook: PluginHook) {
        if (hook.isHookable()) {
            // Add to Registry
            registry[hook.pluginName] = hook
            // Add ClassLoaderProvider
            if (hook is ManagedPluginHook) hook.bindProvider?.let { hookClassLoader.addProvider(it) }
        }
    }

    /**
     * 取消注册钩子
     */
    fun unregister(hook: PluginHook) {
        // Remove from Registry
        registry.remove(hook.pluginName)
        // Remove ClassLoaderProvider
        if (hook is ManagedPluginHook) hook.bindProvider?.let { hookClassLoader.removeProvider(it) }
    }

    fun <T : ManagedPluginHook> buildHookClasses(hookClass: Class<T>) =
        classNamesInJar.mapNotNull { name ->
            name.takeIf { it.startsWith(hookClass.`package`.name) && !it.startsWith(hookClass.name) }
        }.toTypedArray()

    /**
     * 通过 [HookInject] 对类进行依赖注入
     */
    fun inject(clazz: Class<*>, action: BiConsumer<HookInject, Method>) =
        clazz.methods.forEach {
            if (it.isAnnotationPresent(HookInject::class.java))
                action.accept(it.getAnnotation(HookInject::class.java), it)
        }

    @Awake(LifeCycle.LOAD)
    fun hookInject() {
        // 在 LOAD, ENABLE, ACTIVE, DISABLE 时分别注入
        for (lifeCycle in arrayOf(LifeCycle.LOAD, LifeCycle.ENABLE, LifeCycle.ACTIVE, LifeCycle.DISABLE)) {
            TabooLib.registerLifeCycleTask(lifeCycle, 10) {
                // 获取已注册的 ManagedPluginHook
                registry.values.forEach {
                    if (it is ManagedPluginHook) {
                        // 对 managedClasses 分别执行注入
                        it.managedClasses.forEach { className ->
                            // 加载受托管的类(强制自加载)
                            val clazz = hookClassLoader.loadClass(className, resolve = false, forceSelfLoad = true)
                            inject(clazz) { anno, method ->
                                // 生命周期不匹配时返回
                                if (anno.lifeCycle != lifeCycle) return@inject
                                // 尝试执行, 错误时输出信息并取消注册
                                try {
                                    val instance =
                                        if (Modifier.isStatic(method.modifiers)) null
                                        else ReflexClass.of(clazz).getField("INSTANCE", findToParent = false, remap = false).get()
                                            ?: kotlin.runCatching { clazz.getConstructor().newInstance() }.getOrNull()
                                            ?: clazz.unsafeInstance()
                                    method.invoke(instance)
                                } catch (ex: Exception) {
                                    unregister(it) // 取消注册
                                    console().sendLang("Plugin-Compat-Failed", it.pluginName)
                                    ex.printStackTrace()
                                }
                            }
                        }
                    }
                    // ACTIVE 时输出成功信息
                    if (lifeCycle == LifeCycle.ACTIVE)
                        console().sendLang("Plugin-Compat-Hooked", it.pluginName)
                }
            }
        }
    }

    /**
     * 当前插件Jar文件内所有的类名
     */
    val classNamesInJar: List<String> by lazy {
        val classNames = LinkedList<String>()
        val url = TabooLib::class.java.protectionDomain.codeSource.location
        val srcFile = try {
            File(url.toURI())
        } catch (ex: IllegalArgumentException) {
            File((url.openConnection() as JarURLConnection).jarFileURL.toURI())
        } catch (ex: URISyntaxException) {
            File(url.path)
        }
        if (srcFile.isFile) {
            JarFile(srcFile).stream().filter { it.name.endsWith(".class") }.forEach {
                classNames += it.name.replace('/', '.').substringBeforeLast(".class")
            }
        } else {
            srcFile.walkTopDown().filter { it.extension == "class" }.forEach {
                classNames += it.path.substringAfter(srcFile.path).drop(1).replace('/', '.').substringBeforeLast(".class")
            }
        }
        return@lazy classNames
    }

}