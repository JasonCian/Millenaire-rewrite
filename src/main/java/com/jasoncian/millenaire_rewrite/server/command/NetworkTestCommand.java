package com.jasoncian.millenaire_rewrite.server.command;

import com.jasoncian.millenaire_rewrite.network.NetworkHandler;
import com.jasoncian.millenaire_rewrite.network.NetworkUtils;
import com.jasoncian.millenaire_rewrite.network.packet.SimpleSyncPacket;
import com.jasoncian.millenaire_rewrite.network.packet.VillageDataSyncPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * 网络测试命令
 * 
 * 提供测试网络通信层功能的命令集合。
 * 用于验证数据包的发送、接收和处理是否正常工作。
 * 
 * 命令格式：
 * - /millenaire network ping - 测试基础网络连接
 * - /millenaire network message <message> - 发送测试消息
 * - /millenaire network village <action> - 测试村庄数据同步
 * - /millenaire network stats - 显示网络统计信息
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class NetworkTestCommand {

    /**
     * 注册网络测试命令
     * 
     * @param dispatcher 命令分发器
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("millenaire")
                .then(Commands.literal("network")
                        .requires(source -> source.hasPermission(2)) // 需要OP权限
                        .then(Commands.literal("ping")
                                .executes(NetworkTestCommand::pingCommand))
                        .then(Commands.literal("message")
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(NetworkTestCommand::messageCommand)))
                        .then(Commands.literal("village")
                                .then(Commands.literal("test")
                                        .executes(NetworkTestCommand::villageTestCommand))
                                .then(Commands.literal("sync")
                                        .executes(NetworkTestCommand::villageSyncCommand)))
                        .then(Commands.literal("stats")
                                .executes(NetworkTestCommand::statsCommand))
                        .then(Commands.literal("broadcast")
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(NetworkTestCommand::broadcastCommand)))));
    }

    /**
     * 执行ping命令
     */
    private static int pingCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();

        // 向玩家发送ping消息
        NetworkUtils.sendSimpleMessage(player, "Ping测试消息", SimpleSyncPacket.MessageType.PING);

        source.sendSuccess(() -> Component.literal("已发送ping消息到客户端"), false);
        return 1;
    }

    /**
     * 执行消息命令
     */
    private static int messageCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String message = StringArgumentType.getString(context, "message");

        // 向玩家发送自定义消息
        NetworkUtils.sendInfoMessage(player, message);

        source.sendSuccess(() -> Component.literal("已发送消息: " + message), false);
        return 1;
    }

    /**
     * 执行村庄测试命令
     */
    private static int villageTestCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        BlockPos playerPos = player.blockPosition();

        // 创建测试村庄数据
        VillageDataSyncPacket testVillage = VillageDataSyncPacket.createBasicSync(
                1001,
                "测试村庄",
                playerPos,
                ResourceLocation.fromNamespaceAndPath("millenaire_rewrite", "norman"));

        // 发送测试村庄数据到客户端
        NetworkHandler.sendToPlayer(testVillage, player);

        source.sendSuccess(() -> Component.literal("已发送测试村庄数据到客户端"), false);
        return 1;
    }

    /**
     * 执行村庄同步命令
     */
    private static int villageSyncCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        BlockPos playerPos = player.blockPosition();

        // 创建完整的村庄数据同步包
        VillageDataSyncPacket syncPacket = new VillageDataSyncPacket(
                2001,
                "完整同步测试村庄",
                playerPos,
                ResourceLocation.fromNamespaceAndPath("millenaire_rewrite", "japanese"),
                25, // 当前人口
                50, // 最大人口
                3, // 村庄等级
                VillageDataSyncPacket.VillageStatus.GROWING,
                java.util.Map.of(
                        "gold", 1500,
                        "food", 300,
                        "wood", 200),
                VillageDataSyncPacket.SyncType.FULL_UPDATE);

        NetworkHandler.sendToPlayer(syncPacket, player);

        source.sendSuccess(() -> Component.literal("已发送完整村庄同步数据到客户端"), false);
        return 1;
    }

    /**
     * 执行统计命令
     */
    private static int statsCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String stats = NetworkUtils.getNetworkStats();
        source.sendSuccess(() -> Component.literal("网络统计信息: " + stats), false);

        return 1;
    }

    /**
     * 执行广播命令
     */
    private static int broadcastCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String message = StringArgumentType.getString(context, "message");

        // 向所有在线玩家广播消息
        NetworkUtils.broadcastInfo(message);

        source.sendSuccess(() -> Component.literal("已广播消息: " + message), false);
        return 1;
    }
}
