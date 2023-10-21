package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagDouble")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/DoubleTag")
public final class RefNbtTagDouble extends RefNbtNumber {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagDouble;<init>(D)V")
  @HandleBy(version = CbVersion.v1_15_R1, reference = "")
  public RefNbtTagDouble(double value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_15_R1, reference = "Lnet/minecraft/server/v1_15_R1/NBTTagDouble;a(D)Lnet/minecraft/server/v1_15_R1/NBTTagDouble;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/DoubleTag;valueOf(D)Lnet/minecraft/nbt/DoubleTag;")
  public static native RefNbtTagDouble of(double value);
}
