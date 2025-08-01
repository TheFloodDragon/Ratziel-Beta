package cn.fd.ratziel.core.functional;

import org.jetbrains.annotations.NotNull;

/**
 * ArgumentContextProvider
 *
 * @author TheFloodDragon
 * @since 2025/8/1 23:00
 */
public interface ArgumentContextProvider {

    /**
     * Creates a new ArgumentContext.
     */
    @NotNull ArgumentContext newContext();

}
