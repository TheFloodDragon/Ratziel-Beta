package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.module.item.api.ArgumentResolver
import kotlinx.serialization.json.JsonElement

/**
 * ItemResolver - 物品配置解析
 *
 * @author TheFloodDragon
 * @since 2024/4/14 12:01
 */
@Deprecated("Use JsonTransofmer")
interface ItemResolver : ArgumentResolver<JsonElement, JsonElement>