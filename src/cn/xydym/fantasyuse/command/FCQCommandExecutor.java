package cn.xydym.fantasyuse.command;

import cn.xydym.fantasyuse.LoadFantasy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

//
//  修复神秘灵宝等法杖删除lore时候变为铁法杖的问题 24.8.4
//


public class FCQCommandExecutor implements CommandExecutor {
    private final LoadFantasy plugin;
    private Economy economy;

    public FCQCommandExecutor(LoadFantasy plugin) {
        this.plugin = plugin;
        setupEconomy();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "这个指令只能由玩家执行。");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "给手持物品增加丢弃保护功能，需花费9999幻石！");
            player.sendMessage(ChatColor.GREEN + "输入 '/fcq yes' 增加手持物品的丢弃保护");
            player.sendMessage(ChatColor.GREEN + "输入 '/fcq no ' 移除手持物品的丢弃保护");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("yes")) {
            if (economy == null) {
                player.sendMessage(ChatColor.RED + "无法连接到经济系统。");
                return true;
            }

            if (!economy.has(player, 9999)) {
                player.sendMessage(ChatColor.RED + "你的钱并不够用！");
                return true;
            }

            PlayerInventory inventory = player.getInventory();
            ItemStack item = inventory.getItemInHand();

            if (item.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "你手中没有物品。");
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) {
                List<String> lore = meta.getLore();
                boolean hasDropProtection = false;
                for (String line : lore) {
                    if (ChatColor.stripColor(line).contains("丢弃保护")) {
                        hasDropProtection = true;
                        player.sendMessage(ChatColor.YELLOW + "当前道具已经附加丢弃保护。");
                        break;
                    }
                }

                if (!hasDropProtection) {
                    boolean foundEmptyLine = false;
                    for (int i = 0; i < lore.size(); i++) {
                        String line = lore.get(i);
                        if (line.trim().isEmpty()) {
                            foundEmptyLine = true;
                            lore.set(i, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("drop_protection_keyword")));
                            break;
                        }
                    }

                    if (!foundEmptyLine) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("drop_protection_keyword")));
                    }

                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN + "已为当前道具附加丢弃保护。");

                    if (economy.withdrawPlayer(player, 9999).transactionSuccess()) {
                        player.sendMessage(ChatColor.GREEN + "已扣除 9999 金钱。");
                    } else {
                        player.sendMessage(ChatColor.RED + "扣除金钱失败，请检查你的余额。");
                    }
                }
            } else {
                // 如果物品没有 Lore，则直接添加丢弃保护
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("drop_protection_keyword")));
                meta.setLore(lore);
                item.setItemMeta(meta);
                player.sendMessage(ChatColor.GREEN + "已为当前道具附加丢弃保护。");

                if (economy.withdrawPlayer(player, 9999).transactionSuccess()) {
                    player.sendMessage(ChatColor.GREEN + "已扣除 9999 金钱。");
                } else {
                    player.sendMessage(ChatColor.RED + "扣除金钱失败，请检查你的余额。");
                }
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("no")) {
            PlayerInventory inventory = player.getInventory();
            ItemStack item = inventory.getItemInHand();

            if (item.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "你手中没有物品。");
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) {
                List<String> lore = meta.getLore();
                boolean found = false;
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i);
                    if (ChatColor.stripColor(line).contains("丢弃保护")) {
                        found = true;
                        lore.set(i, " "); // 将整行替换为一个空格
                    }
                }
                if (found) {
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN + "已移除当前道具的丢弃保护。");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "当前道具没有丢弃保护。");
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "当前道具没有丢弃保护。");
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "使用 '/fcq yes' 增加丢弃保护，或 '/fcq no' 移除丢弃保护。");
        }
        return true;
        }
}