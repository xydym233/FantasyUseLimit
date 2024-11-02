package cn.xydym.fantasyuse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

//
// 更新情况记录:
// 将给予BUFF的部分改为异步任务 2024.6.3
//

public class AutoRebirth implements Listener {
    private final JavaPlugin plugin;

    public AutoRebirth(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.plugin != null && this.plugin.getConfig().getBoolean("AutoRebirthKG", true)) {
            Player player = event.getEntity();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline() && player.isDead()) {
                    player.spigot().respawn();
                    if (plugin.getConfig().getBoolean("AutoRebirthSM")) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
                        });
                    }
                }
            }, 3L);
        }
    }
}