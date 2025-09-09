# Forge 集成与注册系统迁移指南

## 1. 主类迁移 (Mill.java)

### 原版本结构 (1.12.2)

**文件**: `org.millenaire.common.forge.Mill.java`

```java
@Mod(modid = "millenaire", name = "Millénaire", version = "Millénaire 8.1.1", useMetadata = true)
public class Mill {
    public static final String MODID = "millenaire";
    public static final String VERSION = "Millénaire 8.1.1";
    public static final String MINECRAFT_VERSION_NUMBER = "1.12.2";

    @SidedProxy(clientSide = "org.millenaire.client.forge.ClientProxy",
                serverSide = "org.millenaire.common.forge.CommonProxy")
    public static CommonProxy proxy;

    @Instance
    public static Mill instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { /* ... */ }

    @EventHandler
    public void init(FMLInitializationEvent e) { /* ... */ }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) { /* ... */ }
}
```

### 1.20.1 迁移要点

#### A. Mod 注解更新

```java
// 1.12.2
@Mod(modid = "millenaire", name = "Millénaire", version = "Millénaire 8.1.1", useMetadata = true)

// 1.20.1 (需要更新)
@Mod("millenaire") // 简化注解
```

#### B. 事件处理更新

```java
// 1.12.2
@EventHandler
public void preInit(FMLPreInitializationEvent event) { }

// 1.20.1 (需要替换为总线事件)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public static class ModEvents {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) { }
}
```

#### C. Proxy 系统移除

```java
// 1.12.2 Proxy系统 (已移除)
@SidedProxy(clientSide = "...", serverSide = "...")
public static CommonProxy proxy;

// 1.20.1 (使用DistExecutor)
public static void clientSetup() {
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientProxy::init);
}
```

## 2. 注册系统迁移

### A. 方块注册 (MillBlocks.java)

#### 原版本 (1.12.2)

```java
@ObjectHolder("millenaire:wood_deco")
public static BlockDecorativeWood WOOD_DECORATION;

public static void registerBlocks(RegistryEvent.Register<Block> event) {
    // 手动注册每个方块
}
```

#### 迁移到 1.20.1

```java
// 使用DeferredRegister
public static final DeferredRegister<Block> BLOCKS =
    DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

public static final RegistryObject<Block> WOOD_DECORATION =
    BLOCKS.register("wood_deco", () -> new BlockDecorativeWood(...));

// 在主类构造函数中注册
public Mill() {
    BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
}
```

### B. 物品注册 (MillItems.java)

#### 原版本 (1.12.2)

```java
@ObjectHolder("millenaire:denier")
public static Item DENIER;

public static void registerItems(RegistryEvent.Register<Item> event) {
    // 手动注册
}
```

#### 迁移到 1.20.1

```java
public static final DeferredRegister<Item> ITEMS =
    DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

public static final RegistryObject<Item> DENIER =
    ITEMS.register("denier", () -> new Item(new Item.Properties()));
```

### C. 实体注册

#### 原版本 (1.12.2)

```java
EntityRegistry.registerModEntity(new ResourceLocation("millenaire", "villager"),
    MillVillager.class, "villager", 1, Mill.instance, 80, 1, true);
```

#### 迁移到 1.20.1

```java
public static final DeferredRegister<EntityType<?>> ENTITIES =
    DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

public static final RegistryObject<EntityType<MillVillager>> VILLAGER =
    ENTITIES.register("villager", () -> EntityType.Builder.of(MillVillager::new,
        MobCategory.CREATURE).sized(0.6F, 1.95F).build("villager"));
```

## 3. TileEntity 注册迁移

### 原版本 (1.12.2)

```java
GameRegistry.registerTileEntity(TileEntityLockedChest.class, "LockedChest");
```

### 迁移到 1.20.1

```java
public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
    DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

public static final RegistryObject<BlockEntityType<TileEntityLockedChest>> LOCKED_CHEST =
    BLOCK_ENTITIES.register("locked_chest", () ->
        BlockEntityType.Builder.of(TileEntityLockedChest::new,
            LOCKED_CHEST_BLOCK.get()).build(null));
```

## 4. 客户端注册 (ClientProxy 迁移)

### 原版本结构

**文件**: `org.millenaire.client.forge.ClientProxy.java`

#### 主要功能

- 模型注册
- 渲染注册
- 按键绑定
- GUI 处理器

### 迁移要点

#### A. 模型注册更新

```java
// 1.12.2
ModelLoader.setCustomModelResourceLocation(item, 0,
    new ModelResourceLocation(item.getRegistryName(), "inventory"));

// 1.20.1 (在ModelRegistryEvent中)
@SubscribeEvent
public static void registerModels(ModelEvent.RegisterAdditional event) {
    // 使用新的模型注册系统
}
```

#### B. 渲染注册更新

```java
// 1.12.2
RenderingRegistry.registerEntityRenderingHandler(MillVillager.class,
    RenderMillVillager::new);

// 1.20.1
@SubscribeEvent
public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(VILLAGER.get(), RenderMillVillager::new);
}
```

## 5. 网络系统迁移

### 原版本 (1.12.2)

```java
public static FMLEventChannel millChannel =
    NetworkRegistry.INSTANCE.newEventDrivenChannel("millenaire");
```

### 迁移到 1.20.1

```java
public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
    new ResourceLocation(MODID, "main"),
    () -> "1.0",
    "1.0"::equals,
    "1.0"::equals
);

// 消息注册
public static void registerMessages() {
    int id = 0;
    NETWORK.registerMessage(id++, ServerMessage.class,
        ServerMessage::encode, ServerMessage::decode, ServerMessage::handle);
}
```

## 6. 配置系统迁移

### 原版本使用自定义配置

**文件**: `org.millenaire.common.config.MillConfigValues.java`

### 迁移建议

- 保持现有配置逻辑
- 适配新的文件系统 API
- 考虑使用 Forge 的配置系统

## 7. 世界生成器注册

### 原版本 (1.12.2)

```java
GameRegistry.registerWorldGenerator(new WorldGenVillage(), 1000);
```

### 迁移到 1.20.1

```java
// 需要重构为新的世界生成系统
// 使用Biome Modifiers或Feature系统
```

## 8. 命令系统迁移

### 原版本 (1.12.2)

```java
@EventHandler
public void serverLoad(FMLServerStartingEvent event) {
    event.registerServerCommand(new CommandSpawnVillage());
}
```

### 迁移到 1.20.1

```java
@SubscribeEvent
public static void registerCommands(RegisterCommandsEvent event) {
    CommandSpawnVillage.register(event.getDispatcher());
}
```

## 迁移检查清单

### 必须迁移的组件

- [ ] 主 Mod 类注解和结构
- [ ] 方块注册系统
- [ ] 物品注册系统
- [ ] 实体注册系统
- [ ] TileEntity 注册系统
- [ ] 客户端渲染注册
- [ ] 网络消息系统
- [ ] 命令注册系统
- [ ] 世界生成器注册

### 可选优化项

- [ ] 使用新的配置系统
- [ ] 优化数据包系统
- [ ] 使用新的标签系统
- [ ] 代码现代化重构

## 预估工作量

- **基础迁移**: 2-3 周
- **测试调试**: 1-2 周
- **优化完善**: 1 周

## 风险评估

- **高风险**: 世界生成系统变更
- **中风险**: 网络系统 API 变更
- **低风险**: 基础注册系统迁移
