package chylex.hee.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class EntityItemInstabilityOrb extends EntityItem{
	public EntityItemInstabilityOrb(World world){
		super(world);
	}
	
	public EntityItemInstabilityOrb(World world, double x, double y, double z, ItemStack is){
		super(world,x,y,z,is);
		
		for(int a = 0; a < is.stackSize-1; a++){
			ItemStack newIS = is.copy();
			newIS.stackSize = 1;
			EntityItem item = new EntityItemInstabilityOrb(world,x,y,z,newIS);
			item.delayBeforeCanPickup = 40;
			world.spawnEntityInWorld(item);
		}
		
		is.stackSize = 1;
	}

	@Override
	public void onUpdate(){
		/* Yeet */
		super.onUpdate();
	}
	
	private void detonate() {
		/* Yeet */
		setDead();
	}
	
	@Override
	public boolean combineItems(EntityItem item){
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.isExplosion()){
			if (rand.nextInt(6) != 0){
				age -= 10-rand.nextInt(80);
				return false;
			}
		}
		
		return super.attackEntityFrom(source,amount);
	}
	
	public static final class ExplosionOrb extends Explosion{
		private final World worldObj;
		private final int dist = 16;
		private final Map<EntityPlayer,Vec3> hurtPlayers = new HashMap<>();
		
		public ExplosionOrb(World world, Entity sourceEntity, double x, double y, double z, float power){
			super(world,sourceEntity,x,y,z,power);
			this.worldObj = world;
			isSmoking = true;
			isFlaming = false;
		}
		
		@Override
		public void doExplosionA(){
			/* Yeet */
		}
		
		@Override
		public Map func_77277_b(){
			return hurtPlayers;
		}
		
		private boolean canDamageEntity(Entity entity){
			return !(entity instanceof EntityLiving);
		}
	}
}
