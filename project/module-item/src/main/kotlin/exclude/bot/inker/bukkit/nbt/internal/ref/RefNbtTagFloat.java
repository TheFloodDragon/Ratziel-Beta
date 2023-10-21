package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagFloat")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/FloatTag")
public final class RefNbtTagFloat extends RefNbtNumber {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagFloat;<init>(F)V")
  @HandleBy(version = CbVersion.v1_16_R1, reference = "")
  public RefNbtTagFloat(float value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_15_R1, reference = "Lnet/minecraft/server/v1_15_R1/NBTTagFloat;a(F)Lnet/minecraft/server/v1_15_R1/NBTTagFloat;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/FloatTag;valueOf(F)Lnet/minecraft/nbt/FloatTag;")
  public static native RefNbtTagFloat of(float value);
}
