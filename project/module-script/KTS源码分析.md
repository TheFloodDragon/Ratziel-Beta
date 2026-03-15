# Kotlin Scripting 超细全量源码分析文档

> 分析范围：`libraries/scripting/` 全部模块、源码、API、测试与 testData。
>
> 分析目标：不是“模块介绍”，而是尽可能贴近源码实现的开发者手册，覆盖类型系统、配置 DSL、执行链路、类加载行为、结果模型、诊断机制、扩展点、测试覆盖与应用场景。

---

# 1. 总体定位

`libraries/scripting/` 是 Kotlin 脚本能力在标准编译器之外的核心运行与集成实现集合。它解决的不是单一“脚本能不能跑”，而是一整套问题：

- 脚本如何定义
- 脚本如何配置
- 脚本如何编译
- 脚本如何执行
- 脚本如何在 REPL 中增量运行
- 脚本如何动态解析依赖
- 脚本如何作为 JSR223 引擎接入 Java
- 脚本如何被 IDE 感知与支持
- 脚本系统如何以 diagnostics 方式输出结构化错误

从架构上，它可分成以下几层：

1. `common`：抽象协议层，定义 API 与配置模型
2. `jvm`：JVM 平台实现层，处理类加载、执行器、JVM 配置等
3. `jvm-host`：宿主封装层，面向应用使用者提供更完整的 JVM host 能力
4. `jsr223`：Java 标准脚本引擎接入层
5. `dependencies` / `dependencies-maven`：依赖解析层
6. `intellij`：IDE 集成支持层
7. `*-test` 与各模块 `test/`：协议、行为与集成回归验证层

---

# 2. common 模块超细分析

`libraries/scripting/common` 是整个脚本体系的根。其核心思想是：

- 用 **PropertiesCollection 驱动的类型化配置系统** 表达脚本世界的一切配置
- 用 **ResultWithDiagnostics** 作为统一返回协议，而不是简单抛异常
- 用 **ScriptCompiler / ScriptEvaluator / Host** 形成可替换的编译-执行编排模型
- 用 **Refine callbacks** 支持按注解、上下文、阶段动态细化配置

以下进入逐文件级分析。

---

## 2.1 `common/src/kotlin/script/experimental/api/scriptCompilation.kt`

这是脚本编译配置与编译协议的核心文件。

## 2.1.1 核心职责

该文件定义了：

- `ScriptCompilationConfiguration`
- 编译配置 DSL 的全部关键 key
- 配置 refine 机制
- `ScriptCompiler`
- `CompiledScript`

它是所有脚本编译实现的“协议起点”。任何平台实现都必须遵守这份契约。

---

## 2.1.2 `ScriptCompilationConfigurationKeys`

这是一个 marker interface，本身不承载逻辑，但作为类型化配置 key 的命名宿主。

设计动机：
- 把编译配置 key 与其他配置 key 隔离
- 让 DSL 调用点具备明确的作用域

---

## 2.1.3 `ScriptCompilationConfiguration`

```kotlin
open class ScriptCompilationConfiguration(...): PropertiesCollection(...)
```

### 作用
它是脚本“编译期配置容器”。

### 结构特征
- 继承自 `PropertiesCollection`
- 支持：
  - 空配置构造
  - 基于多个 base configuration 叠加构造
  - Builder DSL 构建
- `companion object : ScriptCompilationConfigurationKeys`
  - 作为 key anchor 使用
- `object Default`
  - 默认空编译配置单例

### 设计思想
这不是一个普通 data class，而是**可继承、可组合、可惰性默认、可按 key 类型安全访问**的配置对象。

### `toString`
返回 `ScriptCompilationConfiguration($properties)`，方便诊断和调试。

---

## 2.1.4 `with(body)`

```kotlin
fun ScriptCompilationConfiguration?.with(...): ScriptCompilationConfiguration
```

### 作用
在已有配置上追加 DSL 变更。

### 特点
- 如果 receiver 为 `null`，直接构造新配置
- 如果 `body` 没带来变化，则返回原对象
- 这是一个**优化型扩展**：避免无谓创建新配置实例

### 应用场景
- 在 refine 阶段追加 classpath
- 在 host 中注入 hostConfiguration
- 在模板转换结果上叠加业务自定义选项

---

## 2.1.5 编译配置 key 全解析

### `displayName`
脚本类型展示名称。

作用：
- IDE 展示
- 日志/调试识别

### `defaultIdentifier`
默认生成的脚本类名，默认值为 `"Script"`。

作用：
- 当源码没有其他命名依据时作为生成类标识
- 会影响诊断和缓存 key 的可读性

### `fileExtension`
脚本文件扩展名，默认 `kts`。

意义极大：
- 脚本定义发现时的首要筛选条件
- IDE 文件类型绑定
- 多脚本定义并存时的第一层路由规则

### `filePathPattern`
路径级正则，用于在扩展名之外进一步区分脚本类型。

典型场景：
- 同为 `.kts`，但某些路径下应使用不同 definition
- 大型工程中 Gradle、业务 DSL、工具脚本共存

### `fileNamePattern`
已废弃，仅为历史兼容保留。

### `baseClass`
脚本生成类的超类，默认 `Any`。

作用：
- 决定脚本实例构造签名
- 决定脚本内部可用成员
- 是模板化脚本能力的关键承载点

### `implicitReceivers`
隐式接收者类型列表，按由外到内顺序排列。

语义上等价于：
```kotlin
with(receiver1) {
  with(receiver2) {
    // script body
  }
}
```

非常关键：
- 这是脚本 DSL 体验核心之一
- 编译阶段仅声明类型
- 执行阶段必须传入对应实例

### `providedProperties`
外部注入属性的名字到类型映射。

与 `implicitReceivers` 的区别：
- receiver 是隐式成员作用域
- providedProperties 更像显式变量绑定

### `scriptFileLocationVariable`
将脚本文件位置注入为脚本中的某个变量名。

### `scriptFileLocation`
脚本文件的物理位置 `File`。

用途：
- `@file:Import` 的相对路径解析
- 脚本内引用当前文件目录
- diagnostics 显示

### `defaultImports`
默认隐式 import 列表。

用途：
- 提供更 DSL 化的脚本体验
- 减少用户脚本样板代码

### `importScripts`
随当前脚本一起编译并导入的脚本源列表。

作用：
- 形成脚本模块/脚本依赖链
- 后续 evaluator 会先执行 `otherScripts`

### `resultField`
生成脚本类中保存结果的字段名，默认 `$$result`。

意义：
- REPL 中尤其关键
- evaluator 通过反射读取此字段作为“脚本最后表达式结果”

### `dependencies`
平台特定依赖列表。

在 JVM 平台上通常是 `JvmDependency`。

### `compilerOptions`
直接传给底层编译器的命令行风格选项列表。

用途：
- 语言特性开关
- 编译行为微调

### `refineConfigurationBeforeParsing`
解析前 refine 回调。

意义：
- 在真正 parse 脚本前根据上下文先修正配置
- 适合注入环境数据、路径、预配置等

### `refineConfigurationOnAnnotations`
解析出文件级注解后触发的 refine 回调。

极其关键：
- `@DependsOn`
- `@Repository`
- 自定义注解驱动配置扩展

### `refineConfigurationBeforeCompiling`
编译前最后一道 refine 回调。

常用于：
- 在注解解析结束后再最终计算 classpath
- 合并动态依赖

### `sourceFragments`
指定只编译源码的某些片段。

适用：
- 增量分析
- IDE 片段编译
- refine 回调中的特殊切片编译

### `hostConfiguration`
与脚本编译相关的宿主配置。

重要性：
- 这是 common 与 host/jvm-host 的桥
- 某些类加载、上下文、脚本类型解析依赖它

### `isStandalone`
是否始终将脚本视作 standalone，默认 `true`。

意义：
- 决定与其他源码联合编译时的行为
- Gradle 特例会被设置为 `false`

---

## 2.1.6 `refineConfiguration` DSL

```kotlin
val ScriptCompilationConfiguration.Builder.refineConfiguration get() = RefineConfigurationBuilder()
```

它是编译配置 DSL 中最重要的高级扩展之一。

### `beforeParsing(handler)`
在解析脚本前执行。

### `onAnnotations(...)`
在脚本文件注解被解析后执行。
支持：
- `List<KotlinType>`
- `vararg KotlinType`
- `reified Annotation`
- `KClass<out Annotation>`
- `Iterable<KClass<out Annotation>>`

说明这个 API 特别照顾 DSL 书写体验。

### `beforeCompiling(handler)`
真正编译前执行。

---

## 2.1.7 refine handler 类型

### `RefineScriptCompilationConfigurationHandler`
```kotlin
(ScriptConfigurationRefinementContext) -> ResultWithDiagnostics<ScriptCompilationConfiguration>
```

它不是直接返回配置，而是返回带 diagnostics 的结果。

设计价值：
- refine 过程中也能报告 warning/error
- 不强制通过异常表达失败

### `SimpleRefineScriptCompilationConfigurationHandler`
简化版本，不带 diagnostics。

---

## 2.1.8 refine 数据结构

### `RefineConfigurationUnconditionallyData`
封装无条件触发 handler。

### `RefineConfigurationOnAnnotationsData`
封装“针对哪些注解”+ handler。

二者都实现 `Serializable`，说明配置对象在某些场景下可能被序列化传递或缓存。

---

## 2.1.9 refine 执行函数

### `refineBeforeParsing(script, collectedData)`
调用 `simpleRefineImpl`，把当前 script/config/collectedData 包装进 `ScriptConfigurationRefinementContext`。

### `refineOnAnnotations(script, collectedData)`
通过 `refineOnAnnotationsWithLazyDataCollection` 实现。

关键点：
- collectedData 延迟包装为 success
- 适合 annotation-driven refine 流水线

### `refineBeforeCompiling(script, collectedData)`
与 `beforeParsing` 类似，但触发时机更晚。

---

## 2.1.10 `ScriptCompiler`

```kotlin
interface ScriptCompiler {
    suspend operator fun invoke(script, config): ResultWithDiagnostics<CompiledScript>
}
```

特点：
- suspend 接口
- 输入为 `SourceCode + ScriptCompilationConfiguration`
- 输出为 `CompiledScript`

说明：
- 编译行为被抽象为异步友好的纯协议
- common 不关心具体是 JVM、JS 还是 Native，只要求“给我 CompiledScript”

---

## 2.1.11 `CompiledScript`

关键成员：

### `sourceLocationId`
默认 `null`。

用途：
- diagnostics 回传定位
- 缓存/日志可读性

### `compilationConfiguration`
编译时使用的真实配置。

### `getClass(scriptEvaluationConfiguration)`
加载编译产物脚本类。

特点：
- 是 suspend
- 说明类加载本身也可能依赖配置、缓存或外部资源

### `otherScripts`
默认空列表。

表示：
- 与当前脚本同模块编译的其他脚本
- imported/included scripts

### `resultField`
脚本结果字段的名称和类型。

---

## 2.1.12 本文件在整体中的地位总结

这是 Kotlin scripting 编译世界的“核心法典”。

你若想：
- 自定义脚本模板
- 增加依赖注解
- 动态增补 classpath
- 做缓存 key
- 理解 imported script 执行链

都必须先吃透这个文件。

---

## 2.2 `common/src/kotlin/script/experimental/api/scriptEvaluation.kt`

该文件定义执行配置和执行结果协议，是 `scriptCompilation.kt` 的对应面。

## 2.2.1 核心职责

- 定义 `ScriptEvaluationConfiguration`
- 定义执行 refine 机制
- 定义 `ResultValue` / `EvaluationResult`
- 定义 `ScriptEvaluator`
- 定义脚本执行包装器

---

## 2.2.2 `ScriptEvaluationConfiguration`

与 `ScriptCompilationConfiguration` 对称，也是基于 `PropertiesCollection` 的配置容器。

特点：
- 支持 base configurations 叠加
- 有 `Default` 单例
- 使用 `with { }` 做增量构建

这个配置不控制“怎么编译”，而控制“怎么执行”。

---

## 2.2.3 执行配置 key 全解析

### `implicitReceivers`
实际 receiver 对象列表，顺序必须与编译配置中的 receiver 类型声明一致。

### `providedProperties`
实际属性值映射，需与编译期声明类型匹配。

### `compilationConfiguration`
回链到真实编译配置。

意义：
- evaluator/refine 阶段可读取编译期上下文
- 某些平台行为依赖编译选项判断

### `constructorArgs`
脚本基类构造器的额外参数。

它不同于 receiver/providedProperties，是模板基类 constructor 显式需要的附加参数。

### `previousSnippets`
REPL 历史片段对象列表。

重要：
- 第一段也应显式传空列表
- evaluator 会把这些历史片段转成数组传给当前 snippet 构造器

