package cn.fd.ratziel.module.script

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.core.util.findInJar
import cn.fd.ratziel.module.script.internal.Initializable
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getJarFile
import taboolib.common.platform.function.severe
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile
import javax.script.Bindings
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings


/**
 * ScriptManager
 *
 * @author TheFloodDragon
 * @since 2024/7/16 11:30
 */
object ScriptManager {

    /**
     * 默认使用的的脚本语言
     */
    var defaultLanguage = ScriptType.JAVASCRIPT
        private set

    /**
     * 公共的 [ScriptEngineManager]
     */
    val engineManager by lazy { ScriptEngineManager(this::class.java.classLoader) }

    /**
     * 初始化脚本系统
     */
    @Awake(LifeCycle.INIT)
    private fun initialize() {
        // 读取导入的类
        Global.initialize(readDefaultImports())

        // 读取脚本设置
        val conf = Settings.conf.getConfigurationSection("Script")!!
        // 设置默认语言
        conf.getString("default")?.let { ScriptType.match(it) }?.also { defaultLanguage = it }
        // 初始化各个脚本类型
        val languages = conf.getConfigurationSection("languages")!!
        for (key in languages.getKeys(false)) {
            val type = ScriptType.match(key) ?: continue
            val settings = languages.getConfigurationSection(key)!!
            type.enabled = settings.getBoolean("enabled", false)
            if (!type.enabled) continue // 禁用的直接跳过
            try {
                // 获取执行器
                val executor = type.newExecutor()
                // 调用初始化函数
                if (executor is Initializable) {
                    executor.initialize(settings)
                }
            } catch (ex: Exception) {
                type.enabled = false // 禁用
                severe("Failed to enable script-language '${type.name}'!")
                ex.printStackTrace()
            }
        }
    }

    private fun readDefaultImports(): List<String> {
        val imports = ArrayList<String>()

        val jar = JarFile(getJarFile())
        findInJar(jar) {
            !it.isDirectory && it.name.startsWith("script-default/") && it.name.endsWith(".imports")
        }.forEach { jarEntry ->
            val reader = jar.getInputStream(jarEntry).reader(Charsets.UTF_8)
            // 读取导入的类
            val lines = reader.readText().trim().lines().filter { it.isNotBlank() && !it.startsWith('#') }
            imports.addAll(lines)
        }

        return imports
    }

    /**
     * 加载脚本语言的依赖
     */
    internal fun loadDependencies(name: String) {
        RuntimeEnv.ENV_DEPENDENCY.loadFromLocalFile(
            this::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
        )
    }

    /**
     * Global
     *   - 默认导入的类和包 (有些脚本语言可能不支持或部分支持)
     *   - 提供全局的 [Bindings] 对象
     */
    object Global {

        /**
         * 全局的 [Bindings] 对象, 提供全局变量支持
         */
        val globalBindings: Bindings = SimpleBindings(ConcurrentHashMap())

        /**
         * 导入包
         */
        var packages: List<PackageImport> = emptyList()
            private set

        /**
         * 导入类
         */
        var classes: List<ClassImport> = emptyList()
            private set

        /**
         * 通过简单类名称获取已经导入的类
         * @param name 类的简单名称
         * @return [Class], 找不到则返回空
         */
        @JvmStatic
        fun getImportedClass(name: String): Class<*>? {
            // 在导入的类中查找
            val find = classes.find { it.simpleName == name }
            if (find != null) return find.clazz
            // 在导入的包中查找
            for (import in packages) {
                val searched = import.search(name)
                if (searched != null) return searched
            }
            return null
        }

        /**
         * 导入的类
         */
        class ClassImport(
            val simpleName: String,
            val fullName: String,
        ) {
            constructor(name: String) : this(name.substringAfterLast('.'), name)

            val clazz by lazy {
                try {
                    Class.forName(fullName, false, this::class.java.classLoader)
                } catch (_: ClassNotFoundException) {
                    null
                }
            }
        }

        /**
         * 导入的包
         */
        class PackageImport(
            val pkgName: String,
        ) {

            private val classesCache = ConcurrentHashMap<String, Class<*>>()

            fun search(name: String): Class<*>? {
                val cached = classesCache[name]
                if (cached != null) return cached
                val find = try {
                    Class.forName("$pkgName.$name", false, this::class.java.classLoader)
                } catch (_: ClassNotFoundException) {
                    null
                }
                if (find != null) {
                    classesCache[name] = find
                    return find
                }
                return null
            }
        }

        internal fun initialize(imports: List<String>) {
            val packages = ArrayList<PackageImport>()
            val classes = ArrayList<ClassImport>()
            for (import in imports) {
                if (import.endsWith('*') || import.endsWith('.')) {
                    packages.add(PackageImport(import.substringBeforeLast('.')))
                } else {
                    classes.add(ClassImport(import))
                }
            }
            this.packages = packages
            this.classes = classes
        }

    }

}