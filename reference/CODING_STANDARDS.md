# Millenaire Rewrite 代码开发规范与最佳实践指南

## 项目概述

- **项目名称**: Millenaire Rewrite
- **目标版本**: Minecraft 1.20.1, Forge 47.4.3
- **模组ID**: `millenaire_rewrite`
- **基础包**: `com.jasoncian.millenaire_rewrite`
- **项目性质**: 从1.12.2版本完全重写的大型村庄模拟模组

---

## 1. 项目结构与包组织

### 1.1 基础包结构

所有代码必须放置在基础包 `com.jasoncian.millenaire_rewrite` 下，采用**基于功能的包组织**而非传统的层次结构。

```
com.jasoncian.millenaire_rewrite/
├── MillenaireRewriteMod.java                    # 主模组类
├── api/                                         # 公共API接口
│   ├── culture/                                 # 文化系统API
│   ├── village/                                 # 村庄系统API
│   └── economy/                                 # 经济系统API
├── block/                                       # 方块相关
│   ├── entity/                                  # 方块实体
│   └── properties/                              # 方块属性
├── item/                                        # 物品相关
│   ├── tool/                                    # 工具类物品
│   ├── armor/                                   # 盔甲类物品
│   └── food/                                    # 食物类物品
├── entity/                                      # 实体相关
│   ├── villager/                                # 村民实体
│   ├── animal/                                  # 动物实体
│   └── ai/                                      # AI系统
├── village/                                     # 村庄系统
│   ├── data/                                    # 村庄数据模型
│   ├── building/                                # 建筑系统
│   ├── economy/                                 # 村庄经济
│   └── generation/                              # 村庄生成
├── culture/                                     # 文化系统
│   ├── data/                                    # 文化数据
│   ├── building/                                # 文化建筑
│   └── content/                                 # 文化内容
├── world/                                       # 世界生成相关
│   ├── structure/                               # 结构生成
│   ├── biome/                                   # 生物群系
│   └── feature/                                 # 世界特征
├── network/                                     # 网络通信
│   ├── packet/                                  # 数据包
│   └── handler/                                 # 处理器
├── client/                                      # 客户端专用代码
│   ├── gui/                                     # 图形界面
│   ├── renderer/                                # 渲染器
│   ├── model/                                   # 模型
│   └── event/                                   # 客户端事件
├── server/                                      # 服务端专用代码
│   ├── command/                                 # 命令系统
│   └── event/                                   # 服务端事件
├── data/                                        # 数据生成
│   ├── provider/                                # 数据提供器
│   ├── recipe/                                  # 配方生成
│   └── tag/                                     # 标签生成
├── config/                                      # 配置系统
├── util/                                        # 工具类
└── integration/                                 # 第三方模组集成
    ├── jei/                                     # JEI集成
    └── curios/                                  # Curios集成
```

### 1.2 资源目录结构

```
src/main/resources/
├── META-INF/
│   └── mods.toml                                # 模组元数据
├── assets/millenaire_rewrite/                   # 客户端资源
│   ├── blockstates/                             # 方块状态JSON
│   ├── models/                                  # 模型文件
│   │   ├── block/                               # 方块模型
│   │   └── item/                                # 物品模型
│   ├── textures/                                # 贴图文件
│   │   ├── block/                               # 方块贴图
│   │   ├── item/                                # 物品贴图
│   │   ├── entity/                              # 实体贴图
│   │   └── gui/                                 # GUI贴图
│   ├── lang/                                    # 语言文件
│   └── sounds.json                              # 音效定义
├── data/millenaire_rewrite/                     # 数据文件
│   ├── recipes/                                 # 配方
│   ├── loot_tables/                             # 战利品表
│   ├── tags/                                    # 标签
│   ├── advancements/                            # 进度
│   └── structures/                              # 结构文件
└── cultures/                                    # 文化数据（自定义）
    ├── norman/
    ├── japanese/
    └── byzantine/
```

### 1.3 包命名规则

- **核心功能包**: 直接以功能命名，如 `village`, `culture`, `entity`
- **API包**: 必须包含 `.api` 子包，如 `village.api`
- **数据相关**: 使用 `.data` 子包，如 `village.data`
- **客户端专用**: 使用 `.client` 前缀，如 `.client.renderer`
- **服务端专用**: 使用 `.server` 前缀，如 `.server.command`
- **网络相关**: 使用 `.network` 包，如 `.network.packet`
- **集成包**: 使用 `.integration.{modid}` 格式

---

## 2. Java编码规范

### 2.1 语言级别与现代Java特性

**要求**: 使用JDK 17语法特性，在合适的地方采用现代Java语法。

#### 2.1.1 Record类使用

优先使用Record定义不可变数据类：

```java
// ✅ 推荐：使用Record定义文化配置
public record CultureConfig(
    String cultureName,
    ResourceLocation textureLocation,
    int basePopulation,
    List<String> buildingTypes
) {
    public CultureConfig {
        Objects.requireNonNull(cultureName, "Culture name cannot be null");
        Objects.requireNonNull(textureLocation, "Texture location cannot be null");
        if (basePopulation < 0) {
            throw new IllegalArgumentException("Base population must be non-negative");
        }
    }
}

// ❌ 避免：传统的不可变类
public final class CultureConfigOld {
    private final String cultureName;
    private final ResourceLocation textureLocation;
    // ... 大量样板代码
}
```

#### 2.1.2 Sealed Classes

对于有限的类型层次结构，使用sealed classes：

```java
// ✅ 推荐：封闭类定义村民职业类型
public sealed abstract class VillagerProfession
    permits Farmer, Blacksmith, Merchant, Builder {
    
    public abstract ResourceLocation getTexture();
    public abstract List<Item> getRequiredTools();
}

public final class Farmer extends VillagerProfession {
    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(MillenaireRewriteMod.MOD_ID, "textures/entity/villager/farmer.png");
    }
    
    @Override
    public List<Item> getRequiredTools() {
        return List.of(Items.IRON_HOE, Items.WOODEN_SHOVEL);
    }
}
```

#### 2.1.3 Pattern Matching

使用pattern matching简化instanceof检查：

```java
// ✅ 推荐：使用pattern matching
public void handleVillagerAction(VillagerAction action) {
    switch (action) {
        case WorkAction work -> handleWork(work.getWorkType(), work.getDuration());
        case SleepAction sleep -> handleSleep(sleep.getBedLocation());
        case TradeAction trade -> handleTrade(trade.getCustomer(), trade.getItems());
        default -> throw new IllegalArgumentException("Unknown action type: " + action);
    }
}

// ❌ 避免：传统的instanceof链
public void handleVillagerActionOld(VillagerAction action) {
    if (action instanceof WorkAction) {
        WorkAction work = (WorkAction) action;
        handleWork(work.getWorkType(), work.getDuration());
    } else if (action instanceof SleepAction) {
        // ...
    }
}
```

### 2.2 命名约定

#### 2.2.1 基本命名规则

- **类名**: `PascalCase` - `NormanVillager`, `CultureManager`
- **方法名/变量名**: `camelCase` - `getCultureConfig`, `villagerCount`
- **常量**: `UPPER_SNAKE_CASE` - `MAX_VILLAGE_SIZE`, `DEFAULT_CULTURE_ID`
- **包名**: `lowercase` - `village`, `culture`, `network`

#### 2.2.2 Forge注册项命名

**重要**: 所有注册对象的注册名必须使用 `snake_case`，并与代码中的标识符保持一致：

```java
// ✅ 正确的注册命名
public static final RegistryObject<Item> NORMAN_VILLAGER_SPAWN_EGG = ITEMS.register(
    "norman_villager_spawn_egg",  // 注册名：snake_case
    () -> new ForgeSpawnEggItem(/* ... */)
);

public static final RegistryObject<Block> CULTURE_TOTEM_BLOCK = BLOCKS.register(
    "culture_totem_block",        // 注册名：snake_case
    () -> new CultureTotemBlock(/* ... */)
);

public static final RegistryObject<EntityType<NormanVillager>> NORMAN_VILLAGER = ENTITIES.register(
    "norman_villager",            // 注册名：snake_case
    () -> EntityType.Builder.of(NormanVillager::new, MobCategory.CREATURE)
        .sized(0.6F, 1.95F)
        .build(new ResourceLocation(MillenaireRewriteMod.MOD_ID, "norman_villager").toString())
);
```

#### 2.2.3 ResourceLocation命名

```java
// ✅ 推荐：统一的ResourceLocation创建
public static final ResourceLocation NORMAN_CULTURE_ID = 
    new ResourceLocation(MillenaireRewriteMod.MOD_ID, "norman");

public static final ResourceLocation VILLAGE_DATA_SYNC_PACKET = 
    new ResourceLocation(MillenaireRewriteMod.MOD_ID, "village_data_sync");

// ✅ 推荐：使用工具方法
public static ResourceLocation modLoc(String path) {
    return new ResourceLocation(MillenaireRewriteMod.MOD_ID, path);
}

// 使用示例
public static final ResourceLocation JAPANESE_CULTURE_ID = modLoc("japanese");
```

