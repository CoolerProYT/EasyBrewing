# JEI & other mods

## JEI

On NeoForge, with [JEI](https://modrinth.com/mod/jei) installed, the Item Brewing Station has its own recipe category:

- Look up the recipes of any potion or ingredient and the station's category shows how to brew it, with the potion counts your server uses.
- Look up the uses of the Item Brewing Station to browse every brewing recipe.
- Click the middle of the station's screen, between the potion and output slots, to open the station's recipes.

The recipes come from the game's data, so datapack and modded brewing recipes show up too.

The Fabric version does not have a JEI integration on Minecraft 26.3.

## Hoppers and pipes

The station works with vanilla hoppers and droppers, and with any mod that moves items through the standard item transfer system of its loader: Fabric's Transfer API or NeoForge's item handler capability. See [Automation](./automation) for which side leads to which slot.

## Cobblemon

Cobblemon brewing is in the Minecraft 1.21.1 versions of Easy Brewing, on both Fabric and NeoForge, since that is the version Cobblemon is made for. See [Cobblemon on 1.21.1](../old/guide/compat#cobblemon).

On Minecraft 26.3 it is turned off. The `cobblemonPotionCount` config option is kept for when Cobblemon is available.
