package cn.xydym.fantasyuse;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.WeakHashMap;
import java.util.Map;

public class AntiSpamListener implements Listener {
    private final JavaPlugin plugin;
    private final Map<Player, Long> lastClickTimes = new WeakHashMap<>();
    private final Map<Player, Long> lastShootTimes = new WeakHashMap<>();

    public AntiSpamListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private long getClickInterval() {
        return plugin.getConfig().getLong("clickInterval", 90L);
    }

    private long getShootInterval() {
        return plugin.getConfig().getLong("shootInterval", 100L);
    }

    private boolean canPerformAction(Player player, Map<Player, Long> lastActionTimes, long currentTime, long interval) {
        long lastActionTime = lastActionTimes.getOrDefault(player, 0L);
        long timeDifference = currentTime - lastActionTime;

        if (timeDifference < interval) {
            return false;
        } else {
            lastActionTimes.put(player, currentTime);
            return true;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        long currentTime = System.currentTimeMillis();

        // 仅检查右键点击
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (!canPerformAction(player, lastClickTimes, currentTime, getClickInterval())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            long currentTime = System.currentTimeMillis();

            if (!canPerformAction(player, lastShootTimes, currentTime, getShootInterval())) {
                event.setCancelled(true);
            }
        }
    }
}