package exclude.bot.inker.bukkit.nbt;

import exclude.bot.inker.bukkit.nbt.api.NbtLike;
import exclude.bot.inker.bukkit.nbt.internal.loader.CallSiteInstaller;
import exclude.bot.inker.bukkit.nbt.internal.ref.*;
import org.jetbrains.annotations.Nullable;

public abstract class Nbt<NMS extends RefNbtBase> implements NbtLike {
  static {
    if (RefNbtBase.class.getName().equals("bot.inker.bukkit.nbt.loader.ref.RefNbtBase")) {
      CallSiteInstaller.Spy.throwException("CallSiteNbt loaded before CallSiteNbt installed, you should invoke CallSiteNbt#install before load it.");
    }
  }

  final NMS delegate;

  Nbt(NMS delegate) {
    this.delegate = delegate;
  }

  @SuppressWarnings("unchecked")
  static @Nullable <T extends RefNbtBase> Nbt<T> fromNms(@Nullable T source) {
    if (source == null) {
      return null;
    } else if (source instanceof RefNbtTagByte) {
      return (Nbt<T>) NbtByte.fromNmsImpl((RefNbtTagByte) source);
    } else if (source instanceof RefNbtTagByteArray) {
      return (Nbt<T>) new NbtByteArray((RefNbtTagByteArray) source);
    } else if (source instanceof RefNbtTagCompound) {
      return (Nbt<T>) new NbtCompound((RefNbtTagCompound) source);
    } else if (source instanceof RefNbtTagDouble) {
      return (Nbt<T>) NbtDouble.fromNmsImpl((RefNbtTagDouble) source);
    } else if (source instanceof RefNbtTagEnd) {
      return (Nbt<T>) NbtEnd.INSTANCE;
    } else if (source instanceof RefNbtTagFloat) {
      return (Nbt<T>) NbtFloat.fromNmsImpl((RefNbtTagFloat) source);
    } else if (source instanceof RefNbtTagInt) {
      return (Nbt<T>) NbtInt.fromNmsImpl((RefNbtTagInt) source);
    } else if (source instanceof RefNbtTagIntArray) {
      return (Nbt<T>) new NbtIntArray((RefNbtTagIntArray) source);
    } else if (source instanceof RefNbtTagList) {
      return (Nbt<T>) new NbtList((RefNbtTagList) source);
    } else if (source instanceof RefNbtTagLong) {
      return (Nbt<T>) NbtLong.fromNmsImpl((RefNbtTagLong) source);
    } else if (source instanceof RefNbtTagLongArray) {
      return (Nbt<T>) new NbtLongArray((RefNbtTagLongArray) source);
    } else if (source instanceof RefNbtTagShort) {
      return (Nbt<T>) NbtShort.fromNmsImpl((RefNbtTagShort) source);
    } else if (source instanceof RefNbtTagString) {
      return (Nbt<T>) NbtString.fromNmsImpl((RefNbtTagString) source);
    } else {
      throw new UnsupportedOperationException("unknown source: " + source.getClass());
    }
  }

  @Override
  public byte getId() {
    return delegate.getTypeId();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Nbt && delegate.equals(((Nbt<?>) o).delegate);
  }

  @Override
  public abstract Nbt<NMS> clone();

  @SuppressWarnings("unchecked")
  protected NMS cloneNms() {
    return (NMS) delegate.rClone();
  }

  @Override
  public String getAsString() {
    return delegate.asString();
  }
}
