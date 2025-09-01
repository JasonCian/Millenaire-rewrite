# 🎯 Millenaire 1.20.1 重制版 - 后续开发计划

## 📊 当前项目状态总结

### ✅ 已完成的工作
- **基础架构**: 现代化模组主类、注册系统、配置系统
- **物品系统**: 38个物品完整迁移（货币、食物、工具、护甲等）
- **本地化**: 中英文双语支持
- **资源系统**: 材质、模型、数据生成器

### 🔧 待解决的关键问题
- **魔法物品动态显示**: 护符贴图需要根据功能状态变化
- **村庄核心系统**: 尚未开始实现
- **实体系统**: 村民AI和行为系统
- **建筑系统**: 动态建筑生成

## 📅 详细开发计划

### 🔴 优先级1: 魔法物品系统修复 (1-2周)

#### 问题诊断
根据legacy代码分析，护符通过`getColorFromItemStack()`方法实现动态颜色变化：
- **炼金术师护符**: 根据附近矿物数量改变颜色(16色阶)
- **毗湿奴护符**: 根据附近怪物距离改变颜色(16色阶)  
- **世界树护符**: 根据玩家高度改变颜色(32色阶)

#### 现代化解决方案
1. **实现动态材质系统**
   - 创建`DynamicAmuletItem`类继承`Item`
   - 实现1.20.1的动态模型系统和NBT数据
   - 使用客户端tick事件更新护符状态

2. **颜色映射现代化**
   - 将legacy的颜色数组转换为现代RGB系统
   - 实现平滑颜色过渡效果
   - 添加可配置的颜色主题

3. **性能优化**
   - 限制检测频率(每20tick检测一次)
   - 使用缓存避免重复计算
   - 异步处理复杂的范围检测

#### 技术实现方案
```java
public class DynamicAmuletItem extends Item {
    // 实现现代化的动态物品显示
    // 使用ItemStack NBT存储状态数据
    // 客户端渲染时根据NBT调整显示效果
}
```

### 🟡 优先级2: 村庄核心系统 (3-4周)

#### 2.1 村庄数据结构现代化
```java
public class Village extends SavedData {
    // 使用1.20.1的SavedData系统
    // 实现村庄边界、建筑计划、资源管理
    
    private UUID villageId;
    private String cultureName;
    private BlockPos centerPos;
    private Set<BuildingProject> buildings;
    private VillageEconomy economy;
}

public class VillageManager extends SavedData {
    // 全局村庄跟踪和管理
    // 支持多维度村庄
    private Map<UUID, Village> villages;
    private Map<ResourceKey<Level>, Set<UUID>> dimensionVillages;
}
```

#### 2.2 村庄生成集成
- **与原版村庄系统共存**: 确保不冲突
- **生物群系适配**: 根据生物群系生成相应文化的村庄
- **可配置生成**: 允许玩家调整生成频率和规模

### 🟡 优先级3: 建筑系统重构 (2-3周)

#### 3.1 现代建筑生成引擎
```java
public class BuildingProject {
    // 使用Structure API实现
    // 支持异步建造
    // 资源需求计算
    
    private BuildingTemplate template;
    private BlockPos position;
    private BuildingPhase currentPhase;
    private Map<Item, Integer> requiredResources;
    private int progress; // 0-100%
}

public class BuildingTemplate {
    // JSON格式建筑模板
    // 支持变体和随机化
    // 文化特色适配
    
    private String templateName;
    private CultureType culture;
    private List<StructurePiece> pieces;
    private Map<String, Integer> variations;
}
```

#### 3.2 建筑进度系统
- **分阶段建造**: 模拟真实建筑过程
- **资源收集**: 村民自动收集建材
- **玩家参与**: 允许玩家加速建造过程

### 🟢 优先级4: 村民AI系统 (3-4周)

