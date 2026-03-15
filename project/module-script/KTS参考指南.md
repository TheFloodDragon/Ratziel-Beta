# Kotlin Scripting 开发者文档（含示例）

> 这是一份面向实际开发的文档版本。
>
> 它重点回答：
> - 我如何从零接入 Kotlin Scripting
> - 我怎么定义一个自定义脚本类型
> - 我怎么给脚本注入上下文
> - 我怎么执行脚本、做缓存、做 JSR223 集成
>
> 说明：下面的示例以当前 `libraries/scripting` 中已分析的 API 设计为基础，偏“开发者参考实现风格”。

---

# 1. 最常见使用方式总览

## 1.1 如果你只是想“跑一个自定义脚本”

推荐：
- `@KotlinScript`
- `ScriptCompilationConfiguration`
- `ScriptEvaluationConfiguration`
- `BasicJvmScriptingHost`

## 1.2 如果你想“脚本里直接访问宿主 DSL”

推荐：
- `baseClass`
- `implicitReceivers`
- `defaultImports`

## 1.3 如果你想“给脚本传变量”

推荐：
- 编译阶段：`providedProperties`
- 执行阶段：`providedProperties` 实际值

## 1.4 如果你想“重复执行很多次，避免反复编译”

推荐：
- `JvmScriptCompiler`
- `BasicJvmScriptEvaluator`
- `CompiledJvmScriptsCache`
- `CompiledScriptJarsCache`

## 1.5 如果你想“嵌入 Java ScriptEngine 体系”

推荐：
- `KotlinJsr223DefaultScriptEngineFactory`
- `Bindings`
- `Invocable`

---

# 2. 示例：定义一个最小自定义脚本类型

下面示例展示一个最常见的规则脚本模板。

```kotlin
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

object RuleScriptCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        "java.time.Instant",
        "java.math.BigDecimal"
    )

    implicitReceivers(RuleContext::class)

    providedProperties(
        mapOf(
            "env" to kotlin.script.experimental.api.KotlinType(Map::class),
            "requestId" to kotlin.script.experimental.api.KotlinType(String::class)
        )
    )

    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
})

@KotlinScript(
    displayName = "Rule Script",
    fileExtension = "rule.kts",
    compilationConfiguration = RuleScriptCompilationConfiguration::class
)
abstract class RuleScript
```

## 2.1 这个模板实现了什么

- 脚本扩展名是 `rule.kts`
- 脚本默认 import 了常用类型
- 脚本可以把 `RuleContext` 当作隐式 receiver 使用
- 脚本可以直接访问 `env` 和 `requestId`
- 编译时自动继承当前运行环境 classpath

## 2.2 适合什么场景

- 风控规则
- 审批规则
- 定价规则
- 营销规则

---

# 3. 示例：定义宿主上下文对象

```kotlin
data class RuleContext(
    val userId: String,
    val amount: Long,
    val tags: Set<String>
) {
    fun hasTag(tag: String): Boolean = tag in tags
    fun isLargeOrder(): Boolean = amount >= 100_000
}
```

脚本中就可以直接写：

```kotlin
isLargeOrder() && hasTag("vip")
```

因为它作为 `implicitReceivers` 注入了。

---

# 4. 示例：用 BasicJvmScriptingHost 执行脚本

```kotlin
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

val host = BasicJvmScriptingHost()

val scriptText = """
    isLargeOrder() && requestId.startsWith("REQ-")
""".trimIndent()

val scriptSource = scriptText.toScriptSource("sample.rule.kts")

val result = host.evalWithTemplate<RuleScript>(
    script = scriptSource,
    evaluation = {
        implicitReceivers(RuleContext(userId = "u-1", amount = 120_000, tags = setOf("vip")))
        providedProperties(
            mapOf(
                "env" to mapOf("region" to "cn"),
                "requestId" to "REQ-10001"
            )
        )
    }
)

when (result) {
    is kotlin.script.experimental.api.ResultWithDiagnostics.Success -> {
        when (val rv = result.value.returnValue) {
            is ResultValue.Value -> println("脚本结果: ${rv.value}")
            is ResultValue.Unit -> println("脚本返回 Unit")
            is ResultValue.Error -> println("脚本运行出错: ${rv.error}")
            is ResultValue.NotEvaluated -> println("脚本未执行")
        }
    }
    is kotlin.script.experimental.api.ResultWithDiagnostics.Failure -> {
        result.reports.forEach { println(it.render()) }
    }
}
```

## 4.1 这个示例体现了什么

- 通过模板直接构造脚本定义
- 通过 `implicitReceivers` 注入上下文对象
- 通过 `providedProperties` 注入普通变量
- 通过 `ResultWithDiagnostics` 统一处理成功与失败

