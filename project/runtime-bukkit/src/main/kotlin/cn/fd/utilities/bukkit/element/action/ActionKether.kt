package cn.fd.utilities.bukkit.element.action

import cn.fd.utilities.bukkit.kether.api.KetherAction
import taboolib.module.kether.combinationParser

//TODO md好大的坑
/**
 * 用法:
 * press 特征名称 option 特征选项 time 存活周期
 */
@KetherAction(
    value = ["press", "f-press"],
    namespace = ["fdu"],
    shared = false
)
internal fun actionPress() = combinationParser {
    it.group(
        text(),
        command("time", "-t", then = action()).option().defaultsTo(null),
        command("option", "opt", "-o", then = action()).option().defaultsTo(null),
    ).apply(it) { name, time, option ->
        now {
            "name=" + name + " ; " +
                    "time=" + time + " ; " +
                    "option=" + option
        }
    }
}
