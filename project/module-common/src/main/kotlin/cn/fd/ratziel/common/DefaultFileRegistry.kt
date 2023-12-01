package cn.fd.ratziel.common

@Deprecated("还是要尝试新的方法，这样一个个写注册太麻烦")
object DefaultFileRegistry {

    const val PATH: String = "default"

    val files: Array<String> = arrayOf(
        "actionTest.yml",
        "Item-Test01.yml"
    )

}