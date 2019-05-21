package com.jaguth.spigotpluggin.awsmgr.runnables;

import com.jaguth.spigotpluggin.awsmgr.AwsMgr;
import com.jaguth.spigotpluggin.awsmgr.AwsMgrPluggin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import static org.bukkit.Bukkit.getServer;

public class AwsRunnable extends BukkitRunnable {
    private AwsMgr awsMgr;
    private AwsMgrPluggin awsMgrPluggin;

    public AwsRunnable(AwsMgr awsMgr, AwsMgrPluggin awsMgrPluggin) {
        this.awsMgr = awsMgr;
        this.awsMgrPluggin = awsMgrPluggin;
    }

    @Override
    public void run() {
        EC2Monitor ec2Monitor = new EC2Monitor(awsMgr);
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleAsyncDelayedTask(awsMgrPluggin, ec2Monitor);
    }
}
