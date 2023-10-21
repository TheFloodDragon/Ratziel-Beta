package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

import java.util.Map;
import java.util.Set;

@HandleBy(version = CbVersion.v1_12_R1, reference = "org/bukkit/inventory/meta/ItemMeta")
public final class RefCraftMetaItem {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem;HANDLED_TAGS:Ljava/util/Set;", accessor = true)
  public static Set<String> HANDLED_TAGS;
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem;unhandledTags:Ljava/util/Map;", accessor = true)
  public Map<String, RefNbtBase> unhandledTags;

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem;applyToItem(Lnet/minecraft/server/v1_12_R1/NBTTagCompound;)V", accessor = true)
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lorg/bukkit/craftbukkit/v1_17_R1/inventory/CraftMetaItem;applyToItem(Lnet/minecraft/nbt/CompoundTag;)V", accessor = true)
  public native void applyToItem(RefNbtTagCompound itemTag);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem;NAME:Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem$ItemMetaKey;", accessor = true)
  public static final RefItemMetaKey NAME = null;

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem;DISPLAY:Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem$ItemMetaKey;", accessor = true)
  public static final RefItemMetaKey DISPLAY = null;
}
