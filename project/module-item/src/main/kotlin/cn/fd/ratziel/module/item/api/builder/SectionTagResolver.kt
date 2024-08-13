package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.module.item.api.ArgumentResolver

/**
 * SectionTagResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/13 14:44
 */
interface SectionTagResolver :ArgumentResolver<Iterable<String>, String?>{

    /**
     * 解析器名称
     */
    val names: Array<String>

}