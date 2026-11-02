package net.phoenix.core.common.data.materials;

import com.gregtechceu.gtceu.api.data.chemical.Element;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.resources.ResourceLocation;
import net.phoenix.core.PhoenixGregicAdditons;

public class PhoenixElements {

    public static final Element APNS = createAndRegister("almost_pure_nevvonian_steel", 35, 450, -1.0, null,
            "Almost Pure Nevvonian Steel", "", false);

    public static Element createAndRegister(String id, long protons, long neutrons, double halfLifeSeconds,
                                            String decayTo, String name, String symbol, boolean isIsotope) {
        ResourceLocation registryId = PhoenixGregicAdditons.id(id);
        Element element = new Element(protons, neutrons, halfLifeSeconds, decayTo, name, symbol, isIsotope);
        GTRegistries.ELEMENTS.register(registryId, element);
        return element;
    }

    public static void init() {}
}
