package com.jasoncian.millenaire_rewrite.items.magic;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 动态护符物品 - 现代化实现
 * 
 * 这个类实现了legacy版本的动态状态显示功能。
 * 护符使用多帧贴图系统，根据检测状态显示对应的帧。
 * 
 * 重要说明：这些贴图不是动画，而是静态帧！
 * - 炼金术师护符：检测到的矿物越多，显示的帧数越高（0-15帧）
 * - 毗湿奴护符：附近怪物越近，显示的帧数越高（0-15帧）
 * - 世界树护符：海拔高度对应不同帧（0-31帧）
 * - 斯科尔和哈提护符：昼夜控制功能
 * 
 * 右键点击护符会在聊天框输出当前检测数据。
 */
public class DynamicAmuletItem extends Item {
    
    // 颜色数组，来自legacy版本
    private static final int[] COLOR_ALCHEMIST = {9868950, 10132109, 10395268, 10658427, 11053168, 11316327, 11579486, 11842645, 12237387, 12500545, 12763705, 13026863, 13421605, 13684764, 13947923, 14211082};
    private static final int[] COLOR_VISHNU = {236, 983260, 2031820, 3080380, 4063405, 5111965, 6160525, 7209085, 8192110, 9240670, 10289230, 11337790, 12320815, 13369375, 14417935, 15466496};
    private static final int[] COLOR_YGGDRASIL = {396556, 990493, 1453614, 2113086, 2576206, 3104864, 3698799, 4227457, 4755857, 5350050, 5878706, 6407106, 7001299, 7464165, 8058100, 8388606, 
            8781823, 9306111, 9895935, 10420223, 10944511, 11534335, 12058623, 12648447, 13172735, 13762559, 14286847, 14876671, 15400959, 15925247, 16515071, 16777213};
    
    public enum AmuletType {
        ALCHEMIST("alchemist", 16),      // 16帧，检测矿物
        VISHNU("vishnu", 16),            // 16帧，检测怪物
        YGGDRASIL("yggdrasil", 16),      // 16帧，显示高度
        SKOLL_HATI("skoll_hati", 16);    // 16帧，昼夜控制
        
        private final String name;
        private final int maxStates;
        
        AmuletType(String name, int maxStates) {
            this.name = name;
            this.maxStates = maxStates;
        }
        
        public String getName() { return name; }
        public int getMaxStates() { return maxStates; }
    }
    
    private final AmuletType amuletType;
    private int lastUpdateTick = 0;
    
    public DynamicAmuletItem(AmuletType type, Properties properties) {
        super(type == AmuletType.SKOLL_HATI ? 
              properties.stacksTo(1).durability(64) : 
              properties.stacksTo(1));
        this.amuletType = type;
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }
        
        // 限制更新频率，每20tick(1秒)更新一次
        long currentTick = level.getGameTime();
        if (currentTick - lastUpdateTick < 20) {
            return;
        }
        lastUpdateTick = (int)(currentTick % Integer.MAX_VALUE);
        
        int detectionValue = calculateDetectionValue(level, player);
        
