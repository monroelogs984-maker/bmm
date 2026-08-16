package com.bromax.bromaxmelees.client;

import com.bromax.bromaxmelees.BromaxMelees;
import com.bromax.bromaxmelees.weapon.IWeaponCategoryItem;
import com.bromax.bromaxmelees.weapon.WeaponProfile;
import com.bromax.bromaxmelees.weapon.WeaponTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = BromaxMelees.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class BMMClientEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof IWeaponCategoryItem weapon)) return;

        WeaponProfile profile = WeaponProfile.get(weapon.getWeaponCategory());
        List<Component> tooltip = event.getToolTip();

        // Find the last attack stat line and insert traits immediately after it
        int insertAt = -1;
        for (int i = 0; i < tooltip.size(); i++) {
            String text = tooltip.get(i).getString();
            if (text.contains("Attack Damage") || text.contains("Attack Speed")) {
                insertAt = i + 1;
            }
        }
        if (insertAt < 0) insertAt = tooltip.size();

        for (WeaponTrait.Instance t : profile.traits) {
            tooltip.add(insertAt++, Component.literal(traitDisplayName(t)).withStyle(ChatFormatting.AQUA));
        }
    }

    private static String traitDisplayName(WeaponTrait.Instance t) {
        String name = switch (t.trait()) {
            case AP -> "Armor Piercing";
            case TH -> "Two-Handed";
            case HS -> "Hard Strike";
            case R  -> "Rageful";
        };
        return name + " " + roman(t.level());
    }

    private static String roman(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> String.valueOf(level);
        };
    }

    private BMMClientEvents() {}
}
