package cn.fd.ratziel.compat.inject;

import sun.misc.Unsafe;
import taboolib.common.PrimitiveLoader;
import taboolib.common.classloader.IsolatedClassLoader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * IntrusiveCompat
 *
 * @author TheFloodDragon
 * @since 2024/3/23 22:10
 */
public class IntrusiveCompat {

    IntrusiveCompat() {
    }

    public static final ClassLoader PLUGIN_CLASS_LOADER = IsolatedClassLoader.INSTANCE.getParent();

    public static final ClassLoader GLOBAL_CLASS_LOADER = PLUGIN_CLASS_LOADER.getParent();

    private static final MethodHandle SETTER;

    static {
        MethodHandles.Lookup lookup;
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            unsafe.ensureClassInitialized(MethodHandles.Lookup.class);
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            Object lookupBase = unsafe.staticFieldBase(lookupField);
            Long lookupOffset = unsafe.staticFieldOffset(lookupField);
            lookup = (MethodHandles.Lookup) unsafe.getObject(lookupBase, lookupOffset);
        } catch (Throwable ex) {
            throw new IllegalStateException("Unsafe not found");
        }
        try {
            SETTER = lookup.findSetter(ClassLoader.class, "parent", ClassLoader.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void inject() throws Throwable {
        System.out.println(PLUGIN_CLASS_LOADER.getParent());
        SETTER.bindTo(PLUGIN_CLASS_LOADER).invokeWithArguments(new IntrusiveClassLoader(GLOBAL_CLASS_LOADER));
        System.out.println(PLUGIN_CLASS_LOADER.getParent());
        // Test
        System.out.println(PrimitiveLoader.TABOOLIB_GROUP);
    }

}