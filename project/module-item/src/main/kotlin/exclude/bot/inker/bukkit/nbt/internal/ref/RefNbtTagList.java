package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTTagList")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/ListTag")
public final class RefNbtTagList extends RefNbtBase {
  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagList;<init>()V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/ListTag;<init>()V")
  public RefNbtTagList() {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagList;i(I)Lnet/minecraft/server/v1_12_R1/NBTBase;")
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTTagList;c(I)Lnet/minecraft/server/v1_13_R1/NBTBase;")
  @HandleBy(version = CbVersion.v1_14_R1, reference = "Lnet/minecraft/server/v1_14_R1/NBTTagList;get(I)Lnet/minecraft/server/v1_14_R1/NBTBase;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Ljava/util/List;get(I)Ljava/lang/Object;", isInterface = true)
  public native RefNbtBase get(int index);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagList;size()I")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/ListTag;size()I")
  public native int size();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagList;a(ILnet/minecraft/server/v1_12_R1/NBTBase;)V")
  @HandleBy(version = CbVersion.v1_13_R1, reference = "")
  public native void set0(int index, RefNbtBase element);

  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTTagList;set(ILnet/minecraft/server/v1_13_R1/NBTBase;)Lnet/minecraft/server/v1_13_R1/NBTBase;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/CollectionTag;set(ILnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;")
  public native RefNbtBase set1(int index, RefNbtBase element);

  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/ListTag;setTag(ILnet/minecraft/nbt/Tag;)Z")
  public native boolean set2(int index, RefNbtBase element);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagList;add(Lnet/minecraft/server/v1_12_R1/NBTBase;)V")
  @HandleBy(version = CbVersion.v1_13_R1, reference = "")
  public native void add0(RefNbtBase element);

  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTTagList;add(Lnet/minecraft/server/v1_13_R1/NBTBase;)Z")
  @HandleBy(version = CbVersion.v1_14_R1, reference = "")
  public native boolean add1(RefNbtBase element);

  @HandleBy(version = CbVersion.v1_14_R1, reference = "Lnet/minecraft/server/v1_14_R1/NBTTagList;add(ILnet/minecraft/server/v1_14_R1/NBTBase;)V")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Ljava/util/List;add(ILjava/lang/Object;)V", isInterface = true)
  public native void add2(int index, RefNbtBase element);

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTTagList;remove(I)Lnet/minecraft/server/v1_12_R1/NBTBase;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Ljava/util/List;remove(I)Ljava/lang/Object;", isInterface = true)
  public native RefNbtBase remove(int index);
}