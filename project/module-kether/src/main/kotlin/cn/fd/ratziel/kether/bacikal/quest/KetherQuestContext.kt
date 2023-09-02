package cn.fd.ratziel.kether.bacikal.quest

import taboolib.library.kether.QuestContext

/**
 * @author Lanscarlos
 * @since 2023-08-27 15:54
 */
class KetherQuestContext(quest: BacikalQuest) : AbstractQuestContext(quest) {
    override fun createRootFrame(context: InnerContext): QuestContext.Frame {
        return context.superCreateRootFrame()
    }
}