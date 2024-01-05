@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.json.*
import java.util.function.Function

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
    }
}

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
 * 构造一个空Json如"{}"
 */
fun emptyJson() = JsonObject(emptyMap())

/**
 * 简单的Json检查
 */
fun String.isJson(): Boolean = startsWith('{') && endsWith('}')

/**
 * 编辑Json对象
 * @return 编辑后的Json对象
 */
fun JsonObject.edit(action: HashMap<String, JsonElement>.() -> Unit) = JsonObject(LinkedHashMap(this).apply(action))

/**
 * 对一个Json的所有Primitive值进行处理
 * @param element 原始 Json 元素
 * @param handle 处理方法
 * @return 最终处理结果
 */
fun handlePrimitives(element: JsonElement, handle: Function<JsonPrimitive, JsonElement>): JsonElement =
    when (element) {
        is JsonPrimitive -> handle.apply(element)
        is JsonArray -> buildJsonArray { element.jsonArray.forEach { add(handlePrimitives(it, handle)) } }
        is JsonObject -> buildJsonObject {
            element.jsonObject.forEach { key, value ->
                put(key, handlePrimitives(value, handle))
            }
        }
    }


/**
 * JsonPrimitive类型的判断
 */
fun JsonPrimitive.isInt() = !this.isString && this.intOrNull != null

fun JsonPrimitive.isLong() = !this.isString && this.longOrNull != null

fun JsonPrimitive.isBoolean() = !this.isString && this.booleanOrNull != null

fun JsonPrimitive.isDouble() = !this.isString && this.doubleOrNull != null

fun JsonPrimitive.isFloat() = !this.isString && this.floatOrNull != null

fun JsonPrimitive.adapt(): Any = if (isString || this is JsonNull) content else {
    booleanOrNull ?: intOrNull ?: doubleOrNull ?: longOrNull ?: floatOrNull
}!!