        // 将检测值保存到NBT中
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt("detection_value", detectionValue);
        nbt.putLong("last_update", level.getGameTime());
    }
    
    private int calculateDetectionValue(Level level, Player player) {
        switch (amuletType) {
            case ALCHEMIST:
                return calculateOreDetection(level, player);
            case VISHNU:
                return calculateMonsterDetection(level, player);
            case YGGDRASIL:
                return calculateAltitudeLevel(player);
            case SKOLL_HATI:
            default:
                return 0;
        }
    }
    
    private int calculateOreDetection(Level level, Player player) {
        int detectionScore = 0;
        int radius = 5;
        BlockPos playerPos = player.blockPosition();
        
        int startY = Math.max(playerPos.getY() - radius, level.getMinBuildHeight());
        int endY = Math.min(playerPos.getY() + radius, level.getMaxBuildHeight());
        
        for (int x = playerPos.getX() - radius; x <= playerPos.getX() + radius; x++) {
            for (int z = playerPos.getZ() - radius; z <= playerPos.getZ() + radius; z++) {
                for (int y = startY; y <= endY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    
                    // 根据矿物类型计算分数，与legacy版本保持一致
                    if (state.is(Blocks.COAL_ORE) || state.is(Blocks.DEEPSLATE_COAL_ORE)) {
                        detectionScore += 1;
                    } else if (state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE)) {
                        detectionScore += 5;
                    } else if (state.is(Blocks.GOLD_ORE) || state.is(Blocks.DEEPSLATE_GOLD_ORE)) {
                        detectionScore += 10;
                    } else if (state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)) {
                        detectionScore += 30;
                    } else if (state.is(Blocks.EMERALD_ORE) || state.is(Blocks.DEEPSLATE_EMERALD_ORE)) {
                        detectionScore += 30;
                    } else if (state.is(Blocks.LAPIS_ORE) || state.is(Blocks.DEEPSLATE_LAPIS_ORE)) {
                        detectionScore += 10;
                    } else if (state.is(Blocks.REDSTONE_ORE) || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)) {
                        detectionScore += 5;
                    } else if (state.is(Blocks.COPPER_ORE) || state.is(Blocks.DEEPSLATE_COPPER_ORE)) {
                        detectionScore += 3;
                    }
                }
            }
        }
        
        // 限制最大值并转换为状态等级
        detectionScore = Math.min(detectionScore, 100);
        return (detectionScore * (amuletType.getMaxStates() - 1)) / 100;
    }
    
    private int calculateMonsterDetection(Level level, Player player) {
        double radius = 20.0;
        double closestDistance = Double.MAX_VALUE;
        
        AABB searchArea = new AABB(
            player.getX() - radius, player.getY() - radius, player.getZ() - radius,
            player.getX() + radius, player.getY() + radius, player.getZ() + radius
        );
        
        List<Monster> monsters = level.getEntitiesOfClass(Monster.class, searchArea);
        
        for (Monster monster : monsters) {
            double distance = player.distanceTo(monster);
            if (distance < closestDistance) {
                closestDistance = distance;
            }
        }
        
        if (closestDistance > radius) {
            return 0;
        } else {
            double detectionLevel = (radius - closestDistance) / radius;
            return (int) (detectionLevel * (amuletType.getMaxStates() - 1));
        }
    }
    
    private int calculateAltitudeLevel(Player player) {
        int altitude = (int) Math.floor(player.getY());
        altitude = Mth.clamp(altitude, 0, 255);
        return altitude / 8; // 32状态，每8格高度一个等级
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null) {
            int detectionValue = nbt.getInt("detection_value");
            
            switch (amuletType) {
                case ALCHEMIST:
                    tooltip.add(Component.translatable("item.millenaire_rewrite.amulet_alchemist.tooltip")
                        .withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.translatable("item.millenaire_rewrite.amulet.ore_detected", detectionValue)
                        .withStyle(ChatFormatting.GREEN));
                    break;
                case VISHNU:
                    tooltip.add(Component.translatable("item.millenaire_rewrite.amulet_vishnu.tooltip")
                        .withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.translatable("item.millenaire_rewrite.amulet.danger_level", detectionValue)
                        .withStyle(ChatFormatting.RED));
                    break;
                case YGGDRASIL:
                    tooltip.add(Component.translatable("item.millenaire_rewrite.amulet_yggdrasil.tooltip")
                        .withStyle(ChatFormatting.AQUA));
                    int altitude = detectionValue * 8;
                    tooltip.add(Component.translatable("item.millenaire_rewrite.amulet.altitude", altitude)
                        .withStyle(ChatFormatting.YELLOW));
                    break;
                case SKOLL_HATI:
                    tooltip.add(Component.translatable("item.millenaire_rewrite.amulet_skoll_hati.tooltip")
                        .withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.translatable("item.millenaire_rewrite.amulet.day_night_control")
                        .withStyle(ChatFormatting.GOLD));
                    break;
            }
        }
        
        super.appendHoverText(stack, level, tooltip, flag);
    }
    
    public AmuletType getAmuletType() {
        return amuletType;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            CompoundTag nbt = stack.getTag();
            int detectionValue = nbt != null ? nbt.getInt("detection_value") : 0;
            
            // 输出调试信息到聊天框
            switch (amuletType) {
                case ALCHEMIST:
                    player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.amulet_alchemist.detection", 
                        detectionValue, amuletType.getMaxStates() - 1)
                        .withStyle(ChatFormatting.GREEN));
                    break;
                case VISHNU:
                    player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.amulet_vishnu.detection", 
                        detectionValue, amuletType.getMaxStates() - 1)
                        .withStyle(ChatFormatting.RED));
                    break;
                case YGGDRASIL:
                    int altitude = (int) Math.floor(player.getY());
                    player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.amulet_yggdrasil.detection", 
                        detectionValue, amuletType.getMaxStates() - 1, altitude)
                        .withStyle(ChatFormatting.YELLOW));
                    break;
                case SKOLL_HATI:
                    // 实现昼夜切换功能
                    if (level instanceof ServerLevel serverLevel) {
                        long currentTime = serverLevel.getDayTime() + 24000L;
                        
                        if (currentTime % 24000L > 11000L && currentTime % 24000L < 23500L) {
                            // 当前是夜晚，切换到白天
                            serverLevel.setDayTime(currentTime - currentTime % 24000L - 500L);
                            player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.amulet_skoll_hati.day")
                                .withStyle(ChatFormatting.GOLD));
                        } else {
                            // 当前是白天，切换到夜晚
                            serverLevel.setDayTime(currentTime - currentTime % 24000L + 13000L);
                            player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.amulet_skoll_hati.night")
                                .withStyle(ChatFormatting.DARK_BLUE));
                        }
                        
                        // 对物品造成耐久度损伤（仅对SKOLL_HATI护符）
                        if (amuletType == AmuletType.SKOLL_HATI) {
                            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                        }
                        
                        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                    }
                    break;
            }
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    /**
     * 获取物品颜色（用于overlay层）
     * 在1.20.1中需要通过ItemColors注册
     */
    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) { // 只有layer1需要着色
            return 0xFFFFFF;
        }
        
        Item item = stack.getItem();
        if (!(item instanceof DynamicAmuletItem amulet)) {
            return 0xFFFFFF;
        }
        
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            return getDefaultColor(amulet.amuletType);
        }
        
        int score = nbt.getInt("detection_value");
        return getColorForScore(amulet.amuletType, score);
    }
    
    private static int getDefaultColor(AmuletType type) {
        switch (type) {
            case ALCHEMIST:
                return COLOR_ALCHEMIST[0];
            case VISHNU:
                return COLOR_VISHNU[0];
            case YGGDRASIL:
                return COLOR_YGGDRASIL[16];
            default:
                return 0xFFFFFF;
        }
    }
    
    private static int getColorForScore(AmuletType type, int score) {
        switch (type) {
            case ALCHEMIST:
                return COLOR_ALCHEMIST[Math.min(score, COLOR_ALCHEMIST.length - 1)];
            case VISHNU:
                return COLOR_VISHNU[Math.min(score, COLOR_VISHNU.length - 1)];
            case YGGDRASIL:
                return COLOR_YGGDRASIL[Math.min(score, COLOR_YGGDRASIL.length - 1)];
            default:
                return 0xFFFFFF;
        }
    }
}
