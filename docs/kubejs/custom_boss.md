# KubeJS 自定义弹幕 Boss

在 `kubejs/server_scripts/` 中创建脚本，并通过 `PoopSkyServerEvents.customBoss` 注册：

```js
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

使用原生参数的示例：

```js
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
