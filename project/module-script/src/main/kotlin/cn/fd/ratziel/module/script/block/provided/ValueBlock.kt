package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.block.ExecutableBlock

/**
 * ValueBlock
 *
 * @author TheFloodDragon
 * @since 2025/4/5 12:00
 */
class ValueBlock(val value: Any?) : ExecutableBlock {

    override fun execute(context: ArgumentContext) = value

}