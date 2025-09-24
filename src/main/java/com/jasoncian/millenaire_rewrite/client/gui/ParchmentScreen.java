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
    private static final int MAX_LINES = 8;
    
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
        super(Component.translatable("gui.millenaire_rewrite.parchment.title"));
        this.parchmentStack = parchmentStack;
        this.allContents = ItemMillParchment.getContents(parchmentStack);
        this.splitIntoPages();
    }

    /**
     * 将内容分页（保留空行，在渲染时处理分页）
     */
    private void splitIntoPages() {
        if (allContents.length == 0) {
            pages = new String[1][0];
            return;
        }

        java.util.List<String[]> pageList = new java.util.ArrayList<>();
        java.util.List<String> currentPage = new java.util.ArrayList<>();

        for (String line : allContents) {


            // 不再跳过空行，保留空行用于渲染时分页
            currentPage.add(line);

            // 严格限制：每页最多8行原始内容（包括空行）
            if (currentPage.size() >= MAX_LINES) {

                pageList.add(currentPage.toArray(new String[0]));
                currentPage = new java.util.ArrayList<>();
            }
        }

        if (!currentPage.isEmpty()) {

            pageList.add(currentPage.toArray(new String[0]));
        }

        pages = pageList.toArray(new String[0][]);


        for (int i = 0; i < pages.length; i++) {

            for (int j = 0; j < pages[i].length; j++) {

            }
        }
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 计算GUI位置（居中）
        this.leftPos = (this.width - GUI_WIDTH) / 2;
        this.topPos = (this.height - GUI_HEIGHT) / 2;
        
        // 创建按钮
        this.prevButton = Button.builder(Component.translatable("gui.millenaire_rewrite.parchment.previous"), button -> {
            if (currentPage > 0) {
                currentPage--;
                updateButtons();
            }
        }).bounds(leftPos + 10, topPos + GUI_HEIGHT - 25, 50, 20).build();

        this.nextButton = Button.builder(Component.translatable("gui.millenaire_rewrite.parchment.next"), button -> {
            if (currentPage < pages.length - 1) {
                currentPage++;
                updateButtons();
            }
        }).bounds(leftPos + GUI_WIDTH - 60, topPos + GUI_HEIGHT - 25, 50, 20).build();

        this.closeButton = Button.builder(Component.translatable("gui.millenaire_rewrite.parchment.close"), button -> {
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
            int renderLineIndex = 0; // 单独的渲染行索引

            for (int i = 0; i < currentPageContent.length && renderLineIndex < MAX_LINES; i++) {
                String line = currentPageContent[i];

                // 遇到空字符串 "" 直接分页（但不包括空行本身）
                if (line.equals("") && renderLineIndex > 0) {
                    // 将剩余行移到下一页（不包括这个空行）
                    moveRemainingLinesToNextPage(currentPageContent, i + 1);
                    updateButtons(); // 更新按钮状态
                    break; // 跳出循环，当前页渲染结束
                }

                // 渲染所有行（包括空行）
                int lineY = topPos + TEXT_START_Y + renderLineIndex * LINE_HEIGHT;

                if (!line.isEmpty()) {
                    // 翻译处理（如果是翻译键）
                    String displayLine = line;
                    if (line.contains(".")) {
                        displayLine = Component.translatable(line).getString();
                    }

                    // 根据内容类型设置颜色
                    int textColor = getContentColor(displayLine);

                    // 检查这行换行后是否会超过8行限制
                    if (font.width(displayLine) > TEXT_WIDTH) {
                        String[] wrappedLines = wrapText(displayLine, TEXT_WIDTH);

                        // 如果换行后的总行数会超过剩余空间，整行移到下一页
                        if (renderLineIndex + wrappedLines.length > MAX_LINES) {
                            moveRemainingLinesToNextPage(currentPageContent, i);
                            updateButtons();
                            break;
                        }

                        // 第一行添加 •，后续行缩进对齐
                        int bulletWidth = font.width("• ");
                        for (int j = 0; j < wrappedLines.length; j++) {
                            if (j == 0) {
                                guiGraphics.drawString(font, "• " + wrappedLines[j],
                                        leftPos + TEXT_START_X, lineY, textColor, false);
                            } else {
                                guiGraphics.drawString(font, wrappedLines[j],
                                        leftPos + TEXT_START_X + bulletWidth, lineY, textColor, false);
                            }
                            renderLineIndex++;
                            lineY += LINE_HEIGHT;
                        }
                    } else {
                        // 单行文本正常添加 •
                        guiGraphics.drawString(font, "• " + displayLine,
                                leftPos + TEXT_START_X, lineY, textColor, false);
                        renderLineIndex++;
                    }
                } else {
                    // 空行，只增加行索引（不渲染内容）
                    renderLineIndex++;
                }
            }
        }

        // 渲染页码信息
        if (pages.length > 1) {
            // 先创建翻译组件
            Component pageInfoComponent = Component.translatable("gui.millenaire_rewrite.parchment.page_info",
                    currentPage + 1, pages.length);

            // 将组件转换为字符串用于宽度计算
            String pageInfoString = pageInfoComponent.getString();

            int pageInfoX = leftPos + (GUI_WIDTH - font.width(pageInfoString)) / 2;

            // 使用翻译组件进行渲染
            guiGraphics.drawString(font, pageInfoComponent, pageInfoX, topPos + GUI_HEIGHT - 40, 0x666666, false);
        }
        
        // 渲染按钮和其他组件
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    /**
     * 将剩余行移到下一页
     */
    private void moveRemainingLinesToNextPage(String[] currentPageContent, int startIndex) {

        // 确保 startIndex 不会越界
        if (startIndex >= currentPageContent.length) {

            return;
        }

        // 创建新页面数组，包含剩余的行（包括空行）
        String[] remainingLines = new String[currentPageContent.length - startIndex];
        System.arraycopy(currentPageContent, startIndex, remainingLines, 0, remainingLines.length);


        for (int k = 0; k < remainingLines.length; k++) {

        }

        // 创建新的当前页面数组
        String[] newCurrentPage = new String[startIndex];
        System.arraycopy(currentPageContent, 0, newCurrentPage, 0, startIndex);

        // 更新页面数组
        pages[currentPage] = newCurrentPage;

        // 插入新页面
        String[][] newPages = new String[pages.length + 1][];
        System.arraycopy(pages, 0, newPages, 0, currentPage + 1);
        newPages[currentPage + 1] = remainingLines;
        System.arraycopy(pages, currentPage + 1, newPages, currentPage + 2, pages.length - currentPage - 1);

        pages = newPages;



        // 确保当前页不会超出范围
        if (currentPage >= pages.length) {
            currentPage = pages.length - 1;
        }

        updateButtons();
    }

    /**
     * 根据内容确定文本颜色
     */
    private int getContentColor(String content) {
        String lowerContent = content.toLowerCase();
        
        if (lowerContent.contains("首领") || lowerContent.contains("leader")) {
            return 0x000000; // 金色
        } else if (lowerContent.contains("战士") || lowerContent.contains("守卫") || lowerContent.contains("warrior") || lowerContent.contains("guard")) {
            return 0x000000; // 红色
        } else if (lowerContent.contains("工人") || lowerContent.contains("农民") || lowerContent.contains("worker") || lowerContent.contains("farmer")) {
            return 0x000000; // 绿色
        } else if (lowerContent.contains("女性") || lowerContent.contains("妇女") || lowerContent.contains("woman") || lowerContent.contains("women")) {
            return 0x000000; // 紫色
        } else if (lowerContent.contains("儿童") || lowerContent.contains("孩子") || lowerContent.contains("child") || lowerContent.contains("children")) {
            return 0x000000; // 黄色
        } else if (lowerContent.contains("建筑") || lowerContent.contains("房屋") || lowerContent.contains("building") || lowerContent.contains("house")) {
            return 0x000000; // 蓝色
        } else if (lowerContent.contains("食物") || lowerContent.contains("武器") || lowerContent.contains("工具") || 
                   lowerContent.contains("food") || lowerContent.contains("weapon") || lowerContent.contains("tool")) {
            return 0x000000; // 青色
        } else {
            return 0x000000; // 黑色
        }
    }

    /**
     * 文本换行处理
     */
    /**
     * 改进的文本换行处理（同时支持中文和英文）
     */
    private String[] wrapText(String text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return new String[]{text};
        }

        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String testLine = currentLine.toString() + c;

            // 检查添加这个字符后是否超宽
            if (font.width(testLine) <= maxWidth) {
                currentLine.append(c);
            } else {
                // 尝试在合适的位置换行
                if (currentLine.length() > 0) {
                    // 如果是空格或标点，在此处换行
                    if (Character.isWhitespace(c) || isPunctuation(c)) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder();
                    } else {
                        // 回溯找到合适的换行位置
                        int breakPos = findBreakPosition(currentLine.toString());
                        if (breakPos > 0) {
                            lines.add(currentLine.substring(0, breakPos));
                            currentLine = new StringBuilder(currentLine.substring(breakPos));
                        } else {
                            lines.add(currentLine.toString());
                            currentLine = new StringBuilder(String.valueOf(c));
                        }
                    }
                } else {
                    // 单个字符就超宽，强制换行
                    lines.add(String.valueOf(c));
                    currentLine = new StringBuilder();
                }
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        // 确保每行都不超宽
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (font.width(line) > maxWidth) {
                // 如果某行仍然超宽，强制拆分
                String[] forcedSplit = forceSplitLine(line, maxWidth);
                lines.remove(i);
                for (int j = forcedSplit.length - 1; j >= 0; j--) {
                    lines.add(i, forcedSplit[j]);
                }
                i += forcedSplit.length - 1;
            }
        }

        return lines.toArray(new String[0]);
    }

    /**
     * 强制拆分超宽的行
     */
    private String[] forceSplitLine(String line, int maxWidth) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        int start = 0;

        while (start < line.length()) {
            int end = start + 1;
            while (end <= line.length() && font.width(line.substring(start, end)) <= maxWidth) {
                end++;
            }

            if (end > start + 1) {
                parts.add(line.substring(start, end - 1));
                start = end - 1;
            } else {
                // 单个字符就超宽，强制截断
                parts.add(line.substring(start, start + 1));
                start++;
            }
        }

        return parts.toArray(new String[0]);
    }

    /**
     * 找到合适的换行位置（空格或标点处）
     */
    private int findBreakPosition(String text) {
        // 从后往前找空格或标点
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c) || isPunctuation(c)) {
                return i + 1; // 在空格或标点后换行
            }
        }
        return -1; // 没有合适的换行位置
    }

    /**
     * 判断字符是否为标点符号
     */
    private boolean isPunctuation(char c) {
        return c == ',' || c == '.' || c == ';' || c == ':' || c == '!' || c == '?' ||
                c == '，' || c == '。' || c == '；' || c == '：' || c == '！' || c == '？';
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
