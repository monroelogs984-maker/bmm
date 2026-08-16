package com.bromax.bromaxmelees.block;

import com.bromax.bromaxmelees.item.BMMMenuTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class WeaponryRackMenu extends AbstractContainerMenu {

    public static final int WEAPON_SLOT_X = 30;
    public static final int WEAPON_SLOT_Y = 87;

    public static final int INV_START_X = 47;
    public static final int INV_START_Y = 194;
    public static final int HOTBAR_Y    = 252;

    private static final int PLAYER_INV_FIRST = 1;
    private static final int TOTAL_SLOTS      = 37; // 1 weapon + 27 inv + 9 hotbar

    private final Container container;

    public WeaponryRackMenu(int containerId, Inventory playerInventory, Container container) {
        super(BMMMenuTypes.WEAPONRY_RACK.get(), containerId);
        this.container = container;
        checkContainerSize(container, 1);

        // Slot 0 — accepts any item that declares a MAINHAND attack damage modifier,
        // which covers BMM weapons, vanilla swords/axes/tridents, and modded weapons
        // (spears, hammers, rapiers, etc.) that follow the attribute convention.
        addSlot(new Slot(container, 0, WEAPON_SLOT_X, WEAPON_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return hasMainhandAttackDamage(stack);
            }
        });

        // Slots 1-27 — player main inventory
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        INV_START_X + col * 18, INV_START_Y + row * 18));

        // Slots 28-36 — hotbar
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col, INV_START_X + col * 18, HOTBAR_Y));
    }

    public WeaponryRackMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(1));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return result;

        ItemStack stack = slot.getItem();
        result = stack.copy();

        if (index == 0) {
            // Weapon slot → try main inventory first, then hotbar
            if (!moveItemStackTo(stack, PLAYER_INV_FIRST, TOTAL_SLOTS, true))
                return ItemStack.EMPTY;
        } else if (slots.get(0).mayPlace(stack)) {
            // Inventory/hotbar → weapon slot if the item is recognised
            if (!moveItemStackTo(stack, 0, 1, false))
                return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public Container getContainer() {
        return container;
    }

    private static boolean hasMainhandAttackDamage(ItemStack stack) {
        ItemAttributeModifiers mods = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        return mods.modifiers().stream()
                .anyMatch(e -> e.attribute().is(Attributes.ATTACK_DAMAGE)
                            && e.slot().test(EquipmentSlot.MAINHAND));
    }
}
