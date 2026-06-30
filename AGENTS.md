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

PoopSky 是一个以**粪便为主题**的 Minecraft 空岛生存模组。玩家在虚空世界中从一棵"粪便树"开始，通过堆肥(Compooper)、筛矿(Sieve)、厕所排便等机制逐步发展科技树。核心玩法围绕粪便资源的收集、加工和转化展开。

## 项目结构

```
PoopSkyMod/
├── build.gradle                          # 构建脚本
├── gradle.properties                     # 版本与依赖配置
├── src/generated/resources/              # Datagen 输出资源
├── src/main/
│   ├── java/com/altnoir/poopsky/
│   │   ├── PoopSky.java                  # 主模组类（@Mod 入口）
│   │   ├── PoopSkyClient.java            # 客户端初始化
│   │   ├── Config.java                   # 配置项定义
│   │   ├── PItemGroups.java              # 创造模式物品栏
│   │   ├── PTags.java                    # 标签定义
│   │   ├── block/
│   │   │   ├── ToiletComponent.java      # 厕所组件
│   │   │   ├── ToiletType.java           # 厕所类型定义
│   │   │   ├── ToiletTypeProperty.java   # 厕所类型方块状态属性
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
│   │   ├── client/
│   │   │   ├── inventory/                # 菜单与客户端屏幕
│   │   │   ├── particle/                 # 客户端粒子
│   │   │   ├── renderer/                 # 客户端覆盖层/高亮渲染
│   │   │   └── sound/                    # 客户端循环音效
│   │   ├── item/
│   │   │   ├── PFoods.java               # 食物属性
│   │   │   ├── PArmorMaterials.java      # 盔甲材料
│   │   │   ├── PToolTiers.java           # 工具等级
│   │   │   └── p/                        # 具体物品实现
│   │   ├── entity/
│   │   │   ├── model/                    # 实体模型
│   │   │   ├── renderer/                 # 实体渲染器
│   │   │   └── p/                        # 具体实体实现
│   │   ├── init/                         # 注册中心 (PBlocks, PItems, PEntityType...)
│   │   │   ├── PBlocks.java              # 方块注册中心
│   │   │   ├── PItems.java               # 物品注册中心
│   │   │   ├── PBlockEntityType.java     # 方块实体注册
│   │   │   ├── PComponents.java          # 数据组件注册
│   │   │   ├── PRecipes.java             # 配方类型/序列化器注册
│   │   │   ├── PSNetworking.java         # 网络包注册
│   │   │   └── PToiletTypes.java         # 厕所类型注册
│   │   ├── effect/                       # 药水效果
│   │   ├── event/                        # 事件处理
│   │   ├── loot/                         # 自定义战利品函数
│   │   ├── recipe/                       # 自定义配方
│   │   ├── worldgen/                     # 世界生成
│   │   │   ├── PSChunkGenerators.java
│   │   │   ├── PSConfigureFeatures.java
│   │   │   ├── PSPlacedFeatures.java
│   │   │   ├── PSStructures.java
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
│       ├── assets/poopsky/               # 客户端资源
│       │   ├── blockstates/
│       │   ├── lang/
│       │   ├── models/
│       │   ├── sounds/
│       │   ├── textures/
│       │   ├── icon.png
│       │   └── sounds.json
│       └── data/                         # 数据包资源
│           ├── farmersdelight/recipe/    # Farmers Delight 兼容配方
│           ├── minecraft/tags/           # 原版命名空间标签
│           └── poopsky/
│               ├── jukebox_song/
│               ├── recipes/
│               ├── structure/
│               └── worldgen/
```

## 依赖模组

| 模组                  | 版本                   | 用途             |
|---------------------|----------------------|----------------|
| **Create**          | `6.0.8-168`          | 机械动力联动（风扇消解配方） |
| **JEI**             | `19.27.0.340`        | 配方查看           |
| **Sable Companion** | `1.6.0`              | 辅助库            |
| **KubeJS**          | `2101.7.2-build.295` | 脚本扩展           |
| **车万女仆**            | -                    | 女仆AI联动         |

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

| Mixin 类                          | 目标                             | 用途        |
|----------------------------------|--------------------------------|-----------|
| `LivingEntityMixin`              | `LivingEntity`                 | 流血伤害、时停无敌 |
| `FishingHookMixin`               | `FishingHook`                  | 钓鱼战利品修改   |
| `VillagerMixin`                  | `Villager`                     | 村民行为      |
| `TradeWithVillagerMixin`         | `ServerGamePacketListenerImpl` | 村民交易      |
| `CarvedPumpkinBlockMixin`        | `CarvedPumpkinBlock`           | 南瓜生成      |
| `BaseCoralPlantTypeBlockMixin`   | `BaseCoralPlantTypeBlock`      | 珊瑚        |
| `NoiseBasedChunkGeneratorMixin`  | `NoiseBasedChunkGenerator`     | 世界生成      |
| `ClientPacketListenerMixin`      | `ClientPacketListener`         | 客户端       |
| `CreateWorldScreenWorldTabMixin` | `CreateWorldScreen`            | 世界创建界面    |
| `WorldCreationUiStateMixin`      | `WorldCreationUiState`         | 世界创建UI    |

