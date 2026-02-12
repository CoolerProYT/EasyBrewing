package com.coolerpromc.easybrewing.screen;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.coolerpromc.easybrewing.network.packet.CapabilityChangeSyncC2SPacket;
import com.coolerpromc.easybrewing.screen.widget.ChangeCapabilityButton;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CyclingSlotIcon;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemBrewingStationScreen extends HandledScreen<ItemBrewingStationMenu> {
    private static final Identifier FUEL_LENGTH_SPRITE = Identifier.ofVanilla("container/brewing_stand/fuel_length");
    private static final Identifier ITEM_BREWING_STATION = EasyBrewing.id("textures/gui/item_brewing_station.png");
    private static final Identifier BREW_PROGRESS_SPRITE = EasyBrewing.id("brew_progress");

    private final CyclingSlotIcon potionIcon = new CyclingSlotIcon(37);
    private final List<ChangeCapabilityButton> capabilityButtons = new ArrayList<>();
    private boolean hasShift = false;

    public ItemBrewingStationScreen(ItemBrewingStationMenu menu, PlayerInventory playerInventory, Text title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        createCapabilityButtons();
    }

    private void createCapabilityButtons(){
        capabilityButtons.clear();
        capabilityButtons.add(new ChangeCapabilityButton(x + 152, y + 49, 8, 8, Text.empty(), this::onPress, ItemBrewingStationBE.RelativeSide.TOP, this.handler.getBlockEntity().getCapabilityBySide(ItemBrewingStationBE.RelativeSide.TOP)));
        capabilityButtons.add(new ChangeCapabilityButton(x + 144, y + 57, 8, 8, Text.empty(), this::onPress, ItemBrewingStationBE.RelativeSide.LEFT, this.handler.getBlockEntity().getCapabilityBySide(ItemBrewingStationBE.RelativeSide.LEFT)));
        capabilityButtons.add(new ChangeCapabilityButton(x + 152, y + 57, 8, 8, Text.empty(), this::onPress, ItemBrewingStationBE.RelativeSide.FRONT, this.handler.getBlockEntity().getCapabilityBySide(ItemBrewingStationBE.RelativeSide.FRONT)));
        capabilityButtons.add(new ChangeCapabilityButton(x + 160, y + 57, 8, 8, Text.empty(), this::onPress, ItemBrewingStationBE.RelativeSide.RIGHT, this.handler.getBlockEntity().getCapabilityBySide(ItemBrewingStationBE.RelativeSide.RIGHT)));
        capabilityButtons.add(new ChangeCapabilityButton(x + 144, y + 65, 8, 8, Text.empty(), this::onPress, ItemBrewingStationBE.RelativeSide.BACK, this.handler.getBlockEntity().getCapabilityBySide(ItemBrewingStationBE.RelativeSide.BACK)));
        capabilityButtons.add(new ChangeCapabilityButton(x + 152, y + 65, 8, 8, Text.empty(), this::onPress, ItemBrewingStationBE.RelativeSide.BOTTOM, this.handler.getBlockEntity().getCapabilityBySide(ItemBrewingStationBE.RelativeSide.BOTTOM)));

        for (ChangeCapabilityButton btn : capabilityButtons){
            this.addDrawableChild(btn);
        }
    }

    private void onPress(ButtonWidget button){
        if (button instanceof ChangeCapabilityButton btn){
            btn.setSlot(btn.getSlot().next());
            ClientPlayNetworking.send(new CapabilityChangeSyncC2SPacket(this.handler.getBlockEntity().getPos(), btn.getDirection(), btn.getSlot()));
        }
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        this.potionIcon.updateTexture(List.of(EasyBrewing.id("item/empty_slot_potion"), EasyBrewing.id("item/empty_slot_splash_potion"), EasyBrewing.id("item/empty_slot_lingering_potion")));
    }

    @Override
    protected void drawBackground(DrawContext guiGraphics, float v, int i, int i1) {
        guiGraphics.drawTexture(RenderPipelines.GUI_TEXTURED, ITEM_BREWING_STATION, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
        this.potionIcon.render(this.handler, guiGraphics, v, x, y);

        int l = MathHelper.clamp((18 * this.handler.getFuel() + 20 - 1) / 20, 0, 18);

        guiGraphics.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FUEL_LENGTH_SPRITE, 18, 4, 0, 0, x + 16, y + 37, l, 4);

        int progress = this.handler.getProgress();
        int maxProgress = this.handler.getMaxProgress();

        int progressHeight = (int) (28f * (1f - (float) progress / maxProgress));
        guiGraphics.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BREW_PROGRESS_SPRITE, 9, 28, 0, 0, x + 97, y + 16, 9, 28 - progressHeight);
    }

    @Override
    public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        drawMouseoverTooltip(guiGraphics, mouseX, mouseY);
        renderArrowTooltip(guiGraphics, mouseX, mouseY);
        renderSlotOutline(guiGraphics);
    }

    private void renderArrowTooltip(DrawContext guiGraphics, int mouseX, int mouseY){
        int arrowX = x + 97;
        int arrowY = y + 16;

        if (mouseX >= arrowX && mouseX <= arrowX + 9 && mouseY >= arrowY && mouseY <= arrowY + 28){
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.translatable("screen.easybrewing.arrow_tooltip", this.handler.getProgress(), this.handler.getMaxProgress()));
            tooltip.add(Text.translatable("screen.easybrewing.speed_multiplier_tooltip", String.format("%.2f", this.handler.getSpeedMultiplier())).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("screen.easybrewing.crafting_amount", this.handler.getAdditionalUpgrade()).formatted(Formatting.GRAY));
            if (hasShift){
                tooltip.add(Text.translatable("screen.easybrewing.max_speed_upgrade", this.handler.getMaxUpgrade()).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("screen.easybrewing.max_amount_upgrade", this.handler.getMaxAmountUpgrade()).formatted(Formatting.GRAY));
            }
            else{
                tooltip.add(Text.translatable("screen.easybrewing.shift_down", this.handler.getAdditionalUpgrade()).formatted(Formatting.DARK_GRAY));
            }
            guiGraphics.drawTooltip(this.textRenderer, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private void renderSlotOutline(DrawContext guiGraphics){
        for (ChangeCapabilityButton btn : capabilityButtons){
            if (btn.isHovered()){
                Slot slot = this.handler.getSlot(btn.getSlot().slotIndex);

                int baseX = slot.x + x - 1;
                int baseY = slot.y + y - 1;

                guiGraphics.fill(baseX, baseY, baseX + 18, baseY + 1, btn.getSlot().color);
                guiGraphics.fill(baseX, baseY + 18 - 1, baseX + 18, baseY + 18, btn.getSlot().color);
                guiGraphics.fill(baseX, baseY, baseX + 1, baseY + 18, btn.getSlot().color);
                guiGraphics.fill(baseX + 18 - 1, baseY, baseX + 18, baseY + 18, btn.getSlot().color);
            }
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        hasShift = input.hasShift();
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        hasShift = input.hasShift();
        return super.keyReleased(input);
    }
}
