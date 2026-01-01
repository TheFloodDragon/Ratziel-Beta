package cn.fd.ratziel.module.compat.inject;

import taboolib.common.LifeCycle;
import taboolib.common.PrimitiveSettings;
import taboolib.common.classloader.IsolatedClassLoader;
import taboolib.common.platform.Awake;
import taboolib.common.platform.function.IOKt;
import taboolib.library.reflex.UnsafeAccess;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

/**
 * IntrusiveCompat
 *
 * @author TheFloodDragon
 * @since 2024/3/23 22:10
 */
public final class IntrusiveCompat {

    public static ClassLoader PLUGIN_CLASSLOADER;

    public static ClassLoader SUPER_CLASSLOADER;

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
            try {
                Field field = PLUGIN_CLASSLOADER.getClass().getDeclaredField("libraryLoader");
                field.setAccessible(true);
                SUPER_CLASSLOADER = (ClassLoader) field.get(PLUGIN_CLASSLOADER);

                // 创建侵入性类加载器
                INTRUSIVE_CLASSLOADER = new IntrusiveClassLoader(SUPER_CLASSLOADER, PLUGIN_GROUP_NAME);

                // 注入类加载器
                field.set(PLUGIN_CLASSLOADER, INTRUSIVE_CLASSLOADER);
                // 检验是否注入成功
                if (field.get(PLUGIN_CLASSLOADER) != INTRUSIVE_CLASSLOADER) {
                    throw new RuntimeException("Failed to inject IntrusiveClassLoader!");
                }
            } catch (NoSuchFieldException ignored) {
                SUPER_CLASSLOADER = PLUGIN_CLASSLOADER.getParent();
                // 低版本可能没有 libraryLoader 字段，则注入PluginClassLoader的parent
                INTRUSIVE_CLASSLOADER = new IntrusiveClassLoader(SUPER_CLASSLOADER, PLUGIN_GROUP_NAME);

                // 获取 parent 字段的 setter
                MethodHandle setter = UnsafeAccess.INSTANCE.getLookup().findSetter(
                        ClassLoader.class, "parent", ClassLoader.class
                );

                // 注入类加载器
                setter.bindTo(PLUGIN_CLASSLOADER).invokeWithArguments(INTRUSIVE_CLASSLOADER);
                // 检验是否注入成功
                if (PLUGIN_CLASSLOADER.getParent() != INTRUSIVE_CLASSLOADER) {
                    throw new RuntimeException("Failed to inject IntrusiveClassLoader!");
                }
            }

            IOKt.debug(
                    "侵入性类加载器已注入:",
                    "    P | " + PLUGIN_CLASSLOADER,
                    "    S | " + SUPER_CLASSLOADER,
                    "    I | " + INTRUSIVE_CLASSLOADER
            );
        } catch (Throwable e) {
            throw new RuntimeException("Failed to inject IntrusiveClassLoader!", e);
        }
    }

}