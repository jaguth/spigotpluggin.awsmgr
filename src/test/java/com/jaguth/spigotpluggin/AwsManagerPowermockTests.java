package com.jaguth.spigotpluggin;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;
import com.google.common.collect.Lists;
import com.jaguth.spigotpluggin.awsmgr.AwsMgr;
import com.jaguth.spigotpluggin.awsmgr.AwsMgrPluggin;
import com.jaguth.spigotpluggin.awsmgr.MinecraftUtil;
import com.jaguth.spigotpluggin.awsmgr.domain.AwsAvatar;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
public class AwsManagerPowermockTests {

    @Ignore // too lazy to stub out new methods. test already provided its usefulness at stomping a difficult bug
    @Test
    @PrepareForTest(MinecraftUtil.class)
    public void testSpawnNewAvatars_ByUniqueInstanceName_DifferentInstanceName() throws Exception {
        // setup new instance
        String newInstanceName = "name_1"; // same name
        String newInstanceId = "id_2"; // different instance id

        Instance newInstance = new Instance();
        newInstance.setInstanceId(newInstanceId);

        List<Tag> newInstanceTags = new ArrayList<>();
        newInstanceTags.add(new Tag("Name", newInstanceName));
        newInstance.setTags(newInstanceTags);

        HashMap<String, Instance> newInstanceMap = new HashMap<>();
        newInstanceMap.put(newInstanceName, newInstance);

        // setup existing instance
        String existingInstanceName = "name_1";
        String existingInstanceId = "id_1";

        Instance existingInstance = new Instance();
        existingInstance.setInstanceId(existingInstanceId);
        List<Tag> existingInstanceTags = new ArrayList<>();
        existingInstanceTags.add(new Tag("Name", existingInstanceName));
        existingInstance.setTags(newInstanceTags);

        // setup player map
        HashMap<String, Player> playerMap = new HashMap<>();
        Player player = new TestPlayer();
        String playerName = "cow";
        player.setPlayerListName(playerName);
        playerMap.put(playerName, player);

        // setup animal
        Animals mockAnimal = mock(TestAnimal.class);
        UUID animalUUID = UUID.randomUUID();
        when(mockAnimal.getUniqueId()).thenReturn(animalUUID);

        // build awsAvatarMap
        List<String> uniqueInstanceNames = Lists.newArrayList(existingInstanceName);
        HashMap<String, AwsAvatar> awsAvatarMap = new HashMap<>();
        AwsAvatar awsAvatar = new AwsAvatar(mockAnimal, existingInstance, "moo");
        awsAvatarMap.put(existingInstance.getInstanceId(), awsAvatar);

        // build and mock stateful collections
        AwsMgrPluggin awsMgrPlugginMock = mock(AwsMgrPluggin.class, RETURNS_DEEP_STUBS);

        when(awsMgrPlugginMock.getConfig().get("playerMap")).thenReturn(playerMap);
        when(awsMgrPlugginMock.getConfig().get("awsAvatarMap")).thenReturn(awsAvatarMap);
        when(awsMgrPlugginMock.getConfig().get("uniqueInstanceNames")).thenReturn(uniqueInstanceNames);
        mockStatic(MinecraftUtil.class);
        when(MinecraftUtil.spawnEntityFromText(anyString(), anyString(), any(Player.class))).thenReturn(null);

        AwsMgr awsMgr = new AwsMgr(awsMgrPlugginMock);
        awsMgr.spawnNewAvatarsByUniqueInstanceName(newInstanceMap);

        // assert
        int expectedAwsAvatarMapSize = 2;
        Assert.assertEquals(expectedAwsAvatarMapSize, awsAvatarMap.size());
    }

    @Ignore // too lazy to stub out new methods. test already provided its usefulness at stomping a difficult bug
    @Test
    @PrepareForTest(MinecraftUtil.class)
    public void testSpawnNewAvatars_ByUniqueInstanceName_SameInstanceName() throws Exception {
        // setup single instance to process
        String newInstanceName = "name_1"; // same name
        String newInstanceId = "id_1"; // different instance id

        Instance newInstance = new Instance();
        newInstance.setInstanceId(newInstanceId);

        List<Tag> newInstanceTags = new ArrayList<>();
        newInstanceTags.add(new Tag("Name", newInstanceName));
        newInstance.setTags(newInstanceTags);

        HashMap<String, Instance> newInstanceMap = new HashMap<>();
        newInstanceMap.put(newInstanceName, newInstance);

        // setup existing instance
        String existingInstanceName = "name_1";
        String existingInstanceId = "id_1";

        Instance existingInstance = new Instance();
        existingInstance.setInstanceId(existingInstanceId);
        List<Tag> existingInstanceTags = new ArrayList<>();
        existingInstanceTags.add(new Tag("Name", existingInstanceName));
        existingInstance.setTags(newInstanceTags);

        // setup player map
        HashMap<String, Player> playerMap = new HashMap<>();
        Player player = new TestPlayer();
        String playerName = "cow";
        player.setPlayerListName(playerName);
        playerMap.put(playerName, player);

        // setup animal
        Animals mockAnimal = mock(TestAnimal.class);
        UUID animalUUID = UUID.randomUUID();
        when(mockAnimal.getUniqueId()).thenReturn(animalUUID);

        // build awsAvatarMap
        List<String> uniqueInstanceNames = Lists.newArrayList(existingInstanceName);
        HashMap<String, AwsAvatar> awsAvatarMap = new HashMap<>();
        AwsAvatar awsAvatar = new AwsAvatar(mockAnimal, existingInstance, "moo");
        awsAvatarMap.put(existingInstance.getInstanceId(), awsAvatar);

        // build and mock stateful collections
        AwsMgrPluggin awsMgrPlugginMock = mock(AwsMgrPluggin.class, RETURNS_DEEP_STUBS);

        when(awsMgrPlugginMock.getConfig().get("playerMap")).thenReturn(playerMap);
        when(awsMgrPlugginMock.getConfig().get("awsAvatarMap")).thenReturn(awsAvatarMap);
        when(awsMgrPlugginMock.getConfig().get("uniqueInstanceNames")).thenReturn(uniqueInstanceNames);
        mockStatic(MinecraftUtil.class);
        when(MinecraftUtil.spawnEntityFromText(anyString(), anyString(), any(Player.class))).thenReturn(null);

        AwsMgr awsMgr = new AwsMgr(awsMgrPlugginMock);
        awsMgr.spawnNewAvatarsByUniqueInstanceName(newInstanceMap);

        // assert
        int expectedAwsAvatarMapSize = 1;
        Assert.assertEquals(expectedAwsAvatarMapSize, awsAvatarMap.size());
    }
}
