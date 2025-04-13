package cn.fd.ratziel.module.compat.inject;

import org.jetbrains.annotations.Nullable;
import taboolib.common.classloader.IsolatedClassLoader;

import java.net.URL;

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
            // 同时检查可访问性
            if (find == null && name.startsWith(ACCESS_GROUP_NAME)) try {
                find = IsolatedClassLoader.INSTANCE.loadClass(name, resolve, false);
            } catch (ClassNotFoundException ignored) {
            }
            // 检查结果
            if (find == null) throw new ClassNotFoundException(name);
            // 返回值
            return find;
        }
    }

    @Override
    public @Nullable URL findResource(String name) {
        return IsolatedClassLoader.INSTANCE.getResource(name);
    }

    public static Class<?> loadClassOrNull(ClassLoader loader, String name) {
        try {
            return loader.loadClass(name);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

}
