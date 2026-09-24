<!--@include: ../../guide/automation.md-->

## Create's mechanical arm

On **Forge 1.20.1** and **NeoForge 1.21.1**, a [Create](https://modrinth.com/mod/create) Mechanical Arm can use the station as an input or an output point:

- **Taking** from the station always takes finished potions from the output slot.
- **Depositing** puts the item in the first slot that accepts it, trying the ingredient slot, then fuel, then potions. Blaze powder is also a brewing ingredient, so an arm puts blaze powder in the ingredient slot when that slot is empty or already holds blaze powder. Feed fuel with a hopper on top instead.

The arm ignores the side settings.
