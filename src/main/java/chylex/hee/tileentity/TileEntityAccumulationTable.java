package chylex.hee.tileentity;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemAbstractEnergyAcceptor;
import chylex.hee.mechanics.energy.EnergyChunkData;

public class TileEntityAccumulationTable extends TileEntityAbstractTable{
	private static final int[] slotsAll = new int[]{ 0 };
	private static final float maxStoredEnergy = EnergyChunkData.energyDrainUnit*50F;
	
	private float storedEnergy;
	
	@Override
	public void invalidateInventory(){}
	
	@Override
	public float getMaxStoredEnergy(){
		return maxStoredEnergy;
	}
	
	@Override
	protected boolean isWorking(){
		return storedEnergy < maxStoredEnergy;
	}
	
	@Override
	protected void onWork(){
		storedEnergy += getDrainAmount();
	}

	@Override
	protected boolean onWorkFinished(){
		return false;
	}
	
	@Override
	public int getHoldingStardust(){
		return 0;
	}
	
	@Override
	public int getSizeInventory(){
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return slot == 0 && is.getItem() instanceof ItemAbstractEnergyAcceptor;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return slotsAll;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.accumulationTable";
	}
}