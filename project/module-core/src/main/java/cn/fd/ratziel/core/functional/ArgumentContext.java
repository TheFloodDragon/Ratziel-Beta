package cn.fd.ratziel.core.functional;

import cn.fd.ratziel.core.exception.ArgumentNotFoundException;
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
    <T> @NotNull T pop(@NotNull Class<T> type) throws ArgumentNotFoundException;

    /**
     * 弹出指定类型的参数
     * 若无法找到, 则返回默认值
     */
    <T> @NotNull T popOr(@NotNull Class<T> type, @NotNull Supplier<T> def);

    /**
     * 弹出指定类型的参数
     * 若无法找到, 则返回空
     */
    <T> @Nullable T popOrNull(@NotNull Class<T> type);

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
