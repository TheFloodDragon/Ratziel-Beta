package cn.fd.ratziel.common.block

import cn.fd.ratziel.core.contextual.ArgumentContext

/**
 * ExecutableBlock - 可执行语句块
 *
 * @author TheFloodDragon
 * @since 2024/10/2 17:39
 */
interface ExecutableBlock {

    /**
     * 执行语句块
     * @param context 执行上下文
     * @return 语句返回值
     */
    fun execute(context: ArgumentContext): Any?

}