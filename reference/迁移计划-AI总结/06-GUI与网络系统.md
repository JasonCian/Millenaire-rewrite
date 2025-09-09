# GUI 与网络系统迁移指南

## 1. GUI 系统总览

### 现有 GUI 结构分析

**主要 GUI 包位置**:

- `org.millenaire.client.gui` - 主 GUI 类
- `org.millenaire.client.gui.text` - 文本界面系统
- `org.millenaire.common.ui` - 服务端容器类

### GUI 类型统计

#### A. 容器 GUI (Container-based)

```java
GuiTrade.java           // 贸易界面 (核心)
GuiPujas.java          // 宗教祭祀界面
GuiLockedChest.java    // 锁定箱子界面
GuiFirePit.java        // 火炉界面
```

#### B. 文本 GUI (Text-based)

```java
GuiText.java           // 基础文本GUI
GuiTravelBook.java     // 旅行手册
GuiPanelParchment.java // 面板羊皮纸
```

#### C. 配置 GUI

```java
GuiConfig.java         // 配置界面
GuiControlledMilitary.java    // 军事控制
GuiControlledProjects.java    // 项目控制
GuiCustomBuilding.java        // 自定义建筑
```

#### D. 显示 GUI

```java
DisplayActions.java    // GUI显示控制器
```

## 2. 核心 GUI 类迁移分析

### A. GuiTrade.java - 贸易系统界面

#### 当前实现 (1.12.2)

```java
public class GuiTrade extends GuiContainer {
    private final EntityPlayer player;
    private final Building building;
    private final ContainerTrade container;

    public GuiTrade(EntityPlayer player, Building building) {
        super(new ContainerTrade(player, building));
        this.player = player;
        this.building = building;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
```

#### 迁移到 1.20.1

```java
public class GuiTrade extends AbstractContainerScreen<ContainerTrade> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("millenaire", "textures/gui/trade.png");
    private final Player player;
    private final Building building;

    public GuiTrade(ContainerTrade container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
```

#### 主要变更点

1. **基类变更**: `GuiContainer` → `AbstractContainerScreen`
2. **渲染方法**: `drawGuiContainerBackgroundLayer` → `renderBg`
3. **坐标系统**: `guiLeft/guiTop` → `leftPos/topPos`
4. **纹理系统**: `GlStateManager` → `RenderSystem`
5. **参数变更**: 添加 `PoseStack` 参数

### B. ContainerTrade.java - 贸易容器

#### 当前实现 (1.12.2)

```java
public class ContainerTrade extends Container {
    private final Building building;
    private final EntityPlayer player;

    public ContainerTrade(EntityPlayer player, Building building) {
        this.player = player;
        this.building = building;

        // 添加玩家物品栏槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // 添加快捷栏槽位
        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(player.inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return building != null && building.isVillageActive();
    }
}
```

#### 迁移到 1.20.1

```java
public class ContainerTrade extends AbstractContainerMenu {
    private final Building building;
    private final Player player;

    public ContainerTrade(int id, Inventory playerInv, FriendlyByteBuf data) {
        super(ModMenuTypes.TRADE.get(), id);
        this.player = playerInv.player;
        this.building = getBuildingFromData(data);

        // 添加玩家物品栏槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // 添加快捷栏槽位
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return building != null && building.isVillageActive();
    }
}
```

#### 主要变更点

1. **基类变更**: `Container` → `AbstractContainerMenu`
2. **构造参数**: 需要菜单 ID 和数据包
3. **方法名**: `canInteractWith` → `stillValid`
4. **槽位添加**: `addSlotToContainer` → `addSlot`

## 3. 文本 GUI 系统迁移

### A. GuiText.java - 基础文本界面

#### 当前功能

- 多页文本显示
- 按钮导航
- 本地化支持
- 自定义渲染

#### 迁移挑战

```java
// 1.12.2 文本渲染
fontRenderer.drawString(text, x, y, color);

// 1.20.1 需要使用PoseStack
font.draw(poseStack, text, x, y, color);
```

### B. GuiTravelBook.java - 旅行手册

#### 核心功能

- 村庄信息显示
- 文化介绍
- 贸易商品列表
- 建筑图鉴

#### 迁移要点

- 保持界面布局
- 更新渲染调用
- 适配新的输入系统

## 4. 网络系统迁移分析

### 现有网络架构

#### A. 服务端网络 (ServerSender/ServerReceiver)

