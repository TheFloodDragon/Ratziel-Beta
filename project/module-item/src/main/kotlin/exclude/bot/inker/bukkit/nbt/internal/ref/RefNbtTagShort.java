package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagShort")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/ShortTag")
public final class RefNbtTagShort extends RefNbtNumber {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagShort;<init>(S)V")
  @HandleBy(version = CbVersion.v1_15_R1, reference = "")
  public RefNbtTagShort(short value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_15_R1, reference = "Lnet/minecraft/server/v1_15_R1/NBTTagShort;a(S)Lnet/minecraft/server/v1_15_R1/NBTTagShort;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/ShortTag;valueOf(S)Lnet/minecraft/nbt/ShortTag;")
  public static native RefNbtTagShort of(short value);
}
