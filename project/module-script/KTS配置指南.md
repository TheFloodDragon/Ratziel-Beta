# Kotlin Scripting 配置项速查手册

> 面向开发者的快速查阅文档。
>
> 目标：把 `libraries/scripting` 中最重要的配置项按用途整理成一份参考手册，便于在开发自定义脚本平台、规则引擎、REPL、JSR223 接入时快速定位。

---

# 1. 使用说明

Kotlin Scripting 的配置体系主要分成三类：

1. `ScriptCompilationConfiguration`
2. `ScriptEvaluationConfiguration`
3. `ScriptingHostConfiguration`

此外还有平台扩展：
- `jvm.*`
- `jsr223.*`

理解方法：
- **编译配置**：脚本“可以看见什么、怎么编译”
- **执行配置**：脚本“运行时真正拿到什么、怎么执行”
- **Host 配置**：宿主系统“为脚本体系提供什么能力”

---

# 2. ScriptCompilationConfiguration 配置项

## 2.1 脚本识别与定义

### `displayName`
- 类型：`String`
- 作用：脚本类型显示名
- 典型用途：IDE 展示、日志展示、类型说明
- 开发建议：建议为每个自定义脚本类型都设置

### `defaultIdentifier`
- 类型：`String`
- 默认值：`"Script"`
- 作用：默认生成脚本类名
- 典型用途：在未指定更具体命名时作为类名基础
- 注意：可能影响日志、调试可读性

### `fileExtension`
- 类型：`String`
- 默认值：`"kts"`
- 作用：脚本文件扩展名
- 典型用途：脚本定义发现、IDE 文件识别
- 开发建议：自定义脚本类型尽量使用明确且不冲突的扩展名

### `filePathPattern`
- 类型：`String`
- 作用：额外路径匹配规则
- 典型用途：当多个脚本类型共用扩展名时，通过路径区分
- 示例场景：`config/**/*.kts` 与 `rules/**/*.kts`

### `isStandalone`
- 类型：`Boolean`
- 默认值：`true`
- 作用：脚本是否始终按 standalone 方式处理
- 典型用途：控制与其他源文件联合编译时的行为
- 注意：Gradle 某些脚本模板会特殊处理为 `false`

---

## 2.2 类型系统与脚本上下文

### `baseClass`
- 类型：`KotlinType`
- 默认值：`Any`
- 作用：脚本生成类的基类
- 典型用途：
  - 给脚本提供宿主 API
  - 定义构造器参数
  - 承载公共 DSL 函数
- 开发建议：自定义脚本平台通常都会设置它

### `implicitReceivers`
- 类型：`List<KotlinType>`
- 作用：声明脚本可用的隐式 receiver 类型
- 典型用途：DSL 风格脚本
- 示例：
  - `ProjectContext`
  - `RuleBuilder`
  - `DeploymentDsl`
- 注意：执行时必须按顺序传对应实例

### `providedProperties`
- 类型：`Map<String, KotlinType>`
- 作用：声明脚本中可用的外部变量
- 典型用途：
  - `env`
  - `config`
  - `now`
  - `user`
- 注意：执行时必须提供同名值

---

## 2.3 文件与源码输入相关

### `scriptFileLocationVariable`
- 类型：`String`
- 作用：把脚本文件位置注入为某个变量名
- 典型用途：脚本内部读取当前脚本路径

### `scriptFileLocation`
- 类型：`File`
- 作用：当前脚本文件位置
- 典型用途：
  - 相对导入
  - 同目录资源访问
  - diagnostics 定位

### `sourceFragments`
- 类型：`List<ScriptSourceNamedFragment>`
- 作用：只编译源码的某些片段
- 典型用途：
  - 增量编译
  - IDE 局部分析
  - 代码片段执行

---

## 2.4 import 与结果相关

### `defaultImports`
- 类型：`List<String>`
- 作用：脚本默认 import 列表
- 典型用途：减少样板代码、提升 DSL 体验
- 开发建议：对自定义脚本平台非常常用

### `importScripts`
- 类型：`List<SourceCode>`
- 作用：当前脚本编译时一并导入的脚本
- 典型用途：
  - 公共脚本模块
  - 脚本级依赖
- 注意：这些脚本后续执行时会先于当前脚本执行

### `resultField`
- 类型：`String`
- 默认值：`"$$result"`
- 作用：生成脚本类中保存结果的字段名
- 典型用途：
  - REPL
  - 结果提取
- 注意：通常不需要修改，但需要理解其存在

---

## 2.5 依赖与编译行为

### `dependencies`
- 类型：`List<ScriptDependency>`
- 作用：脚本依赖列表
- JVM 常见实现：
  - `JvmDependency(classpath)`
  - `JvmDependencyFromClassLoader(...)`
- 典型用途：给脚本提供额外 jar 或 classes 目录

