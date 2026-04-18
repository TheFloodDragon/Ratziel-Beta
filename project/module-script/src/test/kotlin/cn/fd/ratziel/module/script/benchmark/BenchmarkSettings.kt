package cn.fd.ratziel.module.script.benchmark

private const val ITER = "__ITER__"

internal val BENCHMARK_SETTINGS = BenchmarkSettings()

internal data class BenchmarkSettings(
    val buildIterations: Int = 5,
    val hotWarmupIterations: Int = 1,
    val hotMeasuredIterations: Int = 5,
    val coldWarmupIterations: Int = 1,
    val coldMeasuredIterations: Int = 5,
    val iterations: Int = 2_000,
) {

    fun apply(content: String): String {
        return content.replace(ITER, iterations.toString())
    }
}