### `scriptsInstancesSharing`
是否共享导入脚本实例，默认 `false`。

意义：
- 当多个 import path 指向同一脚本时，可避免重复实例化
- 对模块脚本/共享状态型脚本很重要

### `hostConfiguration`
执行相关宿主配置。

### `refineConfigurationBeforeEvaluate`
执行前 refine 回调列表。

### `scriptExecutionWrapper`
执行包装器。

适合：
- 埋点
- 限时
- 权限边界
- ThreadLocal / MDC 注入
- 自定义异常处理

---

## 2.2.4 辅助 DSL

### `scriptExecutionWrapper(wrapper: (() -> T) -> T)`
允许直接传 lambda，而不手写 `ScriptExecutionWrapper` 实现。

### `enableScriptsInstancesSharing()`
一键启用共享实例。

### `refineConfigurationBeforeEvaluate(handler)`
把 handler 加入 refine 列表。

---

## 2.2.5 执行 refine

### `RefineScriptEvaluationConfigurationHandler`
```kotlin
(ScriptEvaluationConfigurationRefinementContext) -> ResultWithDiagnostics<ScriptEvaluationConfiguration>
```

### `refineBeforeEvaluation(script, contextData)`
执行逻辑：
1. 从 `hostConfiguration` 取 `getEvaluationContext`
2. 生成 host 级 contextData
3. 与显式传入 contextData 合并
4. 用 `simpleRefineImpl` 依次跑 handler

说明：
- 执行 refine 支持“宿主上下文”参与
- 能在最后一刻按运行时环境修正执行参数

---

## 2.2.6 `ResultValue` 全解析

这是脚本执行结果的统一模型。

### `ResultValue.Value`
表示脚本有值结果。

字段：
- `name`：结果字段名
- `value`：实际值
- `type`：类型名
- `scriptClass`
- `scriptInstance`

### `ResultValue.Unit`
表示脚本结果为 Unit。

### `ResultValue.Error`
表示脚本体执行时抛出的异常。

注意：
- 它不是编译失败，而是“脚本已执行，但执行结果是异常”
- 保留 `wrappingException`，通常是 `InvocationTargetException`

### `ResultValue.NotEvaluated`
用于某些不真正执行脚本的 evaluator。

---

## 2.2.7 `EvaluationResult`

```kotlin
data class EvaluationResult(val returnValue: ResultValue, val configuration: ScriptEvaluationConfiguration?)
```

它不仅返回值，还绑定“最终执行所用配置”。

这非常重要：
- refine 之后的真实配置可被上层继续读取
- 对调试和多阶段执行很关键

---

## 2.2.8 `ScriptEvaluator`

```kotlin
interface ScriptEvaluator {
    suspend operator fun invoke(compiledScript, scriptEvaluationConfiguration): ResultWithDiagnostics<EvaluationResult>
}
```

这是执行层的统一入口。

---

## 2.2.9 本文件地位总结

这个文件定义了“脚本被执行时，世界是什么样子”。

如果编译配置定义了“语言与约束”，那执行配置定义的是“上下文与实例化方式”。

---

## 2.3 `common/src/kotlin/script/experimental/api/scriptData.kt`

该文件定义脚本输入与中间上下文数据模型。

## 2.3.1 `SourceCode`

这是脚本源码抽象，而不是简单字符串。

成员：
- `text`
- `name`
- `locationId`

### 设计意义
统一来自：
- 文件
- 内存字符串
- IDE buffer
- 外部 URL 资源

### 嵌套类型
- `Position(line, col, absolutePos)`
- `Range(start, end)`
- `Location(start, end?)`
- `LocationWithId(codeLocationId, locationInText)`

这些类型是 diagnostics 精确定位基础。

---

## 2.3.2 `ScriptSourceAnnotation`

表示脚本解析时发现的文件级注解及其位置。

用途：
- `@DependsOn`
- `@Repository`
- 自定义注解驱动 refine
- IDE 高亮/跳转

---

## 2.3.3 `ExternalSourceCode`

扩展 `SourceCode`，增加 `externalLocation: URL`。

适合：
- 从 URL 拉取脚本
- 远程存储脚本

---

## 2.3.4 `ScriptSourceNamedFragment`

表示带名字的源码片段范围。

场景：
- 增量编译
- 片段求值
- IDE / partial analysis

---

## 2.3.5 `ScriptDependency`

平台无关依赖抽象接口。

注意它故意极薄：真正的依赖载体放到平台实现里，例如 JVM 的 `JvmDependency`。

---

## 2.3.6 `ScriptCollectedData`

解析期间收集的数据容器，用于传递给编译 refine 回调。

### `foundAnnotations`
旧字段，已废弃，仅保留兼容。

### `collectedAnnotations`
新字段，包含注解及位置信息。

---

## 2.3.7 `ScriptConfigurationRefinementContext`

编译 refine 的上下文对象：
- `script`
- `compilationConfiguration`
- `collectedData`

---

## 2.3.8 `ScriptEvaluationContextData`

执行上下文数据容器，也是 `PropertiesCollection`。

用于把宿主运行时环境信息送入 evaluation refine。

### `merge(vararg contexts)`
优化型合并函数：
- 全空 -> null
- 单个非空 -> 直接返回
- 多个 -> 组合成新上下文

### `commandLineArgs`
当前进程命令行参数。

适合脚本运行时使用。

---

## 2.3.9 `ScriptEvaluationConfigurationRefinementContext`

执行 refine 的上下文：
- `compiledScript`
- `evaluationConfiguration`
- `contextData`

---

## 2.3.10 本文件地位总结

它定义了“脚本是什么、脚本注解是什么、编译/执行 refine 能看到什么上下文”。

没有它，整个 scripting API 就会退化成字符串输入 + 黑盒输出，无法支持 IDE、诊断和高级扩展。

---

## 2.4 `common/src/kotlin/script/experimental/api/errorHandling.kt`

这是整个脚本体系最关键的基础设施文件之一。

## 2.4.1 设计核心

Kotlin scripting 没有把“异常”作为唯一错误表达机制，而是统一采用：

- `ScriptDiagnostic`
- `ResultWithDiagnostics<T>`

这样做的好处：
- 可同时返回结果和 warning
- 可保留失败上下文
- 便于 IDE/CLI/服务端统一消费

---

## 2.4.2 `ScriptDiagnostic`

字段：
- `code`
- `message`
- `severity`
- `sourcePath`
- `location`
- `exception`

### `Severity`
- `DEBUG`
- `INFO`
- `WARNING`
- `ERROR`
- `FATAL`

### 常量 code
- `unspecifiedInfo = 0`
- `unspecifiedError = -1`
- `unspecifiedException = -2`
- `incompleteCode = -3`

说明：
- 既支持结构化编码，也兼容“临时/未知错误”

### `render(...)`
可控制是否输出：
- severity
- location
- exception
- stacktrace

这是 CLI/日志显示的直接基础。

### `isError()`
判断是否是默认错误/异常级别的真实错误。

---

## 2.4.3 `ResultWithDiagnostics<R>`

两种状态：
- `Success(value, reports)`
- `Failure(reports)`

注意：
- success 也可以带 reports
- 这允许“成功但带 warning/info”

---

## 2.4.4 组合操作

### `onSuccess`
若成功则执行下一段并合并 diagnostics。

这是 host/evaluator 中极常见的链式调用基础。

### `mapSuccess` / `mapNotNullSuccess` / `flatMapSuccess`
对集合执行“带 diagnostics 的映射”，并在过程中累积报告。

应用：
- 多 imported scripts 逐个执行
- 多依赖逐个解析

### `onFailure`
失败时执行副作用逻辑。

### `operator List<ScriptDiagnostic>.plus(result)`
把已有 reports 合并进另一个结果。

---

## 2.4.5 构造辅助

### `asSuccess`
把值包装成成功结果。

### `makeFailureResult(...)`
多种重载，快速构造失败结果。

### `Throwable.asDiagnostics(...)`
把异常转成结构化 diagnostic。

### `String.asErrorDiagnostics(...)`
把纯文本消息转成 error diagnostic。

---

## 2.4.6 取值辅助

### `valueOrNull()`
失败则返回 null。

### `valueOr { ... }`
失败则执行非返回 lambda。

### `valueOrThrow()`
失败则抛 `RuntimeException`，并把 diagnostics 拼成消息。

这是与“传统异常式调用方”兼容的重要桥。

---

## 2.4.7 `IterableResultsCollector`

这是一个“小而实用”的聚合器。

设计语义：
- 收集多个 `ResultWithDiagnostics<Iterable<T>>`
- 只要有值，就尽量返回 success
- 若完全没有值，则返回 failure

这比“只要一失败就全失败”的策略更宽容，适合 resolver 组合场景。

### `asSuccessIfAny()`
对多个 result 做宽松聚合。

---

## 2.4.8 本文件地位总结

如果说 `PropertiesCollection` 是配置系统基石，
那么 `errorHandling.kt` 就是 scripting 体系的“控制流基石”。

大量源码之所以看起来清晰，正是因为“成功/失败 + reports”模型被统一了。

---

## 2.5 `common/src/kotlin/script/experimental/host/BasicScriptingHost.kt`

这是脚本 Host 的最小基础编排器。

## 2.5.1 类结构

```kotlin
abstract class BasicScriptingHost(
    val compiler: ScriptCompiler,
    val evaluator: ScriptEvaluator
)
```

Host 的本质不是实现编译器或执行器，而是**编排它们**。

---

## 2.5.2 `runInCoroutineContext`

默认通过 `internalScriptingRunSuspend` 运行 suspend block。

设计点：
- Host 可覆盖这个方法，把执行放进自定义 coroutine context
- 例如 UI 线程、受控 dispatcher、隔离执行器

---

## 2.5.3 `eval(script, compilationConfiguration, evaluationConfiguration)`

逻辑很简洁：
1. 调用 compiler
2. 成功则调用 evaluator
3. 使用 `onSuccess` 自动合并 diagnostics

这是 scripting host 的标准调用模板。

意义：
- common 层不关心 JVM 细节
- 但已经规定了编译与执行的标准串联方式

---

## 2.5.4 本文件总结

`BasicScriptingHost` 是“把编译器和执行器粘起来”的那层胶水。

看似简单，实际是整个宿主 API 的基准模型。

---

## 2.6 `common/src/kotlin/script/experimental/host/configurationFromTemplate.kt`

这是模板脚本系统的核心转换器。

## 2.6.1 核心职责

- 从 `@KotlinScript` 标注的模板类构造脚本定义
- 推导 compilation/evaluation/host configuration
- 处理模板类加载与默认值继承

它是“声明式模板 -> 可执行配置”的桥。

---

## 2.6.2 `ScriptDefinition`

```kotlin
data class ScriptDefinition(
    val compilationConfiguration: ScriptCompilationConfiguration,
    val evaluationConfiguration: ScriptEvaluationConfiguration
)
```

这是模板解析后的成品定义。

---

## 2.6.3 `createScriptDefinitionFromTemplate(...)`

输入：
- `baseClassType`
- `baseHostConfiguration`
- `contextClass`
- 编译/执行附加 body

流程：
1. 用 `getTemplateClass` 找到模板类
2. 读取模板类上的 `@KotlinScript`
3. 构造 hostConfiguration
4. 构造 compilationConfiguration
5. 构造 evaluationConfiguration
6. 返回 `ScriptDefinition`

这是最推荐的入口，因为它保证编译/执行定义来自同一模板上下文。

---

## 2.6.4 `createCompilationConfigurationFromTemplate` / `createEvaluationConfigurationFromTemplate`

是单独构造各自配置的变体，但源码中也明确说明更推荐整体构造。

---

## 2.6.5 `constructCompilationConfiguration`

步骤：
1. 从注解参数拿到 `compilationConfiguration` KClass
2. 通过 `scriptConfigInstance` 反射实例化配置对象
3. 在其基础上创建新 `ScriptCompilationConfiguration`
4. 用模板元数据覆盖默认 key：
   - hostConfiguration
   - baseClass
   - fileExtension
   - filePathPattern
   - displayName
   - isStandalone（Gradle 特例）
5. 执行调用方传入 body

异常语义很明确：
- 若注解里配置类既不是 object，也不是可无参构造实例化的类，则抛 `IllegalArgumentException`

---

## 2.6.6 `constructEvaluationConfiguration`

逻辑与 compilation 类似，只是应用到 evaluation 配置上。

---

## 2.6.7 `constructHostConfiguration`

是这里最值得注意的部分之一。

逻辑：
1. 如果注解没指定 hostConfiguration 子类，则直接基于 baseHostConfiguration 构造新配置
2. 否则优先尝试寻找“接受 `ScriptingHostConfiguration` 作为第一个参数”的构造器
3. 若找不到，则尝试 objectInstance 或无参构造
4. 最后 `withDefaultsFrom(baseHostConfiguration)`

