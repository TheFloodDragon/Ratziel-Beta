package cn.fd.ratziel.module.script.benchmark

import java.awt.Point

/**
 * 所有基准脚本用例；id 同时作为样本文件名（`/samples/<lang>/<id>.<ext>`）和 JMH `@Param` 取值的前半段
 * （后半段是 displayName，编码在 `ScriptBenchmarkBase` 的 `@Param` 字符串里）。若两处 id 不一致，
 * 启动时会抛 [NoSuchElementException]。
 */
internal val BENCHMARK_SCRIPT_CASES = listOf(
    BenchmarkScriptCase("compute"),
    BenchmarkScriptCase("branching"),
    BenchmarkScriptCase("nested-loop"),
    BenchmarkScriptCase("list-index"),
    BenchmarkScriptCase("list-build"),
    BenchmarkScriptCase("map-build"),
    BenchmarkScriptCase("string-build"),
    BenchmarkScriptCase("variable-expression", ::variableExpressionBindings),
    BenchmarkScriptCase("host-class-access", ::javaApiBindings),
    BenchmarkScriptCase("host-instance-field-read", ::javaApiBindings),
    BenchmarkScriptCase("host-static-field-read", ::javaApiBindings),
    BenchmarkScriptCase("host-instance-method-call", ::javaApiBindings),
    BenchmarkScriptCase("host-static-method-call", ::javaApiBindings),
)

private fun javaApiBindings(): MutableMap<String, Any?> = linkedMapOf(
    "integerClass" to Int::class.javaObjectType,
    "mathClass" to Math::class.java,
    "point" to Point(7, 11),
    "text" to "benchmark-mark",
)

private fun variableExpressionBindings(): MutableMap<String, Any?> = linkedMapOf(
    "base" to 17,
    "multiplier" to 29,
    "modulus" to 7,
    "offset" to 43,
    "divisor" to 3,
    "bias" to 5,
)
