# Minecraft AWS Manager
Manage AWS resources in Minecraft!

## So What Is It?
Right now, its pretty much a virtual death simulator for EC2 instances. Toy with their lives!

## How Do I Get This Working?
[Read dev_setup.md to get started](./dev_setup.md)

## Supported Features

### EC2
EC2 instance management is supported.  Below are commands and how to use them.

**/EC2 fetch [contained_instance_name] [entity_type]**

Example: */EC2 fetch ReaderService cow*. If you have an auto scaling group with 5 instances, and they contain the text "ReaderService" in the tag "Name", then it will match 5 cows to materialize.

The virtual animals that materialize are called "Avatars".  They are composite entities and contain both Minecraft and AWS attributes.

Notes:
- AWS Manager keeps your server in sync with AWS. So if an instance is terminated in AWS, then your fetched avatar will be terminated by a lightning bolt in Minecraft.  And if you kill an avatar in Minecraft, then it will be terminated in AWS. With great power comes great responsibility.
- This works great with high availability. If an instance is killed and is backed by an auto scaling group, then the auto scaling group will try to spin up a new instance to replaced the terminated instance.  Once a new instance is brought online in AWS, a new avatar will materialize in your virtual world.
- The instance name matcher is currently hard-coded to read from the tag "Name". If you don't tag your EC2 instances with some text in the tag "Name", then this won't work.
- Fetching by name uses a "contains" clause.  So if you fetch for the name "Pitt" and you name your instances "BradPittReader", then it will fetch those instances. But be careful! If you also have instances named "BradPittWriter", then it will fetch those too! 
- The avatar(s) will all have unique names with the format "<instance_name> - <instance_id>".


**/EC2 clear**

This will remove all instances that *you* have fetched. If another user has joined your server and fetched their own instances, it will not remove their instances. 

**/EC2 info**

This will display the AWS Account the server is connected to, the region, how many users (devops engineers) are on the server, how many instances are currently fetched. 

**/EC2 mode**

This toggles the mode.  Their are two modes:
1) Destructive:  An avatar killed in Minecraft will terminate the EC2 instance in AWS.
2) Sane: An avatar killed in Minecraft will not terminate the EC2 instance.

By default, the mode is set to "Sane" as a safety-measure.

**/EC2 region [region]**

This changes the region to fetch from. This will also clear all avatars from the world; this plugin does not support managing resources in different regions at the same time.  

#### Supported Animal Entities
The following animal entities are currently supported for EC2. Other animal entities may be added in the future (like fish if you wanted to make some kind of EC2 aquarium).
- Bat
- Cat
- Chicken
- Cod
- Cow
- Donkey
- Dolphin
- Fish
- Fox
- Horse
- Llama
- Mule
- Ocelot
- Panda
- Parrot
- Pig
- PolarBear
- PufferFish
- Rabbit
- Salmon
- SilverFish
- Sheep
- Squid
- TropicalFish
- Turtle
- Wolf

Supplying "random" will choose an animal at random.

### Collaboration
Other users can join the server and also commands. This is built to be multiplayer, just like Minecraft!
