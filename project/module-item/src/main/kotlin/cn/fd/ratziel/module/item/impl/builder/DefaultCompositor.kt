package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.InterpreterCompositor
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.action.ActionInterpreter
import cn.fd.ratziel.module.item.impl.builder.provided.DefinitionInterpreter
import cn.fd.ratziel.module.item.impl.builder.provided.NativeDataInterpreter
import cn.fd.ratziel.module.item.impl.builder.provided.SourceInterpreter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * DefaultCompositor
 *
 * @author TheFloodDragon
 * @since 2025/6/15 09:37
 */
class DefaultCompositor(
    val baseStream: NativeItemStream,
) : InterpreterCompositor {

    /**
     * 物品动作解释器
     */
    val actionInterpreter = ActionInterpreter.refer()

    /**
     * 默认解析器
     */
    val defaultResolver = DefaultResolver().refer()

    val definitionInterpreter = DefinitionInterpreter().refer()

    val nativeDataInterpreter = NativeDataInterpreter().refer()

    override suspend fun runTask(stream: ItemStream) = coroutineScope {

        listOf(
            launch { defaultResolver.interpret(stream) },
            launch { definitionInterpreter.interpret(stream) },
            launch { nativeDataInterpreter.interpret(stream) }
        ).joinAll()

        ItemRegistry.sources.map {
            launch {
                SourceInterpreter(it).interpret(stream)
            }
        }.joinAll()

    }

    private fun <T : ItemInterpreter> T.refer(): T {
        if (this is ItemInterpreter.PreInterpretable) {
            runBlocking { this@refer.preFlow(baseStream) }
        }
        return this
    }

}