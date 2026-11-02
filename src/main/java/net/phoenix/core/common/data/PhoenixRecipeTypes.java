package net.phoenix.core.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

public class PhoenixRecipeTypes {

    public static GTRecipeType PLEASE;

    public static void init() {
        PLEASE = register("please", MULTIBLOCK)
                .setMaxIOSize(16, 1, 8, 4)
                .setSound(GTSoundEntries.FORGE_HAMMER)
                .setEUIO(IO.IN)
                .UI(b -> b
                        .setItemSlotsOverlay(IO.IN, 0, 15, GTGuiTextures.BOX_OVERLAY)
                        .setItemSlotsOverlay(IO.OUT, 0, 0, GTGuiTextures.BOX_OVERLAY)
                        .setProgressBar(GTGuiTextures.PROGRESS_COMPRESS));
    }
}
