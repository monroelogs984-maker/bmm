package com.bromax.bromaxmelees.item;

import com.bromax.bromaxmelees.weapon.IWeaponCategoryItem;
import com.bromax.bromaxmelees.weapon.WeaponCategory;
import com.bromax.bromaxmelees.weapon.WeaponProfile;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

/**
 * Base weapon item for all BMM weapons.
 * Stats are driven entirely by WeaponProfile — it is the single source of truth.
 *
 * The SwordItem constructor takes int baseDamageBonus, which loses decimal precision.
 * We pass 0 there and override getAttributeModifiers to inject the exact float values
 * from WeaponProfile instead.
 */
public class BMMWeapon extends SwordItem implements IWeaponCategoryItem {

    private final WeaponCategory category;
    private final BMMTier bmmTier;

    public BMMWeapon(WeaponCategory category) {
        this(category, BMMTier.IRON);
    }

    public BMMWeapon(WeaponCategory category, BMMTier tier) {
        super(tier, 0, WeaponProfile.get(category).speed - 4.0f,
              new Properties().tab(BMMCreativeTab.TAB));
        this.category = category;
        this.bmmTier = tier;
    }

    /** Effective attack damage for this tier: baseDps * dpsModifier / speed. */
    public float effectiveDamage() {
        WeaponProfile profile = WeaponProfile.get(category);
        return bmmTier.getBaseDps() * profile.dpsModifier / profile.speed;
    }

    public BMMTier getBmmTier() { return bmmTier; }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return buildModifiers(slot);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return buildModifiers(slot);
    }

    private Multimap<Attribute, AttributeModifier> buildModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) return ImmutableMultimap.of();
        WeaponProfile profile = WeaponProfile.get(category);
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                        effectiveDamage() - 1.0,
                        AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(
                        BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                        profile.speed - 4.0,
                        AttributeModifier.Operation.ADDITION))
                .build();
    }

    @Override
    public WeaponCategory getWeaponCategory() {
        return category;
    }
}
