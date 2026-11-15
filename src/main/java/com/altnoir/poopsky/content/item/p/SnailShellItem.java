package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

import java.util.List;

public class SnailShellItem extends ArmorItem {
    private final ResourceLocation texture;

    public SnailShellItem(String color, Properties properties) {
        super(ArmorMaterials.TURTLE, Type.CHESTPLATE, properties.durability(240));
        this.texture = PoopSky.loc("textures/entity/snail/" + color + "_snail.png");
    }

    @Override
    public int getDefense() {
        return 4;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(PoItems.SNAIL_SHELL_FRAGMENT.get());
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity,
            EquipmentSlot slot, ArmorMaterial.Layer layer, boolean inner) {
        return texture;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.poopsky.equippable", Component.translatable("tooltip.poopsky.equipment_slot.chest")).withStyle(ChatFormatting.GRAY));
    }
}
