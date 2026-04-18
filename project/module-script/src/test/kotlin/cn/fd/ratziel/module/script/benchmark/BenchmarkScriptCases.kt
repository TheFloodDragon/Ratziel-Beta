package cn.fd.ratziel.module.script.benchmark

import java.awt.Point

private fun javaApiBindings(): MutableMap<String, Any?> = linkedMapOf(
    "integerClass" to Integer::class.java,
    "mathClass" to Math::class.java,
    "point" to Point(7, 11),
    "text" to "benchmark-mark",
)

private fun variableExpressionBindings(): MutableMap<String, Any?> = linkedMapOf(
    "base" to 17L,
    "multiplier" to 29L,
    "modulus" to 7L,
    "offset" to 43L,
    "divisor" to 3L,
    "bias" to 5L,
)

internal val BENCHMARK_SCRIPT_CASES = listOf(
    BenchmarkScriptCase(
        id = "compute",
        displayName = "数值累加",
        fileNames = mapOf(
            "javascript" to "compute.js",
            "jexl" to "compute.jexl",
            "kotlin" to "compute.kts",
            "fluxon" to "compute.fs",
        ),
    ),
    BenchmarkScriptCase(
        id = "branching",
        displayName = "条件分支",
        fileNames = mapOf(
            "javascript" to "branching.js",
            "jexl" to "branching.jexl",
            "kotlin" to "branching.kts",
            "fluxon" to "branching.fs",
        ),
    ),
    BenchmarkScriptCase(
        id = "nested-loop",
        displayName = "嵌套循环",
        fileNames = mapOf(
            "javascript" to "nested-loop.js",
            "jexl" to "nested-loop.jexl",
            "kotlin" to "nested-loop.kts",
            "fluxon" to "nested-loop.fs",
        ),
    ),
    BenchmarkScriptCase(
        id = "list-index",
        displayName = "列表索引访问",
        fileNames = mapOf(
            "javascript" to "list-index.js",
            "jexl" to "list-index.jexl",
            "kotlin" to "list-index.kts",
            "fluxon" to "list-index.fs",
        ),
    ),
    BenchmarkScriptCase(
        id = "list-build",
        displayName = "列表构建",
        fileNames = mapOf(
            "javascript" to "list-build.js",
            "jexl" to "list-build.jexl",
            "kotlin" to "list-build.kts",
            "fluxon" to "list-build.fs",
        ),
    ),
    BenchmarkScriptCase(
        id = "map-build",
        displayName = "映射构建",
        fileNames = mapOf(
            "javascript" to "map-build.js",
            "jexl" to "map-build.jexl",
            "kotlin" to "map-build.kts",
            "fluxon" to "map-build.fs",
        ),
    ),
    BenchmarkScriptCase(
        id = "string-build",
        displayName = "字符串构建",
        fileNames = mapOf(
            "javascript" to "string-build.js",
            "jexl" to "string-build.jexl",
            "kotlin" to "string-build.kts",
            "fluxon" to "string-build.fs",
        ),
    ),
    BenchmarkScriptCase(
        id = "variable-expression",
        displayName = "变量计算（复杂表达式）",
        fileNames = mapOf(
            "javascript" to "variable-expression.js",
            "jexl" to "variable-expression.jexl",
            "kotlin" to "variable-expression.kts",
            "fluxon" to "variable-expression.fs",
        ),
        bindingsFactory = ::variableExpressionBindings,
    ),
    BenchmarkScriptCase(
        id = "host-class-access",
        displayName = "Java API 类元数据访问",
        fileNames = mapOf(
            "javascript" to "host-class-access.js",
            "jexl" to "host-class-access.jexl",
            "kotlin" to "host-class-access.kts",
            "fluxon" to "host-class-access.fs",
        ),
        bindingsFactory = ::javaApiBindings,
    ),
    BenchmarkScriptCase(
        id = "host-instance-field-read",
        displayName = "Java API 实例字段读取",
        fileNames = mapOf(
            "javascript" to "host-instance-field-read.js",
            "jexl" to "host-instance-field-read.jexl",
            "kotlin" to "host-instance-field-read.kts",
            "fluxon" to "host-instance-field-read.fs",
        ),
        bindingsFactory = ::javaApiBindings,
    ),
    BenchmarkScriptCase(
        id = "host-static-field-read",
        displayName = "Java API 静态字段读取",
        fileNames = mapOf(
            "javascript" to "host-static-field-read.js",
            "jexl" to "host-static-field-read.jexl",
            "kotlin" to "host-static-field-read.kts",
            "fluxon" to "host-static-field-read.fs",
        ),
        bindingsFactory = ::javaApiBindings,
    ),
    BenchmarkScriptCase(
        id = "host-instance-method-call",
        displayName = "Java API 实例方法调用",
        fileNames = mapOf(
            "javascript" to "host-instance-method-call.js",
            "jexl" to "host-instance-method-call.jexl",
            "kotlin" to "host-instance-method-call.kts",
            "fluxon" to "host-instance-method-call.fs",
        ),
        bindingsFactory = ::javaApiBindings,
    ),
    BenchmarkScriptCase(
        id = "host-static-method-call",
        displayName = "Java API 静态方法调用",
        fileNames = mapOf(
            "javascript" to "host-static-method-call.js",
            "jexl" to "host-static-method-call.jexl",
            "kotlin" to "host-static-method-call.kts",
            "fluxon" to "host-static-method-call.fs",
        ),
        bindingsFactory = ::javaApiBindings,
    ),
)
