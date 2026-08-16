package com.bromax.bromaxmelees.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;

public enum BMMTier implements Tier {

    IRON     (2, 3000, 6.0f, 2.0f, 14, Ingredient.of(Items.IRON_INGOT),       9.6f),
    DIAMOND  (3, 4500, 8.0f, 3.0f, 10, Ingredient.of(Items.DIAMOND),          11.2f),
    NETHERITE(4, 8000, 9.0f, 4.0f, 15, Ingredient.of(Items.NETHERITE_INGOT),  12.8f);

    private final int level;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final Ingredient repairIngredient;
    private final float baseDps;

    BMMTier(int level, int uses, float speed, float attackDamageBonus,
            int enchantmentValue, Ingredient repairIngredient, float baseDps) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
        this.baseDps = baseDps;
    }

    public float getBaseDps() { return baseDps; }

    @Override public int getUses()              { return uses; }
    @Override public float getSpeed()           { return speed; }
    @Override public float getAttackDamageBonus() { return attackDamageBonus; }
    @Override public int getLevel()             { return level; }
    @Override public int getEnchantmentValue()  { return enchantmentValue; }
    @Override public Ingredient getRepairIngredient() { return repairIngredient; }
}
