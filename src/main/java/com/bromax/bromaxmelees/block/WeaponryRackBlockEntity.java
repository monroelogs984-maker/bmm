package com.bromax.bromaxmelees.block;

import com.bromax.bromaxmelees.item.BMMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WeaponryRackBlockEntity extends BlockEntity implements MenuProvider, Container {

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public WeaponryRackBlockEntity(BlockPos pos, BlockState state) {
        super(BMMBlockEntities.WEAPONRY_RACK.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.bromax_melees.weaponry_rack");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new WeaponryRackMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override public int getContainerSize()                    { return 1; }
    @Override public boolean isEmpty()                         { return items.get(0).isEmpty(); }
    @Override public ItemStack getItem(int slot)               { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int count) { return ContainerHelper.removeItem(items, slot, count); }
    @Override public ItemStack removeItemNoUpdate(int slot)    { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack)   { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player)         { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent()                       { items.clear(); }
}
