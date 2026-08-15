# AGENTS.md — PoopSky Mod 开发指南

## 项目概述

- **模组名称**: PoopSky
- **Mod ID**: `poopsky`
- **包名**: `com.altnoir.poopsky`
- **Minecraft 版本**: 1.21.1
- **模组加载器**: NeoForge `21.1.235`
- **Java 版本**: 21
- **映射**: Parchment `2024.11.17` (Mojang 映射 + 社区参数名)
- **构建工具**: Gradle + NeoGradle (moddev `2.0.141`)
- **许可证**: MIT
- **作者**: Altnoir, lonelyicer, Wulian233

## 模组简介

PoopSky 是一个以**粪便为主题**的 Minecraft 空岛生存模组。玩家在虚空世界中从一棵“粪便树”开始，通过堆肥(Compooper)、筛矿(Sieve)、厕所排便、苍蝇养殖与变异等机制逐步发展科技树。核心玩法围绕粪便资源的收集、加工、转化和自动化展开。

## 项目结构

```
PoopSkyMod/
├── build.gradle                          # 构建脚本、运行配置、依赖声明
├── gradle.properties                     # 版本、Mod 元数据、依赖版本
├── settings.gradle                       # Gradle 项目设置
├── AGENTS.md                             # Agent 开发指南
├── README.md                             # 项目说明
├── LICENSE-CODE                          # 代码许可证
├── LICENSE-ART                           # 美术资源许可证
├── src/generated/resources/             # Datagen 输出，作为 main resources 加载
└── src/main/
    ├── templates/META-INF/neoforge.mods.toml
    ├── java/com/altnoir/poopsky/
    │   ├── PoopSky.java                 # @Mod 入口、PoRegistrate 实例与注册编排
    │   ├── PoopSkyClient.java           # 客户端初始化
    │   ├── Config.java / PoItemGroups.java
    │   ├── init/                        # PoBlocks、PoItems、PoEntityType 等注册类
    │   ├── content/                     # 方块、物品、实体、配方、村民等玩法
    │   ├── client/                      # 菜单屏幕、模型、粒子、渲染、循环音效
    │   ├── compat/                      # Create、JEI、车万女仆兼容
    │   ├── impl/                        # 事件、网络、datagen、标签、工具与 Registrate 扩展
    │   │   ├── network/                 # CustomPacketPayload 与 PoNetworking
    │   │   ├── registrate/              # PoRegistrate 及各类 Datagen provider
    │   │   └── type/                    # 苍蝇/厕所类型与伤害类型数据
    │   ├── mixin/                        # Mixin 注入
    │   └── worldgen/                    # 虚空区块生成、特征、结构与树叶生成器
    └── resources/
        ├── poopsky.mixins.json           # Mixin 配置
        ├── META-INF/accesstransformer.cfg
        ├── assets/poopsky/               # 客户端资源
        │   ├── blockstates/
        │   ├── lang/                     # 本地化 JSON
        │   ├── models/
        │   ├── sounds/
        │   ├── textures/
        │   ├── icon.png
        │   └── sounds.json
        └── data/                         # 手写数据包资源
            ├── minecraft/tags/           # 原版命名空间标签
            └── poopsky/
                ├── jukebox_song/
                ├── recipes/
                ├── structure/
                └── worldgen/
```

## 依赖模组

| 模组                | 版本                 | 用途                         |
|---------------------|----------------------|------------------------------|
| **Create**          | `6.0.8-168`          | 机械动力联动（风扇消解配方） |
| **JEI**             | `19.27.0.340`        | 配方查看                     |
| **Sable Companion** | `1.6.0`              | 辅助库                       |
| **KubeJS**          | `2101.7.2-build.295` | 脚本扩展                     |
| **车万女仆**        | -                    | 女仆AI联动                   |

## 注册模式 (PoRegistrate + DeferredRegister)

项目以 `PoopSky.registrate()` 返回的自定义 `PoRegistrate` 为主。方块、物品、实体、方块实体、菜单、流体、效果、药水、粒子、音效、统计、村民与世界生成等均优先沿用 Registrate。`PoRecipes`、`PoComponents` 和 `PFluidTypes` 等少数注册类仍使用 NeoForge `DeferredRegister`。

