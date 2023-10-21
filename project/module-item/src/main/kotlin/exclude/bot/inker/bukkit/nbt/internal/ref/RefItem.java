package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/Item")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/world/item/Item")
public class RefItem {
    @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/Item;getName()Ljava/lang/String;")
    @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/world/item/Item;getDescriptionId()Ljava/lang/String;")
    public native String getDescriptionId();

    @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/Item;a(Lnet/minecraft/server/v1_12_R1/ItemStack;)Ljava/lang/String;")
    @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/Item;h(Lnet/minecraft/server/v1_13_R1/ItemStack;)Ljava/lang/String;")
    @HandleBy(version = CbVersion.v1_14_R1, reference = "Lnet/minecraft/server/v1_14_R1/Item;f(Lnet/minecraft/server/v1_14_R1/ItemStack;)Ljava/lang/String;")
    @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/world/item/Item;getDescriptionId(Lnet/minecraft/world/item/ItemStack;)Ljava/lang/String;")
    public native String getDescriptionId(RefNmsItemStack itemStack);
}
