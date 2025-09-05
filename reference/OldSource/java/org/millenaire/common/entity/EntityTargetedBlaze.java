package org.millenaire.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.millenaire.common.utilities.Point;

public class EntityTargetedBlaze extends EntityBlaze {
  public Point target = null;
  
  public EntityTargetedBlaze(World par1World) {
    super(par1World);
  }
  
  protected boolean canDespawn() {
    return false;
  }
  
  private boolean isCourseTraversable(double par1, double par3, double par5, double par7) {
    double d4 = (this.target.x - this.posX) / par7;
    double d5 = (this.target.y - this.posY) / par7;
    double d6 = (this.target.z - this.posZ) / par7;
    AxisAlignedBB axisalignedbb = getCollisionBoundingBox().expand(0.0D, 0.0D, 0.0D);
    for (int i = 1; i < par7; i++) {
      axisalignedbb.offset(d4, d5, d6);
      if (!this.world.getCollisionBoxes((Entity)this, axisalignedbb).isEmpty())
        return false; 
    } 
    return true;
  }
  
  public boolean isWet() {
    return false;
  }
  
  public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
    super.readFromNBT(par1nbtTagCompound);
    this.target = Point.read(par1nbtTagCompound, "targetPoint");
  }
  
  public NBTTagCompound writeToNBT(NBTTagCompound par1nbtTagCompound) {
    super.writeToNBT(par1nbtTagCompound);
    if (this.target != null)
      this.target.write(par1nbtTagCompound, "targetPoint"); 
    return par1nbtTagCompound;
  }
}
