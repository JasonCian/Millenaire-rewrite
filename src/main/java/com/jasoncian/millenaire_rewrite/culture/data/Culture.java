package com.jasoncian.millenaire_rewrite.culture.data;

import com.google.gson.JsonObject;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 文化基类
 * 
 * 这是所有文化的抽象基类，定义了文化系统的核心数据结构和行为。
 * 每个文化实例包含该文化的所有基础信息、建筑样式、社会结构、经济特征等数据。
 * 
 * 设计原则：
 * - 高度可配置：通过JSON配置文件驱动文化特性
 * - 可扩展性：支持插件式添加新文化而不修改核心代码
 * - 数据驱动：文化差异通过数据而非代码来体现
 * - 网络同步：支持客户端-服务端文化数据同步
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class Culture {

    // ===== 核心属性 =====

    /** 文化类型标识符 */
    private final CultureType type;

    /** 文化显示名称（本地化后） */
    private String displayName;

    /** 文化描述信息 */
    private String description;

    /** 文化版本号（用于数据兼容性检查） */
    private String version;

    /** 文化是否已启用 */
    private boolean enabled;

    // ===== 社会结构数据 =====

    /** 支持的职业类型列表 */
    private Set<String> supportedProfessions;

    /** 社会等级制度 */
    private List<String> socialHierarchy;

    /** 文化特有的社会价值观和行为模式 */
    private Map<String, Object> socialValues;

    // ===== 建筑系统数据 =====

    /** 建筑风格标识符 */
    private String architecturalStyle;

    /** 支持的建筑类型列表 */
    private Set<String> buildingTypes;

    /** 建筑材料偏好 */
    private Map<String, Double> buildingMaterialPreferences;

    /** 建筑装饰风格配置 */
    private Map<String, Object> decorativeStyles;

    // ===== 经济系统数据 =====

    /** 使用的货币类型 */
    private List<String> currencyTypes;

    /** 贸易商品偏好 */
    private Map<String, Double> tradePreferences;

    /** 生产专长（该文化擅长生产的物品） */
    private Set<String> productionSpecialties;

    /** 经济发展模式配置 */
    private Map<String, Object> economicModel;

    // ===== 文化特色数据 =====

    /** 文化节日和庆典 */
    private List<Map<String, Object>> festivals;

    /** 文化特有的物品和装备 */
    private Set<String> culturalItems;

    /** 文化传统和习俗 */
    private Map<String, Object> traditions;

    /** 宗教信仰系统 */
    private Map<String, Object> religiousBeliefs;

    // ===== 技术与知识 =====

    /** 技术发展水平 */
    private Map<String, Integer> technologyLevels;

    /** 研究能力和偏好 */
    private Map<String, Double> researchAptitudes;

    /** 知识传承方式 */
    private Map<String, Object> knowledgeTransmission;

    // ===== 构造函数 =====

    /**
     * 创建一个新的文化实例
     * 
     * @param type 文化类型
     */
    public Culture(CultureType type) {
        this.type = type;
        this.displayName = "";
        this.description = "";
        this.version = "1.0.0";
        this.enabled = true;

        // 初始化集合
        this.supportedProfessions = new HashSet<>();
        this.socialHierarchy = new ArrayList<>();
        this.socialValues = new HashMap<>();
        this.buildingTypes = new HashSet<>();
        this.buildingMaterialPreferences = new HashMap<>();
        this.decorativeStyles = new HashMap<>();
        this.currencyTypes = new ArrayList<>();
        this.tradePreferences = new HashMap<>();
        this.productionSpecialties = new HashSet<>();
        this.economicModel = new HashMap<>();
        this.festivals = new ArrayList<>();
        this.culturalItems = new HashSet<>();
        this.traditions = new HashMap<>();
        this.religiousBeliefs = new HashMap<>();
        this.technologyLevels = new HashMap<>();
        this.researchAptitudes = new HashMap<>();
        this.knowledgeTransmission = new HashMap<>();

        MillenaireLogger.debug(LogCategory.CULTURE, "Created new culture instance: {}", type.getName());
    }

    // ===== JSON 序列化/反序列化 =====

    /**
     * 从JSON对象创建文化实例
     * 
     * @param type 文化类型
     * @param json JSON配置对象
     * @return 创建的文化实例
     */
    public static Culture fromJson(CultureType type, JsonObject json) {
        Culture culture = new Culture(type);

        try {
            // 基础信息
            culture.displayName = GsonHelper.getAsString(json, "displayName", type.getName());
            culture.description = GsonHelper.getAsString(json, "description", "");
            culture.version = GsonHelper.getAsString(json, "version", "1.0.0");
            culture.enabled = GsonHelper.getAsBoolean(json, "enabled", true);

            // 社会结构
            if (json.has("social")) {
                JsonObject social = json.getAsJsonObject("social");
                culture.loadSocialData(social);
            }

            // 建筑系统
            if (json.has("architecture")) {
                JsonObject architecture = json.getAsJsonObject("architecture");
                culture.loadArchitecturalData(architecture);
            }

            // 经济系统
            if (json.has("economy")) {
                JsonObject economy = json.getAsJsonObject("economy");
                culture.loadEconomicData(economy);
            }

            // 文化特色
            if (json.has("culture_features")) {
                JsonObject features = json.getAsJsonObject("culture_features");
                culture.loadCulturalFeatures(features);
            }

            // 技术与知识
            if (json.has("technology")) {
                JsonObject technology = json.getAsJsonObject("technology");
                culture.loadTechnologyData(technology);
            }

            MillenaireLogger.info(LogCategory.CULTURE, "Successfully loaded culture {} from JSON", type.getName());

        } catch (Exception e) {
            MillenaireLogger.error(LogCategory.CULTURE, "Failed to load culture {} from JSON: {}",
                    type.getName(), e.getMessage());
            throw new RuntimeException("Failed to load culture data", e);
        }

        return culture;
    }

    /**
     * 将文化数据转换为JSON对象
     * 
     * @return JSON表示的文化数据
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        // 基础信息
        json.addProperty("displayName", displayName);
        json.addProperty("description", description);
        json.addProperty("version", version);
        json.addProperty("enabled", enabled);

        // 社会结构
        JsonObject social = new JsonObject();
        saveSocialData(social);
        json.add("social", social);

        // 建筑系统
        JsonObject architecture = new JsonObject();
        saveArchitecturalData(architecture);
        json.add("architecture", architecture);

        // 经济系统
        JsonObject economy = new JsonObject();
        saveEconomicData(economy);
        json.add("economy", economy);

        // 文化特色
        JsonObject features = new JsonObject();
        saveCulturalFeatures(features);
        json.add("culture_features", features);

        // 技术与知识
        JsonObject technology = new JsonObject();
        saveTechnologyData(technology);
        json.add("technology", technology);

        return json;
    }

    // ===== NBT 序列化/反序列化 =====

    /**
     * 将文化数据保存到NBT标签
     * 用于世界存档中的数据持久化
     * 
     * @return NBT标签
     */
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("type", type.getName());
        tag.putString("displayName", displayName);
        tag.putString("description", description);
        tag.putString("version", version);
        tag.putBoolean("enabled", enabled);

        // 保存社会结构数据
        CompoundTag socialTag = new CompoundTag();
        socialTag.putString("architecturalStyle", architecturalStyle);
        // TODO: 保存其他社会数据
        tag.put("social", socialTag);

        // 保存建筑数据
        CompoundTag architectureTag = new CompoundTag();
        // TODO: 保存建筑数据
        tag.put("architecture", architectureTag);

        // 保存经济数据
        CompoundTag economyTag = new CompoundTag();
        // TODO: 保存经济数据
        tag.put("economy", economyTag);

        return tag;
    }

    /**
     * 从NBT标签加载文化数据
     * 
     * @param tag NBT标签
     * @return 加载的文化实例，如果失败返回null
     */
    @Nullable
    public static Culture loadFromNBT(CompoundTag tag) {
        try {
            String typeName = tag.getString("type");
            CultureType type = CultureType.fromString(typeName);

            if (type == null) {
                MillenaireLogger.error(LogCategory.CULTURE, "Unknown culture type: {}", typeName);
                return null;
            }

            Culture culture = new Culture(type);
            culture.displayName = tag.getString("displayName");
            culture.description = tag.getString("description");
            culture.version = tag.getString("version");
            culture.enabled = tag.getBoolean("enabled");

            // 加载社会结构数据
            if (tag.contains("social")) {
                CompoundTag socialTag = tag.getCompound("social");
                culture.architecturalStyle = socialTag.getString("architecturalStyle");
                // TODO: 加载其他社会数据
            }

            // 加载建筑数据
            if (tag.contains("architecture")) {
                CompoundTag architectureTag = tag.getCompound("architecture");
                // TODO: 加载建筑数据 - 暂时忽略编译警告，后续完善
            }

            // 加载经济数据
            if (tag.contains("economy")) {
                CompoundTag economyTag = tag.getCompound("economy");
                // TODO: 加载经济数据 - 暂时忽略编译警告，后续完善
            }

            return culture;

        } catch (Exception e) {
            MillenaireLogger.error(LogCategory.CULTURE, "Failed to load culture from NBT: {}", e.getMessage());
            return null;
        }
    }

    // ===== 网络序列化 =====

    /**
     * 将文化数据写入网络缓冲区
     * 用于客户端-服务端同步
     * 
     * @param buffer 网络缓冲区
     */
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(type.getName());
        buffer.writeUtf(displayName);
        buffer.writeUtf(description);
        buffer.writeUtf(version);
        buffer.writeBoolean(enabled);
        buffer.writeUtf(architecturalStyle != null ? architecturalStyle : "");

        // 写入支持的职业
        buffer.writeVarInt(supportedProfessions.size());
        for (String profession : supportedProfessions) {
            buffer.writeUtf(profession);
        }

        // 写入建筑类型
        buffer.writeVarInt(buildingTypes.size());
        for (String buildingType : buildingTypes) {
            buffer.writeUtf(buildingType);
        }

        // 写入货币类型
        buffer.writeVarInt(currencyTypes.size());
        for (String currency : currencyTypes) {
            buffer.writeUtf(currency);
        }
    }

    /**
     * 从网络缓冲区读取文化数据
     * 
     * @param buffer 网络缓冲区
     * @return 读取的文化实例
     */
    public static Culture readFromNetwork(FriendlyByteBuf buffer) {
        String typeName = buffer.readUtf();
        CultureType type = CultureType.fromString(typeName);

        if (type == null) {
            throw new RuntimeException("Unknown culture type: " + typeName);
        }

        Culture culture = new Culture(type);
        culture.displayName = buffer.readUtf();
        culture.description = buffer.readUtf();
        culture.version = buffer.readUtf();
        culture.enabled = buffer.readBoolean();
        culture.architecturalStyle = buffer.readUtf();

        // 读取支持的职业
        int professionCount = buffer.readVarInt();
        for (int i = 0; i < professionCount; i++) {
            culture.supportedProfessions.add(buffer.readUtf());
        }

        // 读取建筑类型
        int buildingTypeCount = buffer.readVarInt();
        for (int i = 0; i < buildingTypeCount; i++) {
            culture.buildingTypes.add(buffer.readUtf());
        }

        // 读取货币类型
        int currencyCount = buffer.readVarInt();
        for (int i = 0; i < currencyCount; i++) {
            culture.currencyTypes.add(buffer.readUtf());
        }

        return culture;
    }

    // ===== 数据加载辅助方法 =====

    /**
     * 从JSON加载社会结构数据
     */
    private void loadSocialData(JsonObject social) {
        // 职业系统
        if (social.has("professions")) {
            social.getAsJsonArray("professions").forEach(element -> supportedProfessions.add(element.getAsString()));
        }

        // 社会等级
        if (social.has("hierarchy")) {
            social.getAsJsonArray("hierarchy").forEach(element -> socialHierarchy.add(element.getAsString()));
        }

        // 社会价值观
        if (social.has("values")) {
            JsonObject values = social.getAsJsonObject("values");
            values.entrySet().forEach(entry -> socialValues.put(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * 从JSON加载建筑数据
     */
    private void loadArchitecturalData(JsonObject architecture) {
        architecturalStyle = GsonHelper.getAsString(architecture, "style", "default");

        // 建筑类型
        if (architecture.has("building_types")) {
            architecture.getAsJsonArray("building_types").forEach(element -> buildingTypes.add(element.getAsString()));
        }

        // 材料偏好
        if (architecture.has("material_preferences")) {
            JsonObject materials = architecture.getAsJsonObject("material_preferences");
            materials.entrySet()
                    .forEach(entry -> buildingMaterialPreferences.put(entry.getKey(), entry.getValue().getAsDouble()));
        }

        // 装饰风格
        if (architecture.has("decorative_styles")) {
            JsonObject decorative = architecture.getAsJsonObject("decorative_styles");
            decorative.entrySet().forEach(entry -> decorativeStyles.put(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * 从JSON加载经济数据
     */
    private void loadEconomicData(JsonObject economy) {
        // 货币类型
        if (economy.has("currencies")) {
            economy.getAsJsonArray("currencies").forEach(element -> currencyTypes.add(element.getAsString()));
        }

        // 贸易偏好
        if (economy.has("trade_preferences")) {
            JsonObject trade = economy.getAsJsonObject("trade_preferences");
            trade.entrySet().forEach(entry -> tradePreferences.put(entry.getKey(), entry.getValue().getAsDouble()));
        }

        // 生产专长
        if (economy.has("production_specialties")) {
            economy.getAsJsonArray("production_specialties")
                    .forEach(element -> productionSpecialties.add(element.getAsString()));
        }

        // 经济模式
        if (economy.has("economic_model")) {
            JsonObject model = economy.getAsJsonObject("economic_model");
            model.entrySet().forEach(entry -> economicModel.put(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * 从JSON加载文化特色数据
     */
    private void loadCulturalFeatures(JsonObject features) {
        // 文化物品
        if (features.has("cultural_items")) {
            features.getAsJsonArray("cultural_items").forEach(element -> culturalItems.add(element.getAsString()));
        }

        // 传统习俗
        if (features.has("traditions")) {
            JsonObject traditionObj = features.getAsJsonObject("traditions");
            traditionObj.entrySet().forEach(entry -> traditions.put(entry.getKey(), entry.getValue()));
        }

        // 宗教信仰
        if (features.has("religious_beliefs")) {
            JsonObject religious = features.getAsJsonObject("religious_beliefs");
            religious.entrySet().forEach(entry -> religiousBeliefs.put(entry.getKey(), entry.getValue()));
        }

        // 节日庆典
        if (features.has("festivals")) {
            features.getAsJsonArray("festivals").forEach(element -> {
                Map<String, Object> festival = new HashMap<>();
                JsonObject festivalObj = element.getAsJsonObject();
                festivalObj.entrySet().forEach(entry -> festival.put(entry.getKey(), entry.getValue()));
                festivals.add(festival);
            });
        }
    }

    /**
     * 从JSON加载技术数据
     */
    private void loadTechnologyData(JsonObject technology) {
        // 技术水平
        if (technology.has("levels")) {
            JsonObject levels = technology.getAsJsonObject("levels");
            levels.entrySet().forEach(entry -> technologyLevels.put(entry.getKey(), entry.getValue().getAsInt()));
        }

        // 研究能力
        if (technology.has("research_aptitudes")) {
            JsonObject research = technology.getAsJsonObject("research_aptitudes");
            research.entrySet().forEach(entry -> researchAptitudes.put(entry.getKey(), entry.getValue().getAsDouble()));
        }

        // 知识传承
        if (technology.has("knowledge_transmission")) {
            JsonObject knowledge = technology.getAsJsonObject("knowledge_transmission");
            knowledge.entrySet().forEach(entry -> knowledgeTransmission.put(entry.getKey(), entry.getValue()));
        }
    }

    // ===== 数据保存辅助方法 =====

    private void saveSocialData(JsonObject social) {
        // TODO: 实现社会数据保存
    }

    private void saveArchitecturalData(JsonObject architecture) {
        // TODO: 实现建筑数据保存
    }

    private void saveEconomicData(JsonObject economy) {
        // TODO: 实现经济数据保存
    }

    private void saveCulturalFeatures(JsonObject features) {
        // TODO: 实现文化特色数据保存
    }

    private void saveTechnologyData(JsonObject technology) {
        // TODO: 实现技术数据保存
    }

    // ===== Getter 方法 =====

    public CultureType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getArchitecturalStyle() {
        return architecturalStyle;
    }

    public Set<String> getSupportedProfessions() {
        return Collections.unmodifiableSet(supportedProfessions);
    }

    public List<String> getSocialHierarchy() {
        return Collections.unmodifiableList(socialHierarchy);
    }

    public Map<String, Object> getSocialValues() {
        return Collections.unmodifiableMap(socialValues);
    }

    public Set<String> getBuildingTypes() {
        return Collections.unmodifiableSet(buildingTypes);
    }

    public Map<String, Double> getBuildingMaterialPreferences() {
        return Collections.unmodifiableMap(buildingMaterialPreferences);
    }

    public Map<String, Object> getDecorativeStyles() {
        return Collections.unmodifiableMap(decorativeStyles);
    }

    public List<String> getCurrencyTypes() {
        return Collections.unmodifiableList(currencyTypes);
    }

    public Map<String, Double> getTradePreferences() {
        return Collections.unmodifiableMap(tradePreferences);
    }

    public Set<String> getProductionSpecialties() {
        return Collections.unmodifiableSet(productionSpecialties);
    }

    public Map<String, Object> getEconomicModel() {
        return Collections.unmodifiableMap(economicModel);
    }

    public List<Map<String, Object>> getFestivals() {
        return Collections.unmodifiableList(festivals);
    }

    public Set<String> getCulturalItems() {
        return Collections.unmodifiableSet(culturalItems);
    }

    public Map<String, Object> getTraditions() {
        return Collections.unmodifiableMap(traditions);
    }

    public Map<String, Object> getReligiousBeliefs() {
        return Collections.unmodifiableMap(religiousBeliefs);
    }

    public Map<String, Integer> getTechnologyLevels() {
        return Collections.unmodifiableMap(technologyLevels);
    }

    public Map<String, Double> getResearchAptitudes() {
        return Collections.unmodifiableMap(researchAptitudes);
    }

    public Map<String, Object> getKnowledgeTransmission() {
        return Collections.unmodifiableMap(knowledgeTransmission);
    }

    // ===== Setter 方法 （用于配置和修改） =====

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setArchitecturalStyle(String architecturalStyle) {
        this.architecturalStyle = architecturalStyle;
    }

    // ===== 工具方法 =====

    /**
     * 检查文化是否支持指定职业
     * 
     * @param profession 职业名称
     * @return 如果支持返回true，否则返回false
     */
    public boolean supportsProfession(String profession) {
        return supportedProfessions.contains(profession);
    }

    /**
     * 检查文化是否支持指定建筑类型
     * 
     * @param buildingType 建筑类型
     * @return 如果支持返回true，否则返回false
     */
    public boolean supportsBuildingType(String buildingType) {
        return buildingTypes.contains(buildingType);
    }

    /**
     * 获取材料偏好值
     * 
     * @param material 材料名称
     * @return 偏好值（0.0-1.0），如果没有设置返回0.5
     */
    public double getMaterialPreference(String material) {
        return buildingMaterialPreferences.getOrDefault(material, 0.5);
    }

    /**
     * 获取贸易偏好值
     * 
     * @param item 物品名称
     * @return 偏好值（0.0-1.0），如果没有设置返回0.5
     */
    public double getTradePreference(String item) {
        return tradePreferences.getOrDefault(item, 0.5);
    }

    /**
     * 检查是否为生产专长物品
     * 
     * @param item 物品名称
     * @return 如果是专长物品返回true，否则返回false
     */
    public boolean isProductionSpecialty(String item) {
        return productionSpecialties.contains(item);
    }

    /**
     * 获取技术等级
     * 
     * @param technology 技术名称
     * @return 技术等级，如果没有设置返回0
     */
    public int getTechnologyLevel(String technology) {
        return technologyLevels.getOrDefault(technology, 0);
    }

    /**
     * 获取研究能力值
     * 
     * @param field 研究领域
     * @return 能力值（0.0-1.0），如果没有设置返回0.5
     */
    public double getResearchAptitude(String field) {
        return researchAptitudes.getOrDefault(field, 0.5);
    }

    @Override
    public String toString() {
        return "Culture{" +
                "type=" + type +
                ", displayName='" + displayName + '\'' +
                ", version='" + version + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Culture culture = (Culture) o;
        return type == culture.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
