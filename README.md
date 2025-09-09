# Millenaire Rewrite | 千年村庄重制版

<div align="center">

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-green.svg)
![Forge](https://img.shields.io/badge/Forge-47.4.3-orange.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow.svg)

**一个将经典千年村庄模组完全重写到 Minecraft 1.20.1 的大型项目**

_A complete rewrite of the classic Millenaire mod for Minecraft 1.20.1_

</div>

---

## 📋 项目简介 | Project Overview

千年村庄重制版是对经典 Millenaire 模组从 1.12.2 到 1.20.1 的完全重写项目。该模组为 Minecraft 世界带来了生动的 NPC 村庄，具有多种文化背景、复杂的经济系统和动态的村庄发展机制。

Millenaire Rewrite is a complete reimplementation of the classic Millenaire mod from 1.12.2 to 1.20.1. This mod brings living NPC villages to the Minecraft world, featuring multiple cultural backgrounds, complex economic systems, and dynamic village development mechanics.

## ✨ 核心特性 | Key Features

### 🏘️ 多文化村庄系统 | Multi-Cultural Village System

- **7 种文化体系**：诺曼、日本、拜占庭、塞尔柱、玛雅、因纽特、印度文化
- **动态村庄发展**：村庄会随时间自然成长和进化
- **独特建筑风格**：每种文化都有其特色的建筑样式和材料
- **7 Cultural Systems**: Norman, Japanese, Byzantine, Seljuk, Mayan, Inuit, and Indian cultures
- **Dynamic Village Growth**: Villages naturally grow and evolve over time
- **Unique Architecture**: Each culture features distinctive building styles and materials

### 💰 复杂经济系统 | Complex Economic System

- **贸易网络**：村庄间的商品交换和贸易路线
- **职业分工**：村民拥有不同的职业和专长
- **资源管理**：智能的资源分配和库存管理
- **Trade Networks**: Inter-village commerce and trade routes
- **Professional Division**: Villagers with different occupations and specializations
- **Resource Management**: Intelligent resource allocation and inventory systems

### 🤖 智能 AI 系统 | Intelligent AI System

- **高级行为模式**：村民具有复杂的日常行为和社交互动
- **目标驱动**：基于需求和目标的决策系统
- **适应性学习**：AI 能够适应环境变化
- **Advanced Behavior Patterns**: Complex daily routines and social interactions
- **Goal-Driven**: Decision-making based on needs and objectives
- **Adaptive Learning**: AI that adapts to environmental changes

## 🛠️ 技术规格 | Technical Specifications

### 📋 环境要求 | Requirements

- **Minecraft**: 1.20.1
- **Forge**: 47.4.3+
- **Java**: 17+
- **内存建议 | Recommended RAM**: 4GB+

### 🏗️ 项目架构 | Project Architecture

```
com.jasoncian.millenaire_rewrite/
├── api/                    # 公共API接口 | Public API interfaces
├── block/                  # 方块系统 | Block system
├── item/                   # 物品系统 | Item system
├── entity/                 # 实体系统 | Entity system
├── village/                # 村庄系统 | Village system
├── culture/                # 文化系统 | Culture system
├── world/                  # 世界生成 | World generation
├── network/                # 网络通信 | Network communication
├── client/                 # 客户端代码 | Client-side code
├── server/                 # 服务端代码 | Server-side code
└── util/                   # 工具类 | Utility classes
```

## 🚀 开发状态 | Development Status

### 📊 项目进度 | Project Progress

- **✅ Phase 0**: 项目基础设施与框架搭建 | Project Infrastructure & Framework
- **🚧 Phase 1**: 基础系统迁移 | Basic System Migration (进行中 | In Progress)
- **⏳ Phase 2**: 核心功能实现 | Core Feature Implementation (计划中 | Planned)
- **⏳ Phase 3**: 高级功能与优化 | Advanced Features & Optimization (计划中 | Planned)

### 📈 统计信息 | Statistics

- **预计代码量 | Estimated Code**: 50,000+ 行 | lines
- **核心类数量 | Core Classes**: 200+
- **配置文件 | Config Files**: 500+
- **资源文件 | Resource Files**: 1,000+

## 🔧 开发环境配置 | Development Setup

### 📦 克隆项目 | Clone Repository

```bash
git clone https://github.com/JasonCian/Millenaire-rewrite.git
cd Millenaire-rewrite
```

### 🛠️ 构建项目 | Build Project

```bash
# Windows
./gradlew build

# Linux/macOS
./gradlew build
```

### 🏃 运行开发环境 | Run Development Environment

```bash
# 启动客户端 | Launch Client
./gradlew runClient

# 启动服务端 | Launch Server
./gradlew runServer
```

## 📝 开发规范 | Development Standards

### 🎯 代码风格 | Code Style

- **包命名**：基于功能的包组织结构
- **类命名**：使用 PascalCase，见名知意
- **方法命名**：使用 camelCase，动词开头
- **常量命名**：使用 UPPER_SNAKE_CASE
- **Package Naming**: Function-based package organization
- **Class Naming**: PascalCase with descriptive names
- **Method Naming**: camelCase starting with verbs
- **Constant Naming**: UPPER_SNAKE_CASE

### 📋 提交规范 | Commit Standards

```
feat: 新功能 | new feature
fix: 修复bug | bug fix
docs: 文档更新 | documentation update
style: 代码格式化 | code formatting
refactor: 代码重构 | code refactoring
test: 测试相关 | testing
chore: 构建配置 | build configuration
```

## 🤝 贡献指南 | Contributing

### 🔄 贡献流程 | Contribution Process

1. **Fork** 项目仓库 | Fork the repository
2. **创建** 特性分支 | Create a feature branch
3. **提交** 代码变更 | Commit your changes
4. **推送** 到分支 | Push to the branch
5. **创建** Pull Request | Create a Pull Request

### 📊 开发优先级 | Development Priorities

1. **🔴 高优先级 | High Priority**: 核心系统框架 | Core system framework
2. **🟡 中优先级 | Medium Priority**: 文化系统实现 | Culture system implementation
3. **🟢 低优先级 | Low Priority**: 视觉效果优化 | Visual effects optimization

## 📚 文档资源 | Documentation

### 📖 开发文档 | Development Docs

- [代码开发规范 | Coding Standards](reference/CODING_STANDARDS.md)
- [开发计划路线图 | Development Plan](reference/DEVELOPMENT_PLAN.md)
- [迁移计划总结 | Migration Summary](reference/迁移计划-AI总结/)

### 🎓 学习资源 | Learning Resources

- [Forge 1.20.1 官方文档 | Official Documentation](https://docs.minecraftforge.net/)
- [Minecraft Wiki](https://minecraft.wiki/)
- [模组开发教程 | Modding Tutorials](https://moddingtutorials.org/)

## 🐛 问题反馈 | Issue Reporting

遇到问题？请通过以下方式反馈：
Having issues? Please report through:

- **🐛 Bug 报告 | Bug Reports**: [Issues](https://github.com/JasonCian/Millenaire-rewrite/issues)
- **💡 功能建议 | Feature Requests**: [Discussions](https://github.com/JasonCian/Millenaire-rewrite/discussions)
- **❓ 使用帮助 | Support**: [Wiki](https://github.com/JasonCian/Millenaire-rewrite/wiki)

## 📄 许可证 | License

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE.txt) 文件了解详情。

This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.

## 🙏 致谢 | Acknowledgments

- **原始 Millenaire 团队**：感谢创造了这个令人惊叹的模组
- **Minecraft Forge 团队**：提供了强大的模组开发框架
- **社区贡献者**：感谢所有参与测试和反馈的玩家
- **Original Millenaire Team**: For creating this amazing mod
- **Minecraft Forge Team**: For providing the powerful modding framework
- **Community Contributors**: For all the testing and feedback

---

<div align="center">

**🌟 如果这个项目对你有帮助，请给我们一个 Star！**

**🌟 If this project helps you, please give us a Star!**

Made with ❤️ by [JasonCian](https://github.com/JasonCian)

</div>
