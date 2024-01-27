package cn.fd.ratziel.core.env;

import taboolib.common.PrimitiveLoader;
import taboolib.common.PrimitiveSettings;
import taboolib.common.platform.Awake;

import static taboolib.common.PrimitiveLoader.defaultRelocateRule;
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
            "io.github.altawk.asl", "script-kether", TABOOLIB_VERSION
    };

    @Awake
    public static void init() throws Throwable {
        // Use Default Rule
        String[][] rule = defaultRelocateRule();
        // Load as Taboolib Module
        PrimitiveLoader.load(PrimitiveSettings.REPO_CENTRAL, RUNTIME_DEPENDENCY[0], RUNTIME_DEPENDENCY[1], RUNTIME_DEPENDENCY[2], IS_ISOLATED_MODE, false, rule);
    }

}