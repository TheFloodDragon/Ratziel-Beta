package cn.fd.ratziel.module.script

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.element.ElementLoader
import cn.fd.ratziel.core.util.JarUtil
import cn.fd.ratziel.module.script.ScriptType.Companion.activeLanguages
import cn.fd.ratziel.module.script.element.ScriptElementLoader
import cn.fd.ratziel.module.script.impl.ScriptBootstrap
import cn.fd.ratziel.module.script.imports.GroupImports
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.function.severe
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
     * 全局导入组
     */
    val globalGroup: GroupImports by lazy {
        // 读取文件
        val imports = JarUtil.getEntries { it.name.startsWith("script-default/") && it.name.endsWith(".imports") }.flatMap { it.reader().readLines() }
        val parsed = GroupImports.parse(imports)
        // 初始化
        GroupImports(parsed.first, parsed.second)
    }

    /**
     * 初始化脚本系统
     */
    @Awake(LifeCycle.INIT)
    private fun initialize() {
        // 注册下 脚本元素加载器
        ElementLoader.loaders.addFirst(ScriptElementLoader)

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

}