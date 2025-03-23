package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.common.function.block.provided.ConditionBlock
import cn.fd.ratziel.core.function.block.ExecutableBlock
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.module.script.block.BlockParser
import cn.fd.ratziel.module.script.block.RecursingBlockParser
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic

/**
 * ConditionBlockParser
 *
 * @author TheFloodDragon
 * @since 2025/3/23 09:30
 */
object ConditionBlockParser : RecursingBlockParser {

    override fun parse(
        element: JsonElement,
        parser: BlockParser
    ): ConditionBlock? {
        if (element !is JsonObject) return null
        if (element.any { it.key in setOf("if", "condition", "conditions") }) {
            return createJson(parser).decodeFromJsonElement(ConditionBlock.serializer(), element)
        }
        return null
    }

    fun createJson(parser: BlockParser) = Json(baseJson) {
        serializersModule += SerializersModule {
            polymorphic(ExecutableBlock::class, createSerializer(parser))
        }
    }

    fun createSerializer(parser: BlockParser) = object : KSerializer<ExecutableBlock> {
        override val descriptor = PrimitiveSerialDescriptor("block.ExecutableBlock", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): ExecutableBlock {
            if (decoder is JsonDecoder) {
                val element = decoder.decodeJsonElement()
                return parser.parse(element)!!
            }
            throw UnsupportedOperationException("Only supported for JsonDecoder!")
        }

        override fun serialize(encoder: Encoder, value: ExecutableBlock) = throw UnsupportedOperationException("Not supported for serializing!")
    }

}