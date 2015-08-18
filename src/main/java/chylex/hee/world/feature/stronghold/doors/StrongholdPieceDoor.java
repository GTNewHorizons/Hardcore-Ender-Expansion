package chylex.hee.world.feature.stronghold.doors;
import java.util.Random;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public abstract class StrongholdPieceDoor extends StrongholdPiece{
	public static StrongholdPieceDoor[] generateEmpty(){
		return new StrongholdPieceDoor[]{
			new StrongholdPieceDoorEmpty(Facing4.EAST_POSX),
			new StrongholdPieceDoorEmpty(Facing4.SOUTH_POSZ)
		};
	}
	
	private static class StrongholdPieceDoorEmpty extends StrongholdPieceDoor{
		StrongholdPieceDoorEmpty(Facing4 facing){
			super(facing);
		}
		
		@Override
		protected void generateDoor(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){}
	}
	
	protected final Facing4 facing;
	
	protected StrongholdPieceDoor(Facing4 facing){
		super(Type.DOOR,new Size(facing.getX() != 0 ? 1 : 5,5,facing.getZ() != 0 ? 1 : 5));
		this.facing = facing;
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		placeCube(world,rand,placeStoneBrick,x,y,z,x+maxX,y,z+maxZ);
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ);
		placeLine(world,rand,placeStoneBrick,x,y+1,z,x,y+maxY-1,z);
		placeLine(world,rand,placeStoneBrick,x+maxX,y+1,z+maxZ,x+maxX,y+maxY-1,z+maxZ);
		generateDoor(inst,world,rand,x,y,z);
	}
	
	protected abstract void generateDoor(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z);
}