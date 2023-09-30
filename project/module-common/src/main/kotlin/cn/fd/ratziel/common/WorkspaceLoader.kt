package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.element.DefaultElementLoader
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.util.handle
import cn.fd.ratziel.core.util.future
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
            Settings.workspacePaths.forEach { path ->
                wsm.initializeWorkspace(path, true)
            }
        }.let {
            sender.sendLang("Workspace-Inited", wsm.workspaces.size, it)
        }
    }

    /**
     * 加载工作空间中的元素
     */
    fun load(sender: ProxyCommandSender) {
        /**
         * 加载元素文件
         */
        val loading = ConcurrentLinkedDeque<CompletableFuture<List<Element>>>()
        val handling = ConcurrentLinkedDeque<CompletableFuture<Unit>>()
        measureTimeMillis {
            wsm.gerFilteredFiles()
                .forEach { file ->
                    // 加载元素文件
                    loading += future {
                        DefaultElementLoader.load(file).onEach { em ->
                            elements.add(em) // 插入缓存
                            // 处理元素
                            handling += future {
                                em.handle()
                            }
                        }
                    }
                }
            // 等待所有任务完成
            CompletableFuture.allOf(*loading.toTypedArray()).join()
            CompletableFuture.allOf(*handling.toTypedArray()).join()
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
        init(console())
        load(console())
    }

}