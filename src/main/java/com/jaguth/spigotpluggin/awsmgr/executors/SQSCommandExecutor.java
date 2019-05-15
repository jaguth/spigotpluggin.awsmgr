package com.jaguth.spigotpluggin.awsmgr.executors;

import com.jaguth.spigotpluggin.awsmgr.AwsMgr;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SQSCommandExecutor implements CommandExecutor {
    private AwsMgr awsMgr;

    public SQSCommandExecutor(AwsMgr awsMgr) {
        this.awsMgr = awsMgr;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        // /SQS [arg0] [arg1] [arg2]
        String subCommand = args[0];

        switch (subCommand.toLowerCase()) {
            case "receiver":
                return handleReceiverCommand(sender, args);
            case "sender":
                return handleSenderCommand(sender, args);
            default:
                return false;
        }
    }

    private boolean handleReceiverCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String queueName = args[1];

        try {
            awsMgr.addSQSReceiverToWorld(sender.getName(), queueName);
        }
        catch (Exception e) {
            sender.sendMessage("Failed to add SQS receiver queue \"" + queueName + "\" to world: " + e.toString());
            return false;
        }

        return true;
    }

    private boolean handleSenderCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String queueName = args[1];

        try {
            awsMgr.addSQSSenderToWorld(sender.getName(), queueName);
        }
        catch (Exception e) {
            sender.sendMessage("Failed to add SQS sender queue \"" + queueName + "\" to world: " + e.toString());
            return false;
        }

        return true;
    }

}