### 2.3 访问权限控制

#### 2.3.1 访问修饰符优先级

遵循最小权限原则：`private` > `package-private` > `protected` > `public`

```java
// ✅ 推荐：严格的权限控制
public class CultureManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<ResourceLocation, Culture> cultures = new ConcurrentHashMap<>();
    
    // 包内可见的工厂方法
    static CultureManager create() {
        return new CultureManager();
    }
    
    // 受保护的扩展点
    protected void onCultureRegistered(Culture culture) {
        LOGGER.debug("Culture registered: {}", culture.getId());
    }
    
    // 公共API
    public Optional<Culture> getCulture(ResourceLocation id) {
        return Optional.ofNullable(cultures.get(id));
    }
    
    // 私有实现细节
    private void validateCulture(Culture culture) {
        Objects.requireNonNull(culture.getId(), "Culture ID cannot be null");
    }
}
```

#### 2.3.2 API注解使用

对于公共API，使用适当的注解标明使用意图：

```java
import org.jetbrains.annotations.ApiStatus;

// ✅ 推荐：明确标记API状态
@ApiStatus.Experimental
public interface VillageAI {
    /**
     * 实验性API - 可能在未来版本中更改
     */
    void updateAI(Level level, Village village);
}

@ApiStatus.Internal
public class VillageDataSerializer {
    /**
     * 内部使用 - 不应被外部代码调用
     */
    public static CompoundTag serialize(VillageData data) {
        // ...
    }
}

// 稳定的公共API
public final class CultureRegistry {
    /**
     * 注册新文化到系统中
     * @param culture 要注册的文化
     * @throws IllegalArgumentException 如果文化ID已存在
     */
    public static void register(Culture culture) {
        // ...
    }
}
```

### 2.4 异常处理

#### 2.4.1 异常处理原则

**禁止**: 捕获异常后仅打印堆栈跟踪
**要求**: 提供适当的日志和恢复逻辑

```java
// ❌ 禁止：仅打印异常
try {
    loadVillageData(file);
} catch (IOException e) {
    e.printStackTrace(); // 绝对禁止
}

// ✅ 推荐：适当的异常处理
public Optional<VillageData> loadVillageData(Path file) {
    try {
        return Optional.of(parseVillageData(file));
    } catch (IOException e) {
        LOGGER.error("Failed to load village data from file: {}", file, e);
        return Optional.empty();
    } catch (JsonParseException e) {
        LOGGER.warn("Invalid village data format in file: {}, using default values", file, e);
        return Optional.of(createDefaultVillageData());
    }
}

// ✅ 推荐：将检查异常转换为运行时异常（适当的场景）
public Culture loadCulture(ResourceLocation id) {
    try {
        return loadCultureFromFile(id);
    } catch (IOException e) {
        throw new CultureLoadException("Failed to load culture: " + id, e);
    }
}
```

#### 2.4.2 自定义异常

为模组特定的错误情况创建自定义异常：

```java
// 基础异常类
public class MillenaireException extends RuntimeException {
    public MillenaireException(String message) {
        super(message);
    }
    
    public MillenaireException(String message, Throwable cause) {
        super(message, cause);
    }
}

// 具体异常类型
public class CultureLoadException extends MillenaireException {
    public CultureLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class VillageGenerationException extends MillenaireException {
    private final BlockPos attemptedPosition;
    
    public VillageGenerationException(String message, BlockPos position) {
        super(message);
        this.attemptedPosition = position;
    }
    
    public BlockPos getAttemptedPosition() {
        return attemptedPosition;
    }
}
```

### 2.5 日志系统

#### 2.5.1 日志记录器设置

**要求**: 统一使用SLF4J Logger，每个类都应有自己的logger实例

```java
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public class VillageManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // 或者使用类特定的logger（对于复杂类推荐）
    private static final Logger LOGGER = LogUtils.getLogger(VillageManager.class);
    
    public void createVillage(BlockPos pos, Culture culture) {
        LOGGER.info("Creating village at {} with culture {}", pos, culture.getId());
        
        try {
            // 村庄创建逻辑
            LOGGER.debug("Village creation successful");
        } catch (Exception e) {
            LOGGER.error("Failed to create village at {} with culture {}", pos, culture.getId(), e);
            throw e;
        }
    }
}
```

#### 2.5.2 日志级别使用指南

```java
public class CultureLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public void loadCultures() {
        // ERROR: 严重错误，影响核心功能
        LOGGER.error("Critical: Failed to load any cultures, mod will not function properly");
        
        // WARN: 警告，但不影响核心功能
        LOGGER.warn("Culture file 'norman.json' not found, using default values");
        
        // INFO: 重要的用户信息
        LOGGER.info("Loaded {} cultures: {}", cultures.size(), 
                   cultures.keySet().stream().map(ResourceLocation::toString)
                           .collect(Collectors.joining(", ")));
        
        // DEBUG: 详细的调试信息
        LOGGER.debug("Processing culture data for: {}", cultureId);
        
        // TRACE: 极详细的跟踪信息（通常不使用）
        LOGGER.trace("Parsing building configuration: {}", buildingConfig);
    }
}
```

#### 2.5.3 日志消息格式规范

所有日志消息必须包含模组标识和足够的上下文信息：

```java
// ✅ 推荐：清晰的日志消息
LOGGER.info("[Millenaire] Village '{}' completed construction of building '{}' at {}", 
           village.getName(), building.getType(), building.getPosition());

LOGGER.warn("[Millenaire] Villager {} cannot find path to work location {}, assigning new job",
           villager.getName(), workLocation);

LOGGER.error("[Millenaire] Failed to save village data for '{}' to file '{}': {}", 
            village.getId(), saveFile, e.getMessage(), e);

// ❌ 避免：不清晰的日志消息
LOGGER.info("Done"); // 太简单
LOGGER.error("Error: " + e.getMessage()); // 缺少上下文
```

---

## 3. Forge 1.20.1 特定规范

### 3.1 注册系统

#### 3.1.1 DeferredRegister使用模板

**要求**: 所有内容注册必须使用DeferredRegister模式

```java
// 注册类模板
public class MillenaireBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, MillenaireRewriteMod.MOD_ID);
    
    // 方块注册
    public static final RegistryObject<Block> CULTURE_TOTEM = BLOCKS.register(
        "culture_totem",
        () -> new CultureTotemBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
            .strength(3.0F, 6.0F)
            .requiresCorrectToolForDrops())
    );
    
    public static final RegistryObject<Block> NORMAN_HOUSE_BLOCK = BLOCKS.register(
        "norman_house_block",
        () -> new NormanHouseBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F))
    );
    
    // 私有构造函数防止实例化
    private MillenaireBlocks() {}
}

public class MillenaireItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, MillenaireRewriteMod.MOD_ID);
    
    // 物品注册
    public static final RegistryObject<Item> CULTURE_SCROLL = ITEMS.register(
        "culture_scroll",
        () -> new CultureScrollItem(new Item.Properties())
    );
    
    // 方块物品注册
    public static final RegistryObject<BlockItem> CULTURE_TOTEM_ITEM = ITEMS.register(
        "culture_totem",
        () -> new BlockItem(MillenaireBlocks.CULTURE_TOTEM.get(), new Item.Properties())
    );
    
    private MillenaireItems() {}
}

public class MillenaireEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MillenaireRewriteMod.MOD_ID);
    
    public static final RegistryObject<EntityType<NormanVillager>> NORMAN_VILLAGER = ENTITIES.register(
        "norman_villager",
        () -> EntityType.Builder.of(NormanVillager::new, MobCategory.CREATURE)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build(new ResourceLocation(MillenaireRewriteMod.MOD_ID, "norman_villager").toString())
    );
    
    private MillenaireEntities() {}
}
```

#### 3.1.2 主模组类注册

```java
@Mod(MillenaireRewriteMod.MOD_ID)
public class MillenaireRewriteMod {
    public static final String MOD_ID = "millenaire_rewrite";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public MillenaireRewriteMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册所有内容
        MillenaireBlocks.BLOCKS.register(modEventBus);
        MillenaireItems.ITEMS.register(modEventBus);
        MillenaireEntities.ENTITIES.register(modEventBus);
        MillenaireBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        MillenaireSounds.SOUNDS.register(modEventBus);
        
        // 注册事件监听器
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        
        // 注册配置
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MillenaireConfig.SPEC);
        
        LOGGER.info("[Millenaire] Mod initialization completed");
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 网络注册
            MillenaireNetwork.register();
            
            // 其他初始化
            CultureRegistry.initialize();
            VillageTypes.initialize();
            
            LOGGER.info("[Millenaire] Common setup completed");
        });
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 客户端特定初始化
            EntityRenderers.register(MillenaireEntities.NORMAN_VILLAGER.get(), 
                                   NormanVillagerRenderer::new);
            
            LOGGER.info("[Millenaire] Client setup completed");
        });
    }
}
```

### 3.2 数据生成系统

