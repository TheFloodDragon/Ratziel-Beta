package cn.fd.ratziel.module.item.nbt

import taboolib.module.nms.NMSItemTag
import taboolib.module.nms.nmsProxy

fun NBTData.toNMS() : Any = nmsProxy<NMSItemTag>().itemTagToNMSCopy(this)

fun nbtDataFormNMS(nmsTag: Any): NBTData = nmsProxy<NMSItemTag>().itemTagToBukkitCopy(nmsTag)