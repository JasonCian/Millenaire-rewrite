package org.millenaire.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 基于旧版本Millenaire的护身符实现
 * 包含时间控制、矿物探测、生物感知和高度指示功能
 * 具有动态颜色变化的动画效果
 */
public class ItemMillAmulet extends Item {
    
    // 炼金术师护身符颜色数组（基于旧版本）
    private static final int[] COLOR_ALCHEMIST = new int[]{
        9868950, 10132109, 10395268, 10658427, 11053168, 11316327, 11579486, 11842645, 
        12237387, 12500545, 12763705, 13026863, 13421605, 13684764, 13947923, 14211082
    };
    
    // 毗湿奴护身符颜色数组（基于旧版本）
    private static final int[] COLOR_VISHNU = new int[]{
        236, 983260, 2031820, 3080380, 4063405, 5111965, 6160525, 7209085, 
        8192110, 9240670, 10289230, 11337790, 12320815, 13369375, 14417935, 15466496
    };
    
    // 世界树护身符颜色数组（基于旧版本）
    private static final int[] COLOR_YGGDRASIL = new int[]{
        396556, 990493, 1453614, 2113086, 2576206, 3104864, 3698799, 4227457, 
        4755857, 5350050, 5878706, 6407106, 7001299, 7464165, 8058100, 8388606, 
        8781823, 9306111, 9895935, 10420223, 10944511, 11534335, 12058623, 12648447, 
        13172735, 13762559, 14286847, 14876671, 15400959, 15925247, 16515071, 16777213
    };
    
    public ItemMillAmulet() {
        super(new Properties()
                .stacksTo(1)
                .durability(100));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (world.isClientSide || !(entity instanceof Player player)) {
            return;
        }
        
        // 每隔一段时间执行一次检测
        if (world.getGameTime() % 10 != 0) {
            return;
        }
        
        String itemName = stack.getItem().toString();
        int visScore = 0;
        
        // 炼金术师护身符 - 矿物探测和动画计算
        if (itemName.contains("alchemist")) {
            visScore = calculateAlchemistScore(player, world);
            if (visScore > 0) {
                player.sendSystemMessage(Component.literal("§" + getColorCode(visScore, COLOR_ALCHEMIST) + "矿物能量: " + visScore));
            }
        }
        
        // 毗湿奴护身符 - 生物接近探测和动画计算
        if (itemName.contains("vishnu")) {
            visScore = calculateVishnutScore(player, world);
            if (visScore > 0) {
                player.sendSystemMessage(Component.literal("§" + getColorCode(visScore, COLOR_VISHNU) + "生物威胁: " + visScore));
            }
        }
        
        // 世界树护身符 - 显示高度信息和动画计算
        if (itemName.contains("yggdrasil")) {
            visScore = calculateYggdrasilScore(player);
            showAltitudeWithAnimation(player, visScore);
        }
        
        // 将分数存储到NBT中用于动画
        if (!stack.hasTag()) {
            stack.setTag(new net.minecraft.nbt.CompoundTag());
        }
        stack.getTag().putInt("animationScore", visScore);
    }
    
