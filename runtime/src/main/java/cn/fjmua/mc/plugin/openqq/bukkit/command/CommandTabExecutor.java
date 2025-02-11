package cn.fjmua.mc.plugin.openqq.bukkit.command;

import cn.fjmua.mc.plugin.openqq.script.ScriptManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class CommandTabExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1) {
            if ("reload".equalsIgnoreCase(args[0])) {
                ScriptManager.getInstance().clearAndLoadScripts();
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return List.of("reload");
    }

}