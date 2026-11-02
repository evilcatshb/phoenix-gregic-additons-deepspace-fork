package net.phoenix.core.common.machine.multiblock.part.hpca;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.common.machine.multiblock.part.hpca.HPCAComponentPartMachine;
import com.gregtechceu.gtceu.common.machine.trait.hpca.HPCACoolantProviderTrait;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.phoenix.core.configs.PhoenixConfigs;

import javax.annotation.ParametersAreNonnullByDefault;

// Custom HPCA Cooler Part for the Phoenix mod.
// Handles both a standard and an advanced version via the advanced flag.
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PhoenixCoolerPartMachine extends HPCAComponentPartMachine {

    public PhoenixCoolerPartMachine(BlockEntityCreationInfo info, boolean advanced) {
        super(info, advanced,
                advanced ? GTGuiTextures.HPCA_ACTIVE_COOLER_COMPONENT : GTGuiTextures.HPCA_HEAT_SINK_COMPONENT,
                advanced ? GTGuiTextures.HPCA_ACTIVE_COOLER_COMPONENT : GTGuiTextures.HPCA_HEAT_SINK_COMPONENT,
                createTrait(advanced));
    }

    private static HPCACoolantProviderTrait createTrait(boolean advanced) {
        int upkeepEUt = advanced ? GTValues.VA[PhoenixConfigs.INSTANCE.features.ActiveCoolerEutUpkeep] :
                GTValues.VA[PhoenixConfigs.INSTANCE.features.HeatSinkEutUpkeep];
        int coolingAmount = advanced ? PhoenixConfigs.INSTANCE.features.ActiveCoolerStrength :
                PhoenixConfigs.INSTANCE.features.HeatSinkStrength;
        int maxCoolantPerTick = advanced ? PhoenixConfigs.INSTANCE.features.ActiveCoolerCoolantUse : 0;
        return new HPCACoolantProviderTrait(upkeepEUt, upkeepEUt, false, false, coolingAmount, maxCoolantPerTick,
                advanced);
    }
}
