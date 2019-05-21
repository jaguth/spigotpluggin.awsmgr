package com.jaguth.spigotpluggin.awsmgr.runnables;

import com.jaguth.spigotpluggin.awsmgr.AwsMgr;

public class EC2Monitor implements Runnable {
    private AwsMgr awsMgr;

    public EC2Monitor(AwsMgr awsMgr) {
        this.awsMgr = awsMgr;
    }

    @Override
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
}
