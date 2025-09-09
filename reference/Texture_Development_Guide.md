# 纹理开发指南与占位文件系统

## 概述

千年村庄重写版模组的DataGen框架现在包含了**纹理占位文件生成器**，能够自动生成所有物品、方块和GUI的纹理占位文件，为纹理艺术家提供完整的开发基础。

## 纹理占位文件系统特性

### ✨ 主要功能

- **🎨 自动生成**: 一键生成所有需要的纹理占位文件
- **🏷️ 智能标识**: 每个占位文件包含清晰的文字标识
- **🌈 颜色编码**: 不同文化和类型使用不同的颜色主题
- **📏 多尺寸支持**: 支持16x16物品纹理和256x256 GUI纹理
- **🔤 自适应文字**: 根据图像尺寸自动调整字体大小
- **🎯 高对比度**: 自动选择与背景对比的文字颜色

### 🎨 颜色主题方案

| 文化/类型 | 主色调 | 说明 |
|----------|--------|------|
| **Norman（诺曼）** | 钢蓝色 `#4682B4` | 代表钢铁与海洋 |
| **Japanese（日本）** | 深红色 `#DC143C` | 代表日出与武士精神 |
| **Byzantine（拜占庭）** | 紫色 `#8A2BE2` | 代表皇室与奢华 |
| **Chinese（华夏）** | 中国红 `#DC143C` | 代表传统与喜庆 |
| **货币系统** | 金色系列 | 金、银、铜色调 |
| **建筑材料** | 灰棕色系 | 自然材质色调 |

## 生成的纹理文件结构

运行数据生成后，将在以下位置生成纹理占位文件：

```
src/generated/resources/assets/millenaire_rewrite/textures/
├── item/                           # 物品纹理 (16x16)
│   ├── denier.png                  # 德尼尔币 - 金色
│   ├── denier_or.png              # 金德尼尔 - 橙金色
│   ├── denier_argent.png          # 银德尼尔 - 银色
│   ├── culture_scroll.png         # 文化卷轴 - 米白色
│   ├── parchment.png              # 羊皮纸 - 羊皮色
│   │
│   ├── norman_sword.png           # 诺曼剑 - 钢蓝色
│   ├── norman_helmet.png          # 诺曼头盔 - 深灰色
│   ├── norman_shield.png          # 诺曼盾牌 - 棕色
│   │
│   ├── katana.png                 # 武士刀 - 深红色
│   ├── wakizashi.png              # 胁差 - 橙红色
│   ├── japanese_helmet.png        # 日式头盔 - 暗红色
│   │
│   ├── byzantine_sword.png        # 拜占庭剑 - 紫色
│   ├── byzantine_crown.png        # 拜占庭皇冠 - 金色
│   ├── byzantine_robe.png         # 拜占庭长袍 - 靛蓝色
│   │
│   ├── chinese_sword.png          # 华夏剑 - 中国红
│   ├── chinese_coin.png           # 华夏钱币 - 金色
│   ├── jade_pendant.png           # 玉佩 - 翡翠绿
│   ├── silk_fabric.png            # 丝绸 - 丝绸粉
│   ├── tea_leaves.png             # 茶叶 - 茶叶绿
│   ├── porcelain.png              # 瓷器 - 瓷器白
│   │
│   └── ... 其他物品
│
├── block/                          # 方块纹理 (16x16)
│   ├── village_stone.png          # 村庄石材 - 灰色
│   ├── culture_totem.png          # 文化图腾 - 棕色
│   │
│   ├── norman_stone.png           # 诺曼石材 - 深灰色
│   ├── norman_wood.png            # 诺曼木材 - 棕色
│   ├── norman_roof.png            # 诺曼屋顶 - 深棕色
│   │
│   ├── japanese_wood.png          # 日式木材 - 浅棕色
│   ├── japanese_roof.png          # 日式屋顶 - 灰色
│   ├── tatami.png                 # 榻榻米 - 黄绿色
│   │
│   ├── byzantine_marble.png       # 拜占庭大理石 - 象牙白
│   ├── byzantine_gold.png         # 拜占庭金块 - 金色
│   ├── byzantine_mosaic.png       # 拜占庭马赛克 - 靛蓝色
│   │
│   ├── chinese_brick.png          # 华夏青砖 - 中国红砖
│   ├── chinese_tile.png           # 华夏琉璃瓦 - 琉璃瓦金
│   ├── chinese_wood.png           # 华夏木材 - 中式木材色
│   ├── chinese_stone.png          # 华夏青石 - 青石色
│   ├── lantern.png                # 灯笼 - 灯笼红
│   ├── screen.png                 # 屏风 - 屏风色
│   │
│   └── ... 其他方块
│
└── gui/                            # GUI纹理 (256x256)
    ├── village_info_bg.png        # 村庄信息背景
    ├── trade_interface.png        # 贸易界面
    ├── quest_interface.png        # 任务界面
    ├── reputation_interface.png   # 声望界面
    ├── chinese_interface.png      # 华夏文化界面
    ├── norman_interface.png       # 诺曼文化界面
    ├── japanese_interface.png     # 日本文化界面
    └── byzantine_interface.png    # 拜占庭文化界面
```

