package cn.xydym.fantasyuse;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class FCShowListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getTitle().contains(ChatColor.AQUA + " 的展示物品")) {
            event.setCancelled(true); // 取消点击事件，防止物品被移动
        }
    }
}