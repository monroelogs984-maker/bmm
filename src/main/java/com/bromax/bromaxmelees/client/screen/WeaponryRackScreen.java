package com.bromax.bromaxmelees.client.screen;

import com.bromax.bromaxmelees.block.WeaponryRackMenu;
import com.bromax.bromaxmelees.compat.BHBCompat;
import com.bromax.bromaxmelees.compat.BHBProfile;
import com.bromax.bromaxmelees.item.BMMTier;
import com.bromax.bromaxmelees.item.BMMWeapon;
import com.bromax.bromaxmelees.weapon.IWeaponCategoryItem;
import com.bromax.bromaxmelees.weapon.WeaponCategory;
import com.bromax.bromaxmelees.weapon.WeaponProfile;
import com.bromax.bromaxmelees.weapon.WeaponTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public class WeaponryRackScreen extends AbstractContainerScreen<WeaponryRackMenu> {

    private static final int DIVIDER_X  = 76;
    private static final int PANEL_PAD  = 6;
    private static final int LINE_H     = 10;
    private static final int PANEL_H    = 190;

    private static final int INFO_X     = DIVIDER_X + PANEL_PAD;
    private static final int INFO_Y     = PANEL_PAD;
    private static final int INFO_RIGHT = 253;
    private static final int INFO_BOT   = PANEL_H - PANEL_PAD;
    private static final int TEXT_WIDTH = INFO_RIGHT - INFO_X - 5;

    private int scrollOffset = 0;
    private List<FormattedCharSequence> infoLines = List.of();
    private ItemStack lastItem                    = ItemStack.EMPTY;

    public WeaponryRackScreen(WeaponryRackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth      = 256;
        this.imageHeight     = 276;
        this.titleLabelX     = -9999;
        this.inventoryLabelX = -9999;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        // ── Top info panel ──────────────────────────────────────────────
        g.fill(leftPos,     topPos,     leftPos + imageWidth,     topPos + PANEL_H,          0xFF1A1A1A);
        g.fill(leftPos + 2, topPos + 2, leftPos + DIVIDER_X - 2, topPos + PANEL_H - 2,      0xFF353535);
        g.fill(leftPos + DIVIDER_X - 1, topPos, leftPos + DIVIDER_X + 1, topPos + PANEL_H,  0xFF4A4A4A);
        g.fill(leftPos + DIVIDER_X + 1, topPos + 2, leftPos + imageWidth - 2, topPos + PANEL_H - 2, 0xFF2E2E2E);

        int sx = leftPos + WeaponryRackMenu.WEAPON_SLOT_X;
        int sy = topPos  + WeaponryRackMenu.WEAPON_SLOT_Y;
        g.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF555555);
        g.fill(sx,     sy,     sx + 16, sy + 16, 0xFF8B8B8B);

        // ── Inventory area ───────────────────────────────────────────────
        g.fill(leftPos, topPos + PANEL_H + 2, leftPos + imageWidth, topPos + imageHeight,                    0xFF1A1A1A);
        g.fill(leftPos + 2, topPos + PANEL_H + 4, leftPos + imageWidth - 2, topPos + imageHeight - 2, 0xFF2E2E2E);

        int ix = WeaponryRackMenu.INV_START_X;
        int iy = WeaponryRackMenu.INV_START_Y;
        int hy = WeaponryRackMenu.HOTBAR_Y;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotX = leftPos + ix + col * 18;
                int slotY = topPos  + iy + row * 18;
                g.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF555555);
                g.fill(slotX,     slotY,     slotX + 16, slotY + 16, 0xFF8B8B8B);
            }
        }

        for (int col = 0; col < 9; col++) {
            int slotX = leftPos + ix + col * 18;
            int slotY = topPos  + hy;
            g.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF777777);
            g.fill(slotX,     slotY,     slotX + 16, slotY + 16, 0xFF8B8B8B);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, "Weapon", DIVIDER_X / 2 - font.width("Weapon") / 2,
                WeaponryRackMenu.WEAPON_SLOT_Y - 12, 0xAAAAAA, false);

        ItemStack current = menu.getContainer().getItem(0);
        if (!ItemStack.matches(current, lastItem)) {
            lastItem     = current.copy();
            scrollOffset = 0;
            infoLines    = buildLines(current);
        }

        int visibleLines = (INFO_BOT - INFO_Y) / LINE_H;
        int maxScroll    = Math.max(0, infoLines.size() - visibleLines);
        scrollOffset     = Math.min(scrollOffset, maxScroll);

        g.enableScissor(leftPos + DIVIDER_X + 1, topPos + 2,
                        leftPos + imageWidth - 2, topPos + PANEL_H - 2);

        for (int i = scrollOffset; i < Math.min(infoLines.size(), scrollOffset + visibleLines); i++) {
            g.drawString(font, infoLines.get(i), INFO_X, INFO_Y + (i - scrollOffset) * LINE_H,
                    0xFFFFFF, false);
        }

        g.disableScissor();

        if (infoLines.size() > visibleLines) {
            int barX   = INFO_RIGHT - 2;
            int barY   = INFO_Y;
            int barH   = INFO_BOT - INFO_Y;
            int thumbH = Math.max(8, barH * visibleLines / infoLines.size());
            int thumbY = (maxScroll == 0) ? 0 : (barH - thumbH) * scrollOffset / maxScroll;
            g.fill(barX, barY,          barX + 3, barY + barH,            0xFF555555);
            g.fill(barX, barY + thumbY, barX + 3, barY + thumbY + thumbH, 0xFFAAAAAA);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX > leftPos + DIVIDER_X && mouseY < topPos + PANEL_H) {
            int visibleLines = (INFO_BOT - INFO_Y) / LINE_H;
            int maxScroll    = Math.max(0, infoLines.size() - visibleLines);
            scrollOffset     = (int) Math.clamp(scrollOffset - scrollY, 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    // -------------------------------------------------------------------------

    private List<FormattedCharSequence> buildLines(ItemStack stack) {
        List<Component> raw = new ArrayList<>();

        if (stack.isEmpty()) {
            raw.add(Component.literal("No weapon inserted.").withStyle(ChatFormatting.GRAY));
            raw.add(Component.empty());
            raw.add(Component.literal("Place any item in the").withStyle(ChatFormatting.DARK_GRAY));
            raw.add(Component.literal("slot to inspect it.").withStyle(ChatFormatting.DARK_GRAY));
            return wrap(raw);
        }

        raw.add(Component.literal(stack.getHoverName().getString()).withStyle(ChatFormatting.WHITE));

        boolean isBMM = stack.getItem() instanceof IWeaponCategoryItem;

        WeaponCategory cat     = null;
        WeaponProfile  profile = null;

        if (isBMM) {
            cat     = ((IWeaponCategoryItem) stack.getItem()).getWeaponCategory();
            profile = WeaponProfile.get(cat);

            if (stack.getItem() instanceof BMMWeapon bw) {
                String tier;
                if (bw.getBmmTier() instanceof BMMTier bt) {
                    tier = switch (bt) {
                        case IRON      -> "Iron";
                        case GOLD      -> "Gold";
                        case DIAMOND   -> "Diamond";
                        case NETHERITE -> "Netherite";
                    };
                } else {
                    tier = "Modded";
                }
                raw.add(Component.literal("Tier: " + tier).withStyle(ChatFormatting.GRAY));
            }
            raw.add(Component.literal("Type: " + displayName(cat)).withStyle(ChatFormatting.GRAY));
            raw.add(Component.literal("Group: " + displayGroup(cat)).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            raw.add(Component.literal("Not a BMM weapon.").withStyle(ChatFormatting.GRAY));
        }

        // Durability — both BMM and non-BMM, near top
        if (stack.getMaxDamage() > 0) {
            int remaining = stack.getMaxDamage() - stack.getDamageValue();
            int max       = stack.getMaxDamage();
            ChatFormatting durColor = remaining > max * 3 / 4 ? ChatFormatting.GREEN
                                    : remaining > max     / 4 ? ChatFormatting.YELLOW
                                                              : ChatFormatting.RED;
            raw.add(Component.literal(String.format("Durability  %d / %d", remaining, max))
                    .withStyle(durColor));
        }

        // Stats
        raw.add(Component.empty());
        raw.add(Component.literal("[ Stats ]").withStyle(ChatFormatting.YELLOW));

        if (isBMM) {
            float dmg = (stack.getItem() instanceof BMMWeapon bw) ? bw.effectiveDamage() : profile.damage;
            float spd = profile.speed;
            float dps = dmg * spd;
            raw.add(Component.literal(String.format("Attack Damage  %.1f", dmg)).withStyle(ChatFormatting.WHITE));
            raw.add(Component.literal(String.format("Attack Speed   %.2f/s", spd)).withStyle(ChatFormatting.WHITE));
            raw.add(Component.literal(String.format("DPS            %.1f", dps)).withStyle(ChatFormatting.WHITE));
            raw.add(Component.literal(String.format("DPS Modifier   x%.1f", profile.dpsModifier))
                    .withStyle(profile.dpsModifier >= 1.0f ? ChatFormatting.GREEN : ChatFormatting.RED));
        } else {
            float dmg = readAttr(stack, Attributes.ATTACK_DAMAGE, 1.0);
            float spd = readAttr(stack, Attributes.ATTACK_SPEED,  4.0);
            float dps = dmg * spd;
            raw.add(Component.literal(String.format("Attack Damage  %.1f", dmg)).withStyle(ChatFormatting.WHITE));
            raw.add(Component.literal(String.format("Attack Speed   %.2f/s", spd)).withStyle(ChatFormatting.WHITE));
            raw.add(Component.literal(String.format("DPS            %.1f", dps)).withStyle(ChatFormatting.WHITE));
        }

        // Traits — BMM only
        if (isBMM && !profile.traits.isEmpty()) {
            raw.add(Component.empty());
            raw.add(Component.literal("[ Traits ]").withStyle(ChatFormatting.YELLOW));
            for (WeaponTrait.Instance t : profile.traits) {
                raw.add(Component.literal("  " + traitName(t)).withStyle(ChatFormatting.AQUA));
                raw.add(Component.literal("    " + traitDesc(t)).withStyle(ChatFormatting.GRAY));
            }
        }

        // Enchantments — both BMM and non-BMM, mid-to-bottom
        ItemEnchantments enchants = stack.getEnchantments();
        if (!enchants.isEmpty()) {
            raw.add(Component.empty());
            raw.add(Component.literal("[ Enchantments ]").withStyle(ChatFormatting.YELLOW));
            for (var entry : enchants.entrySet()) {
                Holder<Enchantment> holder = entry.getKey();
                int level = entry.getIntValue();
                raw.add(Component.literal("  ").append(Enchantment.getFullname(holder, level)));
            }
        }

        // BHB — live data from WeaponRegistry for all weapons
        if (BHBCompat.isPresent()) {
            BHBProfile bhb = BHBCompat.getProfile(stack);
            if (bhb != null) {
                raw.add(Component.empty());
                raw.add(Component.literal("[ BHB ]").withStyle(ChatFormatting.YELLOW));
                // BMM weapons use their own category name — BHB's classifier would misidentify them.
                // Non-BMM weapons use BHB's category since BMM has no type info for them.
                String catDisplay = isBMM
                        ? displayName(cat)
                        : Character.toUpperCase(bhb.category().charAt(0)) + bhb.category().substring(1);
                raw.add(Component.literal("  " + catDisplay).withStyle(ChatFormatting.GRAY));

                int n = 0;
                for (BHBProfile.Attack atk : bhb.attacks()) {
                    int pct = atk.weight() * 100 / bhb.totalWeight();
                    String label;
                    ChatFormatting labelColor, statColor;

                    if (atk.speedMul() < 0.95f) {
                        label = "Heavy";     labelColor = ChatFormatting.RED;      statColor = ChatFormatting.DARK_RED;
                    } else if (atk.speedMul() > 1.05f) {
                        label = "Light";     labelColor = ChatFormatting.AQUA;     statColor = ChatFormatting.DARK_AQUA;
                    } else if (atk.damageMul() > 1.05f) {
                        label = "Heavy";     labelColor = ChatFormatting.RED;      statColor = ChatFormatting.DARK_RED;
                    } else if (atk.damageMul() < 0.95f) {
                        label = "Light";     labelColor = ChatFormatting.AQUA;     statColor = ChatFormatting.DARK_AQUA;
                    } else if (n == 0) {
                        label = "Default";   labelColor = ChatFormatting.WHITE;    statColor = ChatFormatting.GRAY;
                    } else {
                        label = "Attack " + (n + 1); labelColor = ChatFormatting.WHITE; statColor = ChatFormatting.GRAY;
                    }

                    raw.add(Component.literal(String.format("  %-8s%d%%", label, pct)).withStyle(labelColor));

                    boolean hasStats = atk.speedMul() != 1.0f || atk.damageMul() != 1.0f || atk.hitDelay() > 0;
                    if (hasStats) {
                        StringBuilder stats = new StringBuilder("    ");
                        if (atk.speedMul() != 1.0f)  stats.append(String.format("Spd x%.2f  ", atk.speedMul()));
                        if (atk.damageMul() != 1.0f) stats.append(String.format("Dmg x%.2f  ", atk.damageMul()));
                        if (atk.hitDelay() > 0)      stats.append(String.format("+%dt delay", atk.hitDelay()));
                        raw.add(Component.literal(stats.toString().stripTrailing()).withStyle(statColor));
                    }
                    n++;
                }
            }
        }

        return wrap(raw);
    }

    private List<FormattedCharSequence> wrap(List<Component> raw) {
        List<FormattedCharSequence> out = new ArrayList<>();
        for (Component c : raw) {
            List<FormattedCharSequence> split = font.split(c, TEXT_WIDTH);
            if (split.isEmpty()) {
                out.add(FormattedCharSequence.EMPTY);
            } else {
                out.addAll(split);
            }
        }
        return out;
    }

    // Reads a single attribute's final value for MAINHAND, applying player base + item modifiers.
    private static float readAttr(ItemStack stack, Holder<Attribute> attribute, double playerBase) {
        double val = playerBase;
        ItemAttributeModifiers mods = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (var e : mods.modifiers()) {
            if (!e.attribute().is(attribute)) continue;
            if (!e.slot().test(EquipmentSlot.MAINHAND)) continue;
            val = switch (e.modifier().operation()) {
                case ADD_VALUE          -> val + e.modifier().amount();
                case ADD_MULTIPLIED_BASE  -> val + playerBase * e.modifier().amount();
                case ADD_MULTIPLIED_TOTAL -> val * (1 + e.modifier().amount());
            };
        }
        return (float) val;
    }

    private static String displayName(WeaponCategory cat) {
        String[] words = cat.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0)));
            sb.append(w.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private static String displayGroup(WeaponCategory cat) {
        String g = cat.group().name();
        return Character.toUpperCase(g.charAt(0)) + g.substring(1).toLowerCase();
    }

    private static String traitName(WeaponTrait.Instance t) {
        String lv = switch (t.level()) { case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; default -> "" + t.level(); };
        return switch (t.trait()) {
            case AP -> "Armor Piercing " + lv;
            case TH -> "Two-Handed "     + lv;
            case HS -> "Hard Strike "    + lv;
            case R  -> "Rageful "        + lv;
        };
    }

    private static String traitDesc(WeaponTrait.Instance t) {
        return switch (t.trait()) {
            case AP -> switch (t.level()) {
                case 1 -> "Ignores 25% of armor";
                case 2 -> "Ignores 65% of armor";
                case 3 -> "Ignores 100% of armor";
                default -> "";
            };
            case TH -> switch (t.level()) {
                case 1 -> "+15% dmg with empty offhand";
                case 2 -> "+35% dmg with empty offhand";
                case 3 -> "+55% dmg with empty offhand";
                default -> "";
            };
            case HS -> switch (t.level()) {
                case 1 -> "-25% enemy speed for 2.0s";
                case 2 -> "-50% enemy speed for 3.0s";
                case 3 -> "-85% enemy speed for 4.0s";
                default -> "";
            };
            case R -> switch (t.level()) {
                case 1 -> "Execute if HP < 0.6x weapon dmg";
                case 2 -> "Execute if HP < 1.2x weapon dmg";
                case 3 -> "Execute if HP < 1.8x weapon dmg";
                default -> "";
            };
        };
    }
}
