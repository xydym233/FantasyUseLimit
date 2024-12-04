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
                updateEntityTimers();
                removeExpiredEntities();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateEntityTimers() {
        plugin.getServer().getWorlds().forEach(world ->
                world.getEntities().stream()
                        .filter(entity -> !(entity instanceof Player))
                        .filter(this::isTargetEntity)
                        .forEach(entity -> entityTimers.putIfAbsent(entity, 3))
        );
    }

    private void removeExpiredEntities() {
        entityTimers.entrySet().removeIf(entry -> {
            int newTime = entry.getValue() - 1;
            Entity entity = entry.getKey();
            if (newTime <= 0 && isTargetEntity(entity)) {
                entity.remove();
                return true;
            } else if (newTime <= 0) {
                return true;
            } else {
                entityTimers.put(entity, newTime);
                return false;
            }
        });
    }

    private boolean isTargetEntity(Entity entity) {
        return isUnknownEntity(entity) || isProjectile(entity);
    }

    private boolean isUnknownEntity(Entity entity) {
        return unknownEntities.contains(entity.getType().name());
    }

    private boolean isProjectile(Entity entity) {
        return entity instanceof Projectile && !(entity instanceof FishHook);
    }
}
