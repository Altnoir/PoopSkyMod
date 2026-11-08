package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.compat.kubejs.PoKubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.latvian.mods.kubejs.script.ScriptManager", remap = false)
public abstract class KubeScriptManagerMixin {
    @Inject(method = "load", at = @At("RETURN"), remap = false)
    private void poopsky$afterScriptsLoaded(CallbackInfo ci) {
        PoKubeJSPlugin.afterScriptsLoaded((ScriptManager) (Object) this);
    }
}
