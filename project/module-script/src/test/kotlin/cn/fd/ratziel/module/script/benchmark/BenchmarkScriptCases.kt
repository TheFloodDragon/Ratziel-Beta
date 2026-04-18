package cn.fd.ratziel.module.script.benchmark

import java.awt.Point

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

internal val BENCHMARK_SCRIPT_CASES = listOf(
    BenchmarkScriptCase(
        id = "compute",
        displayName = "数值累加",
    ),
    BenchmarkScriptCase(
        id = "branching",
        displayName = "条件分支",
    ),
    BenchmarkScriptCase(
        id = "nested-loop",
        displayName = "嵌套循环",
    ),
    BenchmarkScriptCase(
        id = "list-index",
        displayName = "列表索引访问",
    ),
    BenchmarkScriptCase(
        id = "list-build",
        displayName = "列表构建",
    ),
    BenchmarkScriptCase(
        id = "map-build",
        displayName = "映射构建",
    ),
    BenchmarkScriptCase(
        id = "string-build",
        displayName = "字符串构建",
    ),
    BenchmarkScriptCase(
        id = "variable-expression",
        displayName = "变量计算（复杂表达式）",
        bindingsFactory = ::variableExpressionBindings,
    ),
    BenchmarkScriptCase(
        id = "host-class-access",
        displayName = "Java API 类元数据访问",
        bindingsFactory = ::javaApiBindings,
    ),
    BenchmarkScriptCase(
        id = "host-instance-field-read",
        displayName = "Java API 实例字段读取",
        bindingsFactory = ::javaApiBindings,
    ),
    BenchmarkScriptCase(
        id = "host-static-field-read",
        displayName = "Java API 静态字段读取",
        bindingsFactory = ::javaApiBindings,
    ),
    BenchmarkScriptCase(
        id = "host-instance-method-call",
        displayName = "Java API 实例方法调用",
        bindingsFactory = ::javaApiBindings,
    ),
    BenchmarkScriptCase(
        id = "host-static-method-call",
        displayName = "Java API 静态方法调用",
        bindingsFactory = ::javaApiBindings,
    ),
)
