package net.phoenix.core.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.common.item.behavior.CoverPlaceBehavior;
import com.gregtechceu.gtceu.common.item.behavior.TooltipBehavior;
import com.gregtechceu.gtceu.data.lang.LangHandler;

import net.minecraft.network.chat.Component;
import net.phoenix.core.common.registry.PhoenixRegistration;
import net.phoenix.core.configs.PhoenixConfigs;

import com.tterrag.registrate.util.entry.ItemEntry;

import static net.phoenix.core.PhoenixGregicAdditons.PHOENIX_CREATIVE_TAB;
import static net.phoenix.core.common.registry.PhoenixRegistration.REGISTRATE;

public class PhoenixItems {

    static {
        PhoenixRegistration.REGISTRATE.creativeModeTab(() -> PHOENIX_CREATIVE_TAB);
    }

    // Define relative indices for all tiers to be registered
    private static final int UHV_RELATIVE_INDEX = GTValues.UHV - PhoenixCovers.START_TIER;
    private static final int UEV_RELATIVE_INDEX = GTValues.UEV - PhoenixCovers.START_TIER;
    private static final int UIV_RELATIVE_INDEX = GTValues.UIV - PhoenixCovers.START_TIER;
    private static final int UXV_RELATIVE_INDEX = GTValues.UXV - PhoenixCovers.START_TIER;
    private static final int OPV_RELATIVE_INDEX = GTValues.OpV - PhoenixCovers.START_TIER;
    private static final int MAX_RELATIVE_INDEX = GTValues.MAX - PhoenixCovers.START_TIER;

