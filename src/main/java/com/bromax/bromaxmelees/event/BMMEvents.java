package com.bromax.bromaxmelees.event;

import com.bromax.bromaxmelees.BromaxMelees;
import com.bromax.bromaxmelees.item.BMMWeapon;
import com.bromax.bromaxmelees.weapon.IWeaponCategoryItem;
import com.bromax.bromaxmelees.weapon.WeaponProfile;
import com.bromax.bromaxmelees.weapon.WeaponTrait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = BromaxMelees.MOD_ID)
public final class BMMEvents {

    // AP: ThreadLocal passes the pierce bonus from LivingIncomingDamageEvent (pre-armor)
    // to LivingDamageEvent.Pre (post-armor). Always reset at the start of every incoming
    // event so stale values never bleed across unrelated attacks (e.g. when the hit is
    // cancelled by a shield).
    private static final ThreadLocal<Float> AP_PIERCE_BONUS = ThreadLocal.withInitial(() -> 0f);

    // HS: single id for the transient movement speed modifier on any entity.
    // Safe to reuse across entities — each entity has its own AttributeInstance.
    private static final ResourceLocation HS_ID =
            ResourceLocation.fromNamespaceAndPath(BromaxMelees.MOD_ID, "hard_strike");
    private static final Map<UUID, Long> HS_EXPIRY = new HashMap<>();

    // -------------------------------------------------------------------------
    // LivingIncomingDamageEvent — fires before armor reduction
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
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
    // LivingDamageEvent.Pre — fires after armor reduction, before HP is changed
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        // AP: inject pierce bonus computed in the incoming damage event
        float pierceBonus = AP_PIERCE_BONUS.get();
        if (pierceBonus > 0f) {
            event.setNewDamage(event.getNewDamage() + pierceBonus);
            AP_PIERCE_BONUS.set(0f);
        }

        Entity attacker = event.getContainer().getSource().getDirectEntity();
        if (!(attacker instanceof LivingEntity living)) return;

        ItemStack held = living.getMainHandItem();
        if (!(held.getItem() instanceof IWeaponCategoryItem weaponItem)) return;

        WeaponProfile profile = WeaponProfile.get(weaponItem.getWeaponCategory());

        applyHardStrike(event, profile);
        applyRageful(event, profile, weaponItem);
    }

    // -------------------------------------------------------------------------
    // EntityTickEvent.Post — expires Hard Strike speed debuff
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        Long expiry = HS_EXPIRY.get(entity.getUUID());
        if (expiry == null) return;

        if (entity.level().getGameTime() >= expiry) {
            AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr != null) attr.removeModifier(HS_ID);
            HS_EXPIRY.remove(entity.getUUID());
        }
    }

    // -------------------------------------------------------------------------
    // Trait implementations
    // -------------------------------------------------------------------------

    private static void applyTwoHanded(LivingIncomingDamageEvent event, WeaponProfile profile,
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

    private static void computeArmorPierceBonus(LivingIncomingDamageEvent event, WeaponProfile profile) {
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

        float postArmor = CombatRules.getDamageAfterAbsorb(target, rawDamage, event.getSource(), armor, toughness);
        float blocked = rawDamage - postArmor;
        AP_PIERCE_BONUS.set(blocked * pierceFraction);
    }

    private static void applyHardStrike(LivingDamageEvent.Pre event, WeaponProfile profile) {
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

        attr.removeModifier(HS_ID); // clear any existing debuff before refreshing
        attr.addTransientModifier(new AttributeModifier(
                HS_ID, slow, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        HS_EXPIRY.put(target.getUUID(), target.level().getGameTime() + durationTicks);
    }

    private static void applyRageful(LivingDamageEvent.Pre event, WeaponProfile profile,
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
        float remaining = event.getEntity().getHealth() - event.getNewDamage();

        if (remaining > 0f && remaining < threshold) {
            event.setNewDamage(event.getEntity().getHealth());
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
