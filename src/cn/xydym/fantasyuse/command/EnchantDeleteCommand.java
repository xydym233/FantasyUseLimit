package cn.xydym.fantasyuse.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;

public class EnchantDeleteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "你没有权限执行这个命令。");
                return true;
            }

            if (args.length == 1) {
                String enchantmentName = args[0];
                Enchantment enchantment = Enchantment.getByName(enchantmentName.toUpperCase());

                if (enchantment == null) {
                    player.sendMessage(ChatColor.RED + "附魔名无效，请检查拼写。");
                    return true;
                }

                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "你手上没有物品。");
                    return true;
                }

                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null || !itemMeta.hasEnchant(enchantment)) {
                    player.sendMessage(ChatColor.RED + "该物品上没有指定的附魔。");
                    return true;
                }

                itemMeta.removeEnchant(enchantment);
                item.setItemMeta(itemMeta);
                player.sendMessage(ChatColor.GREEN + "已删除附魔 " + enchantment.getName() + "。");
            } else {
                player.sendMessage(ChatColor.RED + "用法: /fcenchant <附魔名>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "该指令只能由玩家执行。");
        }
        return true;
    }
}