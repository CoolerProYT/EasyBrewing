package com.coolerpromc.easybrewing.screen;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.network.packet.CapabilityChangeSyncC2SPacket;
import com.coolerpromc.easybrewing.screen.widget.ChangeCapabilityButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemBrewingStationScreen extends AbstractContainerScreen<ItemBrewingStationMenu> {
    private static final ResourceLocation FUEL_LENGTH_SPRITE = new ResourceLocation("container/brewing_stand/fuel_length");
    private static final ResourceLocation ITEM_BREWING_STATION = EasyBrewing.id("textures/gui/item_brewing_station.png");
    private static final ResourceLocation BREW_PROGRESS_SPRITE = EasyBrewing.id("brew_progress");

    private final CyclingSlotBackground potionIcon = new CyclingSlotBackground(37);
    private final List<ChangeCapabilityButton> capabilityButtons = new ArrayList<>();

    public ItemBrewingStationScreen(ItemBrewingStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        createCapabilityButtons();
    }

    private void createCapabilityButtons(){
        capabilityButtons.clear();
        capabilityButtons.add(new ChangeCapabilityButton(leftPos + 152, topPos + 49, 8, 8, Component.empty(), this::onPress, Direction.UP, this.menu.getBlockEntity().getCapabilityBySide(Direction.UP)));
        capabilityButtons.add(new ChangeCapabilityButton(leftPos + 144, topPos + 57, 8, 8, Component.empty(), this::onPress, Direction.EAST, this.menu.getBlockEntity().getCapabilityBySide(Direction.EAST)));
        capabilityButtons.add(new ChangeCapabilityButton(leftPos + 152, topPos + 57, 8, 8, Component.empty(), this::onPress, Direction.NORTH, this.menu.getBlockEntity().getCapabilityBySide(Direction.NORTH)));
        capabilityButtons.add(new ChangeCapabilityButton(leftPos + 160, topPos + 57, 8, 8, Component.empty(), this::onPress, Direction.WEST, this.menu.getBlockEntity().getCapabilityBySide(Direction.WEST)));
        capabilityButtons.add(new ChangeCapabilityButton(leftPos + 144, topPos + 65, 8, 8, Component.empty(), this::onPress, Direction.SOUTH, this.menu.getBlockEntity().getCapabilityBySide(Direction.SOUTH)));
        capabilityButtons.add(new ChangeCapabilityButton(leftPos + 152, topPos + 65, 8, 8, Component.empty(), this::onPress, Direction.DOWN, this.menu.getBlockEntity().getCapabilityBySide(Direction.DOWN)));

        for (ChangeCapabilityButton btn : capabilityButtons){
            this.addRenderableWidget(btn);
        }
    }

    private void onPress(Button button){
        if (button instanceof ChangeCapabilityButton btn){
            btn.setSlot(btn.getSlot().next());
            EasyBrewing.CHANNEL.sendToServer(new CapabilityChangeSyncC2SPacket(this.menu.getBlockEntity().getBlockPos(), btn.getDirection(), btn.getSlot()));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.potionIcon.tick(List.of(EasyBrewing.id("item/empty_slot_potion"), EasyBrewing.id("item/empty_slot_splash_potion"), EasyBrewing.id("item/empty_slot_lingering_potion")));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(ITEM_BREWING_STATION, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        this.potionIcon.render(this.menu, guiGraphics, v, leftPos, topPos);

        int l = Mth.clamp((18 * this.menu.getFuel() + 20 - 1) / 20, 0, 18);

        guiGraphics.blit(new ResourceLocation("textures/gui/container/brewing_stand.png"), leftPos + 16, topPos + 37, 176, 29, l, 4);

        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();

        int progressHeight = (int) (28f * (1f - (float) progress / maxProgress));
        guiGraphics.blit(new ResourceLocation("textures/gui/container/brewing_stand.png"), leftPos + 97, topPos + 16, 176, 0, 9, 28 - progressHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        renderArrowTooltip(guiGraphics, mouseX, mouseY);
        renderSlotOutline(guiGraphics);
    }

    private void renderArrowTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY){
        int arrowX = leftPos + 97;
        int arrowY = topPos + 16;

        if (mouseX >= arrowX && mouseX <= arrowX + 9 && mouseY >= arrowY && mouseY <= arrowY + 28){
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("screen.easbrewing.arrow_tooltip", this.menu.getProgress(), this.menu.getMaxProgress()));
            tooltip.add(Component.translatable("screen.easybrewing.speed_multiplier_tooltip", String.format("%.2f", this.menu.getSpeedMultiplier())).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("screen.easybrewing.crafting_amount", this.menu.getAdditionalUpgrade()).withStyle(ChatFormatting.GRAY));
            if (hasShiftDown()){
                tooltip.add(Component.translatable("screen.easybrewing.max_speed_upgrade", this.menu.getMaxUpgrade()).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("screen.easybrewing.max_amount_upgrade", this.menu.getMaxAmountUpgrade()).withStyle(ChatFormatting.GRAY));
            }
            else{
                tooltip.add(Component.translatable("screen.easybrewing.shift_down", this.menu.getAdditionalUpgrade()).withStyle(ChatFormatting.DARK_GRAY));
            }
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private void renderSlotOutline(GuiGraphics guiGraphics){
        for (ChangeCapabilityButton btn : capabilityButtons){
            if (btn.isHovered()){
                Slot slot = this.menu.getSlot(btn.getSlot().slotIndex);

                int baseX = slot.x + leftPos - 1;
                int baseY = slot.y + topPos - 1;

                guiGraphics.fill(baseX, baseY, baseX + 18, baseY + 1, btn.getSlot().color);
                guiGraphics.fill(baseX, baseY + 18 - 1, baseX + 18, baseY + 18, btn.getSlot().color);
                guiGraphics.fill(baseX, baseY, baseX + 1, baseY + 18, btn.getSlot().color);
                guiGraphics.fill(baseX + 18 - 1, baseY, baseX + 18, baseY + 18, btn.getSlot().color);
            }
        }
    }
}
