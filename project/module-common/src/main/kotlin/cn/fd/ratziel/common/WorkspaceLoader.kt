package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.element.DefaultElementEvaluator
import cn.fd.ratziel.common.element.DefaultElementLoader
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.FutureFactory
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import kotlin.time.measureTime

object WorkspaceLoader {

    /**
     * 缓存的元素
     */
    val cachedElements = ConcurrentLinkedQueue<Element>()

    /**
     * 线程池
     */
    val executor by lazy {
        Executors.newFixedThreadPool(10)
    }

    /**
     * 初始化工作空间
     */
    fun init(sender: ProxyCommandSender) = measureTime {
        WorkspaceManager.workspaces.clear() // 清空工作空间
        Settings.workspacePaths.forEach { path ->
            WorkspaceManager.initializeWorkspace(path, true)
        }
    }.also { sender.sendLang("Workspace-Inited", WorkspaceManager.workspaces.size, it.inWholeMilliseconds) }

    /**
     * 加载工作空间中的元素
     */
    fun load(sender: ProxyCommandSender) : CompletableFuture<Unit> {
        val result = CompletableFuture<Unit>()
        cachedElements.clear() // 清空缓存
        WorkspaceLoadEvent.Start().call() // 开始加载事件
        val evaluator = DefaultElementEvaluator(executor)
        // 创建异步工厂
        FutureFactory {
            WorkspaceManager.getFilteredFiles()
                .forEach { file ->
                    // 加载元素文件
                    submitAsync(executor) {
                        DefaultElementLoader.load(file).onEach {
                            evaluator.submitElement(it)
                            cachedElements += it
                        }
                    }
                }
        }.thenRun {
            val future = evaluator.evaluate() // 开始评估
            future.thenAccept { time->
                result.complete(Unit)
                sender.sendLang("Workspace-Finished", cachedElements.size, time.toMillis())
                WorkspaceLoadEvent.End().call() // 结束加载事件
            }
        }
        return result
    }

    /**
     * 重新加载工作空间
     */
    fun reload(sender: ProxyCommandSender) {
        // 初始化工作空间
        init(sender)
        // 加载元素文件
        load(sender).join()
    }


    @Awake(LifeCycle.LOAD)
    private fun load() {
        init(console())
        load(console())
    }

}