`PoRegistrate.create(MOD_ID)` 会自行挂载 mod event bus；每个注册类保留空 `register()` 方法用于触发类加载，由 `PoopSky` 构造函数统一调用。

### 方块注册

```java
// PoBlocks.java
private static final PoRegistrate REGISTRATE = PoopSky.registrate();

public static final BlockEntry<PoopBlock> POOP_BLOCK = registerPoopBlock("poop_block",
        props -> new PoopBlock(poopProperties()
                .randomTicks()
                .speedFactor(0.4F)
                .isValidSpawn(Blocks::always)
                .isRedstoneConductor(Blocks::always)
                .isSuffocating(Blocks::always)
                .instrument(NoteBlockInstrument.COW_BELL)));
```

默认方块注册辅助会同时生成 blockstate/model hook、默认战利品表和方块物品。特殊掉落在 `PoBlocks.registerBlock(..., loot)` 等 helper 的 loot 回调中处理。

### 物品注册

```java
// PoItems.java
private static final PoRegistrate REGISTRATE = PoopSky.registrate();

public static final ItemEntry<PoopItem> POOP = registerItem("poop",
        props -> new PoopItem(props.food(PFoods.POOP).stacksTo(88)));
```

### 实体注册

```java
// PoEntityType.java
public static final EntityEntry<FlyEntity> FLY = REGISTRATE
        .entity("fly", FlyEntity::new, MobCategory.CREATURE)
        .properties(properties -> properties.sized(0.5F, 0.6F))
        .renderer(() -> FlyRenderer::new)
        .register();
```

### 注册到事件总线

```java
// PoopSky.java 构造函数
PoItems.register();
PoBlocks.register();
PoRecipes.register(modEventBus);
PoComponents.register(modEventBus);
PoModEvents.registerMod(modEventBus);
```

## 命名约定

### 类命名

- 新注册类优先使用 `Po` 前缀：`PoBlocks`, `PoItems`, `PoEntityType`, `PoNetworking`, `PoSoundEvents`。旧有 `PFluidTypes`、`PFoods`、`PToolTiers` 等名称保持不变，不要为统一前缀而做无关重命名。
- 抽象类放在 `content/block/abs/`，以 `Abstract` 前缀开头。
- 具体方块、物品、实体实现分别放在 `content/block/p/`、`content/item/p/`、`content/entity/p/`。
- 客户端专用类放在 `client/`；通用玩法逻辑放在 `content/`；不要把客户端依赖引入通用代码。
- Mixin 类以 `Mixin` 后缀结尾。
- 方块实体以 `BlockEntity` 后缀结尾。
- 自定义配方以 `Recipe` 后缀结尾，配套 Builder 以 `RecipeBuilder` 后缀结尾。

### 注册名 (Registry Name)

- 使用 `snake_case`：`poop_block`, `golden_poop`, `compooper`, `fly_barrel`。
- 保持与 Minecraft 原版命名风格一致。
- 注册名、资源路径、配方文件名、本地化键后缀应尽量一致。

### Mixin 方法命名

- 注入方法使用 `poopsky$功能描述` 格式：`poopsky$applyBleedingDamage`。

## Mixin 注入

Mixin 配置文件位于 `src/main/resources/poopsky.mixins.json`，包路径为 `com.altnoir.poopsky.mixin`，兼容性级别为 `JAVA_21`。

当前注入的目标类：

| Mixin 类                         | 目标                           | 用途               |
|----------------------------------|--------------------------------|--------------------|
| `LivingEntityMixin`              | `LivingEntity`                 | 流血伤害、时停无敌 |
| `FishingHookMixin`               | `FishingHook`                  | 钓鱼战利品修改     |
| `VillagerMixin`                  | `Villager`                     | 村民行为           |
| `TradeWithVillagerMixin`         | `ServerGamePacketListenerImpl` | 村民交易           |
| `CarvedPumpkinBlockMixin`        | `CarvedPumpkinBlock`           | 南瓜生成           |
| `BaseCoralPlantTypeBlockMixin`   | `BaseCoralPlantTypeBlock`      | 珊瑚相关行为       |
| `NoiseBasedChunkGeneratorMixin`  | `NoiseBasedChunkGenerator`     | 世界生成           |
| `ClientPacketListenerMixin`      | `ClientPacketListener`         | 客户端网络逻辑     |
| `CreateWorldScreenWorldTabMixin` | `CreateWorldScreen`            | 世界创建界面       |
| `WorldCreationUiStateMixin`      | `WorldCreationUiState`         | 世界创建 UI 状态   |

