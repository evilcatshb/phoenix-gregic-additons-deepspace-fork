package net.phoenix.core.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;

import org.jetbrains.annotations.NotNull;

public class BlazingCleanroom extends CleanroomType {

    // This public static final instance ensures the constructor is called
    // when the class is loaded, which registers the new cleanroom type
    // with GTCEu's CleanroomType system.
    public static final CleanroomType BLAZING_CLEANROOM = new CleanroomType(
            "blazing_cleanroom",
            2,
            "gtceu.recipe.cleanroom_blazing.display_name");

    /**
     * Constructs a new CleanroomType instance.
     *
     * @param name           The unique name of the cleanroom type.
     * @param tier           The tier of the cleanroom type.
     * @param translationKey The translation key for the cleanroom's display name.
     */
    public BlazingCleanroom(@NotNull String name, int tier, @NotNull String translationKey) {
        // The super() call triggers the registration logic in the parent class.
        super(name, tier, translationKey);
    }
}
