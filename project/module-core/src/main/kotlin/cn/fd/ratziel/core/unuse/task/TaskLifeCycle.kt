package cn.fd.ratziel.core.unuse.task

import java.util.function.Function

/**
 * TaskLifeCycle
 *
 * @author TheFloodDragon
 * @since 2023/9/22 21:44
 */
open class TaskLifeCycle(
    /**
     * 任务运行前
     */
    protected val beforeStart: Function<TaskLifeCycle, Unit> = Function {},
    /**
     * 任务运行时
     */
    protected val onStart: Function<TaskLifeCycle, Unit> = Function {},
    /**
     * 任务结束前
     */
    protected val beforeFinish: Function<TaskLifeCycle, Unit> = Function {},
    /**
     * 任务结束后
     */
    protected val onFinish: Function<TaskLifeCycle, Unit> = Function {},
) {

    /**
     * 构造器
     */
    constructor(liveCycle: TaskLifeCycle) : this(
        liveCycle.beforeStart,
        liveCycle.onStart,
        liveCycle.beforeFinish,
        liveCycle.onFinish
    )

}