## 网络通信

使用 NeoForge 的 `PayloadRegistrar` 系统，协议版本为 `"1"`。Payload 类位于 `impl/network/`，在 `PoNetworking.registerNetworking()` 中注册，并由 `PoModEvents.registerMod()` 挂载。

- `PlugActionPayload` — 客户端→服务端，马桶塞操作
- `PlugDismountPayload` — 客户端→服务端，下马桶
- `PlugInputPayload` — 客户端→服务端，马桶输入
- `TimeBellFreezePayload` — 服务端→客户端，时停铃

## 配置项

在 `Config.java` 中通过 `ModConfigSpec` 定义，配置文件类型为 `COMMON`，由 `ModContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)` 注册。

| 配置项                 | 类型    | 默认值 | 说明                                      |
|------------------------|---------|-------:|-------------------------------------------|
| `setPoopskyDefault`    | boolean |   true | 是否将 poopsky 设为专用服务器默认世界类型 |
| `voidNetherGeneration` | boolean |   true | 下界是否也使用虚空生成                    |
| `desperateWorld`       | boolean |  false | 是否启用绝望世界模式                      |
| `compooperCrafting`    | boolean |  false | 是否禁用棍子合成时的液体消耗              |
| `lavaFluid`            | boolean |   true | 是否禁用地下熔岩湖                        |
| `plugTrades`           | boolean |  false | 是否禁用马桶搋子交易                      |
| `upgradeTemplate`      | boolean |  false | 是否禁用升级模板交易                      |
| `unlimitedFreeze`      | boolean |  false | 是否启用无限时停                          |
| `freezeFilter`         | boolean |  false | 是否禁用时停滤镜                          |

## 自定义配方

### 厕所有序配方 (ToiletShapedRecipe)

- 序列化器：`poopsky:toilet_shaped`
- 用于给合成结果写入厕所类型组件。
- Builder：`ToiletRecipeBuilder`。
- 数据生成中通过 `toiletRecipes(...)` 批量生成。

### 筛矿配方 (SieveRecipe)

- 序列化器：`poopsky:sieve`
- 配方类型：`poopsky:sieve`
- 配方文件夹：`recipe/sieve/`
- 用于筛网方块产出矿物或其他资源。
- Builder：`SieveRecipeBuilder`。

### PoopTNT 爆炸配方 (POPExplosionRecipe)

- 序列化器：`poopsky:pop_explosion`
- 配方类型：`poopsky:pop_explosion`
- 配方文件夹：`recipe/pop_explosion/`
- 用于 PoopTNT 爆炸时按半径将指定方块转化为输出方块。
- 支持 `Ingredient` 输入和 `Block` 输出。
- Builder：`POPExplosionRecipeBuilder`。
- JEI 显示类：`POPExplosionRecipeCategory`。

配方 JSON 示例：

```json
{
  "type": "poopsky:pop_explosion",
  "input": {
    "item": "minecraft:cobblestone"
  },
  "output": "minecraft:gravel",
  "radius": 1
}
```

### 肛气冲压转换配方 (AnalPressingRecipe)

- 序列化器：`poopsky:anal_pressing`
- 配方类型：`poopsky:anal_pressing`
- 配方文件夹：`recipe/anal_pressing/`
- 字段：`input`、`output`、`replace_target`、`radius`。
- 运行时通过 `applyConversion(Level, BlockPos)` 在指定半径内替换目标方块。
- Builder：`AnalPressingRecipeBuilder`。

### 苍蝇桶产出配方 (FlyBarrelRecipe)

