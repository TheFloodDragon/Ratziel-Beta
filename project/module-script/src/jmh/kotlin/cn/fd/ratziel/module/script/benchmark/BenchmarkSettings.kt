package cn.fd.ratziel.module.script.benchmark

/**
 * 脚本内主循环次数的占位符。在所有单层循环样本中都会出现（如 `for i in 0..<__ITER__`）。
 *
 * JMH 的 warmup/measurement 迭代次数是**独立维度**，由 `@Warmup` / `@Measurement` 注解或
 * `-PjmhWarmupIterations` / `-PjmhIterations` 命令行参数控制，不再复用到脚本内循环。
 */
private const val ITER_PLACEHOLDER = "__ITER__"

internal val BENCHMARK_SETTINGS = BenchmarkSettings()

internal data class BenchmarkSettings(
    /** 脚本主循环次数，仅决定脚本内部工作量 */
    val scriptIterations: Int = 2_000,
) {

    fun apply(content: String): String = content.replace(ITER_PLACEHOLDER, scriptIterations.toString())
}
