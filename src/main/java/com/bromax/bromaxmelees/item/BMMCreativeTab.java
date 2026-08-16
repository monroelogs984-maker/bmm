package com.bromax.bromaxmelees.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 1.20.1 creative tabs are registry objects built with CreativeModeTab.builder()
 * — the old Item.Properties.tab() path is gone, items are added via displayItems.
 */
public final class BMMCreativeTab {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "bromax_melees");

    public static final RegistryObject<CreativeModeTab> TAB =
            TABS.register("bromax_melees", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bromax_melees"))
                    .icon(() -> new ItemStack(BMMItems.LONGSWORD.iron.get()))
                    .displayItems((params, output) ->
                            BMMItems.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());

    private BMMCreativeTab() {}
}
