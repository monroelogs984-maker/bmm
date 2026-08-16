package com.bromax.bromaxmelees.weapon;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Complete weapon data for every category, iron tier baseline.
 *
 * -- Base stats --
 * DPS budget (constant-DPS formula: effective damage = damage × dpsModifier):
 *   `damage` = Budget-2 baseline (9.6 / speed, truncated to 1 dp)
 *   `dpsModifier` is applied at runtime in BMMWeapon.buildModifiers()
 *
 * Budget assignment — based on SUM of all trait levels:
 *   sum = 1 → dpsModifier = 1.1  (+10% DPS — focused, low-complexity weapon)
 *   sum = 2 → dpsModifier = 1.0  (baseline)
 *   sum = 3 → dpsModifier = 0.9  (−10% DPS — high trait power compensates)
 *
 * Examples: TH3 (sum=3) = TH2+AP1 (sum=3) = TH1+HS1+AP1 (sum=3) → all 0.9
 *
 * -- BHB attack variant data (X, Y) --
 * Consumed by BHBCompat when BHB is present.
 *   X  = default attack weight (0–100); heavy/light each get (100−X)/2
 *   Y  = rare deviation multiplier; heavy = Y× speed / (2−Y)× dmg; light = (2−Y)× speed / Y× dmg
 *   variantX = -1 signals "no variant data" (BMM-only weapons not yet profiled for BHB)
 *
 * -- Traits --
 * AP = Armor Piercing (ignore % of armor)
 * TH = Two-Handed (bonus dmg with empty offhand)
 * HS = Hard Strike (movement speed reduction on hit — NOT a potion effect)
 * R  = Rageful (execute if remaining HP < threshold × weapon damage)
 */
public final class WeaponProfile {

    public final WeaponCategory category;
    /** Budget-2 baseline damage (9.6 / speed, truncated to 1 dp). */
    public final float damage;
    /** DPS budget multiplier: 1.1 (trait sum=1), 1.0 (sum=2), or 0.9 (sum=3). Applied at runtime. */
    public final float dpsModifier;
    public final float speed;
    public final int durability;
    public final List<WeaponTrait.Instance> traits;
    public final int variantX;
    public final float variantY;

    private WeaponProfile(WeaponCategory category,
                          float damage, float speed, int durability, float dpsModifier,
                          int variantX, float variantY,
                          WeaponTrait.Instance... traits) {
        this.category = category;
        this.damage = damage;
        this.speed = speed;
        this.durability = durability;
        this.dpsModifier = dpsModifier;
        this.variantX = variantX;
        this.variantY = variantY;
        this.traits = Collections.unmodifiableList(Arrays.asList(traits));
    }

    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------

    private static final Map<WeaponCategory, WeaponProfile> BY_CATEGORY = new EnumMap<>(WeaponCategory.class);

