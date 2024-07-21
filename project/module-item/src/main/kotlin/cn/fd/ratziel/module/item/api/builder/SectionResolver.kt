package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.module.item.api.ArgumentResolver

/**
 * SectionResolver
 *
 * @author TheFloodDragon
 * @since 2024/7/8 17:53
 */
interface SectionResolver<E, T> : ArgumentResolver<E, T> {

    /**
     * StringResolver
     *
     * @author TheFloodDragon
     * @since 2024/6/25 20:16
     */
    interface StringResolver : SectionResolver<String, String>

    /**
     * TagResolver
     *
     * @author TheFloodDragon
     * @since 2024/6/25 20:04
     */
    interface TagResolver : SectionResolver<Iterable<String>, String?> {

        /**
         * 解析器名称集合
         */
        val names: Array<String>

    }

}