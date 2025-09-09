package org.millenaire.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPanel extends ModelBase {
  public ModelRenderer signBoard = new ModelRenderer(this, 0, 0);
  
  public ModelPanel() {
    this.signBoard.addBox(-12.0F, -12.0F, -1.0F, 24, 24, 2, 0.0F);
  }
  
  public void renderSign() {
    this.signBoard.render(0.0625F);
  }
}
