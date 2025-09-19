package com.jasoncian.millenaire_rewrite.data;

import com.jasoncian.millenaire_rewrite.items.ItemMillParchment;
import net.minecraft.world.item.ItemStack;

/**
 * 羊皮纸内容数据生成器
 * 预设各个文化的羊皮纸内容，基于旧版本Millenaire的本地化文件
 */
public class ParchmentContentData {
    
    /**
     * 创建诺曼村民羊皮纸
     */
    public static ItemStack createNormanVillagerParchment() {
        String title = "诺曼村民指南";
        String[] contents = {
            "村庄首领 - 管理村庄事务的权威人物",
            "骑士 - 保卫村庄的武装力量",
            "农民 - 负责种植作物和畜牧",
            "工匠 - 制作工具和装备",
            "妇女 - 负责纺织和家务",
            "儿童 - 村庄的未来希望",
            "商人 - 进行贸易和交换",
            "建筑师 - 设计和建造建筑物"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.NORMAN, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建诺曼建筑羊皮纸
     */
    public static ItemStack createNormanBuildingParchment() {
        String title = "诺曼建筑指南";
        String[] contents = {
            "市政厅 - 村庄的行政中心",
            "铁匠铺 - 制作金属工具和武器",
            "农场 - 种植小麦和饲养牲畜",
            "哨塔 - 防御工事和瞭望点",
            "民居 - 村民的住所",
            "教堂 - 宗教活动场所",
            "市场 - 商品交易中心",
            "马厩 - 饲养战马的场所"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.NORMAN, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建诺曼物品羊皮纸
     */
    public static ItemStack createNormanItemParchment() {
        String title = "诺曼物品指南";
        String[] contents = {
            "诺曼剑 - 锋利的单手剑",
            "诺曼斧 - 实用的伐木工具",
            "诺曼镐 - 坚固的采矿工具",
            "诺曼铲 - 高效的挖掘工具",
            "诺曼锄 - 农业专用工具",
            "苹果酒 - 诺曼特色饮品",
            "血肠 - 传统食物",
            "诺曼盔甲 - 防护装备"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.NORMAN, ItemMillParchment.ParchmentType.ITEM);
    }

    /**
     * 创建诺曼全书羊皮纸 - 诺曼完整指南
     */
    public static ItemStack createNormanAllParchment() {
        String title = "诺曼完整指南";
        String[] contents = {
                "=== 诺曼文化综合指南 ===",
                "包含诺曼村民、建筑、物品的完整信息",
                "村民: 首领、骑士、农民、工匠、妇女、儿童、商人、建筑师",
                "建筑: 市政厅、铁匠铺、农场、哨塔、民居、教堂、市场、马厩",
                "物品: 诺曼剑、诺曼斧、诺曼镐、诺曼铲、诺曼锄、苹果酒、血肠、诺曼盔甲",
                "=== 文化特色 ===",
                "诺曼人以其军事组织和农业技术闻名",
                "他们建立坚固的村庄和有效的管理体系"
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.NORMAN, ItemMillParchment.ParchmentType.ALL);
    }
    
    /**
     * 创建拜占庭村民羊皮纸
     */
    public static ItemStack createByzantineVillagerParchment() {
        String title = "拜占庭村民指南";
        String[] contents = {
            "执政官 - 拜占庭村庄的统治者",
            "百夫长 - 军事指挥官",
            "工匠 - 熟练的手工业者",
            "学者 - 知识的守护者",
            "贵妇 - 社会地位崇高的女性",
            "奴隶 - 从事体力劳动",
            "商贾 - 远程贸易商人",
            "神父 - 东正教神职人员"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.BYZANTINE, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建拜占庭建筑羊皮纸
     */
    public static ItemStack createByzantineBuildingParchment() {
        String title = "拜占庭建筑指南";
        String[] contents = {
            "总督府 - 行政管理中心",
            "要塞 - 军事防御建筑",
            "工坊 - 手工业生产场所",
            "图书馆 - 知识保存中心",
            "浴场 - 公共洗浴设施",
            "斗兽场 - 娱乐竞技场所",
            "大教堂 - 宗教礼拜中心",
            "港口 - 海上贸易据点"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.BYZANTINE, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建拜占庭物品羊皮纸
     */
    public static ItemStack createByzantineItemParchment() {
        String title = "拜占庭物品指南";
        String[] contents = {
            "拜占庭权杖 - 权力的象征",
            "希腊火 - 秘密军事武器",
            "葡萄酒 - 优质发酵饮品",
            "马尔瓦西亚酒 - 高档甜酒",
            "羊乳酪 - 传统奶制品",
            "烤肉串 - 特色美食",
            "拜占庭盔甲 - 精制防具",
            "紫色丝绸 - 贵族专用布料"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.BYZANTINE, ItemMillParchment.ParchmentType.ITEM);
    }

    /**
     * 创建拜占庭全书羊皮纸 - 拜占庭完整指南
     */
    public static ItemStack createByzantineAllParchment() {
        String title = "拜占庭完整指南";
        String[] contents = {
                "=== 拜占庭文化综合指南 ===",
                "包含拜占庭村民、建筑、物品的完整信息",
                "村民: 执政官、百夫长、工匠、学者、贵妇、奴隶、商贾、神父",
                "建筑: 总督府、要塞、工坊、图书馆、浴场、斗兽场、大教堂、港口",
                "物品: 拜占庭权杖、希腊火、葡萄酒、马尔瓦西亚酒、羊乳酪、烤肉串、拜占庭盔甲、紫色丝绸",
                "=== 文化特色 ===",
                "拜占庭帝国以其豪华和军事技术闻名",
                "他们继承罗马传统并发展出独特的东正教文化"
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.BYZANTINE, ItemMillParchment.ParchmentType.ALL);
    }
    
    /**
     * 创建印度村民羊皮纸
     */
    public static ItemStack createHindiVillagerParchment() {
        String title = "印度村民指南";
        String[] contents = {
            "拉贾 - 印度王子或统治者",
            "婆罗门 - 祭司阶层",
            "刹帝利 - 武士阶层",
            "吠舍 - 商人和农民",
            "首陀罗 - 服务者阶层",
            "瑜伽师 - 精神导师",
            "舞者 - 传统艺术表演者",
            "香料商 - 贸易专家"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.HINDI, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建印度建筑羊皮纸
     */
    public static ItemStack createHindiBuildingParchment() {
        String title = "印度建筑指南";
        String[] contents = {
            "宫殿 - 拉贾的华丽居所",
            "神庙 - 宗教朝拜场所",
            "市场 - 香料贸易中心",
            "瑜伽馆 - 修行冥想场所",
            "织布房 - 丝绸生产工坊",
            "香料园 - 种植调料植物",
            "水井 - 社区供水设施",
            "舞蹈厅 - 艺术表演场所"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.HINDI, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建印度物品羊皮纸
     */
    public static ItemStack createHindiItemParchment() {
        String title = "印度物品指南";
        String[] contents = {
            "姜黄 - 珍贵的调料香料",
            "大米 - 主要粮食作物",
            "蔬菜咖喱 - 素食美味",
            "鸡肉咖喱 - 荤食佳肴",
            "奶球甜点 - 传统甜食",
            "丝绸布料 - 华丽纺织品",
            "印度宝剑 - 装饰性武器",
            "香料粉末 - 调味材料"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.HINDI, ItemMillParchment.ParchmentType.ITEM);
    }

    /**
     * 创建印度全书羊皮纸 - 印度完整指南
     */
    public static ItemStack createHindiAllParchment() {
        String title = "印度完整指南";
        String[] contents = {
                "=== 印度文化综合指南 ===",
                "包含印度村民、建筑、物品的完整信息",
                // ... 添加具体内容
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.HINDI, ItemMillParchment.ParchmentType.ALL);
    }
    
    /**
     * 创建玛雅村民羊皮纸
     */
    public static ItemStack createMayanVillagerParchment() {
        String title = "玛雅村民指南";
        String[] contents = {
            "祭司王 - 玛雅文明的统治者",
            "战士 - 勇猛的丛林战士",
            "天文学家 - 观测星象的智者",
            "农民 - 种植玉米的专家",
            "工匠 - 黑曜石雕刻师",
            "舞者 - 宗教仪式表演者",
            "商人 - 远距离贸易者",
            "萨满 - 沟通神灵的巫师"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.MAYAN, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建玛雅建筑羊皮纸
     */
    public static ItemStack createMayanBuildingParchment() {
        String title = "玛雅建筑指南";
        String[] contents = {
            "金字塔 - 宗教仪式中心",
            "天文台 - 观测天体运动",
            "球场 - 传统体育竞技",
            "祭坛 - 献祭仪式场所",
            "蒸汽浴室 - 净化身心场所",
            "工坊 - 黑曜石加工厂",
            "农田 - 玉米种植区域",
            "天坑 - 神圣的水源地"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.MAYAN, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建玛雅物品羊皮纸
     */
    public static ItemStack createMayanItemParchment() {
        String title = "玛雅物品指南";
        String[] contents = {
            "玉米 - 神圣的粮食作物",
            "可可 - 制作神圣饮品",
            "玛萨 - 玉米制作的食物",
            "瓦赫 - 特殊仪式食品",
            "黑曜石工具 - 锋利的石器",
            "玛雅权杖 - 权力象征",
            "羽毛头饰 - 地位标志",
            "翡翠饰品 - 珍贵装饰"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.MAYAN, ItemMillParchment.ParchmentType.ITEM);
    }

    /**
     * 创建玛雅全书羊皮纸
     */
    public static ItemStack createMayanAllParchment() {
        String title = "玛雅完整指南";
        String[] contents = {
                "=== 玛雅文化综合指南 ===",
                "包含玛雅村民、建筑、物品的完整信息",
                // ... 添加具体内容
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.MAYAN, ItemMillParchment.ParchmentType.ALL);
    }
    
    /**
     * 创建日本村民羊皮纸
     */
    public static ItemStack createJapaneseVillagerParchment() {
        String title = "日本村民指南";
        String[] contents = {
            "大名 - 封建领主",
            "武士 - 职业战士阶层",
            "僧侣 - 佛教修行者",
            "农民 - 稻米种植者",
            "工匠 - 手工业专家",
            "艺伎 - 传统艺术表演者",
            "商人 - 贸易从业者",
            "忍者 - 秘密间谍"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.JAPANESE, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建日本建筑羊皮纸
     */
    public static ItemStack createJapaneseBuildingParchment() {
        String title = "日本建筑指南";
        String[] contents = {
            "天守阁 - 城堡主塔",
            "神社 - 神道教圣地",
            "道场 - 武术训练场",
            "茶室 - 茶道仪式场所",
            "稻田 - 水稻种植区",
            "温泉 - 天然热水浴场",
            "竹林 - 竹子种植区域",
            "禅花园 - 冥想修行场所"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.JAPANESE, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建日本物品羊皮纸
     */
    public static ItemStack createJapaneseItemParchment() {
        String title = "日本物品指南";
        String[] contents = {
            "武士刀 - 锋利的长剑",
            "和弓 - 传统弓箭",
            "清酒 - 米酿造酒",
            "乌冬面 - 传统面条",
            "鱿鱼烧 - 特色小食",
            "武士盔甲 - 防护装备",
            "和服 - 传统服装",
            "竹制品 - 实用工具"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.JAPANESE, ItemMillParchment.ParchmentType.ITEM);
    }
    /**
     * 创建日本全书羊皮纸
     */
    public static ItemStack createJapaneseAllParchment() {
        String title = "日本完整指南";
        String[] contents = {
                "=== 日本文化综合指南 ===",
                "包含玛雅村民、建筑、物品的完整信息",
                // ... 添加具体内容
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.JAPANESE, ItemMillParchment.ParchmentType.ALL);
    }
}
