package cn.fd.ratziel.module.item.nbt

/**
 * NBTType - NBT类型
 *
 * 来自 net.minecraft.nbt.TagTypes
 *
 * @author TheFloodDragon
 * @since 2024/3/15 18:59
 */
enum class NBTType(@JvmField val id: Byte) {

    END(0),
    BYTE(1),
    SHORT(2),
    INT(3),
    LONG(4),
    FLOAT(5),
    DOUBLE(6),
    BYTE_ARRAY(7),
    STRING(8),
    LIST(9),
    COMPOUND(10),
    INT_ARRAY(11),
    LONG_ARRAY(12);

}