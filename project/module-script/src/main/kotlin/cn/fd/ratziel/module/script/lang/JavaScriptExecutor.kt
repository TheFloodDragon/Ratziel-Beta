package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.internal.EnginedScriptExecutor
import cn.fd.ratziel.module.script.internal.Initializable
import org.openjdk.nashorn.api.scripting.NashornScriptEngine
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import org.openjdk.nashorn.internal.objects.Global
import org.openjdk.nashorn.internal.objects.NativeJava
import org.openjdk.nashorn.internal.objects.NativeJavaImporter
import org.openjdk.nashorn.internal.runtime.NativeJavaPackage
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.reflex.ReflexClass
import javax.script.ScriptEngine

/**
 * JavaScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:05
 */
object JavaScriptExecutor : EnginedScriptExecutor(), Initializable {

    private var options: Array<String> = emptyArray()
    private var electedEngine: String? = null

    override fun newEngine(): ScriptEngine {
        val engine = if (electedEngine.equals("nashorn", true)) {
            newNashornEngine()
        } else {
            ScriptManager.engineManager.getEngineByName("js")
        } ?: throw NullPointerException("Cannot find ScriptEngine for JavaScript Language")
        return engine
    }

    override fun initialize(settings: ConfigurationSection) {
        ScriptManager.loadDependencies("nashorn")
        this.electedEngine = settings.getString("engine")
        this.options = settings.getStringList("options").toTypedArray()
    }

    private fun newNashornEngine(): ScriptEngine? {
        val factory = ScriptManager.engineManager.engineFactories.find {
            it.engineName == "OpenJDK Nashorn"
        } as? NashornScriptEngineFactory
        val engine = factory?.getScriptEngine(options, this::class.java.classLoader)
        // 导入默认
        if (engine != null) NashornUtils.importDefaults(engine)
        return engine
    }

    private object NashornUtils {

        val defaultPackages: Array<String>

        val defaultClasses: Array<String>

        init {
            val packages = ArrayList<String>()
            val classes = ArrayList<String>()
            for (import in ScriptManager.globalImports) {
                if (import.endsWith(".*") || import.endsWith(".")) {
                    packages.add(import)
                } else {
                    classes.add(import)
                }
            }
            this.defaultPackages = packages.toTypedArray()
            this.defaultClasses = classes.toTypedArray()
        }

        fun importDefaults(engine: ScriptEngine) {
            val global = getGlobal(engine)
            val packageImports = defaultPackages.map { NativeJavaPackage(it, global.objectPrototype) }
            val classImports = defaultClasses.map { NativeJava.type(it, global.objectPrototype) }
            val imports = (packageImports + classImports).toTypedArray()
            val importer = nativeImporterConstructor.instance(imports, global)
            Global.setJavaImporter(global, importer)
        }

        fun getGlobal(engine: ScriptEngine): Global {
            return globalField.get(engine as NashornScriptEngine) as Global
        }

        private val globalField by lazy {
            ReflexClass.of(NashornScriptEngine::class.java, false).getLocalField("global")
        }

        private val nativeImporterConstructor by lazy {
            ReflexClass.of(NativeJavaImporter::class.java, false).structure
                .getConstructorByType(Array::class.java, Global::class.java)
        }

    }

}