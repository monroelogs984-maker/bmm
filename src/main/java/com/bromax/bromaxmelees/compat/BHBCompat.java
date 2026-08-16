package com.bromax.bromaxmelees.compat;

import com.bromax.bromaxmelees.item.BMMItems;
import com.bromax.bromaxmelees.weapon.WeaponCategory;
import com.bromax.bromaxmelees.weapon.WeaponProfile;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.Item;

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
            if (entry == null || !entry.iron.isPresent()) continue;
            RegistryObject<? extends Item> obj = entry.iron;

            WeaponProfile profile = WeaponProfile.get(category);

            // TODO: replace with actual BHB registration API call when BHB 1.19.2
            // exposes one. Placeholder call structure shown below:
            //
            // int x = profile.hasVariantData() ? profile.variantX : 70;
            // float y = profile.hasVariantData() ? profile.variantY : 0.8f;
            // BHBWeaponRegistry.register(obj.get(), category.id(), x, y);
        }
    }

    private BHBCompat() {}
}
