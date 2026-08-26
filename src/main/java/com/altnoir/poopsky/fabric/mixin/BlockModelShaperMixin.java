package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.client.ToiletClientBlockExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(BlockModelShaper.class)
public abstract class BlockModelShaperMixin {
    @Inject(method = "getParticleIcon", at = @At("HEAD"), cancellable = true)
    private void poopsky$useToiletDestroyTexture(BlockState state,
                                                 CallbackInfoReturnable<TextureAtlasSprite> callback) {
        var texture = ToiletClientBlockExtensions.getDestroyParticleTexture();
        if (texture == null) return;

        callback.setReturnValue(Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture));
    }
}
