package com.bromax.bromaxmelees.event;

import com.bromax.bromaxmelees.BromaxMelees;
import com.bromax.bromaxmelees.item.BMMWeapon;
import com.bromax.bromaxmelees.weapon.IWeaponCategoryItem;
import com.bromax.bromaxmelees.weapon.WeaponProfile;
import com.bromax.bromaxmelees.weapon.WeaponTrait;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BromaxMelees.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BMMEvents {

    // AP: ThreadLocal passes the pierce bonus from LivingHurtEvent to LivingDamageEvent.
    // Always reset at the start of every LivingHurtEvent so stale values never bleed
    // across unrelated attacks (e.g. when the hurt event is cancelled by a shield).
    private static final ThreadLocal<Float> AP_PIERCE_BONUS = ThreadLocal.withInitial(() -> 0f);

    // HS: single UUID for the transient movement speed modifier on any entity.
    // Safe to reuse across entities — each entity has its own AttributeInstance.
    private static final UUID HS_UUID = UUID.fromString("b8a7c3d1-4e2f-4a1b-9c3d-5f6e7a8b9c0d");
    private static final Map<UUID, Long> HS_EXPIRY = new HashMap<>();

    // -------------------------------------------------------------------------
    // LivingHurtEvent — fires before armor reduction
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        AP_PIERCE_BONUS.set(0f); // always clear so cancelled hits don't leak

        Entity attacker = event.getSource().getDirectEntity();
        if (!(attacker instanceof LivingEntity living)) return;

        ItemStack held = living.getMainHandItem();
        if (!(held.getItem() instanceof IWeaponCategoryItem weaponItem)) return;

        WeaponProfile profile = WeaponProfile.get(weaponItem.getWeaponCategory());

        applyTwoHanded(event, profile, living);
        computeArmorPierceBonus(event, profile);
    }

    // -------------------------------------------------------------------------
    // LivingDamageEvent — fires after armor reduction, before HP is changed
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        // AP: inject pierce bonus computed in LivingHurtEvent
        float pierceBonus = AP_PIERCE_BONUS.get();
        if (pierceBonus > 0f) {
            event.setAmount(event.getAmount() + pierceBonus);
            AP_PIERCE_BONUS.set(0f);
        }

        Entity attacker = event.getSource().getDirectEntity();
        if (!(attacker instanceof LivingEntity living)) return;

        ItemStack held = living.getMainHandItem();
        if (!(held.getItem() instanceof IWeaponCategoryItem weaponItem)) return;

        WeaponProfile profile = WeaponProfile.get(weaponItem.getWeaponCategory());

        applyHardStrike(event, profile);
        applyRageful(event, profile, weaponItem);
    }

    // -------------------------------------------------------------------------
    // LivingTickEvent — expires Hard Strike speed debuff
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        Long expiry = HS_EXPIRY.get(entity.getUUID());
        if (expiry == null) return;

        if (entity.level.getGameTime() >= expiry) {
            AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr != null) attr.removeModifier(HS_UUID);
            HS_EXPIRY.remove(entity.getUUID());
        }
    }

    // -------------------------------------------------------------------------
    // Trait implementations
    // -------------------------------------------------------------------------

    private static void applyTwoHanded(LivingHurtEvent event, WeaponProfile profile,
                                        LivingEntity attacker) {
        int level = traitLevel(profile, WeaponTrait.TH);
        if (level == 0) return;
        if (!attacker.getOffhandItem().isEmpty()) return;

        float multiplier = switch (level) {
            case 1 -> 1.10f;
            case 2 -> 1.25f;
            default -> 1.40f;
        };
        event.setAmount(event.getAmount() * multiplier);
    }

    private static void computeArmorPierceBonus(LivingHurtEvent event, WeaponProfile profile) {
        int level = traitLevel(profile, WeaponTrait.AP);
        if (level == 0) return;

        float pierceFraction = switch (level) {
            case 1 -> 0.15f;
            case 2 -> 0.40f;
            default -> 0.75f;
        };

        LivingEntity target = event.getEntity();
        float rawDamage = event.getAmount();
        float armor = (float) target.getAttributeValue(Attributes.ARMOR);
        float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

        float postArmor = CombatRules.getDamageAfterAbsorb(rawDamage, armor, toughness);
        float blocked = rawDamage - postArmor;
        AP_PIERCE_BONUS.set(blocked * pierceFraction);
    }

    private static void applyHardStrike(LivingDamageEvent event, WeaponProfile profile) {
        int level = traitLevel(profile, WeaponTrait.HS);
        if (level == 0) return;

        float slow = switch (level) {
            case 1 -> -0.20f;
            case 2 -> -0.40f;
            default -> -0.70f;
        };
        int durationTicks = switch (level) {
            case 1 -> 36;  // 1.8s
            case 2 -> 40;  // 2.0s
            default -> 42; // 2.1s
        };

        LivingEntity target = event.getEntity();
        AttributeInstance attr = target.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;

        attr.removeModifier(HS_UUID); // clear any existing debuff before refreshing
        attr.addTransientModifier(new AttributeModifier(
                HS_UUID, "bmm:hard_strike", slow, AttributeModifier.Operation.MULTIPLY_TOTAL));

        HS_EXPIRY.put(target.getUUID(), target.level.getGameTime() + durationTicks);
    }

    private static void applyRageful(LivingDamageEvent event, WeaponProfile profile,
                                      IWeaponCategoryItem weaponItem) {
        int level = traitLevel(profile, WeaponTrait.R);
        if (level == 0) return;

        float multiplier = switch (level) {
            case 1 -> 0.6f;
            case 2 -> 1.2f;
            default -> 1.8f;
        };

        float effectiveDamage = (weaponItem instanceof BMMWeapon bmmWeapon)
                ? bmmWeapon.effectiveDamage()
                : profile.damage * profile.dpsModifier;
        float threshold = multiplier * effectiveDamage;
        float remaining = event.getEntity().getHealth() - event.getAmount();

        if (remaining > 0f && remaining < threshold) {
            event.setAmount(event.getEntity().getHealth());
        }
    }

    static int traitLevel(WeaponProfile profile, WeaponTrait trait) {
        for (WeaponTrait.Instance t : profile.traits) {
            if (t.trait() == trait) return t.level();
        }
        return 0;
    }

    private BMMEvents() {}
}
