package com.jaguth.spigotpluggin.awsmgr;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Instance;
import com.google.common.collect.Lists;
import com.jaguth.spigotpluggin.awsmgr.domain.AwsAvatar;
import com.jaguth.spigotpluggin.awsmgr.domain.GroupInfo;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

import static com.jaguth.spigotpluggin.awsmgr.AwsUtil.isEC2InstanceRunning;

public class AwsMgr {
    private AwsMgrPluggin awsMgrPluggin;
    private HashMap<String, Player> playerMap; // key = playerName
    private HashMap<String, AwsAvatar> awsAvatarMap; // key = instanceId
    private HashMap<String, GroupInfo> instanceGroups; // key = groupName
    private List<Sign> spawnedSigns;
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
        loadSpawnedSigns();
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
        instanceGroups = (HashMap<String, GroupInfo>) awsMgrPluggin.getConfig().get("instanceGroups");

        if (instanceGroups == null) {
            instanceGroups = new HashMap<>();
            saveInstanceGroups(instanceGroups);
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

    private void loadSpawnedSigns() {
        spawnedSigns = (List<Sign>) awsMgrPluggin.getConfig().get("spawnedSigns");

        if (spawnedSigns == null) {
            spawnedSigns = new ArrayList<>();
            saveSpawnedSigns(spawnedSigns);
        }
    }

    private void savePlayersState(HashMap<String, Player> players) {
        awsMgrPluggin.getConfig().set("playerMap", players);
    }

    private void saveAwsAvatarsState(HashMap<String, AwsAvatar> awsAvatars) {
        awsMgrPluggin.getConfig().set("awsAvatarMap", awsAvatars);
    }

    private void saveInstanceGroups(HashMap<String, GroupInfo> instanceGroups) {
        awsMgrPluggin.getConfig().set("instanceGroups", instanceGroups);
    }

    private void saveDestructiveMode(Boolean destructiveMode) {
        awsMgrPluggin.getConfig().set("destructiveMode", destructiveMode);
    }

    private void saveRegion(String region) {
        awsMgrPluggin.getConfig().set("region", region);
    }

    private void saveSpawnedSigns(List<Sign> spawnedSigns) {
        awsMgrPluggin.getConfig().set("spawnedSigns", spawnedSigns);
    }

    public void addPlayer(Player player) {
        playerMap.put(player.getName(), player);
        savePlayersState(playerMap);
    }

    public Player getPlayer(String playerName) {
        return playerMap.get(playerName);
    }

    public List<Sign> getSpawnedSigns() {
        return spawnedSigns;
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
        } else {
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
        populateInstanceGroups(instances, entityType, player);

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
            String instanceName = AwsUtil.getValueFromTags(instance.getTags(), "Name");
            GroupInfo instanceGroup = instanceGroups.get(instanceName);
            Entity entity = MinecraftUtil.spawnEntityNextToSign(entityType, tagText, instanceGroup.getSpawnedSign());
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
        List<Instance> instances = AwsUtil.getEC2Instances(instanceGroups, region);
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

            if (!instanceGroups.containsKey(instanceName)) {
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

            GroupInfo groupInfo = instanceGroups.get(instanceName);

            Entity entity = MinecraftUtil.spawnEntityNextToSign(groupInfo.getEntityType(), tagText, groupInfo.getSpawnedSign());
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

    private static String SPAWNER_EC2 = "spawner:ec2";

    private void populateInstanceGroups(List<Instance> instances, String entityType, Player player) {
        ArrayList<String> listOfInstanceNamesToAdd = new ArrayList<>();

        for (Instance instance : instances) {
            // todo: figure out good strategy to not hardcode which tag to search
            String instanceName = AwsUtil.getValueFromTags(instance.getTags(), "Name");

            if (!instanceGroups.containsKey(instanceName) && !listOfInstanceNamesToAdd.contains(instanceName)) {
                listOfInstanceNamesToAdd.add(instanceName);
            }
        }

        ArrayList<String> signTextList = Lists.newArrayList(listOfInstanceNamesToAdd);
        signTextList.add(0, SPAWNER_EC2);
        String[] signText = listToSignText(signTextList);
        Sign spawnedSign = MinecraftUtil.spawnSignWherePlayerLooking(player, signText);

        for (String instanceName : listOfInstanceNamesToAdd) {
            instanceGroups.put(instanceName, new GroupInfo(entityType, spawnedSign));
        }

        saveInstanceGroups(instanceGroups);

        spawnedSigns.add(spawnedSign);
        saveSpawnedSigns(spawnedSigns);
    }

    public void destroyInstanceGroupsThatBelongingToDestroyedSign(Sign destroyedSpawnedSign) {
        // remove instance group(s)
        Iterator<Map.Entry<String, GroupInfo>> instanceGroupIterator = instanceGroups.entrySet().iterator();

        while (instanceGroupIterator.hasNext()) {
            GroupInfo groupInfo = instanceGroupIterator.next().getValue();

            if (Arrays.equals(groupInfo.getSpawnedSign().getLines(), destroyedSpawnedSign.getLines())) {
                instanceGroupIterator.remove();
            }
        }

        saveInstanceGroups(instanceGroups);

        // remove avatars(s)
        for (String instanceName : destroyedSpawnedSign.getLines()) {
            if (instanceName.startsWith("spawner")) {
                // first line is just spawner marker
                continue;
            }

            removeAvatarsBeloningToInstance(instanceName);
        }

        // remove spawned sign
        Iterator<Sign> spawnedSignIterator = spawnedSigns.iterator();

        while (spawnedSignIterator.hasNext()) {
            Sign spawnedSign = spawnedSignIterator.next();

            if (Arrays.equals(spawnedSign.getLines(), destroyedSpawnedSign.getLines())) {
                spawnedSignIterator.remove();
            }
        }

        saveSpawnedSigns(spawnedSigns);
    }


    private void removeAvatarsBeloningToInstance(String instanceName) {
        Iterator<Map.Entry<String, AwsAvatar>> avatarIterator = awsAvatarMap.entrySet().iterator();

        while (avatarIterator.hasNext()) {
            AwsAvatar awsAvatar = avatarIterator.next().getValue();
            String name = AwsUtil.getValueFromTags(awsAvatar.getAwsInsance().getTags(), "Name");

            if (name != null && name.equalsIgnoreCase(instanceName)) {
                removeAvatar(avatarIterator, awsAvatar);
            }
        }

        saveAwsAvatarsState(awsAvatarMap);
    }

    public static String[] listToSignText(List<String> textList) {
        final int MAX_LINES_FOR_SIGN = 4;

        int lineSize = MAX_LINES_FOR_SIGN;

        if (textList.size() < MAX_LINES_FOR_SIGN) {
            lineSize = textList.size();
        }

        String[] signText = new String[lineSize];

        int currentIndex = 0;

        for (String text : textList) {
            if (currentIndex <= 3) {
                signText[currentIndex] = text;
            }
            else {
                break;
            }

            currentIndex++;
        }

        return signText;
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
        instanceGroups.clear();
        saveInstanceGroups(instanceGroups);
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
        } catch (Exception e) {
            throw new Exception("Region " + region + " not found");
        }
    }
}
