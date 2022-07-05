# BuildTools - Master
Note: All credit for the BuildTools functionality goes to SpigotMC.
Note: All credit for the JDK's goes to AdoptOpenJDK.

Spigot BuildTools Wiki: https://www.spigotmc.org/wiki/buildtools/

Spigot BuildTools Download: https://www.spigotmc.org/wiki/buildtools/#running-buildtools

### What does this program do?
'BuildTools - Master' is a program dedicated to optimise developer's time when handling multiple versions of NMS (net.minecraft.server) or even the Spigot-API, the program runs through versions of Spigot and provides an automatic process to call each Spigot Version using BuildTools. This process is automated using AdoptOpenJDK versions 16 and 1.8, also downloaded to reduce external bugs such as CACERTS errors, etc. etc.

### How do I run this program?
Running this is as simple as running BuildTools itself

    java -jar BuildTools-Master-1.0-SNAPSHOT.jar

The program will loop through 1.17->1.8, downloading BuildTools and processing each version, as of BuildTools - Master Version '1.0', it is not possible to specify which versions you'd like to have processed, but I am open to Pull-Requests.

### Important Information
This project will download BuildTools, JDK 17, JDK 16 and JDK 8 from their respective download links. I do not own any of these projects and am therefore not liable to assist in bugs which happen with them. I do not claim ownership of any of these projects and I am willing to remove this project if it breaks any form of copyright or usage policy.
