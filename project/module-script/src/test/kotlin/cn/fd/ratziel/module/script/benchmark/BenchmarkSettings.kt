package cn.fd.ratziel.module.script.benchmark

private const val ITER = "__ITER__"

internal val BENCHMARK_SETTINGS = BenchmarkSettings()

internal data class BenchmarkSettings(
    val forks: Int = 3,
    val buildWarmupIterations: Int = 2,
    val buildMeasuredIterations: Int = 5,
    val compiledExecutionWarmupIterations: Int = 2,
    val compiledExecutionMeasuredIterations: Int = 5,
    val interpretedExecutionWarmupIterations: Int = 2,
    val interpretedExecutionMeasuredIterations: Int = 5,
    val iterations: Int = 2_000,
) {

    fun apply(content: String): String {
        return content.replace(ITER, iterations.toString())
    }
}
