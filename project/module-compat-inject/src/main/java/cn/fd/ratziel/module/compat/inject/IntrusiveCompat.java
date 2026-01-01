package cn.fd.ratziel.module.compat.inject;

import taboolib.common.LifeCycle;
import taboolib.common.PrimitiveSettings;
import taboolib.common.classloader.IsolatedClassLoader;
import taboolib.common.platform.Awake;
import taboolib.common.platform.function.IOKt;

import java.lang.reflect.Field;

/**
 * IntrusiveCompat
 *
 * @author TheFloodDragon
 * @since 2024/3/23 22:10
 */
public final class IntrusiveCompat {

    public static ClassLoader PLUGIN_CLASSLOADER;

    public static ClassLoader LIBRARY_CLASSLOADER;

    public static ClassLoader INTRUSIVE_CLASSLOADER;

    public static String PLUGIN_GROUP_NAME = "cn.fd.ratziel";

    @Awake(LifeCycle.CONST)
    private static void inject() {
        // 非隔离模式不需要此
        if (!PrimitiveSettings.IS_ISOLATED_MODE) return;
        try {
            // 插件类加载器
            PLUGIN_CLASSLOADER = IsolatedClassLoader.INSTANCE.getParent();

            // 获取插件类加载器的 libraryLoader
            Field field = PLUGIN_CLASSLOADER.getClass().getDeclaredField("libraryLoader");
            field.setAccessible(true);
            LIBRARY_CLASSLOADER = (ClassLoader) field.get(PLUGIN_CLASSLOADER);

            // 创建侵入性类加载器
            INTRUSIVE_CLASSLOADER = new IntrusiveClassLoader(LIBRARY_CLASSLOADER, PLUGIN_GROUP_NAME);

            // 注入类加载器
            field.set(PLUGIN_CLASSLOADER, INTRUSIVE_CLASSLOADER);

            IOKt.debug(
                    "侵入性类加载器已注入:",
                    "    P | " + PLUGIN_CLASSLOADER,
                    "    L | " + LIBRARY_CLASSLOADER,
                    "    I | " + field.get(PLUGIN_CLASSLOADER)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject IntrusiveClassLoader!", e);
        }
    }

}