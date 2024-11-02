package cn.xydym.fantasyuse;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ChunkEntityLimiterListener {

    private final List<String> excludedWorlds;
    private final int chunkEntityLimit;
    private final JavaPlugin plugin;

    public ChunkEntityLimiterListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.excludedWorlds = plugin.getConfig().getStringList("not_world");
        this.chunkEntityLimit = plugin.getConfig().getInt("ChunkEntityLimiter");
        startEntityCheckTask();
    }

    private void startEntityCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : plugin.getServer().getWorlds()) {
                    if (excludedWorlds.contains(world.getName())) {
                        continue;
                    }
                    for (Chunk chunk : world.getLoadedChunks()) {
                        checkAndClearEntities(chunk);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 600L); // 增加定时任务的间隔时间
    }

    private void checkAndClearEntities(Chunk chunk) {
        Entity[] entities = chunk.getEntities();
        int entityCount = entities.length;

        if (entityCount > chunkEntityLimit) {
            for (Entity entity : entities) {
                if (entity instanceof Player) {
                    continue; // 跳过玩家实体
                }
                entity.remove();
            }
        }
    }
}