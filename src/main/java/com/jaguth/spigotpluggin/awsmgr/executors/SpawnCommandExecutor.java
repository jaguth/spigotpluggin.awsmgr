package com.jaguth.spigotpluggin.awsmgr.executors;

import com.jaguth.spigotpluggin.awsmgr.AwsMgr;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.jaguth.spigotpluggin.awsmgr.MinecraftUtil;
import org.bukkit.entity.Player;

public class SpawnCommandExecutor implements CommandExecutor {
    private AwsMgr awsMgr;
    private static final String USAGE_TEXT = "Usage: /SPAWN <entity_name> <(optional) count>";

    public SpawnCommandExecutor(AwsMgr awsMgr) {
        this.awsMgr = awsMgr;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(USAGE_TEXT);
            return false;
        }

        String entityName = args[0];
        String tagText = args[1];
        int count = 1;

        if (args.length == 3) {
            try {
                count = Integer.parseInt(args[2]);
            }
            catch (Exception e) {
                sender.sendMessage(e.getMessage());
            }
        }

        Player player = awsMgr.getPlayer(sender.getName());

        try {
            for (int i = 0; i < count; i++) {
                MinecraftUtil.spawnEntityFromText(entityName, tagText, player);
            }
        }
        catch (Exception e) {
            sender.sendMessage("Spawn failed: " + e.toString());
            return false;
        }

        return true;
    }

}
