package com.bromax.bromaxmelees.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum BMMTier implements com.bromax.bromaxmelees.weapon.IBMMTier {

    IRON     (BlockTags.INCORRECT_FOR_IRON_TOOL,      3000, 6.0f, 2.0f, 14, 9.6f),
    GOLD     (BlockTags.INCORRECT_FOR_IRON_TOOL,       500, 6.0f, 0.0f, 22, 6.4f),
    DIAMOND  (BlockTags.INCORRECT_FOR_DIAMOND_TOOL,   4500, 8.0f, 3.0f, 10, 11.2f),
    NETHERITE(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 8000, 9.0f, 4.0f, 15, 12.8f);

    private final TagKey<Block> incorrectBlocks;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final float baseDps;

    BMMTier(TagKey<Block> incorrectBlocks, int uses, float speed, float attackDamageBonus,
            int enchantmentValue, float baseDps) {
        this.incorrectBlocks = incorrectBlocks;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.baseDps = baseDps;
    }

    public float getBaseDps() { return baseDps; }

    @Override public int getUses()                { return uses; }
    @Override public float getSpeed()             { return speed; }
    @Override public float getAttackDamageBonus() { return attackDamageBonus; }
    @Override public TagKey<Block> getIncorrectBlocksForDrops() { return incorrectBlocks; }
    @Override public int getEnchantmentValue()    { return enchantmentValue; }

    @Override
    public Ingredient getRepairIngredient() {
        return switch (this) {
            case IRON      -> Ingredient.of(Items.IRON_INGOT);
            case GOLD      -> Ingredient.of(Items.GOLD_INGOT);
            case DIAMOND   -> Ingredient.of(Items.DIAMOND);
            case NETHERITE -> Ingredient.of(Items.NETHERITE_INGOT);
        };
    }
}
