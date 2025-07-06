package cn.fd.ratziel.module.script

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.core.util.JarUtil
import cn.fd.ratziel.module.script.ScriptType.Companion.activeLanguages
import cn.fd.ratziel.module.script.impl.ScriptBootstrap
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.function.severe
import java.util.concurrent.ConcurrentHashMap
import javax.script.ScriptEngineManager


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
    var defaultLanguage: ScriptType = ScriptType.JAVASCRIPT
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
        // 初始化全局环境
        Importer.initialize()

        // 读取脚本设置
        val conf = Settings.conf.getConfigurationSection("Script")!!
        // 设置默认语言
        conf.getString("default")?.let { ScriptType.match(it) }?.also { defaultLanguage = it }
        // 初始化各个脚本类型
        val languages = conf.getConfigurationSection("languages")!!
        for (key in languages.getKeys(false)) {
            val type = ScriptType.match(key) ?: continue
            val settings = languages.getConfigurationSection(key)!!
            // 初始化脚本
            val enabled = settings.getBoolean("enabled", false)
            if (enabled && type is ScriptBootstrap) {
                try {
                    type.initialize(settings)
                    continue // 成功启用脚本, 初始化下一个
                } catch (ex: Exception) {
                    severe("Failed to enable script-language '${type.name}'!")
                    ex.printStackTrace()
                }
            }
            // 失败后禁用脚本
            activeLanguages = activeLanguages.minus(type)
        }
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
     * Importer
     *   - 默认导入的类和包 (有些脚本语言可能不支持或部分支持)
     */
    object Importer {

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

        internal fun initialize() {
            // 读取文件
            val imports = JarUtil.getEntries { it.name.startsWith("script-default/") && it.name.endsWith(".imports") }
                .flatMap { stream ->
                    // 读取导入的类
                    stream.reader().readLines().filter { it.isNotBlank() && !it.startsWith('#') }
                }
            // 初始化
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