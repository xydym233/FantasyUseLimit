package cn.xydym.fantasyuse.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TitleAllCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.isOp()) {
                if (args.length >= 1) {
                    String title = String.join(" ", args);
                    // 替换 & 为 §
                    title = ChatColor.translateAlternateColorCodes('&', title);
                    sendTitleToAllPlayers(title);
                    player.sendMessage(ChatColor.GREEN + "已向所有玩家发送标题: " + title);
                } else {
                    player.sendMessage(ChatColor.RED + "用法: /titleall <标题内容>");
                }
            } else {
                player.sendMessage(ChatColor.RED + "你没有权限使用该指令！");
            }
        } else {
            sender.sendMessage("该指令只能由玩家使用！");
        }
        return true;
    }

    // 设置显示时间
    private void sendTitleToAllPlayers(String title) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(title, "", 100, 400, 100);
        }
    }
}
