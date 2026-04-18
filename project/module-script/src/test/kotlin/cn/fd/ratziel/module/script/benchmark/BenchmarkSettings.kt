package cn.fd.ratziel.module.script.benchmark

private const val ITER = "__ITER__"

internal val BENCHMARK_SETTINGS = BenchmarkSettings()

internal data class BenchmarkSettings(
    val buildMeasuredIterations: Int = 10,
    val hotWarmupIterations: Int = 10,
    val hotMeasuredIterations: Int = 10,
    val coldWarmupIterations: Int = 10,
    val coldMeasuredIterations: Int = 10,
    val iterations: Int = 5_000,
) {

    fun apply(content: String): String {
        return content.replace(ITER, iterations.toString())
    }
}
