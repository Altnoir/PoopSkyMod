# AGENTS.md — PoopSky Mod 开发指南

## 项目概述

- **模组名称**: PoopSky
- **Mod ID**: `poopsky`
- **包名**: `com.altnoir.poopsky`
- **Minecraft 版本**: 1.21.1
- **模组加载器**: NeoForge `21.1.219`
- **模组版本**: `1.21.1-1.4.5`
- **Java 版本**: 21
- **映射**: Parchment `2024.11.17` (Mojang 映射 + 社区参数名)
- **构建工具**: Gradle + NeoGradle (moddev `2.0.78`)
- **许可证**: MIT
- **作者**: Altnoir, lonelyicer, Wulian233

## 模组简介

PoopSky 是一个以**粪便为主题**的 Minecraft 空岛生存模组。玩家在虚空世界中从一棵"粪便树"开始，通过堆肥(Compooper)、筛矿(Sieve)、马桶排便等机制逐步发展科技树。核心玩法围绕粪便资源的收集、加工和转化展开。

## 项目结构

```
PoopSkyMod/
├── build.gradle                          # 构建脚本
├── gradle.properties                     # 版本与依赖配置
├── src/main/
│   ├── java/com/altnoir/poopsky/
│   │   ├── PoopSky.java                  # 主模组类（@Mod 入口）
│   │   ├── PoopSkyClient.java            # 客户端初始化
│   │   ├── Config.java                   # 配置项定义
│   │   ├── PItemGroups.java              # 创造模式物品栏
│   │   ├── PTags.java                    # 标签定义
│   │   ├── block/
│   │   │   ├── PBlocks.java              # 方块注册中心
│   │   │   ├── AllToiletBlocks.java      # 马桶方块注册
│   │   │   ├── ToiletComponent.java      # 马桶组件
│   │   │   ├── abs/                      # 抽象基类
│   │   │   │   ├── AbstractCompooperBlock.java
│   │   │   │   ├── AbstractRawBlock.java
│   │   │   │   └── AbstractToiletBlock.java
│   │   │   ├── entity/                   # 方块实体 (BlockEntity)
│   │   │   │   ├── SieveBlockEntity.java
│   │   │   │   ├── ToiletBlockEntity.java
│   │   │   │   └── PlacerBlockEntity.java
│   │   │   ├── fluid/
│   │   │   │   └── UrineLiquidBlock.java
│   │   │   └── p/                        # 具体方块实现 (30+ 种)
│   │   ├── item/
│   │   │   ├── PItems.java               # 物品注册中心
│   │   │   ├── PFoods.java               # 食物属性
│   │   │   ├── PArmorMaterials.java      # 盔甲材料
│   │   │   ├── PToolTiers.java           # 工具等级
│   │   │   └── p/                        # 具体物品实现
│   │   ├── entity/
│   │   │   ├── model/                    # 实体模型
│   │   │   ├── renderer/                 # 实体渲染器
│   │   │   └── p/                        # 具体实体实现
│   │   ├── init/                         # 注册中心 (PEntityType, PEffects...)
│   │   ├── effect/                       # 药水效果
│   │   ├── event/                        # 事件处理
│   │   ├── recipe/                       # 自定义配方
│   │   ├── worldgen/                     # 世界生成
│   │   │   ├── PSChunkGenerators.java
│   │   │   ├── PSVoidChunkGenerator.java
│   │   │   ├── structure/                # 岛屿结构
│   │   │   └── foliage/                  # 树叶生成器
│   │   ├── compat/                       # 模组兼容
│   │   │   ├── PSMods.java               # 可选模组枚举
│   │   │   ├── create/                   # Create 机械动力
│   │   │   ├── jei/                      # JEI 配方查看
│   │   │   └── maid/                     # 车万女仆
│   │   ├── datagen/                      # 数据生成
│   │   ├── mixin/                        # Mixin 注入
│   │   ├── network/                      # 网络包
│   │   ├── util/                         # 工具类
│   │   └── villager/                     # 村民交易
│   └── resources/
│       ├── poopsky.mixins.json           # Mixin 配置
│       ├── META-INF/
│       │   └── accesstransformer.cfg
│       └── assets/poopsky/               # 模组资源
```

## 依赖模组

| 模组 | 版本 | 用途 |
|------|------|------|
| **Create** | `6.0.8-168` | 机械动力联动（风扇消解配方） |
| **JEI** | `19.27.0.340` | 配方查看 |
| **Sable Companion** | `1.6.0` | 辅助库 |
| **KubeJS** | `2101.7.2-build.295` | 脚本扩展 |
| **车万女仆** | - | 女仆AI联动 |

## 注册模式 (DeferredRegister)

本项目使用 NeoForge 的标准 `DeferredRegister` 注册系统。所有内容注册遵循以下模式：

### 方块注册

