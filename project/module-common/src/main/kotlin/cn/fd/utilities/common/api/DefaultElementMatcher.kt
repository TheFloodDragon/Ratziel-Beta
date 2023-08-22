package cn.fd.utilities.common.api

import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.api.ElementMatcher
import taboolib.module.configuration.Configuration

/**
 * DefaultElementMatcher
 *
 * @author: TheFloodDragon
 * @since 2023/8/22 14:20
 */
object DefaultElementMatcher : ElementMatcher {

    //    /**
//     * 两种情况
//     * 1.
//     *   Actions:
//     *     action1: ....
//     *     action2: ....
//     * 2. TestAction:
//     *      action: ....
//     */
//    fun match(obj: Configuration, file: File? = null): Set<Element> {
//        var tmp: Set<Element> = setOf()
//        //第一种情况处理
//        obj.getKeys(false) //获取元素标识符列表
//            .forEach {
//                val type = it.pat
//                tmp.plus(Element(ElementAddress(it,)))
//            }
//    }
//
//    override fun match(obj: Configuration): Set<Element> {
//        return this.match(obj, null)
//    }
    override fun match(obj: Configuration): Set<Element> {
        TODO("Not yet implemented")
    }

}