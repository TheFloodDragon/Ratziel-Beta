package cn.fd.ratziel.module.script.benchmark

import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State

/**
 * 所有脚本基准共享的 `@Param` 维度与解析逻辑。JMH 允许子类继承父类的 `@State` / `@Param` / `@Setup`，
 * 因此把 (engine, scriptCase) 的 cross-product 只声明一次。
 *
 * `@JvmField` 让这些字段以**公开字段**形式落到字节码上，JMH 字节码生成器才能直接 `putfield` 它们——
 * Kotlin 默认 `private` 字段会被 JMH 跳过。
 *
 * `scriptCase` 编码为 `"id|显示名"`，这样 JMH 输出的 `params.scriptCase` 自带 displayName，
 * 报告渲染按 `|` 拆回即可，不需要额外映射表。
 */
@State(Scope.Benchmark)
internal abstract class ScriptBenchmarkBase {

    @JvmField
    @Param("Fluxon", "GraalJS", "Nashorn", "Jexl", "KotlinScripting")
    var engine: String = "UNKNOWN"

    @JvmField
    @Param(
        "compute|数值累加",
        "branching|条件分支",
        "nested-loop|嵌套循环",
        "list-index|列表索引访问",
        "list-build|列表构建",
        "map-build|映射构建",
        "string-build|字符串构建",
        "variable-expression|变量计算（复杂表达式）",
        "host-class-access|Java API 类元数据访问",
        "host-instance-field-read|Java API 实例字段读取",
        "host-static-field-read|Java API 静态字段读取",
        "host-instance-method-call|Java API 实例方法调用",
        "host-static-method-call|Java API 静态方法调用",
    )
    var scriptCase: String = "UNKNOWN"

    protected var case: BenchmarkCase<Any>? = null
    protected var sample: ScriptSample? = null

    @Setup(Level.Trial)
    fun resolveTargets() {
        val id = scriptCase.substringBefore('|')
        @Suppress("UNCHECKED_CAST")
        val resolved = BENCHMARK_CASES.first { it.engineName == engine } as BenchmarkCase<Any>
        val scriptCaseEntry = BENCHMARK_SCRIPT_CASES.first { it.id == id }
        case = resolved
        sample = resolved.sample(scriptCaseEntry)
    }
}