    static {
        // *** MODIFIED CONDITION: Add || GTCEu.isDataGen() ***
        if (PhoenixConfigs.INSTANCE.features.SolarsEnabled || GTCEu.isDataGen()) {

            // --- UHV Solar Panel (Index: UHV_RELATIVE_INDEX) ---
            final ItemEntry<ComponentItem> COVER_SOLAR_PANEL_UHV = REGISTRATE
                    .item("uhv_solar_panel", ComponentItem::create).lang("Ultra High Voltage Solar Panel")
                    .properties(p -> p.stacksTo(64))
                    .onRegister((c) -> c.attachComponents(
                            new CoverPlaceBehavior(PhoenixCovers.SOLAR_PANEL_TIERED[UHV_RELATIVE_INDEX]),
                            new TooltipBehavior(lines -> {
                                lines.addAll(LangHandler.getMultiLang("item.gtceu.solar_panel.tooltip"));
                                lines.add(Component.translatable("gtceu.universal.tooltip.voltage_out",
                                        GTValues.V[GTValues.UHV],
                                        GTValues.VNF[GTValues.UHV]));
                                lines.add(Component.literal("Textures made by Shugabrush"));
                            })))
                    .register();

            // --- UEV Solar Panel (Index: UEV_RELATIVE_INDEX) ---
            final ItemEntry<ComponentItem> COVER_SOLAR_PANEL_UEV = REGISTRATE
                    .item("uev_solar_panel", ComponentItem::create).lang("Ultra Excessive Voltage Solar Panel")
                    .properties(p -> p.stacksTo(64))
                    .onRegister((c) -> c.attachComponents(
                            new CoverPlaceBehavior(PhoenixCovers.SOLAR_PANEL_TIERED[UEV_RELATIVE_INDEX]),
                            new TooltipBehavior(lines -> {
                                lines.addAll(LangHandler.getMultiLang("item.gtceu.solar_panel.tooltip"));
                                lines.add(Component.translatable("gtceu.universal.tooltip.voltage_out",
                                        GTValues.V[GTValues.UEV],
                                        GTValues.VNF[GTValues.UEV]));
                                lines.add(Component.literal("Textures made by Shugabrush"));
                            })))
                    .register();

            // --- UIV Solar Panel (Index: UIV_RELATIVE_INDEX) ---
            final ItemEntry<ComponentItem> COVER_SOLAR_PANEL_UIV = REGISTRATE
                    .item("uiv_solar_panel", ComponentItem::create).lang("Ultra Immense Voltage Solar Panel")
                    .properties(p -> p.stacksTo(64))
                    .onRegister((c) -> c.attachComponents(
                            new CoverPlaceBehavior(PhoenixCovers.SOLAR_PANEL_TIERED[UIV_RELATIVE_INDEX]),
                            new TooltipBehavior(lines -> {
                                lines.addAll(LangHandler.getMultiLang("item.gtceu.solar_panel.tooltip"));
                                lines.add(Component.translatable("gtceu.universal.tooltip.voltage_out",
                                        GTValues.V[GTValues.UIV],
                                        GTValues.VNF[GTValues.UIV]));
                                lines.add(Component.literal("Textures made by Shugabrush"));
                            })))
                    .register();

            // --- UXV Solar Panel (Index: UXV_RELATIVE_INDEX) ---
            final ItemEntry<ComponentItem> COVER_SOLAR_PANEL_UXV = REGISTRATE
                    .item("uxv_solar_panel", ComponentItem::create).lang("Ultra Extreme Voltage Solar Panel")
                    .properties(p -> p.stacksTo(64))
                    .onRegister((c) -> c.attachComponents(
                            new CoverPlaceBehavior(PhoenixCovers.SOLAR_PANEL_TIERED[UXV_RELATIVE_INDEX]),
                            new TooltipBehavior(lines -> {
                                lines.addAll(LangHandler.getMultiLang("item.gtceu.solar_panel.tooltip"));
                                lines.add(Component.translatable("gtceu.universal.tooltip.voltage_out",
                                        GTValues.V[GTValues.UXV],
                                        GTValues.VNF[GTValues.UXV]));
                                lines.add(Component.literal("Textures made by Shugabrush"));
                            })))
                    .register();

            // --- OPV Solar Panel (Index: OPV_RELATIVE_INDEX) ---
            final ItemEntry<ComponentItem> COVER_SOLAR_PANEL_OPV = REGISTRATE
                    .item("opv_solar_panel", ComponentItem::create).lang("Overpowered Voltage Solar Panel")
                    .properties(p -> p.stacksTo(64))
                    .onRegister((c) -> c.attachComponents(
                            new CoverPlaceBehavior(PhoenixCovers.SOLAR_PANEL_TIERED[OPV_RELATIVE_INDEX]),
                            new TooltipBehavior(lines -> {
                                lines.addAll(LangHandler.getMultiLang("item.gtceu.solar_panel.tooltip"));
                                lines.add(Component.translatable("gtceu.universal.tooltip.voltage_out",
                                        GTValues.V[GTValues.OpV],
                                        GTValues.VNF[GTValues.OpV]));
                                lines.add(Component.literal("Textures made by Shugabrush"));
                            })))
                    .register();

            // --- MAX Solar Panel (Index: MAX_RELATIVE_INDEX) ---
            final ItemEntry<ComponentItem> COVER_SOLAR_PANEL_MAX = REGISTRATE
                    .item("max_solar_panel", ComponentItem::create).lang("Maximum Voltage Solar Panel")
                    .properties(p -> p.stacksTo(64))
                    .onRegister((c) -> c.attachComponents(
                            new CoverPlaceBehavior(PhoenixCovers.SOLAR_PANEL_TIERED[MAX_RELATIVE_INDEX]),
                            new TooltipBehavior(lines -> {
                                lines.addAll(LangHandler.getMultiLang("item.gtceu.solar_panel.tooltip"));
                                lines.add(Component.translatable("gtceu.universal.tooltip.voltage_out",
                                        GTValues.V[GTValues.MAX],
                                        GTValues.VNF[GTValues.MAX]));
                                lines.add(Component.literal("Textures made by Shugabrush"));
                            })))
                    .register();
        }
    }

    public static void init() {}
}
