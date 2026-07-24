package com.altnoir.poopsky.impl.sound;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class SoundGen extends SoundDefinitionsProvider {
    public SoundGen(PackOutput output, ExistingFileHelper helper) {
        super(output, PoopSky.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        add(PoSoundEvents.FART, definition()
                .subtitle("subtitle.poopsky.fart")
                .with(
                        sound(PoopSky.loc("fart_1")),
                        sound(PoopSky.loc("fart_2"))
                ));

        add(PoSoundEvents.ENTITY_POOP_BALL_THROW, definition()
                .subtitle("subtitle.poopsky.poop_ball.throw")
                .with(
                        sound(PoopSky.locMc("entity.snowball.throw"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ITEM_TOILET_LINKER_BOOP, definition()
                .subtitle("subtitle.poopsky.item.toilet_linker.boop")
                .with(
                        sound(PoopSky.locMc("block.mud.break"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ITEM_TOILET_LINKER_SUCCESS, definition()
                .subtitle("subtitle.poopsky.item.toilet_linker.success")
                .with(
                        sound(PoopSky.locMc("block.mud.place"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_POOP_BLOCK_SLIDE, definition()
                .subtitle("subtitle.poopsky.block.poop_block.slide")
                .with(
                        sound(PoopSky.locMc("block.honey_block.slide"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_SALTPETER_CHIME, definition()
                .subtitle("subtitle.poopsky.block.saltpeter.chime")
                .with(
                        sound(PoopSky.locMc("block.amethyst_block.chime"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_COMPOOPER_MAGGOTS, definition()
                .subtitle("subtitle.poopsky.compooper.maggots")
                .with(
                        sound(PoopSky.locMc("block.pointed_dripstone.drip_water_into_cauldron"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_COMPOOPER_BUCKET_FILL, definition()
                .subtitle("subtitle.poopsky.block.compooper.bucket_fill")
                .with(
                        sound(PoopSky.locMc("item.bucket.fill"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_COMPOOPER_BUCKET_FILL_LAVA, definition()
                .subtitle("subtitle.poopsky.block.compooper.bucket_fill")
                .with(
                        sound(PoopSky.locMc("item.bucket.fill_lava"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLY_BARREL_OPEN, definition()
                .subtitle("subtitle.poopsky.fly_barrel.open")
                .with(
                        sound(PoopSky.locMc("block.barrel.open"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLY_BARREL_CLOSE, definition()
                .subtitle("subtitle.poopsky.fly_barrel.close")
                .with(
                        sound(PoopSky.locMc("block.barrel.close"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLUSH_TOILET_OPEN, definition()
                .subtitle("subtitle.poopsky.block.flush_toilet.open")
                .with(
                        sound(PoopSky.locMc("block.bamboo_wood_trapdoor.open"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLUSH_TOILET_CLOSE, definition()
                .subtitle("subtitle.poopsky.block.flush_toilet.close")
                .with(
                        sound(PoopSky.locMc("block.bamboo_wood_trapdoor.close"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLY_BARREL_WORK, definition()
                .subtitle("subtitle.poopsky.fly_barrel.work")
                .with(
                        sound(PoopSky.locMc("block.beehive.work"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_BREEDING_CHEST_WORK, definition()
                .subtitle("subtitle.poopsky.breeding_chest.work")
                .with(
                        sound(PoopSky.locMc("block.beehive.work"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_TOILET_LAVA_EMPTY, definition()
                .subtitle("subtitle.poopsky.block.toilet.lava_empty")
                .with(
                        sound(PoopSky.locMc("item.bucket.empty_lava"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_VILLAGER_WORK_COMPOOPER, definition()
                .subtitle("subtitle.poopsky.villager.work_compooper")
                .with(
                        sound(PoopSky.loc("fart_1")),
                        sound(PoopSky.loc("fart_2")),
                        sound(PoopSky.loc("fart_1")).pitch(0.6),
                        sound(PoopSky.loc("fart_2")).pitch(1.4)
                ));

        add(PoSoundEvents.ENTITY_VILLAGER_WORK_TOILET, definition()
                .subtitle("subtitle.poopsky.villager.work_toilet")
                .with(
                        sound(PoopSky.locMc("entity.generic.eat"), SoundDefinition.SoundType.EVENT),
                        sound(PoopSky.locMc("entity.generic.drink"), SoundDefinition.SoundType.EVENT),
                        sound(PoopSky.locMc("entity.player.burp"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_ATTACK, definition()
                .subtitle("subtitle.poopsky.poolime.attack")
                .with(
                        sound(PoopSky.locMc("entity.slime.attack"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_DEATH, definition()
                .subtitle("subtitle.poopsky.poolime.death")
                .with(
                        sound(PoopSky.locMc("entity.slime.death"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_DEATH_SMALL, definition()
                .subtitle("subtitle.poopsky.poolime.death")
                .with(
                        sound(PoopSky.locMc("entity.slime.death_small"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_HURT, definition()
                .subtitle("subtitle.poopsky.poolime.hurt")
                .with(
                        sound(PoopSky.locMc("entity.slime.hurt"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_HURT_SMALL, definition()
                .subtitle("subtitle.poopsky.poolime.hurt")
                .with(
                        sound(PoopSky.locMc("entity.slime.hurt_small"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_JUMP, definition()
                .subtitle("subtitle.poopsky.poolime.squish")
                .with(
                        sound(PoopSky.locMc("entity.slime.jump"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_JUMP_SMALL, definition()
                .subtitle("subtitle.poopsky.poolime.squish")
                .with(
                        sound(PoopSky.locMc("entity.slime.jump_small"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_SQUISH, definition()
                .subtitle("subtitle.poopsky.poolime.squish")
                .with(
                        sound(PoopSky.locMc("entity.slime.squish"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_SQUISH_SMALL, definition()
                .subtitle("subtitle.poopsky.poolime.squish")
                .with(
                        sound(PoopSky.locMc("entity.slime.squish_small"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POP_PRIMED, definition()
                .subtitle("subtitle.poopsky.pop.primed")
                .with(
                        sound(PoopSky.locMc("entity.tnt.primed"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_FLY_AMBIENT, definition()
                .subtitle("subtitle.poopsky.fly.ambient")
                .with(
                        sound(PoopSky.locMc("entity.bee.loop_aggressive"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_FLY_HURT, definition()
                .subtitle("subtitle.poopsky.fly.hurt")
                .with(
                        sound(PoopSky.locMc("entity.bee.hurt"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_FLY_DEATH, definition()
                .subtitle("subtitle.poopsky.fly.death")
                .with(
                        sound(PoopSky.locMc("entity.bee.death"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_FLY_CAPTURE, definition()
                .subtitle("subtitle.poopsky.fly.capture")
                .with(
                        sound(PoopSky.locMc("block.beehive.exit"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ITEM_JINKELA_USE, definition()
                .subtitle("subtitle.poopsky.item.jinkela.use")
                .with(
                        sound(PoopSky.locMc("item.bone_meal.use"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ITEM_TIME_BELL_OPEN, definition()
                .subtitle("subtitle.poopsky.item.time_bell.open")
                .with(
                        sound(PoopSky.loc("the_world_open"))
                ));

        add(PoSoundEvents.ITEM_TIME_BELL_CLOSE, definition()
                .subtitle("subtitle.poopsky.item.time_bell.close")
                .with(
                        sound(PoopSky.loc("the_world_close"))
                ));

        add(PoSoundEvents.POOPSKY_INTRO, definition()
                .with(
                        sound(PoopSky.loc("poopsky_intro")).preload()
                ));

        add(PoSoundEvents.LAWRENCE, definition()
                .with(
                        sound(PoopSky.loc("merry_christmas_mr_lawrence")).stream()
                ));

        add(PoSoundEvents.LIGHT_DANCE, definition()
                .with(
                        sound(PoopSky.loc("light_dance")).stream()
                ));

        add(PoSoundEvents.MOON_BOWL, definition()
                .with(
                        sound(PoopSky.loc("moon_bowl")).stream()
                ));
    }
}
