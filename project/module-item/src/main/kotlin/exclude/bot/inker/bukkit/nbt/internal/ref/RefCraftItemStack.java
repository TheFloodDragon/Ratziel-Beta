package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;
import org.bukkit.inventory.ItemStack;

@HandleBy(version = CbVersion.v1_12_R1, reference = "org/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack")
public final class RefCraftItemStack {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;handle:Lnet/minecraft/server/v1_12_R1/ItemStack;", accessor = true)
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;handle:Lnet/minecraft/world/item/ItemStack;", accessor = true)
  public RefNmsItemStack handle;

  private RefCraftItemStack() {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;asNMSCopy(Lorg/bukkit/inventory/ItemStack;)Lnet/minecraft/server/v1_12_R1/ItemStack;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;asNMSCopy(Lorg/bukkit/inventory/ItemStack;)Lnet/minecraft/world/item/ItemStack;")
  public static native RefNmsItemStack asNMSCopy(ItemStack original);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;copyNMSStack(Lnet/minecraft/server/v1_12_R1/ItemStack;I)Lnet/minecraft/server/v1_12_R1/ItemStack;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;copyNMSStack(Lnet/minecraft/world/item/ItemStack;I)Lnet/minecraft/world/item/ItemStack;")
  public static native RefNmsItemStack copyNMSStack(RefNmsItemStack original, int amount);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;asBukkitCopy(Lnet/minecraft/server/v1_12_R1/ItemStack;)Lorg/bukkit/inventory/ItemStack;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;asBukkitCopy(Lnet/minecraft/world/item/ItemStack;)Lorg/bukkit/inventory/ItemStack;")
  public static native ItemStack asBukkitCopy(RefNmsItemStack original);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;asCraftMirror(Lnet/minecraft/server/v1_12_R1/ItemStack;)Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;asCraftMirror(Lnet/minecraft/world/item/ItemStack;)Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;")
  public static native RefCraftItemStack asCraftMirror(RefNmsItemStack original);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;asCraftCopy(Lorg/bukkit/inventory/ItemStack;)Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;")
  public static native RefCraftItemStack asCraftCopy(ItemStack original);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;asNewCraftStack(Lnet/minecraft/server/v1_12_R1/Item;)Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;asNewCraftStack(Lnet/minecraft/world/item/Item;)Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;")
  public static native RefCraftItemStack asNewCraftStack(RefItem original);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;asNewCraftStack(Lnet/minecraft/server/v1_12_R1/Item;I)Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftItemStack;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;asNewCraftStack(Lnet/minecraft/world/item/Item;I)Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftItemStack;")
  public static native RefCraftItemStack asNewCraftStack(RefItem original, int amount);
}
