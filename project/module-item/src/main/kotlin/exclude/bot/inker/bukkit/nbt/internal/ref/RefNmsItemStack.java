package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/ItemStack")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/world/item/ItemStack")
public final class RefNmsItemStack {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/ItemStack;<init>(Lnet/minecraft/server/v1_12_R1/Item;)V")
  @HandleBy(version = CbVersion.v1_13_R1, reference = "")
  public RefNmsItemStack(RefItem item) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "")
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R2/ItemStack;<init>(Lnet/minecraft/server/v1_13_R2/IMaterial;)V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;)V")
  public RefNmsItemStack(RefIMaterial imaterial) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/ItemStack;hasTag()Z")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/world/item/ItemStack;hasTag()Z")
  public native boolean hasTag();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/ItemStack;getTag()Lnet/minecraft/server/v1_12_R1/NBTTagCompound;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/world/item/ItemStack;getTag()Lnet/minecraft/nbt/CompoundTag;")
  public native RefNbtTagCompound getTag();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/ItemStack;setTag(Lnet/minecraft/server/v1_12_R1/NBTTagCompound;)V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/world/item/ItemStack;setTag(Lnet/minecraft/nbt/CompoundTag;)V")
  public native void setTag(RefNbtTagCompound nbt);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/ItemStack;getItem()Lnet/minecraft/server/v1_12_R1/Item;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
  public native RefItem getItem();
}
