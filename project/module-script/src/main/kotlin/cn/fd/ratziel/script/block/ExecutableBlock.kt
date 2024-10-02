package cn.fd.ratziel.script.block

import cn.fd.ratziel.script.api.ScriptEnvironment

/**
 * ExecutableBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 17:39
 */
interface ExecutableBlock {

    /**
     * 执行语句块
     * @return 语句返回值
     */
    fun execute(environment: ScriptEnvironment): Any?

}