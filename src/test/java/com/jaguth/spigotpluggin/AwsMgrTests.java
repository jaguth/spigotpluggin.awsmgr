package com.jaguth.spigotpluggin;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;
import com.jaguth.spigotpluggin.awsmgr.AwsMgr;
import com.jaguth.spigotpluggin.awsmgr.AwsMgrPluggin;
import com.jaguth.spigotpluggin.awsmgr.domain.AwsAvatar;
import org.bukkit.entity.Animals;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AwsMgrTests {
    @Test
    public void getAwsAvatar_byMinecraftEntity_ShouldReturnAvatar() {
        // setup existing instance
        String existingInstanceName = "name_1";
        String existingInstanceId = "id_1";

        Instance existingInstance = new Instance();
        existingInstance.setInstanceId(existingInstanceId);
        List<Tag> existingInstanceTags = new ArrayList<>();
        existingInstanceTags.add(new Tag("Name", existingInstanceName));
        existingInstance.setTags(existingInstanceTags);

        // setup animal
        Animals mockAnimal = mock(TestAnimal.class);
        UUID animalUUID = UUID.randomUUID();
        when(mockAnimal.getUniqueId()).thenReturn(animalUUID);

        // build awsAvatarMap
        HashMap<String, AwsAvatar> awsAvatarMap = new HashMap<>();
        AwsAvatar awsAvatar = new AwsAvatar(mockAnimal, existingInstance, "moo");
        awsAvatarMap.put(existingInstance.getInstanceId(), awsAvatar);

        // build and mock stateful collections
        AwsMgrPluggin awsMgrPlugginMock = mock(AwsMgrPluggin.class, RETURNS_DEEP_STUBS);
        when(awsMgrPlugginMock.getConfig().get("playerMap")).thenReturn(null);
        when(awsMgrPlugginMock.getConfig().get("awsAvatarMap")).thenReturn(awsAvatarMap);
        when(awsMgrPlugginMock.getConfig().get("uniqueInstanceNames")).thenReturn(null);

        AwsMgr awsMgr = new AwsMgr(awsMgrPlugginMock);
        AwsAvatar responseAwsAvatar = awsMgr.getAwsAvatar(mockAnimal.getUniqueId());

        assertNotNull(responseAwsAvatar);
    }

    @Test
    public void getAwsAvatar_byEC2Instance_ShouldReturnAvatar() {
        // setup new instance
        String newInstanceId = "id_2"; // different instance id

        Instance newInstance = new Instance();
        newInstance.setInstanceId(newInstanceId);

        // setup existing instance
        String existingInstanceName = "name_1";
        String existingInstanceId = "id_1";

        Instance existingInstance = new Instance();
        existingInstance.setInstanceId(existingInstanceId);
        List<Tag> existingInstanceTags = new ArrayList<>();
        existingInstanceTags.add(new Tag("Name", existingInstanceName));
        existingInstance.setTags(existingInstanceTags);

        // setup animal
        Animals mockAnimal = mock(TestAnimal.class);
        UUID animalUUID = UUID.randomUUID();
        when(mockAnimal.getUniqueId()).thenReturn(animalUUID);

        // build awsAvatarMap
        HashMap<String, AwsAvatar> awsAvatarMap = new HashMap<>();
        AwsAvatar awsAvatar = new AwsAvatar(mockAnimal, existingInstance, "moo");
        awsAvatarMap.put(existingInstance.getInstanceId(), awsAvatar);

        // build and mock stateful collections
        AwsMgrPluggin awsMgrPlugginMock = mock(AwsMgrPluggin.class, RETURNS_DEEP_STUBS);
        when(awsMgrPlugginMock.getConfig().get("playerMap")).thenReturn(null);
        when(awsMgrPlugginMock.getConfig().get("awsAvatarMap")).thenReturn(awsAvatarMap);
        when(awsMgrPlugginMock.getConfig().get("uniqueInstanceNames")).thenReturn(null);

        AwsMgr awsMgr = new AwsMgr(awsMgrPlugginMock);
        AwsAvatar responseAwsAvatar = awsMgr.getAwsAvatar(newInstance);

        assertNull(responseAwsAvatar);
    }

    @Test
    public void getAwsAvatar_byEC2Instance_ShouldNotReturnAvatar() {
        // setup existing instance
        String existingInstanceName = "name_1";
        String existingInstanceId = "id_1";

        Instance existingInstance = new Instance();
        existingInstance.setInstanceId(existingInstanceId);
        List<Tag> existingInstanceTags = new ArrayList<>();
        existingInstanceTags.add(new Tag("Name", existingInstanceName));
        existingInstance.setTags(existingInstanceTags);

        // setup animal
        Animals mockAnimal = mock(TestAnimal.class);
        UUID animalUUID = UUID.randomUUID();
        when(mockAnimal.getUniqueId()).thenReturn(animalUUID);

        // build awsAvatarMap
        HashMap<String, AwsAvatar> awsAvatarMap = new HashMap<>();
        AwsAvatar awsAvatar = new AwsAvatar(mockAnimal, existingInstance, "moo");
        awsAvatarMap.put(existingInstance.getInstanceId(), awsAvatar);

        // build and mock stateful collections
        AwsMgrPluggin awsMgrPlugginMock = mock(AwsMgrPluggin.class, RETURNS_DEEP_STUBS);
        when(awsMgrPlugginMock.getConfig().get("playerMap")).thenReturn(null);
        when(awsMgrPlugginMock.getConfig().get("awsAvatarMap")).thenReturn(awsAvatarMap);
        when(awsMgrPlugginMock.getConfig().get("uniqueInstanceNames")).thenReturn(null);

        AwsMgr awsMgr = new AwsMgr(awsMgrPlugginMock);
        AwsAvatar responseAwsAvatar = awsMgr.getAwsAvatar(existingInstance);

        assertNotNull(responseAwsAvatar);
    }

    @Test
    public void blah() {
        String string1 = "i-0969ae9073d481c54";
        String string2 = "i-0969ae9073d481c54";

        assertEquals(string1, string2);

        assertTrue(string1 == string2);

        System.out.println(string1 == string2);
    }

    @Test
    public void listToSignTextTest1() {
        List<String> inputList = new ArrayList<>();
        inputList.add("first line");
        inputList.add("second line");
        inputList.add("third line");
        inputList.add("last line");
        inputList.add("this line won't be output since a sign holds a maximum of 4 lines");

        String[] outputArray = AwsMgr.listToSignText(inputList);
        int expectedLineCount = 4;

        Assert.assertEquals(expectedLineCount, outputArray.length);
        Assert.assertEquals("first line", outputArray[0]);
        Assert.assertEquals("second line", outputArray[1]);
        Assert.assertEquals("third line", outputArray[2]);
        Assert.assertEquals("last line", outputArray[3]);
    }

    @Test
    public void listToSignTextTest2() {
        List<String> inputList = new ArrayList<>();
        inputList.add("first line");
        inputList.add("second line");
        inputList.add("third line");

        String[] outputArray = AwsMgr.listToSignText(inputList);
        int expectedLineCount = 3;

        Assert.assertEquals(expectedLineCount, outputArray.length);
        Assert.assertEquals("first line", outputArray[0]);
        Assert.assertEquals("second line", outputArray[1]);
        Assert.assertEquals("third line", outputArray[2]);
    }
}
