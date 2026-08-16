package com.bromax.bromaxmelees;

import com.bromax.bromaxmelees.compat.BHBCompat;
import com.bromax.bromaxmelees.item.BMMItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("bromax_melees")
public final class BromaxMelees {

    public static final String MOD_ID = "bromax_melees";

    public BromaxMelees() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BMMItems.ITEMS.register(bus);
        bus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        if (BHBCompat.isPresent()) {
            BHBCompat.register();
        }
    }
}
