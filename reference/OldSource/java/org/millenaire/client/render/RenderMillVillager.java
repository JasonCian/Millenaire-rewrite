package org.millenaire.client.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
import org.millenaire.common.block.BlockMillBed;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.pathing.atomicstryker.AS_PathEntity;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class RenderMillVillager extends RenderBiped<MillVillager> {
  private static final int MAX_VIEW_DISTANCE = 64;
  
  private static final float LINE_HEIGHT = 0.25F;
  
  private static final int LINE_SIZE = 60;
  
  public static class FactoryFemaleAsym implements IRenderFactory<MillVillager.EntityGenericAsymmFemale> {
    public Render<? super MillVillager.EntityGenericAsymmFemale> createRenderFor(RenderManager manager) {
      return (Render<? super MillVillager.EntityGenericAsymmFemale>)new RenderMillVillager(manager, new ModelFemaleAsymmetrical());
    }
  }
  
  public static class FactoryFemaleSym implements IRenderFactory<MillVillager.EntityGenericSymmFemale> {
    public Render<? super MillVillager.EntityGenericSymmFemale> createRenderFor(RenderManager manager) {
      return (Render<? super MillVillager.EntityGenericSymmFemale>)new RenderMillVillager(manager, new ModelFemaleSymmetrical());
    }
  }
  
  public static class FactoryMale implements IRenderFactory<MillVillager.EntityGenericMale> {
    public Render<? super MillVillager.EntityGenericMale> createRenderFor(RenderManager manager) {
      return (Render<? super MillVillager.EntityGenericMale>)new RenderMillVillager(manager, new ModelMillVillager());
    }
  }
  
  public static final FactoryMale FACTORY_MALE = new FactoryMale();
  
  public static final FactoryFemaleAsym FACTORY_FEMALE_ASYM = new FactoryFemaleAsym();
  
  public static final FactoryFemaleSym FACTORY_FEMALE_SYM = new FactoryFemaleSym();
  
  private static void drawItem2D(FontRenderer fontRendererIn, ItemStack itemStack, float x, float y, float z, float iconPos, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking) {
    RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    if (!itemStack.isEmpty()) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(0.25F - iconPos * 0.5F, 0.0F, -7.5F);
      GlStateManager.scale(-0.025F, -0.025F, 0.05F);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      if (!isSneaking)
        GlStateManager.disableDepth(); 
      renderItem.renderItemAndEffectIntoGUI(itemStack, 0, 0);
      GlStateManager.disableAlpha();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
    } 
  }
  
  private static void drawNameplateColour(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, int colour) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);
    GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate((isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
    GlStateManager.scale(-0.025F, -0.025F, 0.025F);
    GlStateManager.disableLighting();
    GlStateManager.depthMask(false);
    if (!isSneaking)
      GlStateManager.disableDepth(); 
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    if (str.length() > 0) {
      int xDelta = fontRendererIn.getStringWidth(str) / 2;
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((-xDelta - 1), (-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
      bufferbuilder.pos((-xDelta - 1), (8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
      bufferbuilder.pos((xDelta + 1), (8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
      bufferbuilder.pos((xDelta + 1), (-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
    } 
    if (!isSneaking) {
      fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, colour);
      GlStateManager.enableDepth();
    } 
    GlStateManager.depthMask(true);
    fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, colour);
    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
  }
  
  public RenderMillVillager(RenderManager renderManager, ModelMillVillager modelbiped) {
    super(renderManager, modelbiped, 0.5F);
    addLayer((LayerRenderer)new LayerBipedArmor((RenderLivingBase)this));
    for (int layer = 0; layer < 2; layer++)
      addLayer(new LayerVillagerClothes((RenderLivingBase<MillVillager>)this, modelbiped, layer)); 
  }
  
  protected void applyRotations(MillVillager v, float par2, float rotationYaw, float partialTicks) {
    if (v.isEntityAlive() && v.isVillagerSleeping()) {
      float orientation = -v.getBedOrientationInDegrees() + 90.0F;
      if (orientation == 0.0F) {
        GL11.glTranslatef(0.5F, 0.0F, -0.5F);
      } else if (orientation == 90.0D) {
        GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
      } else if (orientation == -180.0D) {
        GL11.glTranslatef(-0.5F, 0.0F, 0.5F);
      } else if (orientation == -90.0D) {
        GL11.glTranslatef(0.5F, 0.0F, 0.5F);
      } 
      GL11.glRotatef(orientation, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(getDeathMaxRotation(v), 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
      Block block = v.getPos().getBlock(v.world);
      if (block instanceof BlockMillBed) {
        float adjustement = 0.0F + ((BlockMillBed)block).getBedHeight() / 16.0F;
        GL11.glTranslatef(0.0F, 0.0F, 0.6F - adjustement);
      } else if (block instanceof net.minecraft.block.BlockBed) {
        GL11.glTranslatef(0.0F, 0.0F, 0.1F);
      } 
    } else {
      super.applyRotations(v, par2, rotationYaw, partialTicks);
    } 
  }
  
  private List<ItemStack> defineSpecialIcons(MillVillager villager) {
    List<ItemStack> icons = new ArrayList<>();
    if (villager.vtype != null) {
      if (villager.vtype.isChief)
        icons.add(Items.GOLDEN_HELMET.getDefaultInstance()); 
      if (villager.getCurrentGoal() != null && villager.getCurrentGoal().getFloatingIcon() != null)
        icons.add(villager.getCurrentGoal().getFloatingIcon()); 
      if (villager.isForeignMerchant())
        icons.add(MillItems.PURSE.getDefaultInstance()); 
      if (villager.vtype.hireCost > 0)
        icons.add(MillItems.DENIER.getDefaultInstance()); 
      if (villager.isRaider)
        icons.add(MillItems.NORMAN_AXE.getDefaultInstance()); 
    } 
    return icons;
  }
  
  private void displayText(MillVillager v, String text, int colour, double x, double y, double z) {
    renderLivingLabelColour(v, text, x, y, z, 64, colour);
  }
  
  public void doRender(MillVillager entity, double x, double y, double z, float entityYaw, float partialTicks) {
    MillVillager villager = entity;
    super.doRender(entity, x, y, z, entityYaw, partialTicks);
    doRenderVillagerName(villager, x, y, z);
  }
  
  public void doRenderVillagerName(MillVillager villager, double x, double y, double z) {
    if (villager.shouldLieDown) {
      double height = (villager.getRenderBoundingBox()).maxY - (villager.getRenderBoundingBox()).minY;
      float angle = villager.getBedOrientationInDegrees();
      double dx = 0.0D, dz = 0.0D;
      if (angle == 0.0F) {
        dx = -height * 0.9D;
      } else if (angle == 90.0F) {
        dz = -height * 0.9D;
      } else if (angle == 180.0F) {
        dx = height * 0.9D;
      } else if (angle == 270.0F) {
        dz = height * 0.9D;
      } 
      x = villager.lastTickPosX + dx;
      z = villager.lastTickPosZ + dz;
    } 
    Minecraft minecraft = FMLClientHandler.instance().getClient();
    EntityPlayerSP entityPlayerSP = minecraft.player;
    UserProfile profile = Mill.clientWorld.getProfile((EntityPlayer)entityPlayerSP);
    float f4 = villager.getDistance((Entity)entityPlayerSP);
    if (f4 < MillConfigValues.VillagersNamesDistance) {
      String gameSpeech = villager.getGameSpeech(Mill.proxy.getTheSinglePlayer().getName());
      String nativeSpeech = villager.getNativeSpeech(Mill.proxy.getTheSinglePlayer().getName());
      float height = 0.0F;
      if (MillConfigValues.DEV && Mill.serverWorlds.size() > 0 && ((MillWorldData)Mill.serverWorlds.get(0)).getVillagerById(villager.getVillagerId()) != null && !MillConfigValues.DEV) {
        MillVillager dv = ((MillWorldData)Mill.serverWorlds.get(0)).getVillagerById(villager.getVillagerId());
        AS_PathEntity pe = dv.pathEntity;
        if (pe != null && pe.pointsCopy != null) {
          PathPoint[] pp = pe.pointsCopy;
          if (pp != null && 
            pp.length > 0) {
            String s = "";
            for (int i = pe.getCurrentPathIndex(); i < pp.length && i < pe.getCurrentPathIndex() + 5; i++)
              s = s + "(" + pp[i] + ") "; 
            displayText(villager, s, -1593835521, (float)x, ((float)y + height), (float)z);
            height += 0.25F;
          } 
          if (pe != null) {
            if (pe.getCurrentPathLength() > 0) {
              displayText(villager, "Path: " + pe
                  .getCurrentPathLength() + " end: " + pe.getCurrentTargetPathPoint() + " dist: " + (
                  Math.round(villager.getPos().horizontalDistanceTo(pe.getFinalPathPoint()) * 10.0D) / 10L) + " index: " + pe.getCurrentPathIndex() + " " + dv.hasPath() + ", stuck: " + dv.longDistanceStuck, -1593835521, (float)x, ((float)y + height), (float)z);
            } else {
              displayText(villager, "Empty path, stuck: " + dv.longDistanceStuck, -1593835521, (float)x, ((float)y + height), (float)z);
            } 
            height += 0.25F;
          } 
        } else {
          displayText(villager, "Null path entity, stuck: " + dv.longDistanceStuck, -1593835521, (float)x, ((float)y + height), (float)z);
          height += 0.25F;
        } 
        if (dv.getAttackTarget() == null) {
          displayText(villager, "Pos: " + dv
              .getPos() + " Path dest: " + dv.getPathDestPoint() + " Goal dest: " + dv.getGoalDestPoint() + " dist: " + (
              Math.round(dv.getPos().horizontalDistanceTo(dv.getPathDestPoint()) * 10.0D) / 10L) + " sm: " + dv.stopMoving + " jps busy: " + dv.pathPlannerJPS.isBusy(), -1593835521, (float)x, ((float)y + height), (float)z);
        } else {
          displayText(villager, "Pos: " + dv
              .getPos() + " Entity: " + dv.getAttackTarget() + " dest: " + new Point((Entity)dv.getAttackTarget()) + " dist: " + (
              Math.round(dv.getPos().horizontalDistanceTo(new Point((Entity)dv.getAttackTarget())) * 10.0D) / 10L) + " sm: " + dv.stopMoving + " jps busy: " + dv.pathPlannerJPS.isBusy(), -1593835521, (float)x, ((float)y + height), (float)z);
        } 
        height += 0.25F;
      } 
      if (villager.hiredBy == null) {
        if (gameSpeech != null) {
          List<String> lines = new ArrayList<>();
          String line = gameSpeech;
          while (line.length() > 60) {
            int cutoff = line.lastIndexOf(' ', 60);
            if (cutoff == -1)
              cutoff = 60; 
            String subLine = line.substring(0, cutoff);
            line = line.substring(subLine.length()).trim();
            lines.add(subLine);
          } 
          lines.add(line);
          for (int i = lines.size() - 1; i >= 0; i--) {
            displayText(villager, lines.get(i), -1596166533, (float)x, ((float)y + height), (float)z);
            height += 0.25F;
          } 
        } 
        if (nativeSpeech != null) {
          List<String> lines = new ArrayList<>();
          String line = nativeSpeech;
          while (line.length() > 60) {
            int cutoff = line.lastIndexOf(' ', 60);
            if (cutoff == -1)
              cutoff = 60; 
            String subLine = line.substring(0, cutoff);
            line = line.substring(subLine.length()).trim();
            lines.add(subLine);
          } 
          lines.add(line);
          for (int i = lines.size() - 1; i >= 0; i--) {
            displayText(villager, lines.get(i), -1603244324, (float)x, ((float)y + height), (float)z);
            height += 0.25F;
          } 
        } 
        if (MillConfigValues.displayNames && villager.getCurrentGoal() != null) {
          displayText(villager, villager.getCurrentGoal().gameName(villager), -1596142994, (float)x, ((float)y + height), (float)z);
          height += 0.25F;
        } 
        if (villager.getAttackTarget() != null) {
          displayText(villager, LanguageUtilities.string("other.villagerattackinglabel", new String[] { villager.getAttackTarget().getName() }), -1593901056, (float)x, ((float)y + height), (float)z);
          height += 0.25F;
        } 
        if (profile.villagersInQuests.containsKey(Long.valueOf(villager.getVillagerId()))) {
          QuestInstance qi = (QuestInstance)profile.villagersInQuests.get(Long.valueOf(villager.getVillagerId()));
          if ((qi.getCurrentVillager()).id == villager.getVillagerId()) {
            displayText(villager, "[" + qi.getLabel(profile) + "]", -1596072483, (float)x, ((float)y + height), (float)z);
            height += 0.25F;
          } 
        } 
        if (villager.isRaider) {
          displayText(villager, LanguageUtilities.string("ui.raider"), -1593872773, (float)x, ((float)y + height), (float)z);
          height += 0.25F;
        } 
        if (villager.vtype.showHealth) {
          displayText(villager, LanguageUtilities.string("hire.health") + ": " + (villager.getHealth() * 0.5D) + "/" + (villager.getMaxHealth() * 0.5D), -1596072483, (float)x, ((float)y + height), (float)z);
          height += 0.25F;
        } 
      } else if (villager.hiredBy.equals(profile.playerName)) {
        String s = LanguageUtilities.string("hire.health") + ": " + (villager.getHealth() * 0.5D) + "/" + (villager.getMaxHealth() * 0.5D);
        if (villager.aggressiveStance) {
          s = s + " - " + LanguageUtilities.string("hire.aggressive");
        } else {
          s = s + " - " + LanguageUtilities.string("hire.passive");
        } 
        displayText(villager, s, -1596142994, (float)x, ((float)y + height), (float)z);
        height += 0.25F;
        s = LanguageUtilities.string("hire.timeleft", new String[] { "" + Math.round((float)((villager.hiredUntil - villager.world.getWorldTime()) / 1000L)) });
        displayText(villager, s, -1596072483, (float)x, ((float)y + height), (float)z);
        height += 0.25F;
      } else {
        String s = LanguageUtilities.string("hire.hiredby", new String[] { villager.hiredBy });
        displayText(villager, s, -1596072483, (float)x, ((float)y + height), (float)z);
        height += 0.25F;
      } 
      if (villager.isDead)
        displayText(villager, "Dead on client!", -1593901056, (float)x, ((float)y + height), (float)z); 
      if (villager.isDeadOnServer)
        displayText(villager, "Dead on server!", -1593901056, (float)x, ((float)y + height), (float)z); 
      if (MillConfigValues.displayNames && !villager.vtype.hideName) {
        displayText(villager, villager.getName() + ", " + villager.getNativeOccupationName(), -1593835521, (float)x, ((float)y + height), (float)z);
        height += 0.25F;
      } 
      List<ItemStack> specialIcons = defineSpecialIcons(villager);
      if (!specialIcons.isEmpty()) {
        height += 0.2F;
        renderIcons(villager, specialIcons, (float)x, ((float)y + height), (float)z, 64);
        displayText(villager, "", -1593835521, (float)x, ((float)y + height), (float)z);
      } 
    } 
  }
  
  protected ResourceLocation getEntityTexture(MillVillager villager) {
    return villager.texture;
  }
  
  protected void preRenderCallback(MillVillager villager, float f) {
    preRenderScale(villager, f);
  }
  
  protected void preRenderScale(MillVillager villager, float f) {
    float scale = 1.0F;
    if (villager.getRecord() != null)
      scale = (villager.getRecord()).scale; 
    GL11.glScalef(scale, scale, scale);
  }
  
  private void renderIcons(MillVillager entityIn, List<ItemStack> icons, double x, double y, double z, int maxDistance) {
    double d0 = entityIn.getDistanceSq(this.renderManager.renderViewEntity);
    if (d0 <= (maxDistance * maxDistance)) {
      boolean isSneaking = entityIn.isSneaking();
      float viewerYaw = this.renderManager.playerViewY;
      float viewerPitch = this.renderManager.playerViewX;
      boolean isThirdPersonFrontal = (this.renderManager.options.thirdPersonView == 2);
      float f2 = entityIn.height + 0.5F - (isSneaking ? 0.25F : 0.0F);
      int pos = 0;
      for (ItemStack icon : icons) {
        drawItem2D(getFontRendererFromRenderManager(), icon, (float)x, (float)y + f2, (float)z, pos - (icons.size() - 1) / 2.0F, viewerYaw, viewerPitch, isThirdPersonFrontal, isSneaking);
        pos++;
      } 
    } 
  }
  
  private void renderLivingLabelColour(MillVillager entityIn, String str, double x, double y, double z, int maxDistance, int colour) {
    double d0 = entityIn.getDistanceSq(this.renderManager.renderViewEntity);
    if (d0 <= (maxDistance * maxDistance)) {
      boolean isSneaking = entityIn.isSneaking();
      float viewerYaw = this.renderManager.playerViewY;
      float viewerPitch = this.renderManager.playerViewX;
      boolean isThirdPersonFrontal = (this.renderManager.options.thirdPersonView == 2);
      float f2 = entityIn.height + 0.5F - (isSneaking ? 0.25F : 0.0F);
      int i = "deadmau5".equals(str) ? -10 : 0;
      drawNameplateColour(getFontRendererFromRenderManager(), str, (float)x, (float)y + f2, (float)z, i, viewerYaw, viewerPitch, isThirdPersonFrontal, isSneaking, colour);
    } 
  }
}
