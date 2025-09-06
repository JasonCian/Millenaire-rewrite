# 🏗️ Millenaire Rewrite - 增强型方块系统设计

## 📌 设计理念

基于CODE_STYLE_AND_DEVELOPMENT_STANDARDS和当前系统分析，设计一个更高效、模块化、现代化的方块系统。

### 核心改进点

1. **统一的基础建筑方块系统** - 将所有基础装饰方块统合为一类
2. **自动变体衍生** - 为每个基础方块自动生成楼梯、台阶、墙等变体
3. **文化分类管理** - 按文化体系组织方块，便于维护
4. **性能优化** - 减少重复代码，提高注册和渲染效率
5. **类型安全** - 使用强类型泛型系统确保类型安全

---

## 🏛️ 新架构设计

### 1. 核心架构层次

```
基础建筑方块系统 (BuildingBlockSystem)
├── 文化方块组 (CulturalBlockGroup)
│   ├── 基础方块 (BaseBlock)
│   ├── 楼梯方块 (StairsBlock)  [自动生成]
│   ├── 台阶方块 (SlabBlock)    [自动生成]
│   ├── 墙方块 (WallBlock)      [自动生成]
│   └── 栅栏方块 (FenceBlock)   [自动生成]
└── 特殊功能方块 (FunctionalBlocks)
    ├── 村庄石 (VillageStone)
    ├── 贸易桌 (MillChest)
    └── 农业方块 (Agricultural)
```

### 2. 包结构重新设计

```
com.jasoncian.millenaire_rewrite.blocks/
├── BuildingBlockRegistry.java         # 新的统一注册器
├── system/                            # 方块系统核心
│   ├── BuildingBlockSet.java         # 方块组合类
│   ├── CulturalBlockFamily.java      # 文化方块族
│   ├── BlockVariantGenerator.java    # 变体自动生成器
│   └── MaterialDefinition.java       # 材料定义
├── base/                              # 基础方块类
│   ├── BaseBuildingBlock.java        # 基础建筑方块
│   ├── CulturalStairsBlock.java      # 文化楼梯方块
│   ├── CulturalSlabBlock.java        # 文化台阶方块
│   └── CulturalWallBlock.java        # 文化墙方块
├── cultural/                          # 按文化分类的方块组
│   ├── norman/                        # 诺曼文化
│   ├── byzantine/                     # 拜占庭文化
│   ├── indian/                        # 印度文化
│   ├── japanese/                      # 日本文化
│   ├── mayan/                         # 玛雅文化
│   └── inuit/                         # 因纽特文化
└── functional/                        # 功能方块（保持现有结构）
    ├── VillageStoneBlock.java
    └── ...
```

---

## 🎯 核心类设计

### 1. 材料定义系统

```java
/**
 * 建筑材料定义 - 定义所有基础建筑材料的属性
 */
public enum BuildingMaterial {
    // 石质材料
    STONE_BASIC("stone_basic", MapColor.STONE, 2.0F, 6.0F, SoundType.STONE, true),
    STONE_CARVED("stone_carved", MapColor.STONE, 2.5F, 7.0F, SoundType.STONE, true),
    COOKED_BRICK("cooked_brick", MapColor.COLOR_ORANGE, 2.0F, 6.0F, SoundType.STONE, true),
    MUD_BRICK("mud_brick", MapColor.DIRT, 1.5F, 4.0F, SoundType.MUD_BRICKS, true),
    
    // 木质材料  
    TIMBER_FRAME("timber_frame", MapColor.WOOD, 1.0F, 3.0F, SoundType.WOOD, false),
    THATCH("thatch", MapColor.COLOR_YELLOW, 0.5F, 1.0F, SoundType.GRASS, false),
    
    // 特殊材料
    GALIANITE("galianite", MapColor.COLOR_PURPLE, 5.0F, 12.0F, SoundType.METAL, true),
    PAINTED_BRICK("painted_brick", MapColor.COLOR_RED, 2.0F, 6.0F, SoundType.STONE, true);
    
    // 材料属性...
}
```

### 2. 文化方块族定义

```java
/**
 * 文化方块族 - 按文化组织方块集合
 */
public enum CulturalBlockFamily {
    NORMAN("norman", "诺曼文化",
        BuildingMaterial.TIMBER_FRAME,
        BuildingMaterial.THATCH,
        BuildingMaterial.COOKED_BRICK,
        BuildingMaterial.STONE_CARVED
    ),
    
    BYZANTINE("byzantine", "拜占庭文化", 
        BuildingMaterial.STONE_CARVED,
        BuildingMaterial.PAINTED_BRICK,
        BuildingMaterial.GALIANITE
    ),
    
    INDIAN("indian", "印度文化",
        BuildingMaterial.MUD_BRICK,
        BuildingMaterial.PAINTED_BRICK,
        BuildingMaterial.STONE_BASIC
    ),
    
    JAPANESE("japanese", "日本文化",
        BuildingMaterial.TIMBER_FRAME,
        BuildingMaterial.THATCH,
        BuildingMaterial.STONE_BASIC
    ),
    
    MAYAN("mayan", "玛雅文化",
        BuildingMaterial.STONE_CARVED,
        BuildingMaterial.PAINTED_BRICK
    ),
    
    INUIT("inuit", "因纽特文化",
        BuildingMaterial.STONE_BASIC
    );
    
    private final String name;
    private final String displayName;
    private final BuildingMaterial[] materials;
    
    // 构造函数和方法...
}
```

### 3. 方块组合类

