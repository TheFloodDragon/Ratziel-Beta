package cn.fd.ratziel.core.util

import java.util.*

fun randomUUID() = UUID.randomUUID().toString().replace("-", "").lowercase()