#### 4.1 现代实体系统
```java
public class EntityMillVillager extends AbstractVillager {
    // 继承原版抽象村民，添加自定义行为
    // 使用现代Goal系统
    // 支持复杂的工作调度
    
    private VillagerProfession millProfession;
    private Village homeVillage;
    private WorkSchedule workSchedule;
}

public class MillVillagerGoals {
    // 定制化AI目标
    public static final VillagerGoal COLLECT_RESOURCES = new CollectResourcesGoal();
    public static final VillagerGoal BUILD_STRUCTURE = new BuildStructureGoal();
    public static final VillagerGoal TRADE_WITH_PLAYER = new TradeWithPlayerGoal();
}
```

#### 4.2 智能行为系统
- **工作AI**: 资源采集、建造、农业
- **社交AI**: 村民间互动、交易网络
- **防御AI**: 应对威胁、巡逻系统

### 🟢 优先级5: 经济和贸易系统 (2-3周)

#### 5.1 货币系统完善
```java
public class VillageEconomy {
    private Map<Item, Integer> treasury;  // 村庄金库
    private Map<Item, Float> exchangeRates;  // 汇率系统
    private List<TradeRoute> tradeRoutes;  // 贸易路线
}

public class CurrencyExchange {
    // 文化间货币兑换
    public static float getExchangeRate(CultureType from, CultureType to);
    public static ItemStack convertCurrency(ItemStack source, CultureType targetCulture);
}
```

#### 5.2 复杂贸易网络
- **商路系统**: 村庄间贸易路线
- **市场波动**: 动态价格系统
- **贸易商**: NPC商队系统

### 🔵 优先级6: 文化系统扩展 (2-3周)

#### 6.1 文化差异化
```java
public enum CultureType {
    NORMAN("norman", 0x8B4513),
    BYZANTINE("byzantine", 0x800080),
    HINDI("hindi", 0xFF8C00),
    JAPANESE("japanese", 0xDC143C),
    MAYAN("mayan", 0x228B22);
    
    private final String name;
    private final int primaryColor;
    
    // 每种文化的特色建筑、食物、装备等
}

public class CultureManager {
    // 文化特性管理
    public static BuildingStyle getBuildingStyle(CultureType culture);
    public static List<VillagerProfession> getUniqueProfessions(CultureType culture);
    public static Map<Item, Float> getCulturalPreferences(CultureType culture);
}
```

#### 6.2 文化交流
- **外交系统**: 村庄间关系管理
- **文化传播**: 技术和知识的传播
- **冲突系统**: 文化间的竞争和合作

## 🛠️ 近期开发重点

### 第一步：修复魔法物品显示问题
1. 创建现代化的动态物品渲染系统
2. 实现护符的功能检测逻辑
3. 测试性能和显示效果

### 第二步：建立村庄基础框架
1. 设计村庄数据结构
2. 实现村庄保存/加载系统
3. 创建简单的村庄生成测试

### 第三步：开发核心村民实体
1. 创建基础村民实体类
2. 实现基本AI行为
3. 测试村民与村庄的交互

## 📈 里程碑目标

### 短期目标 (1个月内)
- ✅ 修复魔法物品动态显示
- ✅ 完成村庄核心数据结构
- ✅ 实现基础村民实体

### 中期目标 (3个月内)
- ✅ 完整的建筑生成系统
- ✅ 功能完善的村民AI
- ✅ 基础经济和贸易系统

### 长期目标 (6个月内)
- ✅ 多文化村庄系统
- ✅ 复杂的文化交流机制
- ✅ 完整的游戏平衡和优化

## 🎮 测试策略

### 单元测试
- 村庄数据结构测试
- 货币系统计算测试
- AI行为逻辑测试

### 集成测试
- 村庄与世界生成集成
- 村民与建筑系统协作
- 多村庄交互测试

### 性能测试
- 大量村庄的性能影响
- 内存使用优化
- 网络同步效率

## 🚀 技术优势

### 现代化架构
- 充分利用1.20.1的新特性
- 模块化设计便于维护
- 高性能的数据处理

### 向后兼容
- 支持legacy存档转换
- 保持经典玩法体验
- 可配置的现代化改进

### 扩展性
- 易于添加新文化
- 支持mod间协作
- 开放的API接口

---

**下一步行动**: 开始实现魔法物品的动态显示系统，这是当前最紧迫的技术问题。