#### 3.2.1 DataProvider基础设置

**要求**: 所有物品、方块、语言等必须使用DataProvider生成，禁止手写JSON

```java
// 数据生成事件处理
@Mod.EventBusSubscriber(modid = MillenaireRewriteMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MillenaireDataGenerators {
    
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        
        // 服务端数据生成
        generator.addProvider(event.includeServer(), new MillenaireRecipeProvider(output));
        generator.addProvider(event.includeServer(), new MillenaireLootTableProvider(output));
        
        MillenaireBlockTagsProvider blockTagsProvider = new MillenaireBlockTagsProvider(
            output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new MillenaireItemTagsProvider(
            output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        
        // 客户端数据生成
        generator.addProvider(event.includeClient(), new MillenaireBlockStateProvider(
            output, existingFileHelper));
        generator.addProvider(event.includeClient(), new MillenaireItemModelProvider(
            output, existingFileHelper));
        generator.addProvider(event.includeClient(), new MillenaireLanguageProvider(
            output, "en_us"));
    }
}
```

#### 3.2.2 配方生成器模板

```java
public class MillenaireRecipeProvider extends RecipeProvider {
    public MillenaireRecipeProvider(PackOutput output) {
        super(output);
    }
    
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // 有序配方
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, MillenaireBlocks.CULTURE_TOTEM.get())
            .pattern("CSC")
            .pattern("STS")
            .pattern("CSC")
            .define('C', Items.CLAY_BALL)
            .define('S', Items.STONE)
            .define('T', Items.TOTEM_OF_UNDYING)
            .unlockedBy("has_totem", has(Items.TOTEM_OF_UNDYING))
            .save(consumer);
        
        // 无序配方
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MillenaireItems.CULTURE_SCROLL.get())
            .requires(Items.PAPER)
            .requires(Items.INK_SAC)
            .requires(Items.FEATHER)
            .unlockedBy("has_paper", has(Items.PAPER))
            .save(consumer);
        
        // 熔炼配方
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(MillenaireItems.RAW_NORMAN_METAL.get()),
                RecipeCategory.MISC,
                MillenaireItems.NORMAN_INGOT.get(),
                0.7F,
                200)
            .unlockedBy("has_raw_norman_metal", has(MillenaireItems.RAW_NORMAN_METAL.get()))
            .save(consumer);
    }
}
```

#### 3.2.3 方块状态和模型生成

```java
public class MillenaireBlockStateProvider extends BlockStateProvider {
    public MillenaireBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MillenaireRewriteMod.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void registerStatesAndModels() {
        // 简单方块
        simpleBlockWithItem(MillenaireBlocks.CULTURE_TOTEM.get(), 
                           cubeAll(MillenaireBlocks.CULTURE_TOTEM.get()));
        
        // 带方向的方块
        horizontalBlock(MillenaireBlocks.NORMAN_HOUSE_BLOCK.get(),
                       models().orientable("norman_house_block",
                                         modLoc("block/norman_house_side"),
                                         modLoc("block/norman_house_front"),
                                         modLoc("block/norman_house_top")));
        
        // 自定义模型的方块
        ModelFile cultureAltarModel = models().getExistingFile(modLoc("block/culture_altar"));
        getVariantBuilder(MillenaireBlocks.CULTURE_ALTAR.get())
            .partialState()
            .modelForState()
            .modelFile(cultureAltarModel)
            .addModel();
    }
    
    private ResourceLocation modLoc(String path) {
        return new ResourceLocation(MillenaireRewriteMod.MOD_ID, path);
    }
}
```

#### 3.2.4 语言文件生成

```java
public class MillenaireLanguageProvider extends LanguageProvider {
    public MillenaireLanguageProvider(PackOutput output, String locale) {
        super(output, MillenaireRewriteMod.MOD_ID, locale);
    }
    
    @Override
    protected void addTranslations() {
        // 物品翻译
        addItem(MillenaireItems.CULTURE_SCROLL, "Culture Scroll");
        addItem(MillenaireItems.NORMAN_INGOT, "Norman Ingot");
        
        // 方块翻译
        addBlock(MillenaireBlocks.CULTURE_TOTEM, "Culture Totem");
        addBlock(MillenaireBlocks.NORMAN_HOUSE_BLOCK, "Norman House Block");
        
        // 实体翻译
        addEntityType(MillenaireEntities.NORMAN_VILLAGER, "Norman Villager");
        
        // GUI翻译
        add("gui.millenaire_rewrite.village_info.title", "Village Information");
        add("gui.millenaire_rewrite.culture_selection.title", "Choose Culture");
        
        // 消息翻译
        add("message.millenaire_rewrite.village_created", "Village created successfully!");
        add("message.millenaire_rewrite.invalid_location", "Cannot create village here");
        
        // 配置翻译
        add("config.millenaire_rewrite.village_spawn_rate", "Village Spawn Rate");
        add("config.millenaire_rewrite.village_spawn_rate.tooltip", 
            "Controls how frequently villages spawn in the world");
        
        // 创造模式标签页
        add("itemGroup.millenaire_rewrite", "Millenaire Rewrite");
    }
}
```

### 3.3 网络通信系统

#### 3.3.1 网络注册

```java
public class MillenaireNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MillenaireRewriteMod.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    
    private static int packetId = 0;
    
    public static void register() {
        // 村庄数据同步包
        INSTANCE.messageBuilder(VillageDataSyncPacket.class, nextId())
            .decoder(VillageDataSyncPacket::decode)
            .encoder(VillageDataSyncPacket::encode)
            .consumerMainThread(VillageDataSyncPacket::handle)
            .add();
        
        // 村民AI状态更新包
        INSTANCE.messageBuilder(VillagerAIUpdatePacket.class, nextId())
            .decoder(VillagerAIUpdatePacket::decode)
            .encoder(VillagerAIUpdatePacket::encode)
            .consumerMainThread(VillagerAIUpdatePacket::handle)
            .add();
        
        // GUI开启包
        INSTANCE.messageBuilder(OpenVillageGUIPacket.class, nextId())
            .decoder(OpenVillageGUIPacket::decode)
            .encoder(OpenVillageGUIPacket::encode)
            .consumerMainThread(OpenVillageGUIPacket::handle)
            .add();
    }
    
    private static int nextId() {
        return packetId++;
    }
    
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
```

#### 3.3.2 数据包模板

```java
// 数据包基类
public abstract class MillenairePacket {
    protected static final Logger LOGGER = LogUtils.getLogger();
    
    public abstract void encode(FriendlyByteBuf buf);
    public abstract void handle(Supplier<NetworkEvent.Context> contextSupplier);
}

// 具体数据包实现
public class VillageDataSyncPacket extends MillenairePacket {
    private final UUID villageId;
    private final CompoundTag villageData;
    
    public VillageDataSyncPacket(UUID villageId, CompoundTag villageData) {
        this.villageId = villageId;
        this.villageData = villageData;
    }
    
    public static VillageDataSyncPacket decode(FriendlyByteBuf buf) {
        UUID villageId = buf.readUUID();
        CompoundTag villageData = buf.readNbt();
        return new VillageDataSyncPacket(villageId, villageData);
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(villageId);
        buf.writeNbt(villageData);
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {
                // 客户端处理逻辑
                ClientVillageManager.updateVillageData(villageId, villageData);
                LOGGER.debug("Received village data sync for village: {}", villageId);
            });
        }
        
        context.setPacketHandled(true);
    }
}
```

#### 3.3.3 线程安全与客户端/服务端分离

**重要**: 严格遵守客户端/服务端分离原则

```java
// ✅ 正确：安全的网络处理
public class VillagerAIUpdatePacket extends MillenairePacket {
    private final int entityId;
    private final AIState newState;
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if (context.getDirection().getReceptionSide().isClient()) {
            // 确保在客户端线程执行
            context.enqueueWork(() -> {
                Minecraft minecraft = Minecraft.getInstance();
                Level level = minecraft.level;
                
                if (level != null) {
                    Entity entity = level.getEntity(entityId);
                    if (entity instanceof MillenaireVillager villager) {
                        villager.updateAIState(newState);
                    }
                }
            });
        } else {
            // 服务端处理
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player != null) {
                    ServerLevel level = player.serverLevel();
                    Entity entity = level.getEntity(entityId);
                    
                    if (entity instanceof MillenaireVillager villager) {
                        // 验证玩家是否有权限修改
                        if (canPlayerModifyVillager(player, villager)) {
                            villager.updateAIState(newState);
                        }
                    }
                }
            });
        }
        
        context.setPacketHandled(true);
    }
    
    private boolean canPlayerModifyVillager(ServerPlayer player, MillenaireVillager villager) {
        // 权限检查逻辑
        return player.hasPermissions(2) || // OP权限
               isVillagerInPlayerVillage(player, villager);
    }
}

// ❌ 错误：直接访问可能不存在的对象
public void handleBad(Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    
    // 危险：没有检查是否在正确的端
    Minecraft.getInstance().level.getEntity(entityId); // 可能在服务端调用
    
    // 危险：没有使用enqueueWork
    updateVillagerDirectly(); // 可能在网络线程执行
}
```