```java
// PBlocks.java
public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PoopSky.MOD_ID);

public static final DeferredBlock<Block> POOP_BLOCK = registerBlock("poop_block",
    () -> new PoopBlock(BlockBehaviour.Properties.of()
        .randomTicks()
        .strength(0.5F)
        .mapColor(MapColor.COLOR_BROWN)
        .speedFactor(0.4F)
        .isValidSpawn(Blocks::always)
        .instrument(NoteBlockInstrument.COW_BELL)
        .sound(SoundType.MUD))
);
```

### 物品注册

```java
// PItems.java
public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PoopSky.MOD_ID);

public static final DeferredItem<Item> POOP = ITEMS.register("poop", () ->
    new PoopItem(new Item.Properties().food(PFoods.POOP).stacksTo(88)));
```

### 实体注册

```java
// PEntityType.java
public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
    DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, PoopSky.MOD_ID);
```

### 注册到事件总线

```java
// PoopSky.java 构造函数
PBlocks.register(modEventBus);
PItems.register(modEventBus);
PEntityType.register(modEventBus);
// ... 其他注册
```

## 命名约定

### 类命名
- 注册类以 `P` 前缀开头：`PBlocks`, `PItems`, `PEntityType`, `PEffects` 等
- 抽象类放在 `abs/` 子包中，以 `Abstract` 前缀开头
- 具体实现在 `p/` 子包中
- Mixin 类以 `Mixin` 后缀结尾
- 方块实体以 `BlockEntity` 后缀结尾

### 注册名 (Registry Name)
- 使用 `snake_case`：`poop_block`, `golden_poop`, `compooper`
- 保持与 Minecraft 原版命名风格一致

### Mixin 方法命名
- 使用 `模组名$功能描述` 格式：`poopsky$applyBleedingDamage`

## Mixin 注入

Mixin 配置文件位于 `src/main/resources/poopsky.mixins.json`，包路径为 `com.altnoir.poopsky.mixin`。

当前注入的目标类：

| Mixin 类 | 目标 | 用途 |
|----------|------|------|
| `LivingEntityMixin` | `LivingEntity` | 流血伤害、时停无敌 |
| `FishingHookMixin` | `FishingHook` | 钓鱼战利品修改 |
| `VillagerMixin` | `Villager` | 村民行为 |
| `TradeWithVillagerMixin` | `ServerGamePacketListenerImpl` | 村民交易 |
| `CarvedPumpkinBlockMixin` | `CarvedPumpkinBlock` | 南瓜生成 |
| `BaseCoralPlantTypeBlockMixin` | `BaseCoralPlantTypeBlock` | 珊瑚 |
| `NoiseBasedChunkGeneratorMixin` | `NoiseBasedChunkGenerator` | 世界生成 |
| `ClientPacketListenerMixin` | `ClientPacketListener` | 客户端 |
| `CreateWorldScreenWorldTabMixin` | `CreateWorldScreen` | 世界创建界面 |
| `WorldCreationUiStateMixin` | `WorldCreationUiState` | 世界创建UI |

## 网络通信

使用 NeoForge 的 `PayloadRegistrar` 系统，协议版本为 `"1"`。

- `PlugActionPayload` — 客户端→服务端，马桶塞操作
- `PlugDismountPayload` — 客户端→服务端，下马桶
- `PlugInputPayload` — 客户端→服务端，马桶输入
- `TimeBellFreezePayload` — 服务端→客户端，时停铃

## 配置项

在 `Config.java` 中通过 `ModConfigSpec` 定义，配置文件类型为 `COMMON`：

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `setPoopskyDefault` | boolean | true | 是否将 poopsky 设为默认世界类型 |
| `voidNetherGeneration` | boolean | true | 地狱是否也使用虚空生成 |
| `desperateWorld` | boolean | false | 绝望世界模式 |
| `compooperCrafting` | boolean | false | 禁用堆肥配方消耗液体 |
| `lavaFluid` | boolean | true | 禁用地下熔岩湖 |
| `plugTrades` | boolean | false | 禁用塞子交易 |
| `upgradeTemplate` | boolean | false | 禁用升级模板交易 |
| `unlimitedFreeze` | boolean | false | 无限时停 |
| `freezeFilter` | boolean | false | 禁用时停滤镜 (客户端) |

## 自定义配方

### 筛矿配方 (SieveRecipe)
- 序列化器：`poopsky:sieve`
- 配方文件夹：`sieve/`
- 用于筛网方块产出矿物

### 堆肥配方 (CompooperRecipe)
- 用于 JEI 显示堆肥桶配方

### Create 风扇消解配方 (DigestingRecipe)
- 仅在 Create 模组加载时注册
- 用风力处理粪便

