package cn.fd.ratziel.common

import cn.fd.ratziel.common.element.ElementEvaluator
import cn.fd.ratziel.common.element.ElementLoader
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.common.util.FileListener
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
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
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.TimeSource

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
        Executors.newFixedThreadPool(8)
    }

    /**
     * 加载过的元素表
     *
     * 元素在评估之前就被加入到了这个列表里,
     * 因此无论元素在评估过程成失败与否,
     * 总能通过这个表获取从配置文件中加载的元素
     */
    val livingElements: MutableMap<String, Element> = ConcurrentHashMap()

    /**
     * 初始化工作空间
     */
    private fun init(sender: ProxyCommandSender): CompletableFuture<Duration> {
        // 开始记录时间
        val timeMark = TimeSource.Monotonic.markNow()

        WorkspaceManager.initializeAllWorkspace() // 初始化工作空间
        FileListener.clear() // 清空监听器
        livingElements.clear() // 清空加载过的元素表
        ElementEvaluator.clear()  // 清空评估器

        // 创建任务工厂
        val tasks = ArrayList<CompletableFuture<Unit>>()
        // 加载所有工作空间
        for (workspace in WorkspaceManager.workspaces) {
            // 加载工作空间内的文件
            for (file in workspace.filteredFiles) {
                // 分配到的加载器
                val loader = ElementLoader.allocate(file, workspace) ?: continue
                // 提交加载任务
                tasks.add(CompletableFuture.supplyAsync({
                    // 加载文件
                    val result = loader.load(file, workspace)
                    if (result.isSuccess) {
                        for (loadedElement in result.getOrThrow()) {
                            // 检查重复元素
                            if (livingElements[loadedElement.name] != null) {
                                sender.sendLang("Element-File-Duplicated", loadedElement.name, file)
                                continue // 跳过加载
                            }
                            livingElements[loadedElement.name] = loadedElement
                            // 提交到周期评估任务表里
                            ElementEvaluator.submit(loadedElement)
                        }
                    } else sender.sendLang("Element-File-Load-Failed", file.name)
                }, executor))
                // 监听自动重载的文件
                if (workspace.listen) listenFile(file, workspace, loader, sender)
            }
        }

        // 所有加载任务结束后执行
        return CompletableFuture.allOf(*tasks.toTypedArray()).thenApply {
            timeMark.elapsedNow().also {
                sender.sendLang("Workspace-Initiated", WorkspaceManager.workspaces.size, it.inWholeMilliseconds)
            }
        }
    }

    /**
     * 加载工作空间中的元素
     */
    private fun load(sender: ProxyCommandSender): CompletableFuture<Duration> {
        val result = CompletableFuture<Duration>()

        // 设置失败消息回调
        ElementHandler.failureCallback = { element, throwable ->
            // 从评估过的元素中删除
            ElementEvaluator.evaluatedElements.remove(element.identifier)
            // 失败时发送失败消息
            if (throwable != null) sender.sendLang("Element-File-Evaluate-Failed", element.name, element.file.name)
        }

        WorkspaceLoadEvent.Start().call() // 事件 - 开始加载

        // 评估器开始评估
        ElementEvaluator.evaluateCycled().thenAccept { time ->
            WorkspaceLoadEvent.End().call() // 事件 - 结束加载
            sender.sendLang("Workspace-Loaded", ElementEvaluator.evaluatedElements.size, time.inWholeMilliseconds)
            result.complete(time) // 完成最后任务
        }

        // 返回任务
        return result
    }

    private fun listenFile(file: File, workspace: Workspace, loader: ElementLoader, sender: ProxyCommandSender) {
        FileListener.listen(file) { file ->
            if (!file.exists()) return@listen
            measureTimeMillis {
                // 加载文件
                val result = loader.load(file, workspace)
                if (result.isSuccess) {
                    for (loadedElement in result.getOrThrow()) {
                        // 加入到 livingElements
                        livingElements[loadedElement.name] = loadedElement
                        // 处理任务
                        val throwable = ElementEvaluator.handleElement(loadedElement)
                        // 只要一个元素评估失败就判定整个文件都算失败的
                        if (throwable != null) {
                            sender.sendLang("Element-File-Reload-Failed", file.name)
                            return@listen
                        }
                    }
                } else {
                    sender.sendLang("Element-File-Reload-Failed", file.name)
                    return@listen
                }
            }.also {
                // 成功加载和评估的提示
                sender.sendLang("Element-File-Reload-Succeed", file.name, it)
            }
        }
    }

    /**
     * 重新加载工作空间
     */
    fun reload(sender: ProxyCommandSender) {
        // 初始化工作空间
        init(sender).join()
        // 加载元素文件
        load(sender).join()
    }

    @Awake(LifeCycle.LOAD)
    private fun load() {
        init(console()).join()
        load(console())
    }

}