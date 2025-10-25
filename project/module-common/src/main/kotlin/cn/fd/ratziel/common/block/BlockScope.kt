package cn.fd.ratziel.common.block

import cn.fd.ratziel.common.block.provided.*
import cn.fd.ratziel.core.Prioritized
import cn.fd.ratziel.core.util.sortPriority

/**
 * BlockScope
 *
 * @author TheFloodDragon
 * @since 2025/10/2 19:48
 */
interface BlockScope {

    /**
     * 顺序的解析器列表
     */
    val sequentialParsers: List<BlockParser>

    /**
     * 注册解析器
     */
    fun register(parser: BlockParser, priority: Byte = 0)

    companion object {

        /**
         * 作用域注册表
         */
        @JvmField
        val registry = sortedSetOf<PrioritizedScope>(compareBy { it.priority })

        /**
         * 逻辑作用域
         */
        @JvmField
        val LOGIC = newScope(
            ConditionBlock.Parser,
            WhenBlock.Parser,
            priority = -1,
        )

        /**
         * 默认作用域
         */
        @JvmField
        val DEFAULT = newScope(
            MultiLineBlock.Parser,
            ValueBlock.Parser,
            priority = 1,
        )

        /**
         * 工具作用域
         */
        @JvmField
        val UTILS = newScope(
            SampleBlockParser,
            priority = 0,
        )

        /**
         * 创建一个新的语句块作用域并注册
         */
        @JvmStatic
        fun newScope(vararg parsers: BlockParser, priority: Byte = 0): PrioritizedScope {
            val prioritiedScope = PrioritizedScope(priority, *parsers)
            this.registry.add(prioritiedScope)
            return prioritiedScope
        }

    }

    class PrioritizedScope(val priority: Byte) : BlockScope {

        constructor(priority: Byte, scope: BlockScope) : this(priority) {
            scope.sequentialParsers.forEachIndexed { i, parser -> this.register(parser, i.toByte()) }
        }

        constructor(priority: Byte, vararg parsers: BlockParser) : this(priority) {
            for (parser in parsers) register(parser)
        }

        val parsers = ArrayDeque<Prioritized<BlockParser>>()

        override val sequentialParsers get() = parsers.sortPriority()

        /**
         * 注册语句块解析器
         */
        override fun register(parser: BlockParser, priority: Byte) {
            this.parsers.addFirst(Prioritized(priority, parser))
        }

    }

}