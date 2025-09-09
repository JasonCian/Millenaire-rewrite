package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.init.ModItems;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

/**
 * 物品模型数据生成器
 * 
 * 自动生成所有模组物品的基础模型文件，减少手动创建模型的工作量，提高开发效率。
 * 遵循千年村庄重写版的数据生成规范，所有物品模型必须通过此类生成。
 *
 * 功能特性：
 * - 简单物品模型生成
 * - 工具物品模型生成
 * - 文化特色物品模型生成
 * - 自定义材质路径支持
 * - 为各文化系统预留扩展接口
 *
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
public class MillenaireItemModelProvider extends ItemModelProvider {

    public MillenaireItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        MillenaireLogger.info(LogCategory.DATAGEN, "正在生成物品模型数据...");

        // TODO: 在这里添加物品模型生成
        // 示例：
        // simpleItem(ModItems.CULTURE_SCROLL.get());
        // simpleItem(ModItems.DENIER.get());

        // 为文化系统预留的扩展点
        generateCultureItems();

        // 为货币系统预留的扩展点
        generateCurrencyItems();

        // 为工具系统预留的扩展点
        generateToolItems();

        // 为装饰物品预留的扩展点
        generateDecorativeItems();

        // 为贸易商品预留的扩展点
        generateTradeItems();

        // 为特殊物品预留的扩展点
        generateSpecialItems();

        MillenaireLogger.info(LogCategory.DATAGEN, "物品模型数据生成完成");
    }

    /**
     * 生成文化相关物品的模型
     * 
     * 包括各种文化特色物品：
     * - Norman（诺曼）文化物品
     * - Japanese（日本）文化物品
     * - Byzantine（拜占庭）文化物品
     * - Huaxia（华夏）文化物品
     */
    private void generateCultureItems() {
        // TODO: 实现文化物品的模型生成
        // 这将在文化系统实现时添加
    }

    /**
     * 生成货币相关物品的模型
     * 
     * 包括各种货币类型：
     * - Denier（德尼尔）
     * - DenierOr（金德尼尔）
     * - DenierArgent（银德尼尔）
     * - 华夏货币（铜钱、银两、金锭等）
     * - 通用钱包
     */
    private void generateCurrencyItems() {
        // 华夏货币系统
        simpleItem(ModItems.HUAXIA_COPPER_COIN);
        simpleItem(ModItems.HUAXIA_SILVER_TAEL);
        simpleItem(ModItems.HUAXIA_GOLD_INGOT);
        
        // 通用钱包
        simpleItem(ModItems.UNIVERSAL_WALLET);
        
        // TODO: 添加其他文化的货币物品
        // simpleItem(ModItems.DENIER);
        // simpleItem(ModItems.DENIER_OR);
        // simpleItem(ModItems.DENIER_ARGENT);
    }

    /**
     * 生成工具相关物品的模型
     * 
     * 包括各种文化特色工具和武器
     */
    private void generateToolItems() {
        // ===== 华夏工具物品模型 =====
        
        // 农具类
        toolItem(ModItems.HUAXIA_BAMBOO_HOE);
        toolItem(ModItems.HUAXIA_BRONZE_SICKLE);
        toolItem(ModItems.HUAXIA_IRON_FLAIL);
        
        // 手工业工具类
        toolItem(ModItems.HUAXIA_BAMBOO_CHISEL);
        toolItem(ModItems.HUAXIA_IRON_SAW);
        toolItem(ModItems.HUAXIA_BRONZE_HAMMER);
        
        // 特殊工具类
        simpleItem(ModItems.HUAXIA_ABACUS);
        simpleItem(ModItems.HUAXIA_BAMBOO_SCROLL);
        simpleItem(ModItems.HUAXIA_COMPASS);
        
        // ===== 华夏武器物品模型 =====
        
        // 剑类武器
        toolItem(ModItems.HUAXIA_BRONZE_JIAN);
        toolItem(ModItems.HUAXIA_IRON_DAO);
        toolItem(ModItems.HUAXIA_STEEL_JIAN_MASTERWORK);
        
        // 长兵器类
        toolItem(ModItems.HUAXIA_IRON_QIANG);
        toolItem(ModItems.HUAXIA_STEEL_JI);
        
        // 特殊武器类
        toolItem(ModItems.HUAXIA_SHUANG_DAO);
        toolItem(ModItems.HUAXIA_BIAN_FINE);
        
        // TODO: 添加其他文化的工具和武器
        // toolItem(ModItems.NORMAN_SWORD);
        // toolItem(ModItems.JAPANESE_KATANA);
        // toolItem(ModItems.BYZANTINE_CROSSBOW);
    }

    /**
     * 生成装饰性物品的模型
     * 
     * 包括各种装饰物品、护身符等
     */
    private void generateDecorativeItems() {
        // ===== 华夏食物物品模型 =====
        
        // 主食类
        simpleItem(ModItems.HUAXIA_RICE);
        simpleItem(ModItems.HUAXIA_NOODLES);
        simpleItem(ModItems.HUAXIA_BAOZI);
        simpleItem(ModItems.HUAXIA_JIAOZI);
        
        // 菜肴类
        simpleItem(ModItems.HUAXIA_MAPO_TOFU);
        simpleItem(ModItems.HUAXIA_KUNG_PAO_CHICKEN);
        simpleItem(ModItems.HUAXIA_BRAISED_PORK);
        
        // 小食类
        simpleItem(ModItems.HUAXIA_MOONCAKE);
        simpleItem(ModItems.HUAXIA_TANGYUAN);
        simpleItem(ModItems.HUAXIA_ZONGZI);
        
        // 饮品类
        simpleItem(ModItems.HUAXIA_GREEN_TEA);
        simpleItem(ModItems.HUAXIA_OOLONG_TEA);
        simpleItem(ModItems.HUAXIA_HUANGJIU);
        
        // 药膳类
        simpleItem(ModItems.HUAXIA_GINSENG_SOUP);
        simpleItem(ModItems.HUAXIA_BIRD_NEST);
        simpleItem(ModItems.HUAXIA_GOJI_TEA);
        
        // TODO: 添加其他装饰性物品
        // simpleItem(ModItems.VILLAGE_BANNER);
        // simpleItem(ModItems.CULTURE_AMULET);
    }

    /**
     * 生成贸易商品物品的模型
     * 
     * 包括各种文化的贸易商品：
     * - 华夏贸易商品（茶叶、瓷器、丝绸、香料）
     * - 其他文化的贸易商品
     */
    private void generateTradeItems() {
        // ===== 华夏茶叶类贸易商品 =====
        simpleItem(ModItems.HUAXIA_GREEN_TEA_LEAVES);
        simpleItem(ModItems.HUAXIA_OOLONG_TEA_LEAVES);
        simpleItem(ModItems.HUAXIA_LONGJING_TEA_LEAVES);
        simpleItem(ModItems.HUAXIA_DAHONGPAO_TEA_LEAVES);
        
        // ===== 华夏瓷器类贸易商品 =====
        simpleItem(ModItems.HUAXIA_CELADON);
        simpleItem(ModItems.HUAXIA_WHITE_PORCELAIN);
        simpleItem(ModItems.HUAXIA_BLUE_WHITE_PORCELAIN);
        simpleItem(ModItems.HUAXIA_DOUCAI_PORCELAIN);
        
        // ===== 华夏丝绸类贸易商品 =====
        simpleItem(ModItems.HUAXIA_RAW_SILK);
        simpleItem(ModItems.HUAXIA_SILK_FABRIC);
        simpleItem(ModItems.HUAXIA_BROCADE);
        simpleItem(ModItems.HUAXIA_CLOUD_BROCADE);
        
        // ===== 华夏香料类贸易商品 =====
        simpleItem(ModItems.HUAXIA_STAR_ANISE);
        simpleItem(ModItems.HUAXIA_SICHUAN_PEPPER);
        simpleItem(ModItems.HUAXIA_CINNAMON);
        simpleItem(ModItems.HUAXIA_AGARWOOD);
        
        // TODO: 添加其他文化的贸易商品
        // simpleItem(ModItems.NORMAN_WOOL);
        // simpleItem(ModItems.JAPANESE_SAKE);
        // simpleItem(ModItems.BYZANTINE_PURPLE_DYE);
    }

    /**
     * 生成特殊物品的模型
     * 
     * 包括各种文化的特殊物品：
     * - 华夏特殊物品（卷轴、印章、风水罗盘）
     * - 其他文化的特殊物品
     */
    private void generateSpecialItems() {
        // ===== 华夏卷轴类特殊物品 =====
        simpleItem(ModItems.HUAXIA_BLUEPRINT_SCROLL);
        simpleItem(ModItems.HUAXIA_TRADE_RECORD_SCROLL);
        simpleItem(ModItems.HUAXIA_VILLAGE_MAP_SCROLL);
        simpleItem(ModItems.HUAXIA_SPELL_SCROLL);
        
        // ===== 华夏印章类特殊物品 =====
        simpleItem(ModItems.HUAXIA_VILLAGE_CHIEF_SEAL);
        simpleItem(ModItems.HUAXIA_TRADE_SEAL);
        simpleItem(ModItems.HUAXIA_ARTISAN_SEAL);
        simpleItem(ModItems.HUAXIA_IMPERIAL_SEAL);
        
        // ===== 华夏风水罗盘类特殊物品 =====
        simpleItem(ModItems.HUAXIA_BASIC_FENGSHUI_COMPASS);
        simpleItem(ModItems.HUAXIA_ADVANCED_FENGSHUI_COMPASS);
        simpleItem(ModItems.HUAXIA_MASTER_FENGSHUI_COMPASS);
        
        // TODO: 添加其他文化的特殊物品
        // simpleItem(ModItems.VILLAGE_WAND);
        // simpleItem(ModItems.NORMAN_SEAL);
        // simpleItem(ModItems.JAPANESE_SCROLL);
        // simpleItem(ModItems.BYZANTINE_COMPASS);
    }

    /**
     * 为简单物品生成标准模型
     * 
     * @param item 要生成模型的物品
     */
    @SuppressWarnings("unused")
    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        // 使用 getBuilder 而不是 withExistingParent 来避免纹理存在性检查
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/" + item.getId().getPath()));
    }

    /**
     * 为有自定义材质路径的物品生成模型
     * 
     * @param item        要生成模型的物品
     * @param texturePath 自定义材质路径
     */
    @SuppressWarnings("unused")
    private ItemModelBuilder simpleItemWithTexture(RegistryObject<Item> item, String texturePath) {
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc(texturePath));
    }

    /**
     * 为工具类物品生成模型
     * 
     * @param item 要生成模型的工具物品
     */
    @SuppressWarnings("unused")
    private ItemModelBuilder toolItem(RegistryObject<Item> item) {
        return getBuilder(item.getId().getPath())
                .parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", modLoc("item/" + item.getId().getPath()));
    }
}
