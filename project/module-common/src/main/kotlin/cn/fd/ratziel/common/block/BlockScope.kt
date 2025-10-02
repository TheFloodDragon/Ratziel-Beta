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
        this.array = parsers
    }

    constructor() : this(ArrayDeque())

    constructor(vararg parsers: Supplier<BlockParser>) : this(ArrayDeque(parsers.toList()))

    private val array: ArrayDeque<Supplier<BlockParser>>

    val parsers: List<BlockParser> get() = array.map { it.get() }

    fun with(action: ArrayDeque<Supplier<BlockParser>>.() -> Unit): BlockScope {
        val copied = ArrayDeque(this.array)
        action(copied)
        return BlockScope(copied)
    }

    operator fun plus(scope: BlockScope) = with {
        for (parser in scope.array) addFirst(parser)
    }

    operator fun plus(parser: Supplier<BlockParser>) = BlockScope(ArrayDeque(this.array).apply { addFirst(parser) })

    companion object

}