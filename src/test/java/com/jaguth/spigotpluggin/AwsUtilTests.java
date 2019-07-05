package com.jaguth.spigotpluggin;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.google.common.collect.Lists;
import com.jaguth.spigotpluggin.awsmgr.AwsUtil;
import com.jaguth.spigotpluggin.awsmgr.domain.GroupInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static com.jaguth.spigotpluggin.awsmgr.AwsUtil.INSTANCE_RUNNING_CODE;

public class AwsUtilTests {
    @Test
    public void testInstanceNotRunning_InstanceIsNull() {
        Instance instance = null;

        boolean isInstanceRunning = AwsUtil.isEC2InstanceRunning(instance);
        Assert.assertFalse(isInstanceRunning);
    }

    @Test
    public void testInstanceNotRunning_InstanceIsWrongCode() {
        Instance instance = new Instance();
        InstanceState instanceState = new InstanceState();
        instanceState.setCode(12);
        instance.setState(instanceState);

        boolean isInstanceRunning = AwsUtil.isEC2InstanceRunning(instance);
        Assert.assertFalse(isInstanceRunning);
    }

    @Test
    public void testInstanceNotRunning_InstanceIsRunningCode() {
        Instance instance = new Instance();
        InstanceState instanceState = new InstanceState();
        instanceState.setCode(INSTANCE_RUNNING_CODE);
        instance.setState(instanceState);

        boolean isInstanceRunning = AwsUtil.isEC2InstanceRunning(instance);
        Assert.assertTrue(isInstanceRunning);
    }

    @Test
    public void testGetInstances() {
        HashMap<String, GroupInfo> instanceGroups = new HashMap<>();
        instanceGroups.put("sociallinks", new GroupInfo("cow", null));
        List<Instance> instances = AwsUtil.getEC2Instances(instanceGroups, "us-west-2");
        Assert.assertTrue(instances.size() > 0);
    }

}
