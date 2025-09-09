package org.millenaire.common.block.mock;

import net.minecraft.block.BlockBanner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.millenaire.common.entity.TileEntityMockBanner;

public class MockBlockBannerStanding extends BlockBanner.BlockBannerStanding {
  public final int bannerType;
  
  public MockBlockBannerStanding(int bannerType) {
    this.bannerType = bannerType;
  }
  
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return (TileEntity)new TileEntityMockBanner();
  }
}
