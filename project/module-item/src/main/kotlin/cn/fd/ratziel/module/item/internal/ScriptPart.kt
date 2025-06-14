package cn.fd.ratziel.module.item.internal

import cn.fd.ratziel.module.script.ScriptType

/**
 * ScriptPart
 *
 * @author TheFloodDragon
 * @since 2025/6/14 22:25
 */
data class ScriptPart(
    /**
     * 内容 - 可能是正常的字符串, 也有可能是脚本内容
     */
    val content: String,
    /**
     * 脚本语言 - 为空时代表此片段不是脚本片段
     */
    val language: ScriptType?,
) {
    /** 判断此片段是不是脚本片段 **/
    val isScript get() = language != null
}