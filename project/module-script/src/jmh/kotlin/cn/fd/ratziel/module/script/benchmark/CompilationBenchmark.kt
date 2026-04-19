package cn.fd.ratziel.module.script.benchmark

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

/**
 * 编译/预处理基准：测量 `prepare()` 单次开销（编译到字节码/AST/缓存的引擎内部结构）。
 *
 * 每次 `@Benchmark` 都完整 `prepare()` 一次，`@TearDown(Level.Invocation)` 在测量结束后释放，
 * 不计入测量时间。`@Fork(2)` + 5 次测量 = 10 次样本。
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, batchSize = 1)
@Measurement(iterations = 5, batchSize = 1)
@Fork(2)
internal open class CompilationBenchmark : ScriptBenchmarkBase() {

    private var prepared: Any? = null

    @Benchmark
    fun prepare(bh: Blackhole) {
        val p = case!!.prepare(sample!!)
        prepared = p
        bh.consume(p)
    }

    @TearDown(Level.Invocation)
    fun disposeAfter() {
        val c = case ?: return
        val p = prepared ?: return
        c.dispose(p)
        prepared = null
    }
}
