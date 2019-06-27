package com.jaguth.spigotpluggin.awsmgr;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Instance;
import com.jaguth.spigotpluggin.awsmgr.domain.AwsAvatar;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

import static com.jaguth.spigotpluggin.awsmgr.AwsUtil.isEC2InstanceRunning;

public class AwsMgr {
    private AwsMgrPluggin awsMgrPluggin;
    private HashMap<String, Player> playerMap; // key = playerName
    private HashMap<String, AwsAvatar> awsAvatarMap; // key = instanceId
    private List<String> uniqueInstanceNames;
    private Boolean destructiveMode;
    private String region;

    public AwsMgr(AwsMgrPluggin awsMgrPluggin) {
        this.awsMgrPluggin = awsMgrPluggin;
        loadAll();
    }

    public void loadAll() {
        loadPlayers();
        loadAwsAvatars();
        loadUniqueInstanceNames();
        loadDestructiveMode();
        loadRegion();
    }

    // note: spigot handles all plugin classes as stateless. so each time an event is triggered, AwsMgr is instantiated, and all of its members
    // are null.  Therefore, whenever this class is instantiated, we need to load from a saved state in spigot's config to our members.
    // That also means that whenever a member state is changed, it also needs to save its state in spigot's config.
    private void loadPlayers() {
        playerMap = (HashMap<String, Player>) awsMgrPluggin.getConfig().get("playerMap");

        if (playerMap == null) {
            // if first time loaded, then there will have been no previous state, so it will be null.
            // this is when we want to instantiate the member.
            playerMap = new HashMap<>();
            savePlayersState(playerMap);
        }
    }

    private void loadAwsAvatars() {
        awsAvatarMap = (HashMap<String, AwsAvatar>) awsMgrPluggin.getConfig().get("awsAvatarMap");

        if (awsAvatarMap == null) {
            awsAvatarMap = new HashMap<>();
            saveAwsAvatarsState(awsAvatarMap);
        }
    }

    private void loadUniqueInstanceNames() {
        uniqueInstanceNames = (List<String>) awsMgrPluggin.getConfig().get("uniqueInstanceNames");

        if (uniqueInstanceNames == null) {
            uniqueInstanceNames = new ArrayList<>();
            saveUniqueInstanceNameState(uniqueInstanceNames);
        }
    }

    private void loadDestructiveMode() {
        destructiveMode = (Boolean) awsMgrPluggin.getConfig().get("destructiveMode");

        if (destructiveMode == null) {
            destructiveMode = false;
            saveDestructiveMode(destructiveMode);
        }
    }

    private void loadRegion() {
        region = (String) awsMgrPluggin.getConfig().get("region");

        if (region == null) {
            region = "us-west-2";
            saveRegion(region);
        }
    }

    private void savePlayersState(HashMap<String, Player> players) {
        awsMgrPluggin.getConfig().set("playerMap", players);
    }

    private void saveAwsAvatarsState(HashMap<String, AwsAvatar> awsAvatars) {
        awsMgrPluggin.getConfig().set("awsAvatarMap", awsAvatars);
    }

    private void saveUniqueInstanceNameState(List<String> uniqueInstanceNames) {
        awsMgrPluggin.getConfig().set("uniqueInstanceNames", uniqueInstanceNames);
    }

    private void saveDestructiveMode(Boolean destructiveMode) {
        awsMgrPluggin.getConfig().set("destructiveMode", destructiveMode);
    }

    private void saveRegion(String region) {
        awsMgrPluggin.getConfig().set("region", region);
    }


    public void addPlayer(Player player) {
        playerMap.put(player.getName(), player);
        savePlayersState(playerMap);
    }

    public Player getPlayer(String playerName) {
        return playerMap.get(playerName);
    }

    public boolean playersInServer() {
        return playerMap.size() > 0;
    }

    public boolean awsAvatarsInServer() {
        return awsAvatarMap.size() > 0;
    }

    public void removeAwsPlayer(Player player) {
        clearPlayerAvatars(player.getName());
        playerMap.remove(player.getName());

        if (playerMap.size() == 0) {
            clearAllAvatars();
        }

        savePlayersState(playerMap);
    }

    public String getDestructiveMode() {
        if (destructiveMode == true) {
            return "destructive";
        }
        else {
            return "sane";
        }
    }

    public void printInfo() {
        Bukkit.broadcastMessage(generatePrintInfo());
    }

