package net.phoenix.core.integration.ae2;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.phoenix.core.integration.ae2.utils.TagMatcher;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.screen.viewport.ModularGuiContext;
import brachy.modularui.theme.WidgetThemeEntry;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.value.sync.StringSyncValue;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widget.Widget;
import brachy.modularui.widgets.ButtonWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.layout.Grid;
import brachy.modularui.widgets.textfield.TextFieldWidget;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class METagInputBusPartMachine extends MEBusPartMachine
                                      implements IDataStickInteractable {

    protected static final int CONFIG_SIZE = 32;

    protected final ExportOnlyAEItemList aeItemHandler;

    protected String whitelistExpr = "";
    protected String blacklistExpr = "";

    protected int refreshTimer = 0;

    public final ItemStack[] previewStacks = new ItemStack[CONFIG_SIZE];
    public final long[] previewAmounts = new long[CONFIG_SIZE];

    public METagInputBusPartMachine(BlockEntityCreationInfo info) {
        super(info, IO.IN, new ExportOnlyAEItemList(CONFIG_SIZE));
        this.aeItemHandler = (ExportOnlyAEItemList) getInventory();
        for (int i = 0; i < CONFIG_SIZE; i++) {
            previewStacks[i] = ItemStack.EMPTY;
        }
    }

    /////////////////////////////////
    // ***** Machine LifeCycle ****//
    /////////////////////////////////

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) updateConfigurationFromTags();
    }

    @Override
    public void onMachineDestroyed() {
        super.onMachineDestroyed();
        flushInventory();
    }

    protected void flushInventory() {
        if (getMainNode().getGrid() == null) return;
        var storage = getMainNode().getGrid().getStorageService().getInventory();
        for (var aeSlot : aeItemHandler.getInventory()) {
            GenericStack stock = aeSlot.getStock();
            if (stock != null) {
                long inserted = storage.insert(stock.what(), stock.amount(), Actionable.MODULATE, actionSource);
                if (inserted > 0) aeSlot.extractItem(0, GTMath.saturatedCast(inserted), false);
            }
        }
    }

    /////////////////////////////////
    // ********** Sync ME *********//
    /////////////////////////////////

    @Override
    protected void autoIO() {
        if (!this.isWorkingEnabled() || !this.shouldSyncME()) return;

        if (this.updateMEStatus()) {
            if (!isRemote() && ++refreshTimer >= 20) {
                refreshTimer = 0;
                updateConfigurationFromTags();
            }

            this.syncME();
            this.updateInventorySubscription();
        }
    }

    protected void syncME() {
        if (getMainNode().getGrid() == null) {
            for (int i = 0; i < CONFIG_SIZE; i++) {
                previewStacks[i] = ItemStack.EMPTY;
                previewAmounts[i] = 0L;
            }
            return;
        }

        MEStorage networkInv = this.getMainNode().getGrid().getStorageService().getInventory();

        for (int i = 0; i < CONFIG_SIZE; i++) {
            ExportOnlyAEItemSlot aeSlot = this.aeItemHandler.getInventory()[i];

            GenericStack exceedItem = aeSlot.exceedStack();
            if (exceedItem != null) {
                long inserted = networkInv.insert(exceedItem.what(), exceedItem.amount(), Actionable.MODULATE,
                        this.actionSource);
                if (inserted > 0) aeSlot.extractItem(0, GTMath.saturatedCast(inserted), false);
            }

            GenericStack reqItem = aeSlot.requestStack();
            if (reqItem != null && reqItem.what() instanceof AEItemKey key) {
                if (isAllowed(key)) {
                    long extracted = networkInv.extract(reqItem.what(), reqItem.amount(), Actionable.MODULATE,
                            this.actionSource);
                    if (extracted != 0) aeSlot.addStack(new GenericStack(reqItem.what(), extracted));
                }
            }

            GenericStack stock = aeSlot.getStock();
            GenericStack config = aeSlot.getConfig();

            if (stock != null && stock.what() instanceof AEItemKey itemKey) {
                previewStacks[i] = itemKey.toStack(1);
                previewAmounts[i] = stock.amount();
            } else if (config != null && config.what() instanceof AEItemKey configKey) {
                previewStacks[i] = configKey.toStack(1);
                previewAmounts[i] = 0L;
            } else {
                previewStacks[i] = ItemStack.EMPTY;
                previewAmounts[i] = 0L;
            }
        }
    }

    protected boolean isAllowed(AEItemKey key) {
        if (whitelistExpr.isBlank() && blacklistExpr.isBlank()) return false;
        if (!blacklistExpr.isBlank() && TagMatcher.doesItemMatch(key, blacklistExpr)) return false;
        if (!whitelistExpr.isBlank()) return TagMatcher.doesItemMatch(key, whitelistExpr);
        return true;
    }

    protected void updateConfigurationFromTags() {
        if (isRemote() || getMainNode().getGrid() == null) return;

        var storage = getMainNode().getGrid().getStorageService().getInventory();
        var availableInNetwork = storage.getAvailableStacks();
        boolean changed = false;

        java.util.Set<AEItemKey> alreadyConfigured = new java.util.HashSet<>();
        for (int i = 0; i < CONFIG_SIZE; i++) {
            var slot = this.aeItemHandler.getInventory()[i];
            GenericStack config = slot.getConfig();

            if (config != null && config.what() instanceof AEItemKey key) {
                boolean stillExists = availableInNetwork.get(key) > 0;

                if (isAllowed(key) && stillExists) {
                    alreadyConfigured.add(key);
                } else {
                    slot.setConfig(null);
                    previewStacks[i] = ItemStack.EMPTY;
                    previewAmounts[i] = 0L;
                    changed = true;
                }
            }
        }

        for (var entry : availableInNetwork) {
            if (entry.getKey() instanceof AEItemKey itemKey && isAllowed(itemKey)) {
                if (!alreadyConfigured.contains(itemKey)) {
                    for (int i = 0; i < CONFIG_SIZE; i++) {
                        var slot = this.aeItemHandler.getInventory()[i];
                        if (slot.getConfig() == null) {
                            slot.setConfig(new GenericStack(itemKey, Integer.MAX_VALUE));
                            alreadyConfigured.add(itemKey);
                            changed = true;
                            break;
                        }
                    }
                }
            }
        }

        if (changed) {
            notifyUpdate();
        }
    }

    private void notifyUpdate() {
        this.updateInventorySubscription();
        self().setChanged();
    }

    /////////////////////////////////
    // ************ UI ************//
    /////////////////////////////////

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

        flow.child(Text.str("Item Preview (Read Only)").asWidget().marginBottom(2));

        Grid previewGrid = new Grid()
                .coverChildren()
                .gridOfSizeWidth(8, 4,
                        (x, y, i) -> i < CONFIG_SIZE ? createPreviewSlot(i) : new Widget<>().size(18, 18));
        flow.child(previewGrid);

        mainWidget.child(flow.center());
    }

    private Widget<?> createPreviewSlot(int index) {
        return new ItemPreviewWidget(index, this).size(18, 18);
    }

    /**
     * Read-only widget rendering an item preview and its (possibly large) network-backed amount.
     */
    private static class ItemPreviewWidget extends Widget<ItemPreviewWidget> {

        private final int index;
        private final METagInputBusPartMachine machine;

        public ItemPreviewWidget(int index, METagInputBusPartMachine machine) {
            this.index = index;
            this.machine = machine;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            var graphics = context.getGraphics();
            GTGuiTextures.SLOT.draw(context, 0, 0, 18, 18);

            ItemStack stack = machine.previewStacks[index];
            if (stack != null && !stack.isEmpty()) {
                graphics.renderItem(stack, 1, 1);

                long amount = machine.previewAmounts[index];
                if (amount > 0) {
                    var font = net.minecraft.client.Minecraft.getInstance().font;
                    String amountStr = formatCompact(amount);
                    graphics.pose().pushPose();
                    graphics.pose().translate(17.0, 17.0, 0.0);
                    graphics.pose().scale(0.5f, 0.5f, 1.0f);
                    int textWidth = font.width(amountStr);
                    graphics.drawString(font, amountStr, -textWidth, -8, 0xFFFFFFFF, true);
                    graphics.pose().popPose();
                }
            }
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void drawForeground(ModularGuiContext context) {
            if (isHovering()) {
                ItemStack stack = machine.previewStacks[index];
                if (stack != null && !stack.isEmpty()) {
                    context.getGraphics().renderTooltip(net.minecraft.client.Minecraft.getInstance().font, stack,
                            context.getMouseX(), context.getMouseY());
                }
            }
        }

        private static String formatCompact(long amount) {
            if (amount < 1000) return Long.toString(amount);
            String[] suffixes = { "", "K", "M", "B", "T" };
            int magnitude = (int) (Math.log10(amount) / 3);
            magnitude = Math.min(magnitude, suffixes.length - 1);
            double shortened = amount / Math.pow(1000, magnitude);
            return String.format("%.1f%s", shortened, suffixes[magnitude]);
        }
    }

    /////////////////////////////////
    // ******* Data Stick *********//
    /////////////////////////////////

    @Override
    public final InteractionResult onDataStickShiftUse(Player player, ItemStack dataStick) {
        if (!isRemote()) {
            CompoundTag tag = new CompoundTag();
            tag.put("METagInputBus", writeConfigToTag());
            dataStick.setTag(tag);

            dataStick.setHoverName(Component.literal("ME Tag Input Bus Configuration Data")
                    .withStyle(style -> style.withItalic(true)));

            player.sendSystemMessage(Component.literal("Settings Copied: " + whitelistExpr));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public final InteractionResult onDataStickUse(Player player, ItemStack dataStick) {
        if (!dataStick.hasTag() || !dataStick.getTag().contains("METagInputBus")) {
            return InteractionResult.PASS;
        }

        if (!isRemote()) {
            readConfigFromTag(dataStick.getTag().getCompound("METagInputBus"));
            updateConfigurationFromTags();
            player.sendSystemMessage(Component.literal("Settings Pasted successfully."));
        }
        return InteractionResult.sidedSuccess(isRemote());
    }

    protected CompoundTag writeConfigToTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("WhitelistExpr", whitelistExpr);
        tag.putString("BlacklistExpr", blacklistExpr);
        return tag;
    }

    protected void readConfigFromTag(CompoundTag tag) {
        this.whitelistExpr = tag.getString("WhitelistExpr");
        this.blacklistExpr = tag.getString("BlacklistExpr");
        updateConfigurationFromTags();
    }
}
