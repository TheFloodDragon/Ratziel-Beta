package cn.fd.ratziel.module.script.block

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.function.block.ExecutableBlock
import cn.fd.ratziel.module.script.block.provided.ConditionBlockParser
import cn.fd.ratziel.module.script.block.provided.MultiLineBlock
import cn.fd.ratziel.module.script.block.provided.PrimitiveBlock
import cn.fd.ratziel.module.script.block.provided.ScriptBlock
import kotlinx.serialization.json.JsonElement

/**
 * GlobalBlockBuilder
 *
 * @author TheFloodDragon
 * @since 2024/10/2 17:49
 */
object GlobalBlockBuilder : BlockParser {

    /**
     * 语句块解析器注册表
     */
    val registry: MutableList<Priority<BlockParser>> = ArrayList()

    /**
     * 构建语句块
     * 无法解析时抛出异常
     */
    override fun parse(element: JsonElement): ExecutableBlock {
        for (p in registry.sortedBy { it.priority }) {
            // 解析器
            val parser = p.value
            // 开始解析
            val result = if (parser is RecursingBlockParser)
                parser.parse(element, this)
            else parser.parse(element)
            // 返回存在的结果
            if (result != null) return result
        }
        throw NullPointerException("Cannot parse the element to block. Source: $element")
    }

    @JvmName("parse1")
    fun parse(element: JsonElement?): ExecutableBlock? = element?.let { parse(it) }

    /**
     * 注册语句块解析器
     */
    fun register(parser: BlockParser, priority: Byte) {
        registry.add(Priority(priority, parser))
    }

    /**
     * 注册语句块解析器
     */
    fun register(parser: BlockParser) = register(parser, 0)

    init {
        // 注册
        register(ScriptBlock.Parser, 10)
        register(ConditionBlockParser, 20)
        register(MultiLineBlock.Parser, 50)
        register(PrimitiveBlock.Parser, 126)
    }

}