**文件**:

- `org.millenaire.common.network.ServerSender.java`
- `org.millenaire.common.network.ServerReceiver.java`

#### B. 客户端网络 (ClientSender/ClientReceiver)

**文件**:

- `org.millenaire.client.network.ClientSender.java`
- `org.millenaire.client.network.ClientReceiver.java`

#### C. 数据流处理

**文件**: `org.millenaire.common.network.StreamReadWrite.java`

### 网络消息类型统计

#### A. GUI 相关消息

```java
// 打开GUI消息
MSG_OPEN_TRADE_GUI       // 打开贸易界面
MSG_OPEN_PUJAS_GUI       // 打开祭祀界面
MSG_OPEN_CHEST_GUI       // 打开箱子界面

// GUI交互消息
MSG_TRADE_ACTION         // 贸易操作
MSG_BUTTON_CLICK         // 按钮点击
MSG_SLOT_CLICK           // 槽位点击
```

#### B. 实体相关消息

```java
MSG_VILLAGER_DATA        // 村民数据同步
MSG_VILLAGER_SPAWN       // 村民生成
MSG_VILLAGER_GOAL        // 村民目标更新
MSG_VILLAGER_INVENTORY   // 村民库存同步
```

#### C. 村庄相关消息

```java
MSG_VILLAGE_DATA         // 村庄数据同步
MSG_BUILDING_UPDATE      // 建筑更新
MSG_CONSTRUCTION_PROGRESS // 建造进度
MSG_VILLAGE_RELATIONS    // 村庄关系
```

#### D. 配置和控制消息

```java
MSG_CONFIG_UPDATE        // 配置更新
MSG_COMMAND_EXECUTE      // 命令执行
MSG_CHUNK_DATA          // 区块数据
```

## 5. 网络系统迁移要点

### A. 消息注册系统迁移

#### 原版本 (1.12.2)

```java
public static FMLEventChannel millChannel =
    NetworkRegistry.INSTANCE.newEventDrivenChannel("millenaire");

static {
    millChannel.register(new ServerReceiver());
    millChannel.register(new ClientReceiver());
}
```

#### 迁移到 1.20.1

```java
public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
    new ResourceLocation(Mill.MODID, "main"),
    () -> "1.0",
    "1.0"::equals,
    "1.0"::equals
);

public static void registerMessages() {
    int id = 0;

    // GUI消息
    NETWORK.registerMessage(id++, OpenTradeGuiMessage.class,
        OpenTradeGuiMessage::encode,
        OpenTradeGuiMessage::decode,
        OpenTradeGuiMessage::handle);

    // 实体消息
    NETWORK.registerMessage(id++, VillagerDataMessage.class,
        VillagerDataMessage::encode,
        VillagerDataMessage::decode,
        VillagerDataMessage::handle);
}
```

### B. 消息类重构

#### 原版本消息处理

```java
@SubscribeEvent
public void onClientPacket(ClientCustomPacketEvent event) {
    try {
        ByteBuf buffer = event.packet.payload();
        int messageType = buffer.readInt();

        switch (messageType) {
            case MSG_OPEN_TRADE_GUI:
                handleOpenTradeGui(buffer);
                break;
        }
    } catch (Exception e) {
        MillLog.error("Error handling client packet: " + e.getMessage());
    }
}
```

#### 迁移到 1.20.1

```java
public class OpenTradeGuiMessage {
    private final BlockPos buildingPos;

    public OpenTradeGuiMessage(BlockPos pos) {
        this.buildingPos = pos;
    }

    public static void encode(OpenTradeGuiMessage msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.buildingPos);
    }

    public static OpenTradeGuiMessage decode(FriendlyByteBuf buf) {
        return new OpenTradeGuiMessage(buf.readBlockPos());
    }

    public static void handle(OpenTradeGuiMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            Building building = Mill.serverWorlds.get(0).getBuilding(new Point(msg.buildingPos));
            if (building != null) {
                NetworkHooks.openScreen(player, new TradeMenuProvider(building), msg.buildingPos);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
```

### C. GUI 打开机制迁移

#### 原版本 (1.12.2)

```java
// 服务端
player.openGui(Mill.instance, GUI_ID, world, x, y, z);

// 客户端GUI工厂
@Override
public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (ID == TRADE_GUI_ID) {
        Building building = Mill.clientWorld.getBuilding(new Point(x, y, z));
        return new GuiTrade(player, building);
    }
    return null;
}
```

