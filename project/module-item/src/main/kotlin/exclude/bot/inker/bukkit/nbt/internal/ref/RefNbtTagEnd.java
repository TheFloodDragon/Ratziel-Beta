package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagEnd")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/EndTag")
public final class RefNbtTagEnd extends RefNbtBase {
  @HandleBy(version = CbVersion.v1_15_R1, reference = "Lnet/minecraft/server/v1_16_R3/NBTTagEnd;b:Lnet/minecraft/server/v1_16_R3/NBTTagEnd;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/EndTag;INSTANCE:Lnet/minecraft/nbt/EndTag;")
  public static final RefNbtTagEnd INSTANCE = null;

  private RefNbtTagEnd() {
    throw new UnsupportedOperationException();
  }
}
