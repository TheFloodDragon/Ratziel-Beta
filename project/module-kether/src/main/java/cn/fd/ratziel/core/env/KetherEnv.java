package cn.fd.ratziel.core.env;

import taboolib.common.PrimitiveSettings;
import taboolib.common.classloader.IsolatedClassLoader;
import taboolib.common.platform.Awake;
import taboolib.library.reflex.ClassMethod;
import taboolib.library.reflex.ReflexClass;

import static taboolib.common.PrimitiveSettings.IS_ISOLATED_MODE;
import static taboolib.common.PrimitiveSettings.TABOOLIB_VERSION;

/**
 * KetherEnv
 *
 * @author TheFloodDragon
 * @since 2024/1/27 9:58
 */
public class KetherEnv {

    public static String[] RUNTIME_DEPENDENCY = {
            "!io.github.altawk.asl".substring(1), "script-kether", TABOOLIB_VERSION
    };

    @Awake
    public static void init() throws ClassNotFoundException {
        // Because class PrimitiveLoader is isolated, so we need to use reflex
        Class<?> plc = Class.forName("taboolib.common.PrimitiveLoader", true, IsolatedClassLoader.INSTANCE);
        ReflexClass rc = ReflexClass.Companion.of(plc, true);
        // Use Default Rule
        String[][] rule = (String[][]) rc.getMethodByType("defaultRelocateRule", true, true).invokeStatic();
        // Get Method
        // public static boolean load(String repo, String group, String name, String version, boolean isIsolated, boolean isExternal, String[][] relocate)
        ClassMethod method = rc.getMethodByType("load", true, true, String.class, String.class, String.class, String.class, Boolean.class, boolean.class, String[][].class);
        // Load as Taboolib Module
        method.invokeStatic(PrimitiveSettings.REPO_CENTRAL, RUNTIME_DEPENDENCY[0], RUNTIME_DEPENDENCY[1], RUNTIME_DEPENDENCY[2], IS_ISOLATED_MODE, false, rule);
    }

}