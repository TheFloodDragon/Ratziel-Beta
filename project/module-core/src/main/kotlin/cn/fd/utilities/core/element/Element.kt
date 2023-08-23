package cn.fd.utilities.core.element

import taboolib.library.configuration.ConfigurationSection
import java.io.File

class Element(
    /**
     * 元素标识符
     */
    val id: String,
    /**
     * 元素类型
     */
    val type: ElementType,
    /**
     * 元素属性
     */
    private var conf: ConfigurationSection? //TODO Use Kotlin Serialization
) {

    /**
     * 元素地址
     */
    var address: ElementAddress? = null

    /**
     * 获取元素属性
     */
    fun getConfig(): ConfigurationSection? {
        return conf
    }

    /**
     * 其它构造器
     */
    constructor(
        /**
         * 元素标识符
         */
        id: String,
        /**
         * 元素所在文件路径(若为空则不是文件地址)
         */
        file: File?,
        /**
         * 元素类型所在空间
         */
        type: ElementType,
        /**
         * 元素属性
         */
        conf: ConfigurationSection?,
    ) : this(id, type, conf) {
        // 元素地址赋值
        this.address = ElementAddress(id, this.type, file)
    }

    constructor(
        /**
         * 元素地址
         */
        address: ElementAddress,
        /**
         * 元素属性
         */
        conf: ConfigurationSection?,
    ) : this(address.id, address.file, address.type, conf)

    constructor(
        /**
         * 元素标识符
         */
        id: String,
        /**
         * 元素所在文件路径(若为空则不是文件地址)
         */
        file: File?,
        /**
         * 元素类型所在空间
         */
        space: String,
        /**
         * 元素类型主名称
         */
        name: String,
        /**
         * 元素类型别名
         */
        alias: Set<String>,
        /**
         * 元素属性
         */
        conf: ConfigurationSection?,
    ) : this(id, file, ElementType(space, name, alias), conf)

    constructor(
        /**
         * 元素标识符
         */
        id: String,
        /**
         * 元素类型所在空间
         */
        space: String,
        /**
         * 元素类型主名称
         */
        name: String,
        /**
         * 元素类型别名
         */
        alias: Set<String>,
        /**
         * 元素属性
         */
        conf: ConfigurationSection?
    ) : this(id, ElementType(space, name, alias), conf)

}