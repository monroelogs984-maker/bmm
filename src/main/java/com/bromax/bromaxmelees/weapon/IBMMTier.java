package com.bromax.bromaxmelees.weapon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Tier;

/** Extension of Tier that carries the DPS budget baseDps used by BMMWeapon. */
public interface IBMMTier extends Tier {
    float getBaseDps();
    default int getLootingBonus() { return 0; }
    default void applyHitEffect(LivingEntity target, LivingEntity attacker) {}
    /** Damage multiplier applied in LivingIncomingDamageEvent; 1.0 = no bonus. */
    default float getDamageMultiplier(LivingEntity target) { return 1.0f; }
}