意义：
- Host 配置支持继承与扩展
- 模板注解可以携带自定义 host 配置类

---

## 2.6.8 `propertiesFromTemplate(...)`

它把模板元数据映射到编译配置。

关键逻辑：
- `baseClass.replaceOnlyDefault(...)`
- `fileExtension.replaceOnlyDefault(...)`
- Gradle 脚本扩展名特例：`isStandalone(false)`
- `fileNamePattern` / `filePathPattern`
- `displayName`

这个函数说明：**模板配置不会强行覆盖调用方已显式设置的值，只替换默认值。**

这是非常谨慎且合理的设计。

---

## 2.6.9 `kotlinScriptAnnotation`

从模板类上查找 `@KotlinScript`。

如果找不到，还会对历史标准模板类名做兜底：
- `SimpleScriptTemplate`
- `ScriptTemplateWithArgs`
- `ScriptTemplateWithBindings`

若仍没有，则抛异常。

这体现了新老脚本 API 迁移兼容性。

---

## 2.6.10 `KotlinType.getTemplateClass(...)`

通过 `hostConfiguration[ScriptingHostConfiguration.getScriptingClass]` 完成实际类加载。

说明：
- common 不直接负责类加载策略
- 这件事委托给 hostConfiguration

这是一个非常漂亮的解耦点。

---

## 2.6.11 `scriptConfigInstance(kclass)`

优先：
- `objectInstance`
- 无参构造器

这是 annotation 中配置类实例化的通用辅助。

---

## 2.6.12 本文件总结

这几乎是“脚本模板机制”的大脑。

所有基于模板的自定义脚本类型，本质上最终都要经过它变成可执行配置。

---

## 2.7 `common/src/kotlin/script/experimental/host/hostConfiguration.kt`

这是宿主配置模型。

## 2.7.1 `ScriptingHostConfiguration`

和编译/执行配置一样，也是 `PropertiesCollection`。

说明：
- scripting 体系所有配置采用同一种抽象底座
- 学会一个，其他都一样

---

## 2.7.2 `with(body)` / `withDefaultsFrom(defaults)`

### `with`
增量构造

### `withDefaultsFrom`
只补缺失项，不覆盖显式设置

这在模板 + host + 调用方多层组合时非常关键。

---

## 2.7.3 宿主配置 key

### `configurationDependencies`
脚本配置类与 refine 回调所需依赖。

意义：
- 配置代码本身也可能依赖额外 classpath
- host 要能提供这些依赖

### `getScriptingClass`
通用“脚本类型解析器”。

签名：
```kotlin
(classType: KotlinType, contextClass: KClass<*>, hostConfiguration: ScriptingHostConfiguration) -> KClass<*>
```

这是 common 层解决“如何从 KotlinType 找到真实 KClass”的关键策略点。

### `getEvaluationContext`
从 hostConfiguration 生成 `ScriptEvaluationContextData`。

用于 evaluation refine。

---

## 2.7.4 辅助函数

### `getEvaluationContext(handler)` DSL
方便塞 lambda。

### `getScriptingClass(type, contextClass)`
便捷调用，若缺少 `getScriptingClass` 则直接报错。

---

## 2.7.5 本文件总结

如果 compilation/evaluation configuration 是“脚本自身配置”，
那么 hostConfiguration 表达的是“宿主世界给脚本系统提供什么能力”。

---

## 2.8 `common/src/kotlin/script/experimental/annotations/scriptAnnotations.kt`

这是脚本模板注解定义文件。

## 2.8.1 `@KotlinScript`

参数：
- `displayName`
- `fileExtension`
- `filePathPattern`
- `compilationConfiguration`
- `evaluationConfiguration`
- `hostConfiguration`

### 语义
被它标记的类可视为“脚本模板类”。

### 关键意义
模板类承担三件事：
1. 成为生成脚本类的基类
2. 携带脚本类型元信息
3. 绑定默认编译/执行/宿主配置

### 为什么这个注解重要
几乎所有自定义脚本 DSL，最终都要从这个注解出发。

---

# 3. JVM 模块初步超细分析

下面开始进入 `libraries/scripting/jvm`。这一层把 common 中的抽象协议落实到 JVM 平台。

---

## 3.1 `jvm/src/kotlin/script/experimental/jvm/jvmScriptCompilation.kt`

## 3.1.1 作用

为编译配置引入 JVM 平台特定 key 与依赖模型。

---

## 3.1.2 `JvmDependency`

```kotlin
data class JvmDependency(val classpath: List<File>) : ScriptDependency
```

这是 JVM 平台最常见的依赖表达。

语义：
- 一组 classpath 文件（jar/目录）
- 是 `ScriptCompilationConfiguration.dependencies` 中的一个平台化条目

---

## 3.1.3 `JvmDependencyFromClassLoader`

用一个函数从配置推导 classloader。

作用：
- 某些情况下 classpath 不是直接静态列表，而是通过 classloader 间接获取

---

## 3.1.4 `JsDependency`

虽然在 jvm 文件中出现，但它也是平台依赖抽象的一种简单表示，用 path 描述 JS 依赖。

---

## 3.1.5 `JvmScriptCompilationConfigurationBuilder`

为 JVM 编译配置提供 DSL scope。

---

## 3.1.6 `dependenciesFromClassContext(...)`

从某个 `KClass` 的 classloader 所在上下文提取 classpath。

适用：
- 让脚本自动继承某个 API/宿主模块所在 classpath

---

## 3.1.7 `dependenciesFromCurrentContext(...)`

从当前线程上下文 classloader 提取依赖。

参数：
- `libraries`
- `wholeClasspath`
- `unpackJarCollections`

适合：
- 宿主应用直接把自己运行时 classpath 暴露给脚本

---

## 3.1.8 `dependenciesFromClassloader(...)`

最终通用实现，调用 `scriptCompilationClasspathFromContext(...)` 再 `updateClasspath(...)`。

说明：
- classpath 发现与配置更新明确分离

---

## 3.1.9 `withUpdatedClasspath(classpath)`

在已有配置基础上追加新的 classpath，并自动去重。

这是运行时动态补依赖的常见入口。

---

## 3.1.10 `updateClasspathImpl`

内部逻辑：
1. 过滤掉空输入
2. 通过 `filterNewClasspath` 做去重
3. 追加 `JvmDependency(newClasspath)` 到 dependencies

### `filterNewClasspath`
从已有 `ScriptDependency` 中提取所有 `JvmDependency.classpath` 做去重。

意义：
- 避免重复 jar
- 降低类冲突与编译负担

---

## 3.1.11 JVM 编译 key

### `jdkHome`
从 hostConfiguration 的 JVM 配置复制而来。

### `jvmTarget`
目标字节码版本字符串。

---

## 3.1.12 本文件总结

它建立了一个非常清晰的边界：
- common 只知道 `dependencies`
- jvm 具体把它实现为 `JvmDependency(classpath)`

---

## 3.2 `jvm/src/kotlin/script/experimental/jvm/jvmScriptEvaluation.kt`

## 3.2.1 作用

为执行配置添加 JVM 专属行为控制项。

---

## 3.2.2 关键 key

### `baseClassLoader`
脚本类加载的父加载器。

默认值：
- 先取 `hostConfiguration.jvm.baseClassLoader`
- 否则取 `Thread.currentThread().contextClassLoader`

这直接决定脚本对宿主类可见性。

### `lastSnippetClassLoader`
REPL 增量执行时上一段 snippet 使用的类加载器。

### `loadDependencies`
执行前是否主动加载脚本依赖，默认 `true`。

若设为 `false`：
- 认为依赖已由 `baseClassLoader` 提供
- 可减少额外加载动作

### `mainArguments`
如果脚本通过 main 方法执行，则传给 main 的参数。

### internal: `actualClassLoader`
真实执行时准备好的 classloader。

### internal: `scriptsInstancesSharingMap`
共享脚本实例缓存容器。

---

## 3.2.3 本文件总结

它定义了 JVM 上“脚本怎么找到类、怎么共享实例、REPL 如何递进”的最核心执行参数。

---

## 3.3 `jvm/src/kotlin/script/experimental/jvm/BasicJvmScriptEvaluator.kt`

这是 JVM 平台最重要的执行器实现之一。

## 3.3.1 总体职责

将 `CompiledScript` 真正转成 JVM 类实例，并按脚本约定抽取执行结果。

---

## 3.3.2 `invoke(compiledScript, scriptEvaluationConfiguration)` 主流程

总体流程：
1. `compiledScript.getClass(config)` 获取脚本类
2. 准备共享配置
3. 若该脚本实例可共享且已有缓存，则直接返回缓存结果
4. 递归执行 `otherScripts`
5. `refineBeforeEvaluation(compiledScript)`
6. 真正调用构造器实例化脚本
7. 读取 resultField 或返回 Unit
8. 缓存 EvaluationResult
9. 返回 Success
10. 任何 Throwable 包装成 Failure diagnostics

这是 imported scripts / sharing / refine / classloader / result extraction 五条逻辑线的汇合点。

---

## 3.3.3 共享配置准备：`getOrPrepareShared(classLoader)`

若 evaluation config 中还没有 `actualClassLoader`：
- 写入 `actualClassLoader(classLoader)`
- 如果启用了 `scriptsInstancesSharing`，创建 `scriptsInstancesSharingMap`

目的：
- 同一组脚本模块共享一致的类加载/缓存上下文

---

## 3.3.4 imported scripts 递归执行

```kotlin
compiledScript.otherScripts.mapSuccess { invoke(it, configurationForOtherScripts) }
```

说明：
- imported scripts 会先于当前脚本执行
- 对 imported script 使用的配置会清掉 `previousSnippets`
- 避免 REPL 历史错误污染 imported scripts

这是一个很细但非常正确的处理。

---

## 3.3.5 执行前 refine

```kotlin
sharedConfiguration.with {
    compilationConfiguration(compiledScript.compilationConfiguration)
}.refineBeforeEvaluation(compiledScript)
```

这里把 compilation config 回填到 evaluation config 中，再执行 refine。

价值：
- 执行 refine 可读取编译信息
- 支持更智能的执行前动态配置

---

## 3.3.6 结果构造

如果 `compiledScript.resultField != null`：
- 反射读取 `scriptClass.java.getDeclaredField(resultFieldName)`
- 包装成 `ResultValue.Value`

否则：
- 返回 `ResultValue.Unit`

说明：
脚本的“返回值”本质上并不是普通函数 return，而是生成类上的结果字段。

---

## 3.3.7 `InvocationTargetException` 处理

若构造器调用抛 `InvocationTargetException`：
- 解包 `targetException`
- 返回 `ResultValue.Error`

这点非常关键：
- 脚本“执行出错”不一定表现为 Failure
- 可能是 Success(EvaluationResult(ResultValue.Error(...))) 或结合上层策略消费
- 这里保留 wrapping exception 以便更准确地处理栈信息

---

## 3.3.8 `evalWithConfigAndOtherScriptsResults(...)`

这是实际拼接构造参数并实例化脚本对象的关键函数。

### 参数拼接顺序
源码逻辑：
1. `previousSnippets` -> 以数组形式整体作为一个参数
2. `constructorArgs`
3. 如果是 K2 编译：先追加 `providedProperties`
4. `importedScriptsEvalResults` 中各脚本实例
5. `implicitReceivers`
6. 如果不是 K2 编译：最后再追加 `providedProperties`

### 极其重要的结论
**K2 与 K1/旧链路在 providedProperties 的构造参数位置不同。**

这正是为什么测试里专门有 `ConstructorArgumentOrderTest.kt`。

### 构造器选择
```kotlin
val ctor = java.constructors.single()
```

说明脚本生成类预期只有一个构造器。

### 执行包装器
若配置中存在 `scriptExecutionWrapper`：
- 用 wrapper 包裹 `ctor.newInstance(...)`

这是执行切面的唯一官方入口之一。

### contextClassLoader 切换
执行前：
```kotlin
Thread.currentThread().contextClassLoader = this.java.classLoader
```
执行后恢复。

意义：
- 让脚本运行时通过线程上下文类加载器访问到自己依赖的类
- 避免服务加载器/反射链找不到脚本类或其依赖

---

## 3.3.9 本文件总结

如果只能选一个文件来理解“JVM 脚本究竟怎么跑起来”，那就是它。

它集中了：
- 模块脚本执行顺序
- 参数绑定规则
- K1/K2 差异
- 实例共享
- wrapper 扩展点
- classloader 切换
- 结果字段提取

---

# 4. 当前阶段总结

本轮已经把以下关键核心文件纳入超细文档：

