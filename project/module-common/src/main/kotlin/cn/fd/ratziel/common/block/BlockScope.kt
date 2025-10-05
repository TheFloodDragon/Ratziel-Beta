package cn.fd.ratziel.common.block

import cn.fd.ratziel.common.block.provided.ConditionBlock
import cn.fd.ratziel.common.block.provided.MultiLineBlock
import cn.fd.ratziel.common.block.provided.SampleBlockParser
import cn.fd.ratziel.common.block.provided.ValueBlock

/**
 * BlockScope
 *
 * @author TheFloodDragon
 * @since 2025/10/2 19:48
 */
class BlockScope(val parsers: ArrayDeque<BlockParser>) {

    constructor(vararg parsers: BlockParser) : this(ArrayDeque(parsers.toList()))

    /**
     * 注册语句块解析器
     */
    fun register(parser: BlockParser) = this.parsers.addFirst(parser)

    companion object {

        @JvmField
        val DefaultScope =
            BlockScope(
                ConditionBlock.Parser,
                MultiLineBlock.Parser,
                SampleBlockParser,
                ValueBlock.Parser,
            )

    }

}