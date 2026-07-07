# PoopSkyMod 版本移植完整指南

> **目标**: 建立一套可维护、可扩展的版本迁移体系，从轻量封装逐步演进到完整的跨版本支持架构

---

## 📋 目录

1. [项目现状分析](#-项目现状分析)
2. [阶段一：轻量封装（立即实施）](#-阶段一轻量封装立即实施)
3. [阶段二：渐进式抽象层（中期目标）](#-阶段二渐进式抽象层中期目标)
4. [阶段三：完整抽象层架构（长期目标）](#-阶段三完整抽象层架构长期目标)
5. [阶段四：Gradle多源集多版本构建](#-阶段四gradle多源集多版本构建)
6. [自动化工具与脚本](#-自动化工具与脚本)
7. [API变更追踪体系](#-api变更追踪体系)
8. [最佳实践与注意事项](#-最佳实践与注意事项)

---

## 📊 项目现状分析

### 当前技术栈

| 属性 | 值 |
|------|-----|
| **模组ID** | poopsky |
| **当前版本** | Minecraft 1.21.1 + NeoForge 21.1.219 |
| **Java版本** | 21 |
| **代码规模** | 200+ Java文件 |
| **构建系统** | Gradle + net.neoforged.moddev 插件 |
| **主要依赖** | JEI, Create, Ponder, Flywheel, Registrate, KubeJS, Touhou Little Maid, Sable Companion |

### 现有注册架构

```
src/main/java/com/altnoir/poopsky/
├── PoopSky.java                    # 主入口类
├── init/                           # 注册类集合
│   ├── PBlocks.java                # 方块注册 (~550行，最复杂)
│   ├── PItems.java                 # 物品注册 (~130行)
│   ├── PEntityType.java            # 实体类型注册
│   ├── PEffects.java               # 药水效果注册
│   ├── PSoundEvents.java           # 声音事件注册
│   ├── PBlockEntityType.java       # 方块实体类型注册
│   ├── PComponents.java            # 数据组件注册 (1.20.5+新API)
│   ├── PSNetworking.java           # 网络包注册
│   ├── PFluids.java / PFluidTypes.java  # 流体注册
│   ├── PMenuTypes.java             # 菜单类型注册
│   ├── PParticles.java             # 粒子注册
│   ├── PPotions.java               # 药水注册
│   ├── PLootFunctions.java         # 掉落函数注册
│   ├── PVillagers.java             # 村民职业注册
│   ├── PRecipes.java               # 配方类型注册
│   ├── PItemGroups.java            # 物品栏分类注册
│   ├── PStats.java                 # 统计数据注册
│   ├── PBlockSetType.java          # 方块声音类型注册
│   └── PWoodType.java              # 木头类型注册
├── common/                         # 业务逻辑 (相对稳定)
│   ├── block/                      # 方块实现
│   ├── entity/                     # 实体实现
│   ├── item/                       # 物品实现
│   ├── effect/                     # 效果实现
│   ├── recipe/                     # 配方实现
│   ├── event/                      # 事件处理
│   └── villager/                   # 村民相关
├── client/                         # 客户端代码
├── datagen/                        # 数据生成器
├── compat/                         # 兼容性模块 (JEI/Create/Maid)
├── mixin/                          # Mixin注入
├── network/                        # 网络包定义
└── worldgen/                       # 世界生成
```

### 核心注册模式分析

所有注册类都遵循相同的模式：

```java
public class PXXX {
    // 1. 创建DeferredRegister实例
    public static final DeferredRegister<XXX> REGISTRY = DeferredRegister.create(Registries.XXX, PoopSky.MOD_ID);
    
    // 2. 注册条目
    public static final DeferredHolder<..., ...> ITEM = REGISTRY.register("name", () -> new ...);
    
    // 3. 绑定事件总线
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
```

### 版本升级痛点识别

#### 高频变动区域（每次升级都可能受影响）

1. **网络系统** (`PSNetworking.java`)
   - Payload API频繁调整
   - 编解码器接口变更
   - 版本协商机制变化

2. **数据组件** (`PComponents.java`)
   - Data Components是1.20.5新增的API
   - Codec和StreamCodec规范可能微调
   - 持久化策略可能变更

3. **Mixin目标**
   - Mojang经常重构内部类
   - 方法签名可能改变
   - 新增/删除字段

4. **渲染系统** (客户端代码)
   - 渲染器API持续演化
   - 着色器系统更新
   - 模型加载机制调整

5. **依赖库兼容性**
   - JEI API变更
   - Create Mod内部API调整
   - 其他依赖的版本匹配问题

#### 中频变动区域（大版本更新时受影响）

1. **实体系统** - 属性、AI目标、生成条件
2. **方块行为** - 状态属性、交互逻辑
3. **物品系统** - 组件系统、使用逻辑
4. **配方系统** - 序列化格式、类型系统
5. **世界生成** - 特征放置、结构模板

#### 低频变动区域（相对稳定）

1. **业务逻辑** - 自定义的行为算法
2. **配置系统** - mod配置项
3. **资源包** - 模型、纹理、语言文件
4. **数据生成** - 大部分datagen代码

---

## 🚀 阶段一：轻量封装（立即实施）

### 目标

在不大幅改动现有代码的前提下，建立统一的注册入口点和基础工具集。

### 预计工作量: 2-3小时

### 1.1 创建统一注册管理器

**文件位置**: `src/main/java/com/altnoir/poopsky/api/PSRegistries.java`

```java
package com.altnoir.poopsky.api;

import com.altnoir.poopsky.PoopSky;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一注册表管理器
 * 
 * 功能：
 * 1. 集中管理所有DeferredRegister实例
 * 2. 提供批量注册能力
 * 3. 支持注册顺序控制
 * 4. 提供调试信息输出
 */
public final class PSRegistries {
    
    private static final String MOD_ID = PoopSky.MOD_ID;
    
    private static final List<DeferredRegister<?>> registries = new ArrayList<>();
    private static final List<String> registryOrder = new ArrayList<>();
    
    /**
     * 创建并跟踪一个DeferredRegister实例
     */
    public static <T> DeferredRegister<T> create(Class<T> type, String registryKey) {
        var registry = DeferredRegister.create(type, registryKey, MOD_ID);
        registries.add(registry);
        registryOrder.add(registryKey);
        return registry;
    }
    
    /**
     * 快捷方法：创建方块注册表
     */
    public static DeferredRegister.Blocks createBlocks() {
        return (DeferredRegister.Blocks) create(net.minecraft.world.level.block.Block.class, "block");
    }
    
    /**
     * 快捷方法：创建物品注册表
     */
    public static DeferredRegister.Items createItems() {
        return (DeferredRegister.Items) create(net.minecraft.world.item.Item.class, "item");
    }
    
    /**
     * 批量注册所有注册表到事件总线
     * 
     * @param bus NeoForge模组事件总线
     */
    public static void registerAll(IEventBus bus) {
        PoopSky.LOGGER.info("Registering {} registries for {}", registries.size(), MOD_ID);
        
        for (int i = 0; i < registries.size(); i++) {
            var registry = registries.get(i);
            var name = registryOrder.get(i);
            registry.register(bus);
            PoopSky.LOGGER.debug("  ✓ Registered: {}", name);
        }
    }
    
    /**
     * 获取已注册的注册表数量（用于调试）
     */
    public static int getRegistryCount() {
        return registries.size();
    }
}
```

### 1.2 创建注册辅助工具类

**文件位置**: `src/main/java/com/altnoir/poopsky/api/RegistryHelper.java`

```java
package com.altnoir.poopsky.api;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

/**
 * 注册辅助工具类
 * 
 * 提供常用的注册快捷方法和工具函数，
 * 减少样板代码，统一注册风格。
 */
public final class RegistryHelper {
    
    private RegistryHelper() {} // 工具类，禁止实例化
    
    /**
     * 创建资源位置
     */
    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, path);
    }
    
    /**
     * 注册简单物品（无特殊构造函数）
     * 
     * 用法示例：
     * DeferredItem<Item> item = RegistryHelper.simpleItem("my_item", new Item.Properties());
     */
    public static DeferredItem<?> simpleItem(String name, net.minecraft.world.item.Item.Properties properties) {
        return PItems.ITEMS.registerSimpleItem(name, properties);
    }
    
    /**
     * 批量注册相关物品组
     * 
     * 例如：同时注册方块及其对应的物品
     */
    public static void registerBlockWithItem(
            String name,
            Supplier<net.minecraft.world.level.block.Block> blockSupplier,
            Supplier<net.minecraft.world.item.Item> itemSupplier
    ) {
        PBlocks.BLOCKS.register(name, blockSupplier);
        PItems.ITEMS.register(name, itemSupplier);
    }
    
    /**
     * 安全获取已注册的对象
     * 如果对象不存在，返回默认值并记录警告
     */
    public static <T> T getOrDefault(DeferredItem<T> deferred, T defaultValue) {
        try {
            return deferred.get();
        } catch (Exception e) {
            PoopSky.LOGGER.warn("Failed to get registered object, using default value", e);
            return defaultValue;
        }
    }
}
```

### 1.3 重构现有注册类（示例）

#### 改造前：PItems.java

```java
// ❌ 原始写法
public class PItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PoopSky.MOD_ID);
    
    public static final DeferredItem<Item> POOP = ITEMS.register("poop", () ->
            new PoopItem(new Item.Properties().food(PFoods.POOP).stacksTo(88)));
    
    // ... 更多物品
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
```

#### 改造后：PItems.java

```java
// ✅ 使用封装后的写法
public class PItems {
    // 使用统一的注册表创建方法
    public static final DeferredRegister.Items ITEMS = PSRegistries.createItems();
    
    public static final DeferredItem<Item> POOP = ITEMS.register("poop", () ->
            new PoopItem(new Item.Properties()
                    .food(PFoods.POOP)
                    .stacksTo(88))
    );
    
    // 可以使用工具类的快捷方法
    public static final DeferredItem<Item> SPALL = RegistryHelper.simpleItem("spall", 
            new Item.Properties());
    
    // ... 更多物品
    
    // 不再需要单独的register方法，由PSRegistries统一处理
}
```

### 1.4 简化主入口类

#### 改造前：PoopSky.java 构造函数

```java
// ❌ 原始写法 - 需要逐一调用每个register方法
public PoopSky(IEventBus modEventBus, ModContainer modContainer) {
    modEventBus.addListener(this::commonSetup);
    modEventBus.addListener(PSNetworking::register);

    PEffects.register(modEventBus);
    PPotions.register(modEventBus);
    PParticles.register(modEventBus);

    PBlocks.register(modEventBus);
    PBlockEntityType.register(modEventBus);
    PItems.register(modEventBus);
    PEntityType.register(modEventBus);
    PSFoliagePlacerTypes.register(modEventBus);
    PSStructures.register(modEventBus);
    PSChunkGenerators.register(modEventBus);

    PItemGroups.register(modEventBus);
    PSoundEvents.register(modEventBus);
    PStats.register(modEventBus);

    PComponents.register(modEventBus);
    PLootFunctions.register(modEventBus);
    PVillagers.register(modEventBus);
    PRecipes.register(modEventBus);

    PFluids.FLUIDS.register(modEventBus);
    PFluidTypes.FLUID_TYPES.register(modEventBus);
    
    // ... 更多初始化代码
}
```

#### 改造后：PoopSky.java 构造函数

```java
// ✅ 封装后的写法 - 统一注册
public PoopSky(IEventBus modEventBus, ModContainer modContainer) {
    modEventBus.addListener(this::commonSetup);
    modEventBus.addListener(PSNetworking::register);

    // 一行代码完成所有注册！
    PSRegistries.registerAll(modEventBus);
    
    // 兼容性模块按需加载
    if (ModList.get().isLoaded(PSMods.TOUHOU_LITTLE_MAID.id())) {
        MaidPlugin.registry(modEventBus);
    }
    if (ModList.get().isLoaded(PSMods.CREATE.id())) {
        CreatePlugin.register(modEventBus);
    }
    
    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
}
```

### 1.5 创建版本常量类

**文件位置**: `src/main/java/com/altnoir/poopsky/api/PSVersion.java`

```java
package com.altnoir.poopsky.api;

/**
 * 版本信息常量类
 * 
 * 集中管理所有版本相关的常量，
 * 方便在升级时快速定位和修改。
 */
public final class PSVersion {
    
    private PSVersion() {}
    
    // ===== Minecraft版本 =====
    public static final String MC_VERSION = "1.21.1";
    public static final String MC_VERSION_RANGE = "[1.21.1, 1.21.2)";
    
    // ===== NeoForge版本 =====
    public static final String NEO_VERSION = "21.1.219";
    public static final String NEO_VERSION_RANGE = "[21.1.0,)";
    
    // ===== Loader版本 =====
    public static final String LOADER_VERSION_RANGE = "[4,)";
    
    // ===== Mod版本 =====
    public static final String MOD_VERSION = "1.21.1-2.0.1";
    
    // ===== 兼容的目标版本列表 =====
    public static final String[] SUPPORTED_MC_VERSIONS = {
        "1.21.1",
        "1.21.11"  // 未来支持
    };
    
    /**
     * 检查当前是否运行在指定的MC版本
     */
    public static boolean isMCVersion(String version) {
        return MC_VERSION.equals(version);
    }
    
    /**
     * 获取主要版本号 (例如: 1.21)
     */
    public static String getMajorVersion() {
        return MC_VERSION.substring(0, MC_VERSION.lastIndexOf('.'));
    }
}
```

### 1.6 实施检查清单

- [ ] 创建 `api` 包目录
- [ ] 实现 `PSRegistries.java`
- [ ] 实现 `RegistryHelper.java`
- [ ] 实现 `PSVersion.java`
- [ ] 逐个改造现有注册类（优先级从高到低）：
  - [ ] `PItems.java` （最简单，适合练手）
  - [ ] `PEffects.java`
  - [ ] `PSoundEvents.java`
  - [ ] `PEntityType.java`
  - [ ] `PComponents.java`
  - [ ] `PBlocks.java` （最复杂，最后改造）
- [ ] 简化 `PoopSky.java` 主入口
- [ ] 测试编译和运行
- [ ] 更新文档

---

## 🏗️ 阶段二：渐进式抽象层（中期目标）

### 目标

为高频变动的子系统建立抽象接口，将版本相关的实现细节隔离。

### 预计工作量: 8-12小时（分多次完成）

### 2.1 设计抽象层架构

```
com.altnoir.poopsky/
├── api/                              # 抽象接口层（版本无关）
│   ├── registry/
│   │   ├── IRegistryManager.java     # 注册管理器接口
│   │   ├── IBlockRegistry.java       # 方块注册接口
│   │   ├── IItemRegistry.java        # 物品注册接口
│   │   ├── IEntityRegistry.java      # 实体注册接口
│   │   └── ...
│   ├── network/
│   │   ├── INetworkHandler.java      # 网络包处理器接口
│   │   ├── INetworkChannel.java      # 网络通道接口
│   │   └── IPayload.java             # 数据包接口
│   ├── component/
│   │   ├── IComponentFactory.java    # 组件工厂接口
│   │   └── IComponentSerializer.java # 组件序列化器接口
│   ├── rendering/
│   │   ├── IRendererProvider.java    # 渲染器提供者接口
│   │   └── IRenderType.java          # 渲染类型接口
│   └── PSVersion.java                # 版本常量（已在阶段一创建）
│
├── impl/                             # 具体实现层（版本相关）
│   └── neoforge_21_1/               # 1.21.1的具体实现
│       ├── NeoForgeRegistryManager.java
│       ├── NeoForgeBlockRegistry.java
│       ├── NeoForgeNetworkHandler.java
│       ├── NeoForgeComponentFactory.java
│       └── ...
│
├── init/                             # 注册类（使用api接口）
│   ├── PBlocks.java                  # 改为使用 IBlockRegistry
│   ├── PItems.java                   # 改为使用 IItemRegistry
│   └── ...
│
└── common/                           # 业务逻辑（不变）
```

### 2.2 核心接口定义

#### IRegistryManager.java

```java
package com.altnoir.poopsky.api.registry;

import net.neoforged.bus.api.IEventBus;

/**
 * 注册管理器接口
 * 
 * 提供统一的注册表管理能力，
 * 屏蔽不同版本的实现差异。
 */
public interface IRegistryManager {
    
    IBlockRegistry blocks();
    IItemRegistry items();
    IEntityRegistry entities();
    IEffectRegistry effects();
    ISoundRegistry sounds();
    
    void registerAll(IEventBus bus);
    String getVersion();
}
```

#### IBlockRegistry.java

```java
package com.altnoir.poopsky.api.registry;

import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

/**
 * 方块注册接口
 */
public interface IBlockRegistry {
    
    <T extends Block> RegistryHolder<T> register(String name, Supplier<T> supplier);
    
    <T extends Block> RegistryHolder<T> registerSimple(String name, Supplier<T> supplier);
    
    void registerWithItem(
            String name,
            Supplier<? extends Block> blockSupplier,
            Supplier<? extends net.minecraft.world.item.Item> itemSupplier
    );
    
    <T extends Block> T get(RegistryHolder<T> holder);
}

/**
 * 注册持有者接口（泛型包装）
 */
public interface RegistryHolder<T> {
    T get();
    net.minecraft.resources.ResourceLocation getId();
}
```

#### INetworkHandler.java

```java
package com.altnoir.poopsky.api.network;

import java.util.function.Consumer;

/**
 * 网络处理器接口
 * 
 * 封装NeoForge的网络系统，
 * 处理版本间的API差异。
 */
public interface INetworkHandler {
    
    <T> void registerToServer(Object type, Object codec, Consumer<T> handler);
    
    <T> void registerToClient(Object type, Object codec, Consumer<T> handler);
    
    void sendToServer(Object payload);
    
    void sendToPlayer(Object payload, net.minecraft.server.level.ServerPlayer player);
    
    void sendToAll(Object payload);
    
    void setProtocolVersion(String version);
}
```

### 2.3 实现适配器（以网络为例）

#### NeoForgeNetworkHandler.java (1.21.1实现)

```java
package com.altnoir.poopsky.impl.neoforge_21_1.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.api.network.INetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Consumer;

/**
 * NeoForge 1.21.1 的网络处理器实现
 */
public class NeoForgeNetworkHandler implements INetworkHandler {
    
    private PayloadRegistrar registrar;
    private String protocolVersion = "1";
    
    public void init(RegisterPayloadHandlersEvent event) {
        this.registrar = event.registrar(PoopSky.MOD_ID)
                .versioned(protocolVersion);
    }
    
    @Override
    public <T> void registerToServer(Object type, Object codec, Consumer<T> handler) {
        // 具体的NeoForge 1.21.1 API调用
        // 升级时只需修改此处的实现
    }
    
    @Override
    public <T> void registerToClient(Object type, Object codec, Consumer<T> handler) {
        // 具体的NeoForge 1.21.1 API调用
    }
    
    @Override
    public void sendToServer(Object payload) {
        net.neoforged.neoforge.network.PacketDistributor.sendToServer(payload);
    }
    
    @Override
    public void sendToPlayer(Object payload, ServerPlayer player) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, payload);
    }
    
    @Override
    public void sendToAll(Object payload) {
        net.neoforged.neoforge.network.PacketDistributor.sendToAll(payload);
    }
    
    @Override
    public void setProtocolVersion(String version) {
        this.protocolVersion = version;
    }
}
```

### 2.4 改造现有注册类（以网络为例）

#### 改造前：PSNetworking.java

```java
// ❌ 直接依赖NeoForge具体API
public class PSNetworking {
    private static final String VERSION = "1";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PoopSky.MOD_ID).versioned(VERSION);
        registrar.playToServer(
                PlugActionPayload.TYPE,
                PlugActionPayload.CODEC,
                PlugActionPayload::handle
        );
        // ... 更多注册
    }
}
```

#### 改造后：PSNetworking.java

```java
// ✅ 使用抽象接口
public class PSNetworking {
    
    private static INetworkHandler networkHandler;
    
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        // 获取当前版本的实现
        networkHandler = NetworkHandlerFactory.create(event);
        
        // 使用抽象接口注册
        networkHandler.registerToServer(
                PlugActionPayload.TYPE,
                PlugActionPayload.CODEC,
                PlugActionPayload::handle
        );
        networkHandler.registerToServer(
                PlugDismountPayload.TYPE,
                PlugDismountPayload.CODEC,
                PlugDismountPayload::handle
        );
        // ... 更多注册
    }
    
    public static INetworkHandler getNetworkHandler() {
        return networkHandler;
    }
}
```

### 2.5 创建工厂类

**文件位置**: `src/main/java/com/altnoir/poopsky/api/NetworkHandlerFactory.java`

```java
package com.altnoir.poopsky.api;

import com.altnoir.poopsky.api.network.INetworkHandler;
import com.altnoir.poopsky.impl.neoforge_21_1.network.NeoForgeNetworkHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * 网络处理器工厂
 * 
 * 根据当前环境选择合适的实现。
 */
public final class NetworkHandlerFactory {
    
    private NetworkHandlerFactory() {}
    
    public static INetworkHandler create(RegisterPayloadHandlersEvent event) {
        String version = PSVersion.NEO_VERSION;
        
        if (version.startsWith("21.1")) {
            var handler = new NeoForgeNetworkHandler();
            handler.init(event);
            return handler;
        }
        
        throw new UnsupportedOperationException(
                "Unsupported NeoForge version: " + version
        );
    }
}
```

### 2.6 优先级排序

按照影响程度和变更频率，建议按以下顺序实施抽象：

| 优先级 | 子系统 | 原因 | 预计工时 |
|--------|--------|------|----------|
| 🔴 **P0** | 网络 (Networking) | API变化最频繁 | 2-3h |
| 🔴 **P0** | 数据组件 (Components) | 较新的API，可能调整 | 2h |
| 🟡 **P1** | 渲染 (Rendering) | 客户端API易变 | 3-4h |
| 🟡 **P1** | Mixin系统 | 目标类可能移动 | 2h |
| 🟢 **P2** | 方块/物品注册 | 相对稳定 | 1-2h |
| 🟢 **P2** | 实体系统 | 中等频率变化 | 2h |
| ⚪ **P3** | 配方/世界生成 | 较低频率变化 | 1-2h |

### 2.7 实施检查清单

- [ ] 设计并定义核心接口（IRegistryManager, IBlockRegistry等）
- [ ] 实现网络抽象层（INetworkHandler → NeoForgeNetworkHandler）
- [ ] 实现组件抽象层（IComponentFactory → NeoForgeComponentFactory）
- [ ] 逐步改造现有注册类使用新接口
- [ ] 创建工厂类用于实例化正确的实现
- [ ] 编写单元测试验证抽象层正确性
- [ ] 更新技术文档

---

## 🎨 阶段三：完整抽象层架构（长期目标）

### 目标

建立完全版本无关的业务代码，实现真正的"一次编写，多处运行"。

### 适用场景

- 需要同时维护多个MC版本发布
- 团队协作，需要清晰的层次划分
- 长期项目，预期会经历多次大版本升级

### 3.1 完整架构设计

```
PoopSkyMod/
├── src/
│   ├── main/
│   │   ├── java/com/altnoir/poopsky/
│   │   │   ├── api/                          # 公共API层（版本无关）
│   │   │   │   ├── core/
│   │   │   │   │   ├── IModCore.java         # 模组核心接口
│   │   │   │   │   ├── IPlatformHelper.java  # 平台助手接口
│   │   │   │   │   └── IEventBus.java        # 事件总线抽象
│   │   │   │   ├── registry/
│   │   │   │   │   ├── IRegistryManager.java
│   │   │   │   │   ├── IBlockRegistry.java
│   │   │   │   │   ├── IItemRegistry.java
│   │   │   │   │   ├── IEntityRegistry.java
│   │   │   │   │   ├── IEffectRegistry.java
│   │   │   │   │   ├── ISoundRegistry.java
│   │   │   │   │   ├── IFluidRegistry.java
│   │   │   │   │   ├── IRecipeRegistry.java
│   │   │   │   │   ├── IDataComponentRegistry.java
│   │   │   │   │   └── IMenuTypeRegistry.java
│   │   │   │   ├── network/
│   │   │   │   │   ├── INetworkManager.java
│   │   │   │   │   ├── IPayload.java
│   │   │   │   │   ├── IPayloadHandler.java
│   │   │   │   │   └── IPacketDistributor.java
│   │   │   │   ├── rendering/
│   │   │   │   │   ├── IRendererRegistry.java
│   │   │   │   │   ├── IBlockEntityRenderer.java
│   │   │   │   │   ├── IEntityRenderer.java
│   │   │   │   │   └── IScreenFactory.java
│   │   │   │   ├── world/
│   │   │   │   │   ├── IFeatureRegistry.java
│   │   │   │   │   ├── IStructureRegistry.java
│   │   │   │   │   └── IBiomeModifier.java
│   │   │   │   ├── data/
│   │   │   │   │   ├── IDataProvider.java
│   │   │   │   │   ├── IRecipeGenerator.java
│   │   │   │   │   └── ILootTableGenerator.java
│   │   │   │   ├── compat/
│   │   │   │   │   ├── ICompatModule.java
│   │   │   │   │   └── ICompatLoader.java
│   │   │   │   └── event/
│   │   │   │       ├── IGameEvent.java
│   │   │   │       ├── ILifecycleEvent.java
│   │   │   │       └── IPlayerEvent.java
│   │   │   │
│   │   │   ├── common/                      # 业务逻辑（完全版本无关）
│   │   │   │   ├── block/
│   │   │   │   │   ├── abs/                # 抽象基类
│   │   │   │   │   │   ├── AbstractPoopBlock.java
│   │   │   │   │   │   ├── AbstractToiletBlock.java
│   │   │   │   │   │   └── AbstractCompooperBlock.java
│   │   │   │   │   └── p/                  # 具体实现
│   │   │   │   ├── entity/
│   │   │   │   ├── item/
│   │   │   │   ├── effect/
│   │   │   │   ├── recipe/
│   │   │   │   └── ...
│   │   │   │
│   │   │   └── init/                       # 初始化器（使用api接口）
│   │   │       ├── ModInitializer.java      # 主初始化器
│   │   │       ├── BlockInitializer.java
│   │   │       ├── ItemInitializer.java
│   │   │       └── ...
│   │   │
│   │   └── resources/                      # 资源文件（共享）
│   │       ├── assets/poopsky/
│   │       ├── data/poopsky/
│   │       └── ...
│   │
│   └── neoforge/                           # NeoForge特定实现
│       └── java/com/altnoir/poopsky/neoforge/
│           ├── core/
│           │   ├── NeoForgeModCore.java     # 模组核心实现
│           │   ├── NeoForgePlatform.java   # 平台助手实现
│           │   └── EventBusWrapper.java    # 事件总线包装
│           ├── registry/
│           │   ├── NeoForgeRegistryManager.java
│           │   ├── NeoForgeBlockRegistry.java
│           │   ├── NeoForgeItemRegistry.java
│           │   └── ... (每个注册表的实现)
│           ├── network/
│           │   ├── NeoForgeNetworkManager.java
│           │   ├── NeoForgePayload.java
│           │   └── NeoForgePacketDistributor.java
│           ├── rendering/
│           │   ├── NeoForgeRendererRegistry.java
│           │   └── ...
│           ├── mixin/
│           │   └── (Mixin实现)
│           └── PoopSkyNeoforge.java        # NeoForge入口点
│
├── build.gradle                            # 多平台构建配置
├── gradle.properties                       # 版本属性
└── settings.gradle                         # 项目设置
```

### 3.2 核心抽象接口示例

#### IModCore.java

```java
package com.altnoir.poopsky.api.core;

import com.altnoir.poopsky.api.registry.IRegistryManager;
import com.altnoir.poopsky.api.network.INetworkManager;

/**
 * 模组核心接口
 * 
 * 定义模组的生命周期和基本功能，
 * 所有平台实现都需要遵循此契约。
 */
public interface IModCore {
    
    String getModId();
    String getModName();
    String getVersion();
    
    void initialize(Object modBus);
    void commonSetup(Object event);
    
    default void clientSetup(Object event) {}
    
    IRegistryManager getRegistryManager();
    INetworkManager getNetworkManager();
    IPlatformHelper getPlatform();
    
    org.slf4j.Logger getLogger();
}
```

#### IPlatformHelper.java

```java
package com.altnoir.poopsky.api.core;

import java.nio.file.Path;

/**
 * 平台助手接口
 * 
 * 提供平台特定的功能访问，
 * 如物理路径、环境检测等。
 */
public interface IPlatformHelper {
    
    String getPlatformName();
    boolean isPhysicalClient();
    boolean isDevelopmentEnvironment();
    Path getModFilePath();
    boolean isModLoaded(String modId);
    void executeOnMainThread(Runnable task);
}
```

### 3.3 业务代码示例（完全版本无关）

#### AbstractPoopBlock.java

```java
package com.altnoir.poopsky.common.block.abs;

import com.altnoir.poopsky.api.core.IModCore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 抽象粪便方块基类
 * 
 * 此类完全不知道底层平台的细节，
 * 只关注业务逻辑。
 */
public abstract class AbstractPoopBlock extends Block {
    
    protected final IModCore core;
    
    protected AbstractPoopBlock(BlockBehaviour.Properties properties, IModCore core) {
        super(properties);
        this.core = core;
    }
    
    protected abstract void onPoopLogic(net.minecraft.world.level.Level level, 
                                       net.minecraft.core.BlockPos pos, 
                                       net.minecraft.world.level.block.state.BlockState state);
    
    protected float getDecaySpeed() {
        return 1.0F;
    }
    
    public boolean canFertilize() {
        return true;
    }
}
```

#### PoopBlock.java (具体实现)

```java
package com.altnoir.poopsky.common.block.p;

import com.altnoir.poopsky.api.core.IModCore;
import com.altnoir.poopsky.common.block.abs.AbstractPoopBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

/**
 * 普通粪便方块
 * 
 * 业务逻辑实现，不包含任何平台相关代码。
 */
public class PoopBlock extends AbstractPoopBlock {
    
    public PoopBlock(IModCore core) {
        super(createProperties(), core);
    }
    
    private static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .strength(0.5F)
                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                .randomTicks()
                .speedFactor(0.4F)
                .isValidSpawn((state, getter, pos, type) -> true)
                .isRedstoneConductor((state, getter, pos) -> true)
                .isSuffocating((state, getter, pos) -> true)
                .instrument(NoteBlockInstrument.COW_BELL);
    }
    
    @Override
    protected void onPoopLogic(net.minecraft.world.level.Level level, 
                               net.minecraft.core.BlockPos pos, 
                               net.minecraft.world.level.block.state.BlockState state) {
        if (level.getRandom().nextFloat() < 0.01F) {
            spawnFlyNearby(level, pos);
        }
    }
    
    private void spawnFlyNearby(net.minecraft.world.level.Level level, 
                                net.minecraft.core.BlockPos pos) {
        // 使用core获取实体注册表来生成苍蝇
        // 完全不关心如何注册实体，只关心业务逻辑
    }
    
    @Override
    protected float getDecaySpeed() {
        return 1.5F;
    }
}
```

### 3.4 初始化流程

#### ModInitializer.java

```java
package com.altnoir.poopsky.init;

import com.altnoir.poopsky.api.core.IModCore;
import com.altnoir.poopsky.api.registry.IRegistryManager;
import com.altnoir.poopsky.api.network.INetworkManager;

/**
 * 模组初始化器
 * 
 * 协调所有子系统的初始化顺序，
 * 确保依赖关系正确。
 */
public final class ModInitializer {
    
    private ModInitializer() {}
    
    public static void initialize(IModCore core, Object eventBus) {
        IRegistryManager registries = core.getRegistryManager();
        INetworkManager network = core.getNetworkManager();
        
        // 阶段1：注册核心内容（无依赖）
        core.getLogger().info("Phase 1: Registering core content...");
        registerEffects(registries);
        registerParticles(registries);
        registerFluids(registries);
        
        // 阶段2：注册依赖核心的内容
        core.getLogger().info("Phase 2: Registering dependent content...");
        registerBlocks(registries);
        registerItems(registries);
        registerEntities(registries);
        
        // 阶段3：注册高级特性
        core.getLogger().info("Phase 3: Registering advanced features...");
        registerRecipes(registries);
        registerStructures(registries);
        registerVillagers(registries);
        
        // 阶段4：设置网络
        core.getLogger().info("Phase 4: Setting up networking...");
        setupNetwork(network);
        
        // 阶段5：注册兼容性模块
        core.getLogger().info("Phase 5: Loading compatibility modules...");
        loadCompatibilityModules(core);
        
        // 绑定所有注册表到事件总线
        registries.registerAll(eventBus);
        
        core.getLogger().info("Initialization complete!");
    }
}
```

### 3.5 NeoForge平台实现

#### PoopSkyNeoforge.java (入口点)

```java
package com.altnoir.poopsky.neoforge;

import com.altnoir.poopsky.api.core.IModCore;
import com.altnoir.poopsky.init.ModInitializer;
import com.altnoir.poopsky.neoforge.core.NeoForgeModCore;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(PoopSkyNeoforge.MOD_ID)
public class PoopSkyNeoforge {
    
    public static final String MOD_ID = "poopsky";
    
    private final IModCore core;
    
    public PoopSkyNeoforge(IEventBus modEventBus, ModContainer container) {
        this.core = new NeoForgeModCore(MOD_ID, container);
        ModInitializer.initialize(core, modEventBus);
    }
    
    public static IModCore getCore() {
        return getInstance().core;
    }
}
```

### 3.6 实施路线图

#### 第1个月：基础设施

- [ ] 定义核心接口（IModCore, IPlatformHelper, IEventBus）
- [ ] 定义注册接口（IRegistryManager及子接口）
- [ ] 实现NeoForge平台核心
- [ ] 迁移最简单的注册类（如PStats）

#### 第2个月：核心系统迁移

- [ ] 实现网络抽象层
- [ ] 迁移PItems, PEffects
- [ ] 迁移PSoundEvents
- [ ] 建立单元测试框架

#### 第3-4个月：复杂系统迁移

- [ ] 实现方块/物品完整抽象
- [ ] 迁移PBlocks（最大的注册类）
- [ ] 迁移实体系统
- [ ] 迁移流体系统

#### 第5个月：高级特性

- [ ] 实现渲染抽象层
- [ ] 迁移客户端代码
- [ ] 迁移世界生成
- [ ] 迁移兼容性模块

#### 第6个月：优化和完善

- [ ] 性能优化
- [ ] 文档完善
- [ ] 多版本测试
- [ ] CI/CD集成

---

## 🔧 阶段四：Gradle多源集多版本构建

### 目标

使用Gradle的多源集（Multi-Source Set）功能，在同一代码库中同时支持多个Minecraft版本。

### 适用场景

- 需要为多个MC版本发布独立jar
- 不同版本有较大API差异，难以用抽象层解决
- 希望利用各版本的新特性

### 4.1 项目结构调整

```
PoopSkyMod/
├── build.gradle                          # 多源集配置
├── gradle.properties                     # 多版本属性
├── settings.gradle
│
├── src/
│   ├── main/                            # 共享代码（所有版本通用）
│   │   ├── java/com/altnoir/poopsky/
│   │   │   ├── api/                     # 抽象接口
│   │   │   ├── common/                  # 业务逻辑
│   │   │   └── config/                  # 配置类
│   │   └── resources/                   # 共享资源
│   │
│   ├── mc_21_1/                         # 1.21.1特有代码
│   │   └── java/com/altnoir/poopsky/
│   │       ├── impl/                    # 1.21.1的实现
│   │       ├── mixin/                   # 1.21.1的Mixin
│   │       └── compat/                  # 1.21.1的兼容层
│   │
│   ├── mc_21_11/                        # 1.21.11特有代码（未来）
│   │   └── java/com/altnoir/poopsky/
│   │       ├── impl/
│   │       ├── mixin/
│   │       └── compat/
│   │
│   └── client/                          # 客户端专用（也可分版本）
│       └── java/com/altnoir/poopsky/client/
│
├── gradle/
│   └── versions/
│       ├── 1.21.1.properties            # 1.21.1版本属性
│       └── 1.21.11.properties           # 1.21.11版本属性
│
└── libs/                                # 本地依赖
```

### 4.2 Gradle配置

#### build.gradle (关键配置)

```groovy
plugins {
    id 'idea'
    id 'java-library'
    id 'maven-publish'
    id 'net.neoforged.moddev' version '2.0.78'
}

// ====== 版本配置 ======
def targetMcVersion = project.findProperty('target_mc_version') ?: '1.21.1'

// 根据目标版本加载不同的属性文件
def versionProps = file("gradle/versions/${targetMcVersion}.properties")
if (versionProps.exists()) {
    def props = new Properties()
    versionProps.withInputStream { props.load(it) }
    props.each { key, value ->
        if (!project.hasProperty(key)) {
            project.ext.set(key, value)
        }
    }
}

// ====== 多源集配置 ======
sourceSets {
    main {
        java {
            srcDirs = ['src/main/java']
        }
        resources {
            srcDirs = ['src/main/resources']
        }
    }
    
    // 为每个支持的MC版本创建源集
    ["21_1", "21_11"].each { version ->
        if (targetMcVersion.replace('.', '_') == version || 
            !project.hasProperty('single_version_build')) {
            
            "mc_${version}" {
                java {
                    srcDirs = ["src/mc_${version}/java"]
                }
                compileClasspath += sourceSets.main.compileClasspath
                runtimeClasspath += sourceSets.main.runtimeClasspath
            }
        }
    }
    
    // 组合最终的源集
    def currentSourceSet = "mc_${targetMcVersion.replace('.', '_')}"
    if (sourceSets.findByName(currentSourceSet)) {
        main {
            java {
                srcDirs += sourceSets."${currentSourceSet}".java.srcDirs
            }
        }
    }
}

// ====== 依赖配置 ======
repositories {
    mavenLocal()
    maven { url = "https://maven.ryanhcode.dev/releases" }
    maven { url = "https://maven.createmod.net" }
    maven { url = "https://mvn.devos.one/snapshots" }
    maven { url = "https://maven.blamejared.com" }
    maven { url = "https://cursemaven.com" }
}

dependencies {
    // 根据版本动态选择依赖
    switch(targetMcVersion) {
        case '1.21.1':
            implementation "net.neoforged:neoforge:${neo_version}"
            compileOnly "mezz.jei:jei-${minecraft_version}-common-api:${jei_version_21_1}"
            compileOnly "mezz.jei:jei-${minecraft_version}-neoforge-api:${jei_version_21_1}"
            implementation "com.simibubi.create:create-${minecraft_version}:${create_version_21_1}:slim"
            break
            
        case '1.21.11':
            implementation "net.neoforged:neoforge:${neo_version}"
            compileOnly "mezz.jei:jei-${minecraft_version}-common-api:${jei_version_21_11}"
            compileOnly "mezz.jei:jei-${minecraft_version}-neoforge-api:${jei_version_21_11}"
            implementation "com.simibubi.create:create-${minecraft_version}:${create_version_21_11}:slim"
            break
    }
}

// ====== 多版本构建任务 ======
task buildAllVersions {
    group = 'build'
    description = 'Build for all supported Minecraft versions'
}

["1.21.1", "1.21.11"].each { version ->
    def taskName = "build${version.replace('.', '_')}"
    
    tasks.register(taskName, GradleBuild) {
        dir = projectDir
        tasks = ['build']
        startParameter.projectProperties = ['target_mc_version': version]
    }
    
    buildAllVersions.dependsOn(taskName)
}

base {
    archivesName = mod_id
    version = "${targetMcVersion}-${mod_version}"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)
```

#### gradle/versions/1.21.1.properties

```properties
# Minecraft 1.21.1 版本特定属性
minecraft_version=1.21.1
minecraft_version_range=[1.21.1, 1.21.2)
neo_version=21.1.219
neo_version_range=[21.1.0,)
loader_version_range=[4,)

# 依赖版本
jei_version_21_1=19.27.0.340
create_version_21_1=6.0.8-168
ponder_version=1.0.64
flywheel_version=1.0.4
registrate_version=MC1.21-1.3.0+62
kubejs_version=2101.7.2-build.295
sable_companion_version=1.6.0
```

#### gradle/versions/1.21.11.properties

```properties
# Minecraft 1.21.11 版本特定属性（示例值，需根据实际情况调整）
minecraft_version=1.21.11
minecraft_version_range=[1.21.11, 1.22.0)
neo_version=21.11.xxx
neo_version_range=[21.11.0,)
loader_version_range=[4,)

# 依赖版本（可能有更新）
jei_version_21_11=19.xx.x.xxx
create_version_21_11=6.x.x-xxx
```

### 4.3 版本特定代码示例

#### src/mc_21_1/java/.../impl/NeoForgeImpl21_1.java

```java
package com.altnoir.poopsky.impl.mc_21_1;

import com.altnoir.poopsky.api.core.IPlatformHelper;
import net.neoforged.fml.ModList;

/**
 * 1.21.1 平台特定实现
 */
public class NeoForgeImpl21_1 implements IPlatformHelper {
    
    @Override
    public String getPlatformName() {
        return "NeoForge-21.1";
    }
    
    @Override
    public boolean isPhysicalClient() {
        return net.neoforged.fml.loading.FMLLoader.getDist().isClient();
    }
    
    @Override
    public boolean isDevelopmentEnvironment() {
        return !net.neoforged.fml.loading.FMLLoader.isProduction();
    }
    
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
```

#### src/mc_21_11/java/.../impl/NeoForgeImpl21_11.java

```java
package com.altnoir.poopsky.impl.mc_21_11;

import com.altnoir.poopsky.api.core.IPlatformHelper;

/**
 * 1.21.11 平台特定实现
 * 
 * 假设1.21.11有一些API变更
 */
public class NeoForgeImpl21_11 implements IPlatformHelper {
    
    @Override
    public String getPlatformName() {
        return "NeoForge-21.11";
    }
    
    @Override
    public boolean isPhysicalClient() {
        // 假设API有变化
        return net.neoforged.fml.loading.FMLEnvironment.dist.isClient();
    }
    
    @Override
    public boolean isDevelopmentEnvironment() {
        return !net.neoforged.fml.loading.FMLEnvironment.production;
    }
    
    @Override
    public boolean isModLoaded(String modId) {
        return net.neoforged.fml.ModList.get().isLoaded(modId);
    }
}
```

### 4.4 Mixin版本管理

**src/main/resources/mixins.poopsky.json** (公共Mixin)

```json
{
  "required": true,
  "minVersion": "0.8.4",
  "package": "com.altnoir.poopsky.mixin.common",
  "compatibilityLevel": "JAVA_21",
  "mixins": [],
  "client": [],
  "server": []
}
```

**src/mc_21_1/resources/mixins.poopsky.21_1.json**

```json
{
  "required": true,
  "parent": "mixins.poopsky.json",
  "package": "com.altnoir.poopsky.mixin.mc_21_1",
  "mixins": [
    "LivingEntityMixin_21_1"
  ],
  "client": [
    "ClientPacketListenerMixin_21_1"
  ]
}
```

### 4.5 构建和测试命令

```bash
# 构建1.21.1版本
./gradlew build -Ptarget_mc_version=1.21.1

# 构建1.21.11版本
./gradlew build -Ptarget_mc_version=1.21.11

# 构建所有版本
./gradlew buildAllVersions

# 运行1.21.1客户端
./gradlew runClient -Ptarget_mc_version=1.21.1

# 运行1.21.11服务端
./gradlew runServer -Ptarget_mc_version=1.21.11

# 为1.21.1生成数据
./gradlew runData -Ptarget_mc_version=1.21.1
```

### 4.6 优缺点分析

#### ✅ 优点

1. **真正的多版本支持**：可以为每个MC版本发布独立的优化jar
2. **利用新特性**：高版本可以使用新版API，无需向下兼容
3. **清晰隔离**：版本间代码完全隔离，不会互相干扰
4. **灵活性强**：可以为不同版本做完全不同的实现

#### ❌ 缺点

1. **代码重复**：如果两个版本差异小，会有大量重复代码
2. **维护成本高**：需要在多个地方同步修复bug
3. **构建复杂**：CI/CD流程更复杂
4. **IDE支持有限**：某些IDE对多源集支持不够好
5. **学习曲线陡峭**：团队成员需要理解复杂的构建系统

### 4.7 适用场景判断

| 场景 | 推荐？ | 原因 |
|------|--------|------|
| 偶尔升级版本 | ❌ | 过度工程化 |
| 维护2个相近版本 | ⚠️ | 考虑阶段三的抽象层 |
| 维护3+个版本 | ✅ | 多源集优势明显 |
| 版本间API差异巨大 | ✅ | 无法用抽象层解决 |
| 需要为旧版本出hotfix | ✅ | 可以独立构建旧版本 |

---

## 🤖 自动化工具与脚本

### 5.1 版本迁移辅助脚本

#### scripts/migrate-version.sh

```bash
#!/bin/bash

set -e  # 遇到错误立即退出

# ===== 配置 =====
OLD_VERSION="${1:?Usage: $0 <old_version> <new_version>}"
NEW_VERSION="${2:?Usage: $0 <old_version> <new_version>}"

echo "=========================================="
echo "PoopSkyMod Version Migration Tool"
echo "From: $OLD_VERSION"
echo "To:   $NEW_VERSION"
echo "=========================================="

# ===== 1. 备份当前状态 =====
BACKUP_DIR="backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo "[1/7] Creating backup in $BACKUP_DIR..."
cp -r src "$BACKUP_DIR/"
cp gradle.properties "$BACKUP_DIR/"

# ===== 2. 更新gradle.properties =====
echo "[2/7] Updating gradle.properties..."

sed -i "s/minecraft_version=$OLD_VERSION/minecraft_version=$NEW_VERSION/" gradle.properties
echo "  Updated minecraft_version to $NEW_VERSION"

# ===== 3. 扫描可能的API使用 =====
echo "[3/7] Scanning for potential API changes..."

grep -rh "import net\.minecraft\." --include="*.java" src/main/java/ | \
    sort | uniq > "$BACKUP_DIR/api_usage_before.txt"

echo "  Found $(wc -l < "$BACKUP_DIR/api_usage_before.txt") unique Minecraft API imports"

# ===== 4. 检查Mixin目标 =====
echo "[4/7] Checking Mixin targets..."

grep -rh "@Mixin" --include="*.java" -A 1 src/main/java/ > "$BACKUP_DIR/mixin_targets.txt"

MIXIN_COUNT=$(grep -c "@Mixin" "$BACKUP_DIR/mixin_targets.txt" 2>/dev/null || echo "0")
echo "  Found $MIXIN_COUNT Mixin annotations"

# ===== 5. 检查依赖兼容性 =====
echo "[5/7] Checking dependency compatibility..."

cat > "$BACKUP_DIR/dependency_checklist.md" << EOF
# Dependency Compatibility Checklist

Please manually verify the following dependencies are compatible with $NEW_VERSION:

- [ ] JEI: Check https://www.curseforge.com/minecraft/mc-mods/jei
- [ ] Create: Check https://www.curseforge.com/minecraft/mods/create
- [ ] Ponder API
- [ ] Flywheel
- [ ] Registrate
- [ ] KubeJS
- [ ] Sable Companion
- [ ] Touhou Little Maid

See also:
- NeoForge changelog
- Minecraft Wiki
EOF

echo "  Created dependency checklist"

# ===== 6. 生成迁移报告 =====
echo "[6/7] Generating migration report..."

cat > "$BACKUP_DIR/migration_report.md" << EOF
# Migration Report: $OLD_VERSION → $NEW_VERSION

## Files to Review

### High Priority (Likely Changes)
- PSNetworking.java - Network API frequently changes
- PComponents.java - Data Components may have updates
- All files in mixin/ - Targets may have moved
- Client renderers - Rendering API evolution

### Medium Priority (Possible Changes)
- PEntityType.java - Entity attributes/AI
- PBlocks.java - Block behavior properties
- PItems.java - Item component system
- World generation code

### Low Priority (Usually Stable)
- Business logic in common/
- Recipe implementations
- Configuration classes

## Steps to Complete Migration

1. Update dependency versions in gradle.properties
2. Run ./gradlew build and fix compilation errors
3. Check Mixin targets with mixin tool
4. Test in-game functionality
5. Update PSVersion.java constants
6. Update this document with actual changes found
EOF

echo "  Report saved to $BACKUP_DIR/migration_report.md"

# ===== 7. 完成 =====
echo ""
echo "[7/7] Migration preparation complete!"
echo ""
echo "Next steps:"
echo "  1. Review $BACKUP_DIR/migration_report.md"
echo "  2. Check dependency versions"
echo "  3. Run: ./gradlew build"
echo "  4. Fix compilation errors iteratively"
echo ""
echo "Backup location: $BACKUP_DIR"
echo "=========================================="
```

#### scripts/check-api-changes.py

```python
#!/usr/bin/env python3
"""
API变更检查工具

比较两个版本的Minecraft API使用情况，
帮助识别潜在的破坏性变更。
"""

import os
import re
from pathlib import Path
from collections import defaultdict
from datetime import datetime

class APIChangeDetector:
    
    # 已知的易变API模式
    UNSTABLE_PATTERNS = [
        r'network\.(payload|packet)',
        r'datacomponent',
        r'renderer?\.',
        r'mixin\s*=\s*\{[^}]*\}',
        r'RegisterPayloadHandlersEvent',
        r'DeferredRegister',
        r'ByteBufCodecs',
        r'StreamCodec',
    ]
    
    def __init__(self, source_dir='src/main/java'):
        self.source_dir = Path(source_dir)
        self.results = defaultdict(list)
    
    def scan(self):
        """扫描所有Java文件"""
        print(f"Scanning {self.source_dir}...")
        
        for java_file in self.source_dir.rglob('*.java'):
            self._analyze_file(java_file)
        
        self._generate_report()
    
    def _analyze_file(self, file_path):
        """分析单个文件"""
        try:
            content = file_path.read_text(encoding='utf-8')
        except Exception as e:
            print(f"Warning: Could not read {file_path}: {e}")
            return
        
        relative_path = file_path.relative_to(self.source_dir)
        
        for pattern in self.UNSTABLE_PATTERNS:
            matches = re.findall(pattern, content, re.IGNORECASE)
            if matches:
                self.results[pattern].append({
                    'file': str(relative_path),
                    'count': len(matches),
                    'lines': self._find_matching_lines(content, pattern)
                })
    
    def _find_matching_lines(self, content, pattern):
        """找到匹配的行号"""
        lines = []
        for i, line in enumerate(content.split('\n'), 1):
            if re.search(pattern, line, re.IGNORECASE):
                lines.append(i)
        return lines[:5]
    
    def _generate_report(self):
        """生成报告"""
        report_file = f'api_scan_{datetime.now().strftime("%Y%m%d_%H%M%S")}.md'
        
        with open(report_file, 'w', encoding='utf-8') as f:
            f.write('# API Change Detection Report\n\n')
            f.write(f'Generated: {datetime.now()}\n\n')
            
            total_issues = sum(len(v) for v in self.results.values())
            f.write(f'Total potential issues: {total_issues}\n\n')
            
            f.write('## Risk Categories\n\n')
            
            for pattern, files in sorted(self.results.items()):
                risk = 'HIGH' if len(files) > 3 else ('MEDIUM' if len(files) > 1 else 'LOW')
                
                f.write(f'### [{risk}] `{pattern}` ({len(files)} files)\n\n')
                
                for file_info in files:
                    f.write(f'- **{file_info["file"]}** ({file_info["count"]} occurrences)\n')
                    f.write(f'  - Lines: {", ".join(map(str, file_info["lines"]))}\n')
                
                f.write('\n')
            
            f.write('## Recommendations\n\n')
            f.write('1. Prioritize fixing HIGH risk items first\n')
            f.write('2. Consider wrapping unstable APIs in adapter classes\n')
            f.write('3. Add unit tests for critical paths\n')
            f.write('4. Monitor upstream changelogs for breaking changes\n')
        
        print(f"\nReport generated: {report_file}")
        print(f"Total potential issues found: {total_issues}")

def main():
    detector = APIChangeDetector()
    detector.scan()

if __name__ == '__main__':
    main()
```

### 5.2 CI/CD自动化

#### .github/workflows/version-test.yml

```yaml
name: Multi-Version Test

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test-matrix:
    strategy:
      matrix:
        version: ['1.21.1', '1.21.11']
      fail-fast: false
    
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
      
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      
      - name: Build version ${{ matrix.version }}
        run: ./gradlew build -Ptarget_mc_version=${{ matrix.version }}
      
      - name: Upload artifacts
        uses: actions/upload-artifact@v4
        with:
          name: poopsky-${{ matrix.version }}
          path: build/libs/*.jar
```

---

## 📝 API变更追踪体系

### 6.1 变更日志模板

创建 `docs/API_CHANGELOG.md`：

```markdown
# API Changelog

追踪PoopSkyMod所依赖的外部API变更历史。

---

## [Unreleased]

### Minecraft 1.21.1 → 1.21.11 (Expected)

#### Possible Breaking Changes

- **Network System**
  - Payload registration API may be refactored
  - Expected impact: HIGH
  
- **Data Components**
  - New component types may be added
  - Expected impact: MEDIUM
  
- **Rendering**
  - Renderer infrastructure updates likely
  - Expected impact: HIGH (client-side only)

- **Mixin Targets**
  - Internal class restructuring probable
  - Expected impact: MEDIUM-HIGH

#### Dependencies to Update

| Dependency | Current Version | Target Version | Status |
|------------|-----------------|----------------|--------|
| NeoForge | 21.1.219 | TBD | Pending |
| JEI | 19.27.0.340 | TBD | Pending |
| Create | 6.0.8-168 | TBD | Pending |
| Ponder | 1.0.64 | TBD | Pending |
| Flywheel | 1.0.4 | TBD | Pending |

#### Files Requiring Review

- [ ] PSNetworking.java
- [ ] PComponents.java
- [ ] All files in mixin/
- [ ] client/ directory
- [ ] compat/ directory

---

## [1.21.1-2.0.1] - Current

### Added
- Initial release on 1.21.1
- Data Components support (new API from 1.20.5)
- NeoForge DeferredRegister pattern
- Network payload system
```

### 6.2 版本差异速查表

创建 `docs/VERSION_DIFF.md`：

```markdown
# Version Difference Quick Reference

快速查找不同版本间的API差异。

---

## 网络系统 (Networking)

| API | 1.21.1 | 1.21.11 | Notes |
|-----|--------|---------|-------|
| Payload注册 | `registrar.playToServer()` | TBD | |
| 编解码器 | `StreamCodec` | TBD | |
| 版本协商 | `.versioned(VERSION)` | TBD | |
| 发包方式 | `PacketDistributor.sendToServer()` | TBD | |

## 数据组件 (Data Components)

| API | 1.21.1 | 1.21.11 | Notes |
|-----|--------|---------|-------|
| 注册方式 | `registerComponentType()` | TBD | |
| 持久化 | `.persistent(CODEC)` | TBD | |
| 网络同步 | `.networkSynchronized(STREAM_CODEC)` | TBD | |

## 注册系统 (Registry)

| API | 1.21.1 | 1.21.11 | Notes |
|-----|--------|---------|-------|
| 方块注册 | `DeferredRegister.createBlocks()` | TBD | |
| 物品注册 | `DeferredRegister.createItems()` | TBD | |
| 实体注册 | `DeferredRegister.create(Registries.ENTITY_TYPE, ...)` | TBD | |

## Mixin目标

| 目标类 | 1.21.1 | 1.21.11 | Notes |
|--------|--------|---------|-------|
| LivingEntity | `net.minecraft.world.entity.LivingEntity` | TBD | |
| ClientPacketListener | `net.minecraft.client.multiplayer.ClientPacketListener` | TBD | |
| CarvedPumpkinBlock | `net.minecraft.world.block.CarvedPumpkinBlock` | TBD | |
```

---

## 📌 最佳实践与注意事项

### 7.1 代码组织原则

1. **依赖倒置原则（DIP）**
   - 高层模块（业务逻辑）不应依赖低层模块（平台API）
   - 两者都应依赖抽象（接口）
   - 抽象不应依赖细节，细节应依赖抽象

2. **最小知识原则（LoD）**
   - 业务代码不应知道底层平台的细节
   - 通过接口/适配器隔离平台知识

3. **单一职责原则（SRP）**
   - 每个注册类只负责一种类型的注册
   - 平台适配器只负责一个平台的适配

### 7.2 版本升级检查清单

每次升级MC版本时，按以下顺序检查：

#### 第一步：环境准备

- [ ] 创建新的Git分支
- [ ] 备份当前代码
- [ ] 更新 `gradle.properties` 中的版本号
- [ ] 更新 `PSVersion.java` 中的常量

#### 第二步：依赖更新

- [ ] 检查NeoForge版本兼容性
- [ ] 更新JEI版本
- [ ] 更新Create版本
- [ ] 更新所有其他依赖
- [ ] 运行 `./gradlew --refresh-dependencies`

#### 第三步：编译修复

- [ ] 运行 `./gradlew build`
- [ ] 修复所有编译错误
- [ ] 检查deprecation警告
- [ ] 更新已废弃的API调用

#### 第四步：Mixin验证

- [ ] 检查所有Mixin目标类是否存在
- [ ] 验证方法签名是否匹配
- [ ] 检查字段名称是否变更
- [ ] 运行Mixin诊断工具

#### 第五步：功能测试

- [ ] 启动客户端，检查崩溃
- [ ] 测试所有方块功能
- [ ] 测试所有物品功能
- [ ] 测试所有实体行为
- [ ] 测试网络通信
- [ ] 测试兼容性模块

#### 第六步：收尾

- [ ] 更新文档
- [ ] 更新API变更日志
- [ ] 合并到主分支
- [ ] 发布新版本

### 7.3 常见陷阱

1. **过度抽象**
   - 不要为不变的API创建抽象层
   - 只对高频变动的部分做抽象
   - 保持抽象层的最小化

2. **忽略二进制兼容性**
   - NeoForge的DeferredRegister在不同版本可能有细微差异
   - 即使编译通过，运行时也可能出错
   - 务必做完整的运行时测试

3. **Mixin脆弱性**
   - Mixin是最容易在版本升级中出问题的部分
   - 尽量减少Mixin的使用
   - 优先使用事件系统替代Mixin
   - 必须使用Mixin时，添加详细的注释说明目标

4. **依赖版本锁定**
   - 始终在 `gradle.properties` 中明确指定依赖版本
   - 避免使用动态版本号（如 `latest`）
   - 定期检查依赖更新

### 7.4 推荐工具

| 工具 | 用途 | 链接 |
|------|------|------|
| **NeoForge Dev Tools** | 官方开发工具 | neoforged.net |
| **Mixin Checker** | Mixin目标验证 | GitHub |
| **CurseForge** | 依赖版本查询 | curseforge.com |
| **Minecraft Wiki** | 版本变更记录 | minecraft.wiki |
| **NeoForge Discord** | 社区支持 | discord.gg |

### 7.5 阶段选择决策树

```
你需要版本迁移吗？
├── 否 → 不需要做任何事
└── 是 → 你需要同时维护多个版本吗？
    ├── 否 → 只用阶段一（轻量封装）
    │        升级时手动修改即可
    └── 是 → 版本间API差异大吗？
        ├── 否 → 阶段二（渐进式抽象层）
        │        用接口隔离变动部分
        └── 是 → 需要同时发布3+个版本吗？
            ├── 否 → 阶段三（完整抽象层）
            │        建立完整的版本无关架构
            └── 是 → 阶段四（Gradle多源集）
                     每个版本独立源集和构建
```

---

## 📚 附录

### A. 现有注册类完整清单

| 类名 | 注册类型 | 行数(估) | 变动频率 |
|------|---------|---------|---------|
| PBlocks | Block | ~550 | 低 |
| PItems | Item | ~130 | 低 |
| PEntityType | EntityType | ~60 | 中 |
| PEffects | MobEffect | ~58 | 低 |
| PSoundEvents | SoundEvent | ~62 | 低 |
| PBlockEntityType | BlockEntityType | ~44 | 中 |
| PComponents | DataComponentType | ~39 | 高 |
| PSNetworking | Network Payload | ~38 | 高 |
| PFluids | Fluid | ~30 | 中 |
| PFluidTypes | FluidType | ~20 | 中 |
| PMenuTypes | MenuType | ~20 | 低 |
| PParticles | ParticleType | ~15 | 低 |
| PPotions | Potion | ~15 | 低 |
| PLootFunctions | LootItemFunctionType | ~15 | 低 |
| PVillagers | VillagerProfession | ~15 | 中 |
| PRecipes | RecipeType | ~15 | 低 |
| PItemGroups | CreativeModeTab | ~30 | 低 |
| PStats | StatType | ~15 | 低 |
| PBlockSetType | BlockSetType | ~10 | 低 |
| PWoodType | WoodType | ~10 | 低 |
| PDamageTypes | DamageType | ~10 | 低 |
| PToiletTypes | (自定义) | ~10 | 低 |

### B. Mixin清单

| Mixin类 | 目标类 | 用途 |
|---------|--------|------|
| LivingEntityMixin | LivingEntity | 效果相关逻辑 |
| ClientPacketListenerMixin | ClientPacketListener | 网络包处理 |
| CarvedPumpkinBlockMixin | CarvedPumpkinBlock | 南瓜生成逻辑 |
| VillagerMixin | Villager | 村民行为 |
| TradeWithVillagerMixin | (交易相关) | 交易逻辑 |
| FishingHookMixin | FishingHook | 钓鱼逻辑 |
| BaseCoralPlantTypeBlockMixin | BaseCoralPlantTypeBlock | 珊瑚方块 |
| NoiseBasedChunkGeneratorMixin | NoiseBasedChunkGenerator | 世界生成 |
| WorldCreationUiStateMixin | (创建世界UI) | UI修改 |
| CreateWorldScreenWorldTabMixin | CreateWorldScreen | 世界创建 |

### C. 网络包清单

| Payload类 | 方向 | 用途 |
|-----------|------|------|
| PlugActionPayload | C→S | 马桶塞动作 |
| PlugDismountPayload | C→S | 下马动作 |
| PlugInputPayload | C→S | 马桶塞输入 |
| TimeBellFreezePayload | S→C | 时间钟冻结效果 |

### D. 兼容性模块清单

| 模块 | 目标Mod | 功能 |
|------|---------|------|
| PSJEIPlugin | JEI | 配方显示 |
| MaidPlugin | Touhou Little Maid | 女仆厕所行为 |
| CreatePlugin | Create | 风扇消化配方 |

---

> **最后更新**: 2026-07-07
> **适用版本**: Minecraft 1.21.1 + NeoForge 21.1.219
> **文档版本**: 1.0