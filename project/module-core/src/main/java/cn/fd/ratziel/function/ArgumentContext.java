package cn.fd.ratziel.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ArgumentContext - 参数上下文
 * 本质为一个参数容器
 *
 * @author TheFloodDragon
 * @since 2024/7/16 11:56
 */
@SuppressWarnings("NullableProblems")
public interface ArgumentContext {

    /**
     * 弹出第一个指定类型的参数
     *
     * @throws NullPointerException 当无法找到指定类型的参数时抛出
     */
    default <@NotNull T> @NotNull T pop(@NotNull Class<T> type) throws NullPointerException {
        T result = popOrNull(type);
        if (result == null) throw new NullPointerException("Cannot find argument: " + type.getName() + " !");
        return result;
    }

    /**
     * 弹出第一个指定类型的参数
     * 若无法找到, 则返回空
     */
    <@NotNull T> @Nullable T popOrNull(@NotNull Class<T> type);

    /**
     * 弹出第一个指定类型的参数
     * 若无法找到, 则返回默认值
     */
    default <@NotNull T> @NotNull T popOr(@NotNull Class<T> type, @NotNull T def) {
        T result = popOrNull(type);
        if (result == null) return def;
        return result;
    }

    /**
     * 弹出所有指定类型的参数
     */
    <@NotNull T> @NotNull Iterable<? extends T> popAll(@NotNull Class<T> type);

    /**
     * 添加一个参数元素
     */
    boolean add(Object element);

    /**
     * 删除一个参数元素
     */
    boolean remove(Object element);

    /**
     * 获取所有参数
     */
    @NotNull
    Iterable<Object> args();

}
