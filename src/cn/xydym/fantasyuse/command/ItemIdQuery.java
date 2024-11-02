package cn.xydym.fantasyuse.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemIdQuery implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fcname")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.isOp()) { // 检查是否是OP
                    if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
                        String itemName = player.getItemInHand().getType().name();
                        player.sendMessage(ChatColor.YELLOW + "注意！请删除前缀 fcul.name. 再使用这个id！");
                        player.sendMessage(ChatColor.GREEN + "可点击复制！: fcul.name." + itemName);
                    } else {
                        player.sendMessage(ChatColor.RED + "你当前没有手持任何物品。");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "这个命令只能由管理员执行。");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "这个命令只能由玩家执行。");
            }
            return true;
        }
        return false;
    }
}