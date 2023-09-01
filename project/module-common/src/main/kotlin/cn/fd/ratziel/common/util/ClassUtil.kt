package cn.fd.ratziel.common.util

import taboolib.common.io.runningClassMap
import taboolib.common.util.unsafeLazy

/**
 * 当前插件的所有类 (排除Taboolib本身的)
 */
val runningClassMapWithoutTaboolib by unsafeLazy { runningClassMap.filter { !it.key.startsWith("tb") } }

/**
 * 当前插件的所有类的集合 (排除Taboolib本身的)
 */
val runningClassesWithoutTaboolib by unsafeLazy { runningClassMapWithoutTaboolib.values }