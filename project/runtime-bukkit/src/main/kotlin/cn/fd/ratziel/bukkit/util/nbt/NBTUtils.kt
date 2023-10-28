package cn.fd.ratziel.bukkit.util.nbt

import taboolib.module.nms.NMSItemTag
import taboolib.module.nms.nmsProxy

fun NBTTagData.toNMS() : Any = nmsProxy<NMSItemTag>().itemTagToNMSCopy(this)

fun nbtDataFormNMS(nmsTag: Any): NBTTagData = nmsProxy<NMSItemTag>().itemTagToBukkitCopy(nmsTag)