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
                generateEntityTexturePlaceholders(cache);
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
            // generateTexturePlaceholder(cache, "item/test_item", "测试物品", Color.BLUE, 16, 16);
            // generateTexturePlaceholder(cache, "block/test_block", "测试方块", Color.GREEN, 16, 16);

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

        // ===== 开发工具物品 =====
        generateTexturePlaceholder(cache, "item/mill_villager_spawner", "村民生成器", new Color(144, 238, 144));

        // TODO: 其他文化的物品纹理占位将在对应文化实现时添加
        // Norman（诺曼）文化物品
        // Japanese（日本）文化物品
        // Byzantine（拜占庭）文化物品
    }

    /**
     * 生成方块纹理占位文件
     */
    private void generateBlockTexturePlaceholders(CachedOutput cache) throws IOException {
        // ===== 华夏文化方块 =====
        
        // 华夏建筑材料
        generateTexturePlaceholder(cache, "block/huaxia_green_brick", "青砖", new Color(119, 136, 153));
        generateTexturePlaceholder(cache, "block/huaxia_red_wall", "红墙", new Color(220, 20, 60));
        generateTexturePlaceholder(cache, "block/huaxia_glazed_tile", "琉璃瓦", new Color(255, 215, 0));
        
        // 华夏装饰方块
        generateTexturePlaceholder(cache, "block/huaxia_lantern", "灯笼", new Color(255, 69, 0));
        generateTexturePlaceholder(cache, "block/huaxia_screen", "屏风", new Color(139, 69, 19));
        generateTexturePlaceholder(cache, "block/huaxia_stone_lion", "石狮", new Color(169, 169, 169));
        
        // 华夏功能方块
        generateTexturePlaceholder(cache, "block/huaxia_chest", "华夏箱子", new Color(139, 69, 19));
        generateTexturePlaceholder(cache, "block/huaxia_tea_table", "茶桌", new Color(160, 82, 45));
        generateTexturePlaceholder(cache, "block/huaxia_alchemy_cauldron", "药鼎", new Color(105, 105, 105));
        generateTexturePlaceholder(cache, "block/huaxia_loom", "织机", new Color(139, 69, 19));

        // TODO: 其他文化的方块纹理占位将在对应文化实现时添加
        // Norman（诺曼）文化方块
        // Japanese（日本）文化方块
        // Byzantine（拜占庭）文化方块
    }

    /**
     * 生成实体纹理占位文件
     */
    private void generateEntityTexturePlaceholders(CachedOutput cache) throws IOException {
        // ===== 村民实体纹理 =====
        
        // 为每个文化、性别、职业组合生成纹理占位符
        String[] cultures = {"norman", "byzantine", "japanese", "huaxia"};
        String[] genders = {"male", "female"};
        String[] professions = {"farmer", "blacksmith", "merchant", "guard", "scholar", "tea_merchant", "potter"};
        
        Color[] cultureColors = {
            new Color(139, 69, 19),    // Norman - 棕色
            new Color(128, 0, 128),    // Byzantine - 紫色
            new Color(255, 99, 71),    // Japanese - 红橙色
            new Color(255, 215, 0)     // Huaxia - 金色
        };
        
        for (int i = 0; i < cultures.length; i++) {
            String culture = cultures[i];
            Color baseColor = cultureColors[i];
            
            for (String gender : genders) {
                for (String profession : professions) {
                    // 根据性别调整颜色
                    Color genderColor = gender.equals("male") ? baseColor : adjustColorForFemale(baseColor);
                    
                    // 根据职业调整颜色
                    Color finalColor = adjustColorForProfession(genderColor, profession);
                    
                    String texturePath = String.format("entity/villager/%s_%s_%s", culture, gender, profession);
                    String displayText = String.format("%s %s %s", 
                        culture.substring(0, 1).toUpperCase() + culture.substring(1),
                        gender.equals("male") ? "男" : "女",
                        getProfessionDisplayName(profession));
                    
                    // 生成64x64的实体纹理占位符
                    generateTexturePlaceholder(cache, texturePath, displayText, finalColor, 64, 64);
                }
            }
        }
        
        MillenaireLogger.info(LogCategory.DATAGEN, "实体纹理占位文件生成完成");
    }

    /**
     * 为女性角色调整颜色（通常使用更柔和的色调）
     */
    private Color adjustColorForFemale(Color baseColor) {
        int r = Math.min(255, baseColor.getRed() + 20);
        int g = Math.min(255, baseColor.getGreen() + 20);
        int b = Math.min(255, baseColor.getBlue() + 20);
        return new Color(r, g, b);
    }

    /**
     * 根据职业调整颜色
     */
    private Color adjustColorForProfession(Color baseColor, String profession) {
        switch (profession) {
            case "farmer":
                return mixColors(baseColor, new Color(34, 139, 34)); // 绿色
            case "blacksmith":
                return mixColors(baseColor, new Color(128, 128, 128)); // 灰色
            case "merchant":
                return mixColors(baseColor, new Color(255, 215, 0)); // 金色
            case "guard":
                return mixColors(baseColor, new Color(139, 0, 0)); // 深红色
            case "scholar":
                return mixColors(baseColor, new Color(70, 130, 180)); // 钢蓝色
            case "tea_merchant":
                return mixColors(baseColor, new Color(144, 238, 144)); // 淡绿色
            case "potter":
                return mixColors(baseColor, new Color(160, 82, 45)); // 棕色
            default:
                return baseColor;
        }
    }

    /**
     * 混合两种颜色
     */
    private Color mixColors(Color color1, Color color2) {
        int r = (color1.getRed() + color2.getRed()) / 2;
        int g = (color1.getGreen() + color2.getGreen()) / 2;
        int b = (color1.getBlue() + color2.getBlue()) / 2;
        return new Color(r, g, b);
    }

    /**
     * 获取职业显示名称
     */
    private String getProfessionDisplayName(String profession) {
        switch (profession) {
            case "farmer": return "农";
            case "blacksmith": return "铁";
            case "merchant": return "商";
            case "guard": return "卫";
            case "scholar": return "学";
            case "tea_merchant": return "茶";
            case "potter": return "陶";
            default: return profession.substring(0, 1).toUpperCase();
        }
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
        
        // 华夏文化GUI纹理
        generateHuaxiaGUITextures(cache);
        
        // TODO: 其他文化GUI纹理
    }
    
    /**
     * 生成华夏文化特色的GUI纹理占位符
     */
    private void generateHuaxiaGUITextures(CachedOutput cache) throws IOException {
        // 华夏箱子GUI - 标准27槽位容器界面 (176x166像素)
        generateContainerGUI(cache, "gui/huaxia_chest", "华夏箱子", 
                176, 166, new Color(139, 69, 19), 3, 9); // 3行9列
        
        // 华夏茶桌GUI - 9槽位容器界面 (176x133像素)  
        generateTeaTableGUI(cache, "gui/huaxia_tea_table", "华夏茶桌",
                176, 133, new Color(101, 67, 33)); // 茶桌专用布局
    }
    
    /**
     * 生成标准容器GUI纹理占位符
     */
    private void generateContainerGUI(CachedOutput cache, String texturePath, String title,
            int width, int height, Color bgColor, int rows, int cols) throws IOException {
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制主背景
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);
        
        // 绘制容器区域背景 (稍深一些)
        g2d.setColor(bgColor.darker());
        g2d.fillRect(7, 17, 162, 54); // 容器背景区域
        
        // 绘制槽位格子
        g2d.setColor(Color.DARK_GRAY);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                g2d.drawRect(x, y, 16, 16); // 18x18间距，16x16槽位
                
                // 在槽位中标记不同类型
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(x + 1, y + 1, 15, 15);
                g2d.setColor(Color.DARK_GRAY);
            }
        }
        
        // 绘制玩家背包区域
        g2d.setColor(bgColor.darker());
        g2d.fillRect(7, height - 82, 162, 76); // 玩家背包背景
        
        // 绘制玩家背包槽位 (4行9列)
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + col * 18;
                int y = height - 82 + 1 + row * 18;
                g2d.drawRect(x, y, 16, 16);
                
                // 标记背包槽位
                g2d.setColor(new Color(200, 200, 255, 100));
                g2d.fillRect(x + 1, y + 1, 15, 15);
                g2d.setColor(Color.DARK_GRAY);
            }
        }
        
        // 绘制标题
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString(title, 8, 8);
        
        g2d.dispose();
        
        // 保存图像
        saveTexture(cache, texturePath, image);
    }
    
    /**
     * 生成茶桌专用GUI纹理占位符
     */
    private void generateTeaTableGUI(CachedOutput cache, String texturePath, String title,
            int width, int height, Color bgColor) throws IOException {
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制主背景
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);
        
        // 绘制茶桌容器区域 - 3x3布局
        g2d.setColor(bgColor.darker());
        g2d.fillRect(61, 17, 54, 54); // 3x3区域背景
        
        // 绘制茶桌槽位 - 分为三个区域
        String[] areaLabels = {"茶叶", "茶具", "调料"};
        Color[] areaColors = {
            new Color(34, 139, 34, 150),   // 绿色 - 茶叶区域
            new Color(139, 69, 19, 150),   // 棕色 - 茶具区域  
            new Color(255, 215, 0, 150)    // 金色 - 调料区域
        };
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int slotIndex = i * 3 + j;
                int x = 62 + j * 18;
                int y = 18 + i * 18;
                
                // 绘制槽位
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(x, y, 16, 16);
                
                // 根据槽位类型着色
                Color slotColor;
                if (slotIndex < 3) slotColor = areaColors[0]; // 茶叶
                else if (slotIndex < 6) slotColor = areaColors[1]; // 茶具
                else slotColor = areaColors[2]; // 调料
                
                g2d.setColor(slotColor);
                g2d.fillRect(x + 1, y + 1, 15, 15);
            }
        }
        
        // 绘制区域标签
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 6));
        g2d.drawString("茶叶", 125, 30);
        g2d.drawString("茶具", 125, 48); 
        g2d.drawString("调料", 125, 66);
        
        // 绘制玩家背包区域
        g2d.setColor(bgColor.darker());
        g2d.fillRect(7, height - 76, 162, 76);
        
        // 绘制玩家背包槽位
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + col * 18;
                int y = height - 76 + 1 + row * 18;
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(x, y, 16, 16);
                
                g2d.setColor(new Color(200, 200, 255, 100));
                g2d.fillRect(x + 1, y + 1, 15, 15);
            }
        }
        
        // 绘制标题
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString(title, 8, 8);
        
        g2d.dispose();
        
        // 保存图像
        saveTexture(cache, texturePath, image);
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
        saveTexture(cache, texturePath, image);
    }

    /**
     * 保存纹理到文件
     */
    private void saveTexture(CachedOutput cache, String texturePath, BufferedImage image) throws IOException {
        // 确定输出路径
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
