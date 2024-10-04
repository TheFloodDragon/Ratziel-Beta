package cn.fd.ratziel.script

import cn.fd.ratziel.core.env.CoreEnv
import taboolib.common.ClassAppender
import taboolib.common.LifeCycle
import taboolib.common.env.JarRelocation
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake


/**
 * ScriptManager
 *
 * @author TheFloodDragon
 * @since 2024/7/16 11:30
 */
object ScriptManager {

    /**
     * 默认使用的的脚本语言
     */
    var default = ScriptType.JAVASCRIPT
        private set

    @Deprecated("Will be remove when taboolib update")
    @Awake(LifeCycle.INIT)
    private fun init() {
        loadEnv(
            value = "!org.openjdk.nashorn:nashorn-core:15.4",
            test = "!org.openjdk.nashorn.api.scripting.NashornScriptEngine",
            transitive = true
        )
        loadEnv(
            value = "!org.apache.commons:commons-jexl3:3.4.0",
            test = "!org.apache.commons.jexl3.JexlEngine",
            transitive = false
        )

        loadEnv(
            value = "!org.jetbrains.kotlin:kotlin-reflect:" + CoreEnv.KOTLIN_VERSION,
            test = "!kotlin.reflect.jvm.ReflectLambdaKt",
            transitive = false
        );loadEnv(
            value = "!org.jetbrains.kotlin:kotlin-compiler-embeddable:" + CoreEnv.KOTLIN_VERSION,
            transitive = false
        );loadEnv(
            value = "!org.jetbrains.kotlin:kotlin-script-runtime:" + CoreEnv.KOTLIN_VERSION,
            transitive = false
        );loadEnv(
            value = "!org.jetbrains.kotlin:kotlin-scripting-common:" + CoreEnv.KOTLIN_VERSION,
            transitive = false
        );loadEnv(
            value = "!org.jetbrains.kotlin:kotlin-scripting-jvm:" + CoreEnv.KOTLIN_VERSION,
            transitive = false
        );loadEnv(
            value = "!org.jetbrains.kotlin:kotlin-scripting-jvm-host:" + CoreEnv.KOTLIN_VERSION,
            transitive = false
        );loadEnv(
            value = "!org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:" + CoreEnv.KOTLIN_VERSION,
            transitive = false
        );loadEnv(
            value = "!org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:" + CoreEnv.KOTLIN_VERSION,
            transitive = false
        );loadEnv(
            value = "!org.jetbrains.intellij.deps:trove4j:1.0.20200330",
            test = "!gnu.trove.TObjectHashingStrategy",
            transitive = false
        )
    }

    /**
     * 加载指定环境环境
     */
    @Deprecated("Will be remove when taboolib update")
    fun loadEnv(value: String, test: String = "", transitive: Boolean = false, relocations: List<JarRelocation> = emptyList()) {
        fun real(url: String) = if (url.startsWith("!")) url.substring(1) else url
        // 检查测试类是否存在
        val testClass = real(test)
        if (testClass.isNotEmpty() && !ClassAppender.isExists(testClass)) return
        // 加载类
        RuntimeEnv.ENV_DEPENDENCY.loadDependency((real(value)), transitive, relocations)
    }

}