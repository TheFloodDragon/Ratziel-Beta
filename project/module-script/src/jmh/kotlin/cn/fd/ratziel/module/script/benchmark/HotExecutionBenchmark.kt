package cn.fd.ratziel.module.script.benchmark

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

/**
 * 热执行基准：复用已编译/预处理的脚本对象，测量稳态执行开销。
 *
 * - `@Fork(1)`：每个 (engine, scriptCase) 组合在独立 JVM 中运行，避免引擎间类加载/GC 互污染。
 * - `@Warmup` + `@Measurement`：让 HotSpot 充分特化，随后采样。
 * - `@Benchmark` 方法只做一次 `execute`，脚本内部循环由 `__ITER__` 控制（详见 [BenchmarkSettings]）。
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
internal open class HotExecutionBenchmark : ScriptBenchmarkBase() {

    private var prepared: Any? = null

    @Setup(Level.Trial)
    fun prepareScript() {
        prepared = case!!.prepare(sample!!)
    }

    @Benchmark
    fun execute(bh: Blackhole) {
        bh.consume(case!!.execute(prepared!!))
    }

    @TearDown(Level.Trial)
    fun disposeScript() {
        val c = case ?: return
        val p = prepared ?: return
        c.dispose(p)
        prepared = null
    }
}
