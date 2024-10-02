package cn.fd.ratziel.script.internal.applier

import cn.fd.ratziel.script.ScriptTypes
import cn.fd.ratziel.script.api.ScriptEnvironment
import taboolib.common.platform.Awake
import taboolib.common.LifeCycle
import taboolib.common.platform.ProxyCommandSender

/**
 * BasicAppliers
 *
 * 依托这设计
 *
 * @author TheFloodDragon
 * @since 2024/7/16 12:21
 */
@Deprecated("Shi")
object BasicAppliers {

    @Awake(LifeCycle.CONST)
    fun register() {
        for (lang in ScriptTypes.entries) {
            // Sender from Bindings
            lang.appliers.add(ScriptEnvironment.Applier {
                val sender = it.context.popOrNull(ProxyCommandSender::class.java)
                if (sender == null) {
                    val newSender = it.bindings["player"] ?: it.bindings["sender"]
                    if (newSender != null) it.context.add(newSender)
                }
            })
            // Sender from Context
            if (lang == ScriptTypes.KETHER) {
                lang.appliers.add(fetch<ProxyCommandSender> { bindings["@Sender"] = it })
            } else lang.appliers.add(fetch<ProxyCommandSender> { bindings["sender"] = it })
        }
    }

    inline fun <reified T : Any> fetch(noinline function: ScriptEnvironment.(T) -> Unit) = fetch(T::class.java, function)

    fun <T : Any> fetch(type: Class<T>, function: ScriptEnvironment.(T) -> Unit) =
        ScriptEnvironment.Applier {
            val ins = it.context.popOrNull(type) ?: return@Applier
            function(it, ins);
        }

}