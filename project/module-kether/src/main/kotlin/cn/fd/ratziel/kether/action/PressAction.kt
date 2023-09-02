package cn.fd.ratziel.kether.action

import cn.fd.ratziel.kether.api.KetherAction
import cn.fd.ratziel.kether.getFromFrame
import taboolib.module.kether.ParserHolder.defaultsTo
import taboolib.module.kether.ParserHolder.option
import taboolib.module.kether.combinationParser

/**
 * PressAction
 *
 * @author TheFloodDragon
 * @since 2023/9/2 12:56
 */

//TODO md好大的坑
/**
 * 用法:
 * press 特征名称 option 特征选项 time 存活周期
 */
@KetherAction(
    value = ["press", "r-press"],
    namespace = ["ratziel"],
    shared = false
)
internal fun actionPress() = combinationParser {
    it.group(
        text(),
        command("time", "-t", then = action()).option().defaultsTo(null),
        command("option", "opt", "-o", then = action()).option().defaultsTo(null),
    ).apply(it) { name, time, option ->
        now {
            "name=" + name+ " ; " +
                    "time=" + getFromFrame(time,"2.5s") + " ; " +
                    "option=" + getFromFrame(option,"fuckyou")
        }
    }
}
