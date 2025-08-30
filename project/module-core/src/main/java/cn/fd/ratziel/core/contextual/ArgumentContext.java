package cn.fd.ratziel.core.contextual;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * ArgumentContext - 参数上下文
 * 本质为一个参数容器
 *
 * @author TheFloodDragon
 * @since 2024/7/16 11:56
 */
public interface ArgumentContext {

    /**
     * 弹出指定类型的参数
     *
     * @throws ArgumentNotFoundException 当无法找到指定类型的参数时抛出
     */
    <T> @NotNull T pop(@NotNull Class<@NotNull T> type) throws ArgumentNotFoundException;

    /**
     * 弹出指定类型的参数
     * 若无法找到, 则返回空
     */
    <T> @Nullable T popOrNull(@NotNull Class<@NotNull T> type);

    /**
     * 弹出指定类型的参数
     * 若无法找到, 则返回默认值
     */
    default <T> @NotNull T popOr(@NotNull Class<T> type, @NotNull Supplier<@NotNull T> def) {
        final T obj = this.popOrNull(type);
        return obj == null ? def.get() : obj;
    }

    /**
     * 弹出指定类型的参数
     * 若无法找到, 则添加默认值
     */
    default <T> @NotNull T popOrPut(@NotNull Class<T> type, @NotNull Supplier<@NotNull T> def) {
        T obj = this.popOrNull(type);
        if (obj == null) {
            obj = def.get();
            this.put(obj);
        }
        return obj;
    }

    /**
     * 添加一个参数
     */
    void put(@NotNull Object element);

    /**
     * 添加多个参数
     */
    default void putAll(@NotNull Iterable<@NotNull Object> elements) {
        for (Object element : elements) put(element);
    }

    /**
     * 删除一个参数
     */
    void remove(@NotNull Class<?> type);

    /**
     * 获取所有参数
     */
    @NotNull
    Iterable<@NotNull Object> args();

}
