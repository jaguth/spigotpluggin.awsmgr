package com.jaguth.spigotpluggin.awsmgr;

import com.jaguth.spigotpluggin.awsmgr.executors.EC2CommandExecutor;
import com.jaguth.spigotpluggin.awsmgr.executors.SQSCommandExecutor;
import com.jaguth.spigotpluggin.awsmgr.executors.SpawnCommandExecutor;
import com.jaguth.spigotpluggin.awsmgr.listeners.PlayerListener;
import com.jaguth.spigotpluggin.awsmgr.runnables.AwsRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AwsMgrPluggin extends JavaPlugin {
    public static final long MINECRAFT_TICKS_PER_SECOND = 20;

    @Override
    public void onEnable() {
        AwsMgr awsMgr = new AwsMgr(this);

        this.getCommand("ec2").setExecutor(new EC2CommandExecutor(awsMgr));
        this.getCommand("sqs").setExecutor(new SQSCommandExecutor(awsMgr));
        this.getCommand("spawn").setExecutor(new SpawnCommandExecutor(awsMgr));
        getServer().getPluginManager().registerEvents(new PlayerListener(awsMgr), this);

        long delayInSeconds = 10;
        long periodInSeconds = 10 * MINECRAFT_TICKS_PER_SECOND;

        AwsRunnable awsRunnable = new AwsRunnable(awsMgr);
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, awsRunnable, delayInSeconds, periodInSeconds);

        System.out.println("[AwsMgr] Enabled");
        System.out.println("[AwsMgr] Make sure you set your aws credential provide chain 'before' you start your spigot server. For example, your ~/.aws/credentials file or the environment variables.  Otherwise, the AWS calls will fail.  More Info: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html");
    }

    @Override
    public void onDisable() {
        System.out.println("[AwmMgr] Disabled");
    }
}
