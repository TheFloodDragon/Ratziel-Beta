package cn.fd.utilities.test

import cn.fd.utilities.script.ScriptF

object TestScript : ScriptF() {

    init {
        println("static test-02")
    }

    override fun init() {
        println("init-02")
        println(name)
    }

    override fun enable() {
        println("enable-02")
    }

    override fun disable() {
        println("disable-02")
    }

}