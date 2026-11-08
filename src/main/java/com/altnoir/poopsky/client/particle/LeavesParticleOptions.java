package com.altnoir.poopsky.client.particle;

import com.altnoir.poopsky.init.PoParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.ByteBufCodecs;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;

public record LeavesParticleOptions(int color) implements ParticleOptions {
    public static final MapCodec<LeavesParticleOptions> CODEC = Codec.INT
            .fieldOf("color")
            .xmap(LeavesParticleOptions::new, LeavesParticleOptions::color);
    public static final StreamCodec<? super FriendlyByteBuf, LeavesParticleOptions> STREAM_CODEC = ByteBufCodecs.INT
            .map(LeavesParticleOptions::new, LeavesParticleOptions::color);

    public static final ParticleOptions.Deserializer<LeavesParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override public LeavesParticleOptions fromCommand(ParticleType<LeavesParticleOptions> type, com.mojang.brigadier.StringReader reader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
            reader.expect(' ');
            return new LeavesParticleOptions(reader.readInt());
        }
        @Override public LeavesParticleOptions fromNetwork(ParticleType<LeavesParticleOptions> type, FriendlyByteBuf buffer) { return new LeavesParticleOptions(buffer.readInt()); }
    };
    @Override public void writeToNetwork(FriendlyByteBuf buffer) { buffer.writeInt(color); }
    @Override public String writeToString() { return net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.getKey(getType()) + " " + color; }
    @Override
    public ParticleType<LeavesParticleOptions> getType() {
        return PoParticles.LEAVES_PARTICLE.get();
    }
}
