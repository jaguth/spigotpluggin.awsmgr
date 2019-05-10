package com.jaguth.spigotpluggin.awsmgr.domain;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;

public class AwsSqsQueue {
    private List<Message> messagesToReceive;
    private List<Message> receivedMessages;

}
