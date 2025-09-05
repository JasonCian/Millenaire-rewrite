# 🎯 Millenaire Rewrite 1.20.1 - 代码规范与开发标准

## 📌 强制参考文档 - AI 开发助手必读

> ⚠️ **重要提醒**：本文档是 Millenaire Rewrite 项目的核心开发规范，所有 AI 开发助手和开发者都必须严格遵守这些标准，以确保代码风格的一致性和项目架构的完整性。

---

## 🏗️ 项目基础信息

### 📊 当前实现状态（更新于 2025 年 9 月 5 日）

#### ✅ 已完成系统

- **翻译系统**: 100% 完成 - 双语支持（英文/中文各 249 个条目）完全同步
- **创造模式标签页**: 100% 完成 - 8 个文化分类标签页全部正常工作
- **物品系统**: 100% 完成 - 所有文化物品（货币、食物、工具、材料）已注册
- **装饰方块系统**: 100% 完成 - 所有装饰方块和纹理实现
- **数据生成**: 100% 完成 - 语言文件、模型、纹理自动生成

#### 🔄 进行中系统

- **功能性方块**: 30% 完成 - Village Stone 基础实现，Mill Chest 需要完善

## 开发指导原则

1. **功能逻辑** - 参考 legacy 代码了解要实现什么功能
2. **实现方式** - 参考 example 代码了解如何用现代 API 实现
3. **资源格式** - 参考 legacy 资源了解材质和模型的组织方式
4. **不要直接复制** - 所有代码都需要用现代方式重新实现
5. **保持模块化** - 新代码应该更加模块化和可维护

### 基本配置

- **Minecraft 版本**: 1.20.1
- **Forge 版本**: 47.4.3
- **Java 版本**: 17
- **主包命名**: `com.jasoncian.millenaire_rewrite`
- **Mod ID**: `millenaire_rewrite`
- **Mod 名称**: `Millenaire Rewrite`
- **版本号**: `0.1.3-alpha`

### 项目目标

通过干净、现代的代码库为 Minecraft 1.20.1 带来身临其境的村庄体验，重制经典的 Millenaire mod，采用最新的 Forge API 和现代 Java 开发实践。

---

## 📁 包结构标准

### 核心包结构

```
com.jasoncian.millenaire_rewrite/
├── MillenaireRewrite.java          # 主类
├── core/                           # 核心注册器
│   ├── ModBlocks.java             # 方块注册
│   ├── ModItems.java              # 物品注册
│   ├── ModBlockItems.java         # 方块物品注册
│   ├── ModEntities.java           # 实体注册
│   ├── ModBlockEntities.java      # 方块实体注册
│   ├── ModFoodProperties.java     # 食物属性
│   ├── ModToolMaterials.java      # 工具材料
│   ├── ModArmorMaterials.java     # 护甲材料
│   └── MillCreativeTabs.java      # 创造模式标签页
├── config/                         # 配置管理
│   └── MillenaireConfig.java      # 主配置文件
├── blocks/                         # 方块实现
│   ├── decorative/                # 装饰方块
│   └── functional/                # 功能方块
├── blockentities/                  # 方块实体实现
│   └── VillageStoneBlockEntity.java
├── items/                          # 物品实现
│   ├── ItemMillParchment.java     # 羊皮纸物品
│   ├── ItemMillPurse.java         # 钱包物品
│   ├── ItemVillageSign.java       # 村庄标志物品
│   ├── magic/                     # 魔法物品
│   ├── tools/                     # 工具类物品
│   └── blocks/                    # 方块物品
│       └── DecorativeBlockItem.java
├── containers/                     # 容器实现
├── menus/                          # 菜单实现
├── entity/                         # 实体实现
├── village/                        # 村庄系统
├── building/                       # 建筑系统
├── client/                         # 客户端专用
│   ├── ClientColorHandlers.java   # 颜色处理器
│   └── gui/                       # 图形界面
│       └── ParchmentScreen.java   # 羊皮纸界面
├── datagen/                        # 数据生成器
│   ├── DataGenerators.java        # 数据生成器主类
│   ├── ModLanguageProvider.java   # 语言文件生成
│   ├── ModBlockStateProvider.java # 方块状态生成
│   ├── ModItemModelProvider.java  # 物品模型生成
│   ├── ModBlockItemModelProvider.java # 方块物品模型生成
│   ├── ModLootTableProvider.java  # 战利品表生成
│   └── ModBlockLootTables.java    # 方块战利品表
├── data/                           # 数据处理
│   └── ParchmentContentData.java  # 羊皮纸内容数据
└── util/                          # 工具类
    └── CurrencyUtils.java         # 货币工具类
```

