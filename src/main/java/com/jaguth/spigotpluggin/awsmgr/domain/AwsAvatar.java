package com.jaguth.spigotpluggin.awsmgr.domain;

import com.amazonaws.services.ec2.model.Instance;
import org.bukkit.entity.Entity;

public class AwsAvatar {
    private Entity minecraftEntity;
    private Instance awsInsance;
    private String fetchedByPlayerName;

    public AwsAvatar(Entity minecraftEntity, Instance awsInsance, String fetchedByPlayerName) {
        this.minecraftEntity = minecraftEntity;
        this.awsInsance = awsInsance;
        this.fetchedByPlayerName = fetchedByPlayerName;
    }

    public Entity getMinecraftEntity() {
        return minecraftEntity;
    }

    public void setMinecraftEntity(Entity minecraftEntity) {
        this.minecraftEntity = minecraftEntity;
    }

    public Instance getAwsInsance() {
        return awsInsance;
    }

    public void setAwsInsance(Instance awsInsance) {
        this.awsInsance = awsInsance;
    }

    public String getFetchedByPlayerName() {
        return fetchedByPlayerName;
    }

    public void setFetchedByPlayerName(String fetchedByPlayerName) {
        this.fetchedByPlayerName = fetchedByPlayerName;
    }
}

