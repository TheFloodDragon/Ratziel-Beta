package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.common.function.block.provided.ConditionBlock
import cn.fd.ratziel.module.script.block.BlockParser
import cn.fd.ratziel.module.script.block.RecursingBlockParser
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * ConditionBlockParser
 *
 * @author TheFloodDragon
 * @since 2025/3/23 09:30
 */
object ConditionBlockParser : RecursingBlockParser {

    override fun parse(element: JsonElement, parser: BlockParser): ConditionBlock? {
        if (element !is JsonObject) return null
        val valueIf = element["if"] ?: element["condition"] ?: return null
        val valueThen = element["then"]
        val valueElse = element["else"]
        return ConditionBlock(
            parser.parse(valueIf)!!,
            valueThen?.let { parser.parse(it) },
            valueElse?.let { parser.parse(it) }
        )
    }

//
//    override fun parse(
//        element: JsonElement,
//        parser: BlockParser
//    ): ConditionBlock? {
//        if (element !is JsonObject) return null
//        if (element.any { it.key in setOf("if", "condition", "conditions") }) {
//            return createJson(parser).decodeFromJsonElement(ConditionBlock.serializer(), element)
//        }
//        return null
//    }
//
//    fun createJson(parser: BlockParser) = Json(baseJson) {
//        serializersModule += SerializersModule {
//            polymorphic(ExecutableBlock::class, createSerializer(parser))
//        }
//    }
//
//    fun createSerializer(parser: BlockParser) = object : KSerializer<ExecutableBlock> {
//        override val descriptor = PrimitiveSerialDescriptor("block.ExecutableBlock", PrimitiveKind.STRING)
//        override fun deserialize(decoder: Decoder): ExecutableBlock {
//            if (decoder is JsonDecoder) {
//                val element = decoder.decodeJsonElement()
//                return parser.parse(element)!!
//            }
//            throw UnsupportedOperationException("Only supported for JsonDecoder!")
//        }
//
//        override fun serialize(encoder: Encoder, value: ExecutableBlock) = throw UnsupportedOperationException("Not supported for serializing!")
//    }

}