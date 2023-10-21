package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "org/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem$ItemMetaKey", accessor = true)
public final class RefItemMetaKey {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem$ItemMetaKey;BUKKIT:Ljava/lang/String;", accessor = true)
  public final String BUKKIT = null;

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/craftbukkit/v1_12_R1/inventory/CraftMetaItem$ItemMetaKey;NBT:Ljava/lang/String;", accessor = true)
  public final String NBT = null;
}
