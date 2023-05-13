package cn.fd.utilities.module

import java.util.*

abstract class ModuleExpansion {

    //模块名称,也就是模块标识符
    abstract val name: String

    //模块作者
    open val author: String = if (Locale.getDefault().language == "zh_CN") "未知" else "UNKNOWN"

    //模块版本
    open val version: String = if (Locale.getDefault().language == "zh_CN") "未知" else "UNKNOWN"

    //是否启用模块
    open val enable: Boolean = true


    /*
      模块初始化方法,在模块启用前调用
      可用于更新模块启用状态
     */
    open fun init() {}


    /*
      模块重载方法,在模块启用后调用
      可用于加载模块配置文件
    */
    open fun load() {}

    //测试方法
    open fun printMyself() {
        println("Hello World")
        println("作者: $author \n名称: $name \n版本: $version")
        println("Test successful")
    }

    /**
     * 重载模块
     */
    fun reload() {
        //卸载模块
        this.unregister()
        //初始化方法
        init()
        //如果模块启用,就重新注册该模块
        if (enable) {
            this.register()
            //加载方法
            load()
        }
    }

    //注册此模块
    fun register(): Boolean {
        return ModuleManager.register(this)
    }

    //取消注册此模块
    fun unregister(): Boolean {
        return ModuleManager.unregister(this)
    }


}