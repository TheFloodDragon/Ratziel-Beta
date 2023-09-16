package tb.platform;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;

/**
 * FoliaSupport
 *
 * @author TheFloodDragon
 * @since 2023/9/16 12:11
 */
public final class FoliaSupporter {

    private FoliaSupporter() {}

    private static final boolean EXISTS =
            isClassExists("io.papermc.paper.threadedregions.RegionizedServer") ||
                    isClassExists("io.papermc.paper.threadedregions.RegionizedServerInitEvent");

//    private static AsyncScheduler asyncScheduler;
//
//    static {
//        try {
//            Class<?> classBukkit = Class.forName("org.bukkit.Bukkit");
//            asyncScheduler = (AsyncScheduler) classBukkit.getDeclaredMethod("getAsyncScheduler").invoke(null);
//        } catch (Exception ignored) {}
//    }
//
//    public static CompletableFuture<Void> runAsync(Plugin plugin, Runnable runnable) {
//        CompletableFuture<Void> future = new CompletableFuture<>();
//
//        asyncScheduler.runNow(plugin, task -> {
//            runnable.run();
//            future.complete(null);
//        });
//
//        return future;
//    }


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
