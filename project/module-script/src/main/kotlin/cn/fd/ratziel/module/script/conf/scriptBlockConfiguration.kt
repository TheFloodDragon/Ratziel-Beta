package cn.fd.ratziel.module.script.conf

import cn.fd.ratziel.core.contextual.AttachedProperties
import cn.fd.ratziel.module.script.importing.GroupImports

/**
 * scriptBlockConfiguration
 *
 * @author TheFloodDragon
 * @since 2025/11/14 18:24
 */

/**
 * 脚本导入
 */
val ScriptConfigurationKeys.scriptImporting by AttachedProperties.key<GroupImports>(GroupImports.Default)

/**
 * 脚本名称
 */
val ScriptConfigurationKeys.scriptName by AttachedProperties.key<String?>(null)

/**
 * 脚本缓存等级: (默认为1)
 *   < 0 : 不缓存
 *   >= 1 : 缓存 (缓存的模式视脚本语言而定)
 *
 * Fluxon:
 *   1 - AST 缓存
 *   2 - 字节码缓存
 */
val ScriptConfigurationKeys.scriptCaching by AttachedProperties.key(1)

/**
 * 显式脚本解析 (默认为 true)
 */
val ScriptConfigurationKeys.explicitScriptParsing by AttachedProperties.key(true)
