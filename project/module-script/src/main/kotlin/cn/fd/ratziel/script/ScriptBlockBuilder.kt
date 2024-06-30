package cn.fd.ratziel.script


/**
 * ScriptBlockBuilder
 *
 * @author TheFloodDragon
 * @since 2024/6/30 16:55
 */
object ScriptBlockBuilder {

    fun build(section: Any, baseLang: ScriptLanguage, env: ScriptEnvironment): Block {
        when (section) {
            // ScriptBlock
            is String -> return ScriptBlock(RawScript(section))
            // ListBlock
            is Iterable<*> -> return ListBlock(section.mapNotNull { l -> l?.let { build(it, baseLang, env) } })
            is Map<*, *> -> {
                // ConditionBlock
                val ifValue = section["if"] ?: section["condition"]
                if (ifValue != null) {
                    val thenValue = section["then"]
                    val elseValue = section["else"]
                    return ConditionBlock(
                        build(ifValue, baseLang, env),
                        thenValue?.let { build(it, baseLang, env) },
                        elseValue?.let { build(it, baseLang, env) }
                    )
                } else {
                    // ToggleLangBlock
                    for (e in section) {
                        val key = e.key.toString().trim()
                        if (key.startsWith(MARK_TOGGLE)) {
                            val lang = ScriptRunner.findLang(key.drop(MARK_TOGGLE.length))
                            val value = e.value
                            if (value != null) return ToggleLangBlock(lang, build(value, lang, env))
                        }
                    }
                }
            }
            // PrimitiveBlock
            else -> return PrimitiveBlock(section)
        }
        return PrimitiveBlock(null)
    }

    const val MARK_TOGGLE = "\$"

    interface Block {
        fun evaluate(lang: ScriptLanguage, env: ScriptEnvironment): Any?
    }

    data class PrimitiveBlock(val value: Any?) : Block {
        override fun evaluate(lang: ScriptLanguage, env: ScriptEnvironment) = value
    }

    data class ScriptBlock(val raw: ScriptStorage) : Block {
        override fun evaluate(lang: ScriptLanguage, env: ScriptEnvironment): Any? = lang.eval(raw, env)
    }

    data class ListBlock(val list: Iterable<Block>) : Block {
        override fun evaluate(lang: ScriptLanguage, env: ScriptEnvironment): Any? {
            var result: Any? = null
            for (raw in list) {
                result = raw.evaluate(lang, env)
            }
            return result
        }
    }

    data class ToggleLangBlock(val newLang: ScriptLanguage, val block: Block) : Block {
        override fun evaluate(lang: ScriptLanguage, env: ScriptEnvironment): Any? = block.evaluate(newLang, env)
    }

    data class ConditionBlock(
        val ifBlock: Block,
        val thenBlock: Block?,
        val elseBlock: Block?
    ) : Block {
        override fun evaluate(lang: ScriptLanguage, env: ScriptEnvironment): Any? {
            return if (ifBlock.evaluate(lang, env) == true) thenBlock?.evaluate(lang, env) else elseBlock?.evaluate(lang, env)
        }
    }

}