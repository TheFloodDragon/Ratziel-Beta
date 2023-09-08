package cn.fd.ratziel.kether.bacikal

import cn.fd.ratziel.kether.bacikal.parser.BacikalContext
import cn.fd.ratziel.kether.bacikal.parser.BacikalFruit
import cn.fd.ratziel.kether.bacikal.parser.DefaultContext
import cn.fd.ratziel.kether.bacikal.quest.BacikalBlockBuilder
import cn.fd.ratziel.kether.bacikal.quest.BacikalQuest
import cn.fd.ratziel.kether.bacikal.quest.BacikalQuestBuilder
import cn.fd.ratziel.kether.bacikal.quest.DefaultQuestBuilder
import taboolib.module.kether.ScriptActionParser

/**
 * @author Lanscarlos
 * @since 2023-08-21 10:29
 */

/**
 * 语句处理
 * */
fun <T> bacikalParser(func: BacikalContext.() -> BacikalFruit<T>): ScriptActionParser<T> {
    return ScriptActionParser {
        val context = DefaultContext(this)
        func(context)
    }
}

fun bacikalQuest(name: String, func: BacikalQuestBuilder.() -> Unit): BacikalQuest {
    return DefaultQuestBuilder(name).also(func).build()
}

fun bacikalSimpleQuest(name: String, func: BacikalBlockBuilder.() -> Unit): BacikalQuest {
    return DefaultQuestBuilder(name).also { it.appendBlock(name, func) }.build()
}

fun String.toBacikalQuest(name: String): BacikalQuest {
    return DefaultQuestBuilder(name).also {
        it.appendBlock(name) {
            appendContent(this@toBacikalQuest)
        }
    }.build()
}