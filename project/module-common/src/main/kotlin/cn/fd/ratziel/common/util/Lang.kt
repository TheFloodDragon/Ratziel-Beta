package cn.fd.ratziel.common.util

import taboolib.module.lang.LanguageFile
import taboolib.module.lang.Type as LangType

/**
 * 获取语言类型
 */
fun LanguageFile.getType(node: String): LangType? = nodes[node]