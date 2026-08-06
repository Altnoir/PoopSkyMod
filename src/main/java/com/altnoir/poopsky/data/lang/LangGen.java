package com.altnoir.poopsky.data.lang;

import com.altnoir.poopsky.PoItemGroups;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoPotions;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;

import java.util.Map;

public final class LangGen {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();
    private static RegistrateLangProvider provider; // 静态字段

    private LangGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.LANG, LangGen::generateEnUs);
    }

    private static void generateEnUs(RegistrateLangProvider provider) {
        LangGen.provider = provider;
        addAdvancements();
        addToiletTypes();
        addFlyTypes();
        addPotions();
        addItemExtras();
        addKeybindings();
        addMessages();
        addSubtitles();
        addStats();
        addItemGroups();
        addMisc();
        addTags();
        addTooltips();
        addJei();
        addCreate();
        addTasks();
        addConfigs();
    }

    private static void addAdvancements() {
        addAdvancement("root", "POOPSKY", "It all began with a pile of poop");
        addAdvancement("poop_block_slide", "Master of Poop Sliding", "Slide on a poop block");
        addAdvancement("saltpeter_cluster", "Potassium Nitrate", "Place a Poop Block below a Toilet, and Saltpeter Clusters will randomly grow around it\nSaltpeter Clusters can be matured with Poop");
        addAdvancement("powder_snow", "Chemical Ice Making", "When Saltpeter Clusters are waterlogged, they freeze. When attached to ice and waterlogged, they turn into Powder Snow");
        addAdvancement("poop_sapling", "Fecalith", "Grow a Poop Block to obtain a Poop Sapling");
        addAdvancement("sapling", "Going Green", "Obtain a Sapling Poop Ball from composting, then use it");
        addAdvancement("maggots", "Maggotchain", "Obtain Maggots from a Compooper filled with poop\nMaggots can be planted as crops and crafted into blocks");
        addAdvancement("summon_villager", "Playing God", "Place a Carved Pumpkin on a Poop Block");
        addAdvancement("toilet_plug", "\"Magical Girl\"", "Buy a Toilet Plug from a Poopmaker");
        addAdvancement("placer", "Midas Touch", "Craft a Placer using a Toilet Plug");
        addAdvancement("omen_armor", "Avatar of Omen", "Upgrade Golden Equipment into Omen Armor");
        addAdvancement("chili", "Gunpowder Fruit", "Sieve a Cactus on a Sieve to obtain a King of Dragon Fruit");
        addAdvancement("chili_poop", "Chili Poop", "While affected by Intestinal Spasm, you can poop out Chili Poop\nThe crafted Chili Poop Block can be grown with bone meal");
        addAdvancement("rainbow_toilet", "Rainbow Toilet", "Buy a Rainbow Toilet from a Poopmaker");
        addAdvancement("fly_catcher", "Fly-Catching Expert", "Capture a fly with a Fly Catcher");
        addAdvancement("fly_barrel", "Billions in a Second", "Craft a Fly Barrel");
        addAdvancement("breeding_chest", "Gene Splicer", "Craft a Breeding Chest");
        addAdvancement("foliium_senna", "Folium Sennae", "Fish in non-open water for a chance to obtain Folium Sennae");
        addAdvancement("king_of_dragon_fruit", "Gunpowder Fruit", "Sieve a Cactus on a Sieve to obtain a King of Dragon Fruit");
        addAdvancement("poolime_maggots_block", "Poolime Maggot Block", "Craft a Poolime Maggot Block using Poop Blocks");
        addAdvancement("pooop_tnt", "POP", "A special TNT that can crush cobblestone and gravel, with partial destructive power");
        addAdvancement("poop_ball", "Jinkela!", "Kill a Poolime to obtain a Poop Ball");
        addAdvancement("poolime_block", "Artificial Slime Block", "Crafted from Poop Balls, can be dyed to become Slime Blocks");
        addAdvancement("brown_tile_block", "Clean and Sanitary", "Smelt Poolime Blocks to obtain Brown Tiles, which can be dyed into other color variants");
        addAdvancement("pointed_dripstone", "Artificial Dirt", "Place a solid block under a Poop Block, then place a Pointed Dripstone under the solid block and wait for it to naturally turn into Dirt");
        addAdvancement("roundworm", "Parasite", "Roundworms can be grown with bone meal just like glow berries");
        addAdvancement("sea_poop_ball", "A Crappy Catch", "Fish in Poop Liquid to obtain a Sea Poop Ball");
        addAdvancement("sieve", "Ex Nihilo", "Sieve the entire world out of poop!");
        addAdvancement("string", "Dried Roundworm", "Roast a Roundworm on a Campfire to obtain String");
        addAdvancement("urine_compooper", "Night-Soil Collector", "Use a Compooper to scoop liquid poop out of a toilet");
        addAdvancement("wither_poop_ball", "Severely Poisoned!", "Craft a Wither Poop Ball using a Poop Ball and a Wither Rose");
        addAdvancement("compooper", "Compooper", "Break a Poop Log to craft a Compooper\nCompoopers can also collect rainwater and snow");
        addAdvancement("fly", "!? Fly?!", "Place a Carved Pumpkin on a Maggot Block");
        addAdvancement("coal_block", "Carbonized Poop", "Encase a Poop Log in solid blocks and wait for it to turn into a Block of Coal");
        addAdvancement("cocoa", "\"Chocolate\"", "Roast Poop on a Campfire to obtain Cocoa Beans");


    }

    private static void addAdvancement(String key, String title, String desc) {
        if (!title.isEmpty()) {
            provider.add("advancements.poopsky." + key + ".title", title);
        }
        if (!desc.isEmpty()) {
            provider.add("advancements.poopsky." + key + ".description", desc);
        }
    }

    private static void addToiletTypes() {
        for (ToiletType type : ToiletType.getAll().values()) {
            String nameKey = type.nameKey();
            if (nameKey != null && nameKey.startsWith("block.poopsky.toilet.")) {
                String key = nameKey.substring("block.poopsky.toilet.".length());
                String displayName = formatToiletName(key);
                addToilet(key, displayName);
            }
        }
    }

    private static String formatToiletName(String key) {
        StringBuilder sb = new StringBuilder();
        String[] parts = key.split("_");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(' ');
            String part = parts[i];
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1));
        }
        return sb.toString();
    }

    private static void addToilet(String key, String value) {
        provider.add("block.poopsky.toilet." + key, value);
    }

    private static void addPotions() {
        for (var entry : PoPotions.all()) {
            String path = entry.getId().getPath();
            String baseName = path.replace("_potion", "");
            String potionName = potionDisplayName(baseName);
            String arrowName = arrowDisplayName(baseName);

            provider.add("item.minecraft.potion.effect." + path, potionName);
            provider.add("item.minecraft.splash_potion.effect." + path, potionName);
            provider.add("item.minecraft.lingering_potion.effect." + path, potionName);
            provider.add("item.minecraft.tipped_arrow.effect." + path, arrowName);
        }
    }

    private static String potionDisplayName(String baseName) {
        String name = baseName;
        if (name.startsWith("long_")) name = name.substring(5);
        else if (name.startsWith("strong_")) name = name.substring(7);
        else if (name.startsWith("super_")) name = name.substring(6);
        return titleCase(name);
    }

    private static String arrowDisplayName(String baseName) {
        String name = baseName.startsWith("long_") ? baseName.substring(5) : baseName;
        return titleCase(name);
    }

    private static String titleCase(String snakeCase) {
        String[] parts = snakeCase.split("_");
        var sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(" ");
            String part = parts[i];
            if (i == 0 || !isMinorWord(part)) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) sb.append(part.substring(1));
            } else {
                sb.append(part);
            }
        }
        return sb.toString();
    }

    private static boolean isMinorWord(String word) {
        return switch (word) {
            case "the", "of", "a", "an", "in", "on", "at", "to", "for" -> true;
            default -> false;
        };
    }

    private static void addItemExtras() {
        provider.add("upgrade.poopsky.omen_upgrade", "Omen Upgrade");
        provider.add("item.poopsky.smithing_template.omen_upgrade.applies_to", "Golden Equipment");
        provider.add("item.poopsky.smithing_template.omen_upgrade.ingredients", "Ominous Filthy Ingot");
        provider.add("item.poopsky.smithing_template.omen_upgrade.base_slot_description", "Upgrade golden equipment here.");
        provider.add("item.poopsky.smithing_template.omen_upgrade.additions_slot_description", "Add Ominous Filthy Ingot here.");
        provider.add("item.poopsky.music_disc_lawrence.desc", "Ryuichi Sakamoto - Merry Christmas Mr. Lawrence");
        provider.add("item.poopsky.music_disc_light_dance.desc", "Sakanaction - Light Dance");
        provider.add("item.poopsky.music_disc_moon_bowl.desc", "Sakanaction - Moon Bowl");
    }

    private static void addKeybindings() {
        provider.add("key.category.poopsky", "POOPSKY");
        provider.add("key.poopsky.dismount_plug", "Dismount Toilet Plug");
        provider.add("key.poopsky.down", "Descend");
        provider.add("key.poopsky.up", "Up");
        provider.add("key.poopsky.use_plug", "Summon Toilet Plug");
    }

    private static void addMessages() {
        provider.add("message.poopsky.time_bell.frozen", "Game time frozen");
        provider.add("message.poopsky.time_bell.unfrozen", "Game time unfrozen");
        provider.add("message.poopsky.toilet_linker.1", "Link Toilet ①");
        provider.add("message.poopsky.toilet_linker.2", "Link Toilet ②");
        provider.add("message.poopsky.toilet_linker.3", "Linked");
        provider.add("message.poopsky.toilet_linker.4", "Cleared");
        provider.add("message.poopsky.toilet_plug.dismount", "Press %s to dismount Toilet Plug");
    }

    private static void addFlyTypes() {
        for (String id : FlyType.FLY_TYPES) {
            addFlyType(id, formatFlyTypeName(id));
        }
    }

    private static String formatFlyTypeName(String id) {
        StringBuilder sb = new StringBuilder();
        String[] parts = id.split("_");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(' ');
            String part = parts[i];
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1));
        }
        sb.append(" Fly");
        return sb.toString();
    }

    private static void addFlyType(String key, String value) {
        provider.add("fly_type.poopsky." + key, value);
    }

    private static void addSubtitles() {
        addSubtitle("fart", "Fart Sound");
        addSubtitle("block.poop_block.slide", "Poop block slides");
        addSubtitle("compooper.maggots", "Compooper maggots");
        addSubtitle("fly_barrel.open", "Fly Barrel opens");
        addSubtitle("fly_barrel.close", "Fly Barrel closes");
        addSubtitle("fly_barrel.work", "Fly Barrel buzzes");
        addSubtitle("breeding_chest.work", "Breeding Box buzzes");
        addSubtitle("villager.work_compooper", "Poopmaker works");
        addSubtitle("villager.work_toilet", "Gastronome eats");
        addSubtitle("poolime.attack", "Poolime attacks");
        addSubtitle("poolime.death", "Poolime dies");
        addSubtitle("poolime.hurt", "Poolime hurts");
        addSubtitle("poolime.squish", "Poolime squishes");
        addSubtitle("poop_ball.throw", "Poop Ball flies");
        addSubtitle("fly.ambient", "Fly buzzes");
        addSubtitle("fly.hurt", "Fly hurts");
        addSubtitle("fly.death", "Fly dies");
        addSubtitle("fly.capture", "Fly captured");
        addSubtitle("pop.primed", "POP fizzes");
        addSubtitle("item.toilet_linker.boop", "Toilet Plug Wand links");
        addSubtitle("item.toilet_linker.success", "Toilet Plug Wand binds");
        addSubtitle("block.saltpeter.chime", "Saltpeter chimes");
        addSubtitle("block.toilet.lava_empty", "Toilet empties lava");
        addSubtitle("block.flush_toilet.open", "Toilet lid opens");
        addSubtitle("block.flush_toilet.close", "Toilet lid closes");
        addSubtitle("block.compooper.bucket_fill", "Compooper fills");
        addSubtitle("item.jinkela.use", "Jinkela rustles");
        addSubtitle("item.time_bell.open", "Time Bell activates");
        addSubtitle("item.time_bell.close", "Time Bell deactivates");
    }

    private static void addSubtitle(String key, String value) {
        provider.add("subtitle.poopsky." + key, value);
    }


    private static void addStats() {
        provider.add("stat.poopsky.poop_stats", "Poop Stats");
        provider.add("stat.poopsky.inspect_placer", "Inspect Placer Stats");
        provider.add("stat.poopsky.poop_stat", "Poop Stat");
    }

    private static void addItemGroups() {
        Map<String, String> labels = Map.of(
                PoItemGroups.POOPSKY_TAB_KEY, "POOPSKY",
                PoItemGroups.POOPSKY_DECO_TAB_KEY, "PoopSky Builder Block",
                PoItemGroups.TS_ITEMS.translationKey(), "Basic Items",
                PoItemGroups.TS_BLOCKS.translationKey(), "Functional Blocks",
                PoItemGroups.TS_FLIES.translationKey(), "Flies",
                PoItemGroups.TS_POTIONS.translationKey(), "Potions",
                PoItemGroups.TS_DECO_MATERIALS.translationKey(), "Building Blocks",
                PoItemGroups.TS_DECO_TILES.translationKey(), "Tiles",
                PoItemGroups.TS_DECO_TOILETS.translationKey(), "Toilets"
        );
        for (String key : PoItemGroups.translationKeys()) {
            provider.add(key, labels.get(key));
        }
    }

    private static void addMisc() {
        provider.add("container.placer", "Placer");
        provider.add("generator.poopsky.poopsky", "PoopSky");
        provider.add("death.attack.roundworm", "%1$s died from Roundworm");
        provider.add("death.attack.roundworm.player", "%1$s was killed by %2$s by Roundworm");
        provider.add("death.attack.poop_ball", "%1$s died from Poop Ball");
        provider.add("death.attack.poop_ball.player", "%1$s was killed by %2$s by Poop Ball");
        provider.add("pack.poopsky.name", "Cognitive Filter");

        // Effects
        provider.add("effect.poopsky.fecal_incontinence", "Fecal Incontinence");
        provider.add("effect.poopsky.intestinal_spasm", "Intestinal Spasm");
        provider.add("effect.poopsky.on_the_verge", "On the Verge");
        provider.add("effect.poopsky.omener", "Omener");
        provider.add("effect.poopsky.seedbed_curse", "Seedbed Curse");
        provider.add("effect.poopsky.bleeding", "Bleeding");
        provider.add("effect.poopsky.moment_of_ptyme", "Moment of Ptyme");

        // Blocks
        provider.add("block.poopsky.urine", "Urine");
        provider.add("block.poopsky.toilet_block", "Toilet");
        provider.add("block.poopsky.toilet_format", "%s Toilet");
        provider.add("block.poopsky.rainbow_toilet", "Rainbow Toilet");

        // Containers
        provider.add("container.poopsky.fly_barrel", "Fly Barrel");
        provider.add("container.poopsky.breeding_chest", "Breeding Box");
        provider.add("container.poopsky.flush_toilet", "Flush Toilet");

        // Painting
        provider.add("painting.poopsky.poop.title", "Poop");
        provider.add("painting.poopsky.poop.author", "yinianzhihai");
        provider.add("painting.poopsky.poop_king.title", "Poop King");
        provider.add("painting.poopsky.poop_king.author", "Xiris");
        provider.add("painting.poopsky.toilet.title", "Within Reach");
        provider.add("painting.poopsky.toilet.author", "EEK");
        provider.add("painting.poopsky.vip.title", "Membership-based Restaurant");
        provider.add("painting.poopsky.vip.author", "EEK");

        // Entities
        provider.add("entity.minecraft.villager.poopsky.poopmaker", "Poopmaker");
        provider.add("entity.minecraft.villager.poopsky.gastronome", "Gastronome");
        provider.add("entity.minecraft.villager.poopmaker", "Poopmaker");
        provider.add("entity.minecraft.villager.gastronome", "Gastronome");
        provider.add("entity.poopsky.toilet_plug", "Toilet Plug");
        provider.add("entity.poopsky.stool_entity", "Poop Stool");
        provider.add("entity.poopsky.poolime", "Poolime");
        provider.add("entity.poopsky.fly", "Fly");
        provider.add("entity.poopsky.poop_tnt", "POP");
    }

    private static void addTags() {
        provider.add("tag.item.poopsky.compooper_saplings", "Compooper can produce saplings");
        provider.add("tag.item.poopsky.can_compooper", "Can be composted");
    }

    private static void addTooltips() {
        provider.add("tooltip.poopsky.fly_type", "Type");
        provider.add("tooltip.poopsky.item.info_0", "§7Hold §6[Shift] §7to show details");
        provider.add("tooltip.poopsky.item.info_1", "§8Right-click two different toilet blocks to link channels");
        provider.add("tooltip.poopsky.poop_ball.info_1", "Villager Support");
        provider.add("tooltip.poopsky.poop_ball.info_2", "500KG Poop");
        provider.add("tooltip.poopsky.toilet_linker.info_1", "Toilet① - DimID: %1$s, Coordinates: %2$s, %3$s, %4$s");
        provider.add("tooltip.poopsky.toilet_linker.info_2", "Toilet② - DimID: %1$s, Coordinates: %2$s, %3$s, %4$s");
        provider.add("tooltip.poopsky.jinkela.info", "§8Fertilizer mixed with Jinkela: one bag spreads like two");
        provider.add("tooltip.poopsky.toilet_type", "Type");
    }

    private static void addJei() {
        provider.add("jei.category.poopsky.compooper", "Compooper");
        provider.add("jei.category.poopsky.sieve", "Sieve");
        provider.add("jei.category.poopsky.digesting", "Digesting");
        provider.add("jei.category.poopsky.digesting.fan", "Place Digesting Fan After Urine Liquid");
        provider.add("jei.category.poopsky.pop_explosion", "POP Explosion");
        provider.add("jei.category.poopsky.anal_pressing", "Anal Pressing");
        provider.add("jei.category.poopsky.fly_barrel", "Fly Barrel");
        provider.add("jei.category.poopsky.breeding_chest", "Breeding Box");
        provider.add("jei.poopsky.sieve_chance", "Chance: %.2f%%");
        provider.add("jei.poopsky.pop_explosion_radius", "Radius > %s");
        provider.add("jei.poopsky.anal_pressing_replace", "Max Press %s");
        provider.add("jei.poopsky.digesting_chance", "Chance: %.2f%%");
        provider.add("jei.poopsky.breeding_chest_chance", "Mutation Chance: %.0f%%");
        provider.add("jei.poopsky.breeding_chest_desc", "The Breeding Box is a workstation for fly mutations. Place two different flies and consume poop to mutate them. Poop blocks within a 5x5x5 area around the Breeding Box accelerate work efficiency, and Maggot Blocks increase output.");
        provider.add("jei.poopsky.folium_senna", "Obtained by fishing with a chance");
        provider.add("jei.poopsky.sea_poop_ball", "Obtained by fishing in Poop Liquid with a chance");
        provider.add("jei.poopsky.sapling_poop_ball", "Sapling Poop Balls are obtained from the Compooper. Eating one grants a sapling.");
        provider.add("jei.poopsky.maggots_seeds", "A Compooper filled with poop can produce Maggots");
        provider.add("jei.poopsky.cactus", "Cactus");
        provider.add("jei.poopsky.sugar_cane", "Sugar Cane");
        provider.add("jei.poopsky.fly_desc.white", "Obtained by killing with Ascarid");
        provider.add("jei.poopsky.fly_desc.black", "Obtained by lightning strike");
        provider.add("jei.poopsky.fly_desc.red", "Obtained by feeding chili");
        provider.add("jei.poopsky.fly_desc.green", "Obtained by killing with cactus");
        provider.add("jei.poopsky.fly_desc.blue", "Obtained by drowning");
        provider.add("jei.poopsky.fly_desc.dragon_fruit", "Obtained by feeding King of Dragon Fruit");
        provider.add("jei.poopsky.urea", "Place a urine-filled Compooper on a campfire and wait for it to turn into a water-filled Compooper");
        provider.add("jei.poopsky.saltpeter_shard", "Obtained by mining saltpeter clusters.\nPlacing a poop block below a toilet has a chance to spawn saltpeter clusters nearby");
        provider.add("jei.poopsky.saltpeter_cluster", "When waterlogged, it turns into ice. When attached to ice and waterlogged, it turns into Powder Snow");
    }

    private static void addCreate() {
        provider.add("create.item_attributes.can_be_digested", "Can be digested");
    }

    private static void addTasks() {
        provider.add("task.poopsky.defecate", "Defecate");
        provider.add("task.poopsky.defecate.desc", "Maid will search for toilets around");
    }

    private static void addConfigs() {
        addConfig("title", "%s Options");
        addConfig("world", "World");
        addConfig("setPoopskyDefault", "Default Dedicated Server World Type");
        addConfig("setPoopskyDefault.tooltip", "Makes dedicated servers use the PoopSky world preset by default");
        addConfig("voidNetherGeneration", "Void Nether Generation");
        addConfig("voidNetherGeneration.tooltip", "Keeps the Nether empty when using the Poopsky custom void generator");
        addConfig("strongholdGeneration", "Generate Strongholds");
        addConfig("strongholdGeneration.tooltip", "Whether strongholds generate in PoopSky worlds");
        addConfig("skyFlushToilet", "SKY Flush Toilet");
        addConfig("skyFlushToilet.tooltip", "Replaces the spawn toilet with a flush toilet");
        addConfig("desperateWorld", "Desperate World");
        addConfig("desperateWorld.tooltip", "Enables Desperate World generation. This can cause lag");
        addConfig("lavaFluid", "Disable Underground Lava Lakes");
        addConfig("lavaFluid.tooltip", "Disables underground lava lakes during PoopSky world generation");

        addConfig("crafting", "Crafting");
        addConfig("compooperCrafting", "Keep Liquid When Crafting Sticks");
        addConfig("compooperCrafting.tooltip", "Prevents liquid in the compooper from being consumed when crafting sticks");

        addConfig("trades", "Trades");
        addConfig("plugTrades", "Disable Toilet Plug Trades");
        addConfig("plugTrades.tooltip", "Whether to disable Toilet Plug trading with villagers");
        addConfig("upgradeTemplate", "Disable Upgrade Template Trades");
        addConfig("upgradeTemplate.tooltip", "Whether to disable upgrade template trading");

        addConfig("timeStop", "Time Stop");
        addConfig("unlimitedFreeze", "Unlimited Freeze");
        addConfig("unlimitedFreeze.tooltip", "Whether to enable unlimited freeze");
        addConfig("freezeFilter", "Freeze Filter");
        addConfig("freezeFilter.tooltip", "Whether to disable freeze filter");

        addConfig("introAnimation", "Play Intro Animation");
        addConfig("introAnimation.tooltip", "Whether to play the intro animation when entering a PoopSky world for the first time");
        addConfig("endAnimation", "Toilet End Poem");
        addConfig("endAnimation.tooltip", "Whether to play the Toilet End Poem before traveling through an End Toilet for the first time");
        addConfig("introText", "Intro Text");
        addConfig("introYear", "Intro Number");
    }

    private static void addConfig(String key, String value) {
        provider.add("poopsky.configuration." + key, value);
    }
}
