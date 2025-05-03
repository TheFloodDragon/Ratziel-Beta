package cn.fd.ratziel.core.serialization.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * JsonTree
 *
 * @author TheFloodDragon
 * @since 2025/5/3 17:22
 */
class JsonTree(
    /**
     * 根节点
     */
    val root: Node,
) {

    constructor(element: JsonElement) : this(parseToNode(element, null))

    /**
     * 转化成 [JsonElement]
     */
    fun toElement(): JsonElement {
        return parseToElement(root)
    }

    companion object {

        private fun parseToNode(element: JsonElement, parent: Node?): Node = when (element) {
            is JsonObject -> ObjectNode(element.mapValues { parseToNode(it.value, parent) }.toMutableMap(), parent)
            is JsonArray -> ArrayNode(element.map { parseToNode(it, parent) }.toMutableList(), parent)
            is JsonPrimitive -> PrimitiveNode(element, parent)
        }

        private fun parseToElement(node: Node): JsonElement = when (node) {
            is ObjectNode -> JsonObject(node.value.mapValues { parseToElement(it.value) })
            is ArrayNode -> JsonArray(node.value.map { parseToElement(it) })
            is PrimitiveNode -> node.value
        }

    }

    /**
     * Node
     */
    sealed interface Node {

        /**
         * 父节点 (空代表此节点为跟节点)
         */
        val parent: Node?

    }

    /**
     * ObjectNode
     */
    class ObjectNode(
        val value: MutableMap<String, Node>,
        override val parent: Node?,
    ) : Node

    /**
     * ArrayNode
     */
    class ArrayNode(
        val value: MutableList<Node>,
        override val parent: Node?,
    ) : Node

    /**
     * PrimitiveNode
     */
    class PrimitiveNode(
        var value: JsonPrimitive,
        override val parent: Node?,
    ) : Node

}