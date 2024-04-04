@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.json.*

val baseJson by lazy {
    Json {
        // 宽松模式
        isLenient = true
        // 忽略未知键
        ignoreUnknownKeys = true
        // 隐式空值
        explicitNulls = false
        // 美观的打印方式
        prettyPrint = true
        // 枚举类不区分大小写
        decodeEnumsCaseInsensitive = true
        // 默认序列化模组
        serializersModule = baseSerializers
    }
}

fun JsonObject.getTentatively(vararg keys: String): JsonElement? = keys.firstNotNullOfOrNull { this[it] }

/**
 * 简便方法, 默认 "serialName" 为 全类名
 */
@Suppress("UnusedReceiverParameter")
inline fun <reified T> KSerializer<T>.primitiveDescriptor(kind: PrimitiveKind) =
    PrimitiveSerialDescriptor(T::class.java.name, kind)

@Suppress("UnusedReceiverParameter")
inline fun <reified T> KSerializer<T>.buildClassDescriptor(
    vararg typeParameters: SerialDescriptor,
    noinline builderAction: ClassSerialDescriptorBuilder.() -> Unit = {},
) = buildClassSerialDescriptor(T::class.java.name, *typeParameters, builderAction = builderAction)

/**
 * @return 所有元素注解的迭代器
 */
val SerialDescriptor.elementAnnotations: Iterable<List<Annotation>>
    get() = Iterable {
        object : Iterator<List<Annotation>> {
            private var elementsLeft = elementsCount
            override fun hasNext(): Boolean = elementsLeft > 0
            override fun next() = getElementAnnotations(elementsCount - (elementsLeft--))
        }
    }

/**
 * 构造一个空Json如"{}"
 */
fun emptyJson() = JsonObject(emptyMap())

/**
 * 简单的Json检查
 */
fun String.isJson(): Boolean = startsWith('{') && endsWith('}')

/**
 * JsonPrimitive类型的判断
 */
fun JsonPrimitive.isInt() = !this.isString && this.intOrNull != null

fun JsonPrimitive.isLong() = !this.isString && this.longOrNull != null

fun JsonPrimitive.isBoolean() = !this.isString && this.booleanOrNull != null

fun JsonPrimitive.isDouble() = !this.isString && this.doubleOrNull != null

fun JsonPrimitive.isFloat() = !this.isString && this.floatOrNull != null