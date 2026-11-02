package net.phoenix.core.common.machine.multiblock.part.hpca;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.common.machine.multiblock.part.hpca.HPCAComponentPartMachine;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.phoenix.core.configs.PhoenixConfigs;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A custom HPCA Computation Part for the Phoenix mod.
 * This part is designed to be a high-end component, offering
 * increased computation power (CWU) but also requiring more
 * energy and cooling.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PhoenixComputationPartMachine extends HPCAComponentPartMachine {

    public PhoenixComputationPartMachine(BlockEntityCreationInfo info) {
        super(info, true,
                GTGuiTextures.HPCA_ADVANCED_COMPUTATION_COMPONENT,
                GTGuiTextures.HPCA_DAMAGED_ADVANCED_COMPUTATION_COMPONENT,
                new PhoenixComputationTrait(
                        GTValues.VA[PhoenixConfigs.INSTANCE.features.PCUEutUpkeep],
                        GTValues.VA[PhoenixConfigs.INSTANCE.features.PCUMaxEUt],
                        PhoenixConfigs.INSTANCE.features.PCUStrength,
                        PhoenixConfigs.INSTANCE.features.PCUCoolantUsed,
                        PhoenixConfigs.INSTANCE.features.damagedPCUStrength));
    }
}
