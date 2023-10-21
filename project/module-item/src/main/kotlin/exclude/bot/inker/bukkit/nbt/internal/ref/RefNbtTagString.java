package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagString")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/StringTag")
public final class RefNbtTagString extends RefNbtBase {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagString;<init>(Ljava/lang/String;)V")
  @HandleBy(version = CbVersion.v1_15_R1, reference = "")
  public RefNbtTagString(String value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_15_R1, reference = "Lnet/minecraft/server/v1_15_R1/NBTTagString;a(Ljava/lang/String;)Lnet/minecraft/server/v1_15_R1/NBTTagString;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/StringTag;valueOf(Ljava/lang/String;)Lnet/minecraft/nbt/StringTag;")
  public native static RefNbtTagString of(String value);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagString;c_()Ljava/lang/String;")
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTTagString;asString()Ljava/lang/String;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/Tag;getAsString()Ljava/lang/String;", isInterface = true)
  public native String asString();
}
