package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.nbt.NBTCompound

fun <T : ItemComponent<in T>> T.transform(): NBTCompound = this.transformer().transform(this)

fun <T : ItemComponent<in T>> T.detransform(from: NBTCompound) = this.apply { transformer().detransform(this@apply, from) }