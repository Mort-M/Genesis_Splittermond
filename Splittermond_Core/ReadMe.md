This module provides means to access all data for the roleplaying game **Splittermond** and load and save characters. It is used within the application *Genesis* and other applications of the *RPGFramework* project.

**splittermond-core** is accompanied by **splittermond-data**, which contains the licensed data itself.

## Declare dependencies in your project
To use these libraries in your project, you need to add our repository in your project configuration:
```
    <repository>
      <id>rpgframework</id>
      <url>http://repository.rpgframework.de:8081/artifactory/libs-release</url>
    </repository>
```
Now you need to declare dependencies to the following artifacts:
```
<dependency>
   <groupId>org.prelle.rpgframework</groupId>
   <artifactId>splittermond-core</artifactId>
   <version>3.0.0</version>
</dependency>
<dependency>
   <groupId>org.prelle.rpgframework</groupId>
   <artifactId>splittermond-data</artifactId>
   <version>3.19.0</version>
</dependency>
```


## Usage in your code

To load all Splittermond data (skills, spells, etc.) in your application, use the following line:
```
(new SplittermondDataPlugin()).init();
```
To parse an existing character you can use one of the load()-methods in SplittermondCore:
```
try {
  SpliMoCharacter myChar = SplittermondCore.load(new FileInputStream("mychar.xml"));
} catch (IOException e) {
}
```

# Licensing
This code is dual licensed. It can be freely used under the AGPL 3.0 license, but individual licenses for proprietary use are possible.

SPDX-License-Identifier: AGPL-3.0-only OR Unlicensed