package com.bromax.bromaxmelees.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class BMMCreativeTab {

    public static final CreativeModeTab TAB = new CreativeModeTab("bromax_melees") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BMMItems.LONGSWORD.iron.get());
        }
    };

    private BMMCreativeTab() {}
}
