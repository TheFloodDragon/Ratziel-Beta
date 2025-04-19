package cn.fd.ratziel.common

import cn.fd.ratziel.common.element.ElementEvaluator
import cn.fd.ratziel.common.element.ElementLoader
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.util.FutureFactory
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.common5.FileWatcher
import taboolib.module.lang.sendLang
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.measureTime

object WorkspaceLoader {

    /**
     * 元素评估器
     */
    lateinit var currentEvaluator: ElementEvaluator
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
    private fun init(sender: ProxyCommandSender) = measureTime {
        WorkspaceManager.initializeAllWorkspace()
    }.also { sender.sendLang("Workspace-Initiated", WorkspaceManager.workspaces.size, it.inWholeMilliseconds) }

    /**
     * 加载工作空间中的元素
     */
    private fun load(sender: ProxyCommandSender): CompletableFuture<Duration> {
        val timeMark = TimeSource.Monotonic.markNow() // 开始记录时间
        WorkspaceLoadEvent.Start().call() // 事件 - 开始加载
        val result = CompletableFuture<Duration>()
        // 创建评估器
        currentEvaluator = ElementEvaluator(executor)
        // 创建任务工厂
        val tasks = FutureFactory<Unit>()
        // 加载所有工作空间
        for (workspace in WorkspaceManager.workspaces) {
            // 加载工作空间内的文件
            for (file in workspace.files) {
                // 监听自动重载的文件
                if (workspace.listen) {
                    FileWatcher.INSTANCE.addSimpleListener(file) {
                        try {
                            val elements = ElementLoader.load(workspace, file)
                            for (element in elements) currentEvaluator.handleElement(element)
                            sender.sendLang("Element-File-Reload-Succeed", file.name)
                        } catch (ex: Exception) {
                            sender.sendLang("Element-File-Reload-Failed", file.name)
                            ex.printStackTrace()
                        }
                    }
                }
                tasks.submitAsync(executor) {
                    try {
                        // 加载元素文件
                        ElementLoader.load(workspace, file).onEach { element ->
                            currentEvaluator.submitWith(element) // 提交到评估器
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
        // 所有任务结束后执行
        tasks.thenRun {
            val loadTime = timeMark.elapsedNow() // 加载所耗费的时间
            // 评估器开始评估
            currentEvaluator.evaluate().thenAccept {
                WorkspaceLoadEvent.End().call() // 事件 - 结束加载
                val time = loadTime.plus(it) // 计算最终时间 = 加载时间 + 评估时间
                sender.sendLang("Workspace-Loaded", currentEvaluator.evaluatedElements.size, time.inWholeMilliseconds)
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