---

# 5. 示例：使用编译与执行分离模式

如果你要反复执行同一份脚本，建议把编译与执行拆开。

```kotlin
import kotlin.script.experimental.jvmhost.JvmScriptCompiler
import kotlin.script.experimental.jvm.BasicJvmScriptEvaluator
import kotlin.script.experimental.api.ResultWithDiagnostics

val compiler = JvmScriptCompiler()
val evaluator = BasicJvmScriptEvaluator()

val compilationConfig = createJvmCompilationConfigurationFromTemplate<RuleScript>()

val compiled = compiler(scriptSource, compilationConfig)

if (compiled is ResultWithDiagnostics.Success) {
    val compiledScript = compiled.value

    val eval1 = evaluator(
        compiledScript,
        ScriptEvaluationConfiguration {
            implicitReceivers(RuleContext("u-1", 1000, setOf("normal")))
            providedProperties(mapOf("env" to emptyMap<String, Any>(), "requestId" to "REQ-1"))
        }
    )

    val eval2 = evaluator(
        compiledScript,
        ScriptEvaluationConfiguration {
            implicitReceivers(RuleContext("u-2", 200000, setOf("vip")))
            providedProperties(mapOf("env" to emptyMap<String, Any>(), "requestId" to "REQ-2"))
        }
    )
}
```

## 5.1 什么时候应该这样做

- 一份脚本会执行很多次
- 每次只是上下文不同
- 希望减少重复编译开销

---

# 6. 示例：用 scriptExecutionWrapper 做执行切面

```kotlin
val evaluationConfig = ScriptEvaluationConfiguration {
    scriptExecutionWrapper { block ->
        val start = System.nanoTime()
        try {
            block()
        } finally {
            val costMs = (System.nanoTime() - start) / 1_000_000
            println("脚本执行耗时: ${costMs}ms")
        }
    }
}
```

## 6.1 可以拿来做什么

- 计时
- 日志
- 审计
- 线程上下文绑定
- 安全检查外围控制

## 6.2 不适合做什么

- 不建议把真正的业务逻辑写进 wrapper
- wrapper 更适合“切面逻辑”而不是“业务逻辑”

---

# 7. 示例：用 Refine 机制按注解动态补依赖

下面是一个思路型示例，展示如何在脚本带某个注解时修正编译配置。

```kotlin
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class UseExtraLib

object DynamicCompilationConfiguration : ScriptCompilationConfiguration({
    refineConfiguration {
        onAnnotations(UseExtraLib::class) { context ->
            val extraClasspath = listOf(java.io.File("libs/extra-lib.jar"))
            ScriptCompilationConfiguration(context.compilationConfiguration) {
                jvm {
                    updateClasspath(extraClasspath)
                }
            }.asSuccess()
        }
    }
})
```

## 7.1 适合什么场景

- 根据脚本声明动态补依赖
- 根据注解决定默认 import
- 根据注解决定编译器选项

## 7.2 真实平台里更常见的用法

- `@DependsOn`
- `@Repository`
- 自定义业务注解

---

# 8. 示例：使用磁盘 jar 缓存

下面是思路型示例。

```kotlin
import java.io.File
import kotlin.script.experimental.jvmhost.CompiledScriptJarsCache

val cacheDir = File("build/script-cache").apply { mkdirs() }

val cache = CompiledScriptJarsCache { script, configuration ->
    val key = (script.locationId ?: script.text.hashCode().toString()) + "-" + configuration.hashCode()
    File(cacheDir, "$key.jar")
}
```

典型使用流程：

```kotlin
val cached = cache.get(scriptSource, compilationConfig)
val compiledScript = if (cached != null) {
    cached
} else {
    val compiled = compiler(scriptSource, compilationConfig).valueOrThrow()
    cache.store(compiled, scriptSource, compilationConfig)
    compiled
}
```

## 8.1 开发建议

缓存 key 至少要考虑：
- 脚本文本或脚本内容 hash
- 模板类型
- dependencies
- defaultImports
- compilerOptions
- K1/K2 或语言版本差异

否则很容易错命中。

---

# 9. 示例：保存预编译脚本为 jar

思路型用法：

```kotlin
import java.io.File
import kotlin.script.experimental.jvmhost.BasicJvmScriptJarGenerator

val jarGenerator = BasicJvmScriptJarGenerator(File("build/generated/my-script.jar"))
val compiledScript = compiler(scriptSource, compilationConfig).valueOrThrow()
jarGenerator(compiledScript, ScriptEvaluationConfiguration())
```

## 9.1 适合场景

