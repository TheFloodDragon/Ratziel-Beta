package cn.fd.utilities.script

abstract class ScriptF {

    /**
     * 脚本标识符(名称)
     */
    val name: String = this.javaClass.name

    /**
     * 脚本被加载时触发
     */
    abstract fun init()

    /**
     * 脚本启用时触发
     */
    abstract fun enable()

    /**
     * 脚本禁用时触发
     */
    abstract fun disable()

}