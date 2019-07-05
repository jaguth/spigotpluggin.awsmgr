package com.jaguth.spigotpluggin.awsmgr.domain;

import org.bukkit.block.Sign;

public class GroupInfo {
    private String entityType;
    private Sign spawnedSign;

    public GroupInfo(String entityType, Sign spawnedSign) {
        this.entityType = entityType;
        this.spawnedSign = spawnedSign;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Sign getSpawnedSign() {
        return spawnedSign;
    }

    public void setSpawnedSign(Sign spawnedSign) {
        this.spawnedSign = spawnedSign;
    }
}
