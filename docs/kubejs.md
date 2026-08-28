# 概述

PoopSky 提供了与 KubeJS 的深度集成功能，允许玩家通过 JavaScript 脚本自定义苍蝇品种和配方。

本指南将介绍：

- 如何使用 `PoopSkyStartupEvents.flyType` 注册自定义 `FlyType`
- 如何使用 `PoopSkyServerEvents.flyType` 添加自定义 `Fly` 配方
- 如何使用 `PoopSkyServerEvents.customBoss` 添加自定义弹幕游戏Boss
- 如何使用 `event.recipes.poopsky.*()` 添加 PoopSky 自定义配方
- 如何使用`Kuebejs`自带的`tags`事件来添加/移除扭蛋生物

## 文件位置

使用 `.js` 扩展名的 JavaScript 文件。

FlyType 注册放在：

```text
kubejs/startup_scripts/
```

# 自定义 FlyType

PoopSky 的 `FlyType` 不是 Minecraft Registry，而是由 `FlyTypeManager` 管理的苍蝇品种列表。

注册方式：

```javascript
PoopSkyStartupEvents.flytype(event => {
    event.register('apple')
        .name('Apple Fly')
})
```

注册后会自动：

- 加入 `FlyTypeManager`
- 加入 PoopSky 创造模式生物分组
- 自动生成 `fly_<id>.json` 物品模型
- 自动生成 `fly_type.poopsky.<id>` 翻译

## 可用配置方法

| 方法名 | 参数 | 必须 | 描述 | 默认值 |
|--------|------|------|------|--------|
| `.name()` | 字符串 | ❌ | 设置苍蝇显示名称 | 自动从 ID 生成 |
| `.texture()` | 资源路径，例如 `'minecraft:item/apple'` | ❌ | 设置苍蝇物品模型贴图 | `poopsky:item/fly_<id>` |

### 贴图自定义

```javascript
PoopSkyStartupEvents.flytype(event => {
    event.register('apple')
        .name('Apple Fly')
        .texture('minecraft:item/apple') // 原版的苹果
})
```

如果使用默认贴图路径 `poopsky:item/fly_<id>`，可以把贴图放在：

```text
kubejs/assets/poopsky/textures/item/fly_apple.png
```


FlyType 配方放在：

```text
kubejs/server_scripts/
```

配方类脚本修改后可以通过 `/reload` 热重载。

| 方法名 | 参数 | 必须 | 描述 | 默认值 |
|--------|------|------|------|--------|
| `.flyBarrel()` | 物品 ID，可选数量 | ❌ | 添加苍蝇桶产出配方 | 无 |
| `.breeding()` | 两个父本品种，可选概率 | ❌ | 添加繁育箱变异配方 | `0.2` |

### 添加苍蝇桶配方

```javascript
PoopSkyServerEvents.flyType(event => {
    event.register('apple')
        .flyBarrel('minecraft:apple', 2)
})
```

含义：`apple` 苍蝇放入苍蝇桶后，产出 `2` 个苹果。

### 添加繁育配方

```javascript
PoopSkyServerEvents.flyType(event => {
    event.register('apple')
        .breeding('red', 'orange', 0.35)
})
```

含义：`red + orange` 有 `35%` 概率变异成当前注册的 `apple`。

不填写概率时，默认概率为 `20%`：

```javascript
PoopSkyServerEvents.flyType(event => {
    event.register('apple')
        .breeding('red', 'orange')
})
```

### 完整示例

```text
kubejs/startup_scripts/
```
```javascript
PoopSkyServerEvents.flyType(event => {
    event.register('apple')
        .name('Apple Fly')
        .texture('minecraft:item/apple')
})
```

```text
kubejs/server_scripts/
```
```javascript
PoopSkyServerEvents.flyType(event => {
    event.register('apple')
        .flyBarrel('minecraft:apple', 2)
        .breeding('red', 'orange', 0.35)
        .breeding('white', 'orange', 0.5)
})
```

