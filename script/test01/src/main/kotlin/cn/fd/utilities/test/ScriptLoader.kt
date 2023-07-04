package cn.fd.utilities.test

import cn.fd.utilities.script.ScriptF

object TestScript : ScriptF() {

    init {
        println("static test")
    }

    override fun init() {
        println("init")
        println(name)
    }

    override fun enable() {
        println("enable")
    }

    override fun disable() {
        println("disable")
    }

}