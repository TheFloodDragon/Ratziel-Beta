package cn.fd.ratziel.compat.test;

import taboolib.common.platform.Ghost;

/**
 * Test01
 *
 * @author TheFloodDragon
 * @since 2024/2/15 17:11
 */
// With @Ghost
@Ghost
public class Test01 {

    static {
        // Test Static Coding Block
        System.out.println("[Test01] 静态代码块执行");
    }

}
