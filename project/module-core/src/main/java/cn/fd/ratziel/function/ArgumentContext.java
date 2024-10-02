package cn.fd.ratziel.function;

import cn.fd.ratziel.function.exception.ArgumentNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ArgumentContext - 参数上下文
 * 本质为一个参数容器
 *
 * @author TheFloodDragon
 * @since 2024/7/16 11:56
 */
public interface ArgumentContext {

    /**
     * 获取指定类型的参数
     *
     * @throws ArgumentNotFoundException 当无法找到指定类型的参数时抛出
     */
    <T> @NotNull T get(@NotNull Class<T> type) throws ArgumentNotFoundException;

    /**
     * 获取指定类型的参数
     * 若无法找到, 则返回默认值
     */
    <T> @NotNull T getOr(@NotNull Class<T> type, @NotNull T def);

    /**
     * 获取指定类型的参数
     * 若无法找到, 则返回空
     */
    <T> @Nullable T getOrNull(@NotNull Class<T> type);

    /**
     * 添加一个参数
     */
    void put(@NotNull Object element);

    /**
     * 删除一个参数
     */
    void remove(@NotNull Object element);

    /**
     * 获取所有参数
     */
    @NotNull
    Iterable<Object> args();

}
