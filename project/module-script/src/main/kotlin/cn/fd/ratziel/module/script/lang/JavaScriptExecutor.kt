package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.internal.Initializable
import taboolib.library.configuration.ConfigurationSection
import javax.script.ScriptEngine

/**
 * JavaScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2025/4/26 9:37
 */
object JavaScriptExecutor : ScriptExecutor, Initializable {

    private lateinit var engine: ScriptExecutor

    override fun initialize(settings: ConfigurationSection) {
        // 读取引擎
        val selected = settings.getString("engine")
        // 创建脚本执行器
        val engine = when (selected?.lowercase()) {
            "nashorn" -> NashornScriptExecutor
            "graaljs" -> GraalJsScriptExecutor
            else -> NashornScriptExecutor
        }
        this.engine = engine
    }

    override fun build(script: String) = engine.build(script)

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment) = engine.evaluate(script, environment)

    internal val defaultPackages: Array<String>

    internal val defaultClasses: Array<String>

    init {
        val packages = ArrayList<String>()
        val classes = ArrayList<String>()
        for (import in ScriptManager.globalImports) {
            if (import.endsWith("*") || import.endsWith(".")) {
                packages.add(import.substringBeforeLast('.'))
            } else {
                classes.add(import)
            }
        }
        this.defaultPackages = packages.toTypedArray()
        this.defaultClasses = classes.toTypedArray()
    }

    internal fun importDefaults(engine: ScriptEngine) {
        engine.eval(buildString {
            appendLine("// Importing Start")
            appendLine("load('nashorn:mozilla_compat.js');")
            for (pkg in defaultPackages) {
                appendLine("importPackage($pkg);")
            }
            for (cls in defaultClasses) {
                appendLine("importClass($cls);")
            }
            appendLine("// Importing Ended")
        })
    }

}