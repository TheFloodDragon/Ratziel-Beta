package cn.fd.ratziel.script;

import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ReleasableScriptLanguage
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:41
 */
public abstract class ReleasableScriptLanguage implements ScriptLanguage {

    public interface ScriptEnvReleaser {

        void release(@NotNull ScriptEnvironment env);

    }

    public ReleasableScriptLanguage(@NotNull String... names) {
        this.NAMES = names;
        this.releasers = new CopyOnWriteArrayList<>();
    }

    @NotNull
    private final String[] NAMES;

    @NotNull
    protected List<ScriptEnvReleaser> releasers;

    abstract Object eval(@NotNull ScriptStorage script, @NotNull ScriptEnvironment env) throws ScriptException;

    @Override
    public Object evaluate(@NotNull ScriptStorage script, @NotNull ScriptEnvironment environment) throws ScriptException {
        // 释放环境中的参数变量到脚本变量中
        for (ScriptEnvReleaser releaser : releasers) {
            releaser.release(environment);
        }
        // 评估脚本
        return eval(script, environment);
    }

    public void addReleaser(ScriptEnvReleaser releaser) {
        releasers.add(releaser);
    }

    public void removeReleaser(ScriptEnvReleaser releaser) {
        releasers.remove(releaser);
    }

    public @NotNull List<ScriptEnvReleaser> getReleasers() {
        return releasers;
    }

    @Override
    public @NotNull String[] getNames() {
        return NAMES;
    }

}