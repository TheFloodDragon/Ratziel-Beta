package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

import java.util.List;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagByteArray")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/ByteArrayTag")
public final class RefNbtTagByteArray extends RefNbtBase {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagByteArray;<init>([B)V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/ByteArrayTag;<init>([B)V")
  public RefNbtTagByteArray(byte[] value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagByteArray;<init>(Ljava/util/List;)V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/ByteArrayTag;<init>(Ljava/util/List;)V")
  public RefNbtTagByteArray(List<Byte> value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagByteArray;c()[B")
  @HandleBy(version = CbVersion.v1_14_R1, reference = "Lnet/minecraft/server/v1_16_R3/NBTTagByteArray;getBytes()[B")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/ByteArrayTag;getAsByteArray()[B")
  public native byte[] getBytes();
}