---

## 4. 模组内容设计规范

### 4.1 文化系统设计

#### 4.1.1 文化数据结构

文化系统采用数据驱动设计，支持JSON配置和代码扩展：

```java
// 文化数据记录
public record CultureData(
    ResourceLocation id,
    String displayName,
    ResourceLocation textureLocation,
    List<String> availableBuildings,
    Map<String, Integer> resourceRequirements,
    CultureConfig config
) {
    public static final Codec<CultureData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(CultureData::id),
            Codec.STRING.fieldOf("display_name").forGetter(CultureData::displayName),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(CultureData::textureLocation),
            Codec.STRING.listOf().fieldOf("buildings").forGetter(CultureData::availableBuildings),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("resources").forGetter(CultureData::resourceRequirements),
            CultureConfig.CODEC.fieldOf("config").forGetter(CultureData::config)
        ).apply(instance, CultureData::new)
    );
}

// 文化配置
public record CultureConfig(
    int basePopulation,
    double growthRate,
    List<ResourceLocation> preferredBiomes,
    Map<String, Double> buildingProbabilities
) {
    public static final Codec<CultureConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("base_population").forGetter(CultureConfig::basePopulation),
            Codec.DOUBLE.fieldOf("growth_rate").forGetter(CultureConfig::growthRate),
            ResourceLocation.CODEC.listOf().fieldOf("preferred_biomes").forGetter(CultureConfig::preferredBiomes),
            Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("building_probabilities").forGetter(CultureConfig::buildingProbabilities)
        ).apply(instance, CultureConfig::new)
    );
}
```

#### 4.1.2 文化注册系统

```java
public class CultureRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, Culture> CULTURES = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, CultureData> CULTURE_DATA = new ConcurrentHashMap<>();
    
    /**
     * 注册新文化
     */
    public static void registerCulture(Culture culture) {
        Objects.requireNonNull(culture, "Culture cannot be null");
        Objects.requireNonNull(culture.getId(), "Culture ID cannot be null");
        
        if (CULTURES.containsKey(culture.getId())) {
            throw new IllegalArgumentException("Culture already registered: " + culture.getId());
        }
        
        CULTURES.put(culture.getId(), culture);
        LOGGER.info("Registered culture: {}", culture.getId());
    }
    
    /**
     * 从JSON数据创建文化
     */
    public static Culture createFromData(CultureData data) {
        return new Culture.Builder(data.id())
            .displayName(data.displayName())
            .textureLocation(data.textureLocation())
            .basePopulation(data.config().basePopulation())
            .growthRate(data.config().growthRate())
            .preferredBiomes(data.config().preferredBiomes())
            .buildingTypes(data.availableBuildings())
            .build();
    }
    
    /**
     * 加载JSON文化配置
     */
    public static void loadCultureData(ResourceManager resourceManager) {
        Collection<ResourceLocation> resources = resourceManager.listResources(
            "cultures", location -> location.getPath().endsWith(".json"));
        
        for (ResourceLocation location : resources) {
            try {
                Resource resource = resourceManager.getResource(location).orElseThrow();
                CultureData data = GsonHelper.fromJson(GSON, 
                    new InputStreamReader(resource.open()), CultureData.class);
                
                CULTURE_DATA.put(data.id(), data);
                registerCulture(createFromData(data));
                
            } catch (Exception e) {
                LOGGER.error("Failed to load culture data from {}", location, e);
            }
        }
    }
    
    public static Optional<Culture> getCulture(ResourceLocation id) {
        return Optional.ofNullable(CULTURES.get(id));
    }
    
    public static Collection<Culture> getAllCultures() {
        return Collections.unmodifiableCollection(CULTURES.values());
    }
}
```

#### 4.1.3 文化JSON格式

```json
{
  "id": "millenaire_rewrite:norman",
  "display_name": "Norman Culture",
  "texture": "millenaire_rewrite:textures/culture/norman.png",
  "buildings": [
    "house_small",
    "house_medium", 
    "house_large",
    "blacksmith",
    "church",
    "town_hall"
  ],
  "resources": {
    "wood": 100,
    "stone": 80,
    "iron": 20
  },
  "config": {
    "base_population": 10,
    "growth_rate": 0.02,
    "preferred_biomes": [
      "minecraft:plains",
      "minecraft:forest"
    ],
    "building_probabilities": {
      "house_small": 0.4,
      "house_medium": 0.3,
      "house_large": 0.1,
      "blacksmith": 0.1,
      "church": 0.05,
      "town_hall": 0.05
    }
  }
}
```

### 4.2 村庄系统设计

#### 4.2.1 村庄数据模型

```java
public class VillageData {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private final UUID villageId;
    private final BlockPos centerPos;
    private final ResourceLocation cultureId;
    private final String villageName;
    
    // 村庄状态
    private int population;
    private double happiness;
    private VillageLevel level;
    private final Map<ResourceLocation, Integer> resources;
    private final List<Building> buildings;
    private final List<UUID> villagers;
    
    // 经济数据
    private final Map<Item, Integer> inventory;
    private final List<TradeOffer> activeOffers;
    
    // 构造函数和建造者模式
    private VillageData(Builder builder) {
        this.villageId = builder.villageId;
        this.centerPos = builder.centerPos;
        this.cultureId = builder.cultureId;
        this.villageName = builder.villageName;
        this.population = builder.population;
        this.happiness = builder.happiness;
        this.level = builder.level;
        this.resources = new ConcurrentHashMap<>(builder.resources);
        this.buildings = new ArrayList<>(builder.buildings);
        this.villagers = new ArrayList<>(builder.villagers);
        this.inventory = new ConcurrentHashMap<>(builder.inventory);
        this.activeOffers = new ArrayList<>(builder.activeOffers);
    }
    
    public static class Builder {
        private UUID villageId = UUID.randomUUID();
        private BlockPos centerPos;
        private ResourceLocation cultureId;
        private String villageName;
        private int population = 0;
        private double happiness = 0.5;
        private VillageLevel level = VillageLevel.SETTLEMENT;
        private Map<ResourceLocation, Integer> resources = new HashMap<>();
        private List<Building> buildings = new ArrayList<>();
        private List<UUID> villagers = new ArrayList<>();
        private Map<Item, Integer> inventory = new HashMap<>();
        private List<TradeOffer> activeOffers = new ArrayList<>();
        
        public Builder centerPos(BlockPos pos) {
            this.centerPos = pos;
            return this;
        }
        
        public Builder culture(ResourceLocation cultureId) {
            this.cultureId = cultureId;
            return this;
        }
        
        public Builder name(String name) {
            this.villageName = name;
            return this;
        }
        
        // 其他建造方法...
        
        public VillageData build() {
            Objects.requireNonNull(centerPos, "Center position is required");
            Objects.requireNonNull(cultureId, "Culture ID is required");
            Objects.requireNonNull(villageName, "Village name is required");
            
            return new VillageData(this);
        }
    }
    
    // 村庄更新逻辑
    public void tick(ServerLevel level) {
        if (level.getGameTime() % 20 == 0) { // 每秒更新一次
            updatePopulation();
            updateHappiness();
            updateResources();
            processBuildings(level);
        }
    }
    
    private void updatePopulation() {
        // 基于幸福度和资源计算人口增长
        if (happiness > 0.7 && hasBasicResources()) {
            double growthChance = happiness * 0.001; // 每秒0.1%的基础增长率
            if (Math.random() < growthChance) {
                population++;
                LOGGER.debug("Village {} population increased to {}", villageName, population);
            }
        }
    }
    
    private void updateHappiness() {
        double newHappiness = 0.5; // 基础幸福度
        
        // 基于住房质量
        newHappiness += calculateHousingBonus();
        
        // 基于资源充足度
        newHappiness += calculateResourceBonus();
        
        // 基于安全性
        newHappiness += calculateSafetyBonus();
        
        this.happiness = Math.max(0.0, Math.min(1.0, newHappiness));
    }
    
    // NBT序列化
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("VillageId", villageId);
        tag.putLong("CenterPos", centerPos.asLong());
        tag.putString("CultureId", cultureId.toString());
        tag.putString("VillageName", villageName);
        tag.putInt("Population", population);
        tag.putDouble("Happiness", happiness);
        tag.putString("Level", level.name());
        
        // 保存资源
        CompoundTag resourcesTag = new CompoundTag();
        resources.forEach((resource, amount) -> 
            resourcesTag.putInt(resource.toString(), amount));
        tag.put("Resources", resourcesTag);
        
        // 保存建筑
        ListTag buildingsTag = new ListTag();
        buildings.forEach(building -> buildingsTag.add(building.saveToNBT()));
        tag.put("Buildings", buildingsTag);
        
        return tag;
    }
    
    public static VillageData loadFromNBT(CompoundTag tag) {
        UUID villageId = tag.getUUID("VillageId");
        BlockPos centerPos = BlockPos.of(tag.getLong("CenterPos"));
        ResourceLocation cultureId = new ResourceLocation(tag.getString("CultureId"));
        String villageName = tag.getString("VillageName");
        
        Builder builder = new Builder()
            .centerPos(centerPos)
            .culture(cultureId)
            .name(villageName);
        
        // 加载其他数据...
        
        return builder.build();
    }
}

// 村庄等级枚举
public enum VillageLevel {
    SETTLEMENT(0, 50, "Settlement"),
    VILLAGE(50, 150, "Village"), 
    TOWN(150, 500, "Town"),
    CITY(500, Integer.MAX_VALUE, "City");
    
    private final int minPopulation;
    private final int maxPopulation;
    private final String displayName;
    
    VillageLevel(int minPopulation, int maxPopulation, String displayName) {
        this.minPopulation = minPopulation;
        this.maxPopulation = maxPopulation;
        this.displayName = displayName;
    }
    
    public static VillageLevel fromPopulation(int population) {
        for (VillageLevel level : values()) {
            if (population >= level.minPopulation && population < level.maxPopulation) {
                return level;
            }
        }
        return SETTLEMENT;
    }
}
```

