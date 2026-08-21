package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class PoAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, PoopSky.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Long>> POOP_TIME = ATTACHMENTS
            .register("poop_time", () -> AttachmentType.builder(() -> 0L)
                    .serialize(Codec.LONG.fieldOf("value"))
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> SEEN_INTRO = ATTACHMENTS
            .register("seen_intro", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("value"))
                    .copyOnDeath()
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> SEEN_POEM = ATTACHMENTS
            .register("seen_poem", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("value"))
                    .copyOnDeath()
                    .build());

    private PoAttachments() {
    }

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }
}
