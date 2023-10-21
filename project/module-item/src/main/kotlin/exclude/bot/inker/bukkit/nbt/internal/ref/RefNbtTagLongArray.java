package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

import java.util.List;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagLongArray")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/LongArrayTag")
public final class RefNbtTagLongArray extends RefNbtBase {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagLongArray;b:[J", accessor = true)
  public long[] longs;

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagLongArray;<init>([J)V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/LongArrayTag;<init>([J)V")
  public RefNbtTagLongArray(long[] value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagLongArray;<init>(Ljava/util/List;)V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/LongArrayTag;<init>(Ljava/util/List;)V")
  public RefNbtTagLongArray(List<Long> value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTTagLongArray;d()[J")
  @HandleBy(version = CbVersion.v1_14_R1, reference = "Lnet/minecraft/server/v1_14_R1/NBTTagLongArray;getLongs()[J")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/LongArrayTag;getAsLongArray()[J")
  public native long[] getLongs();
}
