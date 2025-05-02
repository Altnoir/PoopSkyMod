package com.altnoir.poopsky.sound;

import com.altnoir.poopsky.entity.ToiletPlugEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

public class TPFlySoundWrapper {
    private final ToiletPlugEntity attachedInstance;

    public TPFlySoundWrapper(ToiletPlugEntity attachedInstance) {
        this.attachedInstance = attachedInstance;
    }

    public void stop() {
        if(attachedInstance.getWorld().isClient){
            stop0();
        }
    }
    public void play() {
        if(attachedInstance.getWorld().isClient){
            play0();
        }
    }
    public void tick() {
        if(attachedInstance.getWorld().isClient){
            tick0();
        }
    }

    @Environment(EnvType.CLIENT)
    private TPFlySound bsf;

    @Environment(EnvType.CLIENT)
    private void stop0(){
        MinecraftClient.getInstance().getSoundManager().stop(bsf);
        bsf=null;
    }
    @Environment(EnvType.CLIENT)
    private void play0(){
        if(bsf==null){
            bsf = new TPFlySound(attachedInstance);
        }
        MinecraftClient.getInstance().getSoundManager().play(bsf);
    }
    @Environment(EnvType.CLIENT)
    private void tick0(){
        if(bsf!=null){
            bsf.tick();
        }
    }
}