```java
/**
 * 建筑方块组合 - 包含一个基础方块的所有变体
 */
public class BuildingBlockSet {
    private final String baseName;
    private final BuildingMaterial material;
    private final CulturalBlockFamily culture;
    
    // 方块注册对象
    private final RegistryObject<Block> baseBlock;
    private final RegistryObject<StairBlock> stairsBlock;
    private final RegistryObject<SlabBlock> slabBlock;
    private final RegistryObject<WallBlock> wallBlock;
    private final RegistryObject<FenceBlock> fenceBlock; // 仅木质材料
    
    // 对应的方块物品
    private final RegistryObject<Item> baseBlockItem;
    private final RegistryObject<Item> stairsBlockItem;
    private final RegistryObject<Item> slabBlockItem;
    private final RegistryObject<Item> wallBlockItem;
    private final RegistryObject<Item> fenceBlockItem;
    
    /**
     * 构造函数 - 自动注册所有变体
     */
    public BuildingBlockSet(String baseName, BuildingMaterial material, CulturalBlockFamily culture) {
        this.baseName = baseName;
        this.material = material;
        this.culture = culture;
        
        // 自动注册所有方块变体
        this.baseBlock = registerBaseBlock();
        this.stairsBlock = registerStairsBlock();
        this.slabBlock = registerSlabBlock();
        this.wallBlock = registerWallBlock();
        this.fenceBlock = material.isWooden() ? registerFenceBlock() : null;
        
        // 自动注册所有物品变体
        this.baseBlockItem = registerBaseBlockItem();
        this.stairsBlockItem = registerStairsBlockItem();
        this.slabBlockItem = registerSlabBlockItem();
        this.wallBlockItem = registerWallBlockItem();
        this.fenceBlockItem = material.isWooden() ? registerFenceBlockItem() : null;
    }
    
    // 私有注册方法...
}
```

### 4. 统一注册器

```java
/**
 * 建筑方块注册器 - 统一管理所有建筑方块的注册
 */
public class BuildingBlockRegistry {
    
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, MillenaireRewrite.MOD_ID);
    
    public static final DeferredRegister<Item> BLOCK_ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, MillenaireRewrite.MOD_ID);
    
    // 所有方块组合的映射
    private static final Map<String, BuildingBlockSet> BLOCK_SETS = new HashMap<>();
    
    // ================ 自动注册系统 ================
    
    static {
        // 按文化自动注册所有方块组合
        for (CulturalBlockFamily culture : CulturalBlockFamily.values()) {
            for (BuildingMaterial material : culture.getMaterials()) {
                String blockName = culture.getName() + "_" + material.getName();
                BLOCK_SETS.put(blockName, new BuildingBlockSet(blockName, material, culture));
            }
        }
    }
    
    /**
     * 获取指定的方块组合
     */
    public static BuildingBlockSet getBlockSet(String name) {
        return BLOCK_SETS.get(name);
    }
    
    /**
     * 获取指定文化的所有方块组合
     */
    public static List<BuildingBlockSet> getBlockSetsForCulture(CulturalBlockFamily culture) {
        return BLOCK_SETS.values().stream()
            .filter(set -> set.getCulture() == culture)
            .collect(Collectors.toList());
    }
    
    /**
     * 注册所有方块到模组事件总线
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
    }
}
```

---

## 🎨 创造模式标签页重新设计

### 按文化分类的标签页

```java
/**
 * 增强的创造模式标签页 - 按文化和功能分类
 */
public class EnhancedCreativeTabs {
    
    // 文化建筑标签页
    public static final RegistryObject<CreativeModeTab> NORMAN_BUILDING = CREATIVE_MODE_TABS.register("norman_building",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.norman_building"))
            .icon(() -> BuildingBlockRegistry.getBlockSet("norman_timber_frame").getBaseBlockItem().get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                // 自动添加诺曼文化的所有建筑方块
                BuildingBlockRegistry.getBlockSetsForCulture(CulturalBlockFamily.NORMAN)
                    .forEach(set -> set.addToCreativeTab(output));
            })
            .build());
    
    // 其他文化标签页...
}
```

---

## 🔧 技术优势

### 1. 代码复用
- 一套代码生成多种变体（楼梯、台阶、墙等）
- 统一的属性管理和材质系统
- 自动化的注册流程

### 2. 性能优化
- 减少重复的方块类
- 统一的渲染逻辑
- 优化的内存使用

### 3. 易于维护
- 新增材料只需在枚举中定义
- 自动生成所有必要的变体
- 集中化的属性管理

### 4. 类型安全
- 强类型的材料和文化定义
- 编译时检查所有引用
- 防止运行时错误

### 5. 扩展性
- 新文化只需添加枚举条目
- 新变体类型易于扩展
- 支持动态配置

---

## 📋 实施计划

### 阶段1：核心系统实现 (1-2天)
1. 创建材料定义系统
2. 实现文化方块族
3. 构建方块组合类
4. 建立统一注册器

### 阶段2：基础方块迁移 (2-3天)
1. 迁移现有的装饰方块到新系统
2. 实现自动变体生成
3. 更新创造模式标签页
4. 生成对应的数据文件

### 阶段3：物品系统集成 (1-2天)
1. 更新ModBlockItems使用新系统
2. 实现自动物品注册
3. 更新本地化文件
4. 测试所有变体

### 阶段4：优化和完善 (1天)
1. 性能优化
2. 代码审查
3. 文档更新
4. 测试验证

---

## 🎯 预期成果

1. **方块数量**: 从现在的~10个基础方块扩展到60+个变体方块
2. **代码量**: 减少50%的重复代码
3. **维护性**: 新增文化或材料的工作量减少80%
4. **性能**: 注册和渲染性能提升20%
5. **扩展性**: 支持轻松添加新的文化和材料类型

---

**📋 文档版本**: 1.0.0  
**创建日期**: 2025年9月6日  
**设计目标**: 建立现代化、高效、可扩展的方块系统架构
