# Pigly's Player Head Drops
<a href="https://modrinth.com/mod/piglys-player-head-drops"><img src="https://img.shields.io/badge/dynamic/json?color=158000&label=downloads&prefix=+%20&query=downloads&url=https://api.modrinth.com/v2/project/hvwKeVnz&logo=modrinth" alt="Modrinth Downloads"></a>

When a player is killed by a player, they drop a head.

This head can be used by another player to make them invisible to the head's owner.

Players can also run /decorate to make the heads purely decorative.

Invisibility lasts for 1 minute or until the head's owner is hit by the player.

Heads have a 20-second cooldown.

[Javadoc](https://piglys-player-head-javadoc.replit.app/)

## Changes in 1.1.0
- Config option for consuming the head on use
- Config option for head use duration
- Config option for head use cooldown
- Disguise heads
- Config option for disguise duration
- NBT Crafting API (can be used in plugins with this as a dependency)
- Useful for crafting recipes involving player heads
- Owner storage changes (using PlayerProfile instead of Player)