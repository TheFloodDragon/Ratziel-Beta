package cn.fd.ratziel.kether.bacikal.quest

import taboolib.module.kether.KetherScriptLoader
import taboolib.module.kether.ScriptService
import taboolib.module.kether.printKetherErrorMessage
import java.nio.charset.StandardCharsets

/**
 * @author Lanscarlos
 * @since 2023-08-20 23:20
 */
object KetherQuestCompiler : BacikalQuestCompiler {

    override fun compile(name: String, source: String, namespace: List<String>): BacikalQuest {
        return try {
            val quest = KetherScriptLoader().load(
                ScriptService,
                "bacikal_$name",
                source.toByteArray(StandardCharsets.UTF_8),
                listOf("ratziel", *namespace.toTypedArray())
            )
            DefaultQuest(name, source, quest)
        } catch (ex: Exception) {
            ex.printKetherErrorMessage(true)
            AberrantQuest(name, source, ex)
        }
    }
}