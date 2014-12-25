package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityAbstractTable extends TileEntityAbstractEnergyInventory implements IInventoryInvalidateable{
	protected static final int totalTime = 1000;
	
	protected short time, timeStep;
	protected byte requiredStardust;
	private boolean postLoadInvalidate;
	
	/**
	 * Return true to reset time and required stardust.
	 */
	protected abstract boolean onWorkFinished();
	public abstract int getHoldingStardust();
	
	@Override
	protected final byte getDrainTimer(){
		return 15;
	}
	
	@Override
	protected final float getDrainAmount(){
		return time < totalTime ? EnergyChunkData.energyDrainUnit : 0F;
	}

	@Override
	protected boolean isWorking(){
		return requiredStardust > 0 && getHoldingStardust() >= requiredStardust;
	}

	@Override
	protected void onWork(){
		if ((time += timeStep) >= totalTime){
			if (onWorkFinished()){
				resetTable();
				markDirty();
				invalidateInventory();
			}
			else time = totalTime;
		}
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (worldObj != null && !worldObj.isRemote && postLoadInvalidate){
			postLoadInvalidate = false;
			invalidateInventory();
		}
	}
	
	protected final void resetTable(){
		time = timeStep = requiredStardust = 0;
	}
	
	public final int getTime(){
		return time;
	}
	
	public final int getRequiredStardust(){
		return requiredStardust;
	}
	
	@SideOnly(Side.CLIENT)
	public final void setTime(int time){
		this.time = (short)time;
	}
	
	@SideOnly(Side.CLIENT)
	public final void setRequiredStardust(int requiredStardust){
		this.requiredStardust = (byte)requiredStardust;
	}

	@SideOnly(Side.CLIENT)
	public final int getScaledProgressTime(int scale){
		if (time == 0 && timeStep == 0)return -1;
		return MathUtil.ceil(time*(double)scale/totalTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);		
		postLoadInvalidate = true;
	}
}
