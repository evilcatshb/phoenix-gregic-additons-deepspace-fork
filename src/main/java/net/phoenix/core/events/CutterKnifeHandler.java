package net.phoenix.core.events;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.container.ContainerRenamer;

public class CutterKnifeHandler {

    private static final ResourceLocation CERTUS = new ResourceLocation("ae2", "certus_quartz_cutting_knife");
    private static final ResourceLocation NETHER = new ResourceLocation("ae2", "nether_quartz_cutting_knife");

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player.isSpectator()) return;

        ItemStack stack = event.getItemStack();
        if (!isKnife(stack)) return;

        BlockEntity be = event.getLevel().getBlockEntity(event.getPos());
        if (!(be instanceof MetaMachine mbe)) return;

        if (!(mbe instanceof MEPatternBufferPartMachine)) return;

        if (!event.getLevel().isClientSide) {
            MenuOpener.open(ContainerRenamer.TYPE, player, MenuLocators.forBlockEntity(be));
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
    }

    private boolean isKnife(ItemStack stack) {
        return stack.is(ForgeRegistries.ITEMS.getValue(CERTUS)) || stack.is(ForgeRegistries.ITEMS.getValue(NETHER));
    }
}
