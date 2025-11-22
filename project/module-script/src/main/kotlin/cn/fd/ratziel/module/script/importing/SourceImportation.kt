package cn.fd.ratziel.module.script.importing

import cn.fd.ratziel.module.script.api.ScriptType

/**
 * SourceImportation - 源导入件
 *
 * 如特定脚本 (Fluxon) 的包的概念的导入在此进行
 * 
 * @author TheFloodDragon
 * @since 2025/11/22 21:04
 */
interface SourceImportation : Importation {

    /**
     * 源原始内容
     */
    val content: String

    /**
     * 源归属的脚本类型
     */
    val type: ScriptType

}
