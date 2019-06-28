package com.jaguth.spigotpluggin.awsmgr.domain;

import org.bukkit.block.Block;

public class GroupInfo {
    private String entityType;
    private Block spawnBlock;

    public GroupInfo(String entityType, Block spawnBlock) {
        this.entityType = entityType;
        this.spawnBlock = spawnBlock;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Block getSpawnBlock() {
        return spawnBlock;
    }

    public void setSpawnBlock(Block spawnBlock) {
        this.spawnBlock = spawnBlock;
    }
}
