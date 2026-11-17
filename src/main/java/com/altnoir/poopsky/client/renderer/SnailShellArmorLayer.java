package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.content.item.p.SnailShellItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

public class SnailShellArmorLayer extends HumanoidArmorLayer<AbstractClientPlayer,
        PlayerModel<AbstractClientPlayer>, HumanoidModel<AbstractClientPlayer>> {
    private SnailShellArmorLayer(PlayerRenderer renderer, EntityRendererProvider.Context context, boolean slim) {
        super(renderer,
                new HumanoidModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager());
    }

    public static void registerLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                var layers = renderer.layers;
                for (int i = 0; i < layers.size(); i++) {
                    if (layers.get(i).getClass() == HumanoidArmorLayer.class) {
                        layers.set(i, new SnailShellArmorLayer(renderer, event.getContext(), skin == PlayerSkin.Model.SLIM));
                    }
                }
            }
        }
    }

    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (event.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SnailShellItem) {
            var model = event.getRenderer().getModel();
            model.leftLeg.visible = false;
            model.rightLeg.visible = false;
            model.leftPants.visible = false;
            model.rightPants.visible = false;
        }
    }

    @Override
    protected Model getArmorModelHook(AbstractClientPlayer player, ItemStack stack, EquipmentSlot slot,
            HumanoidModel<AbstractClientPlayer> model) {
        if ((slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET)
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SnailShellItem) {
            model.setAllVisible(false);
            return model;
        }
        return super.getArmorModelHook(player, stack, slot, model);
    }
}
