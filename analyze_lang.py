import json, re

java = open("F:/Minecraft/ModDev/PoopSkyMod/src/main/java/com/altnoir/poopsky/impl/registrate/LangGen.java", "r", encoding="utf-8").read()
raw_hand = open("F:/Minecraft/ModDev/PoopSkyMod/src/main/resources/assets/poopsky/lang/en_us.json", "r", encoding="utf-8").read()
gen = json.load(open("F:/Minecraft/ModDev/PoopSkyMod/src/generated/resources/assets/poopsky/lang/en_us.json", "r", encoding="utf-8"))
gen_keys = set(gen.keys())
hand = json.loads(raw_hand)
hand_ordered = list(json.JSONDecoder(object_pairs_hook=lambda x: x).decode(raw_hand))
hand_keys_ordered = [item[0] for item in hand_ordered]

# Get all keys from LangGen
adds = set()
for m in re.finditer(r'add\("([^"\\]*(?:\\.[^"\\]*)*)"\s*,\s*"([^"\\]*(?:\\.[^"\\]*)*)"\)', java):
    adds.add(m.group(1))
for m in re.finditer(r'addAdvancement\("([^"\\]*(?:\\.[^"\\]*)*)",\s*"([^"\\]*(?:\\.[^"\\]*)*)",\s*"([^"\\]*(?:\\.[^"\\]*)*)"\)', java):
    k, t, d = m.group(1), m.group(2), m.group(3)
    t = t.replace('\\"', '"').replace("\\\\", "\\")
    d = d.replace('\\"', '"').replace("\\\\", "\\")
    if t: adds.add("advancements.poopsky." + k + ".title")
    if d: adds.add("advancements.poopsky." + k + ".description")
for m in re.finditer(r'addToilet\("([^"\\]*(?:\\.[^"\\]*)*)",\s*"([^"\\]*(?:\\.[^"\\]*)*)"\)', java):
    adds.add("block.poopsky.toilet." + m.group(1))
for m in re.finditer(r'addSubtitle\("([^"\\]*(?:\\.[^"\\]*)*)",\s*"([^"\\]*(?:\\.[^"\\]*)*)"\)', java):
    adds.add("subtitle.poopsky." + m.group(1))
for m in re.finditer(r'addSubtitleEx\("([^"\\]*(?:\\.[^"\\]*)*)",\s*"([^"\\]*(?:\\.[^"\\]*)*)"\)', java):
    adds.add("subtitles.poopsky." + m.group(1))
for m in re.finditer(r'addEffect\("([^"\\]*(?:\\.[^"\\]*)*)",\s*"([^"\\]*(?:\\.[^"\\]*)*)"\)', java):
    adds.add("item.minecraft.potion.effect." + m.group(1))
for m in re.finditer(r'addTippedArrow\("([^"\\]*(?:\\.[^"\\]*)*)",\s*"([^"\\]*(?:\\.[^"\\]*)*)"\)', java):
    adds.add("item.minecraft.tipped_arrow." + m.group(1))

# Get missing entries in order
missing_in_order = []
for k in hand_keys_ordered:
    if k not in gen_keys and k not in adds:
        missing_in_order.append(k)