- 序列化器：`poopsky:fly_barrel`
- 配方类型：`poopsky:fly_barrel`
- 配方文件夹：`recipe/fly_barrel/`
- 定义每种苍蝇品种在苍蝇桶中的产出物品。
- 字段：`fly_type`（品种 ID，如 `"normal"`、`"red"`）→ `result`（ItemStack）。
- 运行时通过 `PFlyRecipes.getProduct(level, type)` 查询，底层使用 `matches(flyTypeId)` 匹配。
- 不使用标准 `RecipeInput` 匹配（`matches(RecipeInput, Level)` 始终返回 `false`）。
- Datagen：`PSRecipeProvider.buildFlyBarrelRecipes()`，通过 `FlyBarrelRecipeBuilder.flyBarrel(typeId, result)` 构建。

配方 JSON 示例：

```json
{
  "type": "poopsky:fly_barrel",
  "fly_type": "red",
  "result": {
    "id": "minecraft:redstone",
    "count": 1
  }
}
```

### 繁育箱变异配方 (BreedingChestRecipe)

- 序列化器：`poopsky:breeding_chest`
- 配方类型：`poopsky:breeding_chest`
- 配方文件夹：`recipe/breeding_chest/`
- 定义两只苍蝇变异后产生的新品种及概率。
- 字段：`parent1` + `parent2`（父本品种 ID）→ `result`（子代品种 ID）+ `chance`（0.0~1.0）。
- 匹配是**双向的**：`parent1+parent2` 和 `parent2+parent1` 都能匹配。
- 不使用标准 `RecipeInput` 匹配，`assemble()` 和 `getResultItem()` 返回 `ItemStack.EMPTY`（结果不是物品而是品种 ID）。
- 运行时通过 `PFlyRecipes.tryMutate(level, parent1, parent2)` 查询，底层使用 `matches(p1, p2)` 匹配。
- Datagen：`PSRecipeProvider.buildBreedingChestRecipes()`，通过 `BreedingChestRecipeBuilder.breedingChest(p1, p2, result)` 构建，必要时链式调用 `.chance(chance)`。

配方 JSON 示例：

```json
{
  "type": "poopsky:breeding_chest",
  "parent1": "red",
  "parent2": "blue",
  "result": "purple",
  "chance": 0.2
}
```

### Create 风扇配方

- 运行时位于 `compat/create/` 与 `compat/create/content/`，datagen 位于 `datagen/create/`。
- 仅在 Create 加载时通过 `CreatePlugin.register(modEventBus)` 注册。
- 当前 datagen 包含 `PDigestingRecipeGen`、`PWashingRecipeGen`、`PHauntingRecipeGen`，位于 `impl/create/`。

## 数据包扩展数据

### 苍蝇类型列表

- 管理器：`FlyTypeManager`
- 重载目录：`data/<namespace>/poopsky_data/fly_types.json`
- 支持数组格式，也支持对象格式：

```json
{
  "replace": false,
  "values": [
    "normal",
    "red"
  ]
}
```

- `replace: true` 会清空低优先级数据包已加载的列表。
- 默认回退到 `FlyType.FLY_TYPES`。

### 厕所类型数据

- 管理器：`ToiletTypeManager`
- 重载目录：`data/<namespace>/poopsky_data/toilet_type/*.json`
- 文件路径作为类型 ID，例如 `poopsky_data/toilet_type/stone.json` 的 ID 为 `stone`。
- 解析失败会记录错误并跳过该类型。

## 本地化与翻译

- 简体中文文件 `src/main/resources/assets/poopsky/lang/zh_cn.json` 是**唯一基准**。
- 新增、删除、移动本地化键时，先修改 `zh_cn.json`，再同步其他语言文件。
- 所有语言文件的键顺序必须与 `zh_cn.json` 完全一致，空行保留也需完全一致。
- 其他语言缺失键时必须补齐翻译；暂时无法准确翻译时，允许先使用简体中文原文占位，但要优先给出对应语言翻译。
- 不要在其他语言文件中保留 `zh_cn.json` 不存在的额外键，除非明确是 Minecraft 语言加载需要的特殊兼容键。
- 本地化键命名按用途分组，并保持组内稳定顺序：
  - `itemGroup.poopsky.*`
  - `block.poopsky.*`
  - `item.poopsky.*`
  - `entity.poopsky.*`
  - `effect.poopsky.*`
  - `death.attack.*`
  - `subtitles.poopsky.*` / `subtitle.poopsky.*`
  - `container.poopsky.*`
  - `stat.poopsky.*`
  - `poopsky.configuration.*`
  - `jei.poopsky.*`
