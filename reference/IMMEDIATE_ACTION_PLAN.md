# 🚀 Millenaire Rewrite - 即时开发行动计划

## 📋 立即开始的任务清单

### 🔴 第一优先级（本周内完成）

#### 1. Mill Chest 系统完善

**位置**: `src/main/java/com/jasoncian/millenaire_rewrite/blocks/functional/MillChestBlock.java`
**状态**: 基础实现存在，需要完善

**具体任务**:

- [ ] 完善 MillChestBlockEntity 的 NBT 数据保存/加载
- [ ] 实现 Container 类（MillChestMenu）
- [ ] 创建客户端 Screen 界面（MillChestScreen）
- [ ] 添加网络数据包处理
- [ ] 实现权限系统（村民访问控制）
- [ ] 添加 GUI 纹理和本地化

#### 2. 完成核心注册系统中的 TODO 项目

**涉及文件**:

- `ModBlockItems.java` - 添加更多方块变体物品
- `ModBlocks.java` - 添加路径系统方块
- `ModItems.java` - 完成所有遗留物品注册

### 🟡 第二优先级（下周内完成）

#### 3. Mill Sign 系统实现

**新建文件**: `blocks/functional/MillSignBlock.java`

**功能需求**:

- 显示村庄信息（名称、人口、声誉等）
- 支持多语言显示
- 可配置的信息显示格式
- 与 Village 数据系统集成

#### 4. Path System 基础架构

**新建文件**: `blocks/decorative/PathBlock.java`

**功能需求**:

- 多种路径材质变体（石头、土路、木板等）
- 自动连接相邻路径
- 支持村庄道路生成算法

### 🟢 第三优先级（2 周内完成）

#### 5. 网络通信系统建立

**新建包**: `com.jasoncian.millenaire_rewrite.network`

**包含文件**:

- `PacketHandler.java` - 数据包注册和处理
- `packets/MillChestSyncPacket.java` - Mill Chest 数据同步
- `packets/VillageInfoPacket.java` - 村庄信息同步

#### 6. GUI 系统框架

**新建包**: `com.jasoncian.millenaire_rewrite.client.gui`

**包含文件**:

- `MillChestScreen.java` - Mill Chest 界面
- `components/` - 可复用 GUI 组件

---

## 🛠️ 具体实施指南

### Mill Chest 完善步骤

#### 步骤 1：BlockEntity 完善

```java
// 需要在MillChestBlockEntity中实现:
public class MillChestBlockEntity extends BlockEntity implements Container {
    // 物品存储
    private NonNullList<ItemStack> items;
    // 权限管理
    private UUID ownerVillage;
    private Set<UUID> authorizedPlayers;

    // NBT保存/加载方法
    @Override
    public void saveAdditional(CompoundTag tag) { }

    @Override
    public void load(CompoundTag tag) { }

    // Container接口实现
    @Override
    public int getContainerSize() { }

    // 权限检查方法
    public boolean canPlayerAccess(Player player) { }
}
```

#### 步骤 2：Menu 类实现

```java
// 新建: containers/MillChestMenu.java
public class MillChestMenu extends AbstractContainerMenu {
    private final MillChestBlockEntity blockEntity;

    public MillChestMenu(int id, Inventory playerInventory, MillChestBlockEntity blockEntity) {
        // Container构造逻辑
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // 快速移动物品逻辑
    }
}
```

#### 步骤 3：Screen 界面

```java
// 新建: client/gui/MillChestScreen.java
public class MillChestScreen extends AbstractContainerScreen<MillChestMenu> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation(MillenaireRewrite.MOD_ID, "textures/gui/mill_chest.png");

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // 背景渲染
    }
}
```

### Mill Sign 实现步骤

#### 步骤 1：方块类设计

```java
public class MillSignBlock extends Block {
    // 方向属性
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public MillSignBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOD)
            .strength(2.0F)
            .sound(SoundType.WOOD)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // 与Village系统集成的信息显示逻辑
    public Component getDisplayText(Level level, BlockPos pos) {
        // 根据位置查找最近的村庄
        // 返回格式化的村庄信息
    }
}
```

### Path System 架构设计

#### 路径方块变体枚举

```java
public enum PathVariant implements StringRepresentable {
    DIRT_PATH("dirt_path"),
    STONE_PATH("stone_path"),
    COBBLE_PATH("cobble_path"),
    WOOD_PATH("wood_path");

    private final String name;

    PathVariant(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
```

#### 自动连接逻辑

```java
public class PathBlock extends Block {
    public static final EnumProperty<PathVariant> VARIANT = EnumProperty.create("variant", PathVariant.class);

    // 检查相邻方块并更新连接状态
    private void updateConnections(Level level, BlockPos pos) {
        // 检查上下左右四个方向
        // 更新当前方块和相邻方块的连接状态
    }
}
```

---

## 📁 需要创建的文件结构

### 新建文件清单

