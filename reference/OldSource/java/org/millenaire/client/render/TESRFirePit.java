package org.millenaire.client.render;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.util.vector.Quaternion;
import org.millenaire.common.block.BlockFirePit;
import org.millenaire.common.entity.TileEntityFirePit;

public class TESRFirePit extends TileEntitySpecialRenderer<TileEntityFirePit> {
  private static final TRSRTransformation[] COOKING_POSITIONS = new TRSRTransformation[] { get(0.5F, 1.0F, 0.4F, 25.0F, 180.0F, -45.0F, 0.35F), get(0.5F, 0.9F, 0.5F, 0.0F, 45.0F, -45.0F, 0.35F), 
      get(0.5F, 1.0F, 0.6F, -25.0F, 180.0F, -45.0F, 0.35F) };
  
  private static final TRSRTransformation[] COOKED_POSITIONS = new TRSRTransformation[] { get(0.5F, 0.9F, 0.4F, 25.0F, 180.0F, -45.0F, 0.35F), get(0.5F, 0.9F, 0.5F, 0.0F, -45.0F, -45.0F, 0.35F), 
      get(0.5F, 0.9F, 0.6F, -25.0F, 180.0F, -45.0F, 0.35F) };
  
  private static Quaternion transformation = new Quaternion();
  
  private static void apply(TRSRTransformation transform) {
    Vector3f translate = transform.getTranslation();
    Quat4f left = transform.getLeftRot();
    Quat4f right = transform.getRightRot();
    Vector3f scale = transform.getScale();
    GlStateManager.translate(translate.x, translate.y, translate.z);
    transformation.set(left.x, left.y, left.z, left.w);
    GlStateManager.rotate(transformation);
    transformation.set(right.x, right.y, right.z, right.w);
    GlStateManager.rotate(transformation);
    GlStateManager.scale(scale.x, scale.y, scale.z);
  }
  
  private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
    return new TRSRTransformation(new Vector3f(tx, ty, tz), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null);
  }
  
  private static void render(ItemStack stack, RenderItem item) {
    item.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
  }
  
  private static void renderPlacedItem(RenderItem item, ItemStack stack, TRSRTransformation transform) {
    if (!stack.isEmpty()) {
      GlStateManager.pushMatrix();
      apply(transform);
      render(stack, item);
      GlStateManager.popMatrix();
    } 
  }
  
  public void render(TileEntityFirePit te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    RenderItem item = Minecraft.getMinecraft().getRenderItem();
    IBlockState firePitBS = te.getWorld().getBlockState(te.getPos());
    if (!(firePitBS.getBlock() instanceof BlockFirePit))
      return; 
    double alignment = ((BlockFirePit.EnumAlignment)firePitBS.getValue((IProperty)BlockFirePit.ALIGNMENT)).angle;
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
    GlStateManager.rotate((float)alignment, 0.0F, 1.0F, 0.0F);
    GlStateManager.translate(-0.5D, -0.5D, -0.5D);
    ItemStack fuel = te.fuel.getStackInSlot(0);
    if (!fuel.isEmpty()) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.5D, 0.2D, 0.5D);
      GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
      int posTotal = te.getPos().getX() + te.getPos().getY() + te.getPos().getZ() & 0x3;
      GlStateManager.rotate((90 * posTotal), 0.0F, 0.0F, 1.0F);
      GlStateManager.scale(0.5D, 0.5D, 0.5D);
      render(fuel, item);
      GlStateManager.popMatrix();
    } 
    for (int i = 0; i < 3; i++) {
      ItemStack input = te.inputs.getStackInSlot(i);
      ItemStack output = te.outputs.getStackInSlot(i);
      renderPlacedItem(item, input, COOKING_POSITIONS[i]);
      renderPlacedItem(item, output, COOKED_POSITIONS[i]);
    } 
    GlStateManager.popMatrix();
  }
}
