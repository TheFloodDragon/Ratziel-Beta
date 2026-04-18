package cn.fd.ratziel.module.script.performance

internal val PERFORMANCE_SCRIPT_CASES = listOf(
    PerformanceScriptCase(
        id = "compute",
        displayName = "数值累加",
        fileNames = mapOf(
            "javascript" to "compute.js",
            "jexl" to "compute.jexl",
            "kotlin" to "compute.benchmark.kts",
            "fluxon" to "compute.fs",
        ),
    ),
    PerformanceScriptCase(
        id = "branching",
        displayName = "条件分支",
        fileNames = mapOf(
            "javascript" to "branching.js",
            "jexl" to "branching.jexl",
            "kotlin" to "branching.benchmark.kts",
            "fluxon" to "branching.fs",
        ),
    ),
    PerformanceScriptCase(
        id = "nested-loop",
        displayName = "嵌套循环",
        fileNames = mapOf(
            "javascript" to "nested-loop.js",
            "jexl" to "nested-loop.jexl",
            "kotlin" to "nested-loop.benchmark.kts",
            "fluxon" to "nested-loop.fs",
        ),
    ),
    PerformanceScriptCase(
        id = "list-index",
        displayName = "列表索引访问",
        fileNames = mapOf(
            "javascript" to "list-index.js",
            "jexl" to "list-index.jexl",
            "kotlin" to "list-index.benchmark.kts",
            "fluxon" to "list-index.fs",
        ),
    ),
    PerformanceScriptCase(
        id = "list-build",
        displayName = "列表构建",
        fileNames = mapOf(
            "javascript" to "list-build.js",
            "jexl" to "list-build.jexl",
            "kotlin" to "list-build.benchmark.kts",
            "fluxon" to "list-build.fs",
        ),
    ),
    PerformanceScriptCase(
        id = "map-build",
        displayName = "映射构建",
        fileNames = mapOf(
            "javascript" to "map-build.js",
            "jexl" to "map-build.jexl",
            "kotlin" to "map-build.benchmark.kts",
            "fluxon" to "map-build.fs",
        ),
    ),
    PerformanceScriptCase(
        id = "string-build",
        displayName = "字符串构建",
        fileNames = mapOf(
            "javascript" to "string-build.js",
            "jexl" to "string-build.jexl",
            "kotlin" to "string-build.benchmark.kts",
            "fluxon" to "string-build.fs",
        ),
    ),
)