### `compilerOptions`
- 类型：`List<String>`
- 作用：底层编译器参数
- 典型用途：
  - 控制语言版本
  - 开启/关闭实验特性
  - 调整编译行为
- 注意：错误的 compilerOptions 可能直接导致编译失败

### `hostConfiguration`
- 类型：`ScriptingHostConfiguration`
- 作用：与编译相关的宿主配置
- 典型用途：
  - 传入默认类加载策略
  - 传递脚本类型解析能力
  - 让 refine 能访问 host 能力

---

## 2.6 编译阶段动态修正（Refine）

### `refineConfigurationBeforeParsing`
- 类型：`List<RefineConfigurationUnconditionallyData>`
- 触发时机：脚本解析前
- 典型用途：注入基础环境配置

### `refineConfigurationOnAnnotations`
- 类型：`List<RefineConfigurationOnAnnotationsData>`
- 触发时机：解析出文件级注解后
- 典型用途：
  - `@DependsOn`
  - `@Repository`
  - 自定义注解驱动依赖和行为扩展

### `refineConfigurationBeforeCompiling`
- 类型：`List<RefineConfigurationUnconditionallyData>`
- 触发时机：真正编译前
- 典型用途：做最后的 classpath / 选项修正

---

# 3. ScriptEvaluationConfiguration 配置项

## 3.1 运行时上下文

### `implicitReceivers`
- 类型：`List<Any?>`
- 作用：实际注入脚本的 receiver 对象
- 对应编译配置：`ScriptCompilationConfiguration.implicitReceivers`
- 注意：顺序必须一致

### `providedProperties`
- 类型：`Map<String, Any?>`
- 作用：实际注入脚本的变量值
- 对应编译配置：`ScriptCompilationConfiguration.providedProperties`
- 注意：键名与值类型应与编译期声明兼容

### `constructorArgs`
- 类型：`List<Any?>`
- 作用：脚本基类构造器额外参数
- 典型用途：
  - 模板基类需要固定参数
  - 传业务上下文对象

### `compilationConfiguration`
- 类型：`ScriptCompilationConfiguration`
- 作用：回链真实编译配置
- 典型用途：执行前 refine 读取编译信息

---

## 3.2 REPL 与共享状态

### `previousSnippets`
- 类型：`List<Any?>`
- 作用：REPL 历史脚本实例
- 典型用途：逐段执行
- 注意：第一段也应显式传空列表

### `scriptsInstancesSharing`
- 类型：`Boolean`
- 默认值：`false`
- 作用：是否共享 imported script 的实例
- 典型用途：避免多个 import 路径重复实例化同一脚本
- 适用场景：有状态脚本、模块脚本

---

## 3.3 执行流程控制

### `hostConfiguration`
- 类型：`ScriptingHostConfiguration`
- 作用：执行相关宿主配置
- 典型用途：
  - 注入执行上下文
  - 获取 host 自定义能力

### `refineConfigurationBeforeEvaluate`
- 类型：`List<RefineEvaluationConfigurationData>`
- 触发时机：真正执行前
- 典型用途：按运行时环境补变量/控制包装器

### `scriptExecutionWrapper`
- 类型：`ScriptExecutionWrapper<*>`
- 作用：包裹真实脚本执行
- 典型用途：
  - 计时
  - 日志
  - 安全边界
  - ThreadLocal / MDC 注入
  - 自定义异常处理

---

# 4. ScriptingHostConfiguration 配置项

## 4.1 通用 Host 能力

### `configurationDependencies`
- 类型：`List<ScriptDependency>`
- 作用：配置类和 refine 回调自身所需依赖
- 典型用途：模板类、配置类不在默认类路径中时提供其依赖

### `getScriptingClass`
- 类型：`GetScriptingClass`
- 作用：把 `KotlinType` 解析成真实 `KClass`
- 典型用途：
  - 模板类加载
  - 配置类加载
- 注意：这是模板解析能否工作的重要前提

### `getEvaluationContext`
- 类型：`(ScriptingHostConfiguration) -> ScriptEvaluationContextData`
- 作用：给执行 refine 提供 host 级上下文
- 典型用途：
  - 命令行参数
  - request/session 信息
  - 多租户上下文

---

# 5. JVM 扩展配置项

## 5.1 编译配置：`ScriptCompilationConfiguration.jvm.*`

### `jdkHome`
- 类型：`File`
- 作用：JDK 根目录
- 典型用途：指定编译环境所使用的 JDK

### `jvmTarget`
- 类型：`String`
- 作用：目标字节码版本
- 典型用途：
  - 与宿主运行环境一致
  - 控制脚本字节码兼容级别

---

## 5.2 执行配置：`ScriptEvaluationConfiguration.jvm.*`

