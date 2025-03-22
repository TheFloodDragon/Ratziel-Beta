package cn.fd.ratziel.module.item.internal

import cn.altawk.nbt.SerialNameDeterminer
import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * NbtNameDeterminer
 *
 * @author TheFloodDragon
 * @since 2025/3/22 16:35
 */
internal object NbtNameDeterminer : SerialNameDeterminer {
    override fun determineName(index: Int, descriptor: SerialDescriptor): String {
        val parent = descriptor.serialName.substringAfterLast('.')
        val elementName = descriptor.getElementName(index)
        val mappedName = ItemSheet.mappings[parent to elementName]
        return mappedName ?: elementName
    }

    override fun mapName(elementName: String, descriptor: SerialDescriptor): String {
        val address = ItemSheet.mappingsReversed[elementName] ?: return elementName
        val parent = descriptor.serialName.substringAfterLast('.')
        return if (address.first == parent) address.second else elementName
    }
}