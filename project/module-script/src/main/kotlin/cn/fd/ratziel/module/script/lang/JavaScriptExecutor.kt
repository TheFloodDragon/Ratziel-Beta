package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.internal.Initializable
import cn.fd.ratziel.module.script.internal.NonStrictCompilation
import taboolib.library.configuration.ConfigurationSection
import java.util.function.Supplier

/**
 * JavaScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 9:37
 */
class JavaScriptExecutor : ScriptExecutor by executor.get(), Initializable, NonStrictCompilation {

    companion object {
        /** 脚本执行器实例 **/
        private lateinit var executor: Supplier<ScriptExecutor>
    }

    override fun initialize(settings: ConfigurationSection) {
        // 读取引擎
        val selected = settings.getString("engine")
        // 设置全局使用的脚本执行器
        executor = when (selected?.lowercase()) {
            "nashorn" -> {
                ScriptManager.loadDependencies("nashorn") // 加载 Nashorn JavaScript 依赖
                Supplier { NashornScriptExecutor() }
            }

            "graaljs" -> {
                ScriptManager.loadDependencies("graaljs") // 加载 GraalVM JavaScript 依赖
                Supplier { GraalJsScriptExecutor() }
            }

            else -> throw IllegalArgumentException("Unknown engine '$selected' selected.")
        }
    }

}