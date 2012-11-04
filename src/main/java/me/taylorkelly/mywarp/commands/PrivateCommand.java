package me.taylorkelly.mywarp.commands;

import me.taylorkelly.mywarp.LanguageManager;
import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.data.Warp;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateCommand extends BasicCommand implements Command {
    private MyWarp plugin;

    public PrivateCommand(MyWarp plugin) {
        super("Private");
        this.plugin = plugin;
        setDescription("Make §9<name>§e a private warp");
        setUsage("/warp private §9<name>");
        setArgumentRange(1, 255);
        setIdentifiers("private");
        setPermission("mywarp.warp.soc.private");
    }

    @Override
    public boolean execute(CommandSender executor, String identifier, String[] args) {
        Player player = null;

        if (executor instanceof Player) {
            player = (Player) executor;
        }
        String name = plugin.getWarpList()
                .getMatche(StringUtils.join(args, ' '), player);

        if (!plugin.getWarpList().warpExists(name)) {
            executor.sendMessage(LanguageManager.getString("error.noSuchWarp").replaceAll("%warp%", name));
            return true;
        }

        Warp warp = plugin.getWarpList().getWarp(name);

        if (player != null ? !warp.playerCanModify(player) : false) {
            executor.sendMessage(LanguageManager.getString("error.noPermission.private"));
            return true;
        }

        if (player != null ? !plugin.getWarpList().playerCanBuildPrivateWarp(player)
                : false) {
            executor.sendMessage(LanguageManager.getString("limit.private.reached").replaceAll("%maxPrivate%", Integer.toString(MyWarp.getWarpPermissions().maxPrivateWarps(player))));
            return true;
        }

        plugin.getWarpList().privatize(name);
        executor.sendMessage(LanguageManager.getString("warp.private").replaceAll("%warp%", name));
        return true;
    }
}
