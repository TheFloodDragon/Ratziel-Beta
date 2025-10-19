package cn.fd.ratziel.module.item.feature.update

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter

/**
 * UpdateInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/10/19 11:20
 */
class UpdateInterpreter : ItemInterpreter {

    /**
     * 受保护的 [NbtPath] 列表
     */
    val protectedPaths: MutableList<NbtPath> = arrayListOf()

}