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
public class BasicPhoenixComputationPartMachine extends HPCAComponentPartMachine {

    public BasicPhoenixComputationPartMachine(BlockEntityCreationInfo info) {
        super(info, false,
                GTGuiTextures.HPCA_ADVANCED_COMPUTATION_COMPONENT,
                GTGuiTextures.HPCA_DAMAGED_COMPUTATION_COMPONENT,
                new PhoenixComputationTrait(
                        GTValues.VA[PhoenixConfigs.INSTANCE.features.basicPCUEutUpkeep],
                        GTValues.VA[PhoenixConfigs.INSTANCE.features.basicPCUMaxEUt],
                        PhoenixConfigs.INSTANCE.features.BasicPCUStrength,
                        PhoenixConfigs.INSTANCE.features.BasicPCUCoolantUsed,
                        PhoenixConfigs.INSTANCE.features.damagedBasicPCUStrength));
    }
}
