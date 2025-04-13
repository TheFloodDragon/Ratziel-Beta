package cn.fd.ratziel.module.script.internal.engine

import cn.fd.ratziel.module.script.ScriptManager
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import taboolib.common.platform.Ghost

/**
 * NashornEngineProvider
 *
 * @author TheFloodDragon
 * @since 2025/4/13 00:38
 */
@Ghost
object NashornEngineProvider : EngineProvider {

    override fun getFactory(): NashornScriptEngineFactory? =
        ScriptManager.engineManager.engineFactories.find {
            it.engineName == "OpenJDK Nashorn"
        } as? NashornScriptEngineFactory

}