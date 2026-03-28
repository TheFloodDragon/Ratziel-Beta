package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.transformer.JsonTransformer
import cn.fd.ratziel.module.item.api.component.transformer.MinecraftTransformer
import cn.fd.ratziel.module.item.api.component.transformer.NbtTransformer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertFailsWith

class ComponentTypeBuilderTest {

    @Test
    fun `旧版本在仅提供 json 和 nbt 时自动补齐 minecraft transformer`() {
        val nbt = StubNbtTransformer()
        val type = withVersion(12004) {
            ComponentTypeBuilder(
                id = "demo",
                type = String::class.java,
                serializer = String.serializer(),
            ).json(StubJsonTransformer())
                .nbt(nbt)
                .build()
        }

        assertSame(nbt, type.transforming.nbtTransformer)
        assertIs<LegacyMinecraftTransformer<*>>(type.transforming.minecraftTransformer)
    }

    @Test
    fun `新版本缺少 minecraft transformer 时构建失败`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            withVersion(12005) {
                ComponentTypeBuilder(
                    id = "demo",
                    type = String::class.java,
                    serializer = String.serializer(),
                ).json(StubJsonTransformer())
                    .nbt(StubNbtTransformer())
                    .build()
            }
        }

        assertContains(ex.message.orEmpty(), "MinecraftTransformer is required")
    }

    @Test
    fun `受支持组件统一暴露 nbt 与 minecraft transformer`() {
        val nbt = StubNbtTransformer()
        val minecraft = StubMinecraftTransformer()
        val type = withVersion(12005) {
            ComponentTypeBuilder(
                id = "demo",
                type = String::class.java,
                serializer = String.serializer(),
            ).json(StubJsonTransformer())
                .nbt(nbt)
                .minecraft(minecraft)
                .build()
        }

        assertNotNull(type.transforming.jsonTransformer)
        assertSame(nbt, type.transforming.nbtTransformer)
        assertSame(minecraft, type.transforming.minecraftTransformer)
    }

    private inline fun <T> withVersion(versionId: Int, block: () -> T): T {
        val previous = ComponentTypeBuilder.versionIdProvider
        ComponentTypeBuilder.versionIdProvider = { versionId }
        return try {
            block()
        } finally {
            ComponentTypeBuilder.versionIdProvider = previous
        }
    }

    private class StubJsonTransformer : JsonTransformer<String> {
        override fun toJsonElement(component: String) = JsonPrimitive(component)
        override fun formJsonElement(element: JsonElement) =
            if (element is JsonPrimitive) element.content else null
    }

    private class StubNbtTransformer : NbtTransformer<String> {
        override fun write(root: NbtCompound, component: String) {
            root["value"] = cn.altawk.nbt.tag.NbtString(component)
        }

        override fun read(root: NbtCompound): String? {
            return root["value"]?.content as? String
        }

        override fun remove(root: NbtCompound) {
            root.remove("value")
        }
    }

    private class StubMinecraftTransformer : MinecraftTransformer<String> {
        override fun read(nmsItem: Any): String? = null
        override fun write(nmsItem: Any, component: String) = Unit
        override fun remove(nmsItem: Any) = Unit
    }

}