## 如何运行纹理占位生成

### 1. 运行数据生成任务

```bash
# 在项目根目录运行
./gradlew runData
```

### 2. 查看生成结果

生成完成后，检查 `src/generated/resources/assets/millenaire_rewrite/textures/` 目录

### 3. 复制到主资源目录

将需要的占位文件复制到 `src/main/resources/assets/millenaire_rewrite/textures/` 用于实际开发

## 纹理开发工作流程

### 📋 推荐流程

1. **📦 生成占位**: 运行 `runData` 生成所有占位文件
2. **🎨 选择文件**: 从生成的占位文件中选择要制作的纹理
3. **📁 复制基础**: 将占位文件复制到主资源目录作为起始点
4. **🖌️ 创作纹理**: 使用Photoshop、GIMP等工具创作实际纹理
5. **🔄 测试验证**: 在游戏中测试纹理效果
6. **✨ 优化完善**: 根据游戏效果进行调整优化

### 🎨 纹理设计指南

#### 物品纹理 (16x16)
- **风格统一**: 保持像素艺术风格，与Minecraft原版协调
- **文化特色**: 体现不同文化的特色元素和色彩
- **清晰可辨**: 在小尺寸下仍能清楚识别物品特征
- **细节适度**: 避免过于复杂的细节，保持简洁美观

#### 方块纹理 (16x16)
- **可平铺**: 确保纹理可以无缝平铺
- **材质感**: 体现不同材质的质感（石材、木材、金属等）
- **光影考虑**: 考虑Minecraft的光照系统
- **文化差异**: 突出不同文化建筑材料的特色

#### GUI纹理 (256x256)
- **界面美观**: 设计美观且实用的界面布局
- **文化主题**: 体现对应文化的设计风格
- **用户体验**: 确保界面元素清晰易用
- **色彩搭配**: 使用和谐的色彩搭配方案

## 扩展纹理占位生成

### 添加新的纹理占位

在 `MillenaireTexturePlaceholderProvider.java` 中添加新的纹理：

```java
// 在对应的方法中添加
generateTexturePlaceholder(cache, "item/new_item", "NEW", new Color(255, 0, 0));
generateTexturePlaceholder(cache, "block/new_block", "BLK", new Color(0, 255, 0));
```

### 自定义颜色主题

```java
// 为新文化定义颜色主题
Color cultureColor = new Color(R, G, B); // RGB值
generateTexturePlaceholder(cache, "item/culture_item", "CUL", cultureColor);
```

### 添加新的GUI纹理

```java
// 生成自定义尺寸的GUI纹理
generateTexturePlaceholder(cache, "gui/custom_interface", "CUI", color, 512, 512);
```

## 华夏文化纹理重点

为了突出华夏文化的独特性，特别关注以下纹理：

### 🏮 华夏特色物品
- **jade_pendant.png**: 玉佩，体现中华玉文化
- **silk_fabric.png**: 丝绸，体现丝绸之路文化
- **tea_leaves.png**: 茶叶，体现茶文化
- **porcelain.png**: 瓷器，体现制瓷工艺
- **chinese_coin.png**: 华夏钱币，体现古代货币文化

### 🏛️ 华夏特色建筑
- **chinese_brick.png**: 中式青砖，体现传统建筑材料
- **chinese_tile.png**: 琉璃瓦，体现皇家建筑风格
- **lantern.png**: 灯笼，体现节庆文化
- **screen.png**: 屏风，体现室内装饰文化

### 🎨 设计要求
- 使用传统中国色彩：中国红、琉璃金、翡翠绿等
- 融入传统文化元素：龙纹、云纹、回字纹等
- 体现工艺特色：玉石质感、丝绸光泽、瓷器温润等

## 纹理质量标准

### ✅ 合格标准
- [ ] 尺寸正确（16x16 或指定尺寸）
- [ ] 风格统一（像素艺术风格）
- [ ] 色彩和谐（符合文化主题）
- [ ] 清晰可辨（在游戏中容易识别）
- [ ] 文化特色（体现对应文化特征）

### 🌟 优秀标准
- [ ] 细节丰富（在限定像素内表现丰富细节）
- [ ] 创意独特（具有创新性和独特性）
- [ ] 质感真实（材质质感表现到位）
- [ ] 文化深度（深度体现文化内涵）
- [ ] 游戏协调（与Minecraft整体风格协调）

---

纹理占位文件生成器现已就绪！运行 `./gradlew runData` 即可为整个项目生成完整的纹理开发基础。这将大大提升纹理开发的效率和质量！
