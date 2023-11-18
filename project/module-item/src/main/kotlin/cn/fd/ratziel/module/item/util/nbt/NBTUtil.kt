package cn.fd.ratziel.module.item.util.nbt

import taboolib.module.nms.NMSItemTag
import taboolib.module.nms.nmsProxy

/**
 * 将包装的NBT数据转为NMS形式
 */
fun NBTData.toNMS(): Any = nmsProxy<NMSItemTag>().itemTagToNMSCopy(this)

/**
 * 将NMS的NBT数据转为包装形式
 */
fun nbtFromNMS(nmsData: Any): NBTData = nmsProxy<NMSItemTag>().itemTagToBukkitCopy(nmsData)