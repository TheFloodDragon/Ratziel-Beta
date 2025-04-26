package cn.fd.ratziel.module.script

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.module.script.internal.Initializable
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.function.severe
import java.util.concurrent.CopyOnWriteArrayList
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
    var defaultLanguage = ScriptType.JAVASCRIPT
        private set

    /**
     * 公共的 [ScriptEngineManager]
     */
    val engineManager by lazy { ScriptEngineManager(this::class.java.classLoader) }

    /**
     * 全局导入的类
     */
    val globalImports: MutableList<String> = CopyOnWriteArrayList()

    /**
     * 初始化脚本系统
     */
    @Awake(LifeCycle.INIT)
    private fun initialize() {
        // 读取导入的类
        val res = this::class.java.classLoader.getResource("script-default/default.imports")!!
        val lines = res.readText(Charsets.UTF_8).trim().lines().filter { it.isNotBlank() }
        globalImports.addAll(lines)
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
                val executor = type.executorOrThrow
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

    /**
     * 加载脚本语言的依赖
     */
    internal fun loadDependencies(name: String) {
        RuntimeEnv.ENV_DEPENDENCY.loadFromLocalFile(
            this::class.java.classLoader.getResource("META-INF/dependencies/$name.json")
        )
    }

}