package net.phoenix.core.integration.ae2;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableFluidTank;
import com.gregtechceu.gtceu.integration.ae2.machine.MEHatchPartMachine;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEFluidList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEFluidSlot;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.phoenix.core.integration.ae2.utils.TagMatcher;
import net.phoenix.core.integration.ae2.widget.FluidPreviewWidget;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.value.sync.StringSyncValue;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.ButtonWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.layout.Grid;
import brachy.modularui.widgets.textfield.TextFieldWidget;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class METagInputHatchPartMachine extends MEHatchPartMachine
                                        implements IDataStickInteractable {

    protected static final int CONFIG_SIZE = 32;

    protected ExportOnlyAEFluidList aeFluidHandler;
    private boolean nukeTriggered = false;

    protected String whitelistExpr = "";
    protected String blacklistExpr = "";

    public FluidStack[] previewFluids = new FluidStack[CONFIG_SIZE];
    public long[] previewAmounts = new long[CONFIG_SIZE];

    protected int refreshTimer = 0;

    public METagInputHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, IO.IN);
        clearPreview();
    }

    /*
     * =========================
     * == LIFECYCLE / TANK ==
     * =========================
     */

    @Override
    protected NotifiableFluidTank createTank(int initialCapacity, int slots) {
        this.aeFluidHandler = new ExportOnlyAEFluidList(this, slots);
        return aeFluidHandler;
    }

    @Override
    public void onMachineDestroyed() {
        super.onMachineDestroyed();
        flushInventory();
    }

    /*
     * =========================
     * == ME SYNC ==
     * =========================
     */

    @Override
    protected void autoIO() {
        if (!isWorkingEnabled()) return;
        if (!shouldSyncME()) return;

        if (updateMEStatus()) {

            if (!isRemote() && !nukeTriggered && containsNukeTag()) {
                nukeTriggered = true;
                triggerNuke();
                setChanged();
                return;
            }

            if (!isRemote() && ++refreshTimer >= 20) {
                refreshTimer = 0;
                updateConfigurationFromTags();
            }

            syncME();
            updateTankSubscription();
        }
    }

    private void triggerNuke() {
        if (!(getLevel() instanceof ServerLevel world)) return;

        double x = getBlockPos().getX() + 0.5;
        double y = getBlockPos().getY() + 0.5;
        double z = getBlockPos().getZ() + 0.5;

        float power = 4.0f;

        world.explode(
                null,
                x, y, z,
                power,
                Level.ExplosionInteraction.BLOCK);

        int radius = Mth.clamp((int) Math.ceil(power / 4.0), 1, 3);
        BlockPos center = getBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {

                    if (dx * dx + dy * dy + dz * dz > radius * radius + 1) continue;

                    BlockPos targetPos = center.offset(dx, dy, dz);
                    if (!world.isLoaded(targetPos)) continue;

                    BlockState state = world.getBlockState(targetPos);
                    if (state.isAir()) continue;
                    if (state.getDestroySpeed(world, targetPos) < 0) continue;

                    world.removeBlockEntity(targetPos);
                    world.setBlock(
                            targetPos,
                            Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_ALL | 64 | 128);
                }
            }
        }
    }

    private boolean containsNukeTag() {
        return whitelistExpr.contains("forge:nuclear_bombs") || blacklistExpr.contains("forge:nuclear_bombs");
    }

    protected void syncME() {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            clearPreview();
            return;
        }

        MEStorage network = grid.getStorageService().getInventory();

        for (int i = 0; i < CONFIG_SIZE; i++) {
            ExportOnlyAEFluidSlot slot = aeFluidHandler.getInventory()[i];

            GenericStack overflow = slot.exceedStack();
            if (overflow != null) {
                long inserted = network.insert(overflow.what(), overflow.amount(), Actionable.MODULATE, actionSource);
                if (inserted > 0) {
                    slot.drain(GTMath.saturatedCast(inserted), IFluidHandler.FluidAction.EXECUTE);
                }
            }

            GenericStack req = slot.requestStack();
            if (req != null && req.what() instanceof AEFluidKey key && isAllowed(key)) {
                long extracted = network.extract(req.what(), req.amount(), Actionable.MODULATE, actionSource);
                if (extracted > 0) {
                    slot.addStack(new GenericStack(key, extracted));
                }
            }

            GenericStack stock = slot.getStock();
            GenericStack config = slot.getConfig();

            if (stock != null && stock.what() instanceof AEFluidKey key) {
                previewFluids[i] = key.toStack(1000);
                previewAmounts[i] = stock.amount();
            } else if (config != null && config.what() instanceof AEFluidKey key) {
                previewFluids[i] = key.toStack(1000);
                previewAmounts[i] = 0;
            } else {
                previewFluids[i] = FluidStack.EMPTY;
                previewAmounts[i] = 0;
            }
        }
    }

    protected void flushInventory() {
        var grid = getMainNode().getGrid();
        if (grid == null) return;

        MEStorage storage = grid.getStorageService().getInventory();
        for (var slot : aeFluidHandler.getInventory()) {
            GenericStack stock = slot.getStock();
            if (stock != null) {
                storage.insert(stock.what(), stock.amount(), Actionable.MODULATE, actionSource);
            }
        }
    }

    /*
     * =========================
     * == TAG LOGIC ==
     * =========================
     */

    protected boolean isAllowed(AEFluidKey key) {
        if (whitelistExpr.isBlank() && blacklistExpr.isBlank()) return false;
        if (!blacklistExpr.isBlank() && TagMatcher.doesFluidMatch(key, blacklistExpr)) return false;
        if (!whitelistExpr.isBlank()) return TagMatcher.doesFluidMatch(key, whitelistExpr);
        return true;
    }

    protected void updateConfigurationFromTags() {
        if (isRemote()) return;

        var grid = getMainNode().getGrid();
        if (grid == null) return;

        var storage = grid.getStorageService().getInventory();
        var available = storage.getAvailableStacks();

        Set<AEFluidKey> configured = new HashSet<>();
        boolean changed = false;

        for (int i = 0; i < CONFIG_SIZE; i++) {
            var slot = aeFluidHandler.getInventory()[i];
            var config = slot.getConfig();

            if (config != null && config.what() instanceof AEFluidKey key) {
                if (available.get(key) > 0 && isAllowed(key)) {
                    configured.add(key);
                } else {
                    slot.setConfig(null);
                    previewFluids[i] = FluidStack.EMPTY;
                    previewAmounts[i] = 0;
                    changed = true;
                }
            }
        }

        for (var entry : available) {
            if (entry.getKey() instanceof AEFluidKey key && isAllowed(key)) {
                if (!configured.contains(key)) {
                    for (var slot : aeFluidHandler.getInventory()) {
                        if (slot.getConfig() == null) {
                            slot.setConfig(new GenericStack(key, Integer.MAX_VALUE));
                            configured.add(key);
                            changed = true;
                            break;
                        }
                    }
                }
            }
        }

        if (changed) {
            updateTankSubscription();
            setChanged();
        }
    }

    protected void clearPreview() {
        for (int i = 0; i < CONFIG_SIZE; i++) {
            previewFluids[i] = FluidStack.EMPTY;
            previewAmounts[i] = 0;
        }
    }

    /*
     * =========================
     * == GUI ==
     * =========================
     */

    @Override
    public void buildMainUI(ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
                            UISettings settings) {
        StringSyncValue whitelistValue = new StringSyncValue(() -> whitelistExpr, val -> {
            whitelistExpr = val;
            updateConfigurationFromTags();
        }).allowC2S();
        StringSyncValue blacklistValue = new StringSyncValue(() -> blacklistExpr, val -> {
            blacklistExpr = val;
            updateConfigurationFromTags();
        }).allowC2S();

        var flow = Flow.col().coverChildren();

        flow.child(Text.dynamic(() -> isOnline() ?
                Component.translatable("gtceu.gui.me_network.online") :
                Component.translatable("gtceu.gui.me_network.offline"))
                .asWidget().marginBottom(4));

        flow.child(new ButtonWidget<>()
                .size(60, 16)
                .overlay(Text.str("Clear"))
                .onMousePressed((context, button) -> {
                    if (button != 0) return false;
                    whitelistExpr = "";
                    blacklistExpr = "";
                    updateConfigurationFromTags();
                    return true;
                }).marginBottom(4));

        flow.child(Text.str("Whitelist Tags").asWidget());
        flow.child(new TextFieldWidget().width(166).height(30).value(whitelistValue).marginBottom(4));

        flow.child(Text.str("Blacklist Tags").asWidget());
        flow.child(new TextFieldWidget().width(166).height(30).value(blacklistValue).marginBottom(4));

        flow.child(Text.str("Fluid Preview (Read Only)").asWidget().marginBottom(2));

        Grid previewGrid = new Grid()
                .coverChildren()
                .gridOfSizeWidth(8, 4,
                        (x, y, i) -> i < CONFIG_SIZE ? new FluidPreviewWidget(i, this).size(18, 18) :
                                new brachy.modularui.widget.Widget<>().size(18, 18));
        flow.child(previewGrid);

        mainWidget.child(flow.center());
    }

    /*
     * =========================
     * == DATA STICK ==
     * =========================
     */

    @Override
    public InteractionResult onDataStickShiftUse(Player player, ItemStack stick) {
        if (!isRemote()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("WhitelistExpr", whitelistExpr);
            tag.putString("BlacklistExpr", blacklistExpr);
            stick.getOrCreateTag().put("METagInputHatch", tag);
            player.sendSystemMessage(Component.literal("Tag Fluid Hatch settings copied"));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onDataStickUse(Player player, ItemStack stick) {
        if (!stick.hasTag() || !stick.getTag().contains("METagInputHatch"))
            return InteractionResult.PASS;

        if (!isRemote()) {
            CompoundTag tag = stick.getTag().getCompound("METagInputHatch");
            whitelistExpr = tag.getString("WhitelistExpr");
            blacklistExpr = tag.getString("BlacklistExpr");
            updateConfigurationFromTags();
            player.sendSystemMessage(Component.literal("Tag Fluid Hatch settings pasted"));
        }
        return InteractionResult.sidedSuccess(isRemote());
    }
}