# 自定义弹幕游戏Boss

弹幕boss注册放在：

```text
kubejs/server_scripts/
```

PoopSky 为 KubeJS 添加了弹幕游戏Boss的自定义方法。

## 示例
```javascript
PoopSkyServerEvents.customBoss(event => {
  event.register('spiral_guardian')
    .baseHp(120)
    .bulletCount(18)
    .maxBounces(1)
    .attackInterval(12)
    .bulletSpeed(2.4)
    .weight(3)
    .minWave(2)
    .loot('minecraft:apple')
    .tick(ctx => {
      if (ctx.age % 12 === 0) {
        ctx.spawnCircle(18, 2.4, 1, ctx.rotation)
      }

      ctx.moveToPlayerX(0.35)
      ctx.rotation += 4
    })
})
```

- `baseHp(value)`：基础血量，默认 `80`，仍会叠加每波 `+5` 的原有成长。
- `bulletCount(value)`、`maxBounces(value)`、`attackInterval(value)`、`bulletSpeed(value)`、`rotation(value)`、`angleStep(value)`：与 `BossModifierTemplate` 对应。每项都支持 `min, max` 两个参数，生成 Boss 时会在区间中随机确定本局数值。
- `circularRotation()` 或 `circularRotation(startDelay, duration)`：配置环形旋转元数据；不能与 `rotation(...)` 同时使用。
- `movement(type, amplitude, speed)`：配置原生 Boss 移动，`type` 可为 `left_right`、`orbit` 或 `random`。搭配 `movementWave(wave[, randomMovement])` 指定启用波次。
- `weight(value)`：随机权重，默认 `1`；原生五种 Boss 各占 `1` 权重。
- `minWave(value)`：允许出现的最早波次，使用玩家看到的波次编号，默认第 `1` 波。
- `loot(itemId)`：击杀这个 Boss 时立即从街机正面喷出的物品。可重复调用以添加多个掉落；不会计入街机奖励或保存到方块数据。
- `tick(callback)`：每个 Boss 每游戏 tick 执行一次。
- `rawTick(callback)`：原生三参数回调，签名为 `(boss, state, random) => {}`。它与 `tick(...)` 只能二选一，适合需要直接调用 `Boss`、`TouhouGameState` 或 `Random` 方法的高级脚本。

`ctx` 可读取 `age`、`wave`、`rotation`、`bossX`、`bossY`、`playerX`、`playerY`；其中 `rotation` 可直接写回。已生成的修饰器值可通过 `baseHp`、`bulletCount`、`maxBounces`、`attackInterval`、`bulletSpeed`、`modifierRotation`、`angleStep`、`movementWave` 读取。可调用：

- `spawnBullet(vx, vy, maxBounces)`
- `spawnCircle(count, speed, maxBounces, angleOffsetDegrees)`
- `spawnArc(count, speed, maxBounces, centerAngleDegrees, spreadDegrees)`
- `moveBy(x, y)`
- `setBossPosition(x, y)`
- `moveToPlayerX(speed)`

脚本仅在服务端运行。tick 回调抛出异常时，该 Boss 会停止执行脚本并写入服务端日志。

## 使用原生参数的示例

```javascript
PoopSkyServerEvents.customBoss(event => {
  event.register('raw_circle')
    .rawTick((boss, state, random) => {
      if (random.nextInt(12) === 0) {
        let modifiers = boss.modifiers()
        state.spawnCircle(
          state.getBossCenterX(),
          state.getBossCenterY(),
          modifiers.bulletCount(),
          modifiers.bulletSpeed(),
          modifiers.maxBounces(),
          0
        )
      }
    })
})
```

# 自定义模组配方

PoopSky 为 KubeJS 注册了 `poopsky` 配方命名空间。

所有配方通过以下格式添加：

```javascript
ServerEvents.recipes(event => {
    event.recipes.poopsky.xxx()
})
```

### Sieve

筛网配方：