### 包命名规则

- 使用小写字母和下划线
- 功能相关的类组织在对应包中
- 核心注册器统一放在 `core`包
- 客户端代码独立在 `client`包

---

## 🎨 代码风格规范

### 1. 类命名规范

#### 1.1 核心注册器类

```java
// 正确示例
public class ModBlocks { }          // 方块注册器
public class ModItems { }           // 物品注册器
public class ModBlockItems { }      // 方块物品注册器
public class ModEntities { }        // 实体注册器
```

#### 1.2 实现类命名

```java
// 方块类
public class VillageStoneBlock { }      // 功能方块
public class DecorativeStoneBlock { }   // 装饰方块

// 物品类
public class ItemMillPurse { }          // 特殊物品前缀Item
public class DynamicAmuletItem { }      // 魔法物品后缀Item
public class DecorativeBlockItem<T> { } // 泛型方块物品

// 实体类
public class EntityMillVillager { }     // 实体前缀Entity
```

### 2. 变量命名规范

#### 2.1 注册器对象

```java
// DeferredRegister命名
public static final DeferredRegister<Block> BLOCKS =
    DeferredRegister.create(ForgeRegistries.BLOCKS, MillenaireRewrite.MOD_ID);

public static final DeferredRegister<Item> ITEMS =
    DeferredRegister.create(ForgeRegistries.ITEMS, MillenaireRewrite.MOD_ID);
```

#### 2.2 RegistryObject 命名

```java
// 使用大写下划线，描述性命名
public static final RegistryObject<Item> DENIER = ITEMS.register("denier",
    () -> new Item(new Item.Properties())
);

public static final RegistryObject<Item> DENIER_OR = ITEMS.register("denier_or",
    () -> new Item(new Item.Properties())
);

public static final RegistryObject<Block> VILLAGE_STONE = BLOCKS.register("village_stone",
    VillageStoneBlock::new);
```

#### 2.3 NBT 标签常量

```java
// NBT标签使用小写下划线，前缀NBT_
private static final String NBT_COPPER_DENIERS = "copper_deniers";
private static final String NBT_SILVER_DENIERS = "silver_deniers";
private static final String NBT_GOLD_DENIERS = "gold_deniers";
```

### 3. 注释规范

#### 3.1 类级注释（必须包含）

```java
/**
 * 类功能简述 - 技术实现要点
 *
 * 详细描述类的作用和实现方式
 * 可包含设计理念、技术特点等
 *
 * 功能特性：
 * - 功能点1
 * - 功能点2
 * - 功能点3
 *
 * @author JasonCian（可选）
 * @version 版本号（可选）
 */
```

#### 3.2 方法注释规范

```java
/**
 * 方法功能描述
 *
 * @param parameter 参数说明
 * @return 返回值说明
 */
public ReturnType methodName(ParameterType parameter) {
    // 实现
}
```

#### 3.3 字段注释规范

```java
/** 字段功能简述 - 用途说明 */
public static final RegistryObject<Item> DENIER = ITEMS.register("denier",
    () -> new Item(new Item.Properties())
);
```

#### 3.4 分区注释规范

```java
// ================ 功能分区名称 ================

/** 具体物品描述 - 功能说明 */
public static final RegistryObject<Item> ITEM_NAME = ITEMS.register("item_name",
    () -> new Item(new Item.Properties())
);
```

### 4. 代码组织规范

#### 4.1 导入顺序

```java
// 1. 同项目包导入
import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.core.ModItems;

// 2. Minecraft原版导入
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// 3. Forge导入
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

// 4. Java标准库导入
import java.util.List;
import javax.annotation.Nullable;
```

#### 4.2 类内容组织顺序

