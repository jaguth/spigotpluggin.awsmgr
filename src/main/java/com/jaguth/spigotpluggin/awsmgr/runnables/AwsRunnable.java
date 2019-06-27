package com.jaguth.spigotpluggin.awsmgr.runnables;

import com.jaguth.spigotpluggin.awsmgr.AwsMgr;

public class AwsRunnable implements Runnable {
    private AwsMgr awsMgr;

    public AwsRunnable(AwsMgr awsMgr) {
        this.awsMgr = awsMgr;
    }

    @Override
    public void run() {
        if (!awsMgr.playersInServer()) {
            // no players in server, not fetching from ec2
            return;
        }

        if (!awsMgr.awsAvatarsInServer()) {
            // no aws avatars in server, not fetching from ec2.
            // its either that or always fetch, which may be too much API traffic.
            // anyways, the bug in this is that if a single instance is fetched and then killed, then this will not
            // continue to try and fetch instances from the ASG. todo: make the code better so we don't have this kind of bug
            return;
        }

        try {
            awsMgr.fetchEc2InstancesAndMerge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
