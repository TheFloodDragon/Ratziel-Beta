package cn.fd.ratziel.common.block

import java.util.function.Supplier

/**
 * BlockScope
 *
 * @author TheFloodDragon
 * @since 2025/10/2 19:48
 */
class BlockScope {

    private constructor(parsers: ArrayDeque<Supplier<BlockParser>>) {
        this.list = parsers
    }

    constructor() : this(ArrayDeque())

    constructor(vararg parsers: Supplier<BlockParser>) : this(ArrayDeque(parsers.toList()))

    private val list: List<Supplier<BlockParser>>

    /**
     * 创建一份解释器列表.
     */
    val parsers: List<BlockParser> get() = list.map { it.get() }

    /**
     * 使用 [action] 创建一个新的 [BlockScope].
     */
    fun with(action: ArrayDeque<Supplier<BlockParser>>.() -> Unit): BlockScope {
        val copied = ArrayDeque(this.list)
        action(copied)
        return BlockScope(copied)
    }

    /**
     * 合并目标 [scope] 并返回一个新的 [BlockScope], 目标的所有解释器的优先级都将高于原语句块域的解释器.
     */
    operator fun plus(scope: BlockScope) = with {
        for (parser in scope.list) addFirst(parser)
    }

    /**
     * 添加一个新的解释器 [parser] 并返回一个包含此的新 [BlockScope], 该解释器的优先级将高于原语句块域的所有解释器.
     */
    operator fun plus(parser: Supplier<BlockParser>) = BlockScope(ArrayDeque(this.list).apply { addFirst(parser) })

    /**
     * 伴生对象, 没啥好说的
     */
    companion object

}