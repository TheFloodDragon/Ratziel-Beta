package cn.fd.ratziel.kether.bacikal

import taboolib.module.kether.ScriptActionParser
import cn.fd.ratziel.kether.bacikal.parser.*
import cn.fd.ratziel.kether.bacikal.quest.*

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