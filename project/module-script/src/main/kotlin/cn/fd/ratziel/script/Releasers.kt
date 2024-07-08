package cn.fd.ratziel.script

import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import javax.script.Bindings

/**
 * Releasers
 *
 * @author TheFloodDragon
 * @since 2024/7/8 15:32
 */
@Awake
internal object Releasers {

    @Awake
    fun init() {
        // CommandSender
        releaserFor<ProxyCommandSender>(KetherLang) {
            put("@Sender", it)
        }
        releaserFor<ProxyCommandSender>(JavaScriptLang, JexlLang) {
            put("sender", it)
        }
    }

    private inline fun <reified T> releaserFor(vararg languages: ReleasableScriptLanguage, crossinline action: Bindings.(T?) -> Unit) {
        for (lang in languages) {
            lang.addReleaser {
                it.bindings.action(it.context.popOrNull(T::class.java))
            }
        }
    }

}