- 方块、物品、实体、效果、菜单、配置、音效字幕、JEI 文本都必须添加本地化。
- 文案以简体中文语义为准；其他语言翻译不得擅自改变玩法含义、概率、范围或条件。
- 翻译繁体中文需要参考<https://minecraft.fandom.com/zh/wiki/Minecraft_Wiki:译名标准化/繁体译名>保证术语一致，不能简单的按简体中文翻译
- JSON 必须是无 BOM 的 UTF-8，保留 2 空格缩进。

建议用脚本校验语言文件：

```bash
python - <<'PY'
import json, pathlib
base = pathlib.Path('src/main/resources/assets/poopsky/lang')
zh = json.loads((base / 'zh_cn.json').read_text(encoding='utf-8-sig'), object_pairs_hook=dict)
zh_keys = list(zh)
for path in sorted(base.glob('*.json')):
    data = json.loads(path.read_text(encoding='utf-8-sig'), object_pairs_hook=dict)
    missing = [key for key in zh_keys if key not in data]
    extra = [key for key in data if key not in zh]
    ordered = [key for key in zh_keys if key in data] == [key for key in data if key in zh]
    print(path.name, 'missing=', len(missing), 'extra=', len(extra), 'order=', ordered)
PY
```

## 数据生成 (Datagen)

数据生成器入口：`impl/DataGenerators.java`，由 `PoModEvents` 订阅 `GatherDataEvent`。输出目录为 `src/generated/resources/`，手写资源目录为 `src/main/resources/`。

### 运行数据生成

```bash
./gradlew runData
```

## 可选模组兼容

在 `PoMods` 枚举中定义可选模组，通过 `ModList.get().isLoaded()` 检查：

```java
if (ModList.get().isLoaded(PoMods.TOUHOU_LITTLE_MAID.id())) {
    MaidPlugin.registry(modEventBus);
}
if (ModList.get().isLoaded(PoMods.CREATE.id())) {
    CreatePlugin.register(modEventBus);
}
```

兼容代码必须隔离在 `compat/<modid>/` 中，只有可选模组加载时才注册对应事件、配方类型或行为。

## 构建与运行

```bash
# 运行客户端
./gradlew runClient

# 运行服务端
./gradlew runServer

# 构建模组
./gradlew build
```

## NeoForge 1.21.1 开发规则

### 1. 使用 Registrate / DeferredRegister

- 不得直接使用原版 `Registry.register()`。
- 方块与普通物品优先沿用 `PBlocks` / `PItems` 的 `PRegistries.REGISTRATE` helper，字段类型通常为 `BlockEntry<T>` / `ItemEntry<T>`。
- 需要自定义方块物品、战利品表或无物品方块时，使用 `PBlocks.registerBlock`、`registerDefaultBlock`、`registerBlockNoItem`、`registerCompooperBlock`、`registerToiletBlock` 等现有 helper。
- 实体、方块实体、菜单、配方、粒子、音效、数据组件、流体、村民、世界生成注册仍使用对应注册类中的 `DeferredRegister`。
- 新增 `DeferredRegister` 时应放入现有注册类并由 `PRegistries.registerAll(modEventBus)` 或对应兼容插件统一挂到 mod event bus。
- Registrate API 中所有需要 `Supplier` 的地方必须使用 `com.tterrag.registrate.util.nullness.NonNullSupplier`，而非 `java.util.function.Supplier`。例如 `REGISTRATE.simple(name, registry, supplier)` 的第三个参数类型为 `NonNullSupplier<T>`。

### 2. 方块属性

