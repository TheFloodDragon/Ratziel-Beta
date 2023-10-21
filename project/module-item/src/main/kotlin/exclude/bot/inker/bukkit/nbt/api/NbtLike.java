package exclude.bot.inker.bukkit.nbt.api;

public interface NbtLike {
  byte getId();

  String getAsString();

  NbtLike clone();
}
