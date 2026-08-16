package com.bromax.bromaxmelees.item;

import com.bromax.bromaxmelees.BromaxMelees;
import com.bromax.bromaxmelees.weapon.WeaponCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

public final class BMMItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.ITEM, BromaxMelees.MOD_ID);

    private static final Map<WeaponCategory, WeaponEntry> BY_CATEGORY =
            new EnumMap<>(WeaponCategory.class);

    // ---- Swords ----
    public static final WeaponEntry SHORTSWORD         = entry(WeaponCategory.SHORTSWORD);
    public static final WeaponEntry LONGSWORD          = entry(WeaponCategory.LONGSWORD);
    public static final WeaponEntry BROADSWORD         = entry(WeaponCategory.BROADSWORD);
    public static final WeaponEntry SABRE              = entry(WeaponCategory.SABRE);
    public static final WeaponEntry RAPIER             = entry(WeaponCategory.RAPIER);
    public static final WeaponEntry GREATSWORD         = entry(WeaponCategory.GREATSWORD);
    public static final WeaponEntry ZWEIHANDER         = entry(WeaponCategory.ZWEIHANDER);
    public static final WeaponEntry KATANA             = entry(WeaponCategory.KATANA);
    public static final WeaponEntry FLAMBERGE          = entry(WeaponCategory.FLAMBERGE);
    public static final WeaponEntry EXECUTIONERS_SWORD = entry(WeaponCategory.EXECUTIONERS_SWORD);
    public static final WeaponEntry FALCHION           = entry(WeaponCategory.FALCHION);

    // ---- Knives ----
    public static final WeaponEntry DAGGER             = entry(WeaponCategory.DAGGER);
    public static final WeaponEntry HUNTERS_KNIFE      = entry(WeaponCategory.HUNTERS_KNIFE);

    // ---- Polearms ----
    public static final WeaponEntry SPEAR              = entry(WeaponCategory.SPEAR);
    public static final WeaponEntry TRIDENT            = entry(WeaponCategory.TRIDENT);
    public static final WeaponEntry HALBERD            = entry(WeaponCategory.HALBERD);
    public static final WeaponEntry GLAIVE             = entry(WeaponCategory.GLAIVE);
    public static final WeaponEntry LANCE              = entry(WeaponCategory.LANCE);

    // ---- Blunt ----
    public static final WeaponEntry MACE               = entry(WeaponCategory.MACE);
    public static final WeaponEntry WARHAMMER          = entry(WeaponCategory.WARHAMMER);
    public static final WeaponEntry HAMMER             = entry(WeaponCategory.HAMMER);
    public static final WeaponEntry FLAIL              = entry(WeaponCategory.FLAIL);
    public static final WeaponEntry GREATHAMMER        = entry(WeaponCategory.GREATHAMMER);

    // ---- Axes ----
    public static final WeaponEntry HANDAXE            = entry(WeaponCategory.HANDAXE);
    public static final WeaponEntry BATTLEAXE          = entry(WeaponCategory.BATTLEAXE);
    public static final WeaponEntry CLEAVER            = entry(WeaponCategory.CLEAVER);
    public static final WeaponEntry LUMBERAXE          = entry(WeaponCategory.LUMBERAXE);

    // ---- Other melee ----
    public static final WeaponEntry SCYTHE             = entry(WeaponCategory.SCYTHE);
    public static final WeaponEntry SICKLE             = entry(WeaponCategory.SICKLE);
    public static final WeaponEntry GAUNTLETS          = entry(WeaponCategory.GAUNTLETS);
    public static final WeaponEntry CLAW               = entry(WeaponCategory.CLAW);
    public static final WeaponEntry NUNCHAKU           = entry(WeaponCategory.NUNCHAKU);
    public static final WeaponEntry QUARTERSTAFF       = entry(WeaponCategory.QUARTERSTAFF);
    public static final WeaponEntry WHIP               = entry(WeaponCategory.WHIP);
    public static final WeaponEntry STAFF              = entry(WeaponCategory.STAFF);

    // ---- BMM additions ----
    public static final WeaponEntry SAI                = entry(WeaponCategory.SAI);
    public static final WeaponEntry TWINBLADES         = entry(WeaponCategory.TWINBLADES);

    // ---- Blocks ----
    public static final DeferredHolder<Item, BlockItem> WEAPONRY_RACK =
            ITEMS.register("weaponry_rack", () -> new BlockItem(BMMBlocks.WEAPONRY_RACK.get(), new Item.Properties()));

    private static WeaponEntry entry(WeaponCategory cat) {
        String id = cat.id();
        DeferredHolder<Item, BMMWeapon> iron      = ITEMS.register(id,                () -> new BMMWeapon(cat, BMMTier.IRON));
        DeferredHolder<Item, BMMWeapon> gold      = ITEMS.register("gold_"      + id, () -> new BMMWeapon(cat, BMMTier.GOLD));
        DeferredHolder<Item, BMMWeapon> diamond   = ITEMS.register("diamond_"   + id, () -> new BMMWeapon(cat, BMMTier.DIAMOND));
        DeferredHolder<Item, BMMWeapon> netherite = ITEMS.register("netherite_" + id, () -> new BMMWeapon(cat, BMMTier.NETHERITE));
        WeaponEntry e = new WeaponEntry(iron, gold, diamond, netherite);
        BY_CATEGORY.put(cat, e);
        return e;
    }

    public static WeaponEntry byCategory(WeaponCategory category) {
        return BY_CATEGORY.get(category);
    }

    private BMMItems() {}
}