- 使用 `BlockBehaviour.Properties.of()` 构建。
- 使用 `Properties.ofFullCopy()` 复用已有方块属性。
- 记得设置 `mapColor`, `strength`, `sound` 等属性。
- 不完整碰撞箱、透明方块、特殊刷怪/红石行为必须在 `Properties` 中显式声明。

### 3. 方块实体

- 方块实体类放在 `content/block/entity/`。
- 在 `PBlockEntityType` 中注册。
- 使用已有项目模式关联方块与 `BlockEntityType`。
- 菜单类放在 `client/inventory/`，菜单类型在 `PMenuTypes` 中注册。

### 4. 事件订阅

- 静态事件处理使用 `@EventBusSubscriber(modid = PoopSky.MOD_ID)`。
- 事件处理方法必须为 `public static` 或与现有订阅模式一致。
- 需要实例方法或构造期注册时，在 `PoopSky` 中对正确事件总线调用 `addListener`。
- Mod bus 与 `NeoForge.EVENT_BUS` 不要混用：注册、生命周期、datagen 用 mod bus；游戏事件用 NeoForge bus。

### 5. Mixin

- 必须使用 `@Mixin` 注解。
- 注入方法必须 `private`，方法名以 `poopsky$` 为前缀。
- 在 `poopsky.mixins.json` 中声明。
- 客户端 Mixin 放在 `client` 数组。
- 兼容性级别：`JAVA_21`。

### 6. 网络包

- 使用 NeoForge 的 `CustomPacketPayload` 接口。
- 实现 `TYPE`、`STREAM_CODEC`，需要时实现普通 `CODEC`。
- 在 `PSNetworking.register()` 中注册。
- 客户端发来的包必须在服务端重新校验玩家状态、距离、维度和目标实体/方块是否有效。

### 7. 资源路径

- 使用 `PoopSky.loc("path")` 或 `ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "path")`。
- 不要使用旧式 `new ResourceLocation("poopsky:xxx")`。
- 数据包路径以 1.21.1 生成结果为准：原版配方输出目录是 `data/<namespace>/recipe/`，不是旧版 `recipes/`。

### 8. 配置

- 使用 `ModConfigSpec` 构建配置。
- 在 `ModContainer.registerConfig()` 中注册。
- 通过 `@EventBusSubscriber` 监听 `ModConfigEvent` 并同步静态缓存字段。
- 配置项必须添加 `translation("poopsky.configuration.xxx")`，并同步本地化。

### 9. 数据组件

- 1.21.1 使用 `DataComponentType` 替代旧的 NBT。
- 在 `PComponents` 中注册。
- 物品状态优先使用数据组件；只有兼容旧数据或复杂持久化时再考虑其他方案。

### 10. 代码风格

- 遵循项目现有缩进和格式。
- 链式调用每行一个方法。
- 长参数列表按现有代码风格换行。
- 不在代码中添加不必要的注释；复杂逻辑可添加简短说明。
- 新代码优先放入现有模块与命名体系，不为单点需求引入过度抽象。

### 11. 客户端代码分离

- 客户端初始化集中在 `PoopSkyClient.java`。
- 客户端模型、渲染、粒子、音效放在 `client/`。
- 客户端 Mixin 在 `poopsky.mixins.json` 的 `client` 数组中声明。
- `content/`、`init/`、`network/` 等通用代码不得直接引用 `Minecraft`、客户端渲染类或客户端声音类。
- 必要时使用 `DistExecutor` 或 NeoForge 客户端事件隔离客户端逻辑。

### 12. 标签

- 标签常量集中在 `PTags`。
- 标签通过 datagen 的 `PSBlockTagProvider` / `PSItemTagProvider` / `PSFluidTagsProvider` / `PSEntityTypeTagsProvider` / `PSDamageTypeTagsProvider` 生成。
- 原版、NeoForge、Common (`c`) 命名空间标签要放到对应命名空间。

### 13. BlockBehaviour.Properties 中的方块行为（非 Block 重写）

1.21.1 中许多方块行为**不是** `Block` 类的可重写方法，而是在 `BlockBehaviour.Properties` 中设置的。不要尝试在 `Block` 子类中 `@Override` 这些方法，它们不存在：

