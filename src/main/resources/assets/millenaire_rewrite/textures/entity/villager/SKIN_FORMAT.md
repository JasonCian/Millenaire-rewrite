# 千年村民皮肤格式说明

## 概述
千年村民现在使用64x64像素的玩家皮肤格式，而不是传统的村民模型。这提供了更多的自定义选项和更好的视觉效果。

## 皮肤文件命名规则
皮肤文件按照以下格式命名：
```
{culture}_{gender}_{profession}.png
```

### 示例文件名：
- `norman_male_farmer.png` - 诺曼文化，男性，农民
- `norman_female_farmer.png` - 诺曼文化，女性，农民
- `norman_male_blacksmith.png` - 诺曼文化，男性，铁匠
- `japanese_male_samurai.png` - 日本文化，男性，武士
- `byzantine_female_merchant.png` - 拜占庭文化，女性，商人

## 文化类型
- `norman` - 诺曼文化
- `japanese` - 日本文化
- `byzantine` - 拜占庭文化
- `mayan` - 玛雅文化
- `hindi` - 印度文化

## 性别类型
- `male` - 男性
- `female` - 女性

## 职业类型
- `farmer` - 农民
- `blacksmith` - 铁匠
- `merchant` - 商人
- `guard` - 守卫
- `priest` - 祭司
- `samurai` - 武士（日本文化专有）
- `carpenter` - 木匠
- `miner` - 矿工

## 皮肤格式
使用标准的Minecraft玩家皮肤格式：
- 尺寸：64x64像素
- 格式：PNG
- 透明度：支持
- 外层衣物：支持（帽子、夹克、裤子、鞋子等）

## 设计建议
1. 保持与各文化的历史准确性
2. 使用适合职业的服装和配色
3. 考虑性别差异在服装上的体现
4. 保持适度的细节，避免过于复杂
5. 确保在游戏中的可读性

## 回退机制
如果特定组合的皮肤不存在，系统将按以下顺序查找：
1. `{culture}_{gender}_default.png`
2. `{culture}_default.png`
3. `norman_male_farmer.png` (全局默认)
