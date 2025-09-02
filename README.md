# 🏰 Millenaire Rewrite | 千年村庄重制版

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-green.svg)](https://minecraft.net)
[![Forge](https://img.shields.io/badge/Forge-47.4.3-orange.svg)](https://files.minecraftforge.net)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.txt)
[![Development](https://img.shields.io/badge/Status-Alpha%20Development-red.svg)](#development-status)

[English](#english) | [中文](#中文)

---

## English

### 📖 About

**Millenaire Rewrite** is a complete modernization of the classic Millenaire mod for Minecraft 1.20.1. This project brings the beloved village-building and cultural experience to modern Minecraft with enhanced performance, updated mechanics, and a cleaner codebase.

Originally created as one of Minecraft's most ambitious civilization mods, Millenaire adds living, breathing villages with unique cultures that grow and evolve over time. This rewrite preserves the core spirit while embracing modern modding standards.

btw,I did not find the open source code library for 1.12. The reference code of the project is forked from https://github.com/MoonCutter2B/Millenaire seems to be version 1.8.

### ✨ Key Features

### Core Systems

- **Advanced Currency System**: Three-tier currency with copper, silver, and gold deniers

#### 🏘️ Living Villages

- **Dynamic Growth**: Villages expand organically based on population and resources
- **Cultural Diversity**: Multiple civilizations with unique building styles and customs
- **Economic Systems**: Complex trade networks and currency exchange
- **Social Interactions**: Deep villager relationships and reputation systems

#### 🎨 Cultural Systems

- **Norman Culture**: Medieval European architecture and feudal society
- **Byzantine Culture**: Eastern Roman Empire aesthetics and governance
- **Hindi Culture**: Indian subcontinent traditions and architecture
- **Japanese Culture**: Traditional Japanese buildings and social structure
- **Mayan Culture**: Pre-Columbian Mesoamerican civilization

#### 🔮 Magic & Technology

- **Dynamic Amulets**: Magical items that change appearance based on environment
- **Advanced Crafting**: Culture-specific recipes and materials
- **Unique Tools**: Specialized equipment for each civilization

### 🚀 Development Status

**Current Version**: `0.1.0-alpha` (Active Development)
**Last Updated**: September 2, 2025

#### ✅ Completed Systems

| System                | Items        | Status      | Description                                          |
| --------------------- | ------------ | ----------- | ---------------------------------------------------- |
| **Item System**       | 95 items     | ✅ Complete | All legacy items migrated with modern mechanics      |
| **Currency System**   | 6 currencies | ✅ Complete | Multi-cultural monetary system                       |
| **Food System**       | 27 foods     | ✅ Complete | Culture-specific cuisine with proper nutrition       |
| **Decorative Blocks** | 9 variants   | ✅ Complete | Stone, wood, and earth decorative building materials |
| **Magic Items**       | 4 amulets    | ✅ Complete | Dynamic color-changing magical amulets               |
| **Core Architecture** | -            | ✅ Complete | Modern Forge 1.20.1 foundation                       |

#### 🔄 In Progress

| System                  | Progress | Expected Completion |
| ----------------------- | -------- | ------------------- |
| **Village Core System** | 25%      | October 2025        |
| **Building Framework**  | 15%      | November 2025       |
| **Entity System**       | 10%      | December 2025       |

#### 📋 Planned Features

- **Mill Chest System**: Secure storage with village integration
- **Path & Road System**: Cultural building connections
- **Sign System**: Multi-language village signage
- **Crop System**: Culture-specific agriculture
- **AI Villagers**: Smart NPCs with complex behaviors
- **Building Generator**: Procedural architecture system

### 🎯 Technical Achievements

#### 🏗️ Modern Architecture

- **Package Structure**: Clean `com.jasoncian.millenaire_rewrite` organization
- **Type Safety**: Full generic system for variant blocks and items
- **Data Generation**: Automated resource file generation (126+ files)
- **Localization**: Complete English/Chinese translation support

#### 📊 Project Statistics

- **Java Classes**: 72 modern implementations
- **Texture Assets**: 107 high-quality PNG files
- **Generated Resources**: 126 automatically created files
- **Registered Items**: 95 unique items with proper integration
- **Registered Blocks**: 4 foundational blocks with entity support

#### 🔧 Code Quality

- **Modern Java 17**: Latest language features and best practices
- **Forge 47.4.3**: Current stable API implementation
- **Clean Architecture**: Modular design for easy expansion
- **Comprehensive Documentation**: Full JavaDoc coverage

### 🛠️ Installation & Development

#### Prerequisites

- **Minecraft**: 1.20.1
- **Forge**: 47.4.3+
- **Java Development Kit**: 17+
- **IDE**: IntelliJ IDEA or Eclipse with Minecraft Development Kit

#### Quick Start

```bash
# Clone the repository
git clone https://github.com/jasoncian/millenaire-rewrite.git
cd millenaire-rewrite

# Setup development environment
./gradlew genEclipseRuns  # For Eclipse
./gradlew genIntellijRuns # For IntelliJ

# Run in development
./gradlew runClient
```

#### Building

```bash
# Build mod JAR
./gradlew build

# Generate data files
./gradlew runData
```

### 🤝 Contributing

We welcome contributions! Please see our development roadmap and pick an area that interests you.

#### Current Priorities

1. **Village Management System** - Core village logic and data structures
2. **Building Generation** - Automated construction system
3. **NPC AI System** - Smart villager behaviors
4. **Cultural Expansion** - Additional civilizations and features

### 📜 License

This project is licensed under the MIT License - see [LICENSE.txt](LICENSE.txt) for details.

### 🙏 Acknowledgments

- **Original Millenaire Team** - For creating the beloved original mod
- **MinecraftForge Team** - For providing the modding framework
- **Community Contributors** - For feedback and suggestions

---

## 中文

### 📖 关于项目

**千年村庄重制版**是经典千年村庄（Millenaire）模组在 Minecraft 1.20.1 上的完全现代化重制。本项目将备受喜爱的村庄建设和文化体验带到现代 Minecraft 中，具有增强的性能、更新的机制和更清洁的代码库。

作为 Minecraft 最具雄心的文明类模组之一，千年村庄添加了具有独特文化的活跃村庄，这些村庄会随时间增长和演化。这次重制保留了核心精神，同时拥抱现代模组开发标准。

提一嘴，我没有找到 1.12 的开源代码库，项目的参考代码 fork 自https://github.com/MoonCutter2B/Millenaire
貌似是 1.8 版本

### ✨ 核心特性

#### 🏘️ 活跃村庄

- **动态增长**: 村庄根据人口和资源有机扩张
- **文化多样性**: 多个文明，具有独特的建筑风格和习俗
- **经济系统**: 复杂的贸易网络和货币交换
- **社交互动**: 深度的村民关系和声望系统

#### 🎨 文化系统

- **诺曼文化**: 中世纪欧洲建筑和封建社会
- **拜占庭文化**: 东罗马帝国美学和治理
- **印度文化**: 印度次大陆传统和建筑
- **日本文化**: 传统日式建筑和社会结构
- **玛雅文化**: 前哥伦布时期中美洲文明

#### 🔮 魔法与科技

- **动态护符**: 根据环境改变外观的魔法物品
- **高级制作**: 文化特定的配方和材料
- **独特工具**: 每个文明的专用装备

### 🚀 开发状态

**当前版本**: `0.1.0-alpha` (积极开发中)
**最后更新**: 2025 年 9 月 2 日

#### ✅ 已完成系统

| 系统         | 数量      | 状态    | 描述                             |
| ------------ | --------- | ------- | -------------------------------- |
| **物品系统** | 95 个物品 | ✅ 完成 | 所有遗留物品已迁移并具有现代机制 |
| **货币系统** | 6 种货币  | ✅ 完成 | 多文化货币体系                   |
| **食物系统** | 27 种食物 | ✅ 完成 | 文化特色料理，具有合适的营养值   |
| **装饰方块** | 9 个变体  | ✅ 完成 | 石材、木材和土质装饰建筑材料     |
| **魔法物品** | 4 个护符  | ✅ 完成 | 动态变色魔法护符                 |
| **核心架构** | -         | ✅ 完成 | 现代 Forge 1.20.1 基础           |

#### 🔄 开发中

| 系统             | 进度 | 预期完成时间  |
| ---------------- | ---- | ------------- |
| **村庄核心系统** | 25%  | 2025 年 10 月 |
| **建筑框架**     | 15%  | 2025 年 11 月 |
| **实体系统**     | 10%  | 2025 年 12 月 |

#### 📋 计划功能

- **千年箱系统**: 与村庄集成的安全储存
- **道路系统**: 文化建筑连接
- **标志系统**: 多语言村庄标识
- **作物系统**: 文化特色农业
- **AI 村民**: 具有复杂行为的智能 NPC
- **建筑生成器**: 程序化建筑系统

### 🎯 技术成就

#### 🏗️ 现代架构

- **包结构**: 清洁的 `com.jasoncian.millenaire_rewrite`组织
- **类型安全**: 变体方块和物品的完整泛型系统
- **数据生成**: 自动化资源文件生成（126+文件）
- **本地化**: 完整的英中文翻译支持

#### 📊 项目统计

- **Java 类**: 72 个现代实现
- **材质资产**: 107 个高质量 PNG 文件
- **生成资源**: 126 个自动创建的文件
- **注册物品**: 95 个独特物品，完整集成
- **注册方块**: 4 个基础方块，支持实体

#### 🔧 代码质量

- **现代 Java 17**: 最新语言特性和最佳实践
- **Forge 47.4.3**: 当前稳定 API 实现
- **清洁架构**: 模块化设计，易于扩展
- **全面文档**: 完整 JavaDoc 覆盖

### 🛠️ 安装与开发

#### 前置条件

- **Minecraft**: 1.20.1
- **Forge**: 47.4.3+
- **Java 开发工具包**: 17+
- **IDE**: IntelliJ IDEA 或 Eclipse，配合 Minecraft 开发工具包

#### 快速开始

```bash
# 克隆仓库
git clone https://github.com/jasoncian/millenaire-rewrite.git
cd millenaire-rewrite

# 设置开发环境
./gradlew genEclipseRuns  # Eclipse用
./gradlew genIntellijRuns # IntelliJ用

# 开发运行
./gradlew runClient
```

#### 构建

```bash
# 构建模组JAR
./gradlew build

# 生成数据文件
./gradlew runData
```

### 🤝 贡献

我们欢迎贡献！请查看我们的开发路线图，选择您感兴趣的领域。

#### 当前优先级

1. **村庄管理系统** - 核心村庄逻辑和数据结构
2. **建筑生成** - 自动化建造系统
3. **NPC AI 系统** - 智能村民行为
4. **文化扩展** - 额外的文明和功能

### 📜 许可证

本项目采用 MIT 许可证 - 详见[LICENSE.txt](LICENSE.txt)。

### 🙏 致谢

- **原版千年村庄团队** - 创造了备受喜爱的原版模组
- **MinecraftForge 团队** - 提供模组开发框架
- **社区贡献者** - 提供反馈和建议

---

## 🔗 Links | 链接

- **Issues** | **问题报告**: [GitHub Issue](https://github.com/jasoncian/millenaire-rewrite/issues)
- **Original Mod** | **原版模组**: [Millenaire Legacy](https://millenaire.org)

---

_Last updated: September 2, 2025 | 最后更新：2025 年 9 月 2 日_
