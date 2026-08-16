package com.bromax.bromaxmelees.client;

import com.bromax.bromaxmelees.BromaxMelees;
import com.bromax.bromaxmelees.client.screen.WeaponryRackScreen;
import com.bromax.bromaxmelees.item.BMMMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = BromaxMelees.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BMMClientSetup {

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(BMMMenuTypes.WEAPONRY_RACK.get(), WeaponryRackScreen::new);
    }

    private BMMClientSetup() {}
}
