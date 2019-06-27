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
import com.amazonaws.services.sqs.model.*;
import com.jaguth.spigotpluggin.awsmgr.AwsUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.jaguth.spigotpluggin.awsmgr.AwsUtil.createTagText;

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
                                    "and nametag %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            createTagText(instance));
                }
            }
        }
    }

    @Ignore
    @Test
    public void testDescribeInstancesImplementation() {
        List<Instance> instances = AwsUtil.getEC2Instances("us-west-2");

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
    public void testSQSListQueues() {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        ListQueuesResult listQueuesResult = sqs.listQueues();

        for (final String queueUrl : listQueuesResult.getQueueUrls()) {
            System.out.println("QueueUrl: " + queueUrl);
        }
    }

    @Test
    public void testSQSGetQueueUrl() {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        String queueName = "TestQueueA";
        String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();

        System.out.println("QueueUrl: " + queueUrl);
    }

    @Test
    public void testSQSSendMessage() {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        String queueName = "TestQueueA";
        String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();

        SendMessageResult sendMessageResult = sqs.sendMessage(queueUrl, "{ \"hello\": \"world\" }"); // default will dedupe message before put into queue

        System.out.println("Sent Message ID: " + sendMessageResult.getMessageId());
    }

    @Test
    public void testSQSReceiveMessage() {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        String queueName = "TestQueueA";
        String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
        receiveMessageRequest.setQueueUrl(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);

        ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(receiveMessageRequest); // receive up to max number messages set
        //ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(queueUrl); // default is receive 1 message from queue

        int messageNumber = 1;

        System.out.println("Messages received: " + receiveMessageResult.getMessages().size());

        for (Message message : receiveMessageResult.getMessages()) {
            System.out.println("Message #" + messageNumber + ": " + message.getBody());
            messageNumber++;
        }
    }

    @Test
    public void testSQSDeleteSingleMessage() {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        String queueName = "TestQueueA";
        String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();

        ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(queueUrl);
        String receiptHandle = receiveMessageResult.getMessages().iterator().next().getReceiptHandle(); // get receipt handle of first message

        System.out.println("Message to delete via receipt handle: " + receiptHandle);

        DeleteMessageResult deleteMessageResult = sqs.deleteMessage(queueUrl, receiptHandle);
        System.out.println("Delete message result: " + deleteMessageResult.toString());
    }
}
