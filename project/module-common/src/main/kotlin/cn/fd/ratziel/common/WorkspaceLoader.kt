package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.element.DefaultElementLoader
import cn.fd.ratziel.common.element.evaluator.ApexElementEvaluator
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.FutureFactory
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.measureTime

object WorkspaceLoader {

    /**
     * 缓存的元素
     */
    val cachedElements = ConcurrentLinkedQueue<Element>()

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
    fun load(sender: ProxyCommandSender) = measureTime {
        cachedElements.clear() // 清空缓存
        ApexElementEvaluator.evalTasks.clear() // 清空评估任务
        WorkspaceLoadEvent.Start().call() // 开始加载事件
        // 创建异步工厂
        FutureFactory {
            WorkspaceManager.getFilteredFiles()
                .forEach { file ->
                    // 加载元素文件
                    supplyAsync(ApexElementEvaluator.executor) {
                        DefaultElementLoader.load(file).forEach {
                            cachedElements += it  // 插入缓存
                            ApexElementEvaluator.handleElement(it) // 处理元素
                        }
                    }.submit()
                }
        }.waitAll() // 等待所有加载任务完成
        WorkspaceLoadEvent.End().call() // 结束加载事件
    }.let { time ->
        ApexElementEvaluator.evalTasks.whenComplete { durations ->
            durations.forEach { time.plus(it) } // 合并时间
            sender.sendLang("Workspace-Finished", cachedElements.size, time.inWholeMilliseconds)
        }
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