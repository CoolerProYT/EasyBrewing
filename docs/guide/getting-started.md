# Getting started

Easy Brewing adds the Item Brewing Station, a brewing stand that brews from a whole stack of potions and can be upgraded to brew faster and in bigger batches.

## Install

1. Install [Fabric](https://fabricmc.net/) with Fabric API, or [NeoForge](https://neoforged.net/), for Minecraft **26.3**.
2. Put the Easy Brewing jar in your `mods` folder.
3. Optional, on NeoForge: add [JEI](https://modrinth.com/mod/jei) to look up brewing recipes from the station.

::: tip Playing on an older Minecraft version?
Use the version menu at the top of the page to switch to the wiki for Minecraft 1.20.1 – 26.2. Brewing recipes and mod integrations work differently there.
:::

<!-- #region first-brew -->
## Your first brew

**1. Craft an Item Brewing Station.** It needs diamonds and a blaze powder, so it comes after your first trip to the Nether.

<RecipeCard id="item_brewing_station" />

**2. Place it and open it.** Mine it with a pickaxe to pick it back up. Anything inside drops when it breaks.

**3. Add fuel.** Put blaze powder in the top-left slot. One powder lasts 20 brews, the same as a vanilla brewing stand.

**4. Add water bottles and nether wart.** Water bottles go in the top slot, nether wart in the slot to its left. The station needs at least **3** potions in the slot to start.

**5. Wait for the arrow.** A brew takes 20 seconds. It uses one nether wart and three water bottles, and puts three Awkward Potions in the bottom slot.

<StationGui />

The station keeps brewing until it runs out of potions, ingredients or fuel, or the output slot is full.

::: tip
Hover over the arrow to see the progress in ticks and the station's current speed and batch size. Hold <kbd>Shift</kbd> for the upgrade limits.
:::

<!-- #endregion first-brew -->
## Where to go next

- [Item Brewing Station](./brewing-station): exactly how a brew works
- [Upgrades](./upgrades): brew faster and in bigger batches, with a calculator
- [Automation](./automation): feed the station with hoppers and pipes
- [Brewing recipes](./brewing-recipes): every potion and what it is brewed from
