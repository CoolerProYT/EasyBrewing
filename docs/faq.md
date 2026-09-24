# FAQ

## My station isn't brewing

Check each of these:

- **Fuel**: there is blaze powder in the fuel slot, or fuel left in the bar.
- **Enough potions**: the potion slot needs at least 3 potions, plus the **Additional Amount** shown when you hover over the arrow.
- **A real recipe**: the potion and the ingredient make a brewing recipe. Items that can't be brewed at all won't go in the slots.
- **Room in the output**: the output slot must be empty, or hold the same potion with space for a whole brew.

## Can I brew three different potions at once, like a brewing stand?

No. The station brews one kind of potion at a time, but in any amount. Use more stations for different potions.

## Why doesn't my second amount upgrade do anything?

Only 1 amount upgrade counts by default. Your server can raise this with [`maxAmountUpgrade`](./config). The same goes for speed upgrades past the fifth, with `maxSpeedUpgrade`.

## Do potions stack on their own now?

Yes. With Easy Brewing installed, potions, splash potions and lingering potions stack to 16 everywhere, not just in the station. Change it with [`potionStackSize`](./config).

## Does the station work when nobody is nearby?

It works while its chunk is loaded, like a furnace.

## Do upgrades use more fuel?

No. A blaze powder always lasts 20 brews. Speed upgrades just get through those brews faster, and amount upgrades make each brew bigger, so they get more potions out of every powder.

## Does it work with Cobblemon?

Yes, on Minecraft 1.21.1. See [Cobblemon on 1.21.1](./old/guide/compat#cobblemon).
