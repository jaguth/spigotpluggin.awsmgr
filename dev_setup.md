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
- Spigot v1.14.3
- IntelliJ Community 2019.1
- Gradle v4.10
- Java 8

## How compile and use Minecraft AWS plugin
1. git clone  https://github.com/jaguth/spigotpluggin.awsmgr
2. Run the gradle tasks:
    * downloadBuildTools
    * buildTheBuildTools
    * installThePlugin
3) Start the server: java -Xms512M -Xmx1G -XX:+UseConcMarkSweepGC -jar buildtools/spigot-1.14.3.jar

To stop the server, in the server console enter the command: stop

## Test the plugin
1) Open your Java Minecraft client
2) Connect to your local server (localhost)
3) Issue some /EC2 commands and see if it works!
