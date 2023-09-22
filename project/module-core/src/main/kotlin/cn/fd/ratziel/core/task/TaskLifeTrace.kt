package cn.fd.ratziel.core.task

import java.util.function.Function

/**
 * 任务行迹
 *
 * @author TheFloodDragon
 * @since 2023/9/22 21:44
 */
open class TaskLifeTrace(
    /**
     * 任务运行前
     */
    protected val beforeStart: Function<TaskLifeTrace, Unit> = Function {},
    /**
     * 任务运行时
     */
    protected val onStart: Function<TaskLifeTrace, Unit> = Function {},
    /**
     * 任务结束前
     */
    protected val beforeFinish: Function<TaskLifeTrace, Unit> = Function {},
    /**
     * 任务结束后
     */
    protected val onFinish: Function<TaskLifeTrace, Unit> = Function {},
) {

    /**
     * 构造器
     */
    constructor(liveCycle: TaskLifeTrace) : this(
        liveCycle.beforeStart,
        liveCycle.onStart,
        liveCycle.beforeFinish,
        liveCycle.onFinish
    )

}