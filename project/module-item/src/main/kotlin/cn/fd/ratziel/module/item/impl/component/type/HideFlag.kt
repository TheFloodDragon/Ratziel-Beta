package cn.fd.ratziel.module.item.impl.component.type

import cn.fd.ratziel.module.item.internal.serializers.HideFlagSerializer
import kotlinx.serialization.Serializable
import taboolib.library.xseries.XItemFlag

/**
 * HideFlag
 * 
 * @author TheFloodDragon
 * @since 2026/4/11 20:56
 */

typealias HideFlag = @Serializable(HideFlagSerializer::class) XItemFlag
