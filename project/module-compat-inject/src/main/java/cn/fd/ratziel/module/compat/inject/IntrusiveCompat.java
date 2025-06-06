package cn.fd.ratziel.module.compat.inject;

import taboolib.common.LifeCycle;
import taboolib.common.PrimitiveSettings;
import taboolib.common.classloader.IsolatedClassLoader;
import taboolib.common.platform.Awake;
import taboolib.common.platform.function.IOKt;
import taboolib.library.reflex.UnsafeAccess;

import java.lang.invoke.MethodHandle;

/**
 * IntrusiveCompat
 *
 * @author TheFloodDragon
 * @since 2024/3/23 22:10
 */
public final class IntrusiveCompat {

    public static ClassLoader PLUGIN_CLASS_LOADER;

    public static ClassLoader GLOBAL_CLASS_LOADER;

    public static ClassLoader INTRUSIVE_CLASSLOADER;

    public static String PLUGIN_GROUP_NAME = "cn.fd.ratziel";

    private static final MethodHandle SETTER;

    static {
        try {
            SETTER = UnsafeAccess.INSTANCE.getLookup().findSetter(
                    ClassLoader.class, "parent", ClassLoader.class
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Awake(LifeCycle.CONST)
    private static void inject() throws Throwable {
        // 类加载器
        PLUGIN_CLASS_LOADER = IsolatedClassLoader.INSTANCE.getParent();
        GLOBAL_CLASS_LOADER = PLUGIN_CLASS_LOADER.getParent();
        INTRUSIVE_CLASSLOADER = new IntrusiveClassLoader(GLOBAL_CLASS_LOADER, PLUGIN_GROUP_NAME);
        // 隔离模式下注入类加载器
        if (PrimitiveSettings.IS_ISOLATED_MODE) {
            SETTER.bindTo(PLUGIN_CLASS_LOADER).invokeWithArguments(INTRUSIVE_CLASSLOADER);
            IOKt.debug(
                    "侵入性类加载器已注入:",
                    "    P | " + PLUGIN_CLASS_LOADER,
                    "    G | " + GLOBAL_CLASS_LOADER,
                    "    I | " + PLUGIN_CLASS_LOADER.getParent()
            );
        }
    }

}