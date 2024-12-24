package cn.xydym.fantasyuse;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class UnluckyPlayerListener implements Listener {

    private final JavaPlugin plugin;
    private Economy economy;
    private UUID lastUnluckyPlayerId = null;
    private int consecutiveCount = 0;

    public UnluckyPlayerListener(JavaPlugin plugin) {
        this.plugin = plugin;
        setupEconomy();
        startUnluckyPlayerTask();
    }

    private void setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    private void startUnluckyPlayerTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                if (players.size() <= 3) {
                    return; // 如果在线玩家数小于等于3，则不触发这个玩法
                }

                Random random = new Random();
                Player unluckyPlayer = players.get(random.nextInt(players.size()));
                UUID unluckyPlayerId = unluckyPlayer.getUniqueId();

                // 检查是否是同一个玩家连续成为倒霉蛋
                if (unluckyPlayerId.equals(lastUnluckyPlayerId)) {
                    consecutiveCount++;
                } else {
                    consecutiveCount = 1;
                }

                // 重置连续次数如果超过5
                if (consecutiveCount > 5) {
                    consecutiveCount = 1;
                }

                // 更新上一次成为倒霉蛋的玩家
                lastUnluckyPlayerId = unluckyPlayerId;

                // 创建霉运石物品
                ItemStack unluckyStone = new ItemStack(Material.DIAMOND, consecutiveCount); // 根据连续次数决定霉运石的数量
                ItemMeta meta = unluckyStone.getItemMeta();
                meta.setDisplayName("§2霉运石");

                // 设置多行 lore
                List<String> lore = new ArrayList<>();
                lore.add("§7一个吸收了霉运凝结的石头");
                lore.add("§a获得方法:§c自行探索");
                meta.setLore(lore);

                unluckyStone.setItemMeta(meta);

                // 给予倒霉蛋物品
                unluckyPlayer.getInventory().addItem(unluckyStone);
                String unluckyMessage = "§b>§6>§c> §d虽然不会获得钱，但你获得了一块霉运石！";
                if (consecutiveCount > 1) {
                    unluckyMessage += "\n §a你因连续" + consecutiveCount + "次成为倒霉蛋 §d(霉运石翻了 " + consecutiveCount + " 倍)";
                }
                unluckyPlayer.sendMessage(unluckyMessage);
                unluckyPlayer.sendMessage("§b>§6>§c> §7你的霉运凝结成了一块石头！或许可以干点什么？");

                // 获取最小和最大金币数
                double minMoney = plugin.getConfig().getDouble("min_money_per_player", 10);
                double maxMoney = plugin.getConfig().getDouble("max_money_per_player", 100);

                for (Player player : players) {
                    if (player.equals(unluckyPlayer)) {
                        continue; // 跳过倒霉蛋
                    } else {
                        // 随机生成一个在 minMoney 和 maxMoney 之间的金额并取整
                        int money = (int) Math.round(minMoney + (maxMoney - minMoney) * random.nextDouble()) * consecutiveCount; // 根据连续次数决定金钱数量
                        economy.depositPlayer(player, money);
                        String message = "§b>§6>§c> §6你不是倒霉蛋并获得了 " + money + " §6的钱！";
                        if (consecutiveCount > 1) {
                            message += "\n §a相同玩家多次为倒霉蛋 §e(随机的红包金额翻了 " + consecutiveCount + " 倍)";
                        }
                        player.sendMessage(message);
                    }
                }

                Bukkit.broadcastMessage("§b>§6>§c> " + ChatColor.YELLOW + unluckyPlayer.getName() + " §a被§b幻想娘§a评为 §6§l< 年度最佳倒霉蛋 >");
                Bukkit.broadcastMessage("§b>§6>§c> " + ChatColor.YELLOW + unluckyPlayer.getName() + " §a作为一个倒霉蛋，没获得§b幻想娘§a发的§c小红包！");
            }
        }.runTaskTimer(plugin, 0, 12000L); // 12000 ticks = 10 minutes
    }


    @EventHandler
    public void onPluginEnable(org.bukkit.event.server.PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("Vault")) {
            setupEconomy();
        }
    }
}
