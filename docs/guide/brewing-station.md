# Item Brewing Station

<ItemSlot id="easybrewing:item_brewing_station" label />

The station brews one recipe at a time from a stack of potions. With the default config, each brew turns **3 potions and 1 ingredient into 3 new potions** in **20 seconds**, the same as a full vanilla brewing stand. The difference is that it keeps going: load 16 water bottles and a stack of nether wart and it works through all of them without you.

The items in the potion, ingredient and output slots are shown on the station's three stands, so you can see what it is brewing without opening it.

<StationGui />

## A brew, step by step

Every tick, the station checks that it can brew:

1. The ingredient and the potions in the potion slot make a brewing recipe.
2. The potion slot holds at least as many potions as one brew uses: 3, plus any from [amount upgrades](./upgrades#amount-upgrades).
3. The output slot is empty, or holds the same potion with room for the whole batch.
4. It has fuel left.

While all four hold, the arrow fills. If any stops being true, for example you take the potions out, the progress resets to zero.

When the arrow is full the station:

- uses **1 ingredient**,
- takes the potions for one brew from the potion slot,
- puts the same number of brewed potions in the output slot,
- uses one brew's worth of fuel,
- and plays the brewing stand sound.

## Fuel

Only blaze powder works as fuel. When the station runs out, it takes one powder from the fuel slot, which lasts **20 brews**. The bar under the fuel slot shows how much is left. Fuel is only used when a brew finishes, so an idle station keeps its charge.

## Potion stacks

Easy Brewing makes potions, splash potions and lingering potions stack to **16**. Server owners can change this with [`potionStackSize`](../config). Since the potion and output slots each hold one stack, one brew can never use more potions than a stack holds.

## Ingredients that leave something behind

No vanilla ingredient leaves anything behind, but brewing recipes added by other mods can use one that does, like a bucket. The leftover stays in the ingredient slot once the last ingredient is used. If there are still ingredients in the slot, the leftover drops on top of the station instead.

## Breaking the station

It needs a pickaxe to drop. Everything inside, including upgrades, drops on the ground. Fuel already taken from a blaze powder is lost.