### `baseClassLoader`
- 类型：`ClassLoader?`
- 默认值：host JVM baseClassLoader 或线程上下文 classloader
- 作用：脚本类加载父加载器
- 典型用途：决定脚本可访问哪些宿主类
- 极其重要：类可见性问题首先看它

### `lastSnippetClassLoader`
- 类型：`ClassLoader?`
- 作用：REPL 中上一段 snippet 的类加载器
- 典型用途：增量执行时维持历史可见性

### `loadDependencies`
- 类型：`Boolean`
- 默认值：`true`
- 作用：执行前是否根据 dependencies 加载依赖
- 适用场景：
  - 若依赖已全部存在于 baseClassLoader，可设为 `false`

### `mainArguments`
- 类型：`Array<out String>`
- 作用：main 风格脚本执行参数
- 典型用途：命令行脚本

---

## 5.3 Host 配置：`ScriptingHostConfiguration.jvm.*`

### `jdkHome`
- 类型：`File`
- 作用：JVM host 使用的 JDK 根目录

### `baseClassLoader`
- 类型：`ClassLoader`
- 作用：JVM 默认类加载基础
- 默认行为：若配置依赖存在，可能基于配置依赖构建隔离 URLClassLoader
- 典型用途：
  - 模板与配置类加载
  - 宿主/脚本隔离

### `compilationCache`
- 类型：`CompiledJvmScriptsCache`
- 作用：编译缓存实现
- 典型用途：重复脚本编译优化

### `disableCompilationCache`
- 类型：`Boolean`
- 默认值：`false`
- 作用：显式禁用缓存

---

# 6. JSR223 扩展配置项

## 6.1 Host：`ScriptingHostConfiguration.jsr223.*`

### `getScriptContext`
- 类型：`() -> ScriptContext?`
- 作用：动态获取当前 `ScriptContext`
- 典型用途：
  - 编译 refine 从 bindings 推导变量
  - 执行 refine 从 bindings 取值

---

## 6.2 编译配置：`ScriptCompilationConfiguration.jsr223.*`

### `getScriptContext`
- 类型：`() -> ScriptContext?`
- 作用：编译阶段读取当前 JSR223 context

### `importAllBindings`
- 类型：`Boolean`
- 默认值：`false`
- 作用：是否把 bindings 自动导入为 providedProperties
- 典型用途：默认 JSR223 脚本模板中通常设为 `true`

---

## 6.3 执行配置：`ScriptEvaluationConfiguration.jsr223.*`

### `getScriptContext`
- 类型：`() -> ScriptContext?`
- 作用：执行阶段从 JSR223 context 读取实际变量值

---

# 7. 扩展点速查

## 7.1 最常用扩展点

### 模板扩展
- `@KotlinScript`
- 自定义 `baseClass`

### 编译动态扩展
- `refineConfiguration.beforeParsing`
- `refineConfiguration.onAnnotations`
- `refineConfiguration.beforeCompiling`

### 执行动态扩展
- `refineConfigurationBeforeEvaluate`
- `scriptExecutionWrapper`

### 类加载扩展
- `jvm.baseClassLoader`
- `JvmDependencyFromClassLoader`
- `getScriptingClass`

### 缓存扩展
- `CompiledJvmScriptsCache`
- `CompiledScriptJarsCache`

### JSR223 扩展
- `importAllBindings`
- `Invocable`
- 动态代理接口实现

---

# 8. 最常见配置组合建议

## 8.1 自定义 DSL 脚本

建议配置：
- `baseClass`
- `implicitReceivers`
- `defaultImports`
- `fileExtension`

## 8.2 规则脚本平台

建议配置：
- `providedProperties`
- `scriptExecutionWrapper`
- `compilationCache`
- `dependencies`

## 8.3 REPL / 控制台

建议配置：
- `previousSnippets`
- `jvm.lastSnippetClassLoader`
- `resultField`

## 8.4 Java / JSR223 集成

建议配置：
- `jsr223.getScriptContext`
- `jsr223.importAllBindings`
- `Bindings`
- `Invocable`

---

# 9. 开发建议

1. 模板优先：先定义清楚脚本类型，再谈执行细节
2. 配置分层：编译配置、执行配置、host 配置不要混着想
3. diagnostics 优先：不要只靠异常处理脚本失败
4. 类加载器谨慎：`baseClassLoader` 往往是排障第一入口
5. 缓存要带配置维度：不要只按文件名缓存
6. K2 升级要回归：特别关注构造参数顺序与执行行为

---

# 10. 一句话总结

如果你把 Kotlin Scripting 当作一个“可插拔的 Kotlin 脚本平台框架”，那这份配置参考表就是它的控制面板：

- 编译配置决定脚本能写什么
- 执行配置决定脚本运行时拿到什么
- Host 配置决定宿主给脚本系统提供什么能力
- JVM / JSR223 扩展决定它如何落地到真实环境中