```
src/main/java/com/jasoncian/millenaire_rewrite/
├── blocks/functional/
│   └── MillSignBlock.java                 # 🆕 村庄标识方块
├── blocks/decorative/
│   └── PathBlock.java                     # 🆕 路径系统方块
├── blockentities/
│   └── MillSignBlockEntity.java           # 🆕 村庄标识数据
├── containers/
│   └── MillChestMenu.java                 # 🆕 Mill Chest容器
├── client/gui/
│   ├── MillChestScreen.java               # 🆕 Mill Chest界面
│   └── components/                        # 🆕 GUI组件包
├── network/
│   ├── PacketHandler.java                 # 🆕 网络处理器
│   └── packets/                           # 🆕 数据包定义
│       ├── MillChestSyncPacket.java
│       └── VillageInfoPacket.java
└── enums/
    └── PathVariant.java                   # 🆕 路径变体枚举
```

### 需要更新的文件

```
已存在需要完善:
├── ModBlocks.java                         # 🔧 添加新方块注册
├── ModBlockItems.java                     # 🔧 添加新方块物品
├── ModBlockEntities.java                  # 🔧 注册新BlockEntity
├── blocks/functional/MillChestBlock.java   # 🔧 完善现有实现
└── blockentities/MillChestBlockEntity.java # 🔧 完善现有实现
```

### 资源文件需求

```
src/main/resources/
├── assets/millenaire_rewrite/
│   ├── textures/gui/
│   │   └── mill_chest.png                 # 🎨 Mill Chest GUI纹理
│   ├── textures/block/
│   │   ├── mill_sign.png                  # 🎨 Mill Sign纹理
│   │   └── path_*.png                     # 🎨 各种路径纹理
│   └── models/block/
│       ├── mill_sign.json                 # 📐 Mill Sign模型
│       └── path_*.json                    # 📐 路径模型
└── data/millenaire_rewrite/
    ├── loot_tables/blocks/
    │   ├── mill_sign.json                 # 🎁 掉落表
    │   └── path_*.json
    └── recipes/
        ├── mill_sign.json                 # 🔨 合成配方
        └── path_*.json
```

---

## 🎯 每日工作计划

### 第 1 天：Mill Chest BlockEntity 完善

- [ ] 完善 NBT 数据保存/加载
- [ ] 实现 Container 接口方法
- [ ] 添加权限检查逻辑
- [ ] 编写单元测试

### 第 2 天：Mill Chest Menu 实现

- [ ] 创建 MillChestMenu 类
- [ ] 实现物品槽位布局
- [ ] 添加快速移动逻辑
- [ ] 注册到 MenuType

### 第 3 天：Mill Chest Screen 界面

- [ ] 创建 Screen 类
- [ ] 设计 GUI 纹理
- [ ] 实现渲染逻辑
- [ ] 添加本地化文本

### 第 4 天：网络数据包系统

- [ ] 创建 PacketHandler
- [ ] 实现数据同步包
- [ ] 测试客户端-服务器通信
- [ ] 优化网络性能

### 第 5 天：Mill Sign 基础实现

- [ ] 创建 MillSignBlock
- [ ] 实现 BlockEntity
- [ ] 添加村庄信息显示
- [ ] 创建基础模型和纹理

---

## 🧪 测试计划

### 功能测试

1. **Mill Chest 测试**

   - 物品存储和取出
   - 权限系统验证
   - NBT 数据持久化
   - GUI 交互测试

2. **Mill Sign 测试**

   - 村庄信息显示
   - 多语言切换
   - 方向和放置测试

3. **Path System 测试**
   - 自动连接功能
   - 变体切换
   - 生成算法准备

### 性能测试

- 大量 Mill Chest 的性能影响
- 网络数据包频率测试
- 内存使用监控

### 兼容性测试

- 与原版容器系统兼容性
- 与其他 mod 的交互测试
- 多人游戏稳定性

---

## 📚 完成后的文档更新

### 需要更新的文档

1. **CHANGELOG.md** - 记录所有新增功能
2. **API_DOCUMENTATION.md** - 新增 API 文档
3. **TESTING_REPORT.md** - 测试结果报告
4. **PERFORMANCE_ANALYSIS.md** - 性能分析报告

### 需要创建的文档

1. **MILL_CHEST_SYSTEM_GUIDE.md** - Mill Chest 使用指南
2. **GUI_DEVELOPMENT_GUIDE.md** - GUI 开发标准
3. **NETWORK_PROTOCOL_SPEC.md** - 网络协议规范

---

**⏰ 预计完成时间**: 1-2 周  
**👨‍💻 主要开发者**: AI 助手 + 项目维护者  
**🎯 成功标准**: 所有功能方块可正常使用，具备完整的交互能力

> 💡 **提示**: 完成这个阶段后，项目将具备完整的功能方块系统，为后续的实体和村庄系统开发奠定坚实基础。
