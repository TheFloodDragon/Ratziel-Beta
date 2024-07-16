package cn.fd.ratziel.script.internal.applier

import cn.fd.ratziel.script.ScriptTypes
import cn.fd.ratziel.script.api.ScriptEnvironment
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender

/**
 * BasicAppliers
 *
 * @author TheFloodDragon
 * @since 2024/7/16 12:21
 */
@Awake
private object BasicAppliers {

    @Awake
    fun register() {
        ScriptTypes.KETHER.appliers.addAll(arrayOf(
            fetch<ProxyCommandSender> {
                bindings["@Sender"] = it
            }
        ))
    }

    inline fun <reified T> fetch(noinline function: ScriptEnvironment.(T) -> Unit) = fetch(T::class.java, function)

    fun <T> fetch(type: Class<T>, function: ScriptEnvironment.(T) -> Unit) =
        ScriptEnvironment.Applier {
            val ins = it.context.popOrNull(type) ?: return@Applier
            function(it, ins);
        }

}