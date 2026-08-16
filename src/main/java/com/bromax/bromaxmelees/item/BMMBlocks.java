package com.bromax.bromaxmelees.item;

import com.bromax.bromaxmelees.BromaxMelees;
import com.bromax.bromaxmelees.block.WeaponryRackBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BMMBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, BromaxMelees.MOD_ID);

    public static final DeferredHolder<Block, WeaponryRackBlock> WEAPONRY_RACK =
            BLOCKS.register("weaponry_rack", () -> new WeaponryRackBlock(
                    BlockBehaviour.Properties.of()
                            .strength(2.5f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    private BMMBlocks() {}
}
