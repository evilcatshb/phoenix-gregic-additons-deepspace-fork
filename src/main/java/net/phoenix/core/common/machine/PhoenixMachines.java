package net.phoenix.core.common.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.multiblock.pattern.MultiblockPatternBuilder;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.common.data.machines.GTResearchMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;
import com.gregtechceu.gtceu.data.lang.LangHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.phoenix.core.PhoenixGregicAdditons;
import net.phoenix.core.common.block.PhoenixBlocks;
import net.phoenix.core.common.machine.multiblock.BlazingCleanroom;
import net.phoenix.core.common.machine.multiblock.electric.research.PhoenixHPCAMachine;
import net.phoenix.core.configs.PhoenixConfigs;

import java.util.Locale;
import java.util.function.BiFunction;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.multiblock.Predicates.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static net.phoenix.core.common.registry.PhoenixRegistration.REGISTRATE;
import static net.phoenix.core.configs.PhoenixConfigs.INSTANCE;

@SuppressWarnings("all")
public class PhoenixMachines {

    public static MultiblockMachineDefinition DANCE = null;
    public static MachineDefinition BLAZING_CLEANING_MAINTENANCE_HATCH = null;
    public static MachineDefinition HIGH_YEILD_PHOTON_EMISSION_REGULATER = null;
    static {
        REGISTRATE.creativeModeTab(() -> PhoenixGregicAdditons.PHOENIX_CREATIVE_TAB);
    }

