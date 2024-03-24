package cn.fd.ratziel.compat.inject;

import taboolib.common.PrimitiveSettings;
import taboolib.common.classloader.IsolatedClassLoader;
import taboolib.library.reflex.UnsafeAccess;

import java.lang.invoke.MethodHandle;

/**
 * IntrusiveCompat
 *
 * @author TheFloodDragon
 * @since 2024/3/23 22:10
 */
public class IntrusiveCompat {

    private IntrusiveCompat() {
    }

    public static ClassLoader PLUGIN_CLASS_LOADER = IsolatedClassLoader.INSTANCE.getParent();

    public static ClassLoader GLOBAL_CLASS_LOADER = PLUGIN_CLASS_LOADER.getParent();

    private static final MethodHandle SETTER;

    static {
        try {
            SETTER = UnsafeAccess.INSTANCE.getLookup().findSetter(ClassLoader.class, "parent", ClassLoader.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void inject() throws Throwable {
        // TODO AWA
        if (PrimitiveSettings.IS_ISOLATED_MODE || PrimitiveSettings.IS_DEBUG_MODE) SETTER.bindTo(PLUGIN_CLASS_LOADER).invokeWithArguments(new IntrusiveClassLoader(GLOBAL_CLASS_LOADER));
    }

}