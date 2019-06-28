package com.jaguth.spigotpluggin.awsmgr;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;
import com.google.common.collect.Lists;
import com.jaguth.spigotpluggin.awsmgr.domain.AwsAvatar;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AwsUtil {
    public static final int INSTANCE_RUNNING_CODE = 16;
    public static final int INSTANCE_PENDING_CODE = 0;

    public static List<Instance> callAwsAndFilterEC2Instances(String ec2NameContainsFilter, String region) {
        List<Instance> responseInstances = AwsUtil.getEC2Instances(region);
        List<Instance> filteredInstances = new ArrayList<>();

        for (Instance instance : responseInstances) {
            // todo: figure out good strategy to not hardcode which tag to search
            String nameToUse = getValueFromTags(instance.getTags(), "Name");

            if (nameToUse == null) {
                continue;
            }

            if (nameToUse.toLowerCase().contains(ec2NameContainsFilter.toLowerCase())) {
                filteredInstances.add(instance);
            }
        }

        return filteredInstances;
    }

    public static List<Instance> getEC2Instances(String region) {
        return getEC2Instances(null, region);
    }

    public static List<Instance> getEC2Instances(HashMap<String, String> instanceGroups, String region) {
        List<Instance> instances = new ArrayList<>();

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
                                                    .withRegion(region)
                                                    .build();

        boolean done = false;

        DescribeInstancesRequest request = new DescribeInstancesRequest();

        // not sure why filter doesn't behave as intended. for now, just grab all ec2 instances. will revisit.
//        if (uniqueInstanceNames != null && uniqueInstanceNames.size() > 0) {
//            Filter nameTagFilter = new Filter("tag:Name").withValues(uniqueInstanceNames);
//            Filter autoScalingGroupNameTagFilter = new Filter("tag:aws:autoscaling:groupName").withValues(uniqueInstanceNames);
//            List<Filter> filters = Lists.newArrayList(nameTagFilter, autoScalingGroupNameTagFilter);
//            request.setFilters(filters);
//        }

        if (instanceGroups != null && instanceGroups.size() > 0) {
            // todo: figure out good strategy to not hardcode which tag to search
            List<String> instanceGroupNames = new ArrayList<>(instanceGroups.keySet());
            Filter nameTagFilter = new Filter("tag:Name").withValues(instanceGroupNames);
            List<Filter> filters = Lists.newArrayList(nameTagFilter);
            request.setFilters(filters);
        }

        List<DescribeInstancesResult> responses = new ArrayList<>();

        // get instance descriptions. uses paging to handle bulk data
        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            responses.add(response);
            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null) {
                done = true;
            }
        }

        for (DescribeInstancesResult response : responses) {
            for (Reservation reservation : response.getReservations()) {
                instances.addAll(reservation.getInstances());
            }
        }

        return instances;
    }

    public static String getValueFromTags(List<Tag> tags, String key) {
        for (Tag tag : tags) {
            if (tag.getKey().equalsIgnoreCase(key)) {
                return tag.getValue();
            }
        }

        return null;
    }

    public static String createTagText(Instance instance) {
        // todo: figure out good strategy to not hardcode which tag to search
        String nameToUse = AwsUtil.getValueFromTags(instance.getTags(), "Name");
        String instanceId = instance.getInstanceId();
        String tagText = nameToUse + " [" + instanceId + "]";

        return tagText;
    }

    public static void terminateEC2Instance(String instanceId, AwsAvatar awsAvatar, String region) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
                                                    .withRegion(region)
                                                    .build();

        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();
        terminateInstancesRequest.withInstanceIds(instanceId);
        ec2.terminateInstances(terminateInstancesRequest);

        Bukkit.broadcastMessage("Instance " + awsAvatar.getMinecraftEntity().getCustomName() + " terminated!");
    }

    public static void fakeTerminateEC2Instance(AwsAvatar awsAvatar) {
        Bukkit.broadcastMessage("[fake] Instance " + awsAvatar.getMinecraftEntity().getCustomName() + " terminated!");
    }

    public static boolean isEC2InstanceRunning(Instance instance) {
        if (instance == null) {
            // no longer in aws
            return false;
        }

        if (!(instance.getState().getCode().equals(INSTANCE_RUNNING_CODE)
                || instance.getState().getCode().equals(INSTANCE_PENDING_CODE))) {
            // not running. most likely in 'terminating' state before is null
            return false;
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    public static String getAwsAccount() {
        AWSSecurityTokenServiceClient client = new AWSSecurityTokenServiceClient();
        GetCallerIdentityRequest request = new GetCallerIdentityRequest();
        GetCallerIdentityResult result = client.getCallerIdentity(request);

        return result.getAccount();
    }
}