| 属性     | Properties 方法                        | 说明                                              |
|----------|----------------------------------------|---------------------------------------------------|
| 红石导通 | `.isRedstoneConductor(Blocks::always)` | 默认基于碰撞箱判断，不完整方块需手动设为 `always` |
| 视线阻挡 | `.isViewBlocking(Blocks::never)`       | 控制方块是否阻挡视线                              |
| 有效刷怪 | `.isValidSpawn(Blocks::always)`        | 控制方块上是否可刷怪                              |
| 可被替换 | `.isReplacementReplaceable()`          | 控制方块是否可被替换                              |
| 信号源   | `.isSignalSource()`                    | 控制方块是否为红石信号源                          |

**常见陷阱**：不完整碰撞箱的方块（如 `PoopBlock`）默认无法被红石充能，因为默认 `isRedstoneConductor` 依赖 `isCollisionShapeFullBlock()`。必须显式设置 `.isRedstoneConductor(Blocks::always)`。

### 14. 客户端循环音效

实现实体或方块的持续循环音效时，不要依赖 `getAmbientSound()`（它是间歇性的），应使用 `AbstractTickableSoundInstance` + `looping = true` 模式：

- 创建 `XxxSoundInstance extends AbstractTickableSoundInstance`，设置 `this.looping = true`。
- 创建 `XxxSoundWrapper` 管理音效生命周期（创建、tick、停止）。
- 在实体客户端路径或客户端 tick 中初始化 Wrapper 并持续调用 `tick()`。

**关键**：音效实例的初始 `volume` 必须 > 0（如 `0.1F`）。如果初始为 0，Minecraft 的 `SoundEngine` 不会创建 OpenAL 音频通道，后续 `tick()` 中调高音量也不会有声音。

参考实现：`TPFlySoundInstance` / `TPFlySoundWrapper` / `FlyBuzzSoundInstance` / `FlyBuzzSoundWrapper`

### 15. Minecraft 坐标系与方向向量

Minecraft 使用右手坐标系（x 向南，z 向西，yaw 顺时针增大）：

```java
forward = (-sin(yaw), 0, cos(yaw))   // 前方向
right   = (-forward.z, 0, forward.x) // 右方向 = (cos(yaw), 0, sin(yaw))
left    = -right                     // 左方向
back    = -forward                   // 后方向
```

**常见错误**：用 `(forward.z, 0, -forward.x)` 从 forward 推导 right，这实际是**左方向**（逆时针旋转 90°）。正确的右方向是 forward **顺时针旋转 90°**，即 `(-forward.z, 0, forward.x)`。

### 16. JEI 配方显示

JEI 配方要正确显示配方 ID，`IRecipeCategory` 的泛型参数必须是 `RecipeHolder<...>`：

```java
// 正确：可以显示配方 ID
public class XxxRecipeCategory implements IRecipeCategory<RecipeHolder<XxxRecipe>> {
    static final RecipeType<RecipeHolder<XxxRecipe>> TYPE =
            RecipeType.createRecipeHolderType(MOD_ID, "xxx", RecipeHolder.class);
}

// 错误：丢失配方 ID
public class XxxRecipeCategory implements IRecipeCategory<XxxJeiRecipe> {
    static final RecipeType<XxxJeiRecipe> TYPE =
            RecipeType.create(MOD_ID, "xxx", XxxJeiRecipe.class);
}
```

注册时直接传 `recipeManager.getAllRecipesFor(recipeType)` 的结果，不要手动 map 转换为纯 record（会丢失 ID）。

### 17. 实体乘骑与输入控制

实现可控载具实体时：

- 输入处理在 `tick()` 或自定义方法中，通过 `getControllingPassenger()` 获取驾驶员。
- 左右移动的输入值：按左 = 负值，按右 = 正值。
- 朝向插值系数影响操控手感：`0.5f` 有明显漂移感，`0.9f` 几乎即时跟随，`1.0f` 完全同步。
- 速度阻尼（DAMPING）过高也会导致转向时漂移感。