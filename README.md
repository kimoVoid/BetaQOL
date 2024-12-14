![icon.png](client%2Fsrc%2Fmain%2Fresources%2Fassets%2Fbetaqol%2Ficon.png)
# Beta QOL Mod
This is a quality of life mod for Beta 1.7.3 using Ornithe!
<br>It aims to keep the game faithful to vanilla while also fixing some of the inconveniences that most people face when playing this version.
<br>
<br>Check out the [OrnitheMC website](https://ornithemc.net/) to grab a copy of the installer! You can also make MultiMC/Prism Launcher instances.
<br>
## Client features/fixes
- Fully configurable
  - [Mod Menu](https://modrinth.com/mod/modmenu-ornithe) support
  - Uses AxolotlClient config UI
- Auth fix (use current auth server)
- Skin/cape patch & 1.8 layers backport (ported from [MojangFix](https://modrinth.com/mod/mojangfix))
- Resources patch (BetaCraft proxy)
- Mouse position fix on fullscreen
- Quit button re-added
- Enhanced F3 screen
  - Added facing text
  - Removed graph
  - Added Fabric label
  - Added more information
- Connection improvements
  - TCPNoDelay enabled
  - Removed unnecessary delays (about 300ms improvement)
- Packets can be read/sent outside of tick method, making it faster
- KeepAlive packet is sent back to the server, allowing servers to measure your ping
- Ability to click-mine in multiplayer
- [InventoryTweaks](https://modrinth.com/mod/inventorytweaks) port
  - All configs and functions are ported from the babric version
  - Hotkey swapping can also be configured with the Thorough Keybindings mod (not required)
- FOV slider
  - Compatibility with [OptiFine](https://modrinth.com/mod/legacy-optifabric) zoom
- Customizable death screen message (fixes "&e" as well)
- Removed "Unlicensed copy" timer - one less error in the console
- Backported front-facing perspective mode (F5)
- Fixed entity jitter on multiplayer
- Fixed world jitter at long distances (such as the far lands)
- Chat improvements
  - Unlimited chat history option
  - Chat history can be navigated with up arrow and down arrow keys
  - Ability to CTRL+V in chat
- Player list backport
  - Uses custom server-to-client packets, not required by vanilla clients
- Updated multiplayer screen
  - Ported from [MojangFix](https://modrinth.com/mod/mojangfix)
  - Ability to save servers
  - IP address can be hidden
  - Move servers around with SHIFT + (UP / DOWN)
  - Servers can be pinged
    - Needs a server-side mod
    - Uses BetaTweaks/mcb1.8-1.6 format
    - Pinging can be turned off if found problematic
    - Server MOTD can translate colors with the '&' format
- Crash slab fix
- Texture packs button in-game
- Added ability to copy screenshots to clipboard
- Added more debug keybindings (Configurable with Thorough Keybindings mod)
  - `F3 + G` - Show chunk borders (not working with OptiFine at the moment)
  - `F3 + H` - Show item ID, meta and durability in tooltips
  - `F3 + R` - Reload skins
- Fixed leggings not rendering correctly while riding a vehicle
- Added scrolling in chat
  - Ported from [HudTweaks](https://github.com/telvarost/HudTweaks-StationAPI)
- "Always play music" option, similar to Legacy Console Edition

## Server features/fixes
- Auth fix (online-mode support)
- Connection improvements
  - TCPNoDelay enabled
  - Removed unnecessary delays (about 300ms improvement)
- Packets can be read/sent outside of tick method, making it faster
- Server can read KeepAlive packets
  - This can be used to measure player ping
- Tablist support (Beta QOL client mod compatibility)
- Added new commands & simple command interface
  - `/clear` - Clears your inventory
  - `/debug (option)` - Debug info about the server/world
  - `/give (player) (item/id) [amount] [meta]` - Enhanced give command
  - `/ib` - Toggle instant-mining blocks
  - `/ping [player]` - View ping
  - `/setblock <x> <y> <z> <block/id> [meta]` - Set block backport
  - `/status` - TPS and memory information
  - `/summon (mob)` - Summon backport
  - `/tellraw (text)` - Tellraw backport
  - `/toggledownfall` - Toggle weather
- Server can now reply to server list pings
  - Compatible with Beta QOL and vanilla format (b1.8-1.6)
  - Regular colors aren't supported in the MOTD, however, the Beta QOL client mod can translate colors with the '&' format
- Rcon backport
  - You can enable/disable remote console in the server.properties!
- Basic death messages
- Show coordinates on death
  - Can be enabled in server.properties with `death-coordinates=true` 
- Allows one-player sleep
  - Can be enabled in server.properties with `one-player-sleep=true`
  - Doesn't remove nightmares feature for the players who sleep
- Fixes weather timer bugs
  - Rain timer would reset when players sleep, even if it isn't raining
  - The Nether would override the overworld's rain timer

## Developer usage
(Server-side only, for now)

### Adding commands
To add a command, you need to make a class that extends the `Command` class.
<br>Once you're done with your command, you need to register it (at any point!) with `BetaQOL.registerCommand(ICommand)`.

Commands have different exceptions and utility methods, you can check out the `Command` class if you want to learn more!

### Retrieving a player's ping
To get a `ServerPlayerEntity`'s ping, you need to cast them to `IServerPlayerEntity`. Then, you can use the method `IServerPlayerEntity#getPing()`!
<br>Do note that players who aren't able to reply to ping packets will always display 0ms.

### Retrieving the server's TPS and MSPT
Getting the server's ticks per second (and ms per tick) is similar. You need to cast the server to `IMinecraftServer`. From there, you can get the tick times with `IMinecraftServer#getTickTimes()`.

An example on how to get TPS and MSPT is:
```java
private void printTps() {
    double mspt = this.round(2, this.average(((IMinecraftServer)server).getTickTimes()) * 1.0E-6D);
    double tps = this.round(2, 1000.0D / mspt);

    if (tps > 20.0D) {
        tps = 20.0D;
    }
    
    System.out.println(String.format("TPS: %.1f MSPT: %.2f", tps, mspt));
}

private double round(int places, double value) {
    return (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP).doubleValue();
}

private double average(long[] times) {
    long avg = 0L;
    for (long time : times) {
        avg += time;
    }
    return (double)avg / (double)times.length;
}
```