### 4.3 村民AI系统

#### 4.3.1 Goal系统设计

```java
// AI目标基类
public abstract class MillenaireGoal extends Goal {
    protected static final Logger LOGGER = LogUtils.getLogger();
    protected final MillenaireVillager villager;
    protected final Level level;
    
    protected MillenaireGoal(MillenaireVillager villager, int priority) {
        this.villager = villager;
        this.level = villager.level();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }
    
    /**
     * 检查是否可以开始执行此目标
     */
    @Override
    public abstract boolean canUse();
    
    /**
     * 检查是否应该继续执行此目标
     */
    @Override
    public boolean canContinueToUse() {
        return canUse();
    }
    
    /**
     * 目标开始时调用
     */
    @Override
    public void start() {
        LOGGER.debug("Villager {} started goal: {}", villager.getName().getString(), 
                    this.getClass().getSimpleName());
    }
    
    /**
     * 目标结束时调用
     */
    @Override
    public void stop() {
        LOGGER.debug("Villager {} stopped goal: {}", villager.getName().getString(), 
                    this.getClass().getSimpleName());
    }
    
    /**
     * 每tick执行的逻辑
     */
    @Override
    public abstract void tick();
    
    /**
     * 获取目标的优先级（数字越小优先级越高）
     */
    public abstract int getPriority();
}

// 工作目标
public class WorkGoal extends MillenaireGoal {
    private static final int WORK_DURATION = 200; // 10秒
    private static final double WORK_RANGE = 16.0;
    
    private BlockPos workPos;
    private int workTimer;
    private final VillagerProfession profession;
    
    public WorkGoal(MillenaireVillager villager, VillagerProfession profession) {
        super(villager, 3);
        this.profession = profession;
    }
    
    @Override
    public boolean canUse() {
        // 只在白天工作
        if (!level.isDay()) {
            return false;
        }
        
        // 检查是否有工作地点
        if (workPos == null) {
            workPos = findWorkLocation();
        }
        
        return workPos != null && villager.distanceToSqr(Vec3.atCenterOf(workPos)) < WORK_RANGE * WORK_RANGE;
    }
    
    @Override
    public void start() {
        super.start();
        workTimer = 0;
        villager.getNavigation().moveTo(workPos.getX() + 0.5, workPos.getY(), workPos.getZ() + 0.5, 0.6);
    }
    
    @Override
    public void tick() {
        if (workPos == null) {
            return;
        }
        
        // 检查是否到达工作地点
        if (villager.distanceToSqr(Vec3.atCenterOf(workPos)) < 4.0) {
            // 面向工作台
            villager.getLookControl().setLookAt(workPos.getX() + 0.5, workPos.getY() + 0.5, workPos.getZ() + 0.5);
            
            workTimer++;
            
            // 每40tick（2秒）执行一次工作
            if (workTimer % 40 == 0) {
                performWork();
            }
            
            // 工作完成
            if (workTimer >= WORK_DURATION) {
                completeWork();
            }
        } else {
            // 重新导航到工作地点
            villager.getNavigation().moveTo(workPos.getX() + 0.5, workPos.getY(), workPos.getZ() + 0.5, 0.6);
        }
    }
    
    private BlockPos findWorkLocation() {
        VillageData village = villager.getVillageData();
        if (village == null) {
            return null;
        }
        
        // 根据职业寻找对应的建筑
        return village.getBuildings().stream()
            .filter(building -> profession.canWorkAt(building.getType()))
            .map(Building::getWorkPos)
            .filter(Objects::nonNull)
            .min(Comparator.comparing(pos -> villager.distanceToSqr(Vec3.atCenterOf(pos))))
            .orElse(null);
    }
    
    private void performWork() {
        // 播放工作动画和音效
        villager.swing(InteractionHand.MAIN_HAND);
        level.playSound(null, villager.blockPosition(), profession.getWorkSound(), 
                       SoundSource.NEUTRAL, 0.5F, 1.0F);
        
        // 根据职业执行特定工作
        profession.performWork(villager, level, workPos);
    }
    
    private void completeWork() {
        workTimer = 0;
        villager.addExperience(1);
        
        // 有概率产出物品
        if (level.random.nextFloat() < profession.getProductionRate()) {
            ItemStack product = profession.createProduct(level.random);
            if (!product.isEmpty()) {
                villager.getInventory().addItem(product);
                LOGGER.debug("Villager {} produced {}", villager.getName().getString(), product);
            }
        }
    }
    
    @Override
    public int getPriority() {
        return 3;
    }
}

// 交易目标
public class TradeGoal extends MillenaireGoal {
    private Player targetPlayer;
    private int tradeCooldown;
    
    public TradeGoal(MillenaireVillager villager) {
        super(villager, 2);
    }
    
    @Override
    public boolean canUse() {
        if (tradeCooldown > 0) {
            tradeCooldown--;
            return false;
        }
        
        targetPlayer = level.getNearestPlayer(villager, 3.0);
        return targetPlayer != null && !targetPlayer.isShiftKeyDown();
    }
    
    @Override
    public void start() {
        super.start();
        villager.getLookControl().setLookAt(targetPlayer);
    }
    
    @Override
    public void tick() {
        if (targetPlayer == null || targetPlayer.distanceToSqr(villager) > 9.0) {
            return;
        }
        
        villager.getLookControl().setLookAt(targetPlayer);
        
        // 每2秒检查一次交易机会
        if (villager.tickCount % 40 == 0) {
            attemptTrade();
        }
    }
    
    private void attemptTrade() {
        VillageData village = villager.getVillageData();
        if (village == null) {
            return;
        }
        
        List<TradeOffer> availableOffers = village.getAvailableOffers();
        if (!availableOffers.isEmpty()) {
            // 显示交易界面或发送交易信息
            MillenaireNetwork.sendToPlayer(
                new TradeOffersPacket(villager.getId(), availableOffers), 
                (ServerPlayer) targetPlayer);
        }
    }
    
    @Override
    public void stop() {
        super.stop();
        targetPlayer = null;
        tradeCooldown = 100; // 5秒冷却
    }
    
    @Override
    public int getPriority() {
        return 2;
    }
}
```

### 4.4 配置系统

#### 4.4.1 ForgeConfigSpec配置

**要求**: 使用ForgeConfigSpec构建类型安全的配置系统

