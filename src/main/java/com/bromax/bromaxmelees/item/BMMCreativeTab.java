package com.bromax.bromaxmelees.item;

import com.bromax.bromaxmelees.BromaxMelees;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BMMCreativeTab {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BromaxMelees.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
            TABS.register("bromax_melees", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bromax_melees"))
                    .icon(() -> new ItemStack(BMMItems.LONGSWORD.iron.get()))
                    .displayItems((params, output) -> {
                        BMMItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
                    .build());

    private BMMCreativeTab() {}
}
