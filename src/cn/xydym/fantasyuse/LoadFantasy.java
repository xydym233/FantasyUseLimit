package cn.xydym.fantasyuse;

import java.util.Iterator;
import java.util.List;

import cn.xydym.fantasyuse.command.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;

public class LoadFantasy extends JavaPlugin implements Listener, TabCompleter {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        FileConfiguration config = this.getConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        int delayMinutes = config.getInt("Startcommandstime");
        List<String> commands = config.getStringList("Startcommands");
        boolean enableKeepInventory = config.getBoolean("EnableKeepInventory", false);
        // 是否打开 PlayerMoveListener 监听器
        boolean openBanBlock = config.getBoolean("OPENbanblock", false); // 读取 OPENbanblock 配置

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Iterator var1 = commands.iterator();

            while (var1.hasNext()) {
                String command = (String) var1.next();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                Bukkit.getLogger().info("已执行开服自动指令，来自 FantasyUseLimit 插件");
            }

            // 设置 keepInventory 游戏规则
            if (enableKeepInventory) {
                for (World world : Bukkit.getWorlds()) {
                    world.setGameRuleValue("keepInventory", "true");
                }
                Bukkit.getLogger().info("已启用 keepInventory 游戏规则");
            }

        }, (long) delayMinutes * 20L);

        // 丢弃保护功能
        this.getCommand("fcq").setExecutor(new FCQCommandExecutor(this));
        this.getServer().getPluginManager().registerEvents(new DropItemListener(), this);

        // 使用指令修改物品名字
        this.getCommand("fcitemname").setExecutor(new ItemNameCommand());
        // 删除某个附魔
        this.getCommand("fcenchant").setExecutor(new EnchantDeleteCommand());
        // 展示手持物品
        this.getCommand("fcshow").setExecutor(new FCShowCommand());
        this.getCommand("fcshow_open").setExecutor(new FCShowOpenCommand());
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new FCShowListener(), this);

        // 无限耐久指令
        this.getCommand("fcwxnj").setExecutor(new FCWXNJCommand());

        // 查看方块与物品id
        this.getCommand("fcname").setExecutor(new ItemIdQuery());
        this.getCommand("fcblock").setExecutor(new BlockHandler());
        getServer().getPluginManager().registerEvents(new BlockHandler(), this);

        // 玩家移动检查周围是否存在禁用的方块
        if (openBanBlock) { // 检查 OPENbanblock 是否为 true
            getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
            Bukkit.getLogger().info("FantasyUseLimit：高占用的方块监控者已启动！");
        }

        // 区块实体限制
        new ChunkEntityLimiterListener(this);



        this.getServer().getPluginManager().registerEvents(new AntiSpeedListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EnchantmentLimitListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockPhysicsListener(this), this);
        this.getServer().getPluginManager().registerEvents(new AntiSpamListener(this), this);
        this.getServer().getPluginManager().registerEvents(new AutoRebirth(this), this);
        this.getServer().getPluginManager().registerEvents(new FixWrench(this), this);
        this.getServer().getPluginManager().registerEvents(new NotExplode(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityProjectileListener(this), this);

        Bukkit.getLogger().info("FantasyUseLimit：功能已全部加载完成，高占用功能已提示！");

        // 关服踢出玩家的白名单设置
    }

    @Override
    public void onDisable() {
        // 注销所有与当前插件相关的事件处理器
        HandlerList.unregisterAll((Plugin) this);
        this.getLogger().info(ChatColor.RED + "FantasyUseLimit已经卸载");
    }
}
