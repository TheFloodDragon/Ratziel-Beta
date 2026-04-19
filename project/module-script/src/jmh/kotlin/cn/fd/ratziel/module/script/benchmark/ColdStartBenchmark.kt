package cn.fd.ratziel.module.script.benchmark

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

/**
 * 冷启动基准：每次从源码直接解释/解析执行，不复用已编译对象。
 *
 * 采用 `Mode.SingleShotTime` 让每次 `@Benchmark` 方法调用单独计时；`@Fork(3)` 累计样本量。
 * 不同引擎的 `evaluate()` 路径存在设计差异（见 `BenchmarkCase.evaluate` 的各引擎实现），
 * 这正是"冷启动"真实语义的反映。
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, batchSize = 1)
@Measurement(iterations = 10, batchSize = 1)
@Fork(3)
internal open class ColdStartBenchmark : ScriptBenchmarkBase() {

    @Benchmark
    fun evaluate(bh: Blackhole) {
        bh.consume(case!!.evaluate(sample!!))
    }
}