```java
public class MillenaireConfig {
    private static final Builder BUILDER = new Builder();
    
    // 村庄生成配置
    public static final ForgeConfigSpec.IntValue VILLAGE_SPAWN_RATE;
    public static final ForgeConfigSpec.IntValue MIN_DISTANCE_BETWEEN_VILLAGES;
    public static final ForgeConfigSpec.IntValue MAX_VILLAGE_SIZE;
    public static final ForgeConfigSpec.BooleanValue ALLOW_VILLAGE_EXPANSION;
    
    // 村民AI配置
    public static final ForgeConfigSpec.DoubleValue VILLAGER_WORK_SPEED;
    public static final ForgeConfigSpec.IntValue VILLAGER_INVENTORY_SIZE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_VILLAGER_TRADING;
    
    // 性能配置
    public static final ForgeConfigSpec.IntValue VILLAGE_UPDATE_INTERVAL;
    public static final ForgeConfigSpec.IntValue MAX_VILLAGES_PER_CHUNK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_VILLAGE_CACHING;
    
    // 文化配置
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENABLED_CULTURES;
    public static final ForgeConfigSpec.BooleanValue ALLOW_CUSTOM_CULTURES;
    
    static {
        BUILDER.comment("Millenaire Rewrite Configuration");
        
        // 村庄生成配置
        BUILDER.push("village_generation");
        VILLAGE_SPAWN_RATE = BUILDER
            .comment("Controls how frequently villages spawn (1-100, higher = more frequent)")
            .defineInRange("spawn_rate", 20, 1, 100);
        
        MIN_DISTANCE_BETWEEN_VILLAGES = BUILDER
            .comment("Minimum distance between villages in chunks")
            .defineInRange("min_distance", 8, 4, 32);
        
        MAX_VILLAGE_SIZE = BUILDER
            .comment("Maximum size of a village in blocks")
            .defineInRange("max_size", 200, 50, 500);
        
        ALLOW_VILLAGE_EXPANSION = BUILDER
            .comment("Allow villages to expand over time")
            .define("allow_expansion", true);
        BUILDER.pop();
        
        // 村民AI配置
        BUILDER.push("villager_ai");
        VILLAGER_WORK_SPEED = BUILDER
            .comment("Multiplier for villager work speed (0.1 - 5.0)")
            .defineInRange("work_speed", 1.0, 0.1, 5.0);
        
        VILLAGER_INVENTORY_SIZE = BUILDER
            .comment("Size of villager inventories")
            .defineInRange("inventory_size", 27, 9, 54);
        
        ENABLE_VILLAGER_TRADING = BUILDER
            .comment("Enable trading with villagers")
            .define("enable_trading", true);
        BUILDER.pop();
        
        // 性能配置
        BUILDER.push("performance");
        VILLAGE_UPDATE_INTERVAL = BUILDER
            .comment("Ticks between village updates (higher = better performance)")
            .defineInRange("update_interval", 20, 1, 200);
        
        MAX_VILLAGES_PER_CHUNK = BUILDER
            .comment("Maximum number of villages per chunk")
            .defineInRange("max_villages_per_chunk", 1, 1, 5);
        
        ENABLE_VILLAGE_CACHING = BUILDER
            .comment("Enable village data caching for better performance")
            .define("enable_caching", true);
        BUILDER.pop();
        
        // 文化配置
        BUILDER.push("cultures");
        ENABLED_CULTURES = BUILDER
            .comment("List of enabled cultures")
            .defineList("enabled_cultures", 
                       Arrays.asList("norman", "japanese", "byzantine"),
                       obj -> obj instanceof String);
        
        ALLOW_CUSTOM_CULTURES = BUILDER
            .comment("Allow loading of custom culture files")
            .define("allow_custom_cultures", true);
        BUILDER.pop();
    }
    
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    
    // 配置验证方法
    public static void validateConfig() {
        if (VILLAGE_SPAWN_RATE.get() <= 0) {
            throw new IllegalStateException("Village spawn rate must be positive");
        }
        
        if (MIN_DISTANCE_BETWEEN_VILLAGES.get() < 4) {
            throw new IllegalStateException("Minimum distance between villages too small");
        }
    }
    
    // 配置更新监听
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            validateConfig();
            LOGGER.info("Millenaire configuration reloaded");
        }
    }
}
```

#### 4.4.2 配置文件示例

生成的配置文件 `millenaire_rewrite-common.toml`:

```toml
#Millenaire Rewrite Configuration

[village_generation]
	#Controls how frequently villages spawn (1-100, higher = more frequent)
	#Range: 1 ~ 100
	spawn_rate = 20
	#Minimum distance between villages in chunks
	#Range: 4 ~ 32
	min_distance = 8
	#Maximum size of a village in blocks
	#Range: 50 ~ 500
	max_size = 200
	#Allow villages to expand over time
	allow_expansion = true

[villager_ai]
	#Multiplier for villager work speed (0.1 - 5.0)
	#Range: 0.1 ~ 5.0
	work_speed = 1.0
	#Size of villager inventories
	#Range: 9 ~ 54
	inventory_size = 27
	#Enable trading with villagers
	enable_trading = true

[performance]
	#Ticks between village updates (higher = better performance)
	#Range: 1 ~ 200
	update_interval = 20
	#Maximum number of villages per chunk
	#Range: 1 ~ 5
	max_villages_per_chunk = 1
	#Enable village data caching for better performance
	enable_caching = true

[cultures]
	#List of enabled cultures
	enabled_cultures = ["norman", "japanese", "byzantine"]
	#Allow loading of custom culture files
	allow_custom_cultures = true
```

---

## 5. 性能与最佳实践

### 5.1 内存管理

#### 5.1.1 避免内存泄漏

```java
// ✅ 正确：使用WeakReference避免循环引用
public class VillageManager {
    private final Map<UUID, WeakReference<VillageData>> villageCache = new ConcurrentHashMap<>();
    
    public void cacheVillage(VillageData village) {
        villageCache.put(village.getId(), new WeakReference<>(village));
    }
    
    public Optional<VillageData> getCachedVillage(UUID id) {
        WeakReference<VillageData> ref = villageCache.get(id);
        if (ref != null) {
            VillageData village = ref.get();
            if (village != null) {
                return Optional.of(village);
            } else {
                // 清理已被GC的引用
                villageCache.remove(id);
            }
        }
        return Optional.empty();
    }
}

// ✅ 正确：正确注销事件监听器
public class VillagerEntity extends Mob {
    private EventHandler eventHandler;
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.eventHandler = new VillagerEventHandler(this);
        MinecraftForge.EVENT_BUS.register(this.eventHandler);
    }
    
    @Override
    public void remove(RemovalReason reason) {
        // 重要：注销事件监听器防止内存泄漏
        if (this.eventHandler != null) {
            MinecraftForge.EVENT_BUS.unregister(this.eventHandler);
            this.eventHandler = null;
        }
        super.remove(reason);
    }
}

// ❌ 错误：可能导致内存泄漏的静态集合
public class BadVillageManager {
    private static final Map<UUID, VillageData> STATIC_CACHE = new HashMap<>(); // 危险！
    
    public static void addVillage(VillageData village) {
        STATIC_CACHE.put(village.getId(), village); // 永远不会被GC
    }
}
```

#### 5.1.2 对象池模式

对于频繁创建的对象，使用对象池：

```java
public class PathfindingResultPool {
    private static final int POOL_SIZE = 100;
    private static final Queue<PathfindingResult> POOL = new ConcurrentLinkedQueue<>();
    
    static {
        // 预填充对象池
        for (int i = 0; i < POOL_SIZE; i++) {
            POOL.offer(new PathfindingResult());
        }
    }
    
    public static PathfindingResult acquire() {
        PathfindingResult result = POOL.poll();
        return result != null ? result.reset() : new PathfindingResult();
    }
    
    public static void release(PathfindingResult result) {
        if (POOL.size() < POOL_SIZE) {
            POOL.offer(result.reset());
        }
    }
}

// 使用示例
public class VillagerPathfinding {
    public void findPath(BlockPos target) {
        PathfindingResult result = PathfindingResultPool.acquire();
        try {
            // 使用result进行路径计算
            computePath(result, target);
        } finally {
            PathfindingResultPool.release(result);
        }
    }
}
```

### 5.2 Tick性能优化

#### 5.2.1 时间间隔检查

**禁止**: 在每个tick执行昂贵操作
**要求**: 使用时间间隔和缓存机制

```java
// ✅ 推荐：使用时间间隔的优化tick逻辑
public class VillageData {
    private static final int UPDATE_INTERVAL = 20; // 每秒更新一次
    private static final int EXPENSIVE_UPDATE_INTERVAL = 200; // 每10秒更新一次
    
    private int tickCounter = 0;
    private List<Entity> cachedNearbyEntities;
    private long lastExpensiveUpdate = 0;
    
    public void tick(ServerLevel level) {
        tickCounter++;
        
        // 基本更新：每秒一次
        if (tickCounter % UPDATE_INTERVAL == 0) {
            updateBasicVillageStats();
        }
        
        // 昂贵操作：每10秒一次
        if (tickCounter % EXPENSIVE_UPDATE_INTERVAL == 0) {
            performExpensiveUpdates(level);
            lastExpensiveUpdate = level.getGameTime();
        }
        
        // 缓存失效检查
        if (level.getGameTime() - lastExpensiveUpdate > EXPENSIVE_UPDATE_INTERVAL) {
            invalidateCache();
        }
    }
    
    private void performExpensiveUpdates(ServerLevel level) {
        // 缓存附近的实体而不是每次查询
        AABB searchBox = new AABB(centerPos).inflate(64);
        cachedNearbyEntities = level.getEntitiesOfClass(Entity.class, searchBox);
        
        // 其他昂贵操作
        recalculateVillageStructure();
        updateEconomicData();
    }
    
    // ❌ 错误示例：每tick执行昂贵操作
    public void badTick(ServerLevel level) {
        // 危险：每tick查询实体
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, 
                                                        new AABB(centerPos).inflate(64));
        
        // 危险：每tick执行复杂计算
        recalculateVillageStructure();
        updateEconomicData();
    }
}
```

#### 5.2.2 分帧处理

对于大型操作，分散到多个tick执行：

