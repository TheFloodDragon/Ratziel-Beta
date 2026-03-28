package cn.fd.ratziel.core.contextual

import cn.fd.ratziel.core.serialization.json.baseJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

private object FirstGroup : SerialGroup()
private object SecondGroup : SerialGroup()
private object AliasGroup : SerialGroup()

private interface FirstGroupKeys {
    companion object : FirstGroupKeys
}

private interface SecondGroupKeys {
    companion object : SecondGroupKeys
}

private interface AliasGroupKeys {
    companion object : AliasGroupKeys
}

private interface PlainKeys {
    companion object : PlainKeys
}

private val PlainKeys.shared by AttachedProperties.key(false)
private val FirstGroupKeys.shared by AttachedProperties.serialKey(FirstGroup, false)

private val SecondGroupKeys.shared by AttachedProperties.serialKey(SecondGroup, 0)

@SerialName("renamed")
@JsonNames("alias")
private val AliasGroupKeys.value by AttachedProperties.serialKey(AliasGroup, 0)

class AttachedPropertiesSerialTest {

    @Test
    fun `serial keys with same name from different groups are isolated in maps`() {
        val properties = AttachedProperties.Mutable().apply {
            this[FirstGroupKeys.shared] = true
            this[SecondGroupKeys.shared] = 7
        }.toImmutable()

        assertFalse(FirstGroupKeys.shared == SecondGroupKeys.shared)
        assertEquals(2, properties.entries.size)
        assertEquals(true, properties[FirstGroupKeys.shared])
        assertEquals(7, properties[SecondGroupKeys.shared])
    }

    @Test
    fun `plain keys do not equal serial keys with the same name`() {
        assertNotEquals(PlainKeys.shared, FirstGroupKeys.shared)
        assertNotEquals(FirstGroupKeys.shared, PlainKeys.shared)
    }

    @Test
    fun `group serializer is stable and only serializes its own keys`() {
        val properties = AttachedProperties.Mutable().apply {
            this[FirstGroupKeys.shared] = true
            this[SecondGroupKeys.shared] = 7
        }.toImmutable()

        assertSame(FirstGroup.serializer(), FirstGroup.serializer())

        val json = baseJson.encodeToJsonElement(FirstGroup.serializer(), properties) as JsonObject

        assertEquals(setOf("shared"), json.keys)
        assertEquals(JsonPrimitive(true), json["shared"])

        val restored = baseJson.decodeFromJsonElement(FirstGroup.serializer(), json)
        assertEquals(true, restored[FirstGroupKeys.shared])
        assertFalse(restored.containsKey(SecondGroupKeys.shared))
    }

    @Test
    fun `serial aliases participate in deserialization and serial name is used for serialization`() {
        val deserialized = buildJsonObject {
            put("alias", JsonPrimitive(9))
        }.toAttachedProperties(AliasGroup)

        assertEquals(9, deserialized[AliasGroupKeys.value])

        val serialized = AttachedProperties.Mutable().apply {
            this[AliasGroupKeys.value] = 12
        }.toImmutable().serializeToJson(AliasGroup)

        assertEquals(setOf("renamed"), serialized.keys)
        assertEquals(JsonPrimitive(12), serialized["renamed"])
    }
}
