package cn.xydym.fantasyuse.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemNameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.isOp()) { // 检查玩家是否是OP
                if (args.length > 0) {
                    ItemStack item = player.getInventory().getItemInHand();
                    if (item != null) {
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
                        item.setItemMeta(meta);
                        player.sendMessage(ChatColor.GREEN + "物品名称已更改为: " + meta.getDisplayName());
                    } else {
                        player.sendMessage(ChatColor.RED + "你手中没有物品。");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "请指定一个物品名称。");
                }
            } else {
                player.sendMessage(ChatColor.RED + "你没有权限执行此命令。");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "该命令只能由玩家执行。");
        }
        return true;
    }
}