package cn.fd.ratziel.core.coroutine.task

import cn.fd.ratziel.core.Task

/**
 * TaskForceFinishedException
 *
 * @author TheFloodDragon
 * @since 2023/9/22 22:20
 */
class TaskForceFinishedException(task: Task) : RuntimeException("Task $task is forced to finished. (${task.id})")