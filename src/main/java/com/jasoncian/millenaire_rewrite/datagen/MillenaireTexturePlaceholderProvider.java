package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import com.google.common.hash.HashCode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;

/**
 * 纹理占位文件生成器
 * 
 * 自动生成纹理占位文件，为纹理艺术家提供开发基础。
 * 生成的占位文件包含物品名称标识，方便纹理开发和识别。
 * 
 * 功能特性：
 * - 自动生成16x16像素占位纹理
 * - 包含物品/方块名称文字标识
 * - 支持不同颜色主题（文化区分）
 * - 生成标准Minecraft纹理格式
 * - 为各文化系统预留扩展接口
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
public class MillenaireTexturePlaceholderProvider implements DataProvider {

    private final PackOutput output;
    private final String modId;

    public MillenaireTexturePlaceholderProvider(PackOutput output) {
        this.output = output;
        this.modId = ModConstants.MOD_ID;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            try {
                MillenaireLogger.info(LogCategory.DATAGEN, "正在生成纹理占位文件...");

                // 生成一些测试用的占位文件来验证系统工作
                generateTestPlaceholders(cache);

                generateItemTexturePlaceholders(cache);
                generateBlockTexturePlaceholders(cache);
                generateCultureSpecificTextures(cache);
                generateGUITexturePlaceholders(cache);

                MillenaireLogger.info(LogCategory.DATAGEN, "纹理占位文件生成完成");
            } catch (Exception e) {
                MillenaireLogger.error(LogCategory.DATAGEN, "纹理占位文件生成失败", e);
            }
        });
    }

    @Override
    public String getName() {
        return "Texture Placeholders: " + modId;
    }

    /**
     * 生成测试占位文件来验证系统工作
     */
    private void generateTestPlaceholders(CachedOutput cache) {
        try {
            // 生成一个简单的测试占位图
            // generateTexturePlaceholder(cache, "item/test_item", "测试物品", Color.BLUE, 16,
            // 16);
            // generateTexturePlaceholder(cache, "block/test_block", "测试方块", Color.GREEN,
            // 16, 16);

            MillenaireLogger.info(LogCategory.DATAGEN, "测试占位文件生成完成");
        } catch (Exception e) {
            MillenaireLogger.error(LogCategory.DATAGEN, "测试占位文件生成失败", e);
        }
    }

    /**
     * 生成物品纹理占位文件
     */
    private void generateItemTexturePlaceholders(CachedOutput cache) throws IOException {
        // ===== 华夏货币物品 =====
        generateTexturePlaceholder(cache, "item/huaxia_copper_coin", "铜钱", new Color(205, 127, 50));
        generateTexturePlaceholder(cache, "item/huaxia_silver_tael", "银两", new Color(192, 192, 192));
        generateTexturePlaceholder(cache, "item/huaxia_gold_ingot", "金锭", new Color(255, 215, 0));

        // ===== 通用钱包 =====
        generateTexturePlaceholder(cache, "item/universal_wallet", "钱包", new Color(139, 69, 19));

        // ===== 华夏工具物品 =====
        // 农具类
        generateTexturePlaceholder(cache, "item/huaxia_bamboo_hoe", "锄头", new Color(34, 139, 34));
        generateTexturePlaceholder(cache, "item/huaxia_bronze_sickle", "镰刀", new Color(205, 127, 50));
        generateTexturePlaceholder(cache, "item/huaxia_iron_flail", "连枷", new Color(128, 128, 128));
        
        // 手工业工具类
        generateTexturePlaceholder(cache, "item/huaxia_bamboo_chisel", "凿子", new Color(34, 139, 34));
        generateTexturePlaceholder(cache, "item/huaxia_iron_saw", "锯子", new Color(128, 128, 128));
        generateTexturePlaceholder(cache, "item/huaxia_bronze_hammer", "锤子", new Color(205, 127, 50));
        
        // 特殊工具类
        generateTexturePlaceholder(cache, "item/huaxia_abacus", "算盘", new Color(139, 69, 19));
        generateTexturePlaceholder(cache, "item/huaxia_bamboo_scroll", "竹简", new Color(34, 139, 34));
        generateTexturePlaceholder(cache, "item/huaxia_compass", "罗盘", new Color(205, 127, 50));

        // ===== 华夏武器物品 =====
        // 剑类武器
        generateTexturePlaceholder(cache, "item/huaxia_bronze_jian", "剑", new Color(205, 127, 50));
        generateTexturePlaceholder(cache, "item/huaxia_iron_dao", "刀", new Color(128, 128, 128));
        generateTexturePlaceholder(cache, "item/huaxia_steel_jian_masterwork", "精剑", new Color(169, 169, 169));
        
        // 长兵器类
        generateTexturePlaceholder(cache, "item/huaxia_iron_qiang", "枪", new Color(128, 128, 128));
        generateTexturePlaceholder(cache, "item/huaxia_steel_ji", "戟", new Color(169, 169, 169));
        
        // 特殊武器类
        generateTexturePlaceholder(cache, "item/huaxia_shuang_dao", "双刀", new Color(128, 128, 128));
        generateTexturePlaceholder(cache, "item/huaxia_bian_fine", "鞭", new Color(139, 69, 19));

        // ===== 华夏食物物品 =====
        // 主食类
        generateTexturePlaceholder(cache, "item/huaxia_rice", "米饭", new Color(255, 248, 220));
        generateTexturePlaceholder(cache, "item/huaxia_noodles", "面条", new Color(255, 228, 196));
        generateTexturePlaceholder(cache, "item/huaxia_baozi", "包子", new Color(255, 255, 240));
        generateTexturePlaceholder(cache, "item/huaxia_jiaozi", "饺子", new Color(255, 228, 196));
        
        // 菜肴类
        generateTexturePlaceholder(cache, "item/huaxia_mapo_tofu", "豆腐", new Color(255, 250, 250));
        generateTexturePlaceholder(cache, "item/huaxia_kung_pao_chicken", "鸡丁", new Color(255, 165, 0));
        generateTexturePlaceholder(cache, "item/huaxia_braised_pork", "红烧肉", new Color(165, 42, 42));
        
        // 小食类
        generateTexturePlaceholder(cache, "item/huaxia_mooncake", "月饼", new Color(255, 218, 185));
        generateTexturePlaceholder(cache, "item/huaxia_tangyuan", "汤圆", new Color(255, 255, 255));
        generateTexturePlaceholder(cache, "item/huaxia_zongzi", "粽子", new Color(107, 142, 35));
        
        // 饮品类
        generateTexturePlaceholder(cache, "item/huaxia_green_tea", "绿茶", new Color(144, 238, 144));
        generateTexturePlaceholder(cache, "item/huaxia_oolong_tea", "乌龙茶", new Color(255, 165, 0));
        generateTexturePlaceholder(cache, "item/huaxia_huangjiu", "黄酒", new Color(255, 215, 0));
        
        // 药膳类
        generateTexturePlaceholder(cache, "item/huaxia_ginseng_soup", "人参汤", new Color(255, 228, 196));
        generateTexturePlaceholder(cache, "item/huaxia_bird_nest", "燕窝", new Color(255, 245, 238));
        generateTexturePlaceholder(cache, "item/huaxia_goji_tea", "枸杞茶", new Color(220, 20, 60));

        // ===== 华夏贸易商品 =====
        // 茶叶类
        generateTexturePlaceholder(cache, "item/huaxia_green_tea_leaves", "绿茶叶", new Color(34, 139, 34));
        generateTexturePlaceholder(cache, "item/huaxia_oolong_tea_leaves", "乌龙茶叶", new Color(107, 142, 35));
        generateTexturePlaceholder(cache, "item/huaxia_longjing_tea_leaves", "龙井茶叶", new Color(50, 205, 50));
        generateTexturePlaceholder(cache, "item/huaxia_dahongpao_tea_leaves", "大红袍", new Color(178, 34, 34));
        
        // 瓷器类
        generateTexturePlaceholder(cache, "item/huaxia_celadon", "青瓷", new Color(143, 188, 143));
        generateTexturePlaceholder(cache, "item/huaxia_white_porcelain", "白瓷", new Color(255, 255, 255));
        generateTexturePlaceholder(cache, "item/huaxia_blue_white_porcelain", "青花瓷", new Color(100, 149, 237));
        generateTexturePlaceholder(cache, "item/huaxia_doucai_porcelain", "斗彩瓷", new Color(138, 43, 226));
        
        // 丝绸类
        generateTexturePlaceholder(cache, "item/huaxia_raw_silk", "生丝", new Color(255, 228, 181));
        generateTexturePlaceholder(cache, "item/huaxia_silk_fabric", "绢布", new Color(255, 218, 185));
        generateTexturePlaceholder(cache, "item/huaxia_brocade", "锦缎", new Color(255, 215, 0));
        generateTexturePlaceholder(cache, "item/huaxia_cloud_brocade", "云锦", new Color(255, 20, 147));
        
        // 香料类
        generateTexturePlaceholder(cache, "item/huaxia_star_anise", "八角", new Color(139, 69, 19));
        generateTexturePlaceholder(cache, "item/huaxia_sichuan_pepper", "花椒", new Color(165, 42, 42));
        generateTexturePlaceholder(cache, "item/huaxia_cinnamon", "肉桂", new Color(210, 180, 140));
        generateTexturePlaceholder(cache, "item/huaxia_agarwood", "沉香", new Color(72, 61, 139));

        // ===== 华夏特殊物品 =====
        // 卷轴类
        generateTexturePlaceholder(cache, "item/huaxia_blueprint_scroll", "图纸", new Color(245, 245, 220));
        generateTexturePlaceholder(cache, "item/huaxia_trade_record_scroll", "贸易记录", new Color(255, 228, 196));
        generateTexturePlaceholder(cache, "item/huaxia_village_map_scroll", "村庄地图", new Color(255, 248, 220));
        generateTexturePlaceholder(cache, "item/huaxia_spell_scroll", "法术卷轴", new Color(138, 43, 226));
        
        // 印章类
        generateTexturePlaceholder(cache, "item/huaxia_village_chief_seal", "村长印", new Color(255, 215, 0));
        generateTexturePlaceholder(cache, "item/huaxia_trade_seal", "贸易印", new Color(192, 192, 192));
        generateTexturePlaceholder(cache, "item/huaxia_artisan_seal", "工匠印", new Color(205, 127, 50));
        generateTexturePlaceholder(cache, "item/huaxia_imperial_seal", "皇印", new Color(255, 215, 0));
        
        // 风水罗盘类
        generateTexturePlaceholder(cache, "item/huaxia_basic_fengshui_compass", "罗盘", new Color(205, 127, 50));
        generateTexturePlaceholder(cache, "item/huaxia_advanced_fengshui_compass", "精制罗盘", new Color(169, 169, 169));
        generateTexturePlaceholder(cache, "item/huaxia_master_fengshui_compass", "大师罗盘", new Color(255, 215, 0));

        // TODO: 其他文化的物品纹理占位将在对应文化实现时添加
        // Norman（诺曼）文化物品
        // Japanese（日本）文化物品
        // Byzantine（拜占庭）文化物品
    }

    /**
     * 生成方块纹理占位文件
     */
    private void generateBlockTexturePlaceholders(CachedOutput cache) throws IOException {
        // 基础建筑方块

        // Norman（诺曼）文化方块

        // Japanese（日本）文化方块

        // Byzantine（拜占庭）文化方块

        // Huaxia（华夏）文化方块
    }

    /**
     * 生成文化特定纹理占位文件
     */
    private void generateCultureSpecificTextures(CachedOutput cache) throws IOException {
        // 各文化旗帜

        // 各文化雕像
    }

    /**
     * 生成GUI纹理占位文件
     */
    private void generateGUITexturePlaceholders(CachedOutput cache) throws IOException {
        // GUI背景和组件（生成到gui文件夹）
        // 文化特色GUI
    }

    /**
     * 生成单个纹理占位文件（16x16）
     */
    private void generateTexturePlaceholder(CachedOutput cache, String texturePath, String text, Color bgColor)
            throws IOException {
        generateTexturePlaceholder(cache, texturePath, text, bgColor, 16, 16);
    }

    /**
     * 生成单个纹理占位文件（自定义尺寸）
     */
    private void generateTexturePlaceholder(CachedOutput cache, String texturePath, String text, Color bgColor,
            int width, int height) throws IOException {
        // 创建图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 填充背景
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);

        // 添加边框
        g2d.setColor(bgColor.darker());
        g2d.drawRect(0, 0, width - 1, height - 1);

        // 添加文字
        g2d.setColor(getContrastColor(bgColor));

        // 根据图像大小调整字体
        int fontSize = Math.max(6, Math.min(width / 4, height / 4));
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g2d.setFont(font);

        // 计算文字位置（居中）
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + fm.getAscent();

        g2d.drawString(text, x, y);
        g2d.dispose();

        // 保存文件
        Path textureFolderPath = output.getOutputFolder().resolve("assets").resolve(modId).resolve("textures");
        Path textureFilePath = textureFolderPath.resolve(texturePath + ".png");

        // 创建目录
        Files.createDirectories(textureFilePath.getParent());

        // 将图像写入字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();

        // 使用CachedOutput写入文件
        cache.writeIfNeeded(textureFilePath, imageBytes, HashCode.fromBytes(imageBytes));

        MillenaireLogger.info(LogCategory.DATAGEN, "生成纹理占位文件: {}", textureFilePath);
    }

    /**
     * 获取与背景色对比的文字颜色
     */
    private Color getContrastColor(Color bgColor) {
        // 计算亮度
        double brightness = (bgColor.getRed() * 0.299 + bgColor.getGreen() * 0.587 + bgColor.getBlue() * 0.114);
        return brightness > 128 ? Color.BLACK : Color.WHITE;
    }
}