- `common/api/scriptCompilation.kt`
- `common/api/scriptEvaluation.kt`
- `common/api/scriptData.kt`
- `common/api/errorHandling.kt`
- `common/host/BasicScriptingHost.kt`
- `common/host/configurationFromTemplate.kt`
- `common/host/hostConfiguration.kt`
- `common/annotations/scriptAnnotations.kt`
- `jvm/jvmScriptCompilation.kt`
- `jvm/jvmScriptEvaluation.kt`
- `jvm/BasicJvmScriptEvaluator.kt`

后续还需要继续补完：
- common 其余 util/impl/repl/ide 配置文件
- jvm 其余 evaluator/caching/util/impl/runner
- jvm-host 全部
- jsr223 全部
- dependencies / dependencies-maven 全部
- intellij 全部
- 全部测试与 testData 场景映射

本文件将持续扩写为最终单文件全量文档。

---

# 5. JVM 模块继续深挖

下面补充 `libraries/scripting/jvm` 中剩余关键文件，重点覆盖 REPL、缓存、宿主 JVM 配置、classpath 提取、编译产物表示与运行入口。

---

## 5.1 `jvm/src/kotlin/script/experimental/jvm/BasicJvmReplEvaluator.kt`

这是 JVM REPL 执行器，负责把“按历史链接的已编译 snippet”变成“按历史链接的已执行 snippet”。

### 5.1.1 类型与职责

```kotlin
class BasicJvmReplEvaluator(val scriptEvaluator: ScriptEvaluator = BasicJvmScriptEvaluator())
    : ReplEvaluator<CompiledSnippet, KJvmEvaluatedSnippet>
```

它不是直接执行源码，而是执行 **已经完成编译并且带有链式历史关系** 的 `CompiledSnippet`。

### 5.1.2 核心状态

- `lastEvaluatedSnippet: LinkedSnippetImpl<KJvmEvaluatedSnippet>?`
  - 当前 REPL 已执行历史链尾节点
- `history = SnippetsHistory<KClass<*>?, Any?>()`
  - 保存“脚本类 / 脚本实例”的历史

这说明 REPL evaluator 维护两套历史：
1. 结构化链表历史（`LinkedSnippet`）
2. 快速取值型历史（`SnippetsHistory`）

### 5.1.3 `eval(snippet, configuration)` 主流程

1. 先执行 `verifyHistoryConsistency(snippet)`
2. 从历史中拿到：
   - `lastSnippetClass`
   - `historyBeforeSnippet`（所有历史脚本实例）
3. 构造新的 `ScriptEvaluationConfiguration`
   - `previousSnippets.put(historyBeforeSnippet)`
   - 若存在 `lastSnippetClass`，设置 `jvm.lastSnippetClassLoader(lastSnippetClass.java.classLoader)`
4. 调用底层 `scriptEvaluator` 执行当前 snippet
5. 若成功：
   - 根据 `ResultValue` 更新 `history`
   - 构造 `KJvmEvaluatedSnippet`
6. 若失败：
   - 从 diagnostics 找第一个 error
   - 若存在异常则包装为 `ResultValue.Error`，否则 `ResultValue.NotEvaluated`
7. 把结果追加到 `lastEvaluatedSnippet` 链表
8. 返回新节点并携带原 reports

### 5.1.4 `verifyHistoryConsistency`

这个函数非常关键，防止 REPL 历史错位。

校验逻辑：
- 从待执行 compiled snippet 的 previous 链向前走
- 与 `lastEvaluatedSnippet` 的 previous 链逐个比对
- 要求：
  - `evaluatedVal.compiledSnippet === compiled.get()`
  - `evaluatedVal.result.scriptClass != null`
- 最后两边必须同时走到 null

### 5.1.5 设计意义

这说明 REPL 不是“只看最后一个值”，而是要求：
- 编译历史链与执行历史链严格一致
- 任何历史错位都会导致当前 snippet 失效

这能防止：
- 重新编译某段后拿旧执行结果接着跑
- 某个历史段执行失败但仍继续后续求值

### 5.1.6 `KJvmEvaluatedSnippet`

它是 JVM REPL 的 evaluated snippet 实现，持有：
- `compiledSnippet`
- `configuration`
- `result`

### 5.1.7 本文件结论

`BasicJvmReplEvaluator` 解决的是 REPL 最难的一部分之一：
- 历史一致性
- 前序 snippet 实例传递
- classloader 递进
- 执行失败后的可恢复表示

---

## 5.2 `jvm/src/kotlin/script/experimental/jvm/jvmScriptCaching.kt`

这个文件定义 JVM 编译缓存接口，是宿主性能优化的重要入口。

### 5.2.1 `CompiledJvmScriptsCache`

```kotlin
interface CompiledJvmScriptsCache {
    fun get(script: SourceCode, scriptCompilationConfiguration: ScriptCompilationConfiguration): CompiledScript?
    fun store(compiledScript: CompiledScript, script: SourceCode, scriptCompilationConfiguration: ScriptCompilationConfiguration)
}
```

这是一个极简缓存契约。

### 5.2.2 设计含义

缓存键并不由接口固定，而是由实现者根据：
- `SourceCode`
- `ScriptCompilationConfiguration`
自行决定

这非常合理，因为不同平台/宿主对“缓存命中”的定义不同。

### 5.2.3 `NoCache`

空实现，不缓存。

作用：
- 提供禁用缓存的标准实现
- 避免调用方频繁判空

### 5.2.4 host 配置 key

- `compilationCache`
- `disableCompilationCache`

说明缓存是 **host 级特性**，而不是脚本源码级特性。

### 5.2.5 风险点

缓存实现若设计不当，最容易出错在：
- 忽略编译配置差异
- 忽略模板/默认 imports 差异
- 忽略语言版本/K1/K2 差异
- 忽略 imported scripts 改变

---

## 5.3 `jvm/src/kotlin/script/experimental/jvm/jvmScriptingHostConfiguration.kt`

这个文件负责把 hostConfiguration 具体化到 JVM 平台。

### 5.3.1 JVM host key

#### `jdkHome`
JDK 目录。

#### `baseClassLoader`
默认值逻辑：
- 若 `configurationDependencies` 存在，则基于这些依赖构造一个 `URLClassLoader(..., null)`
- 否则为 null，由后续逻辑决定回退

这说明：
- Host 可用一个“仅由配置依赖构成的 classloader”隔离配置代码加载环境
- 父加载器为 `null`，意味着更强隔离

### 5.3.2 `defaultJvmScriptingHostConfiguration`

默认 host 配置只做了一件事：
- `getScriptingClass(JvmGetScriptingClass())`

这表明 JVM host 的核心默认能力，就是“知道如何把 `KotlinType` 解析成 `KClass`”。

### 5.3.3 `GetScriptingClassByClassLoader`

它是 `GetScriptingClass` 的 JVM 化版本：
- 额外显式接收 `contextClassLoader`

### 5.3.4 `JvmGetScriptingClass`

这是 JVM 平台上模板类/配置类加载的核心实现。

#### 内部状态
- `dependencies`
- `classLoader`
- `baseClassLoaderIsInitialized`
- `baseClassLoader`

这些字段都标记为 `@Transient`，说明序列化后会重新建立运行期状态。

#### `invoke(classType, contextClass, hostConfiguration)`
转发到 `invoke(classType, contextClassLoader, hostConfiguration)`。

#### 主要算法
1. 若 `classType.fromClass != null`：
   - 若其类加载器为 null（根加载器），直接返回
   - 若该类加载器已经在当前上下文加载器链中，直接返回原类
2. 校验 `configurationDependencies` 是否与历史一致
   - 一旦变化，抛异常
3. 初始化 `baseClassLoader`
   - 优先 `hostConfiguration.jvm.baseClassLoader`
   - 否则 `contextClassLoader`
4. 若 `classLoader` 未创建：
   - 从 `dependencies` 中提取所有 `JvmDependency.classpath`
   - 若为空，直接用 `baseClassLoader`
   - 否则构造 `URLClassLoader(classpath, baseClassLoader)`
5. 使用该 classLoader 加载 `classType.typeName`

#### 非常重要的设计点

##### 依赖集不允许变化
```kotlin
if (newDeps != dependencies) throw IllegalArgumentException(...)
```

说明同一个 `JvmGetScriptingClass` 实例被设计成：
- 与一组固定 configuration dependencies 绑定
- 防止在同一解析器实例上切换依赖环境造成类型污染

##### 上下文类优先复用
如果模板类已由上下文加载器链可见，则直接返回原类，不再重复构造加载器。

这能减少：
- 重复类定义
- class cast 问题
- 配置类双份加载

#### `equals/hashCode`
把依赖、classLoader、baseClassLoader 纳入比较，说明该对象可能被放进缓存或配置比较逻辑中。

### 5.3.5 本文件结论

它定义了“JVM 上配置类和模板类如何加载”的实际策略，这直接影响：
- 模板发现
- 配置类实例化
- 类相等性
- 插件环境/IDE 环境兼容性

---

## 5.4 `jvm/src/kotlin/script/experimental/jvm/runner.kt`

这个文件是脚本运行入口辅助，偏向编译产物自举执行。

### 5.4.1 `runCompiledScript(scriptClass: Class<*>, vararg args: String)`

流程：
1. `createScriptFromClassLoader(scriptClass.name, scriptClass.classLoader)`
   - 从脚本类加载器中反查 metadata，恢复 `KJvmCompiledScript`
2. 创建 `BasicJvmScriptEvaluator`
3. 调用 `createEvaluationConfigurationFromTemplate(...)`
   - 使用脚本编译配置中的 `baseClass`
   - 使用编译配置中的 hostConfiguration，并补默认 JVM host 配置
   - 以 `scriptClass.kotlin` 作为上下文类
   - 注入 `jvm.mainArguments(args)`
4. 用 `internalScriptingRunSuspend` 调 evaluator
5. 若失败，把 reports 打到 `System.err`

### 5.4.2 设计意义

这说明脚本编译产物并不是只能靠“外部保存好的 CompiledScript 对象”执行，
只要 classloader 中还带有 metadata，就能反推出脚本定义并重新执行。

适合：
- 已编译脚本 jar 直接运行
- 代码生成后的启动入口

---

## 5.5 `jvm/src/kotlin/script/experimental/jvm/util/SnippetsHistory.kt`

这是一个很小但非常基础的 REPL 工具类。

### 5.5.1 类型别名
- `CompiledHistoryItem<CompiledT, ResultT> = Pair<CompiledT, ResultT>`
- `CompiledHistoryStorage = ArrayList<...>`
- `CompiledHistoryList = List<...>`

### 5.5.2 `SnippetsHistory`

内部就是一个 `ArrayList<Pair<CompiledT, ResultT>>`。

能力：
- `add(line, value)`
- `lastItem()`
- `lastValue()`
- `items`
- `isEmpty()` / `isNotEmpty()`

### 5.5.3 注释中的重要信息
源码明确写了：
> Not thread safe, the caller is assumed to lock access.

这意味着：
- REPL 历史容器本身不负责并发
- 上层 host/repl session 必须自行做同步

这对服务端多会话 REPL 很重要。

---

## 5.6 `jvm/src/kotlin/script/experimental/jvm/util/jvmClassLoaderUtil.kt`

这是资源扫描与类加载器中文件发现工具集。

### 5.6.1 `forAllMatchingFiles(...)`

作用：
- 在 classloader 可见资源中，按 `namePattern` 匹配所有文件
- 支持普通目录和 jar 内资源

处理逻辑：
- 维护 `processedDirs` / `processedJars` 去重
- 若 `url.protocol == "jar"`：
  - 通过 `JarURLConnection` 获取 jarFileURL
  - 遍历 jar entries
- 否则：
  - 计算资源根目录
  - 在目录中递归匹配

### 5.6.2 模式匹配支持

辅助常量和函数：
- `wildcardChars = * ?`
- `patternCharsToEscape`
- `pathSeparatorPattern`
- `pathElementPattern`
- `namePatternToRegex(pattern)`

支持语义：
- `?` -> 单字符
- `*` -> 单路径段任意字符
- `**` -> 跨目录任意路径

这基本是在 scripting 内部实现了一个轻量 glob to regex。

### 5.6.3 目录与 jar 双扫描能力

- `forAllMatchingFilesInDirectory`
- `forAllMatchingFilesInJarStream`
- `forAllMatchingFilesInJar`
- `forAllMatchingFilesInJarFile`

### 5.6.4 设计意义

它支撑的常见场景包括：
- 扫描服务文件
- 扫描脚本元数据
- classpath 下定义发现

---

## 5.7 `jvm/src/kotlin/script/experimental/jvm/util/jvmClasspathUtil.kt`

这是整个 scripting JVM 体系中最复杂、最工程化的工具文件之一。

