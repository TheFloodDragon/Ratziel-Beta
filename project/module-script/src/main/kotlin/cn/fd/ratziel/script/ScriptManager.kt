package cn.fd.ratziel.script

import cn.fd.ratziel.script.internal.Initializable
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake


/**
 * ScriptManager
 *
 * @author TheFloodDragon
 * @since 2024/7/16 11:30
 */
object ScriptManager {

    /**
     * 默认使用的的脚本语言
     */
    var default = ScriptType.JAVASCRIPT
        private set

    /**
     * 初始化脚本系统
     */
    @Awake(LifeCycle.CONST)
    fun initialize() {
        ScriptType.registry.forEach { type ->
            type.enabled = true // TODO 配置文件读取, 目前先默认启用下
            if (!type.enabled) return@forEach // 禁用的直接跳过
            try {
                // 获取执行器
                val executor = type.executorOrThrow
                // 调用初始化函数
                (executor as? Initializable)?.initialize()
                // TODO 日志
            } catch (ex: Exception) {
                type.enabled = false // 禁用
                ex.printStackTrace() // TODO 日志
            }
        }
    }

}