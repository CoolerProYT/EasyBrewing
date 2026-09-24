# Config

Easy Brewing's settings are in `config/easybrewing-common.toml` on every loader. The file is created the first time the game starts. On a server, the server's file is the one that counts.

Out-of-range values are clamped to the nearest allowed value on Fabric, and reset to the default on Forge and NeoForge.

## General

<ConfigTable section="General" />

## Item Brewing Station

<ConfigTable section="ItemBrewingStation" />

On Forge and NeoForge, changes to this section apply while the game is running. On Fabric, restart the game after editing the file.

Each station remembers its brew time, so a new `processingTime` reaches an existing station the next time the contents of its speed slot change. Stations placed afterwards use it straight away.

## Cobblemon

<ConfigTable section="Cobblemon" />

Used on Minecraft 1.21.1, where the station brews [Cobblemon's recipes](./guide/compat#cobblemon). The section is in the file from 1.21.1 onwards, but does nothing on later versions. Minecraft 1.20.1 doesn't have it.

## Example

```toml
[General]
	potionStackSize = 16
	maxSpeedUpgrade = 5
	allowAmountUpgrade = true
	maxAmountUpgrade = 1

[ItemBrewingStation]
	potionCount = 3
	processingTime = 400

[Cobblemon]
	cobblemonPotionCount = 3
```

Try different values in the [upgrade calculator](./guide/upgrades#calculator) before changing them on your server.
