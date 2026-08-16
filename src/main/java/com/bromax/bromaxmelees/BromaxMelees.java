package com.bromax.bromaxmelees;

import com.bromax.bromaxmelees.compat.BHBCompat;
import com.bromax.bromaxmelees.item.BMMBlockEntities;
import com.bromax.bromaxmelees.item.BMMBlocks;
import com.bromax.bromaxmelees.item.BMMCreativeTab;
import com.bromax.bromaxmelees.item.BMMItems;
import com.bromax.bromaxmelees.item.BMMMenuTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(BromaxMelees.MOD_ID)
public final class BromaxMelees {

    public static final String MOD_ID = "bromax_melees";

    public BromaxMelees(IEventBus modBus) {
        BMMBlocks.BLOCKS.register(modBus);
        BMMBlockEntities.BLOCK_ENTITIES.register(modBus);
        BMMMenuTypes.MENUS.register(modBus);
        BMMItems.ITEMS.register(modBus);
        BMMCreativeTab.TABS.register(modBus);
        modBus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        if (BHBCompat.isPresent()) {
            BHBCompat.register();
        }
    }
}
