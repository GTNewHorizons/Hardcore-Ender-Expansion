package chylex.hee.mechanics.voidchest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import chylex.hee.tileentity.TileEntityVoidChest;

public class InventoryVoidChest extends InventoryBasic {
	private TileEntityVoidChest chest;

	public InventoryVoidChest() {
		super("container.voidChest", false, 27);
	}

	public InventoryVoidChest setChest(TileEntityVoidChest chest) {
		this.chest = chest;
		return this;
	}

	private static boolean isSameItem(ItemStack dis, ItemStack other) {
		if (dis == null && other != null || dis != null && other == null)
			return false;
		return dis == other || dis.getItem() == other.getItem() && dis.getItemDamage() == other.getItemDamage() && ItemStack.areItemStackTagsEqual(dis, other);
	}

	public void putItem(ItemStack is) {
		int size = getSizeInventory();

		if (is.isStackable()) {
			boolean markDirty = false;

			for (int a = 0; a < size; a++) {
				ItemStack slotIS = getStackInSlot(a);

				if (!isSameItem(slotIS, is))
					continue;

				int combined = slotIS.stackSize + is.stackSize,
						max = is.getMaxStackSize();

				if (combined <= max) {
					slotIS.stackSize = combined;
					is.stackSize = 0;
					markDirty();
					return;
				} else if (slotIS.stackSize < max) {
					is.stackSize -= max - slotIS.stackSize;
					slotIS.stackSize = max;
					markDirty = true;
				}
			}

			if (markDirty)
				markDirty();

			if (is.stackSize == 0)
				return;
		}

		for (int slot = 0; slot < this.getSizeInventory(); slot++) {
			if (getStackInSlot(slot) == null) {
				setInventorySlotContents(slot, is);
				break;
			}
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return (chest == null || chest.canPlayerUse(player)) && super.isUseableByPlayer(player);
	}

	@Override
	public void openInventory() {
		if (chest != null)
			chest.addPlayerToOpenList();
		super.openInventory();
	}

	@Override
	public void closeInventory() {
		if (chest != null)
			chest.removePlayerFromOpenList();
		super.closeInventory();
		chest = null;
	}
}