```javascript
event.recipes.poopsky.sieve(
    'minecraft:gravel',
    [
        { item: 'minecraft:iron_ingot', chance: 0.5 },
        { item: 'minecraft:flint', chance: 0.25 }
    ],
    100
)
```

第三个参数 `processingTime` 可省略，默认 `200`。

### FlyBarrel

苍蝇桶配方：

```javascript
event.recipes.poopsky.fly_barrel(
    'red',
    Item.of('minecraft:redstone', 2)
)
```

第一个参数是苍蝇品种 ID，第二个参数是产出物品。

### BreedingChest

繁育箱配方：

```javascript
event.recipes.poopsky.breeding_chest(
    'red',
    'blue',
    'purple',
    0.2
)
```

最后一个参数概率可省略，默认 `0.2`。

### PopExplosion

PoopTNT 爆炸转化配方：

```javascript
event.recipes.poopsky.pop_explosion(
    'minecraft:cobblestone',
    'minecraft:gravel',
    1
)
```

输出可以是方块，也可以是物品。

### AnalPressing

肛气冲压转化配方：

```javascript
event.recipes.poopsky.anal_pressing(
    'poopsky:poop_block',
    'minecraft:stone',
    'minecraft:cobblestone',
    1
)
```

参数顺序为：`input`、`output`、`replaceTarget`、`radius`。

### Compooper

堆肥配方：

```javascript
event.recipes.poopsky.compooper(
    'water',
    'minecraft:stone',
    'minecraft:cobblestone'
)
```

参数顺序为：`fluidType`、`input`、`output`。

## 完整示例

```javascript
ServerEvents.recipes(event => {
    let poopsky = event.recipes.poopsky;
    poopsky.sieve(
        'minecraft:gravel',
        [
            { item: 'minecraft:iron_ingot', chance: 0.5 }
        ],
        100
    )

    poopsky.fly_barrel(
        'red',
        Item.of('minecraft:redstone', 2)
    )

    poopsky.breeding_chest(
        'red',
        'blue',
        'purple',
        0.2
    )

    poopsky.pop_explosion(
        'minecraft:cobblestone',
        'minecraft:gravel',
        1
    )

    poopsky.anal_pressing(
        'poopsky:poop_block',
        'minecraft:stone',
        'minecraft:cobblestone',
        1
    )

    poopsky.compooper(
        'water',
        'minecraft:stone',
        'minecraft:cobblestone'
    )
})
```

# 添加/移除扭蛋生物

扭蛋生物是通过`poopsky:gachapon_mob`生物标签来确定的，可用kjs自带的tags事件来处理添加和移除。
```
ServerEvents.tags('entity_type', event => {
    // 示例：把 creeper 加入 poopsky:gachapon_mob 标签
    event.add('poopsky:gachapon_mob', 'minecraft:creeper')

    // 批量添加多个实体
    event.add('poopsky:gachapon_mob', [
        'minecraft:zombie',
        'minecraft:skeleton'
    ])

    // 移除某个实体标签
    event.remove('poopsky:gachapon_mob', 'minecraft:pig')
})
```

## 热重载

FlyType 注册和配方都放在 `server_scripts`，因此修改后可以执行：

```text
/reload
```

注意：

- FlyType ID、显示名、苍蝇桶配方、繁育配方会随服务端脚本重载。
- `event.recipes.poopsky.*()` 配方也会随 KubeJS 服务端脚本重载。
- 即使不使用 KubeJS，数据包中的 `poopsky_data/fly_types.json` 也会随 `/reload` 重新加载。
- 客户端模型和贴图属于客户端资源，如果只看到 ID 更新但模型没变，需要执行 `F3+T` 重载客户端资源，或重启游戏。

## 注意事项

- `toilet_shaped` 当前未提供 `event.recipes.poopsky` 兼容。
- 自定义 FlyType 不需要额外添加装备标签；它注册后会自动进入创造模式、苍蝇桶和繁育箱逻辑。
- 如果注册的 ID 已经存在于内置 `FlyType` 中，会直接报错，请使用新的 ID。
