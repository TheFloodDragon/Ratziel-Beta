package cn.fd.ratziel.module.script.internal.engine

import cn.fd.ratziel.module.script.ScriptManager
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory

/**
 * NashornEngineProvider
 *
 * @author TheFloodDragon
 * @since 2025/4/13 00:38
 */
object NashornEngineProvider : EngineProvider {

    override fun getFactory(): NashornScriptEngineFactory? =
        ScriptManager.engineManager.engineFactories.find {
            it.engineName == "OpenJDK Nashorn"
        } as? NashornScriptEngineFactory

}