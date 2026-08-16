package com.bromax.bromaxmelees.compat;

import com.bromax.bromaxmelees.item.BMMItems;
import com.bromax.bromaxmelees.weapon.WeaponCategory;
import com.bromax.bromaxmelees.weapon.WeaponProfile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.List;

/**
 * Soft-depend BHB integration.
 * Called during mod init ONLY when BHB is loaded. Registers each weapon's
 * X/Y attack variant profile and category with BHB.
 *
 * Do not reference BHB classes anywhere outside this file — it must stay
 * safely unloaded when BHB is absent.
 *
 * The three BMM-only additions (Greathammer, Sai, Twinblades) have no X/Y
 * data yet (variantX = -1). They are registered with default values until
 * a proper BHB profile is authored.
 */
public final class BHBCompat {

    private static final String BHB_MOD_ID = "bromax_battle";

    public static boolean isPresent() {
        return ModList.get().isLoaded(BHB_MOD_ID);
    }

    public static void register() {
        for (WeaponCategory category : WeaponCategory.values()) {
            var entry = BMMItems.byCategory(category);
            if (entry == null) continue;

            WeaponProfile profile = WeaponProfile.get(category);

            // TODO: replace with actual BHB registration API call once the BHB
            // 1.21.1 NeoForge port exposes one. Placeholder call structure:
            //
            // int x = profile.hasVariantData() ? profile.variantX : 70;
            // float y = profile.hasVariantData() ? profile.variantY : 0.8f;
            // BHBWeaponRegistry.register(entry.iron.get(), category.id(), x, y);
        }
    }

    /**
     * Returns BHB's live profile for any weapon stack. Only call after isPresent() == true.
     * BHB classes are referenced only inside this method body so they load lazily — safe
     * when BHB is absent because this method is never invoked in that case.
     */
    public static BHBProfile getProfile(ItemStack stack) {
        var attrs = com.bromax.bromaxbattle.weapon.WeaponRegistry.INSTANCE.getAttributes(stack);
        if (attrs == null || attrs.attacks.isEmpty()) return null;
        List<BHBProfile.Attack> attacks = attrs.attacks.stream()
                .map(a -> new BHBProfile.Attack(a.weight, a.speedMultiplier, a.damageMultiplier, a.hitDelay))
                .toList();
        String categoryName = attrs.category.name().toLowerCase().replace('_', ' ');
        return new BHBProfile(categoryName, attacks, attrs.totalWeight);
    }

    private BHBCompat() {}
}