    static {
        // ---- Swords ----
        // AP1 → sum=1 → 1.1
        register(WeaponCategory.SHORTSWORD,          5.6f, 1.70f, 200, 1.1f, 90, 0.9f,
                WeaponTrait.AP.at(1));

        // AP1+HS1 → sum=2 → 1.0
        register(WeaponCategory.LONGSWORD,           6.4f, 1.50f, 250, 1.0f, 80, 0.9f,
                WeaponTrait.AP.at(1), WeaponTrait.HS.at(1));

        // TH1+HS2 → sum=3 → 0.9
        register(WeaponCategory.BROADSWORD,          7.3f, 1.30f, 250, 0.9f, 80, 0.8f,
                WeaponTrait.TH.at(1), WeaponTrait.HS.at(2));

        // HS1 → sum=1 → 1.1
        register(WeaponCategory.SABRE,               5.6f, 1.70f, 220, 1.1f, 90, 0.9f,
                WeaponTrait.HS.at(1));

        // R1 → sum=1 → 1.1
        register(WeaponCategory.FALCHION,            5.8f, 1.65f, 240, 1.1f, 70, 0.7f,
                WeaponTrait.R.at(1));

        // AP1+R1 → sum=2 → 1.0
        register(WeaponCategory.RAPIER,              5.0f, 1.90f, 180, 1.0f, 70, 0.9f,
                WeaponTrait.AP.at(1), WeaponTrait.R.at(1));

        // AP1+R1 → sum=2 → 1.0
        register(WeaponCategory.KATANA,              6.0f, 1.60f, 200, 1.0f, 70, 0.7f,
                WeaponTrait.AP.at(1), WeaponTrait.R.at(1));

        // TH2+AP1 → sum=3 → 0.9
        register(WeaponCategory.FLAMBERGE,           8.0f, 1.20f, 280, 0.9f, 80, 0.5f,
                WeaponTrait.TH.at(2), WeaponTrait.AP.at(1));

        // TH2+HS1 → sum=3 → 0.9
        register(WeaponCategory.ZWEIHANDER,         10.6f, 0.90f, 300, 0.9f, 60, 0.6f,
                WeaponTrait.TH.at(2), WeaponTrait.HS.at(1));

        // TH3 → sum=3 → 0.9
        register(WeaponCategory.GREATSWORD,         11.2f, 0.85f, 320, 0.9f, 70, 0.8f,
                WeaponTrait.TH.at(3));

        // TH2+R1 → sum=3 → 0.9
        register(WeaponCategory.EXECUTIONERS_SWORD, 21.3f, 0.45f, 350, 0.9f, 60, 0.4f,
                WeaponTrait.TH.at(2), WeaponTrait.R.at(1));

        // ---- Knives ----
        // AP1+R1 → sum=2 → 1.0
        register(WeaponCategory.DAGGER,              4.0f, 2.40f, 150, 1.0f, 60, 0.9f,
                WeaponTrait.AP.at(1), WeaponTrait.R.at(1));

        // AP2 → sum=2 → 1.0
        register(WeaponCategory.HUNTERS_KNIFE,       4.3f, 2.20f, 180, 1.0f, 40, 0.8f,
                WeaponTrait.AP.at(2));

        // ---- Polearms ----
        // TH1+AP1 → sum=2 → 1.0
        register(WeaponCategory.SPEAR,               6.1f, 1.55f, 220, 1.0f, 50, 0.8f,
                WeaponTrait.TH.at(1), WeaponTrait.AP.at(1));

        // AP1+TH1 → sum=2 → 1.0
        register(WeaponCategory.TRIDENT,             6.6f, 1.45f, 240, 1.0f, 50, 0.7f,
                WeaponTrait.AP.at(1), WeaponTrait.TH.at(1));

        // HS3 → sum=3 → 0.9
        register(WeaponCategory.LANCE,              12.0f, 0.80f, 260, 0.9f, 90, 0.7f,
                WeaponTrait.HS.at(3));

        // TH2 → sum=2 → 1.0
        register(WeaponCategory.GLAIVE,              8.7f, 1.10f, 280, 1.0f, 70, 0.5f,
                WeaponTrait.TH.at(2));

        // TH1+HS1+AP1 → sum=3 → 0.9
        register(WeaponCategory.HALBERD,             9.1f, 1.05f, 300, 0.9f, 60, 0.8f,
                WeaponTrait.TH.at(1), WeaponTrait.HS.at(1), WeaponTrait.AP.at(1));

        // ---- Blunt ----
        // R1+HS1 → sum=2 → 1.0
        register(WeaponCategory.MACE,                6.6f, 1.45f, 300, 1.0f, 30, 0.9f,
                WeaponTrait.R.at(1), WeaponTrait.HS.at(1));

        // AP1+HS1+R1 → sum=3 → 0.9
        register(WeaponCategory.FLAIL,               7.1f, 1.35f, 280, 0.9f, 40, 0.5f,
                WeaponTrait.AP.at(1), WeaponTrait.HS.at(1), WeaponTrait.R.at(1));

        // HS2 → sum=2 → 1.0
        register(WeaponCategory.HAMMER,              6.8f, 1.40f, 320, 1.0f, 70, 0.8f,
                WeaponTrait.HS.at(2));

        // AP3 → sum=3 → 0.9
        register(WeaponCategory.WARHAMMER,          10.6f, 0.90f, 360, 0.9f, 60, 0.5f,
                WeaponTrait.AP.at(3));

        // TH2+HS1 → sum=3 → 0.9
        register(WeaponCategory.GREATHAMMER,        12.8f, 0.75f, 400, 0.9f, 40, 0.7f,
                WeaponTrait.TH.at(2), WeaponTrait.HS.at(1));

        // ---- Axes ----
        // HS2 → sum=2 → 1.0
        register(WeaponCategory.HANDAXE,             8.7f, 1.10f, 220, 1.0f, 70, 0.8f,
                WeaponTrait.HS.at(2));

        // AP1+HS1 → sum=2 → 1.0
        register(WeaponCategory.CLEAVER,             6.8f, 1.40f, 260, 1.0f, 50, 0.5f,
                WeaponTrait.AP.at(1), WeaponTrait.HS.at(1));

        // TH2 → sum=2 → 1.0
        register(WeaponCategory.BATTLEAXE,          12.0f, 0.80f, 300, 1.0f, 60, 0.7f,
                WeaponTrait.TH.at(2));

        // TH1+HS1 → sum=2 → 1.0
        register(WeaponCategory.LUMBERAXE,          13.7f, 0.70f, 320, 1.0f, 80, 0.3f,
                WeaponTrait.TH.at(1), WeaponTrait.HS.at(1));

        // ---- Other melee ----
        // TH1+R2 → sum=3 → 0.9
        register(WeaponCategory.SCYTHE,             12.8f, 0.75f, 240, 0.9f, 70, 0.6f,
                WeaponTrait.TH.at(1), WeaponTrait.R.at(2));

        // R2 → sum=2 → 1.0
        register(WeaponCategory.SICKLE,              4.5f, 2.10f, 180, 1.0f, 40, 0.9f,
                WeaponTrait.R.at(2));

        // HS2 → sum=2 → 1.0
        register(WeaponCategory.GAUNTLETS,           4.1f, 2.30f, 160, 1.0f, 90, 0.5f,
                WeaponTrait.HS.at(2));

        // AP1+HS1 → sum=2 → 1.0
        register(WeaponCategory.CLAW,                4.8f, 2.00f, 180, 1.0f, 40, 0.6f,
                WeaponTrait.AP.at(1), WeaponTrait.HS.at(1));

        // HS1 → sum=1 → 1.1
        register(WeaponCategory.NUNCHAKU,            4.9f, 1.95f, 200, 1.1f, 80, 0.7f,
                WeaponTrait.HS.at(1));

        // TH1+R1+HS1 → sum=3 → 0.9
        register(WeaponCategory.QUARTERSTAFF,        5.4f, 1.75f, 300, 0.9f, 80, 0.6f,
                WeaponTrait.TH.at(1), WeaponTrait.R.at(1), WeaponTrait.HS.at(1));

        // TH1+HS1 → sum=2 → 1.0
        register(WeaponCategory.STAFF,               9.1f, 1.05f, 240, 1.0f, 60, 0.4f,
                WeaponTrait.TH.at(1), WeaponTrait.HS.at(1));

        // HS1+AP1 → sum=2 → 1.0
        register(WeaponCategory.WHIP,                4.6f, 2.05f, 160, 1.0f, 30, 0.8f,
                WeaponTrait.HS.at(1), WeaponTrait.AP.at(1));

        // ---- BMM additions ----
        // AP1+R1 → sum=2 → 1.0
        register(WeaponCategory.SAI,                 4.5f, 2.10f, 180, 1.0f, 50, 0.9f,
                WeaponTrait.AP.at(1), WeaponTrait.R.at(1));

        // TH1+AP1+R1 → sum=3 → 0.9
        register(WeaponCategory.TWINBLADES,          4.9f, 1.95f, 220, 0.9f, 90, 0.3f,
                WeaponTrait.TH.at(1), WeaponTrait.AP.at(1), WeaponTrait.R.at(1));
    }

    private static void register(WeaponCategory cat, float damage, float speed,
                                 int durability, float dpsModifier,
                                 int variantX, float variantY,
                                 WeaponTrait.Instance... traits) {
        BY_CATEGORY.put(cat, new WeaponProfile(cat, damage, speed, durability, dpsModifier,
                variantX, variantY, traits));
    }

    public static WeaponProfile get(WeaponCategory category) {
        WeaponProfile p = BY_CATEGORY.get(category);
        if (p == null) throw new IllegalStateException("No profile for " + category);
        return p;
    }

    public static Map<WeaponCategory, WeaponProfile> all() {
        return Collections.unmodifiableMap(BY_CATEGORY);
    }

    /** True if this weapon has BHB attack variant data. */
    public boolean hasVariantData() {
        return variantX >= 0;
    }
}
