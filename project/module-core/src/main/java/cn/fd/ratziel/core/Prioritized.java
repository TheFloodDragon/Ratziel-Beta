package cn.fd.ratziel.core;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Prioritized - 优先级
 *
 * @author TheFloodDragon
 * @since 2024/4/14 12:18
 */
public class Prioritized<T> {

    public Prioritized(@NotNull T value) {
        this(DEFAULT_PRIORITY, value);
    }

    public Prioritized(@NotNull Byte priority, @NotNull T value) {
        this.priority = priority;
        this.value = value;
    }

    /**
     * 默认优先级
     */
    public static final Byte DEFAULT_PRIORITY = 0;

    @NotNull
    private Byte priority;
    @NotNull
    private final T value;

    /**
     * 获取优先级
     */
    public @NotNull Byte getPriority() {
        return priority;
    }

    /**
     * 设置优先级
     */
    public void setPriority(@NotNull Byte priority) {
        this.priority = priority;
    }

    /**
     * 获取值
     */
    public @NotNull T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Prioritized)
            return Objects.equals(((Prioritized<?>) obj).priority, this.priority) && Objects.equals(((Prioritized<?>) obj).value, this.value);
        else return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "(" + priority + ", " + value + ")";
    }

}