```java
public class ExampleClass {
    // 1. 常量定义
    public static final String MOD_ID = "millenaire_rewrite";
    private static final String NBT_TAG = "example_tag";

    // 2. 静态字段（注册器、RegistryObject等）
    public static final DeferredRegister<Item> ITEMS = ...;
    public static final RegistryObject<Item> EXAMPLE_ITEM = ...;

    // 3. 实例字段
    private int instanceField;

    // 4. 构造方法
    public ExampleClass(Properties properties) {
        super(properties);
    }

    // 5. 公共方法
    public void publicMethod() { }

    // 6. 私有方法
    private void privateMethod() { }

    // 7. 静态方法（通常是注册方法）
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
```

---

## 🏛️ 架构设计标准

### 1. 现代化设计原则

#### 1.1 分离关注点

```java
// 正确：方块逻辑与物品逻辑分开
public class ModBlocks {           // 只注册方块
    public static final RegistryObject<Block> DECORATIVE_STONE = ...;
}

public class ModBlockItems {       // 只注册方块物品
    public static final RegistryObject<Item> DECORATIVE_STONE_ITEM = ...;
}
```

#### 1.2 变体独立性

```java
// 正确：每个装饰方块变体都有独立的物品注册
public static final RegistryObject<Item> GOLD_ORNAMENT = BLOCK_ITEMS.register("gold_ornament",
    () -> new DecorativeBlockItem<>(
        ModBlocks.DECORATIVE_STONE,
        StoneDecorativeVariant.GOLD_ORNAMENT,
        new Item.Properties()
    )
);
```

#### 1.3 类型安全

```java
// 正确：使用泛型确保类型安全
public class DecorativeBlockItem<T extends Enum<T>> extends BlockItem {
    private final T variant;
    // 实现...
}
```

### 2. DeferredRegister 使用规范

#### 2.1 注册器创建

```java
// 标准模式
public static final DeferredRegister<ItemType> REGISTRY_NAME =
    DeferredRegister.create(ForgeRegistries.REGISTRY_TYPE, MillenaireRewrite.MOD_ID);
```

#### 2.2 对象注册

```java
// 简单对象注册
public static final RegistryObject<Item> SIMPLE_ITEM = ITEMS.register("simple_item",
    () -> new Item(new Item.Properties())
);

// 复杂对象注册（使用lambda表达式）
public static final RegistryObject<Item> COMPLEX_ITEM = ITEMS.register("complex_item",
    () -> new ComplexItem(new Item.Properties().stacksTo(16))
);

// 方块注册（使用方法引用）
public static final RegistryObject<Block> SIMPLE_BLOCK = BLOCKS.register("simple_block",
    SimpleBlock::new);
```

#### 2.3 注册方法

```java
/**
 * 注册所有[类型]到模组事件总线
 *
 * @param eventBus 模组事件总线
 */
public static void register(IEventBus eventBus) {
    REGISTRY_NAME.register(eventBus);
}
```

### 3. 配置系统规范

#### 3.1 配置类结构

```java
@Mod.EventBusSubscriber(modid = MillenaireRewrite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MillenaireConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // 配置项定义
    public static final ForgeConfigSpec.BooleanValue CONFIG_OPTION;

    static {
        BUILDER.comment("配置分组说明").push("config_group");

        CONFIG_OPTION = BUILDER
            .comment("配置项说明")
            .define("config_key", default_value);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
```

---

## 🎮 游戏内容标准

### 1. 本地化规范

#### 1.1 key 命名规则

```json
{
  "item.millenaire_rewrite.denier": "Copper Denier",
  "item.millenaire_rewrite.denier_or": "Gold Denier",
  "block.millenaire_rewrite.village_stone": "Village Stone",
  "creativetab.millenaire_rewrite": "Millenaire Rewrite"
}
```

#### 1.2 支持语言

- 英语（en_us）- 默认语言
- 简体中文（zh_cn）- 完整支持

### 2. 物品属性标准

#### 2.1 堆叠数量规范

```java
// 普通物品：默认64
new Item.Properties()

// 工具和武器：1个
new Item.Properties().stacksTo(1)

// 特殊物品：根据逻辑需要
new Item.Properties().stacksTo(16)  // 羊皮纸等
```

#### 2.2 食物属性

```java
// 在ModFoodProperties中定义
public static final FoodProperties EXAMPLE_FOOD = new FoodProperties.Builder()
    .nutrition(nutrition_value)      // 营养值
    .saturationMod(saturation_mod)   // 饱食度倍数
    .build();

// 在物品注册中使用
() -> new Item(new Item.Properties().food(ModFoodProperties.EXAMPLE_FOOD))
```

