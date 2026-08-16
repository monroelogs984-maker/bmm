package com.bromax.bromaxmelees.weapon;

/**
 * The 4 core weapon traits. Each trait has 1–3 levels. Weapons carry 2–4 traits total.
 * Traits are broadly distributed so players learn them once and they apply everywhere.
 *
 * AP  — Armor Piercing:  ignores a portion of target armor on hit
 *          AP1: ignore 15% of armor
 *          AP2: ignore 40% of armor
 *          AP3: ignore 75% of armor
 *
 * TH  — Two-Handed:     damage bonus when the offhand slot is empty
 *          TH1: +10% damage
 *          TH2: +25% damage
 *          TH3: +40% damage (reserved for greatsword-tier weapons)
 *
 * HS  — Hard Strike:    raw movement speed reduction on hit (NOT a potion effect —
 *                       immune mobs are not exempt)
 *          HS1: −20% speed for 1.8s
 *          HS2: −40% speed for 2.0s
 *          HS3: −70% speed for 2.1s
 *
 * R   — Rageful:        if an attack would leave an enemy near-death, it finishes them
 *                       off. Threshold is proportionate to weapon damage, NOT enemy max
 *                       HP — prevents boss trivialization by fast low-damage weapons.
 *          R1: execute if remaining HP < 0.6× weapon damage
 *          R2: execute if remaining HP < 1.2× weapon damage
 *          R3: execute if remaining HP < 1.8× weapon damage
 */
public enum WeaponTrait {

    AP(3),
    TH(3),
    HS(3),
    R(3);

    public final int maxLevel;

    WeaponTrait(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public record Instance(WeaponTrait trait, int level) {
        public Instance {
            if (level < 1 || level > trait.maxLevel)
                throw new IllegalArgumentException(trait + " level must be 1–" + trait.maxLevel + ", got " + level);
        }
    }

    public Instance at(int level) {
        return new Instance(this, level);
    }
}
