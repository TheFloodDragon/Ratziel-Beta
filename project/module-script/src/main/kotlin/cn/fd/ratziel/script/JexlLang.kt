package cn.fd.ratziel.script

/**
 * JexlLang
 *
 * @author TheFloodDragon
 * @since 2024/6/30 16:03
 */
object JexlLang : EnginedScriptLanguage(
    "Jexl", // 引擎名称
    "jexl", // 语言名称
    "Jexl", "Jexl3", "jexl3" // 语言别名
)