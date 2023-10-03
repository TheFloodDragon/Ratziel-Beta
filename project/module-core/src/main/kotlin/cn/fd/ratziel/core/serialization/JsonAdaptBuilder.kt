package cn.fd.ratziel.core.serialization

import kotlinx.serialization.json.*
import java.util.function.Function

/**
 * JsonAdaptBuilder
 *
 * @author TheFloodDragon
 * @since 2023/9/2 15:30
 */
open class JsonAdaptBuilder(private val jsonElement: JsonElement) {

    /**
     * JsonObject
     */
    open fun <R> objectScope(function: Function<JsonObject, R>) = defaultScope(function)

    /**
     * JsonArray
     */
    open fun <R> arrayScope(function: Function<JsonArray, R>) = defaultScope(function)

    /**
     * JsonPrimitive
     */
    open fun <R> primitiveScope(function: Function<JsonPrimitive, R>) = defaultScope(function)

    /**
     * JsonNull
     */
    open fun <R> nullScope(function: Function<JsonNull, R>) = defaultScope(function)

    /**
     * 默认通用作用域
     */
    private inline fun <reified T, R> defaultScope(function: Function<T, R>) =
        jsonElement.adapt().takeIf { it is T }?.let {
            function.apply(it as T)
        }

    companion object {

        /**
         * 自适应构建器
         */
        inline fun adaptBuilder(
            jsonElement: JsonElement,
            block: JsonAdaptBuilder.() -> Unit,
        ): JsonAdaptBuilder {
            return JsonAdaptBuilder(jsonElement).also(block)
        }

        @JvmName("jsonAdaptBuilder")
        inline fun JsonElement.adaptBuilder(block: JsonAdaptBuilder.() -> Unit): JsonAdaptBuilder {
            return adaptBuilder(this, block)
        }

    }

}