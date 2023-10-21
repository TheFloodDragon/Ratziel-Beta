package exclude.bot.inker.bukkit.nbt.internal.ref;

import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;

@HandleBy(version = CbVersion.v1_12_R1, reference = "net/minecraft/server/v1_12_R1/NBTBase", accessor = true)
@HandleBy(version = CbVersion.v1_13_R1, reference = "net/minecraft/server/v1_13_R1/NBTNumber")
@HandleBy(version = CbVersion.v1_17_R1, reference = "net/minecraft/nbt/NumericTag")
public abstract class RefNbtNumber extends RefNbtBase {
  RefNbtNumber() {
    throw new UnsupportedOperationException();
  }

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTNumber;d()J", accessor = true)
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTNumber;asLong()J")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/NumericTag;getAsLong()J")
  public native long asLong();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTNumber;e()I", accessor = true)
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTNumber;asInt()I")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/NumericTag;getAsInt()I")
  public native int asInt();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTNumber;f()S", accessor = true)
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTNumber;asShort()S")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/NumericTag;getAsShort()S")
  public native short asShort();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTNumber;g()B", accessor = true)
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTNumber;asByte()B")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/NumericTag;getAsByte()B")
  public native byte asByte();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTNumber;asDouble()D", accessor = true)
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTNumber;asDouble()D")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/NumericTag;getAsDouble()D")
  public native double asDouble();

  @HandleBy(version = CbVersion.v1_12_R1, reference = "Lnet/minecraft/server/v1_12_R1/NBTNumber;i()F", accessor = true)
  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTNumber;asFloat()F")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/NumericTag;getAsFloat()F")
  public native float asFloat();

  @HandleBy(version = CbVersion.v1_13_R1, reference = "Lnet/minecraft/server/v1_13_R1/NBTNumber;j()Ljava/lang/Number;")
  @HandleBy(version = CbVersion.v1_17_R1, reference = "Lnet/minecraft/nbt/NumericTag;getAsNumber()Ljava/lang/Number;")
  public native Number asNumber();
}
