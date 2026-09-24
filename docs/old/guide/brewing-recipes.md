# Brewing recipes

The station brews anything a vanilla brewing stand can. It asks the game's brewing registry, the same one the brewing stand uses, so brewing recipes that other mods add through their mod loader work in the station too:

- **NeoForge**: recipes added with `RegisterBrewingRecipesEvent`
- **Forge**: recipes added to `BrewingRecipeRegistry`
- **Fabric**: recipes added with Fabric API's brewing recipe registry

Before Minecraft 26.3, brewing recipes are not data driven, so a datapack can't add or change them.

Every brew in the station makes 3 potions from one ingredient by default, or more with [amount upgrades](./upgrades#amount-upgrades). Pick the kind of bottle you are starting from:

<BrewingExplorer mark-new />

Recipes marked **1.21** make the potions added in Minecraft 1.21: Wind Charging, Weaving, Oozing and Infestation. They are not in Minecraft 1.20.1. Otherwise the vanilla recipes are the same in every version from 1.20.1 to 26.2.

::: tip
A splash potion is brewed from a normal potion with gunpowder, and a lingering potion from a splash potion with dragon's breath. The tables above include those steps.
:::

On Minecraft 1.21.1 with Cobblemon installed, the station also brews Cobblemon's brewing recipes. See [Cobblemon](./compat#cobblemon).
