package uk.thecodingbadgers.minekart.world;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

public interface BlockChangeDelagator {

	public void setBlock(Location loc, Material mat, MaterialData data);

	public void setBlock(Location loc, Material mat);

	public List<BlockState> getChangedBlocks();

	public void resetChanges();

	public void delayResetChanges(long ticks);

}
