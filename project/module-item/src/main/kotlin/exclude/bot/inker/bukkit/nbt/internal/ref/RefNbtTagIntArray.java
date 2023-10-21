package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

import java.util.List;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagIntArray")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/IntArrayTag")
public final class RefNbtTagIntArray extends RefNbtBase {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagIntArray;<init>([I)V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/IntArrayTag;<init>([I)V")
  public RefNbtTagIntArray(int[] value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagIntArray;<init>(Ljava/util/List;)V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/IntArrayTag;<init>(Ljava/util/List;)V")
  public RefNbtTagIntArray(List<Integer> value) {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagIntArray;d()[I")
  @HandleBy(version = CbVersion.v1_14_R1, reference = "Lnet/minecraft/server/v1_14_R1/NBTTagIntArray;getInts()[I")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/IntArrayTag;getAsIntArray()[I")
  public native int[] getInts();
}
