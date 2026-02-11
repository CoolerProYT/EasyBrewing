package com.coolerpromc.easybrewing.screen.widget;

import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

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
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("widget/button"), this.getX(), this.getY(), this.getWidth(), this.getHeight());

        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + 1, slot.color);
        guiGraphics.fill(this.getX(), this.getY() + this.getHeight() - 1, this.getX() + this.getWidth(), this.getY() + this.getHeight(), slot.color);
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.getHeight(), slot.color);
        guiGraphics.fill(this.getX() + this.getWidth() - 1, this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), slot.color);

        if (this.isHovered){
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(Component.translatable("screen.easybrewing.direction_" + direction.name().toLowerCase(Locale.ROOT)));
            tooltips.add(Component.translatable("screen.easybrewing.change_slot").withStyle(ChatFormatting.GRAY));
            guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltips, Optional.empty(), mouseX, mouseY);
        }
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
