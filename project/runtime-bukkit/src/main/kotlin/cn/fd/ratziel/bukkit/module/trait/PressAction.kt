package cn.fd.ratziel.bukkit.module.trait

import cn.fd.ratziel.core.coroutine.task.ContinuousTaskController
import cn.fd.ratziel.kether.NewKetherAction
import cn.fd.ratziel.kether.getFromFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import taboolib.module.kether.combinationParser
import kotlin.system.measureTimeMillis

/**
 * PressAction
 *
 * @author TheFloodDragon
 * @since 2023/9/2 12:56
 */

val pressController = ContinuousTaskController<Boolean>()

//TODO md好大的坑
/**
 * 用法:
 * press 特征名称 option 特征选项 time 存活周期
 */
@NewKetherAction(
    value = ["press", "r-press"],
)
internal fun actionPress() = combinationParser {
    it.group(
        text(),
        command("option", "opt", "-o", then = action()).option().defaultsTo(null),
        command("time", "-t", then = action()).option().defaultsTo(null),
    ).apply(it) { name, option, time ->
        runBlocking {
            measureTimeMillis {
                launch {
                    delay(3000)
                    pressController.getIds().forEach { key -> pressController.complete(key, false) }
                }
                println(pressController.newTask())
            }.also { mt -> println(mt) }
            now {
                "name=" + name + " ; " +
                        "time=" + getFromFrame(time, "2.5s") + " ; " +
                        "option=" + getFromFrame(option, "fuckyou")
            }
        }
    }
}
//TODO Recode This Shit
//internal fun actionPress() = bacikalParser {
//    fructus(
//        text(),
//        argument("time", "-t", then = action()),
//        argument("option", "opt", "-o", then = action()),
//    ) { frame, name, time, option ->
//        runBlocking {
//            delay(3000)
//            "name=" + name + " ; " +
//                    "time=" + time?.let { frame.runAction(time).get() } + " ; " +
//                    "option=" + option?.let { frame.runAction(option).get() }
//        }
//    }
//}