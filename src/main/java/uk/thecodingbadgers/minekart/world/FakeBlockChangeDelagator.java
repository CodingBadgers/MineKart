package uk.thecodingbadgers.minekart.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import uk.thecodingbadgers.minekart.MineKart;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

@SuppressWarnings("deprecation")
public class FakeBlockChangeDelagator implements BlockChangeDelagator {

	private List<BlockState> changes = new ArrayList<BlockState>();
	private Player player;

	public FakeBlockChangeDelagator(Player player) {
		this.player = player;
	}

	@Override
	public void setBlock(Location loc, Material mat, MaterialData data) {
		setBlock0(loc, mat, data, true);
	}

	private void setBlock0(Location loc, Material mat, MaterialData data, boolean log) {
		Block block = loc.getBlock();

		if (log) {
			changes.add(block.getState());
		}

		player.sendBlockChange(loc, mat, data.getData());
	}

	@Override
	public void setBlock(Location loc, Material mat) {
		setBlock0(loc, mat, true);
	}

	private void setBlock0(Location loc, Material mat, boolean log) {
		Block block = loc.getBlock();

		if (log) {
			changes.add(block.getState());
		}

		player.sendBlockChange(loc, mat, (byte) 0);
	}

	@Override
	public List<BlockState> getChangedBlocks() {
		Builder<BlockState> builder = ImmutableList.builder();
		return builder.addAll(changes).build();
	}

	@Override
	public void resetChanges() {
		for (BlockState state : changes) {
			setBlock0(state.getLocation(), state.getType(), state.getData(), false);
		}
	}

	@Override
	public void delayResetChanges(long ticks) {
		new BukkitRunnable() {

			@Override
			public void run() {
				resetChanges();
			}

		}.runTaskLater(MineKart.getInstance(), ticks);
	}

}
