package cn.fd.ratziel.compat.inject;

import taboolib.common.classloader.IsolatedClassLoader;

/**
 * IntrusiveClassLoader
 *
 * @author TheFloodDragon
 * @since 2024/3/23 22:10
 */
public class IntrusiveClassLoader extends ClassLoader {

    IntrusiveClassLoader(ClassLoader parent) {
        super(parent);
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
                if (name.startsWith("cn.fd.ratziel")) {
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
