package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.api.component.transformer.DelegateTransformer
import cn.fd.ratziel.module.item.api.component.transformer.MinecraftComponentTransformer
import cn.fd.ratziel.module.item.api.component.transformer.SerialJsonTransformer
import cn.fd.ratziel.module.item.api.component.transformer.SerialNbtTransformer
import kotlinx.serialization.KSerializer

/**
 * TransformerBuilder
 * 
 * @author TheFloodDragon
 * @since 2026/1/1 22:51
 */

class TransformerBuilder<T>(val key: String, val serializer: KSerializer<T>) {

    private var jsonTransformer: ItemComponentType.JsonTransformer<T>? = null
    private var nbtTransformer: ItemComponentType.NbtTransformer<T>? = null
    private var nmsTransformer: MinecraftComponentTransformer<T>? = null

    fun jsonEntry(vararg alias: String) = this.apply {
        this.jsonTransformer = SerialJsonTransformer.EntryTransformer(
            serializer, ItemElement.json, key, *alias
        )
    }

    fun nbtEntry(path: String) = this.apply {
        this.nbtTransformer = SerialNbtTransformer.EntryTransformer(
            serializer, ItemElement.nbt, NbtPath(path)
        )
    }

    fun nbtEntry() = this.nbtEntry(ItemSheet.mapping(key))

    fun build(): ItemComponentType.Transformer<T> {
        val jsonTransformer = this.jsonTransformer ?: SerialJsonTransformer(serializer, ItemElement.json)
        val nbtTransformer = this.nbtTransformer ?: SerialNbtTransformer(serializer, ItemElement.nbt)
        return DelegateTransformer(jsonTransformer, nbtTransformer)
    }

}