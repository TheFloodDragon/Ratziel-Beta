package cn.fd.ratziel.core.env;

import taboolib.common.Inject;
import taboolib.common.env.RuntimeEnv;
import taboolib.common.platform.Awake;

import java.util.Collections;

import static taboolib.common.PrimitiveSettings.TABOOLIB_VERSION;

/**
 * KetherEnv
 *
 * @author TheFloodDragon
 * @since 2024/1/27 9:58
 */
public class KetherEnv {

    public static String RUNTIME_DEPENDENCY = "!io.github.altawk.asl:script-kether:" + TABOOLIB_VERSION;

    @Awake
    public static void init() throws Throwable {
        RuntimeEnv.ENV.loadDependency(RUNTIME_DEPENDENCY, false, Collections.emptyList());
    }

}
