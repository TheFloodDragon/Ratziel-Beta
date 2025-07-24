@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.internal.serializers.SoundInstanceSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * SoundInstance - 声音事件
 *
 * @author TheFloodDragon
 * @since 2025/6/7 08:42
 */
@KeepGeneratedSerializer
@Serializable(SoundInstanceSerializer::class)
class SoundInstance(
    /**
     * 声音事件引用
     */
    @SerialName("sound_id")
    @JsonNames("sound", "soundId")
    var sound: NamespacedIdentifier,
    /**
     * 最远传播距离
     */
    var range: Float = 16f,
)