它解决的问题不是“读取 classpath”这么简单，而是：
- 在普通 JVM、IDEA、插件 classloader、fat jar、war 包等环境里尽量稳定提取编译所需 classpath
- 找到 Kotlin compiler / stdlib / reflect / script runtime 等关键 jar

### 5.7.1 常量集

定义了大量 Kotlin 关键 jar 名：
- `kotlin-stdlib.jar`
- `kotlin-reflect.jar`
- `kotlin-script-runtime.jar`
- `kotlin-scripting-compiler.jar`
- `kotlin-scripting-common.jar`
- `kotlin-scripting-jvm.jar`
- `kotlin-compiler.jar`
- `kotlin-compiler-embeddable.jar`
等

还定义了：
- Spring Boot / WAR jar collection 路径：`BOOT-INF/*`, `WEB-INF/*`
- 系统属性键：
  - `kotlin.script.classpath`
  - `kotlin.compiler.classpath`
  - `kotlin.compiler.jar`
  - `kotlin.java.stdlib.jar`
  - `kotlin.java.reflect.jar`
  - `kotlin.script.runtime.jar`

### 5.7.2 `classpathFromClassloader(currentClassLoader, unpackJarCollections)`

这是 classpath 提取主函数。

逻辑概览：
1. 枚举与当前 classloader 相关的所有 classloader（父优先）
2. 若启用 `unpackJarCollections`，检测 fat jar / war，并解包其中 classes/lib
3. 若 classloader 是 `URLClassLoader`，直接取 URLs
4. 否则尝试：
   - 反射 `getUrls()`
   - 从典型资源 URL 推导 classpath 根
5. 去重并返回文件列表

### 5.7.3 `unpackJarCollections`

这是非常工程化的兼容逻辑。

针对：
- Spring Boot fat jars
- WAR 包

做法：
- 把内嵌 `BOOT-INF/classes` / `BOOT-INF/lib` 或 `WEB-INF/*` 解包到临时目录
- 添加 shutdown hook 自动删除

说明 Kotlin scripting 明确考虑了在“非平面 classpath”环境中的运行。

### 5.7.4 `classPathFromGetUrlsMethodOrNull()`

为了兼容 IDEA 平台 `UrlClassLoader` 等非标准实现，反射调用 `getUrls()`。

### 5.7.5 `ClassLoaderResourceRootFIlePathCalculator`

根据某个资源路径反推出 classpath root。

这是从资源 URL 倒推出根目录的基础工具。

### 5.7.6 `classPathFromTypicalResourceUrls()`

通过资源根 `""` 和 `META-INF/MANIFEST.MF` 推断 classpath。

用于弥补某些 classloader 无法直接枚举 URLs 的问题。

### 5.7.7 `classpathFromClasspathProperty()`

从 `java.class.path` 系统属性直接解析。

这是最传统但也最不一定可靠的回退方案。

### 5.7.8 `classpathFromClass` / `classpathFromFQN`

通过某个类或 FQN 的 `.class` 资源位置反推所在 classpath 元素。

适合定点定位依赖。

### 5.7.9 `scriptCompilationClasspathFromContextOrNull(...)`

这是脚本编译 classpath 的最重要入口之一。

逻辑：
1. 若系统属性 `kotlin.script.classpath` 存在，直接用它
2. 否则尝试从 classloader 自动提取
3. 再不行回退到 `java.class.path`
4. 再根据 `wholeClasspath` 决定：
   - `takeIfContainsAll(*keyNames)`
   - 或 `filterIfContainsAll(*keyNames)`

### 5.7.10 `scriptCompilationClasspathFromContext(...)`

若提取不到，则直接抛 `ClasspathExtractionException`。

这比返回 null 更适合强约束调用路径。

### 5.7.11 `KotlinJars`

这是 Kotlin 关键 jar 发现器。

#### `explicitCompilerClasspath`
优先从：
- `kotlin.compiler.classpath`
- `kotlin.compiler.jar`
取值

#### `compilerClasspath` / `compilerWithScriptingClasspath`
自动寻找 Kotlin 编译器相关 classpath。

#### `findCompilerClasspath(withScripting)`
查找逻辑：
1. 优先显式属性
2. 否则从上下文 classloader / `java.class.path` 中找包含关键 jar 的 classpath
3. 若找不到 compiler jar，则抛 `FileNotFoundException`

#### `getLib(...)`
按系统属性 / explicit classpath / marker class 路径来寻找某个库文件。

#### `stdlib`, `reflect`, `scriptRuntime`
分别提供 Kotlin 标准库、反射库、脚本运行时的自动发现能力。

### 5.7.12 `toClassPathOrEmpty()`

把 `List<ScriptDependency>?` 压平成 `List<File>`，仅提取 `JvmDependency.classpath`。

### 5.7.13 本文件结论

这是一个“为真实世界混乱 classloader 环境兜底”的工程文件。它非常重要，因为没有稳定 classpath 提取，脚本编译功能在 IDE、fat jar、插件环境下都容易崩。

---

## 5.8 `jvm/src/kotlin/script/experimental/jvm/impl/KJvmCompiledScript.kt`

这是 JVM 编译产物表示的核心实现。

### 5.8.1 `KJvmCompiledScriptData`

可序列化数据对象，字段：
- `sourceLocationId`
- `compilationConfiguration`
- `scriptClassFQName`
- `resultField`
- `otherScripts`

### 5.8.2 为什么要单独分 data

因为 `KJvmCompiledScript` 本身还带 `compiledModule`，而模块对象未必总适合被共享/复制。
所以把核心可序列化元数据抽出来，便于：
- 序列化
- metadata 存储
- copyWithoutModule

### 5.8.3 `KJvmCompiledScript`

实现 `CompiledScript`，并额外持有：
- `data`
- `compiledModule`

其中注释明确说明：
> imported scripts 的 module 应为 null，只保留一份 module 引用

这体现了模块级产物复用策略。

### 5.8.4 `getClass(scriptEvaluationConfiguration)`

执行流程：
1. 若 eval config 为 null，构造默认配置
2. `getOrCreateActualClassloader(actualEvaluationConfiguration)`
3. 用 classloader `loadClass(scriptClassFQName).kotlin`
4. 成功返回 `Success`
5. 失败返回 `Failure`，diagnostic message 为 `Unable to instantiate class ...`

### 5.8.5 `getOrCreateActualClassloader(evaluationConfiguration)`

优先：
- 直接取 `evaluationConfiguration.jvm.actualClassLoader`

否则：
1. 必须要求 `compiledModule != null`
2. 读取：
   - `baseClassLoader`
   - `lastSnippetClassLoader ?: baseClassLoader`
3. 若 `loadDependencies == false`：
   - 直接用 `baseClassLoader`
4. 否则：
   - `makeClassLoaderFromDependencies(baseClassLoader, lastClassLoader)`
5. 最后 `module.createClassLoader(classLoaderWithDeps)`

这说明最终 classloader 由两层叠加：
- 依赖类加载器链
- 编译产物模块类加载器

### 5.8.6 `makeClassLoaderFromDependencies(...)`

这是脚本依赖类加载器构建的核心算法。

主要步骤：
1. 递归遍历当前脚本及其 `otherScripts`
2. 收集每个脚本编译配置中的 dependencies
3. 从 `lastClassLoader` 向上递归收集已有 URLClassLoader URLs，避免重复加到 classpath
4. 对 dependencies 逐个 fold 叠加 parentClassLoader：
   - `JvmDependency`：把新的 classpath URL 构造成新的 `URLClassLoader`
   - `JvmDependencyFromClassLoader`：构造 `DualClassLoader`
5. 返回最终 parent chain

### 5.8.7 关键设计点

#### imported scripts 的依赖会一起纳入
这意味着当前脚本运行前，其 imported scripts 的依赖可见性已被纳入类加载器构造。

#### 去重同时考虑：
- classpath 元素 URL
- 依赖 classloader 实例

#### `DualClassLoader`
用于把“一个现有依赖 classloader”与当前 parentClassLoader 组合起来，而不是只能 URL 叠加。

这是个很强的扩展点，允许某些依赖以 classloader 形式而不是 classpath 文件形式注入。

### 5.8.8 脚本 metadata 常量

- `KOTLIN_SCRIPT_METADATA_PATH = META-INF/kotlin/script`
- `KOTLIN_SCRIPT_METADATA_EXTENSION_WITH_DOT = .kotlin_script`
- `scriptMetadataPath(scriptClassFQName)`

说明每个脚本编译产物都可以在 classloader 资源里带一份 metadata。

### 5.8.9 `copyWithoutModule()`

只复制 data，不带 compiledModule。

适合：
- imported scripts
- 轻量传输
- 减少 module 引用冗余

### 5.8.10 `toBytes()`

把 `KJvmCompiledScript` 直接序列化为字节数组。

用途：
- 持久化缓存
- jar metadata 嵌入
- 远程传输

### 5.8.11 `createScriptFromClassLoader(scriptClassFQName, classLoader)`

通过 classloader 中的 metadata 反序列化 `KJvmCompiledScript`，再补上：
- `compiledModule = KJvmCompiledModuleFromClassLoader(classLoader)`

这与 `runner.kt` 正好闭环。

### 5.8.12 本文件结论

`KJvmCompiledScript` 是 JVM 脚本运行世界里的“核心载体对象”。

它同时连接了：
- 编译期元数据
- imported scripts
- 依赖类加载器构建
- 模块字节码加载
- metadata 存储与恢复

---

# 6. 本阶段补充结论

在补完以上 JVM 文件后，可以更完整地理解 Kotlin scripting 在 JVM 上的真实运行模型：

1. **模板与配置类加载**：`JvmGetScriptingClass`
2. **编译 classpath 发现**：`jvmClasspathUtil.kt`
3. **编译产物表示与 metadata 恢复**：`KJvmCompiledScript.kt`
4. **普通脚本执行**：`BasicJvmScriptEvaluator`
5. **REPL 增量执行**：`BasicJvmReplEvaluator`
6. **缓存能力注入**：`jvmScriptCaching.kt`
7. **已编译脚本自举运行**：`runner.kt`

接下来仍需继续补完：
- `jvm` 其余 util/impl 文件
- `jvm-host` 全部
- `jsr223` 全部
- `dependencies` / `dependencies-maven` 全部
- `intellij` 全部
- 测试与 testData 全量场景映射

---

# 7. JVM 余下工具与实现文件补完分析

这一部分继续补 `libraries/scripting/jvm` 中前面尚未展开的 util / impl / compat 文件。它们虽然单个文件不大，但很多都处在“关键节点上”，对调试、兼容和二次开发很重要。

---

## 7.1 `jvm/src/kotlin/script/experimental/jvm/util/diagnosticsHelpers.kt`

这个文件非常小，但作用很明确：给 `ResultWithDiagnostics` 提供 JVM 侧常用判断辅助。

### 7.1.1 `isIncomplete()`

```kotlin
fun <T> ResultWithDiagnostics<T>.isIncomplete() =
    this.reports.any { it.code == ScriptDiagnostic.incompleteCode }
```

#### 作用
判断当前结果是否表示“不完整代码”。

#### 场景
主要用于：
- REPL 多行输入判断
- 编辑器实时分析时判断是否应该继续等待补全输入

#### 设计点评
它不是检查 `Failure` 本身，而是检查 diagnostics code，这说明：
- incomplete 只是某种特定诊断语义
- 不一定完全等价于普通错误

### 7.1.2 `isError()`

```kotlin
fun <T> ResultWithDiagnostics<T>.isError() = this is ResultWithDiagnostics.Failure
```

#### 作用
快速判断结果是否失败。

#### 注意点
这个 `isError()` 与 `ScriptDiagnostic.isError()` 并不相同：
- 前者看的是结果包装层面
- 后者看的是单条 diagnostic 的 severity/code

这两个概念不能混用。

---

## 7.2 `jvm/src/kotlin/script/experimental/jvm/util/identifiers.kt`

这个文件提供字符串到合法 JVM 标识符的转换。

### 7.2.1 `String.toValidJvmIdentifier()`

核心行为：
- 空白字符串 -> 用 `_` 重复同长度替代
- 对 JVM 中有特殊语义的字符做编码转换：
  - `/` -> `\|`
  - `.` -> `\,`
  - `;` -> `\?`
  - `$` -> `\%`
  - `<` -> `\^`
  - `>` -> `\_`
  - `[` -> `\{`
  - `]` -> `\}`
  - `:` -> `\!`
  - `\\` -> `\-`

### 7.2.2 设计来源
源码注释写明：
> encoding is taken from https://blogs.oracle.com/jrose/symbolic-freedom-in-the-vm

说明这不是随意替换，而是借用了 JVM 符号编码约定。

