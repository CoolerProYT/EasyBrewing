# Other mods

Which integrations Easy Brewing has depends on your Minecraft version and loader:

| Minecraft | Loader | JEI | REI | Create | Cobblemon |
| --- | --- | :---: | :---: | :---: | :---: |
| 1.20.1 | Forge | ✓ | ✓ | ✓ | |
| 1.20.1 | Fabric | ✓ | ✓ | | |
| 1.21.1 | NeoForge | ✓ | ✓ | ✓ | ✓ |
| 1.21.1 | Fabric | ✓ | ✓ | | ✓ |
| 1.21.11 | NeoForge | ✓ | | | |
| 1.21.11 | Fabric | ✓ | ✓ | | |
| 1.21.11 | Forge | | | | |
| 26.1, 26.2 | NeoForge | ✓ | | | |
| 26.1, 26.2 | Fabric | | | | |

Hoppers and item pipes work in every version. See [Automation](./automation).

## JEI

With [JEI](https://modrinth.com/mod/jei) installed, the Item Brewing Station has its own recipe category:

- Look up the recipes of any potion or ingredient and the station's category shows how to brew it, with the potion counts your server uses.
- Look up the uses of the Item Brewing Station to browse every brewing recipe.
- Click the middle of the station's screen, between the potion and output slots, to open the station's recipes.

## REI

With [REI](https://modrinth.com/mod/rei) installed, the station has an REI category that works the same way: it lists every brewing recipe, shows the station as the place to brew them, and opens from the same spot in the station's screen.

## Create

On Forge 1.20.1 and NeoForge 1.21.1, [Create](https://modrinth.com/mod/create)'s Mechanical Arm can put items in the station and take finished potions out. See [Create's mechanical arm](./automation#create-s-mechanical-arm).

## Cobblemon

On Minecraft 1.21.1, on both Fabric and NeoForge, the station also brews [Cobblemon](https://modrinth.com/mod/cobblemon)'s brewing recipes, the ones a brewing stand makes with Cobblemon installed:

- Put the recipe's bottle in the potion slot and its ingredient in the ingredient slot.
- Each brew uses **3** bottles and makes 3 results by default. Server owners can change this with [`cobblemonPotionCount`](../config#cobblemon). Amount upgrades add to it as usual.
- Fuel, speed upgrades and automation work the same as for potions.
- JEI shows Cobblemon's recipes in the station's category.

Other versions of Minecraft don't have this, since Cobblemon isn't made for them.
