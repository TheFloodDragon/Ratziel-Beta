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
                return loadClass(name.substring(ACCESS_LIBRARIES_NAME.length()), resolve);
            }

            // 优先父级加载
            ClassLoader parent = getParent();
            if (parent != null) {
                try {
                    return parent.loadClass(name);
                } catch (ClassNotFoundException ignored) {
                }
            }

            // 隔离类加载器加载 (不检查其父级)
            // 同时检查可访问性
            if (name.startsWith(ACCESS_GROUP_NAME)) try {
                return IsolatedClassLoader.INSTANCE.loadClass(name, resolve, false);
            } catch (ClassNotFoundException ignored) {
            }

            // 找不到类抛出异常
            throw new ClassNotFoundException(name);
        }
    }

}
