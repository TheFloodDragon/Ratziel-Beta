package cn.fd.ratziel.script.impl;

import cn.fd.ratziel.script.api.ScriptContent;
import cn.fd.ratziel.script.api.ScriptExecutor;
import org.jetbrains.annotations.NotNull;

/**
 * SimpleScriptContent
 *
 * @author TheFloodDragon
 * @since 2024/10/4 20:11
 */
public class SimpleScriptContent implements ScriptContent {

    public SimpleScriptContent(@NotNull String content, @NotNull ScriptExecutor executor) {
        this.content = content;
        this.executor = executor;
    }

    private final String content;
    private final ScriptExecutor executor;

    @Override
    public @NotNull ScriptExecutor getExecutor() {
        return executor;
    }

    @Override
    public @NotNull String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "ScriptContent[content=" + content + ", executor=" + executor + "]";
    }

    @Override
    public int hashCode() {
        return content.hashCode() + 31 * executor.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else if (o instanceof ScriptContent) {
            return getContent().equals(((ScriptContent) o).getContent())
                    && getExecutor().equals(((ScriptContent) o).getExecutor());
        }
        return false;
    }

}
