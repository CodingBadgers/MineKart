package uk.thecodingbadgers.minekart.jockey;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;

public abstract class Mount extends Trait {

	private boolean enabled;

	protected Mount() {
		this(true);
	}

	protected Mount(boolean enabled) {
		super("controllablemount");
		
		this.enabled = enabled;
	}
	public final boolean setEnabled(boolean enabled) {
		return this.enabled = enabled;
	}

	public final boolean isEnabled() {
		return this.enabled;
	}
	
	public abstract boolean mount(Player player);

	public abstract MovementController getController();

	public static interface MovementController {
		void leftClick(PlayerInteractEvent event);

		void rightClick(PlayerInteractEvent event);

		void rightClickEntity(NPCRightClickEvent event);

		void run(Player rider);
		
		boolean isJumping();
	}

}
