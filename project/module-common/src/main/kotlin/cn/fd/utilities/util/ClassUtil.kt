package cn.fd.utilities.util

import taboolib.common.io.runningClassMap
import taboolib.common.util.unsafeLazy

val runningClassMapWithoutTaboolib by unsafeLazy { runningClassMap.filter { !it.key.startsWith("tb") } }

val runningClassesWithoutTaboolib by unsafeLazy { runningClassMapWithoutTaboolib.values }