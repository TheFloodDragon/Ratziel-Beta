package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.nbt.NBTData

fun <T : ItemComponent<T, D>, D : NBTData> T.transform(): D = this@transform.transformer().transform(this@transform)

fun <T : ItemComponent<T, D>, D : NBTData> T.detransform(from: D) = this@detransform.apply { transformer().detransform(this@apply, from) }