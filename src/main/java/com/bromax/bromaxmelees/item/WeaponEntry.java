package com.bromax.bromaxmelees.item;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.Item;

/**
 * Groups all tier variants of one weapon category together.
 * Access tiers via the public fields or via get(BMMTier).
 */
public final class WeaponEntry {

    public final DeferredHolder<Item, BMMWeapon> iron;
    public final DeferredHolder<Item, BMMWeapon> gold;
    public final DeferredHolder<Item, BMMWeapon> diamond;
    public final DeferredHolder<Item, BMMWeapon> netherite;

    WeaponEntry(DeferredHolder<Item, BMMWeapon> iron,
                DeferredHolder<Item, BMMWeapon> gold,
                DeferredHolder<Item, BMMWeapon> diamond,
                DeferredHolder<Item, BMMWeapon> netherite) {
        this.iron      = iron;
        this.gold      = gold;
        this.diamond   = diamond;
        this.netherite = netherite;
    }

    public DeferredHolder<Item, BMMWeapon> get(BMMTier tier) {
        return switch (tier) {
            case IRON      -> iron;
            case GOLD      -> gold;
            case DIAMOND   -> diamond;
            case NETHERITE -> netherite;
        };
    }
}