    static {
        if (PhoenixConfigs.INSTANCE.features.blazingHatchEnabled) {
            BLAZING_CLEANING_MAINTENANCE_HATCH = REGISTRATE
                    .machine("blazing_cleaning_maintenance_hatch",
                            holder -> new CleaningMaintenanceHatchPartMachine(holder,
                                    BlazingCleanroom.BLAZING_CLEANROOM))
                    .langValue("§cBlazing Cleaning Maintenance Hatch")
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.MAINTENANCE)
                    .tooltips(Component.translatable("gtceu.part_sharing.disabled"),
                            Component.translatable("gtceu.machine.maintenance_hatch_cleanroom_auto.tooltip.0"),
                            Component.translatable("gtceu.machine.maintenance_hatch_cleanroom_auto.tooltip.1"))
                    .tooltipBuilder((stack, tooltips) -> tooltips.add(Component.literal("  ").append(Component
                            .translatable(BlazingCleanroom.BLAZING_CLEANROOM.getTranslationKey())
                            .withStyle(ChatFormatting.RED))))
                    .tier(UHV)

                    .overlayTieredHullModel(
                            PhoenixGregicAdditons.id("block/machine/part/overlay_maintenance_blazing_cleaning"))
                    // Tier can always be changed later
                    .register();
        }
    }

    public static MachineDefinition[] registerTieredMachines(String name,
                                                             BiFunction<BlockEntityCreationInfo, Integer, MetaMachine> factory,
                                                             BiFunction<Integer, MachineBuilder<MachineDefinition, MetaMachine, ?>, MachineDefinition> builder,
                                                             int... tiers) {
        MachineDefinition[] definitions = new MachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = REGISTRATE
                    .machine(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + name,
                            holder -> factory.apply(holder, tier))
                    .tier(tier);
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    static {
        if (PhoenixConfigs.INSTANCE.features.PHPCAEnabled) {
            // 2. Mova toda a lógica de registro para dentro do método init()
            HIGH_YEILD_PHOTON_EMISSION_REGULATER = REGISTRATE
                    .multiblock("high_yield_photon_emission_regulator", PhoenixHPCAMachine::new)
                    .langValue("§dHigh Yield Photon Emission Regulator (HPCA)")
                    .tooltips(Component.translatable("phoenix_gregic_additions.tooltip.hyper_machine_purpose",
                            GTMaterials.get(INSTANCE.features.ActiveCoolerCoolantBase).getLocalizedName()
                                    .withStyle(style -> style.withColor(TextColor.fromRgb(GTMaterials
                                            .get(INSTANCE.features.ActiveCoolerCoolantBase).getMaterialARGB()))),
                            GTMaterials.get(INSTANCE.features.ActiveCoolerCoolant1).getLocalizedName()
                                    .withStyle(style -> style.withColor(TextColor.fromRgb(GTMaterials
                                            .get(INSTANCE.features.ActiveCoolerCoolant1).getMaterialARGB()))),
                            GTMaterials.get(INSTANCE.features.ActiveCoolerCoolant2).getLocalizedName()
                                    .withStyle(style -> style.withColor(TextColor.fromRgb(GTMaterials
                                            .get(INSTANCE.features.ActiveCoolerCoolant2).getMaterialARGB())))),
                            Component.translatable("phoenix_gregic_additions.tooltip.hyper_machine_1"),
                            Component
                                    .translatable("phoenix_gregic_additions.tooltip.hyper_machine_coolant_base",
                                            GTMaterials.get(INSTANCE.features.ActiveCoolerCoolantBase)
                                                    .getLocalizedName(),
                                            INSTANCE.features.BaseCoolantBoost)
                                    .withStyle(style -> style.withColor(TextColor.fromRgb(GTMaterials
                                            .get(INSTANCE.features.ActiveCoolerCoolantBase).getMaterialARGB()))),
                            Component.translatable("phoenix_gregic_additions.tooltip.hyper_machine_coolant2",
                                    GTMaterials.get(INSTANCE.features.ActiveCoolerCoolant1).getLocalizedName(),
                                    INSTANCE.features.CoolantBoost1)
                                    .withStyle(style -> style.withColor(TextColor.fromRgb(GTMaterials
                                            .get(INSTANCE.features.ActiveCoolerCoolant1).getMaterialARGB()))),
                            Component
                                    .translatable("phoenix_gregic_additions.tooltip.hyper_machine_coolant3",
                                            GTMaterials.get(INSTANCE.features.ActiveCoolerCoolant2).getLocalizedName(),
                                            INSTANCE.features.CoolantBoost2)
                                    .withStyle(style -> style.withColor(TextColor.fromRgb(GTMaterials
                                            .get(INSTANCE.features.ActiveCoolerCoolant2).getMaterialARGB()))))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .appearanceBlock(PhoenixBlocks.ADVANCED_PHOENIX_COMPUTER_CASING)
                    .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                    .tooltips(LangHandler.getMultiLang("gtceu.machine.high_performance_computation_array.tooltip"))
                    .pattern(definition -> MultiblockPatternBuilder.start()
                            .slice("BBBBCCCBBBB", "CDDCCCCCDDC", "CCDCCCCCDCC", "CCCCCCCCCCC", "CCCCCCCCCCC",
                                    "CCCCCCCCCCC", "CCCCCCCCCCC", "CCCCCCCCCCC", "CCCCCCCCCCC", "CCCCCCCCCCC")
                            .slice("BEEBBBBBEEB", "DEEEFFFEEED", "DEEFGGGFEED", "DDDFGGGFDDD", "CBBFGGGFBBC",
                                    "CBBFGGGFBBC", "CCBFGGGFBCC", "CCBEFFFEBCC", "CCBBBBBBBCC", "CCBBBBBBBCC")
                            .slice("BBHHIIIHHBB", "CDDAAAAADDC", "CIDAAAAADIC", "CIAAAAAAAIC", "CIAAAAAAAIC",
                                    "CEAAAAAAAEC", "CEAAAAAAAEC", "CEAAAAAAAEC", "CEAAAAAAAEC", "CBBIIIIIBBC")
                            .slice("CBIIIIIIIBC", "CJAAAKAAAJC", "CJAAAKAAAJC", "CJAAAKAAAJC", "CFAAAKAAAFC",
                                    "CLAAAKAAALC", "CLAAAKAAALC", "CLAAAKAAALC", "CFAAAAAAAFC", "CBIIIIIIIBC")
                            .slice("CBIIIIIIIBC", "CJAAKMKAAJC", "CJAAKMKAAJC", "CJAAKMKAAJC", "CFAAKMKAAFC",
                                    "CLAAKMKAALC", "CLAAKMKAALC", "CLAAKMKAALC", "CFAAAAAAAFC", "CBIIIIIIIBC")
                            .slice("CBIIIIIIIBC", "CJAAAKAAAJC", "CJAAAKAAAJC", "CJAAAKAAAJC", "CFAAAKAAAFC",
                                    "CLAAAKAAALC", "CLAAAKAAALC", "CLAAAKAAALC", "CFAAAAAAAFC", "CBIIIIIIIBC")
                            .slice("BBHHIIIHHBB", "CDDAAAAADDC", "CIDAAAAADIC", "CIAAAAAAAIC", "CIAAAAAAAIC",
                                    "CEAAAAAAAEC", "CEAAAAAAAEC", "CEAAAAAAAEC", "CEAAAAAAAEC", "CBBIIIIIBBC")
                            .slice("BEEBBNBBEEB", "DEEEFFFEEED", "DEEOJJJOEED", "DDDOJJJODDD", "CBBOJJJOBBC",
                                    "CBBOJJJOBBC", "CCBBJJJBBCC", "CCBBJJJBBCC", "CCBBBBBBBCC", "CCBBBBBBBCC")
                            .slice("BBBBCCCBBBB", "CDDCCCCCDDC", "CCDCCCCCDCC", "CCCCCCCCCCC", "CCCCCCCCCCC",
                                    "CCCCCCCCCCC", "CCCCCCCCCCC", "CCCCCCCCCCC", "CCCCCCCCCCC", "CCCCCCCCCCC")
                            .where('A', air())
                            .where('B', blocks(PhoenixBlocks.SPACE_TIME_COOLED_ETERNITY_CASING.get()))
                            .where('C', any())
                            .where('D', blocks(PhoenixBlocks.AKASHIC_ZERONIUM_CASING.get()))
                            .where('E',
                                    blocks(PhoenixBlocks.ADVANCED_PHOENIX_COMPUTER_CASING.get()).setMinGlobalLimited(20)
                                            .or(abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                                                    .setMaxGlobalLimited(2, 1))
                                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1))
                                            .or(abilities(PartAbility.COMPUTATION_DATA_TRANSMISSION).setExactLimit(1))
                                            .or(autoAbilities(true, false, false)))
                            .where('F', blocks(PhoenixBlocks.PHOENIX_COMPUTER_HEAT_VENT.get()))
                            .where('G', blocks(PhoenixResearchMachines.ADVANCED_PHOENIX_COMPUTATION_COMPONENT.get())
                                    .or(blocks(PhoenixResearchMachines.ADVANCED_PHOENIX_COMPUTATION_COMPONENT.get()))
                                    .or(blocks(GTResearchMachines.HPCA_ADVANCED_COMPUTATION_COMPONENT.get()))
                                    .or(blocks(GTResearchMachines.HPCA_EMPTY_COMPONENT.get()))
                                    .or(blocks(GTResearchMachines.HPCA_COMPUTATION_COMPONENT.get())))
                            .where('H', blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                            .where('I', blocks(PhoenixBlocks.PHOENIX_COMPUTER_CASING.get()))
                            .where('J', blocks(FUSION_GLASS.get()))
                            .where('K', blocks(PhoenixBlocks.AKASHIC_COIL_BLOCK.get()))
                            .where('L', blocks(PhoenixResearchMachines.ACTIVE_PHOENIX_COOLER_COMPONENT.get())
                                    .or(blocks(PhoenixResearchMachines.PHOENIX_COOLER_COMPONENT.get()))
                                    .or(blocks(GTResearchMachines.HPCA_EMPTY_COMPONENT.get()))
                                    .or(blocks(GTResearchMachines.HPCA_BRIDGE_COMPONENT.get()))
                                    .or(blocks(GTResearchMachines.HPCA_ACTIVE_COOLER_COMPONENT.get()))
                                    .or(blocks(GTResearchMachines.HPCA_HEAT_SINK_COMPONENT.get())))
                            .where('M', blocks(PhoenixBlocks.PERFECTED_LOGIC.get()))
                            .where('N', controller(blocks(definition.getBlock())))
                            .where('O', blocks(GCYMBlocks.HEAT_VENT.get()))
                            .build())
                    /*
                     * .shapeInfos(definition -> {
                     * List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
                     * .ShapeInfoBuilder builder = MultiblockShapeInfo.builder()
                     * .slice("SA", "CC", "CC", "OC", "AA")
                     * .slice("VA", "8V", "5V", "2V", "VA")
                     * .slice("VA", "7V", "4V", "1V", "VA")
                     * .slice("VA", "6V", "3V", "0V", "VA")
                     * .slice("AA", "EC", "MC", "HC", "AA")
                     * .where('S', GTResearchMachines.HIGH_PERFORMANCE_COMPUTING_ARRAY, Direction.NORTH)
                     * .where('A', ADVANCED_COMPUTER_CASING)
                     * .where('V', COMPUTER_HEAT_VENT)
                     * .where('C', COMPUTER_CASING)
                     * .where('E', GTMachines.ENERGY_INPUT_HATCH[GTValues.LuV], Direction.SOUTH)
                     * .where('H', GTMachines.FLUID_IMPORT_HATCH[GTValues.LV], Direction.SOUTH)
                     * .where('O', GTResearchMachines.COMPUTATION_HATCH_TRANSMITTER, Direction.NORTH)
                     * .where('M', ConfigHolder.INSTANCE.machines.enableMaintenance ?
                     * GTMachines.MAINTENANCE_HATCH.defaultBlockState().setValue(
                     * GTMachines.MAINTENANCE_HATCH.get().getRotationState().property,
                     * Direction.SOUTH) :
                     * COMPUTER_CASING.getDefaultState());
                     *
                     * // a few example structures
                     * shapeInfo.add(builder.shallowCopy()
                     * .where('0', GTResearchMachines.HPCA_EMPTY_COMPONENT, Direction.WEST)
                     * .where('1', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('2', GTResearchMachines.HPCA_EMPTY_COMPONENT, Direction.WEST)
                     * .where('3', GTResearchMachines.HPCA_EMPTY_COMPONENT, Direction.WEST)
                     * .where('4', GTResearchMachines.HPCA_COMPUTATION_COMPONENT, Direction.WEST)
                     * .where('5', GTResearchMachines.HPCA_EMPTY_COMPONENT, Direction.WEST)
                     * .where('6', GTResearchMachines.HPCA_EMPTY_COMPONENT, Direction.WEST)
                     * .where('7', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('8', GTResearchMachines.HPCA_EMPTY_COMPONENT, Direction.WEST)
                     * .build());
                     *
                     * shapeInfo.add(builder.shallowCopy()
                     * .where('0', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('1', GTResearchMachines.HPCA_COMPUTATION_COMPONENT, Direction.WEST)
                     * .where('2', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('3', GTResearchMachines.HPCA_ACTIVE_COOLER_COMPONENT, Direction.WEST)
                     * .where('4', GTResearchMachines.HPCA_COMPUTATION_COMPONENT, Direction.WEST)
                     * .where('5', GTResearchMachines.HPCA_BRIDGE_COMPONENT, Direction.WEST)
                     * .where('6', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('7', GTResearchMachines.HPCA_COMPUTATION_COMPONENT, Direction.WEST)
                     * .where('8', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .build());
                     *
                     * shapeInfo.add(builder.shallowCopy()
                     * .where('0', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('1', GTResearchMachines.HPCA_COMPUTATION_COMPONENT, Direction.WEST)
                     * .where('2', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('3', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('4', GTResearchMachines.HPCA_ADVANCED_COMPUTATION_COMPONENT, Direction.WEST)
                     * .where('5', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('6', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('7', GTResearchMachines.HPCA_BRIDGE_COMPONENT, Direction.WEST)
                     * .where('8', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .build());
                     *
                     * shapeInfo.add(builder.shallowCopy()
                     * .where('0', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('1', GTResearchMachines.HPCA_ADVANCED_COMPUTATION_COMPONENT, Direction.WEST)
                     * .where('2', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('3', GTResearchMachines.HPCA_ACTIVE_COOLER_COMPONENT, Direction.WEST)
                     * .where('4', GTResearchMachines.HPCA_BRIDGE_COMPONENT, Direction.WEST)
                     * .where('5', GTResearchMachines.HPCA_ACTIVE_COOLER_COMPONENT, Direction.WEST)
                     * .where('6', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .where('7', GTResearchMachines.HPCA_ADVANCED_COMPUTATION_COMPONENT, Direction.WEST)
                     * .where('8', GTResearchMachines.HPCA_HEAT_SINK_COMPONENT, Direction.WEST)
                     * .build());
                     *
                     * return shapeInfo;
                     * })
                     */
                    .workableCasingModel(
                            PhoenixGregicAdditons
                                    .id("block/casings/advanced_computer_casing/phoenix_advanced_computer_casing"),
                            GTCEu.id("block/multiblock/hpca"))
                    .register();
        }
    }

    public static void init() {}
}