    public String generatePrintInfo() {
        ArrayList<String> info = new ArrayList<>();

        info.add("AWS Account: " + AwsUtil.getAwsAccount());
        info.add("Region: " + region);
        info.add("Mode: " + getDestructiveMode());
        info.add("DevOps Engineers: " + playerMap.size());

        for (Map.Entry<String, Player> entry : playerMap.entrySet()) {
            Player player = entry.getValue();
            info.add(" - " + player.getName());
        }

        info.add("AWS Instances: " + awsAvatarMap.size());

        for (Map.Entry<String, AwsAvatar> entry : awsAvatarMap.entrySet()) {
            AwsAvatar awsAvatar = entry.getValue();
            info.add(" - " + awsAvatar.getMinecraftEntity().getCustomName());
        }

        return StringUtils.join(info.toArray(), '\n');
    }

    public void fetchEC2AndSpawnAwsAvatars(String playerName, String ec2NameFilter, String entityType) throws Exception {
        Player player = playerMap.get(playerName);

        int beforeFetchInstanceCount = awsAvatarMap.size();

        List<Instance> instances = AwsUtil.callAwsAndFilterEC2Instances(ec2NameFilter, region);
        populateUniqueInstanceNames(instances);

        for (Instance instance : instances) {
            if (avatarExists(instance)) {
                // avatar already added by previous fetch command
                continue;
            }

            if (!AwsUtil.isEC2InstanceRunning(instance)) {
                // instance must be running
                continue;
            }

            String tagText = AwsUtil.createTagText(instance);
            Entity entity = MinecraftUtil.spawnEntityFromText(entityType, tagText, player);
            AwsAvatar awsAvatar = new AwsAvatar(entity, instance, playerName);
            awsAvatarMap.put(instance.getInstanceId(), awsAvatar);
        }

        int afterFetchInstanceCount = awsAvatarMap.size();

        if (afterFetchInstanceCount == beforeFetchInstanceCount) {
            Bukkit.broadcastMessage("Fetch results: No instances found");
        }

        saveAwsAvatarsState(awsAvatarMap);
    }

    public void fetchEc2InstancesAndMerge() throws Exception {
        List<Instance> instances = AwsUtil.getEC2Instances(uniqueInstanceNames, region);
        Map<String, Instance> instanceMap = new HashMap<>();

        for (Instance instance : instances) {
            instanceMap.put(instance.getInstanceId(), instance);
        }

        removeAwsAvatarsByTerminatedInstances(instanceMap);
        spawnNewAvatarsByUniqueInstanceName(instanceMap);
        saveAwsAvatarsState(awsAvatarMap);
    }

    private void removeAwsAvatarsByTerminatedInstances(Map<String, Instance> instanceMap) {
        Iterator<Map.Entry<String, AwsAvatar>> iterator = awsAvatarMap.entrySet().iterator();

        while (iterator.hasNext()) {
            AwsAvatar awsAvatar = iterator.next().getValue();
            Instance possibleInstance = instanceMap.get(awsAvatar.getAwsInsance().getInstanceId());

            if (!isEC2InstanceRunning(possibleInstance)) {
                removeAvatar(iterator, awsAvatar);
            }
        }
    }

    public void spawnNewAvatarsByUniqueInstanceName(Map<String, Instance> instanceMap) throws Exception {
        for (Map.Entry<String, Instance> instanceEntry : instanceMap.entrySet()) {
            Instance instance = instanceEntry.getValue();
            // todo: figure out good strategy to not hardcode which tag to search
            String instanceName = AwsUtil.getValueFromTags(instance.getTags(), "Name");

            if (!uniqueInstanceNames.contains(instanceName)) {
                // don't want to add names that don't match unique group
                continue;
            }

            if (avatarExists(instance)) {
                // don't want to add existing avatars
                continue;
            }

            if (!AwsUtil.isEC2InstanceRunning(instance)) {
                // instance must be running
                continue;
            }

            String tagText = AwsUtil.createTagText(instance);
            Map.Entry<String, Player> playerEntry = playerMap.entrySet().iterator().next(); // doesn't really matter what player to use, just use one at random
            Player player = playerEntry.getValue();

            Entity entity = MinecraftUtil.spawnEntityFromText("random", tagText, player);
            AwsAvatar awsAvatar = new AwsAvatar(entity, instance, player.getName());
            awsAvatarMap.put(instance.getInstanceId(), awsAvatar);
            Bukkit.broadcastMessage("Instance " + instanceName + " - " + instance.getInstanceId() + " added!");
        }
    }

    public void handleEntityDeath(LivingEntity livingEntity) {
        AwsAvatar awsAvatar = getAwsAvatar(livingEntity.getUniqueId());

        if (awsAvatar == null) {
            return;
        }

        String instanceId = awsAvatar.getAwsInsance().getInstanceId();

        if (destructiveMode) {
            AwsUtil.terminateEC2Instance(instanceId, awsAvatar, region);
        } else {
            AwsUtil.fakeTerminateEC2Instance(awsAvatar);
        }

        awsAvatarMap.remove(instanceId);

        saveAwsAvatarsState(awsAvatarMap);
    }

