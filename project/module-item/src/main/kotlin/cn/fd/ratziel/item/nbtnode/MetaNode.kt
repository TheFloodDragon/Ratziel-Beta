package cn.fd.ratziel.item.nbtnode

/**
 * MetaNode
 *
 * @author TheFloodDragon
 * @since 2023/10/27 20:41
 */
enum class MetaNode(val value: String) {
    NAME("Name"),
    LOCAL_NAME("LocName"),
    DISPLAY("display"),
    LORE("Lore"),
    CUSTOM_MODEL_DATA("CustomModelData"),
    ENCHANTMENTS("Enchantments"),
    ENCHANTMENTS_ID("id"),
    ENCHANTMENTS_LVL("lvl"),
    REPAIR("RepairCost"),
    ATTRIBUTES("AttributeModifiers"),
    ATTRIBUTES_IDENTIFIER("AttributeName"),
    ATTRIBUTES_NAME("Name"),
    ATTRIBUTES_VALUE("Amount"),
    ATTRIBUTES_TYPE("Operation"),
    ATTRIBUTES_UUID_HIGH("UUIDMost"),
    ATTRIBUTES_UUID_LOW("UUIDLeast"),
    ATTRIBUTES_SLOT("Slot"),
    HIDE_FLAGS("HideFlags"),
    UNBREAKABLE("Unbreakable"),
    DAMAGE("Damage"),
    BLOCK_DATA("BlockStateTag"),
}