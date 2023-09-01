package cn.fd.ratziel.test

import cn.fd.ratziel.script.ScriptF

object TestScript : ScriptF() {

    init {
        println("static test-01")
    }

    override fun init() {
        println("init-01")
        println(name)
    }

    override fun enable() {
        println("enable-01")
    }

    override fun disable() {
        println("disable-01")
    }

}