# Automation

Hoppers, droppers and item pipes from other mods can put items in and take items out of the station. Each of the six sides of the block leads to one slot, and you choose which.

## Default sides

Sides are relative to the station's front, the side that faced you when you placed it. Left and right are as you see them standing in front of it.

| Side | Slot |
| --- | --- |
| Top | Fuel |
| Left and right | Potion |
| Front and back | Ingredient |
| Bottom | Output |

So with no setup at all, a hopper on top feeds blaze powder, a hopper into the left or right side feeds potions, a hopper into the front or back feeds ingredients, and a hopper underneath collects the finished potions.

## Changing a side

The six small buttons at the bottom right of the station's screen stand for the six sides, laid out like a map of the block: top, then left, front and right in a row, then back and bottom underneath. Click a button to move that side to the next slot: fuel, potion, ingredient, output, then back to fuel.

Each button's outline is the color of its slot, and hovering over a button outlines that slot in the screen. Try it:

<SideConfig />

Several sides can lead to the same slot, and a slot doesn't need a side at all.

## What each slot accepts

Slots only take items that belong there, whether from a player or a hopper:

- **Fuel**: blaze powder only.
- **Potion**: anything a brewing recipe can start from, such as water bottles and awkward potions.
- **Ingredient**: anything used as an ingredient in a brewing recipe.
- **Output**: nothing. Only the station puts potions here.

A side can also take items out of whichever slot it leads to. A hopper under a side set to **Potion** will pull the potions back out, so keep the side you collect from set to **Output**.

::: tip Chain brewing
To brew all the way to, say, Splash Potions of Swiftness, chain stations: the output of one station feeds the potion slot of the next, and each station gets its own ingredient. The first brews Awkward Potions from water bottles and nether wart, the second adds sugar, the third gunpowder.
:::
