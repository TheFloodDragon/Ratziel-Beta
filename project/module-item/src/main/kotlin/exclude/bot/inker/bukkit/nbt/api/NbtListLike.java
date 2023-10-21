package exclude.bot.inker.bukkit.nbt.api;

import exclude.bot.inker.bukkit.nbt.Nbt;

public interface NbtListLike extends NbtCollectionLike<Nbt<?>> {
  NbtListLike clone();
}
