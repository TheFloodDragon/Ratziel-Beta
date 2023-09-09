package cn.fd.ratziel.core.serialize

import kotlinx.serialization.json.*
import java.util.function.Function

/**
 * SerializeAdaptBuilder
 *
 * @author TheFloodDragon
 * @since 2023/9/2 15:30
 */
class SerializeAdaptBuilder(private val jsonElement: JsonElement) {

    /**
     * JsonObject作用域
     */
    fun <R> objectScope(function: Function<JsonObject, R>): R? {
        return defaultScope(function)
    }

    /**
     * JsonArray作用域
     */
    fun <R> arrayScope(function: Function<JsonArray, R>): R? {
        return defaultScope(function)
    }

    /**
     * JsonPrimitive作用域
     */
    fun <R> primitiveScope(function: Function<JsonPrimitive, R>): R? {
        return defaultScope(function)
    }

    /**
     * JsonNull作用域
     */
    fun <R> nullScope(function: Function<JsonNull, R>): R? {
        return defaultScope(function)
    }

    /**
     * 默认通用作用域
     */
    private inline fun <reified T, R> defaultScope(function: Function<T, R>): R? {
        return jsonElement.adapt().takeIf { it is T }?.let {
            function.apply(it as T)
        }
    }

    companion object {

        /**
         * 自适应构建器
         */
        inline fun adaptBuilder(
            jsonElement: JsonElement,
            block: SerializeAdaptBuilder.() -> Unit,
        ): SerializeAdaptBuilder {
            return SerializeAdaptBuilder(jsonElement).also(block)
        }

        @JvmName("adaptBuilderTyped")
        inline fun JsonElement.adaptBuilder(block: SerializeAdaptBuilder.() -> Unit): SerializeAdaptBuilder {
            return adaptBuilder(this, block)
        }

    }

}