package cn.fd.ratziel.kether.bacikal.quest

import cn.fd.ratziel.kether.bacikal.Bacikal
import taboolib.library.kether.Quest
import java.util.concurrent.CompletableFuture

/**
 * @author Lanscarlos
 * @since 2023-08-20 22:32
 */
class DefaultQuest(override val name: String, override val content: String, override val source: Quest) : BacikalQuest {

    override fun runActions(context: BacikalQuestContext.() -> Unit): CompletableFuture<Any?> {
        return Bacikal.service.buildQuestContext(this).apply(context).runActions()
    }
}