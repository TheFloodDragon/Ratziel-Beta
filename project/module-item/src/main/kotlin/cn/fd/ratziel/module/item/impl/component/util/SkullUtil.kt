@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component.util

import cn.fd.ratziel.module.item.impl.BukkitMaterial
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.xseries.profiles.builder.XSkull
import taboolib.library.xseries.profiles.objects.Profileable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

typealias SkullData = @Serializable(SkullUtil.Serializer::class) CompletableFuture<@Polymorphic ItemStack>

/**
 * SkullUtil - 头颅工具
 *
 * @author TheFloodDragon
 * @since 2024/8/12 16:13
 */
object SkullUtil {

    /**
     * 头颅缓存
     */
    private val CACHE: MutableMap<String, SkullData> = ConcurrentHashMap()

    fun fetchSkull(profileStr: String): ItemStack {
        return fetchSkullFuture(profileStr).get()
    }

    fun fetchSkullFuture(profileStr: String): SkullData {
        return CACHE.computeIfAbsent(profileStr) { generateSkullItem(profileStr) }
    }

    fun generateSkullItem(profileStr: String): SkullData {
        return XSkull.createItem().profile(Profileable.detect(profileStr)).applyAsync()
    }

    fun fetchSkull(skullMeta: SkullMeta): ItemStack {
        return ItemStack(BukkitMaterial.PLAYER_HEAD).apply { itemMeta = skullMeta }
    }

    fun fetchProfileString(skullItem: ItemStack): String? {
        return XSkull.of(skullItem).profileString
    }

    object Serializer : KSerializer<SkullData> {

        override val descriptor = PrimitiveSerialDescriptor("item.SkullData", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): SkullData {
            return fetchSkullFuture(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: SkullData) {
            val profileString = fetchProfileString(value.get())
            if (profileString == null) encoder.encodeNull() else encoder.encodeString(profileString)
        }

    }

}