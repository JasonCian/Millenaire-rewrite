# 数据生成框架使用指南

## 概述

千年村庄重写版模组已经建立了完整的数据生成框架，遵循现代 Minecraft Forge 1.20.1 开发标准。所有游戏数据（物品模型、方块状态、配方、语言文件等）都必须通过 DataProvider 生成，禁止手动编写 JSON 文件。

## 框架结构

### 核心组件

```
datagen/
├── DataGenerators.java                     # 主数据生成事件处理器
├── MillenaireBlockStateProvider.java       # 方块状态和模型生成器
├── MillenaireItemModelProvider.java        # 物品模型生成器
├── MillenaireTexturePlaceholderProvider.java # 纹理占位文件生成器 🆕
├── MillenaireLanguageProvider.java         # 语言文件生成器（中英双语）
├── MillenaireRecipeProvider.java           # 配方生成器
├── MillenaireLootTableProvider.java        # 战利品表包装器
├── MillenaireBlockLootTables.java          # 方块战利品表生成器
├── MillenaireBlockTagsProvider.java        # 方块标签生成器
└── MillenaireItemTagsProvider.java         # 物品标签生成器
```

### 特性

- ✅ **完整覆盖**: 涵盖方块、物品、配方、战利品、标签、语言文件等所有数据类型
- ✅ **纹理占位**: 自动生成纹理占位文件，为纹理开发提供基础 🆕
- ✅ **中英双语**: 内置中文和英文本地化支持
- ✅ **文化系统准备**: 为多文化系统预留了扩展接口
- ✅ **模块化设计**: 每个生成器独立管理其职责范围
- ✅ **符合规范**: 严格遵循项目编码标准和命名规范

## 运行数据生成

### 方法一：Gradle 命令

```bash
# 在项目根目录运行
./gradlew runData
```

### 方法二：IDE 运行配置

在 IntelliJ IDEA 或 Eclipse 中运行 `runData` 配置

### 生成的文件位置

生成的数据文件将保存在：
```
src/generated/resources/
├── assets/millenaire_rewrite/
│   ├── blockstates/          # 方块状态
│   ├── models/
│   │   ├── block/            # 方块模型
│   │   └── item/             # 物品模型
│   ├── textures/             # 纹理占位文件 🆕
│   │   ├── item/             # 物品纹理占位 (16x16)
│   │   ├── block/            # 方块纹理占位 (16x16)
│   │   └── gui/              # GUI纹理占位 (256x256)
│   └── lang/                 # 语言文件
│       ├── en_us.json        # 英文翻译
│       └── zh_cn.json        # 中文翻译
└── data/millenaire_rewrite/
    ├── loot_tables/          # 战利品表
    ├── recipes/              # 配方
    └── tags/                 # 标签
        ├── blocks/           # 方块标签
        └── items/            # 物品标签
```

## 纹理占位文件功能 🆕

DataGen框架现在包含了**纹理占位文件生成器**，能够：

- 🎨 **自动生成纹理占位**: 为所有物品、方块和GUI生成带标识的占位纹理
- 🌈 **颜色编码系统**: 不同文化使用不同颜色主题（诺曼-钢蓝、日本-深红、拜占庭-紫色、华夏-中国红）
- 📏 **多尺寸支持**: 16x16物品/方块纹理，256x256 GUI纹理
- 🏷️ **智能标识**: 每个占位文件包含清晰的文字标识，方便识别
- 🎯 **高对比度**: 自动选择与背景对比的文字颜色

生成的纹理占位文件可以直接用作纹理开发的起始点，大大提升纹理制作效率！

详细的纹理开发指南请参考：`reference/Texture_Development_Guide.md`

## 使用示例

### 添加新物品模型

在 `MillenaireItemModelProvider.java` 中：

```java
@Override
protected void registerModels() {
    // 在 generateCultureItems() 方法中添加
    simpleItem(ModItems.CULTURE_SCROLL);
    simpleItem(ModItems.DENIER);
    toolItem(ModItems.NORMAN_SWORD);
}
```

### 添加新方块状态

在 `MillenaireBlockStateProvider.java` 中：

```java
@Override
protected void registerStatesAndModels() {
    // 在 generateCultureBlocks() 方法中添加
    simpleBlockWithItem(ModBlocks.VILLAGE_STONE.get(), 
                       cubeAll(ModBlocks.VILLAGE_STONE.get()));
}
```

### 添加新配方

在 `MillenaireRecipeProvider.java` 中：

```java
private void generateCultureRecipes(Consumer<FinishedRecipe> consumer) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CULTURE_SCROLL.get())
        .requires(Items.PAPER)
        .requires(Items.INK_SAC)
        .requires(Items.FEATHER)
        .unlockedBy("has_paper", has(Items.PAPER))
        .save(consumer);
}
```

### 添加新翻译

在 `MillenaireLanguageProvider.java` 中：

```java
private void addEnglishTranslations() {
    addItem(ModItems.CULTURE_SCROLL, "Culture Scroll");
    addBlock(ModBlocks.VILLAGE_STONE, "Village Stone");
}

private void addChineseTranslations() {
    addItem(ModItems.CULTURE_SCROLL, "文化卷轴");
    addBlock(ModBlocks.VILLAGE_STONE, "村庄石材");
}
```

## 注意事项

### 当前状态

- ✅ **框架完成**: 数据生成框架已完全搭建
- ⚠️ **等待内容**: 由于物品和方块注册系统尚未实现，具体的数据生成代码被注释
- 🔄 **渐进实现**: 随着物品、方块、实体系统的实现，将逐步激活对应的数据生成代码

### 开发流程

1. **实现注册**: 先在 `ModItems`、`ModBlocks` 等类中注册新内容
2. **添加生成代码**: 在对应的 DataProvider 中添加生成逻辑
3. **运行数据生成**: 执行 `runData` 任务生成 JSON 文件
4. **验证结果**: 检查生成的文件是否正确

### 扩展指南

框架已为以下系统预留扩展接口：

- **文化系统**: 各 Provider 中的 `generateCultureXxx()` 方法
- **建筑系统**: 各 Provider 中的 `generateBuildingXxx()` 方法
- **货币系统**: 各 Provider 中的 `generateCurrencyXxx()` 方法
- **工具系统**: 各 Provider 中的 `generateToolXxx()` 方法
- **装饰系统**: 各 Provider 中的 `generateDecorativeXxx()` 方法

## 后续计划

1. **Phase 1**: 实现基础物品和方块注册系统
2. **Phase 2**: 激活对应的数据生成代码
3. **Phase 3**: 扩展文化特色数据生成
4. **Phase 4**: 完善华夏文化相关数据生成

数据生成框架现已准备就绪，等待物品和方块系统的实现！
