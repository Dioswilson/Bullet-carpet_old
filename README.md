##This is a fork(not technically) of carpetmod112 (https://github.com/gnembon/carpetmod112).

# Carpet Mod 
Yes.

## Getting Started
### Setting up your sources
- Clone this repository.
- Run `gradlew setupCarpetmod` in the root project directory.

### Using an IDE
- To use Eclipse, run `gradlew eclipse`, then import the project in Eclipse.
- To use Intellij, run `gradlew idea`, then import the project in Intellij.

## Using the build system
Edit the files in the `src` folder, like you would for a normal project. The only special things you have to do are as follows:
### To generate patch files so they show up in version control
Use `gradlew genPatches`
### To apply patches after pulling
Use `gradlew setupCarpetmod`. It WILL overwrite your local changes to src, so be careful.
### To create a release / patch files
In case you made changes to the local copy of the code in `src`, run `genPatches` to update the project according to your src.
Use `gradlew createRelease`. The release will be a ZIP file containing all modified classes, obfuscated, in the `build/distributions` folder.
### To run the server locally (Windows)
Use `mktest.cmd` to run the modified server with generated patches as a localhost server. It requires `gradlew createRelease` to finish successfully as well as using default paths for your minecraft installation folder.

In case you use different paths, you might need to modify the build script.
This will leave a ready server jar file in your saves folder.

It requires to have 7za installed in your paths

##  Here i will list what i changed:

###Vales changes:
Vales is an external user who i asked permission to use his code, here are things he did:
```/carpet doorSearchOptimization
/carpet doorCheckOptimization
/carpet ironFarmAABBOptimization
/carpet ironGolemsSwim 
/carpet villagerQueue
/carpet villagerQueueLength 
/carpet villagerTickingRate  
/carpet villagersRandomStart
/carpet doorDeregistrationTime 
/log villagerPos
/log villages <dynamic|overworld|nether|end>
/log golems 
/log counter all 
/log portalCaching uncaching 
fixed NullPointerException with /log
readded time and total to /counter
fixed /log items not tracking items that change dimensions
```

###Prometheus addition:
I added prometheus integration(https://github.com/prometheus/client_java) into this for monitoring`/port` to change port.(Pasted license since i had to copy the code)

###Bullet changes:
Here i will list we did:

- Changed some minecraft internal behaviour on player registery, i yeeted the toLowerCase method on it and changed a map into a tree map
- Added "bullet" as a carpet command category
- I also added the following commands:
```
/carpet updateTabEveryGametick
/carpet alwaysSetPlayerIntoSurvival
/carpet commandEnderchest
/carpet commandInventory(coded by slowik)
/carpet scoreboardStats
/carpet blockStateSyncing
```

