package cn.fd.ratziel.module.script.api

import cn.fd.ratziel.module.script.imports.ImportsGroup

/**
 * Importable
 *
 * @author TheFloodDragon
 * @since 2025/8/30 21:07
 */
interface Importable {

    /**
     * 向环境内导入导入件
     */
    fun importTo(environment: ScriptEnvironment, imports: ImportsGroup)

}