package cn.fd.ratziel.compat.hook

/**
 * HookMap
 * key - 类编号, 调用链接类需要通过类编号获取类并通过反射调用里面的方法
 * value - 类编号对应的链接类
 *
 * @author TheFloodDragon
 * @since 2024/2/17 12:10
 */
class HookMap(baseMap: Map<Int, Class<*>>) : HashMap<Int,Class<*>>(baseMap) {

    constructor() : this(emptyMap())

}