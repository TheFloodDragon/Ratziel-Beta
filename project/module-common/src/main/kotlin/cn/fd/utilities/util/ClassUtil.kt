package cn.fd.utilities.util

import taboolib.common.io.runningClasses
import java.util.*

fun getRunningClasses(): LinkedList<Class<*>> {
    return runningClasses
}