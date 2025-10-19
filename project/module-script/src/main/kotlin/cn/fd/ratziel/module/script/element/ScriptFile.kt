package cn.fd.ratziel.module.script.element

import cn.fd.ratziel.module.script.api.*
import cn.fd.ratziel.module.script.imports.GroupImports
import java.io.File

/**
 * ScriptFile
 *
 * @author TheFloodDragon
 * @since 2025/8/31 10:20
 */
class ScriptFile(
    /** 脚本文件 **/
    val file: File,
    /** 脚本描述 **/
    val desc: ScriptDescription,
    /** 脚本语言 **/
    val language: ScriptType,
    /** 是否立即编译 **/
    immediatelyCompile: Boolean = desc.descFile == null,
) {

    /**
     * 脚本源内容
     */
    val source = this.file.readText()

    /**
     * 脚本执行器
     */
    val executor: ScriptExecutor = this.language.executor

    /**
     * 编译后的脚本
     */
    lateinit var compiled: ScriptContent private set

    init {
        // 开启后立即编译
        if (immediatelyCompile) compile(ScriptEnvironment())
    }

    /**
     * 编译脚本
     */
    fun compile(environment: ScriptEnvironment): ScriptContent {
        // 保存编译后的脚本
        this.compiled = this.executor.build(
            ScriptSource.filed(file), environment.apply {
                // 导入组
                GroupImports.catcher(context) { it.combine(desc.imports) }
            })
        return this.compiled
    }

    override fun toString() = "ScriptFile$desc"

}