### 爆炸转化配方 (ExplosionTransformRecipe)
- 序列化器：`poopsky:explosion_transform`
- 配方文件夹：`explosion_transform/`
- 用于 PoopTNT 爆炸时 1方块→1方块 的转化（如圆石→沙砾→沙子）
- 支持 Ingredient 输入（可使用标签），Block 输出
- 运行时通过 `PoopTntUtil.findExplosionTransformOutput()` 查询
- 配方 JSON 示例：
```json
{
  "type": "poopsky:explosion_transform",
  "input": {
    "item": "minecraft:cobblestone"
  },
  "output": "minecraft:gravel"
}
```

## 数据生成 (Datagen)

数据生成器入口：`DataGenerators.java`。包含以下 Provider：

- `PSBlockStateProvider` — 方块状态 JSON
- `PSItemModelProvider` — 物品模型 JSON
- `PSBlockLootTableProvider` — 方块战利品表
- `PSEntityLootTableProvider` — 实体战利品表
- `PSRecipeProvider` — 配方
- `PSBlockTagProvider` / `PSItemTagProvider` — 标签
- `PSFluidTagsProvider` — 流体标签
- `PSEntityTypeTagsProvider` — 实体类型标签
- `PSDamageTypeTagsProvider` — 伤害类型标签
- `PSAdvancementProvider` — 进度
- `PSDatapackProvider` — 数据包
- `PSGlobalLootModifierProvider` — 全局战利品修改器
- `PSDataMapProvider` — 数据映射
- `PSDigestingRecipeGen` — Create 消解配方
- `PSFishingLootProvider` — 钓鱼战利品

### 运行数据生成
```bash
./gradlew runData
```

## 可选模组兼容

在 `PSMods` 枚举中定义可选模组，通过 `ModList.get().isLoaded()` 检查：

```java
if (ModList.get().isLoaded(PSMods.CREATE.id())) {
    CreatePlugin.register(modEventBus);
}
if (ModList.get().isLoaded(PSMods.TOUHOU_LITTLE_MAID.id())) {
    MaidPlugin.registry(modEventBus);
}
```

## 构建与运行

```bash
# 运行客户端
./gradlew runClient

# 运行服务端
./gradlew runServer

# 运行数据生成
./gradlew runData

# 构建模组
./gradlew build

# 刷新依赖
./gradlew --refresh-dependencies
```

## NeoForge 1.21.1 开发规则

### 1. 使用 DeferredRegister
- 所有注册必须使用 `DeferredRegister`，不得使用原版 `Registry.register()`
- 方块的 `DeferredRegister` 使用 `DeferredRegister.createBlocks()`
- 物品的 `DeferredRegister` 使用 `DeferredRegister.createItems()`

### 2. 方块属性
- 使用 `BlockBehaviour.Properties.of()` 构建
- 使用 `Properties.ofFullCopy()` 复用已有方块属性
- 记得设置 `mapColor`, `strength`, `sound` 等属性

### 3. 方块实体
- 必须实现 `ITickableBlockEntity` 并在 `PBlockEntityType` 中注册
- 使用 `BlockEntityProvider` 接口关联方块

### 4. 事件订阅
- 使用 `@EventBusSubscriber(modid = PoopSky.MOD_ID)` 注解
- 事件处理方法必须为 `public static`

### 5. Mixin
- 必须使用 `@Mixin` 注解
- 注入方法必须 `private`，方法名以 `poopsky$` 为前缀
- 在 `poopsky.mixins.json` 中声明
- 兼容性级别：`JAVA_21`

### 6. 网络包
- 使用 NeoForge 的 `CustomPacketPayload` 接口
- 实现 `STREAM_CODEC` 和 `CODEC`
- 在 `PSNetworking.register()` 中注册

### 7. 资源路径
- 使用 `PoopSky.loc("path")` 或 `ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "path")`
- 不要使用 `new ResourceLocation("poopsky:xxx")`

### 8. 配置
- 使用 `ModConfigSpec` 构建配置
- 在 `ModContainer.registerConfig()` 中注册
- 通过 `@EventBusSubscriber` 监听 `ModConfigEvent`

### 9. 数据组件
- 1.21.1 使用 `DataComponentType` 替代旧的 NBT
- 在 `PComponents` 中注册

### 10. 代码风格
- 逗号前置风格（如 `Properties.of()` 链式调用）
- 链式调用每行一个方法
- 不在代码中添加不必要的注释（除非特别复杂）
- 遵循项目现有代码的缩进和格式

### 11. 客户端代码分离
- 客户端代码放在 `PoopSkyClient.java` 中
- 客户端 Mixin 在 `poopsky.mixins.json` 的 `client` 数组中声明
- 使用 `DistExecutor` 或 `@OnlyIn` 处理客户端专用代码

### 12. 标签
- 使用 `PTags` 类集中管理标签
- 标签通过 datagen 的 `PSBlockTagProvider` / `PSItemTagProvider` 生成