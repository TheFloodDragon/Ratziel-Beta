package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

@HandleBy(version = CbVersion.v1_12_R1, reference = "org/bukkit/inventory/ItemStack")
public final class RefBukkitItemStack {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lorg/bukkit/inventory/ItemStack;meta:Lorg/bukkit/inventory/meta/ItemMeta;", accessor = true)
  public @Nullable ItemMeta meta;
}