## 网络通信

使用 NeoForge 的 `PayloadRegistrar` 系统，协议版本为 `"1"`。

- `PlugActionPayload` — 客户端→服务端，马桶塞操作
- `PlugDismountPayload` — 客户端→服务端，下马桶
- `PlugInputPayload` — 客户端→服务端，马桶输入
- `TimeBellFreezePayload` — 服务端→客户端，时停铃

## 配置项

在 `Config.java` 中通过 `ModConfigSpec` 定义，配置文件类型为 `COMMON`：

| 配置项                    | 类型      | 默认值   | 说明                   |
|------------------------|---------|-------|----------------------|
| `setPoopskyDefault`    | boolean | true  | 是否将 poopsky 设为默认世界类型 |
| `voidNetherGeneration` | boolean | true  | 地狱是否也使用虚空生成          |
| `desperateWorld`       | boolean | false | 绝望世界模式               |
| `compooperCrafting`    | boolean | false | 禁用堆肥配方消耗液体           |
| `lavaFluid`            | boolean | true  | 禁用地下熔岩湖              |
| `plugTrades`           | boolean | false | 禁用塞子交易               |
| `upgradeTemplate`      | boolean | false | 禁用升级模板交易             |
| `unlimitedFreeze`      | boolean | false | 无限时停                 |
| `freezeFilter`         | boolean | false | 禁用时停滤镜 (客户端)         |

## 自定义配方

### 堆肥配方 (CompooperRecipe)
- 用于 JEI 显示堆肥桶配方

### 筛矿配方 (SieveRecipe)
- 序列化器：`poopsky:sieve`
- 配方文件夹：`sieve/`
- 用于筛网方块产出矿物

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

### 苍蝇窝产出配方 (FlyNestRecipe)
- 序列化器：`poopsky:fly_nest`
- 配方文件夹：`fly_nest/`
- 定义每种苍蝇品种在苍蝇窝中的产出物品
- 字段：`fly_type`（品种 ID，如 `"normal"`、`"red"`）→ `result`（ItemStack）
- 运行时通过 `PFlyRecipes.getFlyNestRecipes()` 查询，使用 `matches(flyTypeId)` 匹配
- 不使用标准 `RecipeInput` 匹配（`matches(RecipeInput, Level)` 始终返回 `false`）
- Datagen：`PSRecipeProvider.buildFlyNestRecipes()`，通过 `FlyNestRecipeBuilder.flyNest(typeId, result)` 构建
- 配方 JSON 示例：
```json
{
  "type": "poopsky:fly_nest",
  "fly_type": "red",
  "result": { "id": "minecraft:redstone", "count": 1 }
}
```

### 繁育箱变异配方 (BreedingBoxRecipe)
- 序列化器：`poopsky:breeding_box`
- 配方文件夹：`breeding_box/`
- 定义两只苍蝇杂交后产生的新品种及概率
- 字段：`parent1` + `parent2`（父本品种 ID）→ `result`（子代品种 ID）+ `chance`（0.0~1.0）
- 匹配是**双向的**：`parent1+parent2` 和 `parent2+parent1` 都能匹配
- 不使用标准 `RecipeInput` 匹配，`assemble()` 和 `getResultItem()` 返回 `ItemStack.EMPTY`（结果不是物品而是品种 ID）
- 运行时通过 `PFlyRecipes.getBreedingBoxRecipes()` 查询，使用 `matches(p1, p2)` 匹配
- Datagen：`PSRecipeProvider.buildBreedingBoxRecipes()`，通过 `BreedingBoxRecipeBuilder.breedingBox(p1, p2, result, chance)` 构建
- 配方 JSON 示例：
```json
{
  "type": "poopsky:breeding_box",
  "parent1": "red",
  "parent2": "blue",
  "result": "purple",
  "chance": 0.2
}
```

### Create 风扇消解配方 (DigestingRecipe)
- 仅在 Create 模组加载时注册
- 用风力处理粪便

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

### 13. BlockBehaviour.Properties 中的方块行为（非 Block 重写）
1.21.1 中许多方块行为**不是** `Block` 类的可重写方法，而是在 `BlockBehaviour.Properties` 中设置的。不要尝试在 `Block` 子类中 `@Override` 这些方法，它们不存在：

