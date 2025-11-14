package cn.fd.ratziel.module.script

import cn.fd.ratziel.common.block.BlockScope
import cn.fd.ratziel.common.block.provided.ScriptBlock
import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.element.ElementLoader
import cn.fd.ratziel.core.util.FileResolver
import cn.fd.ratziel.core.util.JarUtil
import cn.fd.ratziel.module.script.api.ScriptType
import cn.fd.ratziel.module.script.element.ScriptElementLoader
import cn.fd.ratziel.module.script.impl.ScriptBootstrap
import cn.fd.ratziel.module.script.importing.GroupImports
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import java.io.File
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
    var defaultLanguage: ScriptType = ScriptService.JAVASCRIPT
        private set

    /**
     * 公共的 [ScriptEngineManager]
     */
    val engineManager by lazy { ScriptEngineManager(this::class.java.classLoader) }

    /**
     * 全局导入组
     */
    val globalGroup: GroupImports by lazy {
        // 读取文件
        val imports = JarUtil.getEntries { it.name.startsWith("internal/script-imports") && it.name.endsWith(".imports") }.flatMap { it.reader().readLines() }
        GroupImports.parse(imports)
    }

    /**
     * 内置的脚本库文件解析器
     */
    val builtinScriptsResolver: FileResolver = object : FileResolver {
        override fun resolve(path: String, baseFile: File?) =
            if (path.startsWith("builtin:")) {
                this::class.java.classLoader.getResource(path.drop("builtin:".length))
                    ?.let { File(it.toURI()) }
            } else null
    }

    /**
     * 初始化脚本系统
     */
    @Awake(LifeCycle.INIT)
    private fun initialize() {
        // 注册下 脚本元素加载器
        ElementLoader.loaders.addFirst(ScriptElementLoader)
        // 你也注册下
        BlockScope.DEFAULT.register(ScriptBlock.Parser)

        // 读取脚本设置
        val conf = Settings.conf.getConfigurationSection("Script")!!
        // 初始化各个脚本类型
        val languages = conf.getConfigurationSection("languages")!!
        for (key in languages.getKeys(false)) {
            val type = ScriptType.match(key, false) ?: continue
            val settings = languages.getConfigurationSection(key)!!
            // 初始化脚本
            val enabled = settings.getBoolean("enabled", false)
            if (!enabled) continue // 配置里禁用的直接跳过
            // 尝试启动有启动器的脚本
            if (type is ScriptBootstrap) {
                try {
                    type.initialize(settings)
                } catch (ex: Exception) {
                    severe("Failed to enable script-language '${type.name}'!", ex.stackTraceToString())
                    continue // 跳过不启用
                }
            }
            // 启用脚本
            ScriptService.enableLanguage(type)
        }

        // 脚本全禁用了直接爬
        if (ScriptService.enabledLanguages.isEmpty()) {
            warning("No script language is enabled! All scripts will not work!")
            return
        }

        // 设置默认语言
        this.defaultLanguage = conf.getString("default")?.let { ScriptType.match(it) }
            ?: ScriptService.enabledLanguages.first().also {
                warning("Default script language is not set or invalid! Using ${it.name} instead.")
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

}