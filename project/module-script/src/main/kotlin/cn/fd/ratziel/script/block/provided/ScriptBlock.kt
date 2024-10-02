package cn.fd.ratziel.script.block.provided

import cn.fd.ratziel.script.ScriptManager
import cn.fd.ratziel.script.ScriptTypes
import cn.fd.ratziel.script.api.*
import cn.fd.ratziel.script.block.BlockParser
import cn.fd.ratziel.script.block.ExecutableBlock
import cn.fd.ratziel.script.impl.SimpleScript
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * ScriptBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:31
 */
class ScriptBlock(val script: ScriptContent, val executor: ScriptExecutor) : ExecutableBlock {

    constructor(script: String, executor: ScriptExecutor) : this(SimpleScript(script, executor), executor)

    init {
        // 预编译 (SimpleScript#compile()为异步执行, 一般情况下这里也是异步的)
        if (script is StorableScript) script.compile(executor)
    }

    override fun execute(environment: ScriptEnvironment) =
        if (script is EvaluableScript) script.evaluate(environment) else executor.evaluate(script, environment)

    object Parser : BlockParser {

        const val MARK_TOGGLE = "\$"

        override fun parse(element: JsonElement): ScriptBlock? {
            return parseWith(element, matchExecutor(element))
        }

        fun parseWith(element: JsonElement, executor: ScriptExecutor): ScriptBlock? = when (element) {
            is JsonArray -> {
                val script = element.map { (it as? JsonPrimitive ?: return null).content }
                ScriptBlock(script.joinToString(""), executor)
            }

            is JsonPrimitive -> ScriptBlock(element.content, executor)
            else -> null
        }

        /**
         * 匹配执行器
         */
        fun matchExecutor(element: JsonElement): ScriptExecutor =
            (element as? JsonObject)?.firstNotNullOfOrNull {
                val key = it.key.trim()
                // 检查开头
                if (!key.startsWith(MARK_TOGGLE, ignoreCase = true)) return@firstNotNullOfOrNull null
                // 匹配类型
                val type = ScriptTypes.match(key.drop(MARK_TOGGLE.length)) ?: return@firstNotNullOfOrNull null
                // 获取脚本执行器
                type.executor
            } ?: ScriptManager.defaultLanguage.executor

    }

}