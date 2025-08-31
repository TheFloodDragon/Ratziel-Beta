package cn.fd.ratziel.module.script.element

import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * ScriptElementManager - 脚本文件管理器
 *
 * @author TheFloodDragon
 * @since 2025/8/31 10:23
 */
object ScriptElementManager {

    val scriptFiles: MutableMap<File, ScriptFile> = ConcurrentHashMap()

}