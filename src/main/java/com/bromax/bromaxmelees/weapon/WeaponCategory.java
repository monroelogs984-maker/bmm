package com.bromax.bromaxmelees.weapon;

/**
 * All 37 melee weapon categories in BMM. Ranged weapons are intentionally excluded.
 *
 * Each category carries its WeaponGroup so compat addons can filter by family
 * without listing every category manually.
 */
public enum WeaponCategory {

    // ---- Swords ----
    SHORTSWORD         (WeaponGroup.SWORD),
    LONGSWORD          (WeaponGroup.SWORD),
    BROADSWORD         (WeaponGroup.SWORD),
    SABRE              (WeaponGroup.SWORD),
    RAPIER             (WeaponGroup.SWORD),
    GREATSWORD         (WeaponGroup.SWORD),
    ZWEIHANDER         (WeaponGroup.SWORD),
    KATANA             (WeaponGroup.SWORD),
    FLAMBERGE          (WeaponGroup.SWORD),
    EXECUTIONERS_SWORD (WeaponGroup.SWORD),
    FALCHION           (WeaponGroup.SWORD),

    // ---- Knives ----
    DAGGER             (WeaponGroup.KNIFE),
    HUNTERS_KNIFE      (WeaponGroup.KNIFE),

    // ---- Polearms ----
    SPEAR              (WeaponGroup.POLEARM),
    TRIDENT            (WeaponGroup.POLEARM),
    HALBERD            (WeaponGroup.POLEARM),
    GLAIVE             (WeaponGroup.POLEARM),
    LANCE              (WeaponGroup.POLEARM),

    // ---- Blunt ----
    MACE               (WeaponGroup.BLUNT),
    WARHAMMER          (WeaponGroup.BLUNT),
    HAMMER             (WeaponGroup.BLUNT),
    FLAIL              (WeaponGroup.BLUNT),
    GREATHAMMER        (WeaponGroup.BLUNT),

    // ---- Axes ----
    HANDAXE            (WeaponGroup.AXE),
    BATTLEAXE          (WeaponGroup.AXE),
    CLEAVER            (WeaponGroup.AXE),
    LUMBERAXE          (WeaponGroup.AXE),

    // ---- Other melee ----
    SCYTHE             (WeaponGroup.OTHER),
    SICKLE             (WeaponGroup.OTHER),
    GAUNTLETS          (WeaponGroup.OTHER),
    CLAW               (WeaponGroup.OTHER),
    NUNCHAKU           (WeaponGroup.OTHER),
    QUARTERSTAFF       (WeaponGroup.OTHER),
    WHIP               (WeaponGroup.OTHER),
    STAFF              (WeaponGroup.OTHER),

    // ---- BMM 1.19.2+ additions ----
    SAI                (WeaponGroup.KNIFE),
    TWINBLADES         (WeaponGroup.SWORD);

    private final WeaponGroup group;

    WeaponCategory(WeaponGroup group) {
        this.group = group;
    }

    public WeaponGroup group() {
        return group;
    }

    public String id() {
        return name().toLowerCase();
    }
}
