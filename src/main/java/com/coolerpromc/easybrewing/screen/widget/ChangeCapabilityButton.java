package com.coolerpromc.easybrewing.screen.widget;

import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ChangeCapabilityButton extends ButtonWidget {
    private final Direction direction;
    private ItemBrewingStationBE.Slot slot;

    public ChangeCapabilityButton(int x, int y, int width, int height, Text message, PressAction onPress, Direction direction, ItemBrewingStationBE.Slot slot) {
        super(x, y, width, height, message, onPress, Supplier::get);
        this.direction = direction;
        this.slot = slot;
    }

    @Override
    protected void renderButton(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderButton(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + 1, slot.color);
        guiGraphics.fill(this.getX(), this.getY() + this.getHeight() - 1, this.getX() + this.getWidth(), this.getY() + this.getHeight(), slot.color);
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.getHeight(), slot.color);
        guiGraphics.fill(this.getX() + this.getWidth() - 1, this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), slot.color);

        if (this.hovered){
            List<Text> tooltips = new ArrayList<>();
            tooltips.add(Text.translatable("screen.easybrewing.direction_" + direction.getName()));
            tooltips.add(Text.translatable("screen.easybrewing.change_slot").formatted(Formatting.GRAY));
            guiGraphics.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltips, Optional.empty(), mouseX, mouseY);
        }
    }

    public void setSlot(ItemBrewingStationBE.Slot slot) {
        this.slot = slot;
    }

    public ItemBrewingStationBE.Slot getSlot() {
        return slot;
    }

    public Direction getDirection() {
        return direction;
    }
}
