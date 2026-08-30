package com.altnoir.poopsky.data.sound;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class SoundGen extends SoundDefinitionsProvider {
    public SoundGen(PackOutput output) {
        super(output, PoopSky.MOD_ID);
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
                        sound(PoopSky.mcloc("entity.snowball.throw"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ITEM_TOILET_LINKER_BOOP, definition()
                .subtitle("subtitle.poopsky.item.toilet_linker.boop")
                .with(
                        sound(PoopSky.mcloc("block.mud.break"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ITEM_TOILET_LINKER_SUCCESS, definition()
                .subtitle("subtitle.poopsky.item.toilet_linker.success")
                .with(
                        sound(PoopSky.mcloc("block.mud.place"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_POOP_BLOCK_SLIDE, definition()
                .subtitle("subtitle.poopsky.block.poop_block.slide")
                .with(
                        sound(PoopSky.mcloc("block.honey_block.slide"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_SALTPETER_CHIME, definition()
                .subtitle("subtitle.poopsky.block.saltpeter.chime")
                .with(
                        sound(PoopSky.mcloc("block.amethyst_block.chime"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_COMPOOPER_MAGGOTS, definition()
                .subtitle("subtitle.poopsky.compooper.maggots")
                .with(
                        sound(PoopSky.mcloc("block.pointed_dripstone.drip_water_into_cauldron"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_COMPOOPER_EMPTY, definition()
                .subtitle("subtitle.poopsky.block.compooper.empty")
                .with(sound(PoopSky.mcloc("block.composter.empty"), SoundDefinition.SoundType.EVENT)));

        add(PoSoundEvents.BLOCK_COMPOOPER_FILL, definition()
                .subtitle("subtitle.poopsky.block.compooper.fill")
                .with(sound(PoopSky.mcloc("block.composter.fill"), SoundDefinition.SoundType.EVENT)));

        add(PoSoundEvents.BLOCK_COMPOOPER_FILL_SUCCESS, definition()
                .subtitle("subtitle.poopsky.block.compooper.fill")
                .with(sound(PoopSky.mcloc("block.composter.fill_success"), SoundDefinition.SoundType.EVENT)));

        add(PoSoundEvents.BLOCK_COMPOOPER_READY, definition()
                .subtitle("subtitle.poopsky.block.compooper.ready")
                .with(sound(PoopSky.mcloc("block.composter.ready"), SoundDefinition.SoundType.EVENT)));

        add(PoSoundEvents.BLOCK_COMPOOPER_BUCKET_FILL, definition()
                .subtitle("subtitle.poopsky.block.compooper.bucket_fill")
                .with(
                        sound(PoopSky.mcloc("item.bucket.fill"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_COMPOOPER_BUCKET_FILL_LAVA, definition()
                .subtitle("subtitle.poopsky.block.compooper.bucket_fill")
                .with(
                        sound(PoopSky.mcloc("item.bucket.fill_lava"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLY_BARREL_OPEN, definition()
                .subtitle("subtitle.poopsky.fly_barrel.open")
                .with(
                        sound(PoopSky.mcloc("block.barrel.open"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLY_BARREL_CLOSE, definition()
                .subtitle("subtitle.poopsky.fly_barrel.close")
                .with(
                        sound(PoopSky.mcloc("block.barrel.close"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLUSH_TOILET_OPEN, definition()
                .subtitle("subtitle.poopsky.block.flush_toilet.open")
                .with(
                        sound(PoopSky.mcloc("block.bamboo_wood_trapdoor.open"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLUSH_TOILET_CLOSE, definition()
                .subtitle("subtitle.poopsky.block.flush_toilet.close")
                .with(
                        sound(PoopSky.mcloc("block.bamboo_wood_trapdoor.close"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_FLY_BARREL_WORK, definition()
                .subtitle("subtitle.poopsky.fly_barrel.work")
                .with(
                        sound(PoopSky.mcloc("block.beehive.work"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_BREEDING_CHEST_WORK, definition()
                .subtitle("subtitle.poopsky.breeding_chest.work")
                .with(
                        sound(PoopSky.mcloc("block.beehive.work"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_TOILET_LAVA_EMPTY, definition()
                .subtitle("subtitle.poopsky.block.toilet.lava_empty")
                .with(
                        sound(PoopSky.mcloc("item.bucket.empty_lava"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.BLOCK_MAGGOTS_CHUNK_LOADER_ACTIVATE, definition()
                .subtitle("subtitle.poopsky.block.maggots_chunk_loader.activate")
                .with(sound(PoopSky.mcloc("block.beacon.activate"), SoundDefinition.SoundType.EVENT)));

        add(PoSoundEvents.BLOCK_MAGGOTS_CHUNK_LOADER_AMBIENT, definition()
                .subtitle("subtitle.poopsky.block.maggots_chunk_loader.ambient")
                .with(sound(PoopSky.mcloc("block.beacon.ambient"), SoundDefinition.SoundType.EVENT)));

        add(PoSoundEvents.BLOCK_MAGGOTS_CHUNK_LOADER_DEACTIVATE, definition()
                .subtitle("subtitle.poopsky.block.maggots_chunk_loader.deactivate")
                .with(sound(PoopSky.mcloc("block.beacon.deactivate"), SoundDefinition.SoundType.EVENT)));

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
                        sound(PoopSky.mcloc("entity.generic.eat"), SoundDefinition.SoundType.EVENT),
                        sound(PoopSky.mcloc("entity.generic.drink"), SoundDefinition.SoundType.EVENT),
                        sound(PoopSky.mcloc("entity.player.burp"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_ATTACK, definition()
                .subtitle("subtitle.poopsky.poolime.attack")
                .with(
                        sound(PoopSky.mcloc("entity.slime.attack"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_DEATH, definition()
                .subtitle("subtitle.poopsky.poolime.death")
                .with(
                        sound(PoopSky.mcloc("entity.slime.death"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_DEATH_SMALL, definition()
                .subtitle("subtitle.poopsky.poolime.death")
                .with(
                        sound(PoopSky.mcloc("entity.slime.death_small"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_HURT, definition()
                .subtitle("subtitle.poopsky.poolime.hurt")
                .with(
                        sound(PoopSky.mcloc("entity.slime.hurt"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_HURT_SMALL, definition()
                .subtitle("subtitle.poopsky.poolime.hurt")
                .with(
                        sound(PoopSky.mcloc("entity.slime.hurt_small"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_JUMP, definition()
                .subtitle("subtitle.poopsky.poolime.squish")
                .with(
                        sound(PoopSky.mcloc("entity.slime.jump"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_JUMP_SMALL, definition()
                .subtitle("subtitle.poopsky.poolime.squish")
                .with(
                        sound(PoopSky.mcloc("entity.slime.jump_small"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_SQUISH, definition()
                .subtitle("subtitle.poopsky.poolime.squish")
                .with(
                        sound(PoopSky.mcloc("entity.slime.squish"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POOLIME_SQUISH_SMALL, definition()
                .subtitle("subtitle.poopsky.poolime.squish")
                .with(
                        sound(PoopSky.mcloc("entity.slime.squish_small"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_POP_PRIMED, definition()
                .subtitle("subtitle.poopsky.pop.primed")
                .with(
                        sound(PoopSky.mcloc("entity.tnt.primed"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_FLY_AMBIENT, definition()
                .subtitle("subtitle.poopsky.fly.ambient")
                .with(
                        sound(PoopSky.mcloc("entity.bee.loop_aggressive"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_FLY_HURT, definition()
                .subtitle("subtitle.poopsky.fly.hurt")
                .with(
                        sound(PoopSky.mcloc("entity.bee.hurt"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_FLY_DEATH, definition()
                .subtitle("subtitle.poopsky.fly.death")
                .with(
                        sound(PoopSky.mcloc("entity.bee.death"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ENTITY_FLY_CAPTURE, definition()
                .subtitle("subtitle.poopsky.fly.capture")
                .with(
                        sound(PoopSky.mcloc("block.beehive.exit"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ITEM_JINKELA_USE, definition()
                .subtitle("subtitle.poopsky.item.jinkela.use")
                .with(
                        sound(PoopSky.mcloc("item.bone_meal.use"), SoundDefinition.SoundType.EVENT)
                ));

        add(PoSoundEvents.ITEM_TIME_BELL_OPEN, definition()
                .subtitle("subtitle.poopsky.item.time_bell.open")
                .with(sound(PoopSky.loc("the_world_open")).stream()));

        add(PoSoundEvents.ITEM_TIME_BELL_CLOSE, definition()
                .subtitle("subtitle.poopsky.item.time_bell.close")
                .with(sound(PoopSky.loc("the_world_close"))));

        add(PoSoundEvents.POOPSKY_INTRO, definition()
                .with(sound(PoopSky.loc("poopsky_intro"))));

        add(PoSoundEvents.THEME, definition()
                .with(sound(PoopSky.loc("disc/theme")).stream()));

        add(PoSoundEvents.LAWRENCE, definition()
                .with(sound(PoopSky.loc("disc/merry_christmas_mr_lawrence")).stream()));

        add(PoSoundEvents.LIGHT_DANCE, definition()
                .with(sound(PoopSky.loc("disc/light_dance")).stream()));

        add(PoSoundEvents.MOON_BOWL, definition()
                .with(sound(PoopSky.loc("disc/moon_bowl")).stream()));


        add(PoSoundEvents.POINT, definition()
                .subtitle("subtitle.poopsky.game.point")
                .with(sound(PoopSky.loc("game/point"))));
        add(PoSoundEvents.NEW_BEST, definition()
                .subtitle("subtitle.poopsky.game.new_best")
                .with(sound(PoopSky.loc("game/new_best"))));
        add(PoSoundEvents.GAME_OVER, definition()
                .subtitle("subtitle.poopsky.game.game_over")
                .with(sound(PoopSky.loc("game/game_over"))));
        add(PoSoundEvents.EXPLOSION, definition()
                .subtitle("subtitle.poopsky.game.explosion")
                .with(sound(PoopSky.loc("game/explosion"))));
        add(PoSoundEvents.SHOOT, definition()
                .subtitle("subtitle.poopsky.game.shoot")
                .with(sound(PoopSky.loc("game/shoot"))));
        add(PoSoundEvents.SWING, definition()
                .subtitle("subtitle.poopsky.game.swing")
                .with(sound(PoopSky.loc("game/swing"))));
    }
}