package cn.fd.ratziel.common.block.conf

import cn.fd.ratziel.common.block.BlockConfigurationKeys
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
val BlockConfigurationKeys.scriptImporting by AttachedProperties.key<GroupImports>(GroupImports.Default)

/**
 * 脚本名称
 */
val BlockConfigurationKeys.scriptName by AttachedProperties.key<String?>(null)

/**
 * 是否尽可能缓存脚本 (默认为 true)
 */
val BlockConfigurationKeys.scriptCaching by AttachedProperties.key(true)

/**
 * 显式脚本解析 (默认为 true)
 */
val BlockConfigurationKeys.explicitScriptParsing by AttachedProperties.key(true)
