package cn.fd.ratziel.kether.bacikal

import cn.fd.ratziel.kether.bacikal.quest.*
import java.util.function.Consumer

/**
 * @author Lanscarlos
 * @since 2023-08-20 22:01
 */
object DefaultBacikalService : BacikalService {

    override val questCompiler: BacikalQuestCompiler by lazy {
        "bacikal".let { value ->
            when (value) {
                "bacikal" -> FixedQuestCompiler
                "kether" -> KetherQuestCompiler
                else -> FixedQuestCompiler
            }
        }
    }

    val questContext: String by lazy {
        "coroutines".let { value ->
            value.lowercase() ?: "kether"
        }
    }

    override fun buildQuest(name: String, func: Consumer<BacikalQuestBuilder>): BacikalQuest {
        val builder = DefaultQuestBuilder(name)
        func.accept(builder)
        return builder.build()
    }

    override fun buildSimpleQuest(name: String, func: Consumer<BacikalBlockBuilder>): BacikalQuest {
        return DefaultQuestBuilder(name).also { it.appendBlock(name, func) }.build()
    }

    override fun buildQuestContext(quest: BacikalQuest): BacikalQuestContext {
        return when (questContext) {
            "coroutines" -> CoroutinesQuestContext(quest)
            "kether" -> KetherQuestContext(quest)
            else -> throw IllegalArgumentException("Unknown context: $questContext")
        }
    }

}