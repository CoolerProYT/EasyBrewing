package com.coolerpromc.easybrewing.screen.widget;

import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public class ChangeCapabilityButton extends Button {
    private final ItemBrewingStationBE.RelativeSide direction;
    private ItemBrewingStationBE.Slot slot;

    public ChangeCapabilityButton(int x, int y, int width, int height, Component message, OnPress onPress, ItemBrewingStationBE.RelativeSide direction, ItemBrewingStationBE.Slot slot) {
        super(x, y, width, height, message, onPress, Supplier::get);
        this.direction = direction;
        this.slot = slot;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + 1, slot.color);
        guiGraphics.fill(this.getX(), this.getY() + this.getHeight() - 1, this.getX() + this.getWidth(), this.getY() + this.getHeight(), slot.color);
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.getHeight(), slot.color);
        guiGraphics.fill(this.getX() + this.getWidth() - 1, this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), slot.color);

        if (this.isHovered){
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(Component.translatable("screen.easybrewing.direction_" + direction.name().toLowerCase(Locale.ROOT)));
            tooltips.add(Component.translatable("screen.easybrewing.change_slot").withStyle(ChatFormatting.GRAY));
            guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltips, Optional.empty(), mouseX, mouseY);
        }
    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return 46 + i * 20;
    }

    public void setSlot(ItemBrewingStationBE.Slot slot) {
        this.slot = slot;
    }

    public ItemBrewingStationBE.Slot getSlot() {
        return slot;
    }

    public ItemBrewingStationBE.RelativeSide getDirection() {
        return direction;
    }
}
