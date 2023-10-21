package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTBase")
@HandleBy(version = CbVersion.v1_13_R1, reference = "net/minecraft/server/v1_13_R1/NBTBase", isInterface = true)
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/Tag", isInterface = true)
public abstract class RefNbtBase {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTBase;createTag(B)Lnet/minecraft/server/v1_12_R1/NBTBase;", accessor = true)
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTBase;createTag(B)Lnet/minecraft/server/v1_13_R1/NBTBase;", isInterface = true)
  @HandleBy(version = CbVersion.v1_16_R1, reference = "")
  public static native RefNbtBase createTag(byte typeId);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTBase;getTypeId()B")
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTBase;getTypeId()B", isInterface = true)
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/Tag;getId()B", isInterface = true)
  public native byte getTypeId();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTBase;clone()Lnet/minecraft/server/v1_12_R1/NBTBase;")
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTBase;clone()Lnet/minecraft/server/v1_13_R1/NBTBase;", isInterface = true)
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/Tag;copy()Lnet/minecraft/nbt/Tag;", isInterface = true)
  public native RefNbtBase rClone();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTBase;c_()Ljava/lang/String;", accessor = true)
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTBase;asString()Ljava/lang/String;", isInterface = true)
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/Tag;getAsString()Ljava/lang/String;", isInterface = true)
  public native String asString();
}
