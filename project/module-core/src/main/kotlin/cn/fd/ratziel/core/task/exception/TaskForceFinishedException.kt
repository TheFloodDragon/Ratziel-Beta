package cn.fd.ratziel.core.task.exception

import cn.fd.ratziel.core.task.api.Task

/**
 * TaskForceFinishedException
 *
 * @author TheFloodDragon
 * @since 2023/9/22 22:20
 */
class TaskForceFinishedException(task: Task) : RuntimeException("Task $task is forced to finished. (${task.id})")