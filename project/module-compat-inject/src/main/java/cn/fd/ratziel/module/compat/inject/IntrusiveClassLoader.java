package cn.fd.ratziel.module.compat.inject;

import taboolib.common.classloader.IsolatedClassLoader;

/**
 * IntrusiveClassLoader
 *
 * @author TheFloodDragon
 * @since 2024/3/23 22:10
 */
public final class IntrusiveClassLoader extends ClassLoader {

    private final String ACCESS_GROUP_NAME;
    private final String ACCESS_LIBRARIES_NAME;

    IntrusiveClassLoader(ClassLoader parent, String groupName) {
        super(parent);
        this.ACCESS_GROUP_NAME = groupName;
        this.ACCESS_LIBRARIES_NAME = groupName + ".libraries.";
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 依赖包重定向
            if (name.startsWith(ACCESS_LIBRARIES_NAME)) {
                return loadClass(name.substring(ACCESS_LIBRARIES_NAME.length()));
            }

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

    public static Class<?> loadClassOrNull(ClassLoader loader, String name) {
        try {
            return loader.loadClass(name);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

}
