package cn.fd.ratziel.script.block

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.script.block.provided.ConditionBlock
import cn.fd.ratziel.script.block.provided.MultiLineBlock
import cn.fd.ratziel.script.block.provided.PrimitiveBlock
import cn.fd.ratziel.script.block.provided.ScriptBlock
import kotlinx.serialization.json.JsonElement
import java.util.*

/**
 * BlockManager
 *
 * @author TheFloodDragon
 * @since 2024/10/2 17:49
 */
object BlockManager : BlockParser {

    /**
     * 脚本解析器注册表
     */
    val registry: MutableSet<Priority<BlockParser>> = TreeSet(compareBy { it.priority })

    /**
     * 解析脚本块
     * 无法解析时抛出异常
     */
    override fun parse(element: JsonElement): ExecutableBlock {
        for (parser in registry) {
            val result = parser.value.parse(element)
            if (result != null) return result
        }
        throw NullPointerException("Cannot parse the element to block. Source: $element")
    }

    fun parse(element: JsonElement?): ExecutableBlock? = element?.let { parse(it) }

    /**
     * 注册脚本解析器
     */
    fun register(parser: BlockParser, priority: Byte) {
        registry.add(Priority(priority, parser))
    }

    /**
     * 注册脚本解析器
     */
    fun register(parser: BlockParser) = register(parser, 0)

    init {
        // 注册
        register(ScriptBlock.Parser, 10)
        register(ConditionBlock.Parser, 20)
        register(MultiLineBlock.Parser, 50)
        register(PrimitiveBlock.Parser, 126)
    }

}