```java
public class VillageStructureBuilder {
    private final Queue<BuildingTask> buildingQueue = new ArrayDeque<>();
    private static final int BLOCKS_PER_TICK = 5; // 每tick最多放置5个方块
    
    public void scheduleBuilding(Building building) {
        // 将大型建筑分解为小任务
        List<BuildingTask> tasks = building.createBuildingTasks();
        buildingQueue.addAll(tasks);
    }
    
    public void tick(ServerLevel level) {
        int processedBlocks = 0;
        
        while (!buildingQueue.isEmpty() && processedBlocks < BLOCKS_PER_TICK) {
            BuildingTask task = buildingQueue.poll();
            
            if (task.execute(level)) {
                processedBlocks++;
            } else {
                // 任务失败，重新排队
                buildingQueue.offer(task);
                break;
            }
        }
    }
}
```

### 5.3 并发处理

#### 5.3.1 线程安全的数据访问

```java
public class ThreadSafeVillageRegistry {
    private final ConcurrentHashMap<UUID, VillageData> villages = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public void addVillage(VillageData village) {
        lock.writeLock().lock();
        try {
            villages.put(village.getId(), village);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public Optional<VillageData> getVillage(UUID id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(villages.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public List<VillageData> getAllVillages() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(villages.values());
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

#### 5.3.2 异步数据加载

```java
public class VillageDataLoader {
    private static final ExecutorService LOADER_EXECUTOR = 
        Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "Millenaire-DataLoader");
            t.setDaemon(true);
            return t;
        });
    
    public CompletableFuture<VillageData> loadVillageAsync(Path file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadVillageFromFile(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load village data", e);
            }
        }, LOADER_EXECUTOR);
    }
    
    public void shutdown() {
        LOADER_EXECUTOR.shutdown();
        try {
            if (!LOADER_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                LOADER_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOADER_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

### 5.4 版本控制与迁移标记

#### 5.4.1 代码迁移标记

对于从1.12.2迁移的代码，使用标准化的注释标记：

```java
public class VillagerAI {
    
    // TODO 1.20.1: 使用新的Goal系统替换旧的AI逻辑
    public void updateOldAI() {
        // LEGACY 1.12.2: 旧版本的AI更新逻辑
        // 需要迁移到新的Goal系统
    }
    
    // MIGRATED 1.20.1: 已从1.12.2成功迁移
    public void updateNewAI() {
        // 新的Goal系统实现
    }
    
    // CHANGED 1.20.1: 相比1.12.2有重大变化
    public void handleTrading() {
        // 注意：交易系统在1.20.1中有显著变化
        // 原版本使用直接物品交换，现版本使用菜单系统
    }
    
    // DEPRECATED 1.20.1: 计划在未来版本移除
    @Deprecated(forRemoval = true)
    public void legacyMethod() {
        // 保留用于兼容性，但不应在新代码中使用
    }
}
```

#### 5.4.2 配置迁移

```java
public class ConfigMigration {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static void migrateFromLegacy(Path legacyConfigFile, Path newConfigFile) {
        if (Files.exists(legacyConfigFile) && !Files.exists(newConfigFile)) {
            try {
                Properties legacyProps = new Properties();
                legacyProps.load(Files.newInputStream(legacyConfigFile));
                
                // 迁移配置项
                ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
                
                // MIGRATION 1.12.2 -> 1.20.1: 配置键名变化
                String oldSpawnRate = legacyProps.getProperty("villageSpawnRate", "20");
                builder.define("village_generation.spawn_rate", Integer.parseInt(oldSpawnRate));
                
                String oldMaxSize = legacyProps.getProperty("maxVillageSize", "200");
                builder.define("village_generation.max_size", Integer.parseInt(oldMaxSize));
                
                LOGGER.info("Successfully migrated legacy configuration");
                
            } catch (Exception e) {
                LOGGER.error("Failed to migrate legacy configuration", e);
            }
        }
    }
}
```

---

## 6. 文档与注释规范

### 6.1 JavaDoc规范

#### 6.1.1 公共API文档

**要求**: 所有公共API必须包含完整的JavaDoc

```java
/**
 * 管理游戏世界中的村庄数据和生命周期。
 * 
 * <p>VillageManager负责：
 * <ul>
 *   <li>村庄的创建和销毁</li>
 *   <li>村庄数据的持久化</li>
 *   <li>村庄间的交互逻辑</li>
 *   <li>性能优化的缓存管理</li>
 * </ul>
 * 
 * <p><b>线程安全性</b>: 此类是线程安全的，可以从多个线程同时访问。
 * 
 * <p><b>使用示例</b>:
 * <pre>{@code
 * VillageManager manager = VillageManager.getInstance();
 * VillageData village = manager.createVillage(pos, culture);
 * manager.saveVillage(village);
 * }</pre>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 0.1.0
 * @see VillageData
 * @see Culture
 */
public class VillageManager {
    
    /**
     * 在指定位置创建新村庄。
     * 
     * <p>此方法会验证位置的合法性，确保：
     * <ul>
     *   <li>位置未被其他村庄占用</li>
     *   <li>地形适合村庄生成</li>
     *   <li>符合最小距离要求</li>
     * </ul>
     * 
     * @param position 村庄中心位置，不能为null
     * @param culture 村庄文化，必须是已注册的文化
     * @param level 目标世界，不能为null
     * @return 创建的村庄数据，如果创建失败则返回empty
     * @throws IllegalArgumentException 如果参数无效
     * @throws VillageCreationException 如果村庄创建失败
     * 
     * @implNote 此方法会触发{@link VillageCreatedEvent}事件
     * @see #canCreateVillageAt(BlockPos, ServerLevel)
     */
    public Optional<VillageData> createVillage(
            @NotNull BlockPos position, 
            @NotNull Culture culture, 
            @NotNull ServerLevel level) {
        // 实现逻辑
    }
    
    /**
     * 检查指定位置是否可以创建村庄。
     * 
     * @param position 要检查的位置
     * @param level 目标世界
     * @return 如果位置合法则返回true
     * @deprecated 使用 {@link #validateVillageLocation(BlockPos, ServerLevel)} 替代
     */
    @Deprecated(since = "0.1.3", forRemoval = true)
    public boolean canCreateVillageAt(BlockPos position, ServerLevel level) {
        return validateVillageLocation(position, level).isValid();
    }
    
    /**
     * 验证村庄位置的详细信息。
     * 
     * @param position 要验证的位置
     * @param level 目标世界
     * @return 验证结果，包含详细的错误信息
     * @since 0.1.3
     */
    public ValidationResult validateVillageLocation(
            @NotNull BlockPos position, 
            @NotNull ServerLevel level) {
        // 实现逻辑
    }
}
```

#### 6.1.2 复杂算法文档

```java
/**
 * 村庄经济系统的供需平衡算法。
 * 
 * <p>此算法基于以下经济学原理：
 * <ul>
 *   <li>供需关系影响价格</li>
 *   <li>资源稀缺性决定价值</li>
 *   <li>贸易距离影响成本</li>
 * </ul>
 * 
 * <p><b>算法复杂度</b>: O(n log n)，其中n是村庄中的物品种类数
 * 
 * <p><b>计算公式</b>:
 * <pre>
 * price = base_price × (demand / supply) × distance_modifier × rarity_factor
 * </pre>
 * 
 * @param village 进行计算的村庄
 * @param item 目标物品
 * @param quantity 交易数量
 * @return 计算得出的价格，单位为村庄货币
 * 
 * @complexity O(n log n)
 * @algorithm 基于供需平衡的动态定价
 */
public double calculateItemPrice(VillageData village, Item item, int quantity) {
    // 获取当前供应量
    int supply = village.getInventory().getAmount(item);
    
    // 计算需求量（基于村民职业和人口）
    double demand = calculateDemand(village, item);
    
    // 应用供需比例
    double supplyDemandRatio = demand / Math.max(supply, 1);
    
    // 距离修正因子（基于最近贸易伙伴距离）
    double distanceModifier = calculateDistanceModifier(village, item);
    
    // 稀有度因子
    double rarityFactor = ItemRarityRegistry.getRarityFactor(item);
    
    // 最终价格计算
    double basePrice = ItemPriceRegistry.getBasePrice(item);
    return basePrice * supplyDemandRatio * distanceModifier * rarityFactor;
}
```

### 6.2 代码注释规范

#### 6.2.1 解释"为什么"而非"做什么"

```java
public class VillagerPathfinding {
    
    public boolean findPath(BlockPos target) {
        // 使用A*算法而非简单的直线路径，因为村庄中有复杂的建筑结构
        // 需要避开房屋、围墙等障碍物
        AStarPathfinder pathfinder = new AStarPathfinder(level);
        
        // 增加路径成本以避免践踏农田
        // 这是从1.12.2版本继承的重要行为，保持向后兼容
        pathfinder.addAvoidanceCost(Blocks.FARMLAND, 10.0);
        
        // 优先使用道路方块，减少路径计算时间
        // 同时让村民行为更加真实（沿着道路行走）
        pathfinder.addPreferenceCost(MillenaireBlocks.ROAD_BLOCK.get(), -5.0);
        
        return pathfinder.calculatePath(villager.blockPosition(), target);
    }
}
```

#### 6.2.2 迁移相关注释

```java
public class VillageDataSerializer {
    
    public CompoundTag serialize(VillageData village) {
        CompoundTag tag = new CompoundTag();
        
        // MIGRATION NOTE: 1.12.2 -> 1.20.1
        // 旧版本将村庄ID存储为字符串，新版本使用UUID
        // 保留兼容性读取逻辑，但始终以UUID格式写入
        tag.putUUID("VillageId", village.getId());
        
        // CHANGED: 1.20.1版本的BlockPos序列化方式已改变
        // 使用新的asLong()方法替代旧的toLong()
        tag.putLong("CenterPos", village.getCenterPos().asLong());
        
        // LEGACY SUPPORT: 保留旧的字符串格式文化ID以支持旧存档
        tag.putString("CultureId", village.getCultureId().toString());
        tag.putString("LegacyCultureName", village.getCulture().getDisplayName());
        
        return tag;
    }
    
    public VillageData deserialize(CompoundTag tag) {
        UUID villageId;
        
        // COMPATIBILITY: 处理1.12.2版本的字符串ID
        if (tag.hasUUID("VillageId")) {
            villageId = tag.getUUID("VillageId");
        } else if (tag.contains("VillageIdString")) {
            // 从旧版本字符串生成确定性UUID
            String oldId = tag.getString("VillageIdString");
            villageId = UUID.nameUUIDFromBytes(oldId.getBytes());
            LOGGER.info("Migrated legacy village ID '{}' to UUID: {}", oldId, villageId);
        } else {
            villageId = UUID.randomUUID();
            LOGGER.warn("Village data missing ID, generated new UUID: {}", villageId);
        }
        
        return new VillageData.Builder()
            .id(villageId)
            // ... 其他构建逻辑
            .build();
    }
}
```

#### 6.2.3 性能相关注释

```java
public class VillageUpdateManager {
    
    // 缓存上次更新时间，避免重复计算
    // 每个村庄独立跟踪，因为更新频率可能不同
    private final Map<UUID, Long> lastUpdateTimes = new ConcurrentHashMap<>();
    
    public void updateVillages(ServerLevel level) {
        long currentTime = level.getGameTime();
        
        // 性能优化：分批处理村庄更新，避免单tick内处理过多村庄
        // 每tick最多更新5个村庄，剩余的在下一tick处理
        int updatesThisTick = 0;
        final int MAX_UPDATES_PER_TICK = 5;
        
        for (VillageData village : getAllVillages()) {
            if (updatesThisTick >= MAX_UPDATES_PER_TICK) {
                break; // 延迟到下一tick
            }
            
            Long lastUpdate = lastUpdateTimes.get(village.getId());
            
            // 避免频繁更新：每个村庄至少间隔20tick（1秒）更新一次
            if (lastUpdate == null || currentTime - lastUpdate >= 20) {
                village.tick(level);
                lastUpdateTimes.put(village.getId(), currentTime);
                updatesThisTick++;
            }
        }
        
        // 内存清理：移除已删除村庄的更新时间记录
        // 注意：这里使用迭代器避免ConcurrentModificationException
        lastUpdateTimes.entrySet().removeIf(entry -> 
            !villageExists(entry.getKey()));
    }
}
```

### 6.3 本地化文档

#### 6.3.1 语言键命名规范

```java
public class MillenaireTranslationKeys {
    // 物品和方块
    public static final String CULTURE_TOTEM = "block.millenaire_rewrite.culture_totem";
    public static final String NORMAN_VILLAGER_EGG = "item.millenaire_rewrite.norman_villager_spawn_egg";
    
    // GUI相关
    public static final String GUI_VILLAGE_INFO_TITLE = "gui.millenaire_rewrite.village_info.title";
    public static final String GUI_VILLAGE_INFO_POPULATION = "gui.millenaire_rewrite.village_info.population";
    public static final String GUI_VILLAGE_INFO_HAPPINESS = "gui.millenaire_rewrite.village_info.happiness";
    
    // 消息和通知
    public static final String MESSAGE_VILLAGE_CREATED = "message.millenaire_rewrite.village.created";
    public static final String MESSAGE_VILLAGE_CREATION_FAILED = "message.millenaire_rewrite.village.creation_failed";
    public static final String MESSAGE_INSUFFICIENT_RESOURCES = "message.millenaire_rewrite.trade.insufficient_resources";
    
    // 配置描述
    public static final String CONFIG_VILLAGE_SPAWN_RATE = "config.millenaire_rewrite.village_spawn_rate";
    public static final String CONFIG_VILLAGE_SPAWN_RATE_TOOLTIP = "config.millenaire_rewrite.village_spawn_rate.tooltip";
    
    // 进度和成就
    public static final String ADVANCEMENT_FIRST_VILLAGE = "advancement.millenaire_rewrite.first_village";
    public static final String ADVANCEMENT_MASTER_TRADER = "advancement.millenaire_rewrite.master_trader";
    
    // 实体相关
    public static final String ENTITY_NORMAN_VILLAGER = "entity.millenaire_rewrite.norman_villager";
    public static final String ENTITY_VILLAGER_PROFESSION_FARMER = "profession.millenaire_rewrite.farmer";
    public static final String ENTITY_VILLAGER_PROFESSION_BLACKSMITH = "profession.millenaire_rewrite.blacksmith";
}
```

#### 6.3.2 多语言支持注释

```java
/**
 * 获取本地化的村庄信息文本。
 * 
 * <p>此方法会根据客户端语言设置返回适当的文本。
 * 支持参数化翻译，可以插入动态数值。
 * 
 * @param village 村庄数据
 * @param player 请求信息的玩家（用于确定语言）
 * @return 格式化的本地化文本
 * 
 * @implNote 翻译键格式: "info.millenaire_rewrite.village.{info_type}"
 * @see MillenaireTranslationKeys
 */
public Component getVillageInfoText(VillageData village, Player player) {
    // 获取玩家语言设置
    String locale = getPlayerLocale(player);
    
    // 构建带参数的翻译文本
    // 格式: "Village: {name}, Population: {population}, Happiness: {happiness}%"
    return Component.translatable(
        MillenaireTranslationKeys.GUI_VILLAGE_INFO_FULL,
        village.getName(),                              // {name}
        village.getPopulation(),                        // {population}  
        Math.round(village.getHappiness() * 100)        // {happiness}
    );
}
```

---

## 7. 代码审查检查清单

在提交代码前，请确保满足以下所有条件：

### 7.1 基础检查

- [ ] 所有类和方法都有适当的访问修饰符
- [ ] 没有未使用的导入和变量
- [ ] 所有字符串都已本地化（无硬编码文本）
- [ ] 使用了正确的命名约定（camelCase, PascalCase, snake_case）
- [ ] 注册项名称与代码标识符一致

### 7.2 Forge特定检查

- [ ] 使用DeferredRegister进行所有注册
- [ ] 网络包正确处理客户端/服务端分离
- [ ] 事件监听器正确注册和注销
- [ ] 配置使用ForgeConfigSpec
- [ ] 数据生成器覆盖所有内容

### 7.3 性能检查

- [ ] Tick方法不包含昂贵操作
- [ ] 使用适当的缓存机制
- [ ] 正确处理并发访问
- [ ] 避免内存泄漏（正确注销监听器）
- [ ] 使用对象池处理频繁创建的对象

### 7.4 文档检查

- [ ] 公共API包含完整JavaDoc
- [ ] 复杂算法有详细注释
- [ ] 迁移代码有适当标记
- [ ] 性能关键代码有解释注释

### 7.5 兼容性检查

- [ ] 向后兼容1.12.2存档格式
- [ ] 配置迁移逻辑完整
- [ ] 错误处理提供有用信息
- [ ] 日志消息清晰且包含上下文

---

## 8. 开发工具与环境

### 8.1 推荐IDE设置

**IDE**: IntelliJ IDEA（推荐）或Eclipse

**必需插件**:
- Minecraft Development (IntelliJ)
- Lombok Plugin
- SonarLint（代码质量检查）

**代码风格设置**:
```xml
<!-- intellij-java-google-style.xml -->
<code_scheme name="GoogleStyle">
  <option name="INDENT_SIZE" value="4"/>
  <option name="TAB_SIZE" value="4"/>
  <option name="USE_TAB_CHARACTER" value="false"/>
</code_scheme>
```

### 8.2 构建和测试

**Gradle任务**:
```bash
# 构建模组
./gradlew build

# 运行客户端测试
./gradlew runClient

# 运行服务端测试  
./gradlew runServer

# 生成数据
./gradlew runData

# 代码质量检查
./gradlew check
```

---

## 结语

本规范文档是Millenaire Rewrite项目的重要参考，所有参与开发的AI和人类开发者都必须严格遵守。

通过遵循这些规范，我们能够：
- 保持代码质量和一致性
- 提高开发效率和可维护性  
- 确保模组的稳定性和性能
- 为未来的扩展奠定坚实基础

如有疑问或需要修改规范，请通过项目的Issue系统提出建议。

**版本**: 1.0.0  
**最后更新**: 2025年9月9日  
**维护者**: JasonCian
```
```
