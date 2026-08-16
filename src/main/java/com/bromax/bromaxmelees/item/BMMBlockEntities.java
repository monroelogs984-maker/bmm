package com.bromax.bromaxmelees.item;

import com.bromax.bromaxmelees.BromaxMelees;
import com.bromax.bromaxmelees.block.WeaponryRackBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BMMBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BromaxMelees.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WeaponryRackBlockEntity>> WEAPONRY_RACK =
            BLOCK_ENTITIES.register("weaponry_rack", () ->
                    BlockEntityType.Builder.of(WeaponryRackBlockEntity::new,
                            BMMBlocks.WEAPONRY_RACK.get()).build(null));

    private BMMBlockEntities() {}
}
