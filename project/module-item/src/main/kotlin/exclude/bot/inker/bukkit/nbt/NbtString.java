package exclude.bot.inker.bukkit.nbt;

import exclude.bot.inker.bukkit.nbt.api.NbtStringLike;
import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.ref.RefNbtTagString;

public final class NbtString extends Nbt<RefNbtTagString> implements NbtStringLike {
  private static final boolean OF_SUPPORTED = CbVersion.v1_15_R1.isSupport();
  private static final NbtString EMPTY = new NbtString(OF_SUPPORTED
      ? RefNbtTagString.of("")
      : new RefNbtTagString(""));

  NbtString(RefNbtTagString delegate) {
    super(delegate);
  }

  public static NbtString valueOf(String value) {
    return value.isEmpty()
        ? EMPTY
        : new NbtString(OF_SUPPORTED
        ? RefNbtTagString.of(value)
        : new RefNbtTagString(value));
  }

  static NbtString fromNmsImpl(RefNbtTagString delegate) {
    return delegate.asString().isEmpty() ? EMPTY : new NbtString(delegate);
  }

  public String getAsString() {
    return delegate.asString();
  }

  @Override
  public NbtString clone() {
    return this;
  }
}
