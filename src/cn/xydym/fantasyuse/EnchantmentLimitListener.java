package cn.xydym.fantasyuse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentLimitListener implements Listener {

    private static final String ENCHANTMENT_CAP_CONFIG_PATH = "Enchantment_cap";
    private static final String ENCHANTMENT_BREAK_CONFIG_PATH = "Enchantment_Break";
    private static final String OTHER_CONFIG_PATH = "OTHEREnchantment";

    private final Map<Enchantment, Integer> enchantmentLimits;
    private final Map<Material, Map<Enchantment, Integer>> itemSpecificLimits;
    private int otherMaxLevel;

    public EnchantmentLimitListener(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        enchantmentLimits = new HashMap<>();
        itemSpecificLimits = new HashMap<>();

        loadEnchantmentLimits(plugin);
        loadItemSpecificLimits(plugin);
        loadOtherMaxLevel(plugin);
    }

    private void loadEnchantmentLimits(JavaPlugin plugin) {
        for (String enchantmentData : plugin.getConfig().getStringList(ENCHANTMENT_CAP_CONFIG_PATH)) {
            String[] data = enchantmentData.split("\\|");
            if (data.length == 2) {
                Enchantment enchantment = getEnchantmentByName(data[0]);
                int maxLevel = Integer.parseInt(data[1]);
                if (enchantment != null) {
                    enchantmentLimits.put(enchantment, maxLevel);
                }
            }
        }
    }

    private void loadItemSpecificLimits(JavaPlugin plugin) {
        for (String itemData : plugin.getConfig().getStringList(ENCHANTMENT_BREAK_CONFIG_PATH)) {
            String[] data = itemData.split("\\|");
            if (data.length == 2) {
                Material material = Material.getMaterial(data[0]);
                if (material != null) {
                    Map<Enchantment, Integer> limits = new HashMap<>();
                    for (String enchantmentData : data[1].split(",")) {
                        String[] enchantmentInfo = enchantmentData.split(":");
                        if (enchantmentInfo.length == 2) {
                            Enchantment enchantment = getEnchantmentByName(enchantmentInfo[0]);
                            int maxLevel = Integer.parseInt(enchantmentInfo[1]);
                            if (enchantment != null) {
                                limits.put(enchantment, maxLevel);
                            }
                        }
                    }
                    itemSpecificLimits.put(material, limits);
                }
            }
        }
    }

    private void loadOtherMaxLevel(JavaPlugin plugin) {
        otherMaxLevel = plugin.getConfig().getInt(OTHER_CONFIG_PATH, 1); // 默认值为1
    }

    private Enchantment getEnchantmentByName(String name) {
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.getName().equalsIgnoreCase(name)) {
                return enchantment;
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (item != null && item.getType() != Material.AIR) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null && itemMeta.hasEnchants()) {
                checkEnchantments(item, event);
            }
        }
    }

    private void checkEnchantments(ItemStack item, PlayerItemHeldEvent event) {
        Material itemType = item.getType();
        if (itemSpecificLimits.containsKey(itemType)) {
            checkItemSpecificLimits(item, event);
        } else {
            checkEnchantmentCap(item, event);
        }
    }

    private void checkEnchantmentCap(ItemStack item, PlayerItemHeldEvent event) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && itemMeta.hasEnchants()) {
            Map<Enchantment, Integer> enchantments = new HashMap<>(itemMeta.getEnchants());
            boolean modified = false; // 标记是否已经修改了附魔

            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                int maxLevel = enchantmentLimits.getOrDefault(enchantment, otherMaxLevel);

                if (level > maxLevel) {
                    itemMeta.removeEnchant(enchantment);
                    itemMeta.addEnchant(enchantment, maxLevel, true);
                    modified = true; // 标记已经修改了附魔
                }
            }

            if (modified) {
                event.getPlayer().sendMessage(ChatColor.RED + "附魔超过等级上限，已自动调整！");
                item.setItemMeta(itemMeta);
                event.getPlayer().getInventory().setItem(event.getNewSlot(), item);
            }
        }
    }

    private void checkItemSpecificLimits(ItemStack item, PlayerItemHeldEvent event) {
        Material itemType = item.getType();
        Map<Enchantment, Integer> limits = itemSpecificLimits.getOrDefault(itemType, new HashMap<>());

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && itemMeta.hasEnchants()) {
            Map<Enchantment, Integer> enchantments = new HashMap<>(itemMeta.getEnchants());
            boolean modified = false; // 标记是否已经修改了附魔

            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                int maxLevel = limits.getOrDefault(enchantment, enchantmentLimits.getOrDefault(enchantment, otherMaxLevel));

                if (level > maxLevel) {
                    itemMeta.removeEnchant(enchantment);
                    itemMeta.addEnchant(enchantment, maxLevel, true);
                    modified = true; // 标记已经修改了附魔
                }
            }

            if (modified) {
                event.getPlayer().sendMessage(ChatColor.RED + "附魔超过等级上限，已自动调整！");
                item.setItemMeta(itemMeta);
                event.getPlayer().getInventory().setItem(event.getNewSlot(), item);
            }
        }
    }
}