| 属性   | Properties 方法                          | 说明                            |
|------|----------------------------------------|-------------------------------|
| 红石导通 | `.isRedstoneConductor(Blocks::always)` | 默认基于碰撞箱判断，不完整方块需手动设为 `always` |
| 视线阻挡 | `.isViewBlocking(Blocks::never)`       | 控制方块是否阻挡视线                    |
| 有效刷怪 | `.isValidSpawn(Blocks::always)`        | 控制方块上是否可刷怪                    |
| 可被替换 | `.isReplacementReplaceable()`          | 控制方块是否可被替换                    |
| 信号源  | `.isSignalSource()`                    | 控制方块是否为红石信号源                  |

**常见陷阱**：不完整碰撞箱的方块（如 `PoopBlock`）默认无法被红石充能，因为默认 `isRedstoneConductor` 依赖 `isCollisionShapeFullBlock()`。必须显式设置 `.isRedstoneConductor(Blocks::always)`。

### 14. 客户端循环音效
实现实体持续循环音效时，不要依赖 `getAmbientSound()`（它是间歇性的），应使用 `AbstractTickableSoundInstance` + `looping = true` 模式：

- 创建 `XxxSoundInstance extends AbstractTickableSoundInstance`，设置 `this.looping = true`
- 创建 `XxxSoundWrapper` 管理音效生命周期（创建、tick、停止）
- 在实体的客户端构造函数中初始化 Wrapper，`aiStep()` 中调用 `tick()`

**关键**：音效实例的初始 `volume` 必须 > 0（如 `0.1F`）。如果初始为 0，Minecraft 的 `SoundEngine` 不会创建 OpenAL 音频通道，后续 `tick()` 中调高音量也不会有声音。

参考实现：`TPFlySoundInstance` / `TPFlySoundWrapper` / `FlyBuzzSoundInstance` / `FlyBuzzSoundWrapper`

### 15. Minecraft 坐标系与方向向量
Minecraft 使用右手坐标系（x 向南，z 向西，yaw 顺时针增大）：

```
forward = (-sin(yaw), 0, cos(yaw))   // 前方向
right   = (-forward.z, 0, forward.x) // 右方向 = (cos(yaw), 0, sin(yaw))
left    = -right                      // 左方向
back    = -forward                    // 后方向
```

**常见错误**：用 `(forward.z, 0, -forward.x)` 从 forward 推导 right，这实际是**左方向**（逆时针旋转 90°）。正确的右方向是 forward **顺时针旋转 90°**，即 `(-forward.z, 0, forward.x)`。

### 16. JEI 配方显示
JEI 配方要正确显示配方 ID，`IRecipeCategory` 的泛型参数必须是 `RecipeHolder<...>`：

```java
// ✅ 正确：可以显示配方 ID
public class XxxRecipeCategory implements IRecipeCategory<RecipeHolder<XxxRecipe>> {
    static final RecipeType<RecipeHolder<XxxRecipe>> TYPE =
        RecipeType.createRecipeHolderType(MOD_ID, "xxx", RecipeHolder.class);
}

// ❌ 错误：丢失配方 ID
public class XxxRecipeCategory implements IRecipeCategory<XxxJeiRecipe> {
    static final RecipeType<XxxJeiRecipe> TYPE =
        RecipeType.create(MOD_ID, "xxx", XxxJeiRecipe.class);
}
```

注册时直接传 `recipeManager.getAllRecipesFor(recipeType)` 的结果，不要手动 map 转换为纯 record（会丢失 ID）。

### 17. 实体乘骑与输入控制
实现可控载具实体时：
- 输入处理在 `tick()` 或自定义方法中，通过 `getControllingPassenger()` 获取驾驶员
- 左右移动的输入值：按左 = 负值，按右 = 正值
- 朝向插值系数影响操控手感：`0.5f` 有明显漂移感，`0.9f` 几乎即时跟随，`1.0f` 完全同步
- 速度阻尼（DAMPING）过高也会导致转向时漂移感
### 18. 文件编码规范
- 所有 Java 源文件必须使用 **无 BOM 的 UTF-8** 编码
- Java 编译器不识别 UTF-8 BOM (`\ufeff` / `EF BB BF`)，会导致编译错误 `非法字符: '\ufeff'`
- 创建或写入文件时，使用 `[System.IO.File]::WriteAllText(path, content, [System.Text.UTF8Encoding]::new(False))` 而非 `Out-File -Encoding utf8`（后者会自动添加 BOM）
- 如果文件已有 BOM，用以下方式移除：
  ```powershell
  $bytes = [System.IO.File]::ReadAllBytes($file)
  if ($bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
      $newBytes = New-Object byte[] ($bytes.Length - 3)
      [Array]::Copy($bytes, 3, $newBytes, 0, $bytes.Length - 3)
      [System.IO.File]::WriteAllBytes($file, $newBytes)
  }
  ```
- JSON 资源文件（lang、models 等）同样必须使用无 BOM 的 UTF-8
