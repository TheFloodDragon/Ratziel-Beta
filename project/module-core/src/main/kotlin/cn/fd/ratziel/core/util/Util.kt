package cn.fd.ratziel.core.util

import java.util.UUID

fun randomUUID() = UUID.randomUUID().toString().replace("-", "").lowercase()