package cn.fd.ratziel.core.task

/**
 * BaseTask - 基础任务
 *
 * @author TheFloodDragon
 * @since 2023/9/22 22:38
 */
open class BaseTask(override val id: String, taskLiveCycle: TaskLifeTrace) : TaskLifeTrace(taskLiveCycle), Task {

    /**
     * 任务是否开始
     */
    var isStarted = false
        protected set

    /**
     * 任务是否结束
     */
    var isFinished = false
        protected set

    /**
     * 开始任务
     */
    protected open fun startTask(function: Runnable = Runnable { }) {
        beforeStart.apply(this)
        function.run()
        isStarted = true
        onStart.apply(this)
    }

    /**
     * 结束任务
     */
    protected open fun finishTask(function: Runnable = Runnable { }) {
        beforeFinish.apply(this)
        function.run()
        isFinished = true
        onFinish.apply(this)
    }

}