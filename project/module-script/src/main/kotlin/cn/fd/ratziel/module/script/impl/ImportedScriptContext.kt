package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.ScriptManager
import javax.script.ScriptContext
import javax.script.SimpleScriptContext

/**
 * ImportedScriptContext
 *
 * @author TheFloodDragon
 * @since 2025/5/31 00:28
 */
open class ImportedScriptContext() : SimpleScriptContext() {

    companion object {

        /**
         * 导入的类的作用域
         */
        const val IMPORT_SCOPE = 300

    }

    constructor(scriptContext: ScriptContext) : this() {
        this.setBindings(scriptContext.getBindings(ENGINE_SCOPE), ENGINE_SCOPE)
        this.setBindings(scriptContext.getBindings(GLOBAL_SCOPE), GLOBAL_SCOPE)
        this.writer = scriptContext.writer
        this.errorWriter = scriptContext.errorWriter
        this.reader = scriptContext.reader
    }

    open fun getImport(name: String): Any? {
        return ScriptManager.Importer.getImportedClass(name)
    }

    override fun getAttribute(name: String): Any? {
        return super.getAttribute(name)
            ?: getAttribute(name, IMPORT_SCOPE) // 都没找到, 就尝试找导入的类
    }

    override fun getAttribute(name: String, scope: Int): Any? {
        return if (scope == IMPORT_SCOPE) {
            getImport(name)
        } else super.getAttribute(name, scope)
    }

    override fun getAttributesScope(name: String): Int {
        val scope = super.getAttributesScope(name)
        if (scope == -1 && // 找到导入的类, 就指向 IMPORT_SCOPE
            getImport(name) != null
        ) {
            return IMPORT_SCOPE
        }
        return scope
    }

    override fun setAttribute(name: String, value: Any?, scope: Int) {
        if (scope == IMPORT_SCOPE) throw IllegalArgumentException("Cannot set attribute in IMPORT_SCOPE")
        super.setAttribute(name, value, scope)
    }

    override fun removeAttribute(name: String, scope: Int): Any? {
        if (scope == IMPORT_SCOPE) throw IllegalArgumentException("Cannot remove attribute in IMPORT_SCOPE")
        return super.removeAttribute(name, scope)
    }

}