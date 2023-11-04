package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.core.function.NullMark
import cn.fd.ratziel.core.function.isMarkedNull
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagType

fun emptyTagData() = ItemTagData(ItemTagType.STRING, NullMark)

fun emptyTag() = emptyTagData() as ItemTag

fun ItemTagData.isNull() = this.unsafeData().isMarkedNull()

fun String.asItemTagData() = ItemTagData(this)