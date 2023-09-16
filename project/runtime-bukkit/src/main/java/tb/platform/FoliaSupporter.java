package tb.platform;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

/**
 * FoliaSupport
 *
 * @author TheFloodDragon
 * @since 2023/9/16 12:11
 */
@SuppressWarnings("JavaReflectionMemberAccess")
public final class FoliaSupporter {

    private FoliaSupporter() {}

    private static final boolean EXISTS =
            isClassExists("io.papermc.paper.threadedregions.RegionizedServer") ||
                    isClassExists("io.papermc.paper.threadedregions.RegionizedServerInitEvent");

    /**
     * Folia的异步调度器
     */
    private static AsyncScheduler asyncScheduler;

    static {
        try {
            Class<?> classBukkit = Class.forName("org.bukkit.Bukkit");
            asyncScheduler = (AsyncScheduler) classBukkit.getDeclaredMethod("getAsyncScheduler").invoke(null);
        } catch (Exception ignored) {
        }
    }

    /**
     * 执行异步任务
     */
    public static CompletableFuture<Void> runAsync(Plugin plugin, Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        asyncScheduler.runNow(plugin, task -> {
            runnable.run();
            future.complete(null);
        });

        return future;
    }


    /**
     * 核心是否为 Folia
     */
    public static boolean isFolia() {
        return EXISTS;
    }

    /**
     * 查找类并返回是否存在
     */
    private static boolean isClassExists(final String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


}