    /**
     * 计算炼金术师护身符的动画分数
     */
    private int calculateAlchemistScore(Player player, Level world) {
        BlockPos playerPos = player.blockPosition();
        int range = 8;
        int visScore = 0;
        
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    
                    // 根据旧版本的分数计算
                    if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
                        visScore += 50;
                    } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
                        visScore += 30;
                    } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
                        visScore += 10;
                    } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
                        visScore += 5;
                    } else if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) {
                        visScore += 10;
                    } else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
                        visScore += 5;
                    }
                }
            }
        }
        
        if (visScore > 100) visScore = 100;
        return (visScore * 15) / 100;
    }
    
    /**
     * 计算毗湿奴护身符的动画分数
     */
    private int calculateVishnutScore(Player player, Level world) {
        int radius = 20;
        double closestDistance = Double.MAX_VALUE;
        
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, 
            player.getBoundingBox().inflate(radius), 
            entity -> entity != player && entity.isAlive() && entity instanceof net.minecraft.world.entity.monster.Monster);
        
        for (LivingEntity entity : entities) {
            double distance = player.distanceTo(entity);
            if (distance < closestDistance) {
                closestDistance = distance;
            }
        }
        
        if (closestDistance > radius) {
            return 0;
        } else {
            double level = (radius - closestDistance) / radius;
            return (int) (level * 15);
        }
    }
    
    /**
     * 计算世界树护身符的动画分数
     */
    private int calculateYggdrasilScore(Player player) {
        int level = (int) Math.floor(player.getY());
        
        if (level > 255) {
            level = 255;
        } else if (level < 0) {
            level = 0;
        }
        
        return level / 8;
    }
    
    /**
     * 带动画效果的高度显示
     */
    private void showAltitudeWithAnimation(Player player, int score) {
        int y = player.blockPosition().getY();
        String colorCode = getColorCode(score, COLOR_YGGDRASIL);
        player.sendSystemMessage(Component.literal("§" + colorCode + "高度: " + y));
    }
    
    /**
     * 根据分数获取颜色代码
     */
    private String getColorCode(int score, int[] colorArray) {
        if (score < 0) score = 0;
        if (score >= colorArray.length) score = colorArray.length - 1;
        
        // 简化的颜色映射到Minecraft颜色代码
        if (score < 3) return "4"; // 深红色
        else if (score < 6) return "c"; // 红色
        else if (score < 9) return "6"; // 金色
        else if (score < 12) return "e"; // 黄色
        else return "a"; // 绿色
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (world.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        
        String itemName = stack.getItem().toString();
        
        // 斯库尔和哈提护身符 - 时间控制
        if (itemName.contains("skoll_hati")) {
            controlTime(player, world);
            return InteractionResultHolder.success(stack);
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    
    /**
     * 时间控制功能 - 斯库尔和哈提护身符
     */
    private void controlTime(Player player, Level world) {
        if (world.isClientSide) {
            return;
        }
        
        long currentTime = world.getDayTime() % 24000;
        
        if (currentTime < 12000) {
            // 如果是白天，设置为夜晚
            world.getServer().overworld().setDayTime(world.getDayTime() + (18000 - currentTime));
            player.sendSystemMessage(Component.literal("§5时间已调整为夜晚"));
        } else {
            // 如果是夜晚，设置为白天
            world.getServer().overworld().setDayTime(world.getDayTime() + (30000 - currentTime));
            player.sendSystemMessage(Component.literal("§e时间已调整为白天"));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        String itemName = stack.getItem().toString();
        
        if (itemName.contains("skoll_hati")) {
            tooltip.add(Component.literal("§7右键切换白天/夜晚"));
            tooltip.add(Component.literal("§7斯库尔与哈提的力量"));
        } else if (itemName.contains("alchemist")) {
            tooltip.add(Component.literal("§7自动探测附近的矿物"));
            tooltip.add(Component.literal("§7炼金术师的智慧"));
            
            // 显示动画状态
            if (stack.hasTag() && stack.getTag().contains("animationScore")) {
                int score = stack.getTag().getInt("animationScore");
                tooltip.add(Component.literal("§7能量等级: " + score + "/15"));
            }
        } else if (itemName.contains("vishnu")) {
            tooltip.add(Component.literal("§7感知附近的生物"));
            tooltip.add(Component.literal("§7毗湿奴的洞察力"));
            
            // 显示动画状态
            if (stack.hasTag() && stack.getTag().contains("animationScore")) {
                int score = stack.getTag().getInt("animationScore");
                tooltip.add(Component.literal("§7威胁等级: " + score + "/15"));
            }
        } else if (itemName.contains("yggdrasil")) {
            tooltip.add(Component.literal("§7显示当前高度"));
            tooltip.add(Component.literal("§7世界树的指引"));
            
            // 显示动画状态
            if (stack.hasTag() && stack.getTag().contains("animationScore")) {
                int score = stack.getTag().getInt("animationScore");
                tooltip.add(Component.literal("§7高度等级: " + score + "/31"));
            }
        }
        
        super.appendHoverText(stack, world, tooltip, flag);
    }
}
