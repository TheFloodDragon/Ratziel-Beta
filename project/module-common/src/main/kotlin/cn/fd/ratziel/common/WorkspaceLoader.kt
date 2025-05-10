package cn.fd.ratziel.common

import cn.fd.ratziel.common.element.ElementEvaluator
import cn.fd.ratziel.common.element.ElementLoader
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.common.util.FileListener
import cn.fd.ratziel.core.element.Element
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.measureTime

/**
 * WorkspaceLoader
 *
 * @author TheFloodDragon
 * @since 2025/4/25 22:41
 */
object WorkspaceLoader {

    /**
     * 线程池
     */
    val executor: ExecutorService by lazy {
        Executors.newFixedThreadPool(4)
    }

    /**
     * 加载过的元素表
     *
     * 元素在评估之前就被加入到了这个列表里,
     * 因此无论元素在评估过程成失败与否,
     * 总能通过这个表获取从配置文件中加载的元素
     */
    val loadedElements: MutableMap<String, Element> = ConcurrentHashMap()

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
        val result = CompletableFuture<Duration>()
        WorkspaceLoadEvent.Start().call() // 事件 - 开始加载

        // 开始记录时间
        val timeMark = TimeSource.Monotonic.markNow()

        FileListener.clear() // 清空监听器
        ElementEvaluator.clear() // 清空评估器

        // 创建任务工厂
        val tasks = ArrayList<CompletableFuture<Unit>>()
        // 加载所有工作空间
        for (workspace in WorkspaceManager.workspaces) {
            // 加载工作空间内的文件
            for (file in workspace.files) {
                // 监听自动重载的文件
                if (workspace.listen) listenFile(workspace, file, sender)

                // 提交加载任务
                tasks.add(CompletableFuture.supplyAsync({
                    // 加载文件
                    ElementLoader.load(workspace, file).getOrElse {
                        sender.sendLang("Element-File-Load-Failed", file.name)
                        emptyList() // 加载失败返回空列表 (表示不注册到评估器里)
                    }.forEach { element ->
                        // 加入到 loadedElements
                        loadedElements[element.name] = element
                        // 提交到周期评估任务表里
                        ElementEvaluator.submit(element) {
                            // 失败时发送失败消息
                            if (it != null) sender.sendLang("Element-File-Evaluate-Failed", element.name, file.name)
                        }
                    }
                }, executor))
            }
        }
        // 所有加载任务结束后执行
        CompletableFuture.allOf(*tasks.toTypedArray()).thenRun {
            val loadTime = timeMark.elapsedNow() // 加载文件所耗费的时间
            // 评估器开始评估
            ElementEvaluator.evaluateCycled().thenAccept { evalTime ->
                val time = loadTime.plus(evalTime) // 计算最终时间 = 加载时间 + 评估时间
                WorkspaceLoadEvent.End().call() // 事件 - 结束加载
                sender.sendLang("Workspace-Loaded", ElementEvaluator.evaluatedElements.size, time.inWholeMilliseconds)
                result.complete(time) // 完成最后任务
            }
        }
        // 返回任务
        return result
    }

    private fun listenFile(workspace: Workspace, file: File, sender: ProxyCommandSender) {
        FileListener.listen(file) { file ->
            // 加载文件
            ElementLoader.load(workspace, file).getOrElse { _ ->
                sender.sendLang("Element-File-Reload-Failed", file.name)
                return@listen
            }.forEach { element ->
                // 加入到 loadedElements
                loadedElements[element.name] = element
                // 处理任务
                val throwable = ElementEvaluator.handleElement(element)
                // 只要一个元素评估失败就判定整个文件都算失败的
                if (throwable != null) {
                    sender.sendLang("Element-File-Reload-Failed", file.name)
                    return@listen
                }
            }
            // 成功加载和评估的提示
            sender.sendLang("Element-File-Reload-Succeed", file.name)
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