# GreenScripter's Minecraft Bot Swarm Library
A fairly lightweight Java library for creating swarms of minecraft bots around the 100-1000 bot range to play on cracked vanilla and paper servers.  
See the greenscripter.minecraft.atests package for some examples of varying quality.  
In order to actually support a swarm on the larger end you need a fairly powerful server. Making most of the bots not load chunks and instead rely on the shared world of a global WorldPlayHandler is recommended. 
## Handlers
Handlers are the primary means of extension and can listen for packet and tick events either on an entire swarm, or individual bots. 
The following are the standard handlers that need to be registered for a fully featured bot:
* `KeepAlivePlayHandler` responds to keep alive packets, the bare minimum to stay connected.
* `TeleportRequestPlayHandler` responds to teleport requests and updates the bot player's position, required to move the player.
* `PlayerPlayHandler` tracks the bot player's health, experience, food and other base information.
* `DeathPlayHandler` makes the bot instantly respawn on death.
* `InventoryPlayHandler` tracks inventory information and provides convenience methods to interact with various kinds of in game GUIs.
* `WorldPlayHandler` reads and maintains the world state, sharing the world between all bots using the same handler.
* `EntityPlayHandler` add on to the world handler that reads entity related packets and controls the entity world state.
## Data Objects
While there aren't restrictions to prevent other methods of sharing data between handlers, Data objects provide a convenient way to associate the current state of a bot with it's ServerConnection and allow different handlers to access that state.
* `ClientConfigData` storage for the client config information like view distance
* `InventoryData` maintained by the inventory handler, provides access to the player's inventory and convenience methods to modify it.
* `PlayerData` maintained by the player handler, and contains references to a few other data objects.
* `PositionData` maintained by the teleport handler, and is the bare minimum needed to do basic movements.
* `RegistryData` maintained by the ServerConnection login system, used by the world handler to look up dimension ids to determine world heights.
* `WorldData` maintained by the world handler, references the current world and provides functions to break and place blocks.

Custom data objects can be registered using `PlayData.playData` and then used like normal.
## Packet Handlers
Packet Handlers are the primary extension block of the entire swarm, and act on a subset of packets by id numbers. Packets are passed as a general unknown packet type and must be converted to the target type.
The conversion itself isn't really a safe operation, and it is up to the caller to ensure that the packet really is of the target type.
New or missing packet types can be created by extending Packet, and need not be registered in any way to be used.
See the existing handler classes for examples, the built in handlers are in no way special and alternate custom implementations could be used instead.
