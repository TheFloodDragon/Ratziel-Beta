package cn.fd.ratziel.compat.inject;

import cn.fd.ratziel.core.util.ClassProvider;
import taboolib.common.classloader.IsolatedClassLoader;

import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * IntrusiveClassLoader
 *
 * @author TheFloodDragon
 * @since 2024/3/23 22:10
 */
public final class IntrusiveClassLoader extends ClassLoader {

    private final String ACCESS_GROUP_NAME;

    IntrusiveClassLoader(ClassLoader parent, String groupName) {
        super(parent);
        this.ACCESS_GROUP_NAME = groupName;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 优先父级加载
            Class<?> find = loadClassOrNull(getParent(), name);
            // 隔离类加载器加载 (不检查其父级)
            if (find == null) try {
                // 检查可访问性
                if (name.startsWith(ACCESS_GROUP_NAME)) {
                    find = IsolatedClassLoader.INSTANCE.loadClass(name, resolve, false);
                }
            } catch (ClassNotFoundException ignored) {
            }
            // 检查结果
            if (find == null) throw new ClassNotFoundException();
            // 返回值
            return find;
        }
    }

    public static Class<?> loadClassOrNull(ClassLoader loader, String name) {
        try {
            return loader.loadClass(name);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

}
