package cn.fd.ratziel.common.block

import cn.fd.ratziel.common.block.BlockConfigurationKeys.WeakKey
import cn.fd.ratziel.core.contextual.AttachedProperties
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * BlockConfiguration
 *
 * @author TheFloodDragon
 * @since 2025/11/14 18:32
 */
open class BlockConfiguration(
    baseConfigurations: Iterable<BlockConfiguration> = emptyList(), body: BlockConfiguration.() -> Unit = {},
) : BlockConfigurationKeys, AttachedProperties.Mutable(baseConfigurations) {

    init {
        // 应用初始化配置
        body.invoke(this)
    }

    /**
     * 清除所有的 [WeakKey]
     */
    internal fun cleanWeakKeys() {
        val iterator = properties.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (key is WeakKey<*>) {
                iterator.remove()
            }
        }
    }

    override fun toString() = "BlockConfiguration($properties)"

}

interface BlockConfigurationKeys {

    companion object : BlockConfigurationKeys { // For directly get
        @JvmStatic
        fun <T> weakKey(getDefaultValue: AttachedProperties.() -> T) = PropertyWeakKeyDelegate(getDefaultValue)
    }

    class WeakKey<T>(name: String, getDefaultValue: AttachedProperties.() -> T) : AttachedProperties.Key<T>(name, getDefaultValue) {
        override fun toString() = "WeakKey($name)"
    }

    class PropertyWeakKeyDelegate<T>(private val getDefaultValue: AttachedProperties.() -> T) : ReadOnlyProperty<Any?, WeakKey<T>> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): WeakKey<T> = WeakKey(property.name, getDefaultValue)
    }
}


// basic configurations

/**
 * 工作文件
 */
val BlockConfigurationKeys.workFile by AttachedProperties.key<File?>(null)

/**
 * 运行时是否复制参数上下文
 */
val BlockConfigurationKeys.copyContext by AttachedProperties.key(false)
