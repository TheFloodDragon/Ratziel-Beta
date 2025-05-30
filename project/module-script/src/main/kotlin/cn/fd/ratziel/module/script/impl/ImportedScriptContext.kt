package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.ScriptManager
import javax.script.SimpleScriptContext

/**
 * ImportedScriptContext
 *
 * @author TheFloodDragon
 * @since 2025/5/31 00:28
 */
class ImportedScriptContext : SimpleScriptContext() {

    override fun getAttribute(name: String): Any? {
        return super.getAttribute(name)
            ?: ScriptManager.Global.getImportedClass(name) // 都没找到, 就尝试找导入的类
    }

}