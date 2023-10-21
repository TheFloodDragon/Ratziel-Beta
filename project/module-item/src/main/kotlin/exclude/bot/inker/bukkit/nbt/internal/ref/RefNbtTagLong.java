package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagLong")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/LongTag")
public final class RefNbtTagLong extends RefNbtNumber {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagLong;<init>(J)V")
  @HandleBy(version = CbVersion.v1_15_R1, reference = "")
  public RefNbtTagLong(long value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_15_R1, reference = "Lnet/minecraft/server/v1_15_R1/NBTTagLong;a(J)Lnet/minecraft/server/v1_15_R1/NBTTagLong;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/LongTag;valueOf(J)Lnet/minecraft/nbt/LongTag;")
  public static native RefNbtTagLong of(long value);
}