### 3. 方块属性标准

#### 3.1 基础属性

```java
// 装饰方块
public ExampleBlock() {
    super(BlockBehaviour.Properties.of(Material.STONE)
        .strength(hardness, resistance)
        .sound(SoundType.STONE)
        .requiresCorrectToolForDrops()
    );
}
```

---

## 🔧 技术实现标准

### 1. NBT 数据处理

#### 1.1 常量定义

```java
private static final String NBT_KEY_NAME = "key_name";
```

#### 1.2 数据保存

```java
@Override
public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putInt(NBT_KEY_NAME, this.value);
    return tag;
}
```

#### 1.3 数据加载

```java
@Override
public void deserializeNBT(CompoundTag tag) {
    if (tag.contains(NBT_KEY_NAME)) {
        this.value = tag.getInt(NBT_KEY_NAME);
    }
}
```

### 2. 事件处理

#### 2.1 事件监听器注册

```java
@Mod.EventBusSubscriber(modid = MillenaireRewrite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent
    public static void onEventName(EventType event) {
        // 事件处理逻辑
    }
}
```

### 3. 客户端代码分离

#### 3.1 客户端事件处理

```java
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public static class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 客户端初始化
    }
}
```

---

## 📝 文档规范

### 1. 开发报告格式

每完成一个功能模块，需要创建对应的实现报告：

- 文件命名：`[功能名称]_IMPLEMENTATION_REPORT.md`
- 包含：功能概述、技术实现、测试结果、已知问题

### 2. 更新日志格式

```markdown
## [版本号] - 日期

### 新增

- 新功能描述

### 修改

- 修改内容描述

### 修复

- 修复的 bug 描述

### 移除

- 移除的功能描述
```

---

## ⚡ 性能优化标准

### 1. 代码优化

- 避免在 tick 方法中进行重计算
- 使用缓存减少重复计算
- 合理使用静态方法和字段

### 2. 内存管理

- 及时释放不需要的对象引用
- 使用对象池处理频繁创建的对象
- 避免内存泄漏

### 3. 网络优化

- 减少不必要的数据包发送
- 使用批量操作减少网络调用
- 合理使用客户端预测

---

## 🚨 重要提醒

### 对 AI 开发助手的要求

1. **严格遵守包结构**：所有新建类必须放在正确的包中
2. **保持命名一致性**：遵循已建立的命名规范
3. **注释完整性**：每个类和公共方法都必须有完整注释
4. **代码风格统一**：格式化、缩进、空行等保持一致
5. **功能分离原则**：不要将不相关的功能混在一个类中
6. **现代 API 使用**：优先使用 1.20.1 的现代 API，避免过时方法

### 禁止事项

- ❌ 不得修改已确定的包结构
- ❌ 不得使用过时的 API（除非注释说明原因）
- ❌ 不得创建未经规划的新包
- ❌ 不得破坏现有的注册系统结构
- ❌ 不得忽略代码注释和文档

### 强制要求

- ✅ 所有新代码必须包含完整的 JavaDoc 注释
- ✅ 所有常量必须使用正确的命名规范
- ✅ 所有注册对象必须遵循既定的注册模式
- ✅ 所有配置项必须在 MillenaireConfig 中定义
- ✅ 所有本地化字符串必须在语言文件中定义

---

## 📚 参考资源

### 现有代码示例

- `ModItems.java` - 物品注册标准实现
- `ModBlockItems.java` - 方块物品注册标准实现
- `ItemMillPurse.java` - 复杂物品实现示例
- `MillenaireRewrite.java` - 主类结构示例

### 技术文档

- [Minecraft Forge 1.20.1 文档](https://docs.minecraftforge.net/en/1.20.1/)
- 项目 reference 目录中的 legacy 代码示例-reference\OldSource
- 项目迁移的具体参考条例 reference\迁移计划-AI 总结
- `reference\CURRENT_DEVELOPMENT_PLAN.md` - 当前开发计划

---

**📋 版本信息**

- 文档版本：1.0.1
- 创建日期：2025 年 9 月 5 日
- 适用版本：Millenaire Rewrite 0.1.3-alpha
- 更新频率：随项目发展动态更新

> 💡 **提示**：本文档将随着项目的发展持续更新，请开发者和 AI 助手定期查阅最新版本。
