package cn.xydym.fantasyuse;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import java.util.List;

public class AnvilListener implements Listener {
    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof AnvilInventory) {
            AnvilInventory anvil = (AnvilInventory) event.getInventory();

            // 检查是否在结果槽位点击
            if (event.getSlotType() == SlotType.RESULT) {
                ItemStack resultItem = anvil.getItem(2); // 结果槽位的索引是 2

                if (resultItem != null && resultItem.getType() != Material.AIR) {
                    NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(resultItem));
                    if (nbt != null && nbt.containsKey("RepairCounter")) {
                        int repairCounter = nbt.getInteger("RepairCounter");
                        if (repairCounter >= 3) {
                            ItemMeta itemMeta = resultItem.getItemMeta();
                            if (itemMeta != null && itemMeta.hasLore()) {
                                List<String> lore = itemMeta.getLore();
                                boolean hasQianghua = false;
                                for (String line : lore) {
                                    if (line.contains("强化")) {
                                        hasQianghua = true;
                                        break;
                                    }
                                }
                                if (!hasQianghua) {
                                    sendCancelMessage(event, (Player) event.getWhoClicked()); // 有lore但没强化字样，取消点击
                                }
                            } else {
                                sendCancelMessage(event, (Player) event.getWhoClicked()); // 没任何lore，也取消点击
                            }
                        }
                    }
                }
            }
        }
    }

    // 有lore没强化字样/无lore 二合一提示
    private void sendCancelMessage(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        player.sendMessage("§c拔刀强化器才可锻刀，切换世界/重进恢复扣的等级");
    }
}

