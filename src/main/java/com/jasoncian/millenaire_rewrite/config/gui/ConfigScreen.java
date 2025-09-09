package com.jasoncian.millenaire_rewrite.config.gui;

import com.jasoncian.millenaire_rewrite.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 配置界面基础类
 * 
 * 为将来的模组配置GUI界面提供基础框架。
 * 当前为预留接口，未来可以扩展为完整的配置界面。
 * 
 * 功能预览：
 * - 实时配置编辑
 * - 配置预览和验证
 * - 配置导入/导出
 * - 配置重置功能
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends Screen {

    /** 父屏幕引用 */
    private final Screen parentScreen;

    /**
     * 构造函数
     * 
     * @param parentScreen 父屏幕，用于返回
     */
    public ConfigScreen(Screen parentScreen) {
        super(Component.translatable("screen.millenaire_rewrite.config.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        // TODO: 添加配置控件
        // 示例：添加返回按钮
        addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                button -> minecraft.setScreen(parentScreen))
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());

        // TODO: 添加配置分类选项卡
        // TODO: 添加各种配置输入控件
        // TODO: 添加配置预览区域
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        renderBackground(guiGraphics);

        // 渲染标题
        guiGraphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);

        // TODO: 渲染配置内容
        renderConfigContent(guiGraphics, mouseX, mouseY, partialTick);

        // 渲染父类内容（按钮等）
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    /**
     * 渲染配置内容
     * 
     * @param guiGraphics 图形上下文
     * @param mouseX      鼠标X坐标
     * @param mouseY      鼠标Y坐标
     * @param partialTick 部分tick
     */
    private void renderConfigContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int startY = 50;
        int lineHeight = 20;
        int currentY = startY;

        // TODO: 实现配置项渲染
        // 示例：显示当前一些关键配置值
        renderConfigValue(guiGraphics, "调试模式", String.valueOf(ModConfig.Helper.isDebugMode()), currentY);
        currentY += lineHeight;

        renderConfigValue(guiGraphics, "经济系统", String.valueOf(ModConfig.Helper.isEconomyEnabled()), currentY);
        currentY += lineHeight;

        renderConfigValue(guiGraphics, "村民渲染距离", String.valueOf(ModConfig.Helper.getVillagerRenderDistance()), currentY);
        currentY += lineHeight;

        renderConfigValue(guiGraphics, "村庄生成概率", String.format("%.2f", ModConfig.Helper.getVillageGenerationChance()), currentY);
    }

    /**
     * 渲染单个配置值
     * 
     * @param guiGraphics 图形上下文
     * @param name        配置名称
     * @param value       配置值
     * @param y           Y坐标
     */
    private void renderConfigValue(GuiGraphics guiGraphics, String name, String value, int y) {
        int centerX = width / 2;
        guiGraphics.drawString(font, name + ":", centerX - 100, y, 0xCCCCCC);
        guiGraphics.drawString(font, value, centerX + 10, y, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        // 配置界面不暂停游戏
        return false;
    }

    /**
     * 创建配置屏幕的工厂方法
     * 
     * @param parentScreen 父屏幕
     * @return 配置屏幕实例
     */
    public static ConfigScreen create(Screen parentScreen) {
        return new ConfigScreen(parentScreen);
    }

    // TODO: 添加配置保存方法
    // TODO: 添加配置重置方法
    // TODO: 添加配置验证方法
    // TODO: 添加配置导入/导出方法
}
