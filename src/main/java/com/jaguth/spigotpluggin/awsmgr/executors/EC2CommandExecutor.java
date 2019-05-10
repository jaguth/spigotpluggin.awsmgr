package com.jaguth.spigotpluggin.awsmgr.executors;

import com.jaguth.spigotpluggin.awsmgr.AwsMgr;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EC2CommandExecutor implements CommandExecutor {
    private AwsMgr awsMgr;

    public EC2CommandExecutor(AwsMgr awsMgr) {
        this.awsMgr = awsMgr;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        // /EC2 [arg0] [arg1] [arg2]
        String subCommand = args[0];

        switch (subCommand.toLowerCase()) {
            case "fetch":
                return handleFetchCommand(sender, args);
            case "clear":
                return handleClearCommand(sender, args);
            case "info":
                return handleInfoCommand(sender, args);
            case "mode":
                return handleModeCommand(sender, args);
            case "region":
                return handleRegionCommand(sender, args);
            default:
                return false;
        }
    }

    private boolean handleFetchCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return false;
        }

        String ec2NameFilter = args[1];
        String entityType = args[2];

        try {
            Bukkit.broadcastMessage(sender.getName() + " is fetching " + ec2NameFilter);
            awsMgr.fetchEC2AndSpawnAwsAvatars(sender.getName(), ec2NameFilter, entityType);
        }
        catch (Exception e) {
            sender.sendMessage("Failed to fetch EC2 instances: " + e.toString());
            System.out.println("Failed to fetch EC2 instances: " + System.lineSeparator() + ExceptionUtils.getStackTrace(e));
            return false;
        }

        return true;
    }

    private boolean handleClearCommand(CommandSender sender, String[] args) {
        try {
            awsMgr.clearPlayerAvatars(sender.getName());
            Bukkit.broadcastMessage(sender.getName() + " has cleared all instances");
        }
        catch (Exception e) {
            sender.sendMessage("Failed to clear avatars: " + e.toString());
            return false;
        }

        return true;
    }

    private boolean handleInfoCommand(CommandSender sender, String[] args) {
        try {
            awsMgr.printInfo();
        }
        catch (Exception e) {
            sender.sendMessage("Failed to generate info: " + e.toString());
            return false;
        }

        return true;
    }

    private boolean handleModeCommand(CommandSender sender, String[] args) {
        try {
            awsMgr.toggleMode();
            Bukkit.broadcastMessage(sender.getName() + " set mode to " + awsMgr.getDestructiveMode());
        }
        catch (Exception e) {
            sender.sendMessage("Failed to generate info: " + e.toString());
            return false;
        }

        return true;
    }

    private boolean handleRegionCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String region = args[1];

        try {
            awsMgr.setRegion(region);
            awsMgr.clearAllAvatars();
            Bukkit.broadcastMessage(sender.getName() + " set region to " + region);
        }
        catch (Exception e) {
            sender.sendMessage("Failed to generate info: " + e.toString());
            return false;
        }

        return true;
    }
}
