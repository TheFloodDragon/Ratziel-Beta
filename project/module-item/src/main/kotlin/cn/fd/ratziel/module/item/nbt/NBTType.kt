package cn.fd.ratziel.module.item.nbt

/**
 * NBTType - NBT类型
 *
 * 来自 net.minecraft.nbt.TagTypes
 *
 * @author TheFloodDragon
 * @since 2024/3/15 18:59
 */
enum class NBTType(val id: Byte, val alias: Array<String> = emptyArray()) {

    END(0),
    BYTE(1, arrayOf("b")),
    SHORT(2, arrayOf("s")),
    INT(3, arrayOf("i")),
    LONG(4, arrayOf("l")),
    FLOAT(5, arrayOf("f")),
    DOUBLE(6, arrayOf("d")),
    BYTE_ARRAY(7, arrayOf("byteArray", "ba")),
    STRING(8, arrayOf("t", "str")),
    LIST(9, arrayOf("a", "list", "array")),
    COMPOUND(10, arrayOf("c", "cpd", "tag", "compound")),
    INT_ARRAY(11, arrayOf("intArray", "ia")),
    LONG_ARRAY(12, arrayOf("longArray", "la"));

    val signName get() = name.lowercase()

}