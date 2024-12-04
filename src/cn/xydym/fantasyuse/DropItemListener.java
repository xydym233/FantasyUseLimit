package cn.xydym.fantasyuse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class DropItemListener implements Listener {
    private final Map<UUID, Integer> dropCountMap = new WeakHashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        dropCountMap.put(player.getUniqueId(), 0);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        dropCountMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        UUID playerId = player.getUniqueId();

        if (item.getType() != Material.AIR) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) {
                List<String> lore = meta.getLore();
                boolean hasDropProtection = false;
                for (String line : lore) {
                    if (ChatColor.stripColor(line).contains("丢弃保护")) {
                        hasDropProtection = true;
                        break;
                    }
                }

                if (hasDropProtection) {
                    if (player.getInventory().firstEmpty() == -1) { // 检查背包是否已满
                        if (addEnderChestItem(player, item)) { // 尝试将物品放入末影箱
                            player.sendMessage(ChatColor.GREEN + "你的背包满了！物品已放入末影箱");
                            event.setCancelled(true);
                            return;
                        } else { // 末影箱也满了
                            player.sendMessage(ChatColor.RED + "末影箱已满，物品无法放入末影箱，已正常丢出");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "你不能丢弃这个物品，因为它有丢弃保护！");
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            int dropCount = dropCountMap.getOrDefault(playerId, 0);
            if (dropCount < 3) {
                player.sendMessage(ChatColor.YELLOW + "使用/fcq功能对手中的珍惜物品附加丢弃保护！");
                dropCountMap.put(playerId, dropCount + 1);
            }
        }
    }

    private boolean addEnderChestItem(Player player, ItemStack item) {
        Inventory enderChest = player.getEnderChest();
        if (enderChest.firstEmpty() != -1) { // 检查末影箱是否有空位
            enderChest.addItem(item);
            return true;
        }
        return false;
    }
}