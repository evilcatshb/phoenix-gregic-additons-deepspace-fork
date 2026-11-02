package net.phoenix.core.integration.ae2.widget;

import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.integration.ae2.gui.AEGuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.phoenix.core.integration.ae2.METagInputHatchPartMachine;

import brachy.modularui.screen.viewport.ModularGuiContext;
import brachy.modularui.theme.WidgetThemeEntry;
import brachy.modularui.widget.Widget;

public class FluidPreviewWidget extends Widget<FluidPreviewWidget> {

    private final int index;
    private final METagInputHatchPartMachine machine;

    public FluidPreviewWidget(int index, METagInputHatchPartMachine machine) {
        this.index = index;
        this.machine = machine;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        var graphics = context.getGraphics();
        GTGuiTextures.FLUID_SLOT.draw(context, 0, 0, 18, 18);

        if (machine.previewFluids == null || index < 0 || index >= machine.previewFluids.length) return;

        FluidStack stack = machine.previewFluids[index];
        long amount = machine.previewAmounts[index];

        if (stack == null || stack.isEmpty()) return;

        AEGuiHelper.drawFluid(graphics, stack, 1, 1, 16, 16);

        if (amount > 0) {
            AEGuiHelper.drawAmountOverlay(graphics, amount, 1, 1);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawForeground(ModularGuiContext context) {
        if (!isHovering()) return;

        FluidStack stack = machine.previewFluids[index];
        if (stack == null || stack.isEmpty()) return;

        context.getGraphics().renderTooltip(
                Minecraft.getInstance().font,
                stack.getDisplayName(),
                context.getMouseX(), context.getMouseY());
    }
}
