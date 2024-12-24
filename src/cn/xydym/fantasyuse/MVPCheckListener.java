package cn.xydym.fantasyuse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class MVPCheckListener implements Listener {

    private Set<Player> ignoredPlayers = new HashSet<>();
    private Queue<Player> playerQueue = new LinkedList<>();
    private JavaPlugin plugin;

    public MVPCheckListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startMVPCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    checkAndQueuePlayer(player);
                }

                // 处理队列中的玩家
                if (!playerQueue.isEmpty()) {
                    Player player = playerQueue.poll();
                    broadcastPlayerTitle(player);
                }

                // 移除已经不在线的玩家在忽略的列表的数据
                ignoredPlayers.removeIf(player -> !player.isOnline());
            }
        }.runTaskTimer(plugin, 0, 100); // 每5秒执行一次
    }

    private void checkAndQueuePlayer(Player player) {
        // 忽略OP玩家
        if (player.isOp()) {
            return;
        }

        if (player.hasPermission("mvps") && !ignoredPlayers.contains(player)) {
            playerQueue.add(player);
            ignoredPlayers.add(player);
        } else if (!player.hasPermission("mvps") && ignoredPlayers.contains(player)) {
            ignoredPlayers.remove(player);
        }
    }

    private void broadcastPlayerTitle(Player player) {
        // 向所有玩家发送标题提示
        String title = "§d§l§n" + player.getName() + " §b§l§n已上线";
        String subtitle = "§e§l§n至臻§6§l§n郡王 §6§l" + player.getName() + " §e§l§n专属登陆特效"; // 可以设置副标题，也可以留空

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendTitle(title, subtitle, 100, 1000, 100); // fadeIn: 50 ticks, stay: 200 ticks, fadeOut: 50 ticks
        }
    }
}
