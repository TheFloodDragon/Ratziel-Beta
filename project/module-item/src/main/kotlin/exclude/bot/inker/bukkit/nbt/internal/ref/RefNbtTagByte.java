package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagByte")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/ByteTag")
public final class RefNbtTagByte extends RefNbtNumber {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagByte;<init>(B)V")
  @HandleBy(version = CbVersion.v1_15_R1, reference = "")
  public RefNbtTagByte(byte value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_15_R1, reference = "Lnet/minecraft/server/v1_15_R1/NBTTagByte;a(B)Lnet/minecraft/server/v1_15_R1/NBTTagByte;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/ByteTag;valueOf(B)Lnet/minecraft/nbt/ByteTag;")
  public static native RefNbtTagByte of(byte value);

  @HandleBy(version = CbVersion.v1_15_R1, reference = "Lnet/minecraft/server/v1_15_R1/NBTTagByte;a(Z)Lnet/minecraft/server/v1_15_R1/NBTTagByte;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/ByteTag;valueOf(Z)Lnet/minecraft/nbt/ByteTag;")
  public static native RefNbtTagByte of(boolean value);
}
