package cn.fd.ratziel.bukkit.module.trait_todo

import cn.fd.ratziel.core.coroutine.task.LiveContinuousTaskController
import cn.fd.ratziel.core.util.randomUUID
import cn.fd.ratziel.kether.getFromFrame
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import taboolib.module.kether.actionNow
import taboolib.module.kether.combinationParser
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * TraitAction
 *
 * @author TheFloodDragon
 * @since 2023/9/2 12:56
 */

val pressController = LiveContinuousTaskController<Boolean>()

//TODO 待验证的可行性
/**
 * 用法:
 * press 特征名称 option 特征选项 time 存活周期
 * /r dev runKether press "interact" -o "left" -t "20s"
 */
//@NewKetherAction(
//    value = ["press", "r-press"],
//)
internal fun actionPress() = combinationParser {
    it.group(
        text(),
        command("option", "opt", "-o", then = action()).option().defaultsTo(null),
        command("time", "-t", then = action()).option().defaultsTo(null),
    ).apply(it) { name, option, time ->
        var result = false
        runBlocking {
            var str: String? = null
            var parsedTime: Duration? = null
            /**
             * 值的设置
             */
            launch {
                actionNow {
                    parsedTime = Duration.parse(getFromFrame(time, "0ms"))
                    println(parsedTime?.toLong(DurationUnit.MILLISECONDS))
                    val trait = TraitRegistry.match(name)
                    println(trait)
                    val opt = getFromFrame(option, String)
                    println(opt)
                    if (trait != null) {
                        str = "${trait.id}_${opt}_${randomUUID()}"
                    }
                }
            }.join()

            measureTimeMillis {
                if (parsedTime != null || str != null) {
                    result = pressController.newTask(
                        id = str!!, duration = parsedTime!!, defaultResult = false
                    )
                    println(result)
                }
            }.also { mt -> println(mt) }

            /**
             * 返回值
             */
            now { result }
        }
    }
}