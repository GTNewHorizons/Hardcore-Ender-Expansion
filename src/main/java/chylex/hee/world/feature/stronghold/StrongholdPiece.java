package chylex.hee.world.feature.stronghold;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.WorldGenStronghold;
import chylex.hee.world.loot.WeightedLootTable;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece;
import chylex.hee.world.structure.dungeon.generators.DungeonGeneratorSpreading.ISpreadingGeneratorPieceType;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.structure.util.IStructureTileEntity;
import chylex.hee.world.util.RandomAmount;
import chylex.hee.world.util.Size;

public abstract class StrongholdPiece extends StructureDungeonPiece{
	protected enum Type implements ISpreadingGeneratorPieceType{
		CORRIDOR, DOOR, ROOM, DEADEND;
		
		@Override
		public boolean isRoom(){
			return this == ROOM || this == DEADEND; // the spreading generator cannot create deadends, so make a couple of them intentionally
		}
		
		@Override
		public boolean isDoor(){
			return this == DOOR;
		}
	}
	
	protected static final IConnectWith fromRoom = type -> type == Type.CORRIDOR || type == Type.DOOR;
	protected static final IConnectWith fromDeadEnd = type -> type == Type.CORRIDOR;
	protected static final IConnectWith fromDoor = type -> true;
	protected static final IConnectWith withAnything = type -> true;
	
	private static final BlockInfo[] blocksStoneBrick = new BlockInfo[]{
		new BlockInfo(Blocks.stonebrick,Meta.stoneBrickPlain),
		new BlockInfo(Blocks.stonebrick,Meta.stoneBrickMossy),
		new BlockInfo(Blocks.stonebrick,Meta.stoneBrickCracked),
		new BlockInfo(Blocks.monster_egg,Meta.silverfishPlain),
		new BlockInfo(Blocks.monster_egg,Meta.silverfishMossy),
		new BlockInfo(Blocks.monster_egg,Meta.silverfishCracked)
	};
	
	protected static final IBlockPicker placeStoneBrick = rand -> {
		int chance = rand.nextInt(100);
		
		if (chance < 45)return blocksStoneBrick[0];
		else if (chance < 75)return blocksStoneBrick[1];
		else if (chance < 95)return blocksStoneBrick[2];
		else{
			chance = rand.nextInt(100);
			
			if (chance < 47)return blocksStoneBrick[3];
			else if (chance < 79)return blocksStoneBrick[4];
			else return blocksStoneBrick[5];
		}
	};
	
	protected static final IBlockPicker placeStoneBrickPlain = blocksStoneBrick[0];
	protected static final IBlockPicker placeStoneBrickChiseled = new BlockInfo(Blocks.stonebrick,Meta.stoneBrickChiseled);
	
	protected static final IBlockPicker placeStoneBrickStairs(Facing4 ascendsTowards, boolean flip){
		return new BlockInfo(Blocks.stone_brick_stairs,Meta.getStairs(ascendsTowards,flip));
	}
	
	protected static final void placeStairOutline(StructureWorld world, Random rand, Block block, int centerX, int y, int centerZ, int distance, boolean outwards, boolean flip){
		IBlockPicker[] stairs = new IBlockPicker[]{
			IBlockPicker.basic(block,Meta.getStairs(outwards ? Facing4.SOUTH_POSZ : Facing4.NORTH_NEGZ,flip)),
			IBlockPicker.basic(block,Meta.getStairs(outwards ? Facing4.NORTH_NEGZ : Facing4.SOUTH_POSZ,flip)),
			IBlockPicker.basic(block,Meta.getStairs(outwards ? Facing4.EAST_POSX : Facing4.WEST_NEGX,flip)),
			IBlockPicker.basic(block,Meta.getStairs(outwards ? Facing4.WEST_NEGX : Facing4.EAST_POSX,flip))
		};
		
		for(int facingInd = 0, off, perX, perZ; facingInd < Facing4.list.length; facingInd++){
			Facing4 facing = Facing4.list[facingInd];
			off = facing.getX() == 0 ? distance-1 : distance;
			perX = facing.perpendicular().getX();
			perZ = facing.perpendicular().getZ();
			placeLine(world,rand,stairs[facingInd],centerX+distance*facing.getX()-off*perX,y,centerZ+distance*facing.getZ()-off*perZ,centerX+distance*facing.getX()+off*perX,y,centerZ+distance*facing.getZ()+off*perZ);
		}
	}
	
	private static final void generateLoot(WeightedLootTable lootTable, int items, int cobwebs, TileEntityChest chest, Random rand){
		while(items-- > 0){
			chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),lootTable.generateWeighted(null,rand));
		}
		
		for(int slot; cobwebs > 0; cobwebs--){
			slot = rand.nextInt(chest.getSizeInventory());
			if (chest.getStackInSlot(slot) == null)chest.setInventorySlotContents(slot,new ItemStack(rand.nextInt(3) == 0 ? BlockList.ancient_web : Blocks.web));
		}
	}
	
	protected static final IStructureTileEntity generateLootGeneral = (tile, rand) -> {
		generateLoot(WorldGenStronghold.lootGeneral,RandomAmount.aroundCenter.generate(rand,3,10),rand.nextInt(7),(TileEntityChest)tile,rand);
	};
	
	protected static final IStructureTileEntity generateLootLibraryMain = (tile, rand) -> {
		generateLoot(WorldGenStronghold.lootLibrary,RandomAmount.linear.generate(rand,9,11),2+rand.nextInt(2),(TileEntityChest)tile,rand);
	};
	
	protected static final IStructureTileEntity generateLootLibrarySecondary = (tile, rand) -> {
		generateLoot(WorldGenStronghold.lootLibrary,RandomAmount.aroundCenter.generate(rand,4,6),4+rand.nextInt(3),(TileEntityChest)tile,rand);
	};
	
	public StrongholdPiece(Type type, Size size){
		super(type,size);
	}
	
	@Override
	public int calculateInstWeight(int availableConnections){
		return type == Type.ROOM ? 2*getWeightFactor(availableConnections) : type == Type.CORRIDOR ? getWeightFactor(availableConnections) : 0;
	}
	
	protected static final int getWeightFactor(int availableConnections){
		return MathUtil.ceil(Math.pow(availableConnections,1.5D));
	}
}
