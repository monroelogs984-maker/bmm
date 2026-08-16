package com.bromax.bromaxmelees.item;

import com.bromax.bromaxmelees.BromaxMelees;
import com.bromax.bromaxmelees.block.WeaponryRackMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BMMMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, BromaxMelees.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<WeaponryRackMenu>> WEAPONRY_RACK =
            MENUS.register("weaponry_rack", () ->
                    new MenuType<>(WeaponryRackMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private BMMMenuTypes() {}
}
