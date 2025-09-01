package com.jasoncian.millenaire_rewrite.client.gui;

import com.jasoncian.millenaire_rewrite.items.ItemMillParchment;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.components.Button;

/**
 * 羊皮纸GUI屏幕 - 现代化实现
 * 基于旧版Millenaire的ML_parchment.png材质
 */
public class ParchmentScreen extends Screen {
    
    /** GUI材质文件 */
    private static final ResourceLocation PARCHMENT_GUI = ResourceLocation.fromNamespaceAndPath("millenaire_rewrite", "textures/gui/parchment_gui.png");
    
    /** GUI尺寸 */
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;
    
    /** 文本显示区域 */
    private static final int TEXT_START_X = 20;
    private static final int TEXT_START_Y = 30;
    private static final int TEXT_WIDTH = 136;
    private static final int LINE_HEIGHT = 12;
    private static final int MAX_LINES = 10;
    
    /** 羊皮纸物品 */
    private final ItemStack parchmentStack;
    
    /** GUI位置 */
    private int leftPos;
    private int topPos;
    
    /** 当前显示页面 */
    private int currentPage = 0;
    private String[] allContents;
    private String[][] pages;
    
    /** 按钮 */
    private Button prevButton;
    private Button nextButton;
    private Button closeButton;
    
    public ParchmentScreen(ItemStack parchmentStack) {
        super(Component.literal("羊皮纸"));
        this.parchmentStack = parchmentStack;
        this.allContents = ItemMillParchment.getContents(parchmentStack);
        this.splitIntoPages();
    }
    
    /**
     * 将内容分页
     */
    private void splitIntoPages() {
        if (allContents.length == 0) {
            pages = new String[1][0];
            return;
        }
        
        // 计算需要多少页
        int totalPages = (allContents.length + MAX_LINES - 1) / MAX_LINES;
        pages = new String[totalPages][];
        
        for (int page = 0; page < totalPages; page++) {
            int startIndex = page * MAX_LINES;
            int endIndex = Math.min(startIndex + MAX_LINES, allContents.length);
            int pageSize = endIndex - startIndex;
            
            pages[page] = new String[pageSize];
            System.arraycopy(allContents, startIndex, pages[page], 0, pageSize);
        }
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 计算GUI位置（居中）
        this.leftPos = (this.width - GUI_WIDTH) / 2;
        this.topPos = (this.height - GUI_HEIGHT) / 2;
        
        // 创建按钮
        this.prevButton = Button.builder(Component.literal("◀ 上一页"), button -> {
            if (currentPage > 0) {
                currentPage--;
                updateButtons();
            }
        }).bounds(leftPos + 10, topPos + GUI_HEIGHT - 25, 50, 20).build();
        
        this.nextButton = Button.builder(Component.literal("下一页 ▶"), button -> {
            if (currentPage < pages.length - 1) {
                currentPage++;
                updateButtons();
            }
        }).bounds(leftPos + GUI_WIDTH - 60, topPos + GUI_HEIGHT - 25, 50, 20).build();
        
        this.closeButton = Button.builder(Component.literal("关闭"), button -> {
            this.onClose();
        }).bounds(leftPos + (GUI_WIDTH - 40) / 2, topPos + GUI_HEIGHT - 25, 40, 20).build();
        
        this.addRenderableWidget(prevButton);
        this.addRenderableWidget(nextButton);
        this.addRenderableWidget(closeButton);
        
        updateButtons();
    }
    
