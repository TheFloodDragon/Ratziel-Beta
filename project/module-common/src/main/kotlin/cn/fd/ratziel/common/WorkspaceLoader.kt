package cn.fd.ratziel.common

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.element.DefaultElementLoader
import cn.fd.ratziel.common.element.ElementEvaluator
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.util.FutureFactory
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.measureTime

object WorkspaceLoader {

    /**
     * 缓存的元素
     */
    lateinit var lastEvaluator: ElementEvaluator
        private set

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
    fun load(sender: ProxyCommandSender): CompletableFuture<Duration> {
        WorkspaceLoadEvent.Start().call() // 事件 - 开始加载
        val timeMark = TimeSource.Monotonic.markNow() // 开始记录时间
        val result = CompletableFuture<Duration>()
        // 创建评估器
        val evaluator = ElementEvaluator(executor)
        lastEvaluator = evaluator
        // 创建任务工厂
        FutureFactory {
            for (file in WorkspaceManager.getFilteredFiles()) {
                submitAsync(executor) {
                    try {
                        // 加载元素文件
                        DefaultElementLoader.load(file).onEach {
                            evaluator.submitWith(it) // 提交到评估器
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }.thenRun {
            val loadTime = timeMark.elapsedNow() // 加载所耗费的时间
            // 评估器开始评估
            evaluator.evaluate().thenAccept {
                val time = loadTime.plus(it) // 计算最终时间 = 加载时间 + 评估时间
                sender.sendLang("Workspace-Finished", evaluator.evaluatedElements.size, time.inWholeMilliseconds)
                WorkspaceLoadEvent.End().call() // 事件 - 结束加载
                result.complete(time) // 完成最后任务
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