    private void populateUniqueInstanceNames(List<Instance> instances) {
        for (Instance instance : instances) {
            // todo: figure out good strategy to not hardcode which tag to search
            String instanceName = AwsUtil.getValueFromTags(instance.getTags(), "Name");

            if (!uniqueInstanceNames.contains(instanceName)) {
                uniqueInstanceNames.add(instanceName);
            }
        }

        saveUniqueInstanceNameState(uniqueInstanceNames);
    }

    private boolean avatarExists(Instance instance) {
        AwsAvatar awsAvatar = getAwsAvatar(instance);

        if (awsAvatar == null) {
            return false;
        }

        return true;
    }

    public AwsAvatar getAwsAvatar(Instance instance) {
        for (Map.Entry<String, AwsAvatar> entry : awsAvatarMap.entrySet()) {
            AwsAvatar awsAvatar = entry.getValue();
            Instance existingInstance = awsAvatar.getAwsInsance();
            Boolean instancesAreEqual = existingInstance.getInstanceId().equals(instance.getInstanceId());

            if (instancesAreEqual) {
                return awsAvatar;
            }
        }

        return null;
    }


    public AwsAvatar getAwsAvatar(UUID minecraftEntityUuid) {
        for (Map.Entry<String, AwsAvatar> entry : awsAvatarMap.entrySet()) {
            AwsAvatar awsAvatar = entry.getValue();

            if (awsAvatar.getMinecraftEntity().getUniqueId().equals(minecraftEntityUuid)) {
                return awsAvatar;
            }
        }

        return null;
    }

    public void clearAllAvatars() {
        Iterator<Map.Entry<String, AwsAvatar>> iterator = awsAvatarMap.entrySet().iterator();

        while (iterator.hasNext()) {
            AwsAvatar awsAvatar = iterator.next().getValue();
            removeAvatar(iterator, awsAvatar);
        }

        saveAwsAvatarsState(awsAvatarMap);
    }

    public void clearPlayerAvatars(String playerName) {
        Iterator<Map.Entry<String, AwsAvatar>> iterator = awsAvatarMap.entrySet().iterator();

        while (iterator.hasNext()) {
            AwsAvatar awsAvatar = iterator.next().getValue();

            if (!awsAvatar.getFetchedByPlayerName().equalsIgnoreCase(playerName)) {
                // don't clear avatar that wasn't fetched by player
                continue;
            }

            removeAvatar(iterator, awsAvatar);
        }

        saveAwsAvatarsState(awsAvatarMap);
        uniqueInstanceNames.clear();
        saveUniqueInstanceNameState(uniqueInstanceNames);
    }

    private void removeAvatar(Iterator<Map.Entry<String, AwsAvatar>> iterator, AwsAvatar awsAvatar) {
        thunderRemove(awsAvatar);
        Bukkit.broadcastMessage("Instance " + awsAvatar.getMinecraftEntity().getCustomName() + " removed!");
        iterator.remove();
    }

    private void thunderRemove(AwsAvatar awsAvatar) {
        UUID avatarUuid = awsAvatar.getMinecraftEntity().getUniqueId();
        Entity currentEntity = Bukkit.getEntity(avatarUuid); // need fresh copy of entity from server to get its current location

        if (currentEntity == null) {
            // if killed by splash damage before thread has time to execute, entity may already be dead and null
            // note: this may no longer be an issue now that entity death logic is handled during the death listener is triggered
            return;
        }

        currentEntity.getLocation();
        currentEntity.getWorld().strikeLightningEffect(currentEntity.getLocation());
        currentEntity.remove();
    }

    public void toggleMode() {
        if (destructiveMode == true) {
            destructiveMode = false;
        } else {
            destructiveMode = true;
        }

        saveDestructiveMode(destructiveMode);
    }

    public void setRegion(String region) throws Exception {
        try {
            Regions.fromName(region); // validate string input
            saveRegion(region);
        }
        catch (Exception e) {
            throw new Exception("Region " + region + " not found");
        }
    }

    public void addSQSReceiverToWorld(String playerName, String queueName, String entityType) {
        MinecraftUtil.spawnSignWherePlayerLooking(findPlayerInWorld(playerName));
    }

    public void addSQSSenderToWorld(String playerName, String queueName, String entityType) {
        MinecraftUtil.spawnSignWherePlayerLooking(findPlayerInWorld(playerName));
    }

    private Player findPlayerInWorld(String playerName) {
        World world = awsMgrPluggin.getServer().getWorlds().get(0); // hacky. hopefully server is running only 1 world
        Player player = world.getPlayers().stream().filter(x -> x.getName().equals(playerName)).findFirst().get();

        return player;
    }
}
