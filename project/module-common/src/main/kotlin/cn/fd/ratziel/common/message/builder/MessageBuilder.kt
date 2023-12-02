package cn.fd.ratziel.common.message.builder

import net.kyori.adventure.text.Component

/**
 * MessageBuilder - 消息构建者
 *
 * @author TheFloodDragon
 * @since 2023/12/2 12:22
 */
interface MessageBuilder {

    /**
     * 将组件序列化成字符串
     */
    fun serialize(source: Component): String

    /**
     * 从字符串中反序列成组件
     */
    fun deserialize(source: String): Component

}