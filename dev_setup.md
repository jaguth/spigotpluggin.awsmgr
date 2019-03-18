# End To End developer workstation setup

## Expectations
- You are familiar with AWS
- You can familiarize yourself with Minecraft
- (Dev Optional) You are familiar with Java, Intelli, and Gradle
- You can familiarize yourself with Spigot Server

### What is Spigot Server?
Spigot Server is an open source Minecraft server.  It is one of the most popular servers because of its large community and abundance of its API/SDK support.  AWS Minecraft Manager was written using the Spigot SDK and is dependent on the Spigot Server. 

## Prerequisites
- Install Spigot Server:  https://www.spigotmc.org/wiki/spigot-installation/
- Install Java 8 or higher runtime
- Install AWS CLI (for authentication)
- You have purchased Minecraft Java Edition and verified it works (https://www.minecraft.net)

## Versions used for this project
- Spigot v1.13.2
- Spigot API: 1.13.2-R0.1-SNAPSHOT
- IntelliJ Community 2018.3
- Gradle v4.10
- Java 8

## How to get the Minecraft AWS plugin

### Option 1: Compile it yourself with IntelliJ
1) Run: git clone  https://github.com/jaguth/spigotpluggin.awsmgr
2) Open in IntelliJ Community. Verify gradle has downloaded all dependencies and project builds.
3) Run the gradle task "shadowJar". The fat jar file will be built to build/libs.

### Option 2: Compile it yourself with Gradle
1) Run: git clone  https://github.com/jaguth/spigotpluggin.awsmgr
2) CD into spigotpluggin.awsmgr. Run: ./gradlew build shadowJar. The fat jar file will be built to build/libs.

### Option 3: Just copy it from github
1) Run: curl https://github.com/jaguth/spigotpluggin.awsmgr/tree/master/bin/spigotpluggin.awsmgr-1.0-all.jar --output spigotpluggin.awsmgr-1.0-all.jar

## Start you local Spigot server
1) Copy spigotpluggin.awsmgr-1.0-all.jar to your Spigot plugin directory
2) Make sure you authenticate to AWS before starting the server (https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html).
2) Start Spigot: java -Xms1G -Xmx1G -XX:+UseConcMarkSweepGC -jar spigot-1.13.2.jar
3) Watch for the text "[AwsMgr] Enabled" to appear in the console output. That means it loaded successfully.

Note:  If your auth token expires while your Spigot server is running, then you must reauthenticate and restart the server.  Otherwise your commands will fail.

## Test the plugin
1) Open your Java Minecraft client
2) Connect to your local server (localhost)
3) Issue some /EC2 commands and see if it works!
