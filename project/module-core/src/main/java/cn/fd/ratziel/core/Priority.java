package cn.fd.ratziel.core;

import java.util.Objects;

/**
 * Priority - 优先级
 *
 * @author TheFloodDragon
 * @since 2024/4/14 12:18
 */
public class Priority<T> {

    public Priority(Byte priority, T value) {
        this.priority = priority;
        this.value = value;
    }

    private Byte priority;
    private final T value;

    /**
     * 获取优先级
     */
    public Byte getPriority() {
        return priority;
    }

    /**
     * 设置优先级
     */
    public Byte setPriority(Byte priority) {
        return this.priority = priority;
    }

    /**
     * 获取值
     */
    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Priority)
            return Objects.equals(((Priority<?>) obj).priority, this.priority) && Objects.equals(((Priority<?>) obj).value, this.value);
        else return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "(" + priority + ", " + value.toString() + ")";
    }

}
