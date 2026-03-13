package com.coolerpromc.easybrewing.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

// DefaultItemComponentEvents.MODIFY in EasyBrewing sets MAX_STACK_SIZE on potion items,
// which makes getDefaultMaxStackSize() return the correct value automatically.
@Mixin(Item.class)
public class ItemMixin {
}