package cn.xydym.fantasyuse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.FishHook;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityProjectileListener implements Listener {
    private final JavaPlugin plugin;
    private final Map<Entity, Integer> entityTimers = new ConcurrentHashMap<>();
    private final List<String> unknownEntities;

    public EntityProjectileListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.unknownEntities = plugin.getConfig().getStringList("unknown-entities");
        startEntityCheckTask();
    }

    private void startEntityCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().getWorlds().forEach(world ->
                        world.getEntities().stream()
                                .filter(entity -> !(entity instanceof Player))
                                .forEach(entity -> {
                                    if (isUnknownEntity(entity) || isProjectile(entity)) {
                                        entityTimers.putIfAbsent(entity, 3);
                                    }
                                })
                );

                entityTimers.entrySet().removeIf(entry -> {
                    int newTime = entry.getValue() - 1;
                    if (newTime <= 0 && (isUnknownEntity(entry.getKey()) || isProjectile(entry.getKey()))) {
                        entry.getKey().remove();
                        return true;
                    } else if (newTime <= 0) {
                        return true;
                    } else {
                        entityTimers.put(entry.getKey(), newTime);
                        return false;
                    }
                });
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private boolean isUnknownEntity(Entity entity) {
        return unknownEntities.contains(entity.getType().name());
    }

    private boolean isProjectile(Entity entity) {
        return entity instanceof Projectile && !(entity instanceof FishHook);
    }
}