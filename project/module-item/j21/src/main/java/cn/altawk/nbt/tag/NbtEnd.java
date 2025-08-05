package cn.altawk.nbt.tag;

import org.jetbrains.annotations.NotNull;

/**
 * NbtEnd
 *
 * @author TheFloodDragon
 * @since 2025/8/5 10:23
 */
public final class NbtEnd implements NbtTag {

    public static final NbtEnd INSTANCE = new NbtEnd();

    private NbtEnd() {
    }

    @Override
    public @NotNull NbtType getType() {
        return NbtType.END;
    }

    @Override
    public @NotNull Object getContent() {
        return INSTANCE;
    }

    @Override
    public @NotNull NbtTag clone() {
        return INSTANCE;
    }

}
