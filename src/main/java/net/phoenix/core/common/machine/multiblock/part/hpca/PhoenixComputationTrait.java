package net.phoenix.core.common.machine.multiblock.part.hpca;

import com.gregtechceu.gtceu.common.machine.trait.hpca.HPCAComputationProviderTrait;

/**
 * Custom computation trait that, unlike the vanilla trait, keeps providing a
 * reduced (rather than zero) CWU/t output while damaged, and does not reduce
 * cooling demand while damaged. Mirrors the pre-8.0.0 Phoenix behavior.
 */
public class PhoenixComputationTrait extends HPCAComputationProviderTrait {

    private final int normalCWUPerTick;
    private final int damagedCWUPerTick;
    private final int coolingPerTickValue;

    public PhoenixComputationTrait(int upkeepEUt, int maxEUt, int cwuPerTick, int coolingPerTick,
                                   int damagedCWUPerTick) {
        super(upkeepEUt, maxEUt, true, false, cwuPerTick, coolingPerTick);
        this.normalCWUPerTick = cwuPerTick;
        this.damagedCWUPerTick = damagedCWUPerTick;
        this.coolingPerTickValue = coolingPerTick;
    }

    @Override
    public int getCWUPerTick() {
        return isDamaged() ? damagedCWUPerTick : normalCWUPerTick;
    }

    @Override
    public int getCoolingPerTick() {
        return coolingPerTickValue;
    }
}
