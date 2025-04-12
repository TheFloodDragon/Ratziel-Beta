package cn.fd.ratziel.module.script.internal.engine

import javax.script.ScriptEngineFactory

/**
 * EngineProvider
 *
 * @author TheFloodDragon
 * @since 2025/4/13 00:46
 */
interface EngineProvider {

    /**
     * 获取 [ScriptEngineFactory]
     */
    fun getFactory(): ScriptEngineFactory?

}