- 预编译后分发
- 冷启动优化
- 构建系统集成

## 9.2 jar 中通常包含什么

- 脚本 class 文件
- 脚本 metadata
- manifest 中的 `Main-Class`
- manifest 中的 `Class-Path`

---

# 10. 示例：最小 JSR223 使用方式

在 Java 或 Kotlin 中都可以使用标准 `ScriptEngine`。

```kotlin
import javax.script.ScriptEngineManager

val engine = ScriptEngineManager().getEngineByExtension("kts")
engine.put("x", 10)
engine.put("y", 20)

val result = engine.eval("x + y")
println(result)
```

## 10.1 默认 JSR223 行为

当前默认模板会：
- 把 `Bindings` 自动导入为脚本变量
- 允许脚本内部继续 `eval(...)`
- 通过 REPL 基础设施维持执行历史

---

# 11. 示例：JSR223 调脚本函数

如果拿到的 engine 同时支持 `Invocable`：

```kotlin
import javax.script.Invocable
import javax.script.ScriptEngineManager

val engine = ScriptEngineManager().getEngineByExtension("kts")
engine.eval(
    """
    fun greet(name: String): String = "Hello, $name"
    """.trimIndent()
)

val invocable = engine as Invocable
val result = invocable.invokeFunction("greet", "Kotlin")
println(result)
```

## 11.1 还可以做什么

- `invokeMethod(...)`
- `getInterface(...)`

适合：
- 把脚本函数暴露成 Java 接口实现
- 从宿主直接回调脚本函数

---

# 12. 示例：如何选择 implicitReceivers 和 providedProperties

这是开发者最常问的问题之一。

## 12.1 什么时候用 `implicitReceivers`

适合：
- DSL 场景
- 希望脚本里直接调用宿主方法
- 希望语法更自然

例如：

```kotlin
class DeployDsl {
    fun image(value: String) {}
    fun replicas(count: Int) {}
}
```

脚本里可以直接写：

```kotlin
image("my-app:1.0")
replicas(3)
```

## 12.2 什么时候用 `providedProperties`

适合：
- 普通变量
- 更显式的数据输入
- 运行时上下文值

例如：

```kotlin
val env = mapOf("region" to "cn")
val now = Instant.now()
```

脚本中直接用：

```kotlin
println(env["region"])
println(now)
```

## 12.3 一个经验法则

- 想做 DSL，就优先 `implicitReceivers`
- 想传数据，就优先 `providedProperties`

---

# 13. 示例：最小模板 + Host 的推荐接入方式

如果你在业务里从零开始，我建议这样组织：

## 13.1 定义模板
- 定义 `@KotlinScript`
- 设置 `fileExtension`
- 设置默认编译配置

## 13.2 定义编译配置
- `baseClass`
- `defaultImports`
- `implicitReceivers`
- `providedProperties`
- `jvm.dependenciesFromCurrentContext(...)`

## 13.3 用 Host 执行
- `BasicJvmScriptingHost`
- `evalWithTemplate<T>`

## 13.4 再逐步加高级功能
- refine
- 缓存
- wrapper
- REPL
- JSR223

这条路径最稳，也最符合当前源码结构。

---

# 14. 常见问题

## 14.1 为什么脚本里看不到宿主类？

优先检查：
- `jvm.baseClassLoader`
- `dependencies`
- 是否真的把宿主 jar/class 目录加入脚本 classpath

## 14.2 为什么变量注入了，但脚本运行还是报错？

检查：
- 编译阶段是否声明了 `providedProperties`
- 执行阶段是否提供了同名值
- 值类型是否与编译期类型兼容

## 14.3 为什么 imported script 重复执行或状态不一致？

检查：
- 是否启用了 `scriptsInstancesSharing`
- imported scripts 路径是否实际指向同一脚本

## 14.4 为什么缓存 jar 命中了但又失效？

可能原因：
- manifest 里的依赖文件已不存在
- 脚本 metadata 无法恢复
- 缓存文件损坏

当前实现会倾向于：
- 认为缓存失效
- 删除坏缓存
- 重新编译

---

# 15. 总结

对开发者来说，最实用的落地路径是：

1. **先做模板**：定义脚本类型
2. **再做配置**：编译期和执行期明确分离
3. **用 Host 跑通最小链路**
4. **再加缓存、Refine、JSR223、REPL 等高级能力**

如果你把这套系统理解成一个“可扩展 Kotlin 脚本平台框架”，那：
- 模板决定脚本长什么样
- 编译配置决定脚本能写什么
- 执行配置决定脚本拿到什么
- Host 决定如何跑起来
- JVM/JSR223 决定它如何进入真实生产环境
