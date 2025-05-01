package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.block.ExecutableBlock

/**
 * MultiLineBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:28
 */
class MultiLineBlock(val blocks: List<ExecutableBlock>) : ExecutableBlock {

    private val first = blocks.first()
    private val others = blocks.drop(1)

    override fun execute(context: ArgumentContext): Any? {
        var result: Any? = first.execute(context)
        for (block in others) result = block.execute(context)
        return result
    }

}