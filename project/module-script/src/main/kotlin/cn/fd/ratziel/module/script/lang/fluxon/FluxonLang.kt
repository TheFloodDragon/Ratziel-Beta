package cn.fd.ratziel.module.script.lang.fluxon

import cn.fd.ratziel.module.script.api.ScriptPreference
import cn.fd.ratziel.module.script.api.ScriptType

/**
 * FluxonLang
 *
 * @author TheFloodDragon
 * @since 2025/11/1 22:04
 */
object FluxonLang : ScriptType {

    override val name = "Fluxon"
    override val languageId = "fs"
    override val alias = arrayOf("fluxon", "fs")
    override val preference = ScriptPreference.INTERPRETATION_PREFERRED

    override val executor get() = FluxonScriptExecutor.DEFAULT

}