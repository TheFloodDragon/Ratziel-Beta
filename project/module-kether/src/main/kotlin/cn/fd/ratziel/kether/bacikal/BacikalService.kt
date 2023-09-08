package cn.fd.ratziel.kether.bacikal

import cn.fd.ratziel.kether.bacikal.quest.*
import java.util.function.Consumer

/**
 * @author Lanscarlos
 * @since 2023-08-20 21:30
 */
interface BacikalService {

    val questCompiler: BacikalQuestCompiler

    fun buildQuest(name: String, func: Consumer<BacikalQuestBuilder>): BacikalQuest

    fun buildSimpleQuest(name: String, func: Consumer<BacikalBlockBuilder>): BacikalQuest

    /**
     * 创建任务运行时上下文
     * */
    fun buildQuestContext(quest: BacikalQuest): BacikalQuestContext

}