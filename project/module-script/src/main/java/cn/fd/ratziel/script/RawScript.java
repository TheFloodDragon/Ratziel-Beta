package cn.fd.ratziel.script;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.CompiledScript;

/**
 * RawScript
 *
 * @author TheFloodDragon
 * @since 2024/6/30 12:02
 */
public class RawScript implements ScriptStorage {

    public RawScript(@NotNull String script) {
        this.content = script;
    }

    @NotNull
    private final String content;
    @Nullable
    private CompiledScript compiledScript = null;

    @Override
    public @NotNull String getContent() {
        return content;
    }

    @Override
    public @Nullable CompiledScript getCompiled() {
        return compiledScript;
    }

    @Override
    public void setCompiled(@NotNull CompiledScript compiled) {
        this.compiledScript = compiled;
    }

}
