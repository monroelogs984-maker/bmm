package com.bromax.bromaxmelees.item;

import com.bromax.bromaxmelees.weapon.IBMMTier;
import com.bromax.bromaxmelees.weapon.IWeaponCategoryItem;
import com.bromax.bromaxmelees.weapon.WeaponCategory;
import com.bromax.bromaxmelees.weapon.WeaponProfile;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Base weapon item for all BMM weapons.
 * Stats are driven entirely by WeaponProfile — it is the single source of truth.
 *
 * Since 1.20.5, attack attributes live in the ITEM_ATTRIBUTE_MODIFIERS data
 * component, built once here from the exact float values in WeaponProfile
 * (the old int-precision SwordItem damage bonus is bypassed entirely).
 */
public class BMMWeapon extends SwordItem implements IWeaponCategoryItem {

    private final WeaponCategory category;
    private final IBMMTier bmmTier;

    public BMMWeapon(WeaponCategory category) {
        this(category, BMMTier.IRON);
    }

    public BMMWeapon(WeaponCategory category, IBMMTier tier) {
        super(tier, new Properties().attributes(buildModifiers(category, tier)));
        this.category = category;
        this.bmmTier = tier;
    }

    /** Effective attack damage for this tier: baseDps * dpsModifier / speed. */
    public static float effectiveDamage(WeaponCategory category, IBMMTier tier) {
        WeaponProfile profile = WeaponProfile.get(category);
        return tier.getBaseDps() * profile.dpsModifier / profile.speed;
    }

    public float effectiveDamage() {
        return effectiveDamage(category, bmmTier);
    }

    public IBMMTier getBmmTier() { return bmmTier; }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            bmmTier.applyHitEffect(target, attacker);
        }
        return result;
    }

    private static ItemAttributeModifiers buildModifiers(WeaponCategory category, IBMMTier tier) {
        WeaponProfile profile = WeaponProfile.get(category);
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        BASE_ATTACK_DAMAGE_ID,
                        effectiveDamage(category, tier) - 1.0,
                        AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(
                        BASE_ATTACK_SPEED_ID,
                        profile.speed - 4.0,
                        AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public WeaponCategory getWeaponCategory() {
        return category;
    }
}
