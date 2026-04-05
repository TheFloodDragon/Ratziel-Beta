@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.element

import cn.fd.ratziel.core.contextual.AttachedProperties
import cn.fd.ratziel.core.contextual.SerialGroup
import cn.fd.ratziel.core.contextual.serialKey
import kotlinx.serialization.json.JsonNames

/**
 * ElementConfiguration
 *
 * @author TheFloodDragon
 * @since 2026/4/5 13:11
 */
class ElementConfiguration(
    baseConfigurations: Iterable<AttachedProperties> = emptyList(),
    builder: Builder.() -> Unit = {},
) : AttachedProperties(Builder(baseConfigurations).apply(builder)) {

    class Builder(baseConfigurations: Iterable<AttachedProperties> = emptyList()) : AttachedProperties.Builder(baseConfigurations)

    companion object {

        /**
         * 元素配置序列化分组。
         */
        @JvmField
        val GROUP = SerialGroup("ElementGroup")

        init {
            elementName; elementType; filter; listen
        }

    }

}

/**
 * 基于当前配置创建新配置。
 */
fun ElementConfiguration?.with(builder: ElementConfiguration.Builder.() -> Unit): ElementConfiguration {
    val newConfiguration =
        if (this == null) ElementConfiguration(builder = builder)
        else ElementConfiguration(listOf(this), builder = builder)
    return if (newConfiguration == this) this else newConfiguration
}

/**
 * 使用文件名作为单元素解析名称时的表达式。
 */
const val FILE_NAME_ELEMENT_NAME = $$"$fn"


private typealias Keys = ElementConfiguration.Companion

/**
 * 指定整个文件解析为单个元素时的元素名称。
 *
 * 工作空间级配置仅允许使用 [FILE_NAME_ELEMENT_NAME]。
 */
val Keys.elementName by AttachedProperties.serialKey<String?>(ElementConfiguration.GROUP, null)

/**
 * 指定当前文件或工作空间默认使用的元素类型。
 */
@JsonNames("unified-type")
val Keys.elementType by AttachedProperties.serialKey<String?>(ElementConfiguration.GROUP, null)

/**
 * 指定工作空间文件过滤器。
 */
val Keys.filter by AttachedProperties.serialKey<String?>(ElementConfiguration.GROUP, null)

/**
 * 指定是否监听工作空间文件变更。
 */
val Keys.listen by AttachedProperties.serialKey<Boolean?>(ElementConfiguration.GROUP, null)
