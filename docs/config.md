# Config

Easy Brewing's settings are in `config/easybrewing-common.toml`, on both Fabric and NeoForge. The file is created the first time the game starts. On a server, the server's file is the one that counts.

Out-of-range values are clamped to the nearest allowed value on Fabric, and reset to the default on NeoForge.

## General

<ConfigTable section="General" />

## Item Brewing Station

<ConfigTable section="ItemBrewingStation" />

On NeoForge, changes to this section apply while the game is running. On Fabric, restart the game after editing the file.

Each station remembers its brew time, so a new `processingTime` reaches an existing station the next time the contents of its speed slot change. Stations placed afterwards use it straight away.

## Cobblemon

<ConfigTable section="Cobblemon" />

Not used on Minecraft 26.3. See [Cobblemon](./guide/compat#cobblemon).

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
```

Try different values in the [upgrade calculator](./guide/upgrades#calculator) before changing them on your server.
