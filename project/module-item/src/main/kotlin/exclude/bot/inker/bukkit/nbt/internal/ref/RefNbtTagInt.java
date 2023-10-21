package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagInt")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/IntTag")
public final class RefNbtTagInt extends RefNbtNumber {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagInt;<init>(I)V")
  @HandleBy(version = CbVersion.v1_15_R1, reference = "")
  public RefNbtTagInt(int value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_15_R1, reference = "Lnet/minecraft/server/v1_15_R1/NBTTagInt;a(I)Lnet/minecraft/server/v1_15_R1/NBTTagInt;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/IntTag;valueOf(I)Lnet/minecraft/nbt/IntTag;")
  public static native RefNbtTagInt of(int value);
}
