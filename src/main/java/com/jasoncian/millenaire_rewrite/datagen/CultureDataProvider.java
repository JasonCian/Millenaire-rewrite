package com.jasoncian.millenaire_rewrite.datagen;

import com.google.gson.JsonObject;
import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * 文化数据提供器
 * 
 * 为数据生成阶段创建文化配置文件，确保所有文化都有基础的
 * JSON配置文件可供加载。这个提供器会生成默认的文化配置，
 * 可以被数据包覆盖。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class CultureDataProvider implements DataProvider {

    /** 输出包 */
    private final PackOutput output;

    /**
     * 创建文化数据提供器
     * 
     * @param output 输出包
     */
    public CultureDataProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.allOf(
                generateCultureData(cache, CultureType.NORMAN),
                generateCultureData(cache, CultureType.BYZANTINE),
                generateCultureData(cache, CultureType.JAPANESE),
                generateCultureData(cache, CultureType.HUAXIA),
                generateCultureData(cache, CultureType.INDIAN),
                generateCultureData(cache, CultureType.MAYAN));
    }

    /**
     * 为指定文化类型生成数据文件
     * 
     * @param cache       缓存输出
     * @param cultureType 文化类型
     * @return 完成的Future
     */
    private CompletableFuture<?> generateCultureData(CachedOutput cache, CultureType cultureType) {
        JsonObject cultureData = createDefaultCultureData(cultureType);

        // 构建输出路径
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID,
                "cultures/" + cultureType.getName());
        Path outputPath = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(location.getNamespace())
                .resolve("cultures")
                .resolve(location.getPath() + ".json");

        return DataProvider.saveStable(cache, cultureData, outputPath);
    }

    /**
     * 为指定文化类型创建默认数据
     * 
     * @param cultureType 文化类型
     * @return 默认文化数据JSON
     */
    private JsonObject createDefaultCultureData(CultureType cultureType) {
        JsonObject data = new JsonObject();

        // 基础信息
        data.addProperty("displayName", getDisplayName(cultureType));
        data.addProperty("description", getDescription(cultureType));
        data.addProperty("version", "1.0.0");
        data.addProperty("enabled", true);

        // 社会结构
        JsonObject social = new JsonObject();
        addSocialData(social, cultureType);
        data.add("social", social);

        // 建筑系统
        JsonObject architecture = new JsonObject();
        addArchitecturalData(architecture, cultureType);
        data.add("architecture", architecture);

        // 经济系统
        JsonObject economy = new JsonObject();
        addEconomicData(economy, cultureType);
        data.add("economy", economy);

        // 文化特色
        JsonObject features = new JsonObject();
        addCulturalFeatures(features, cultureType);
        data.add("culture_features", features);

        // 技术系统
        JsonObject technology = new JsonObject();
        addTechnologyData(technology, cultureType);
        data.add("technology", technology);

        return data;
    }

    /**
     * 获取文化显示名称
     */
    private String getDisplayName(CultureType cultureType) {
        switch (cultureType) {
            case NORMAN:
                return "Norman Culture";
            case BYZANTINE:
                return "Byzantine Culture";
            case JAPANESE:
                return "Japanese Culture";
            case HUAXIA:
                return "华夏文化";
            case INDIAN:
                return "Indian Culture";
            case MAYAN:
                return "Mayan Culture";
            default:
                return cultureType.getName();
        }
    }

    /**
     * 获取文化描述
     */
    private String getDescription(CultureType cultureType) {
        switch (cultureType) {
            case NORMAN:
                return "Medieval European culture featuring castles, knights, and feudal society.";
            case BYZANTINE:
                return "Eastern Roman Empire culture blending Greek, Roman, and Christian traditions.";
            case JAPANESE:
                return "Traditional Japanese culture emphasizing honor, discipline, and harmony with nature.";
            case HUAXIA:
                return "中华传统文化，体现深厚的历史底蕴和独特的东方美学。";
            case INDIAN:
                return "South Asian culture rich in philosophy, spirituality, and diverse traditions.";
            case MAYAN:
                return "Ancient Mesoamerican civilization known for astronomy, mathematics, and monumental architecture.";
            default:
                return "A unique culture with its own traditions and characteristics.";
        }
    }

    /**
     * 添加社会结构数据
     */
    private void addSocialData(JsonObject social, CultureType cultureType) {
        switch (cultureType) {
            case NORMAN:
                addArrayProperty(social, "professions",
                        "knight", "farmer", "craftsman", "merchant", "priest", "baker", "blacksmith", "carpenter");
                addArrayProperty(social, "hierarchy", "lord", "knight", "citizen", "peasant");
                break;
            case BYZANTINE:
                addArrayProperty(social, "professions",
                        "merchant", "craftsman", "scholar", "soldier", "priest", "diplomat", "architect", "silk_weaver",
                        "scribe", "sailor");
                addArrayProperty(social, "hierarchy", "emperor", "noble", "official", "citizen", "peasant");
                break;
            case JAPANESE:
                addArrayProperty(social, "professions",
                        "samurai", "farmer", "artisan", "merchant", "monk", "tea_master", "sword_smith", "archer",
                        "fisherman", "poet");
                addArrayProperty(social, "hierarchy", "daimyo", "samurai", "ashigaru", "citizen", "peasant");
                break;
            case HUAXIA:
                addArrayProperty(social, "professions",
                        "scholar", "farmer", "artisan", "merchant", "official", "doctor", "teacher", "craftsman",
                        "tea_master", "calligrapher");
                addArrayProperty(social, "hierarchy", "emperor", "mandarin", "scholar", "citizen", "peasant");
                break;
            case INDIAN:
                addArrayProperty(social, "professions",
                        "brahmin", "kshatriya", "vaishya", "shudra", "scholar", "merchant", "craftsman", "farmer");
                addArrayProperty(social, "hierarchy", "raja", "brahmin", "kshatriya", "vaishya", "shudra");
                break;
            case MAYAN:
                addArrayProperty(social, "professions",
                        "priest", "astronomer", "scribe", "merchant", "craftsman", "farmer", "warrior");
                addArrayProperty(social, "hierarchy", "halach_uinic", "priest", "noble", "merchant", "craftsman",
                        "farmer");
                break;
        }
    }

    /**
     * 添加建筑数据
     */
    private void addArchitecturalData(JsonObject architecture, CultureType cultureType) {
        switch (cultureType) {
            case NORMAN:
                architecture.addProperty("style", "medieval_european");
                addArrayProperty(architecture, "building_types",
                        "castle", "manor_house", "peasant_house", "church", "farm", "market", "smithy", "mill",
                        "tavern", "guard_tower");
                break;
            case BYZANTINE:
                architecture.addProperty("style", "byzantine");
                addArrayProperty(architecture, "building_types",
                        "palace", "cathedral", "monastery", "basilica", "house", "workshop", "bazaar", "harbor",
                        "bathhouse", "library", "aqueduct");
                break;
            case JAPANESE:
                architecture.addProperty("style", "traditional_japanese");
                addArrayProperty(architecture, "building_types",
                        "dojo", "traditional_house", "temple", "shrine", "tea_house", "farm", "market", "zen_garden",
                        "pagoda", "castle", "bridge");
                break;
            case HUAXIA:
                architecture.addProperty("style", "traditional_chinese");
                addArrayProperty(architecture, "building_types",
                        "palace", "pagoda", "courtyard_house", "temple", "garden", "market", "tea_house", "study_hall",
                        "workshop", "pharmacy", "gate_tower", "bridge");
                break;
            case INDIAN:
                architecture.addProperty("style", "traditional_indian");
                addArrayProperty(architecture, "building_types",
                        "palace", "temple", "ashram", "house", "market", "workshop", "well", "stepwell");
                break;
            case MAYAN:
                architecture.addProperty("style", "mesoamerican");
                addArrayProperty(architecture, "building_types",
                        "pyramid", "temple", "observatory", "ball_court", "palace", "house", "market");
                break;
        }
    }

    /**
     * 添加经济数据
     */
    private void addEconomicData(JsonObject economy, CultureType cultureType) {
        switch (cultureType) {
            case NORMAN:
                addArrayProperty(economy, "currencies", "denier", "denier_or", "denier_argent");
                addArrayProperty(economy, "production_specialties", "wool", "grain", "metal_goods", "weapons", "armor",
                        "ale");
                break;
            case BYZANTINE:
                addArrayProperty(economy, "currencies", "solidus", "denier", "nomisma");
                addArrayProperty(economy, "production_specialties", "silk", "luxury_goods", "manuscripts", "jewelry",
                        "perfumes", "mosaics", "religious_artifacts");
                break;
            case JAPANESE:
                addArrayProperty(economy, "currencies", "koban", "mon", "ryo");
                addArrayProperty(economy, "production_specialties", "rice", "tea", "pottery", "swords", "bamboo_crafts",
                        "silk", "sake", "lacquerware");
                break;
            case HUAXIA:
                addArrayProperty(economy, "currencies", "copper_coin", "silver_tael", "gold_ingot");
                addArrayProperty(economy, "production_specialties", "tea", "silk", "porcelain", "jade_crafts",
                        "traditional_medicine", "paper", "ink", "fireworks", "lacquerware");
                break;
            case INDIAN:
                addArrayProperty(economy, "currencies", "gold_coin", "silver_coin", "copper_coin");
                addArrayProperty(economy, "production_specialties", "spices", "textiles", "gems", "incense", "ivory",
                        "sandalwood");
                break;
            case MAYAN:
                addArrayProperty(economy, "currencies", "cacao_bean", "jade_bead", "obsidian_blade");
                addArrayProperty(economy, "production_specialties", "cacao", "jade", "obsidian", "feathers", "cotton",
                        "rubber");
                break;
        }
    }

    /**
     * 添加文化特色数据
     */
    private void addCulturalFeatures(JsonObject features, CultureType cultureType) {
        switch (cultureType) {
            case NORMAN:
                addArrayProperty(features, "cultural_items",
                        "norman_sword", "norman_shield", "norman_helm", "chainmail", "norman_banner", "holy_book",
                        "ale_mug");
                break;
            case BYZANTINE:
                addArrayProperty(features, "cultural_items",
                        "byzantine_armor", "byzantine_cross", "silk_robe", "golden_chalice", "illuminated_manuscript",
                        "mosaic_tile", "incense_burner", "precious_gem", "imperial_seal");
                break;
            case JAPANESE:
                addArrayProperty(features, "cultural_items",
                        "katana", "wakizashi", "kimono", "tea_set", "sake_cup", "bamboo_flute", "origami",
                        "calligraphy_set", "zen_stone", "cherry_blossom");
                break;
            case HUAXIA:
                addArrayProperty(features, "cultural_items",
                        "chinese_sword", "silk_robe", "tea_set", "compass", "scroll", "ink_stone", "calligraphy_brush",
                        "jade_ornament", "paper_fan", "chinese_medicine");
                break;
            case INDIAN:
                addArrayProperty(features, "cultural_items",
                        "sari", "turban", "incense", "lotus", "mandala", "yoga_mat", "sitar", "tabla");
                break;
            case MAYAN:
                addArrayProperty(features, "cultural_items",
                        "feather_headdress", "jade_mask", "obsidian_knife", "cacao_pod", "calendar_stone",
                        "ball_game_equipment");
                break;
        }
    }

    /**
     * 添加技术数据
     */
    private void addTechnologyData(JsonObject technology, CultureType cultureType) {
        JsonObject levels = new JsonObject();
        JsonObject aptitudes = new JsonObject();

        switch (cultureType) {
            case NORMAN:
                addTechLevels(levels, 4, 5, 4, 4, 5, 3, 3, 2, 2, 3, 3);
                addTechAptitudes(aptitudes, 0.7, 0.8, 0.7, 0.6, 0.8, 0.4, 0.5, 0.3, 0.3, 0.4, 0.6);
                break;
            case BYZANTINE:
                addTechLevels(levels, 4, 6, 5, 4, 5, 6, 7, 4, 5, 5, 7);
                addTechAptitudes(aptitudes, 0.6, 0.8, 0.7, 0.6, 0.7, 0.8, 0.85, 0.6, 0.7, 0.7, 0.9);
                break;
            case JAPANESE:
                addTechLevels(levels, 5, 4, 6, 6, 6, 4, 5, 3, 4, 7, 4);
                addTechAptitudes(aptitudes, 0.8, 0.6, 0.85, 0.8, 0.8, 0.6, 0.7, 0.5, 0.6, 0.9, 0.6);
                break;
            case HUAXIA:
                addTechLevels(levels, 6, 5, 7, 5, 4, 5, 8, 6, 6, 6, 5);
                addTechAptitudes(aptitudes, 0.8, 0.7, 0.9, 0.7, 0.6, 0.7, 0.95, 0.8, 0.8, 0.8, 0.7);
                break;
            case INDIAN:
                addTechLevels(levels, 5, 4, 5, 3, 3, 4, 7, 7, 6, 6, 6);
                addTechAptitudes(aptitudes, 0.7, 0.6, 0.7, 0.5, 0.5, 0.6, 0.9, 0.9, 0.8, 0.8, 0.8);
                break;
            case MAYAN:
                addTechLevels(levels, 4, 6, 4, 2, 4, 3, 5, 3, 7, 5, 4);
                addTechAptitudes(aptitudes, 0.6, 0.8, 0.6, 0.4, 0.6, 0.5, 0.7, 0.5, 0.9, 0.7, 0.6);
                break;
        }

        technology.add("levels", levels);
        technology.add("research_aptitudes", aptitudes);
    }

    /**
     * 添加技术等级
     */
    private void addTechLevels(JsonObject levels, int agriculture, int construction, int craftsmanship,
            int metallurgy, int military, int navigation, int scholarship,
            int medicine, int astronomy, int aesthetics, int trade) {
        levels.addProperty("agriculture", agriculture);
        levels.addProperty("construction", construction);
        levels.addProperty("craftsmanship", craftsmanship);
        levels.addProperty("metallurgy", metallurgy);
        levels.addProperty("military", military);
        levels.addProperty("navigation", navigation);
        levels.addProperty("scholarship", scholarship);
        levels.addProperty("medicine", medicine);
        levels.addProperty("astronomy", astronomy);
        levels.addProperty("aesthetics", aesthetics);
        levels.addProperty("trade", trade);
    }

    /**
     * 添加技术能力值
     */
    private void addTechAptitudes(JsonObject aptitudes, double agriculture, double construction, double craftsmanship,
            double metallurgy, double military, double navigation, double scholarship,
            double medicine, double astronomy, double aesthetics, double trade) {
        aptitudes.addProperty("agriculture", agriculture);
        aptitudes.addProperty("construction", construction);
        aptitudes.addProperty("craftsmanship", craftsmanship);
        aptitudes.addProperty("metallurgy", metallurgy);
        aptitudes.addProperty("military", military);
        aptitudes.addProperty("navigation", navigation);
        aptitudes.addProperty("scholarship", scholarship);
        aptitudes.addProperty("medicine", medicine);
        aptitudes.addProperty("astronomy", astronomy);
        aptitudes.addProperty("aesthetics", aesthetics);
        aptitudes.addProperty("trade", trade);
    }

    /**
     * 添加数组属性的辅助方法
     */
    private void addArrayProperty(JsonObject object, String propertyName, String... values) {
        StringBuilder arrayStr = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0)
                arrayStr.append(", ");
            arrayStr.append("\"").append(values[i]).append("\"");
        }
        arrayStr.append("]");
        object.addProperty(propertyName, arrayStr.toString());
    }

    @Override
    public String getName() {
        return "Culture Data";
    }
}
