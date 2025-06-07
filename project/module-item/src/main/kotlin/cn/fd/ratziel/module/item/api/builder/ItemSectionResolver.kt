package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.function.ArgumentContext

/**
 * ItemSectionResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:16
 */
interface ItemSectionResolver {

    /**
     * 解析处理字符串
     *
     * @param section 要解析处理的部分 (字符串)
     * @param context 上下文
     */
    fun resolve(section: String, context: ArgumentContext): String

}