    /**
     * 更新按钮状态
     */
    private void updateButtons() {
        prevButton.active = currentPage > 0;
        nextButton.active = currentPage < pages.length - 1;
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        this.renderBackground(guiGraphics);
        
        // 渲染GUI背景
        RenderSystem.setShaderTexture(0, PARCHMENT_GUI);
        guiGraphics.blit(PARCHMENT_GUI, leftPos, topPos, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        
        // 渲染标题
        String title = ItemMillParchment.getTitle(parchmentStack);
        ItemMillParchment.Culture culture = ItemMillParchment.getCulture(parchmentStack);
        ItemMillParchment.ParchmentType type = ItemMillParchment.getParchmentType(parchmentStack);
        
        if (!title.isEmpty()) {
            int titleColor = type.getColor().getColor() != null ? type.getColor().getColor() : 0x000000;
            int titleX = leftPos + (GUI_WIDTH - font.width(title)) / 2;
            guiGraphics.drawString(font, title, titleX, topPos + 8, titleColor, false);
        }
        
        // 渲染文化和类型信息
        String cultureTypeText = culture.getDisplayName() + " " + type.getDisplayName();
        int cultureColor = culture.getColor().getColor() != null ? culture.getColor().getColor() : 0x666666;
        int cultureX = leftPos + (GUI_WIDTH - font.width(cultureTypeText)) / 2;
        guiGraphics.drawString(font, cultureTypeText, cultureX, topPos + 18, cultureColor, false);
        
        // 渲染当前页内容
        if (pages.length > 0 && currentPage < pages.length) {
            String[] currentPageContent = pages[currentPage];
            for (int i = 0; i < currentPageContent.length; i++) {
                String line = currentPageContent[i];
                if (!line.isEmpty()) {
                    // 根据内容类型设置颜色
                    int textColor = getContentColor(line);
                    int lineY = topPos + TEXT_START_Y + i * LINE_HEIGHT;
                    
                    // 如果文本太长，进行换行处理
                    if (font.width(line) > TEXT_WIDTH) {
                        String[] wrappedLines = wrapText(line, TEXT_WIDTH);
                        for (int j = 0; j < wrappedLines.length && (i + j) < MAX_LINES; j++) {
                            guiGraphics.drawString(font, "• " + wrappedLines[j], 
                                leftPos + TEXT_START_X, lineY + j * LINE_HEIGHT, textColor, false);
                        }
                        i += wrappedLines.length - 1; // 跳过已渲染的行
                    } else {
                        guiGraphics.drawString(font, "• " + line, 
                            leftPos + TEXT_START_X, lineY, textColor, false);
                    }
                }
            }
        }
        
        // 渲染页码信息
        if (pages.length > 1) {
            String pageInfo = "第 " + (currentPage + 1) + " 页，共 " + pages.length + " 页";
            int pageInfoX = leftPos + (GUI_WIDTH - font.width(pageInfo)) / 2;
            guiGraphics.drawString(font, pageInfo, pageInfoX, topPos + GUI_HEIGHT - 40, 0x666666, false);
        }
        
        // 渲染按钮和其他组件
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    /**
     * 根据内容确定文本颜色
     */
    private int getContentColor(String content) {
        String lowerContent = content.toLowerCase();
        
        if (lowerContent.contains("首领") || lowerContent.contains("leader")) {
            return 0xFFD700; // 金色
        } else if (lowerContent.contains("战士") || lowerContent.contains("守卫") || lowerContent.contains("warrior") || lowerContent.contains("guard")) {
            return 0xFF5555; // 红色
        } else if (lowerContent.contains("工人") || lowerContent.contains("农民") || lowerContent.contains("worker") || lowerContent.contains("farmer")) {
            return 0x55FF55; // 绿色
        } else if (lowerContent.contains("女性") || lowerContent.contains("妇女") || lowerContent.contains("woman") || lowerContent.contains("women")) {
            return 0xFF55FF; // 紫色
        } else if (lowerContent.contains("儿童") || lowerContent.contains("孩子") || lowerContent.contains("child") || lowerContent.contains("children")) {
            return 0xFFFF55; // 黄色
        } else if (lowerContent.contains("建筑") || lowerContent.contains("房屋") || lowerContent.contains("building") || lowerContent.contains("house")) {
            return 0x5555FF; // 蓝色
        } else if (lowerContent.contains("食物") || lowerContent.contains("武器") || lowerContent.contains("工具") || 
                   lowerContent.contains("food") || lowerContent.contains("weapon") || lowerContent.contains("tool")) {
            return 0x55FFFF; // 青色
        } else {
            return 0x000000; // 黑色
        }
    }
    
    /**
     * 文本换行处理
     */
    private String[] wrapText(String text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return new String[]{text};
        }
        
        // 简单的换行逻辑
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();
        
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (font.width(testLine) <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // 单个词太长，强制换行
                    lines.add(word);
                }
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC键关闭GUI
        if (keyCode == 256) { // GLFW.GLFW_KEY_ESCAPE
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