### 7.2.3 典型用途
- 由脚本文件名或逻辑名生成内部类名/字段名
- 避免非法字符导致字节码标识符异常

### 7.2.4 风险点
这个函数主要解决“字符合法性”，并不保证：
- 语义唯一性绝对完美
- 与已有生成命名绝对无冲突

因此真实系统通常还要配合前缀/后缀/hash。

---

## 7.3 `jvm/src/kotlin/script/experimental/jvm/util/runtimeExceptionReporting.kt`

这个文件专门处理脚本执行异常的展示问题。

### 7.3.1 核心对象
扩展在 `ResultValue.Error` 上：
- `renderError(stream: PrintStream)`
- `renderError(): String`

### 7.3.2 为什么单独做这个工具
脚本执行常常经由反射调用：
- `InvocationTargetException`
- 包装层构造器栈
- generated script class 栈

如果直接 `printStackTrace()`，用户看到的往往是大量宿主/反射包装栈，而不是脚本真正出错位置。

### 7.3.3 `renderError(stream)` 逻辑

1. 取 `error.stackTrace`
2. 取 `wrappingException?.stackTrace`
3. 如果没有 wrappingException，或 error 栈比 wrapping 栈更短：
   - 直接完整打印 `error.printStackTrace(stream)`
4. 否则：
   - 打印 `error`
   - 去掉 wrapping 栈尾部重合部分
   - 逐层打印 `cause`
   - 对每层 cause 继续裁剪与上一层重复的尾部栈帧
   - 用 `cyclesDetection` 防止 cause 链循环

### 7.3.4 实际效果
输出更像“手工精简过的用户异常栈”：
- 更聚焦脚本本身错误
- 减少反射/包装器噪音
- 更利于终端和日志阅读

### 7.3.5 `renderError(): String`

就是把上述输出写入 `ByteArrayOutputStream` 再转字符串。

### 7.3.6 适用场景
- REPL 错误展示
- CLI script runner
- JSR223 引擎向用户返回友好错误文本

---

## 7.4 `jvm/src/kotlin/script/experimental/jvm/util/sourceCodeUtils.kt`

这个文件负责源码位置与绝对偏移之间的转换，是 diagnostics 与 IDE 支持的底层工具之一。

### 7.4.1 `AbsSourceCodePosition`

```kotlin
data class AbsSourceCodePosition(val line: Int, val col: Int, val absolutePos: Int)
```

表示同时持有：
- 行
- 列
- 绝对偏移

### 7.4.2 `String.findNth(s, n, start)`

作用：
- 查找第 n 次出现的子串位置

主要用于：
- 通过行号定位换行符位置
- 从 `line/col` 反算 absolute offset

### 7.4.3 `Int.toSourceCodePosition(code)`

把绝对偏移转成 `SourceCode.Position`。

计算逻辑：
1. 取源码前缀 `substring(0, this)`
2. 行号 = 前缀中 `\n` 个数 + 1
3. 自动判断换行分隔符 `\n` 或 `\r\n`
4. 列号 = 当前行内偏移 + 1
5. 返回 `SourceCode.Position(line, col, absolutePos = this)`

### 7.4.4 `String.determineSep()`

若文本中不存在 `\r\n`，则认为换行是 `\n`，否则 `\r\n`。

### 7.4.5 `SourceCode.Position.calcAbsolute(code)`

把 `line/col` 再反算成绝对偏移。

逻辑：
- 若已有 `absolutePos`，直接返回
- 若是第一行，直接 `col - 1`
- 否则找到前 `line - 1` 个换行位置，再加列偏移

### 7.4.6 价值

这类函数通常很容易被忽略，但它们对以下能力至关重要：
- diagnostics 精确落点
- IDE 错误高亮
- 代码片段范围切分
- 报告中 line/col 与 absolutePos 互转

---

## 7.5 `jvm/src/kotlin/script/experimental/jvm/impl/KJvmCompiledModule.kt`

这个文件定义 JVM 编译模块抽象与若干 classloader 组合实现。

### 7.5.1 `KJvmCompiledModule`

```kotlin
interface KJvmCompiledModule {
    fun createClassLoader(baseClassLoader: ClassLoader?): ClassLoader
}
```

语义：
- 一个“已编译脚本模块”知道如何从某个 baseClassLoader 之上构建自己的 classloader

这使得：
- 编译产物存储方式可多样化
- 但对外统一成“给我一个 classloader”

### 7.5.2 `KJvmCompiledModuleInMemory`

扩展接口，多出：
- `compilerOutputFiles: Map<String, ByteArray>`

说明某些模块实现是纯内存字节码形式。

### 7.5.3 `KJvmCompiledModuleFromClassPath`

把一组 classpath 文件作为模块内容：
- `createClassLoader(base)` -> `URLClassLoader(classpathUrls, base)`

适合：
- 编译结果落盘到目录/jar 后重新加载

### 7.5.4 `KJvmCompiledModuleFromClassLoader`

直接拿现成 classloader 代表模块。

若 `baseClassLoader == null`：
- 直接返回 `moduleClassLoader`

否则：
- 返回 `DualClassLoader(moduleClassLoader, baseClassLoader)`

这与 `createScriptFromClassLoader` 的恢复模型配套。

### 7.5.5 `DualClassLoader`

这是很关键的一个类。

#### 构造逻辑
```kotlin
internal class DualClassLoader(fallbackLoader, parentLoader) : ClassLoader(singleClassLoader(...) ?: parentLoader)
```

即：
- 尝试找出 fallback 与 parent 是否本质上已有祖先关系
- 若是，则直接选择单一 loader
- 若不是，再创建真正的双路 fallback 结构

#### `singleClassLoader(...)`
若：
- `parentLoader` 是 `fallbackLoader` 祖先 -> 用 `fallbackLoader`
- `fallbackLoader` 是 `parentLoader` 祖先 -> 用 `parentLoader`
- 否则返回 null

这是一个很聪明的优化：
- 避免不必要的双路加载结构
- 只有两个 loader 真正彼此独立时才启用 fallback

#### `fallbackClassLoader: Wrapper?`
`Wrapper` 暴露了 `findResource(s)`，以便在 fallback 路径上访问受保护的查找方法。

#### `findClass(name)`
先走 `super.findClass(name)`，失败再走 fallback。

#### `getResourceAsStream(name)`
先当前，再 fallback。

#### `findResources(name)`
若存在 fallback，就把两个 loader 的资源枚举拼起来。

#### `findResource(name)`
先当前，再 fallback。

### 7.5.6 设计价值

`DualClassLoader` 是 scripting 体系为了避免硬性“单 parent URLClassLoader 线性链”而做的增强，适合：
- 依赖以现成 classloader 形式提供
- 模块 classloader 与宿主 classloader 并列存在
- 需要复用既有隔离加载环境

---

## 7.6 `jvm/src/kotlin/script/experimental/jvm/impl/pathUtil.kt`

这是资源路径与 URL/file 相互转换工具。

### 7.6.1 `getResourceRoot(context, path)`

从某个类和资源路径推断资源根目录。

逻辑：
- 先 `context.getResource(path)`
- 找不到再 `ClassLoader.getSystemResource(path.substring(1))`
- 最后 `extractRoot(url, path)`

### 7.6.2 `extractRoot(resourceURL, resourcePath)`

支持两类 URL：
- `file:`
- `jar:`

#### file 协议
若真实路径以资源路径结尾，则把资源路径从文件路径末尾裁掉，得到根目录。

#### jar 协议
调用 `splitJarUrl(resourceURL.file)`，最终拿到 jar 文件 canonical path。

### 7.6.3 `splitJarUrl(url)`

把类似：
- `jar:file:/.../x.jar!/a/b.class`
拆成：
- jarPath
- resourcePath

同时兼容若干 file URL 变体。

### 7.6.4 `tryGetResourcePathForClass(aClass)`

通过类的 `.class` 资源反推类所在根目录或 jar。

### 7.6.5 `tryGetResourcePathForClassByName(name, classLoader)`

先尝试 `classLoader.loadClass(name)`，再反推资源路径。

对 `ClassNotFoundException` 和 `NoClassDefFoundError` 都做了吞掉处理并返回 null。

### 7.6.6 `URL.toFileOrNull()` / `URL.toContainingJarOrNull()`

分别用于：
- 普通 URL 转本地文件
- jar URL 反推所在 jar 文件

### 7.6.7 本文件价值

虽然是 util，但它支撑了很多“从 class/resource 反推实际 jar/目录”的逻辑，而这些逻辑又被 classpath 自动推导、KotlinJars 自动发现等大量复用。

---

## 7.7 `jvm/src/kotlin/script/experimental/jvm/compat/diagnosticsUtil.kt`

这个文件负责 **新旧脚本 API 诊断模型之间的映射**。

### 7.7.1 为什么它存在

Kotlin scripting 历史较长，存在：
- legacy `ScriptDependenciesResolver`
- legacy `ScriptReport`
- 新 experimental API `ScriptDiagnostic`

而很多模块需要同时兼容新旧接口。

### 7.7.2 severity 映射

提供：
- `mapLegacyDiagnosticSeverity(ScriptDependenciesResolver.ReportSeverity)`
- `mapLegacyDiagnosticSeverity(ScriptReport.Severity)`
- `mapToLegacyScriptReportSeverity(ScriptDiagnostic.Severity)`

作用很直接：新旧 severity 枚举互转。

### 7.7.3 位置映射

- `mapLegacyScriptPosition(ScriptContents.Position?) -> SourceCode.Location?`
- `mapLegacyScriptPosition(ScriptReport.Position?) -> SourceCode.Location?`
- `mapToLegacyScriptReportPosition(SourceCode.Location?) -> ScriptReport.Position?`

说明新旧 API 的位置模型并不完全相同，需要显式桥接。

### 7.7.4 批量转换

#### `Iterable<ScriptReport>.mapToDiagnostics()`
把 legacy reports 转成新 `ScriptDiagnostic`。

#### `Iterable<ScriptDiagnostic>.mapToLegacyReports()`
把新 diagnostics 转回 legacy `ScriptReport`。

其中：
- 若 diagnostic 带 exception，会把 exception 文本拼到 legacy report 的 message 中

### 7.7.5 适用场景
- 老解析器接新 host
- 新 diagnostics 流转到仍依赖 legacy API 的调用方
- 渐进迁移期间保持兼容

---

# 8. 到目前为止的 JVM 模块完整理解

结合前面所有 JVM 文件，可以把它们的职责连成一条更完整的链：

1. **classpath / 关键 jar 发现**
   - `jvmClasspathUtil.kt`
   - `pathUtil.kt`
2. **模板/配置类加载**
   - `jvmScriptingHostConfiguration.kt`
3. **脚本编译配置中的 JVM 依赖表达**
   - `jvmScriptCompilation.kt`
4. **脚本执行配置中的 JVM 类加载控制**
   - `jvmScriptEvaluation.kt`
5. **编译产物表示与模块 classloader 创建**
   - `KJvmCompiledScript.kt`
   - `KJvmCompiledModule.kt`
6. **普通脚本执行**
   - `BasicJvmScriptEvaluator.kt`
7. **REPL 增量执行**
   - `BasicJvmReplEvaluator.kt`
   - `SnippetsHistory.kt`
8. **异常与诊断展示**
   - `runtimeExceptionReporting.kt`
   - `diagnosticsHelpers.kt`
   - `compat/diagnosticsUtil.kt`
9. **运行入口与缓存插槽**
   - `runner.kt`
   - `jvmScriptCaching.kt`

这已经足以帮助开发者：
- 改 evaluator
- 改 classloader
- 做缓存
- 排 REPL 问题
- 处理 fat jar / IDE 插件 classpath 异常
- 新旧 API 兼容

---

# 9. jvm-host 与 jsr223 模块超细分析

`jvm-host` 是“面向宿主应用”的 Kotlin scripting JVM 入口层，负责把 `common + jvm + compiler plugin` 组合成更易使用的 Host 形态；`jsr223` 则进一步把这套能力暴露为 Java 标准 `ScriptEngine`。

这两层是业务接入中最常直接触碰的模块。

---

## 9.1 `jvm-host/src/kotlin/script/experimental/jvmhost/BasicJvmScriptingHost.kt`

这是 JVM 场景的标准宿主入口。

### 9.1.1 类定义

```kotlin
open class BasicJvmScriptingHost(
    val baseHostConfiguration: ScriptingHostConfiguration? = null,
    compiler: JvmScriptCompiler = JvmScriptCompiler(...),
    evaluator: ScriptEvaluator = BasicJvmScriptEvaluator()
) : BasicScriptingHost(compiler, evaluator)
```

### 9.1.2 默认行为

