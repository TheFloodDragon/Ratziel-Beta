package cn.fd.ratziel.core.serialization

import kotlinx.serialization.json.*
import java.util.function.Consumer

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
    lateinit var objectFunction: Consumer<JsonObject>
        protected set

    open fun objectScope(function: Consumer<JsonObject>) {
        if (!::objectFunction.isInitialized) objectFunction = function
    }

    /**
     * JsonArray
     */
    lateinit var arrayFunction: Consumer<JsonArray>
        protected set

    open fun arrayScope(function: Consumer<JsonArray>) {
        if (!::arrayFunction.isInitialized) arrayFunction = function
    }

    /**
     * JsonPrimitive
     */
    lateinit var primitiveFunction: Consumer<JsonPrimitive>
        protected set

    open fun primitiveScope(function: Consumer<JsonPrimitive>) {
        if (!::primitiveFunction.isInitialized) primitiveFunction = function
    }

    /**
     * JsonNull
     */
    lateinit var nullFunction: Consumer<JsonNull>
        protected set

    open fun nullScope(function: Consumer<JsonNull>) {
        if (!::nullFunction.isInitialized) nullFunction = function
    }

    /**
     * 运行对应方法
     */
    open fun run(): JsonElement {
        when (jsonElement) {
            is JsonObject -> objectFunction.accept(jsonElement)
            is JsonArray -> arrayFunction.accept(jsonElement)
            is JsonPrimitive -> primitiveFunction.accept(jsonElement)
            is JsonNull -> nullFunction.accept(jsonElement)
        }
        return jsonElement // 返回处理后的结果
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