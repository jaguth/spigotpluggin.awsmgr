package com.jaguth.spigotpluggin.awsmgr.runnables;

import com.jaguth.spigotpluggin.awsmgr.AwsMgr;
import com.jaguth.spigotpluggin.awsmgr.AwsMgrPluggin;
import org.bukkit.scheduler.BukkitRunnable;

public class AwsRunnable extends BukkitRunnable {
    private AwsMgr awsMgr;
    private AwsMgrPluggin awsMgrPluggin;

    public AwsRunnable(AwsMgr awsMgr, AwsMgrPluggin awsMgrPluggin) {
        this.awsMgr = awsMgr;
        this.awsMgrPluggin = awsMgrPluggin;
    }

    @Override
    public void run() {
        // Bukkit API does not allow creating entities outside its thread (which is what this runnable is).
        // So we put the code - that is both long running and creates an entity - inside a new runnable and then tell
        // our plugin to run the code as an asynchronous task which is somehow able to reconciliate the threads.
        // I'm doing this because running this within scheduleSyncRepeatingTask() will cause the whole server thread
        // to pause, because it takes a few seconds for the EC2 instance query to complete, and that happens every 15
        // seconds, so its like playing under heavy, heavy lag which is really annoying.

        // update: even with this, the server will still hang while the call to AWS is being made. Not sure if there is
        // a way to make this truly asynchronous so the main server thread isn't waiting for this thread to complete.
        new BukkitRunnable() {
            public void run() {
                if (!awsMgr.playersInServer()) {
                    // no players in server, not fetching from ec2
                    return;
                }

                if (!awsMgr.awsAvatarsInServer()) {
                    // no aws avatars in server, not fetching from ec2
                    return;
                }

                try {
                    awsMgr.fetchEc2InstancesAndMerge();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTask(awsMgrPluggin);
    }
}
