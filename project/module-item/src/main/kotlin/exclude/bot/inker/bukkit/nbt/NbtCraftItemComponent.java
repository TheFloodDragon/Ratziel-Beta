package exclude.bot.inker.bukkit.nbt;

import exclude.bot.inker.bukkit.nbt.api.NbtComponentLike;
import exclude.bot.inker.bukkit.nbt.internal.ref.RefCraftItemStack;

public final class NbtCraftItemComponent extends NbtCompound implements NbtComponentLike {
  NbtCraftItemComponent(RefCraftItemStack itemStack) {
    super(NbtUtils.getOrCreateTag(itemStack.handle));
  }
}