- 若未提供 compiler，则默认创建 `JvmScriptCompiler(baseHostConfiguration.withDefaultsFrom(defaultJvmScriptingHostConfiguration))`
- 若未提供 evaluator，则使用 `BasicJvmScriptEvaluator`

这说明它是一个“标准 JVM host 装配件”，而不是再发明一套新流程。

### 9.1.3 `evalWithTemplate<T>`

逻辑：
1. `createJvmScriptDefinitionFromTemplate<T>(...)`
2. 取出 definition 的 compilation/evaluation configuration
3. 调 `eval(...)`

这大幅简化了最常见的使用路径：
- 有模板类
- 给我直接跑脚本

### 9.1.4 `createLegacy(...)`

使用：
- `JvmScriptCompiler.createLegacy(...)`
- `BasicJvmScriptEvaluator`

其意义是：
- 在同一 host 形态下切换回旧编译器实现
- 便于兼容 K1/legacy 编译链路

### 9.1.5 顶层辅助函数

#### `createJvmCompilationConfigurationFromTemplate<T>()`
#### `createJvmEvaluationConfigurationFromTemplate<T>()`
#### `createJvmScriptDefinitionFromTemplate<T>()`

它们本质上只是把 generic 模板类型、默认 JVM host 配置和 common 层的 template conversion 绑在一起。

### 9.1.6 本文件结论

对业务方来说，`BasicJvmScriptingHost` 基本就是“最值得先拿来用”的入口：
- 默认值合理
- 支持模板化脚本
- 支持 legacy 编译链回退
- 不隐藏底层配置对象，仍易于定制

---

## 9.2 `jvm-host/src/kotlin/script/experimental/jvmhost/jvmScriptCompilation.kt`

这个文件把 `ScriptCompiler` 的 JVM 实现真正接上编译器插件代理。

### 9.2.1 `JvmScriptCompiler`

构造参数：
- `baseHostConfiguration`
- `compilerProxy: ScriptCompilerProxy?`

### 9.2.2 内部字段

- `hostConfiguration = baseHostConfiguration.withDefaultsFrom(defaultJvmScriptingHostConfiguration)`
- `compilerProxy = compilerProxy ?: ScriptJvmK2CompilerIsolated(hostConfiguration)`

#### 关键含义
默认已经切到：
- **K2 isolated compiler**

这说明当前 JVM host 默认走的是 K2 编译器隔离实现。

### 9.2.3 `invoke(script, config)`

核心逻辑：
```kotlin
compilerProxy.compile(
    script,
    scriptCompilationConfiguration.with {
        hostConfiguration.update { it.withDefaultsFrom(this@JvmScriptCompiler.hostConfiguration) }
    }
)
```

#### 含义
在真正调用编译器前，会把 compiler 自己的 hostConfiguration 补入脚本编译配置。

这样能保证：
- 即使调用方没显式塞 hostConfiguration
- 编译器仍能拿到 JVM 默认 host 能力

### 9.2.4 `createLegacy(...)`

切换到：
- `ScriptJvmCompilerIsolated(hostConfiguration)`

说明 legacy 与默认实现的差异点主要在 compiler proxy。

### 9.2.5 本文件结论

`JvmScriptCompiler` 是 jvm-host 层最关键的桥梁之一：
- 上游接 host API
- 下游接 `org.jetbrains.kotlin.scripting.compiler.plugin` 编译器代理

---

## 9.3 `jvm-host/src/kotlin/script/experimental/jvmhost/CompiledScriptJarsCache.kt`

这个文件提供了一个非常实用的磁盘 jar 缓存实现。

### 9.3.1 `CompiledScriptJarsCache`

构造参数：
- `scriptToFile: (SourceCode, ScriptCompilationConfiguration) -> File?`

即：
- 缓存层不强制规定缓存路径算法
- 路径策略完全交给调用方

### 9.3.2 `get(...)`

流程：
1. 根据 `scriptToFile(...)` 找到缓存文件
2. 若找不到映射，直接抛 `IllegalArgumentException`
3. 文件不存在 -> 返回 null
4. 尝试 `file.loadScriptFromJar()`
5. 若加载失败：
   - 删除该缓存文件
   - 返回 null

#### 设计亮点
缓存失效策略非常务实：
- 无法加载 = 认为缓存损坏或依赖已失效
- 直接删除，避免脏缓存反复命中

### 9.3.3 `store(...)`

要求：
- `compiledScript` 必须是 `KJvmCompiledScript`

然后：
- `jvmScript.saveToJar(file)`

### 9.3.4 本文件结论

它是一个“高可用而非复杂”的缓存实现样板：
- 不复杂
- 很适合业务方按源码 + 配置 hash 生成 jar cache
- 失效行为也清晰

---

## 9.4 `jvm-host/src/kotlin/script/experimental/jvmhost/jvmScriptSaving.kt`

这是 jvm-host 中非常关键的“编译产物落盘/装载”文件。

它把 `KJvmCompiledScript` 与：
- class files 输出目录
- jar 输出
- jar 恢复加载

串起来了。

### 9.4.1 `BasicJvmScriptClassFilesGenerator`

它实现 `ScriptEvaluator`，但并不真正执行脚本，而是把编译结果类文件导出到目录。

#### `invoke(compiledScript, config)`
流程：
1. 要求 `compiledScript is KJvmCompiledScript`
2. 要求 `compiledScript.getCompiledModule() is KJvmCompiledModuleInMemory`
3. 遍历 `module.compilerOutputFiles`
4. 写入 `outputDir/path`
5. 返回 `EvaluationResult(ResultValue.NotEvaluated, config)`

#### 设计意义
它把 evaluator 当成一种“编译产物消费器”。
这与 scripting API 的设计很契合：
- evaluator 不一定非得“执行脚本”
- 也可以是“导出字节码”

### 9.4.2 `KJvmCompiledScript.saveToJar(outputJar)`

这个扩展函数是核心。

#### 主要步骤
1. 要求模块是 `KJvmCompiledModuleInMemory`
2. 从脚本编译配置里收集 `JvmDependency`
3. 额外从当前上下文补齐 scripting runtime 相关依赖（用于 main 执行）
4. 构造 manifest：
   - `Manifest-Version`
   - `Created-By`
   - `Class-Path`
   - `Main-Class = scriptClassFQName`
5. 向 jar 中写入：
   - `META-INF/kotlin/script/<fqName>.kotlin_script` metadata
   - 所有编译输出 class/resource 文件

#### 为什么要补 `dependenciesForMain`
因为脚本 jar 若想作为可执行 jar 使用，仅保存脚本自身 class 不够，还要确保运行时能找到 scripting runtime 等依赖。

### 9.4.3 `File.loadScriptFromJar(checkMissingDependencies = true)`

流程：
1. 读取 jar manifest
2. 提取 `Main-Class` 和 `Class-Path`
3. 把 classpath entry 转回本地 `File`
4. 若检查缺失依赖开启：
   - 只有当所有 manifest classpath 项都能落到存在的本地文件时才认为缓存有效
5. 返回 `KJvmCompiledScriptLazilyLoadedFromClasspath(className, classPath)`

#### 很关键的行为
如果 manifest 中依赖丢失：
- 返回 null
- 让上层缓存失效并重新编译/重新解析依赖

这是防止“缓存 jar 还在，但依赖已不可用”的关键逻辑。

### 9.4.4 `BasicJvmScriptJarGenerator`

另一个 evaluator 变体：
- 把 `CompiledScript` 直接输出成 jar
- 返回 `ResultValue.NotEvaluated`

### 9.4.5 `KJvmCompiledScriptLazilyLoadedFromClasspath`

这是一个非常有意思的适配器：
- 它实现 `CompiledScript`
- 但内部真正的 `KJvmCompiledScript` 延迟到第一次 `getClass(...)` 时才加载

#### 核心字段
- `scriptClassFQName`
- `classPath`
- `loadedScript: KJvmCompiledScript?`

#### `getClass(scriptEvaluationConfiguration)`
若尚未加载：
1. 构造 URLClassLoader
   - 若 `loadDependencies` 为 true：整个 classPath 都加进来
   - 否则只取第一个元素（通常就是 jar 本体）
2. `createScriptFromClassLoader(scriptClassFQName, classLoader)`
3. 存入 `loadedScript`
4. 再转发到真实脚本 `getClass(...)`

#### 设计意义
这让缓存 jar 恢复出来的脚本对象具备：
- 更低初始化成本
- 与普通 `CompiledScript` 一致的接口
- 延迟依赖 classloader 构造

### 9.4.6 本文件结论

这个文件是“编译产物持久化”全家桶：
- 导出 class files
- 导出 jar
- 从 jar 恢复 CompiledScript
- lazy load

对于：
- 编译缓存
- 脚本分发
- 预编译脚本
- 构建系统集成
都非常重要。

---

## 9.5 `jvm-host/src/kotlin/script/experimental/jvmhost/obsoleteJvmScriptEvaluation.kt`

这是纯兼容层文件。

### 内容
- 旧的 `JvmScriptEvaluationConfigurationKeys`
- 旧的 `JvmScriptEvaluationConfigurationBuilder`
- 旧的 `ScriptEvaluationConfigurationKeys.jvm`
- 旧的 `BasicJvmScriptEvaluator`

全都标记为 `@Deprecated`，并转发到 `kotlin.script.experimental.jvm` 包中的新定义。

### 意义
说明包结构曾发生迁移，而 jvm-host 提供了迁移过渡层，避免老调用方立即断裂。

---

## 9.6 `jvm-host/src/kotlin/script/experimental/jvmhost/jsr223/KotlinJsr223HostConfiguration.kt`

这是 JSR223 场景专属配置 key 定义文件。

### 9.6.1 为什么单独建一套 jsr223 key
因为 JSR223 需要把 `ScriptContext` 这种 Java 脚本标准对象注入 scripting 配置系统。

### 9.6.2 Host 级 key

#### `ScriptingHostConfigurationKeys.jsr223`
开启 jsr223 DSL 作用域。

#### `Jsr223HostConfigurationKeys.getScriptContext`
`() -> ScriptContext?`

作用：
- 让 host 在需要时动态拿当前 ScriptContext
- 用 lambda 而不是直接存对象，避免上下文固定死

### 9.6.3 Compilation 级 key

#### `ScriptCompilationConfigurationKeys.jsr223`
编译配置 jsr223 DSL。

#### `Jsr223CompilationConfigurationKeys.getScriptContext`
默认会从 hostConfiguration 继承 `getScriptContext`。

#### `importAllBindings`
布尔值，默认 false。

语义：
- 是否把 JSR223 context 中所有 bindings 都自动导入为 providedProperties

### 9.6.4 Evaluation 级 key

#### `ScriptEvaluationConfigurationKeys.jsr223`
执行配置 jsr223 DSL。

#### `Jsr223EvaluationConfigurationKeys.getScriptContext`
默认同样继承自 hostConfiguration。

### 9.6.5 本文件结论

它建立了一个非常清晰的桥：
- Java 的 `ScriptContext`
- Kotlin scripting 的类型化配置系统

---

## 9.7 `jvm-host/src/kotlin/script/experimental/jvmhost/jsr223/propertiesFromContext.kt`

这是 JSR223 与 provided properties 自动绑定的核心逻辑文件。

### 9.7.1 编译 refine：`configureProvidedPropertiesFromJsr223Context(context: ScriptConfigurationRefinementContext)`

逻辑：
1. 从 compilation config 中获取 `ScriptContext`
2. 仅当：
   - `jsr223context != null`
   - `importAllBindings == true`
3. 收集 GLOBAL_SCOPE + ENGINE_SCOPE 所有 bindings
4. 对每个 binding：
   - 如果编译配置中尚未定义同名 property
   - 且值不是本地类（`qualifiedName != null`）
   - 则自动推导其 `KotlinType`
5. 返回带更新后的 `providedProperties` 的新 compilation config

#### 含义
这一步的目标是：
- 在编译阶段就知道有哪些变量可用、类型是什么

### 9.7.2 执行 refine：`configureProvidedPropertiesFromJsr223Context(context: ScriptEvaluationConfigurationRefinementContext)`

逻辑：
1. 读取 compiled script 的 `knownProperties`
2. 若存在已知 property：
   - 依次从 ENGINE_SCOPE / GLOBAL_SCOPE 取值
   - 缺失则返回 Failure("Property xxx is not found in the bindings")
3. 生成新的 evaluation config，填充 `providedProperties`

#### 含义
编译阶段负责“声明变量类型”，执行阶段负责“填充值”。

这是非常正确且分层清晰的设计。

### 9.7.3 风险与限制
源码里有两个 TODO 提示了真实限制：
- 还没校验 binding 名是否一定是合法脚本变量名
- 以运行时值推导编译类型，可能限制某些“后来换成兄弟类型”的场景

---