# ========== GENERATE MODIFIED JAVA ==========
lines = java.splitlines()
out_lines = []
i = 0
while i < len(lines):
    # Check for chili_poop update
    if 'addAdvancement("chili_poop"' in lines[i]:
        out_lines.append('        addAdvancement("chili_poop", "Chili Poop", "While affected by Intestinal Spasm, you can poop out Chili Poop\\nThe crafted Chili Poop Block can be grown with bone meal");')
        i += 1
        continue
    
    # Check for last addAdvancement before method ends
    if 'addAdvancement("wither_poop_ball"' in lines[i]:
        out_lines.append(lines[i])
        i += 1
        # Add new advancements
        out_lines.append('        addAdvancement("compooper", "Compooper", "Break a Poop Log to craft a Compooper\\nCompoopers can also collect rainwater and snow");')
        out_lines.append('        addAdvancement("fly", "!? Fly?!", "Place a Carved Pumpkin on a Maggot Block");')
        out_lines.append('        addAdvancement("coal_block", "Carbonized Poop", "Encase a Poop Log in solid blocks and wait for it to turn into a Block of Coal");')
        out_lines.append('        addAdvancement("cocoa", "\\"Chocolate\\"", "Roast Poop on a Campfire to obtain Cocoa Beans");')
        continue

    # Check for last addToilet before method ends
    if 'addToilet("netherite"' in lines[i]:
        out_lines.append(lines[i])
        i += 1
        # Add new toilet variants
        out_lines.append('        addToilet("weathered_copper", "Weathered Copper");')
        out_lines.append('        addToilet("oxidized_copper", "Oxidized Copper");')
        out_lines.append('        addToilet("weathered_chiseled_copper", "Weathered Chiseled Copper");')
        out_lines.append('        addToilet("oxidized_chiseled_copper", "Oxidized Chiseled Copper");')
        out_lines.append('        addToilet("weathered_cut_copper", "Weathered Cut Copper");')
        out_lines.append('        addToilet("oxidized_cut_copper", "Oxidized Cut Copper");')
        continue

    # Check for last addTippedArrow in addPotions
    if 'addTippedArrow("effect.strong_on_the_verge_potion"' in lines[i]:
        out_lines.append(lines[i])
        i += 1
        # Add splash/lingering potions and missing tipped_arrow
        out_lines.append('')
        out_lines.append('        addSplash("fecal_incontinence_potion", "Fecal Incontinence");')
        out_lines.append('        addLingering("fecal_incontinence_potion", "Fecal Incontinence");')
        out_lines.append('        addSplash("long_fecal_incontinence_potion", "Fecal Incontinence");')
        out_lines.append('        addLingering("long_fecal_incontinence_potion", "Fecal Incontinence");')
        out_lines.append('        addSplash("strong_fecal_incontinence_potion", "Fecal Incontinence");')
        out_lines.append('        addLingering("strong_fecal_incontinence_potion", "Fecal Incontinence");')
        out_lines.append('        addSplash("super_fecal_incontinence_potion", "Fecal Incontinence");')
        out_lines.append('        addLingering("super_fecal_incontinence_potion", "Fecal Incontinence");')
        out_lines.append('        addSplash("on_the_verge_potion", "On the Verge");')
        out_lines.append('        addLingering("on_the_verge_potion", "On the Verge");')
        out_lines.append('        addSplash("long_on_the_verge_potion", "On the Verge");')
        out_lines.append('        addLingering("long_on_the_verge_potion", "On the Verge");')
        out_lines.append('        addSplash("strong_on_the_verge_potion", "On the Verge");')
        out_lines.append('        addLingering("strong_on_the_verge_potion", "On the Verge");')
        out_lines.append('')
        out_lines.append('        addTippedArrow("effect.long_fecal_incontinence_potion", "Fecal Incontinence");')
        out_lines.append('        addTippedArrow("effect.long_on_the_verge_potion", "On the Verge");')
        continue

    # Check for last add in addItemExtras
    if 'additions_slot_description' in lines[i]:
        out_lines.append(lines[i])
        i += 1
        # Add new items
        out_lines.append('        add("item.poopsky.mossy_cobblestone_pickaxe", "Mossy Cobblestone Pickaxe");')
        out_lines.append('        add("item.poopsky.mossy_cobblestone_axe", "Mossy Cobblestone Axe");')
        out_lines.append('        add("item.poopsky.mossy_cobblestone_shovel", "Mossy Cobblestone Shovel");')
        out_lines.append('        add("item.poopsky.mossy_cobblestone_hoe", "Mossy Cobblestone Hoe");')
        out_lines.append('        add("item.poopsky.mossy_cobblestone_sword", "Mossy Cobblestone Sword");')
        out_lines.append('        add("item.poopsky.music_disc_lawrence.desc", "Ryuichi Sakamoto - Merry Christmas Mr. Lawrence");')
        out_lines.append('        add("item.poopsky.music_disc_light_dance.desc", "Sakanaction - Light Dance");')
        out_lines.append('        add("item.poopsky.music_disc_moon_bowl.desc", "Sakanaction - Moon Bowl");')
        continue

    # Check for addFlyTypes call in addTranslations (after addToiletTypes)
    if lines[i].strip() == 'addToiletTypes();':
        out_lines.append(lines[i])
        i += 1
        # Add addFlyTypes call right after addToiletTypes
        # Look for the next method call
        continue

    # Check for addMisc - add effects, blocks, containers, entities
    # We'll add them at the end of addMisc method
    if lines[i].strip() == 'private void addMisc() {' and i > 0:
        out_lines.append(lines[i])
        i += 1
        # We'll handle the content of addMisc below
        # Collect all lines inside addMisc
        add_misc_lines = []
        brace_count = 1
        while i < len(lines) and brace_count > 0:
            add_misc_lines.append(lines[i])
            for c in lines[i]:
                if c == '{': brace_count += 1
                if c == '}': brace_count -= 1
            i += 1
        # Now add_misc_lines contains the original method body + closing brace
        # Remove the closing brace from the list
        if add_misc_lines and add_misc_lines[-1].strip() == '}':
            closing_brace = add_misc_lines.pop()
        
        # Add all original add_misc lines
        for line in add_misc_lines:
            out_lines.append(line)
        
        # Now add new entries in hand-written JSON order
        out_lines.append('')
        out_lines.append('        // Effects')
        out_lines.append('        add("effect.poopsky.fecal_incontinence", "Fecal Incontinence");')
        out_lines.append('        add("effect.poopsky.intestinal_spasm", "Intestinal Spasm");')
        out_lines.append('        add("effect.poopsky.on_the_verge", "On the Verge");')
        out_lines.append('        add("effect.poopsky.omener", "Omener");')
        out_lines.append('        add("effect.poopsky.seedbed_curse", "Seedbed Curse");')
        out_lines.append('        add("effect.poopsky.bleeding", "Bleeding");')
        out_lines.append('        add("effect.poopsky.moment_of_ptyme", "Moment of Ptyme");')
        out_lines.append('')
        out_lines.append('        // Blocks')
        out_lines.append('        add("block.poopsky.urine", "Urine");')
        out_lines.append('        add("block.poopsky.toilet_block", "Toilet");')
        out_lines.append('        add("block.poopsky.toilet_format", "%s Toilet");')
        out_lines.append('        add("block.poopsky.rainbow_toilet", "Rainbow Toilet");')
        out_lines.append('')
        out_lines.append('        // Containers')
        out_lines.append('        add("container.poopsky.fly_barrel", "Fly Barrel");')
        out_lines.append('        add("container.poopsky.breeding_chest", "Breeding Box");')
        out_lines.append('')
        out_lines.append('        // Entities')
        out_lines.append('        add("entity.minecraft.villager.poopsky.poopmaker", "Poopmaker");')
        out_lines.append('        add("entity.minecraft.villager.poopsky.gastronome", "Gastronome");')
        out_lines.append('        add("entity.minecraft.villager.poopmaker", "Poopmaker");')
        out_lines.append('        add("entity.minecraft.villager.gastronome", "Gastronome");')
        out_lines.append('        add("entity.poopsky.toilet_plug", "Toilet Plug");')
        out_lines.append('        add("entity.poopsky.stool_entity", "Poop Stool");')
        out_lines.append('        add("entity.poopsky.poolime", "Poolime");')
        out_lines.append('        add("entity.poopsky.fly", "Fly");')
        out_lines.append('        add("entity.poopsky.poop_tnt", "POP");')
        
        # Add closing brace
        out_lines.append(closing_brace)
        continue

    # Check for addFlyTypes method - we'll add it before addSubtitle
    if lines[i].strip() == 'private void addSubtitles() {' and i > 0:
        # Add addFlyTypes method
        out_lines.append('')
        out_lines.append('    private void addFlyTypes() {')
        for fly_key in ["normal", "white", "black", "green", "yellow", "blue", "red", "brown", "gray", "light_gray", "light_blue", "lime", "magenta", "cyan", "pink", "orange", "purple", "iron", "copper", "gold", "emerald", "diamond", "netherite", "dragon_fruit", "glowstone", "ender"]:
            name = fly_key.replace("_", " ").title()
            if fly_key == "dragon_fruit":
                name = "Dragon Fruit Fly"
            elif fly_key == "ender":
                name = "End Fly"
            elif fly_key == "glowstone":
                name = "Glowstone Fly"
            elif fly_key == "netherite":
                name = "Netherite Fly"
            elif fly_key == "light_gray":
                name = "Light Gray Fly"
            elif fly_key == "light_blue":
                name = "Light Blue Fly"
            else:
                name = name + " Fly"
            out_lines.append(f'        add("fly_type.poopsky.{fly_key}", "{name}");')
        out_lines.append('    }')
        out_lines.append('')
        out_lines.append(lines[i])
        i += 1
        continue

    # Check for addSplash/addLingering helper methods - add after addTippedArrow
    if lines[i].strip() == 'private void addTippedArrow(String key, String value) {':
        out_lines.append(lines[i])
        i += 1
        # Read the addTippedArrow method and add it
        add_tipped_lines = []
        brace_count = 1
        while i < len(lines) and brace_count > 0:
            add_tipped_lines.append(lines[i])
            for c in lines[i]:
                if c == '{': brace_count += 1
                if c == '}': brace_count -= 1
            i += 1
        for line in add_tipped_lines:
            out_lines.append(line)
        
        # Add helper methods
        out_lines.append('')
        out_lines.append('    private void addSplash(String key, String value) {')
        out_lines.append('        add("item.minecraft.splash_potion.effect." + key, value);')
        out_lines.append('    }')
        out_lines.append('')
        out_lines.append('    private void addLingering(String key, String value) {')
        out_lines.append('        add("item.minecraft.lingering_potion.effect." + key, value);')
        out_lines.append('    }')
        continue

    # Default: copy line as-is
    out_lines.append(lines[i])
    i += 1

# Now add the addFlyTypes() call in addTranslations
# Find the addTranslations method and add the call
result = '\n'.join(out_lines)

# Insert addFlyTypes() call after addToiletTypes() line
result = result.replace('        addToiletTypes();\n        addPotions();', '        addToiletTypes();\n        addFlyTypes();\n        addPotions();')

# Write the result
with open("F:/Minecraft/ModDev/PoopSkyMod/src/main/java/com/altnoir/poopsky/impl/registrate/LangGen.java", "w", encoding="utf-8") as f:
    f.write(result)

print("DONE! File written successfully.")
