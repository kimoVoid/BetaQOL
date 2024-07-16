# Beta QOL Mod
This is a quality of life mod for Beta 1.7.3 using Ornithe!
<br>It aims to keep the game faithful to vanilla while also fixing some of the inconveniences that most people face when playing this version.
<br>
<br>Check out the [OrnitheMC website](https://ornithemc.net/) to grab a copy of the installer! You can also make MultiMC/Prism Launcher instances.
<br>
### Current list of features/fixes
- Fully configurable
  - [Mod Menu](https://modrinth.com/mod/modmenu-ornithe) support
  - Uses AxolotlClient config UI
- Auth fix (use current auth server)
- Skin/cape patch & 1.8 layers backport (ported from [MojangFix](https://modrinth.com/mod/mojangfix))
- Resources patch (betacraft proxy)
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
- Added chat history
  - Can be navigated with up arrow and down arrow keys just like in modern MC
- Tablist backport
  - Uses custom server-to-client packets, not required by vanilla clients
  - Server builds with this will be available soon!
- Updated multiplayer screen
  - Ported from [MojangFix](https://modrinth.com/mod/mojangfix)
  - Ability to save servers
  - IP address can be hidden
  - Servers can be pinged
    - Needs a server-side mod
    - Uses BetaTweaks/mcb1.8-1.6 format
    - Pinging can be turned off if found problematic
- Crash slab fix