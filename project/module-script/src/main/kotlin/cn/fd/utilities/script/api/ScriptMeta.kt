package cn.fd.utilities.script.api

import java.io.File

interface ScriptMeta {

    /**
     * 脚本名称
     */
    fun name(): String

    /**
     * 脚本源文件
     */
    fun source(file: File)

    /**
     * 脚本编译后的缓存文件
     */
    fun compiled(file: File)

}