## 9.8 `jvm-host/src/kotlin/script/experimental/jvmhost/jsr223/KotlinJsr223InvocableScriptEngine.kt`

这个文件实现了 JSR223 的 `Invocable` 能力。

### 9.8.1 接口本身

```kotlin
interface KotlinJsr223InvocableScriptEngine : Invocable
```

要求实现方提供：
- `invokeWrapper`
- `backwardInstancesHistory`
- `baseClassLoader`

其中最关键的是 `backwardInstancesHistory`：
- 它代表 REPL 历史中已经生成的脚本实例序列
- 用于在这些实例上查找可调用函数

### 9.8.2 `invokeFunction(name, args...)`

会在 `backwardInstancesHistory` 中从新到旧搜索函数名匹配的函数，并尝试做参数映射。

### 9.8.3 `invokeMethod(thiz, name, args...)`

直接在给定对象上找方法。

### 9.8.4 `invokeImpl(...)`

核心逻辑：
1. 对每个 possible receiver：
   - `instance::class.functions.filter { it.name == name }`
   - 调 `findMapping(listOf(instance) + args)`
2. 找到第一个可映射候选
3. 若存在 `invokeWrapper`，通过 wrapper 执行 `fn.callBy(mapping)`
4. 异常时用 `renderReplStackTrace` 构造 `ScriptException`
5. 若返回类型是 `Unit`，统一返回 `Unit`

#### 关键点
Kotlin 反射调用时，需要显式把 instance 自身作为第一个参数参与参数匹配，这就是 `listOf(instance) + args` 的原因。

### 9.8.5 `getInterface(...)`

通过 Java 动态代理把脚本函数视作某个接口实现。

流程：
- `Proxy.newProxyInstance(baseClassLoader, arrayOf(clasz)) { _, method, args -> invokeImpl(...) }`
- 再 `safeCast` 成目标接口类型

这允许把脚本直接拿去当 Java 接口实现使用。

### 9.8.6 `findMapping(args)`

遍历候选 `KFunction`，调用 `tryCreateCallableMapping(fn, args)`，返回第一个能匹配的函数及参数映射。

### 9.8.7 本文件结论

它给 Kotlin JSR223 引擎补上了 Java 标准脚本引擎非常重要的扩展能力：
- 调脚本函数
- 调脚本对象方法
- 把脚本暴露为接口实现

---

## 9.9 `jvm-host/src/kotlin/script/experimental/jvmhost/jsr223/KotlinJsr223ScriptEngineImpl.kt`

这是 jvm-host 中 JSR223 引擎实现的核心文件。

### 9.9.1 类定义

它继承：
- `KotlinJsr223JvmScriptEngineBase`
并实现：
- `KotlinJsr223InvocableScriptEngine`

说明它并不是完全从零实现 ScriptEngine，而是复用旧 REPL 基类基础设施。
源码注释也明确写了：
> TODO: reimplement without legacy REPL infrastructure

### 9.9.2 构造参数

- `factory`
- `baseCompilationConfiguration`
- `baseEvaluationConfiguration`
- `getScriptArgs: (context: ScriptContext) -> ScriptArgsWithTypes?`

### 9.9.3 `lastScriptContext`

`@Volatile private var lastScriptContext: ScriptContext?`

这是一个关键“hack”状态位，用于在 compile/eval 流程中临时暴露当前 context 给配置 refine 逻辑。

### 9.9.4 `jsr223HostConfiguration`

基于 `defaultJvmScriptingHostConfiguration` 构建，并注册：
- `jsr223.getScriptContext { weakThis.get()?.let { it.lastScriptContext ?: it.getContext() } }`

#### 设计意义
- 在 refine/配置阶段，需要拿当前脚本执行 context
- 但底层流程未必显式传了 context
- 所以这里通过 `WeakReference + lastScriptContext` 做桥接

### 9.9.5 `compilationConfiguration`

在 `baseCompilationConfiguration` 基础上：
1. 注入 hostConfiguration
2. 配置 REPL snippet identifier 生成规则

#### snippet 命名逻辑
会尝试从当前 ScriptContext 的 ENGINE_SCOPE bindings 中取 `KOTLIN_SCRIPT_STATE_BINDINGS_KEY` 对应 engineState。

若存在：
- 生成类似 `ScriptingHost<identityHash>_<defaultSnippetId>` 的类名

若不存在：
- 用默认 snippet identifier

#### 为什么这样做
注释写得很清楚：
- 避免“eval in eval”场景中 snippet 类名冲突

这说明在嵌套 eval 场景下，不同 engine state 的 snippet 类若名称重复，会产生 classloading clash。

### 9.9.6 `evaluationConfiguration`

比 compilationConfiguration 简单，只是注入 hostConfiguration。

### 9.9.7 `replCompiler` / `replEvaluator`

- `replCompiler = JvmReplCompiler(compilationConfiguration)`
- `localEvaluator = GenericReplCompilingEvaluatorBase(replCompiler, JvmReplEvaluator(evaluationConfiguration))`
- `replEvaluator` 返回 `localEvaluator`

说明这个 JSR223 引擎底层本质上是：
- REPL compiler + REPL evaluator 驱动

而不是一次性脚本独立执行模型。

### 9.9.8 `state`

当前引擎状态来自 `getCurrentState(getContext())`。

### 9.9.9 `overrideScriptArgs(context)`

直接把 `getScriptArgs(context)` 提供给底层。

这是脚本构造参数接 JSR223 bindings 的关键入口。

### 9.9.10 `invokeWrapper`

当前返回 `null`，说明默认不对 Invocable 调用加包装。

### 9.9.11 `backwardInstancesHistory`

从当前 REPL evaluator state 中取历史，并逆序映射出所有非空脚本实例。

这正是 `Invocable` 能力的接入点。

### 9.9.12 `baseClassLoader`

直接取 evaluationConfiguration 中的 `jvm.baseClassLoader`。

### 9.9.13 `compileAndEval(script, context)`

这是一个很重要的覆盖点。

逻辑：
1. `lastScriptContext = context`
2. 调 `super.compileAndEval(script, context)`
3. finally 中 `lastScriptContext = null`

#### 为什么这么做
源码注释已明说：
> TODO: find a way to pass context to evaluation directly and avoid this hack

也就是说，当前引擎为了让 refine / hostConfiguration 能感知“本次 eval 的 context”，使用了一个临时字段桥接。

### 9.9.14 本文件结论

这是 Kotlin JSR223 引擎真正跑起来的核心实现。它把：
- JSR223 ScriptContext
- Kotlin REPL 基础设施
- Host 配置
- snippet 唯一命名
- Invocable 历史实例

全部串在一起。

---

## 9.10 `jsr223/src/kotlin/script/experimental/jsr223/KotlinJsr223DefaultScript.kt`

这是默认 JSR223 脚本模板和默认配置定义文件。

### 9.10.1 `KotlinJsr223DefaultScript`

它被 `@KotlinScript` 标记，指定：
- `compilationConfiguration = KotlinJsr223DefaultScriptCompilationConfiguration`
- `evaluationConfiguration = KotlinJsr223DefaultScriptEvaluationConfiguration`

并继承：
- `ScriptTemplateWithBindings`

构造参数：
- `val jsr223Bindings: Bindings`

#### 这说明什么
JSR223 默认脚本模板本质上就是：
- 一个带 `Bindings` 的脚本模板

### 9.10.2 内部辅助：`myEngine`

从 bindings 里取 `KOTLIN_SCRIPT_ENGINE_BINDINGS_KEY` 对应 `ScriptEngine`。

### 9.10.3 `eval(script, newBindings)` / `eval(script)`

这两个方法允许脚本内部再触发新的 JSR223 `eval`。

#### 特别处理：`KOTLIN_SCRIPT_STATE_BINDINGS_KEY`
若新 bindings 中的 state 与当前 bindings 中的 state 是同一个对象，则临时移除后再 eval，再恢复。

#### 为什么这样做
防止嵌套 eval 时把同一个 REPL 状态对象错误复用或污染。

这是一个典型的 JSR223/REPL 交叉边界处理。

### 9.10.4 `createBindings()`

直接透传 `ScriptEngine.createBindings()`。

### 9.10.5 `KotlinJsr223DefaultScriptCompilationConfiguration`

配置内容：
1. `refineConfiguration.beforeCompiling(::configureProvidedPropertiesFromJsr223Context)`
2. `jsr223.importAllBindings(true)`
3. `jvm.jvmTarget(System.getProperty("java.specification.version"))`

#### 含义
默认 JSR223 脚本：
- 会自动把 context 中 bindings 暴露成 providedProperties
- 默认目标 JVM 版本跟当前运行环境一致

### 9.10.6 `KotlinJsr223DefaultScriptEvaluationConfiguration`

只做一件事：
- `refineConfigurationBeforeEvaluate(::configureProvidedPropertiesFromJsr223Context)`

即：
- 编译阶段补“属性声明”
- 执行阶段补“属性值”

### 9.10.7 本文件结论

它定义了 Kotlin 默认 JSR223 脚本的语义基础：
- bindings 是脚本输入世界的核心
- 默认自动导入 bindings
- 默认允许脚本内部继续 eval

---

## 9.11 `jsr223/src/kotlin/script/experimental/jsr223/KotlinJsr223DefaultScriptEngineFactory.kt`

这是默认 JSR223 `ScriptEngineFactory` 实现。

### 9.11.1 常量

`KOTLIN_JSR223_RESOLVE_FROM_CLASSLOADER_PROPERTY`

若系统属性设为 `true`：
- 依赖不先提取成 classpath
- 而是直接通过 `JvmDependencyFromClassLoader { Thread.currentThread().contextClassLoader }`
提供

否则：
- 从上下文 classloader 提取完整 classpath 再写进 dependencies

### 9.11.2 `scriptDefinition`

通过：
- `createJvmScriptDefinitionFromTemplate<KotlinJsr223DefaultScript>()`
得到默认脚本定义。

### 9.11.3 `dependenciesFromCurrentContext()`

这是一个带缓存的辅助函数。

逻辑：
1. 读取当前线程 contextClassLoader
2. 若它变化了，重新 `scriptCompilationClasspathFromContext(... wholeClasspath=true, unpackJarCollections=true)`
3. 否则复用 `lastClassPath`
4. `updateClasspath(classPath)`

#### 意义
在频繁创建 engine 的场景中减少 classpath 提取开销。

### 9.11.4 `getScriptEngine()`

创建 `KotlinJsr223ScriptEngineImpl`，并传入：
- 修改后的 compilationConfiguration
- scriptDefinition.evaluationConfiguration
- `getScriptArgs` lambda

其中 `getScriptArgs` 返回：
```kotlin
ScriptArgsWithTypes(
    arrayOf(it.getBindings(ScriptContext.ENGINE_SCOPE).orEmpty()),
    arrayOf(Bindings::class)
)
```

即默认脚本模板构造参数就是 ENGINE_SCOPE bindings。

### 9.11.5 设计要点

默认工厂已经把几个真实世界问题都考虑了：
- 上下文 classloader 可能经常变化
- fat jar 场景可能需要 unpack
- 某些环境直接依赖 classloader 比抽 classpath 更可靠

---

## 9.12 `jsr223/resources/META-INF/services/javax.script.ScriptEngineFactory`

内容只有一行：
- `kotlin.script.experimental.jsr223.KotlinJsr223DefaultScriptEngineFactory`

### 含义
这就是 Java SPI 注册点。

有了它，Java 侧可通过：
- `new ScriptEngineManager().getEngineBy...`
自动发现 Kotlin JSR223 引擎。

---

# 10. jvm-host + jsr223 总结

这两个模块一起完成了从“底层 scripting 能力”到“标准 Java/宿主可用 API”的最后一公里：

1. `BasicJvmScriptingHost`
   - 面向 Kotlin/宿主开发者的标准入口
2. `JvmScriptCompiler`
   - 接 K2/legacy isolated compiler proxy
3. `CompiledScriptJarsCache` + `jvmScriptSaving.kt`
   - 处理脚本编译产物的持久化与复用
4. `KotlinJsr223HostConfiguration` + `propertiesFromContext.kt`
   - 把 JSR223 `ScriptContext` 融入类型化配置系统
5. `KotlinJsr223ScriptEngineImpl`
   - 把 REPL、host、bindings、Invocable 串成真正 ScriptEngine
6. `KotlinJsr223DefaultScript*`
   - 给出开箱即用的默认 JSR223 模板与工厂
7. SPI 文件
   - 使引擎能被 Java 标准发现机制识别

如果你的目标是：
- 在 Java 系统里嵌入 Kotlin 脚本
- 做可缓存、可预编译的脚本平台
- 定制宿主脚本 API

那么 `jvm-host + jsr223` 基本就是核心阅读区。
