package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemDragonEgg;

public class ItemBlockDragonEgg extends ItemBlock{
	public ItemBlockDragonEgg(Block block){
		super(block);
	}

	@Override
	public final boolean hasCustomEntity(ItemStack is){
		return true;
	}

	@Override
	public final Entity createEntity(World world, Entity originalEntity, ItemStack is){
		EntityItem newEntity = new EntityItemDragonEgg(world,originalEntity.posX,originalEntity.posY,originalEntity.posZ,is);
		newEntity.delayBeforeCanPickup = 10;
		
		newEntity.copyLocationAndAnglesFrom(originalEntity);
		newEntity.motionX = newEntity.motionY = newEntity.motionZ = 0D;
		newEntity.addVelocity(originalEntity.motionX,originalEntity.motionY,originalEntity.motionZ);
		return newEntity;
	}
}
