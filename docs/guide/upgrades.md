# Upgrades

The station has two upgrade slots on the right of its screen: the top one takes **speed upgrades**, the one below it **amount upgrades**. Each slot takes a stack of one kind of upgrade, so you can't mix tiers in the same slot.

All upgrades start from an Upgrade Base.

<RecipeCard id="upgrade_base" />

## Speed upgrades

Speed upgrades make every brew shorter. Each item in the stack multiplies the speed again, so they add up quickly: two Speed Upgrade 1s give ×1.25 × 1.25 = ×1.5625. Only the first **5** items in the stack count by default.

<UpgradeTable kind="speed" />

Each tier is crafted from the one before it.

<RecipeCard id="speed_upgrade_1" />
<RecipeCard id="speed_upgrade_2" />
<RecipeCard id="speed_upgrade_3" />

## Amount upgrades

Amount upgrades make every brew bigger. The station takes that many more potions from the potion slot and brews that many more, still from one ingredient, and the fuel lasts the same 20 brews. Only **1** amount upgrade counts by default, so pick the best one you can make.

<UpgradeTable kind="amount" />

<RecipeCard id="amount_upgrade_1" />
<RecipeCard id="amount_upgrade_2" />

::: warning
A brew can't be bigger than a potion stack, because the potion and output slots each hold one stack. With the default stack size of 16 this never comes up, but a server that lowers `potionStackSize` or allows more amount upgrades can end up with a station that never starts.
:::

## Calculator

Pick your upgrades to see how fast the station brews. Open **Server config** if your server has changed the defaults.

<UpgradeCalculator />

## How the numbers work

- **Time per brew** is `processingTime ÷ speed multiplier`, rounded down to whole ticks. `processingTime` is 400 ticks (20 seconds) by default.
- **Speed multiplier** is the upgrade's multiplier to the power of how many count: Speed Upgrade 3 × 5 is 1.75⁵ ≈ ×16.4, so a brew takes 24 ticks.
- **Potions per brew** is `potionCount` (3 by default) plus the amount upgrade's bonus for each one that counts.
- A blaze powder always lasts **20 brews**, so amount upgrades also stretch your fuel. Speed upgrades don't change how much fuel a potion costs.
