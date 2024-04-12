package cn.fd.ratziel.core.function;

import java.util.function.Function;

/**
 * ClassProvider - 通过全限类定名提供加载后的类
 *
 * @author TheFloodDragon
 * @since 2024/4/13 上午12:07
 */
public class ClassProvider {

    private final Function<String, Class<?>> function;

    public ClassProvider(Function<String, Class<?>> function) {
        this.function = function;
    }

    public Class<?> provide(String name) {
        return function == null ? null : function.apply(name);
    }

}