#### 迁移到 1.20.1

```java
// 使用MenuProvider
public class TradeMenuProvider implements MenuProvider {
    private final Building building;

    public TradeMenuProvider(Building building) {
        this.building = building;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.millenaire.trade");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new ContainerTrade(id, playerInv, building);
    }
}

// 打开GUI
NetworkHooks.openScreen(player, new TradeMenuProvider(building), building.getPos());
```

## 6. 数据同步系统

### A. 实体数据同步

#### 村民数据同步

```java
// 原版本
public void sendVillagerData(MillVillager villager) {
    ByteBuf buffer = Unpooled.buffer();
    buffer.writeInt(MSG_VILLAGER_DATA);
    buffer.writeLong(villager.getUniqueID().getMostSignificantBits());
    buffer.writeLong(villager.getUniqueID().getLeastSignificantBits());
    // ... 写入其他数据

    millChannel.sendToAll(new FMLProxyPacket(buffer, "millenaire"));
}

// 1.20.1 需要重构
public class VillagerDataMessage {
    private final UUID villagerId;
    private final CompoundTag villagerData;

    // ... 编码/解码/处理方法
}
```

### B. 村庄数据同步

#### 建筑数据同步

- 建造进度同步
- 库存状态同步
- 村民分配同步
- 关系状态同步

## 7. 特殊 GUI 功能迁移

### A. 宗教祭祀系统 (GuiPujas)

#### 功能特点

- 祭品选择界面
- 祭祀效果预览
- 文化特定祭祀仪式

#### 迁移挑战

- 复杂的自定义渲染
- 特殊的交互逻辑
- 音效和粒子效果

### B. 军事控制界面 (GuiControlledMilitary)

#### 功能特点

- 军队编队管理
- 巡逻路径设置
- 战斗命令下达

#### 迁移要点

- 保持军事逻辑
- 更新渲染系统
- 适配新的输入处理

## 8. 客户端渲染更新

### A. 2D 渲染更新

```java
// 1.12.2
GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
GlStateManager.enableBlend();
this.drawTexturedModalRect(x, y, u, v, width, height);

// 1.20.1
RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
RenderSystem.enableBlend();
this.blit(poseStack, x, y, u, v, width, height);
```

### B. 文本渲染更新

```java
// 1.12.2
fontRenderer.drawString(text, x, y, color);
fontRenderer.drawStringWithShadow(text, x, y, color);

// 1.20.1
font.draw(poseStack, text, x, y, color);
font.drawShadow(poseStack, text, x, y, color);
```

## 9. 迁移优先级规划

### 第一阶段: 基础容器 GUI (3 周)

1. **ContainerTrade 迁移** - 核心贸易系统
2. **GuiTrade 界面** - 贸易界面
3. **基础网络消息** - 打开/关闭 GUI

### 第二阶段: 文本 GUI 系统 (2 周)

1. **GuiText 基类** - 文本界面基础
2. **GuiTravelBook** - 旅行手册
3. **本地化系统** - 多语言支持

### 第三阶段: 特殊功能 GUI (3 周)

1. **GuiPujas** - 宗教系统
2. **GuiLockedChest** - 箱子系统
3. **配置界面群** - 各种配置 GUI

### 第四阶段: 网络系统完善 (2 周)

1. **复杂数据同步** - 村庄/村民数据
2. **实时更新机制** - 建造进度等
3. **性能优化** - 减少网络流量

### 第五阶段: 高级功能 (2 周)

1. **军事控制界面**
2. **自定义建筑界面**
3. **调试和优化工具**

## 10. 预估工作量与风险

### 总体时间: 12-15 周 (3-4 个月)

#### 风险评估

**高风险项**:

- **网络消息重构** - 大量消息类型需要重写
- **复杂 GUI 渲染** - 自定义渲染逻辑复杂
- **数据同步机制** - 实时同步要求高

**中风险项**:

- **基础容器 GUI** - 标准化程度高
- **文本系统** - 相对独立
- **配置界面** - 逻辑简单

**低风险项**:

- **简单显示 GUI** - 只读界面
- **按钮导航** - 基础交互

### 成功关键因素

1. **保持界面一致性** - 用户体验不能下降
2. **网络稳定性** - 确保数据同步可靠
3. **性能优化** - 利用新版本性能提升
4. **向后兼容** - 保持存档兼容性
5. **充分测试** - 多人游戏环境测试
