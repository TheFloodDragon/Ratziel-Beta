package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.element.DefaultElementLoader
import cn.fd.ratziel.common.element.evaluator.ApexElementEvaluator
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.FutureFactory
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime
import cn.fd.ratziel.common.WorkspaceManager as wsm

object WorkspaceLoader {

    /**
     * 已加载的元素
     */
    val elements = ConcurrentLinkedDeque<Element>()

    /**
     * 初始化工作空间
     */
    fun init(sender: ProxyCommandSender) =
        measureTimeMillis {
            Settings.workspacePaths.forEach { path ->
                wsm.initializeWorkspace(path, true)
            }
        }.also {
            sender.sendLang("Workspace-Inited", wsm.workspaces.size, it)
        }

    /**
     * 加载工作空间中的元素
     */
    fun load(sender: ProxyCommandSender) = measureTime {
        WorkspaceLoadEvent.Start().call()
        // 创建异步工厂
        FutureFactory<List<Element>>().also { loading ->
            /**
             * 加载元素文件
             */
            wsm.getFilteredFiles()
                .forEach { file ->
                    // 加载元素文件
                    loading.newAsync {
                        DefaultElementLoader.load(file).onEach {
                            elements += it  // 插入缓存
                            ApexElementEvaluator.handleElement(it) // 处理元素
                        }
                    }
                }
        }.wait() // 等待所有加载任务完成
        WorkspaceLoadEvent.End().call()
    }.let { time ->
        ApexElementEvaluator.evalTasks.whenFinished { durations ->
            durations.forEach { time.plus(it) } // 合并时间
            sender.sendLang("Workspace-Finished", elements.size, time.inWholeMilliseconds)
        }
    }

    /**
     * 重新加载工作空间
     */
    fun reload(sender: ProxyCommandSender) {
        ApexElementEvaluator.evalTasks.clear() // 清空评估任务
        elements.clear() // 清空缓存
        wsm.workspaces.clear() // 清空工作空间
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