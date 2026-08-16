package com.bromax.bromaxmelees.item;

import net.minecraftforge.registries.RegistryObject;

/**
 * Groups all tier variants of one weapon category together.
 * Access tiers via the public fields or via get(BMMTier).
 */
public final class WeaponEntry {

    public final RegistryObject<BMMWeapon> iron;
    public final RegistryObject<BMMWeapon> diamond;
    public final RegistryObject<BMMWeapon> netherite;

    WeaponEntry(RegistryObject<BMMWeapon> iron,
                RegistryObject<BMMWeapon> diamond,
                RegistryObject<BMMWeapon> netherite) {
        this.iron      = iron;
        this.diamond   = diamond;
        this.netherite = netherite;
    }

    public RegistryObject<BMMWeapon> get(BMMTier tier) {
        return switch (tier) {
            case IRON      -> iron;
            case DIAMOND   -> diamond;
            case NETHERITE -> netherite;
        };
    }
}
