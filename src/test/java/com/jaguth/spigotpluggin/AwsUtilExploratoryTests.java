package com.jaguth.spigotpluggin;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.jaguth.spigotpluggin.awsmgr.AwsUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AwsUtilExploratoryTests {
    @Ignore
    @Test
    public void testDescribeInstances() {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        boolean done = false;

        DescribeInstancesRequest request = new DescribeInstancesRequest();
        List<DescribeInstancesResult> responses = new ArrayList<>();

        // get instances
        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            responses.add(response);
            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null) {
                done = true;
            }
        }

        // describe them
        for (DescribeInstancesResult response : responses) {
            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "Found instance with id %s, " +
                                    "AMI %s, " +
                                    "type %s, " +
                                    "state %s " +
                                    "and monitoring state %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
            }
        }
    }

    @Ignore
    @Test
    public void testDescribeInstancesImplementation() {
        List<Instance> instances = AwsUtil.getEC2Instances(Regions.US_EAST_1);

        for (Instance instance : instances) {
            instance.getTags().forEach(tag -> {
                if (tag.getKey().equalsIgnoreCase("name")) {
                    String name = tag.getValue();
                    System.out.println(instance.getInstanceId() + " - " + name);
                }
            });
        }
    }

    @Ignore
    @Test
    public void testTerminateInstance() {
        String instanceId = "instanceid";
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();
        terminateInstancesRequest.withInstanceIds(instanceId);
        ec2.terminateInstances(terminateInstancesRequest);
    }

    @SuppressWarnings( "deprecation" )
    @Test
    public void testGetCallerIdentity() {
        AWSSecurityTokenServiceClient client = new AWSSecurityTokenServiceClient();
        GetCallerIdentityRequest request = new GetCallerIdentityRequest();
        GetCallerIdentityResult result = client.getCallerIdentity(request);
        System.out.println(result.getAccount());
    }

    @Test
    public void testSQS() {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        ListQueuesResult listQueuesResult = sqs.listQueues();

        for (final String queueUrl : listQueuesResult.getQueueUrls()) {
            System.out.println("  QueueUrl: " + queueUrl);
        }
    }
}
