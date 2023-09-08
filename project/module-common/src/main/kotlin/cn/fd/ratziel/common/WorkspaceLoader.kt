package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.element.DefaultElementLoader
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.util.handle
import cn.fd.ratziel.core.util.future
import cn.fd.ratziel.core.util.runFuture
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.system.measureTimeMillis
import cn.fd.ratziel.common.WorkspaceManager as wsm

object WorkspaceLoader {

    // 已加载的元素
    val elements = ConcurrentLinkedDeque<Element>()

    /**
     * 初始化工作空间
     */
    fun init(sender: ProxyCommandSender) {
        measureTimeMillis {
            Settings.WorkspacePaths.forEach { path ->
                wsm.initializeWorkspace(path, true)
            }
        }.let {
            sender.sendLang("Workspace-Inited", wsm.workspaces.size, it)
        }
    }

    /**
     * 加载命名空间 (在插件加载时)
     */
    fun load(sender: ProxyCommandSender) {
        /**
         * 加载元素文件
         */
        val loading = ConcurrentLinkedDeque<CompletableFuture<List<Element>>>() // 加载过程中的CompletableFuture
        measureTimeMillis {
            val fileMather = Settings.fileFilter.toRegex()
            wsm.getAllFiles()
                .filter { // 匹配文件
                    it.name.matches(fileMather)
                }
                .forEach { file ->
                    // 加载元素文件
                    loading += future {
                        DefaultElementLoader.load(file).onEach {
                            elements.add(it) // 插入缓存
                            it.handle()  // 处理元素
                        }
                    }
                }
            // 等待所有任务完成
            CompletableFuture.allOf(*loading.toTypedArray()).join()
        }.let {
            sender.sendLang("Workspace-Finished", elements.size, it)
        }
    }

    /**
     * 重新加载命名空间
     */
    fun reload(sender: ProxyCommandSender) {
        wsm.workspaces.clear()
        elements.clear()
        // 初始化工作空间
        init(sender)
        // 加载元素文件
        load(sender)
    }

    /**
     * 在插件加载时注册并加载命名空间
     */
    @Awake(LifeCycle.LOAD)
    private fun run() {
        runFuture {
            init(console())
            load(console())
        }
    }

}