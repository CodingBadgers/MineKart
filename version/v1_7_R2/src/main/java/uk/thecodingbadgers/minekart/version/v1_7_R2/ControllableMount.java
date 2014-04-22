package uk.thecodingbadgers.minekart.version.v1_7_R2;

import java.lang.reflect.Constructor;
import java.util.Map;

import net.citizensnpcs.Settings.Setting;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.command.CommandConfigurable;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.Toggleable;
import net.citizensnpcs.util.NMS;
import net.citizensnpcs.util.Util;
import net.minecraft.server.v1_7_R2.EntityEnderDragon;
import net.minecraft.server.v1_7_R2.EntityLiving;
import net.minecraft.server.v1_7_R2.EntityPlayer;
import net.minecraft.server.v1_7_R2.MobEffect;
import net.minecraft.server.v1_7_R2.MobEffectList;

import org.bukkit.craftbukkit.v1_7_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import uk.thecodingbadgers.minekart.jockey.Mount;

import com.google.common.collect.Maps;

public class ControllableMount extends Mount implements Toggleable, CommandConfigurable {

	private MovementController controller = new GroundController();
	@Persist
	private EntityType explicitType;
	private LivingEntity passenger = null;

	public ControllableMount() {
		super();
	}

	public ControllableMount(boolean enabled) {
		super(enabled);
	}
	
	public MovementController getController() {
		return this.controller;
	}

	@Override
	public void configure(CommandContext args) {
		if (args.hasFlag('f')) {
			explicitType = EntityType.BLAZE;
		} else if (args.hasFlag('g')) {
			explicitType = EntityType.OCELOT;
		} else if (args.hasFlag('o')) {
			explicitType = EntityType.UNKNOWN;
		} else if (args.hasFlag('r')) {
			explicitType = null;
		} else if (args.hasValueFlag("explicittype"))
			explicitType = Util.matchEntityType(args.getFlag("explicittype"));
		if (npc.isSpawned())
			loadController();
	}

	private void enterOrLeaveVehicle(Player player) {
		EntityPlayer handle = ((CraftPlayer) player).getHandle();
		if (getHandle().passenger != null) {
			if (getHandle().passenger == handle) {
				player.leaveVehicle();
				this.passenger = null;
			}
			return;
		}
		if (npc.getTrait(Owner.class).isOwnedBy(handle.getBukkitEntity())) {
			handle.setPassengerOf(getHandle());
			this.passenger = player;
		}
	}

	private EntityLiving getHandle() {
		return ((CraftLivingEntity) npc.getEntity()).getHandle();
	}

	@Override
	public void load(DataKey key) throws NPCLoadException {
		if (key.keyExists("explicittype"))
			explicitType = Util.matchEntityType(key.getString("explicittype"));
	}

	private void loadController() {
		EntityType type = npc.getEntity().getType();
		if (explicitType != null)
			type = explicitType;
		Class<? extends MovementController> clazz = controllerTypes.get(type);
		if (clazz == null) {
			controller = new GroundController();
			return;
		}
		Constructor<? extends MovementController> innerConstructor = null;
		try {
			innerConstructor = clazz.getConstructor(ControllableMount.class);
			innerConstructor.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (innerConstructor == null) {
				controller = clazz.newInstance();
			} else
				controller = innerConstructor.newInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
			controller = new GroundController();
		}
	}

	public boolean mount(Player toMount) {
		Entity passenger = npc.getEntity().getPassenger();
		if (passenger != null && passenger != toMount) {
			return false;
		}
		enterOrLeaveVehicle(toMount);
		return true;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!npc.isSpawned() || !isEnabled())
			return;
		EntityPlayer handle = ((CraftPlayer) event.getPlayer()).getHandle();
		Action performed = event.getAction();
		if (!handle.equals(getHandle().passenger))
			return;
		switch (performed) {
			case RIGHT_CLICK_BLOCK:
			case RIGHT_CLICK_AIR:
				controller.rightClick(event);
				break;
			case LEFT_CLICK_BLOCK:
			case LEFT_CLICK_AIR:
				controller.leftClick(event);
				break;
			default:
				break;
		}
	}

	@EventHandler
	public void onRightClick(NPCRightClickEvent event) {
		if (!isEnabled() || !npc.isSpawned() || !event.getNPC().equals(npc))
			return;
		controller.rightClickEntity(event);
	}

	@Override
	public void onSpawn() {
		loadController();
	}

	@Override
	public void run() {

		if (!isEnabled() || !npc.isSpawned())
			return;

		if (getHandle().passenger == null && this.passenger != null) {
			((CraftPlayer) this.passenger).getHandle().setPassengerOf(getHandle());
		}

		if (getHandle().passenger == null)
			return;

		controller.run((Player) getHandle().passenger.getBukkitEntity());
	}

	@Override
	public void save(DataKey key) {
		if (explicitType == null) {
			key.removeKey("explicittype");
		} else {
			key.setString("explicittype", explicitType.name());
		}
	}

	private void setMountedYaw(EntityLiving handle) {
		if (handle instanceof EntityEnderDragon || !Setting.USE_BOAT_CONTROLS.asBoolean())
			return; // EnderDragon handles this separately
		double tX = handle.locX + handle.motX;
		double tZ = handle.locZ + handle.motZ;
		if (handle.locZ > tZ) {
			handle.yaw = (float) -Math.toDegrees(Math.atan((handle.locX - tX) / (handle.locZ - tZ))) + 180F;
		} else if (handle.locZ < tZ) {
			handle.yaw = (float) -Math.toDegrees(Math.atan((handle.locX - tX) / (handle.locZ - tZ)));
		}
		//NMS.setHeadYaw(handle, handle.yaw);
	}

	@Override
	public boolean toggle() {
		setEnabled(!isEnabled());
		if (!isEnabled() && getHandle().passenger != null) {
			getHandle().passenger.getBukkitEntity().leaveVehicle();
		}
		return isEnabled();
	}

	private double updateHorizontralSpeed(EntityLiving handle, double speed, float speedMod) {

		Double maxSpeed = 0.35D;

		MobEffect speedEffect = handle.getEffect(MobEffectList.FASTER_MOVEMENT);
		if (speedEffect != null && speedEffect.getAmplifier() != 0) {
			maxSpeed *= (speedEffect.getAmplifier() * 0.75f);
		}

		MobEffect slownessEffect = handle.getEffect(MobEffectList.SLOWER_MOVEMENT);
		if (slownessEffect != null && slownessEffect.getAmplifier() != 0) {
			maxSpeed /= (slownessEffect.getAmplifier() * 0.75f);
		}

		double oldSpeed = Math.sqrt(handle.motX * handle.motX + handle.motZ * handle.motZ);
		double horizontal = ((EntityLiving) handle.passenger).be;
		if (horizontal > 0.0D) {
			double dXcos = -Math.sin(handle.passenger.yaw * Math.PI / 180.0F);
			double dXsin = Math.cos(handle.passenger.yaw * Math.PI / 180.0F);
			handle.motX += dXcos * speed * 0.5;
			handle.motZ += dXsin * speed * 0.5;
		}
		handle.motX += handle.passenger.motX * speedMod;
		handle.motZ += handle.passenger.motZ * speedMod;

		double newSpeed = Math.sqrt(handle.motX * handle.motX + handle.motZ * handle.motZ);
		if (newSpeed > maxSpeed) {
			double movementFactor = maxSpeed / newSpeed;
			handle.motX *= movementFactor;
			handle.motZ *= movementFactor;
			newSpeed = maxSpeed;
		}

		if (newSpeed > oldSpeed && speed < maxSpeed) {
			return (float) Math.min(maxSpeed, (speed + ((maxSpeed - speed) / maxSpeed)));
		} else {
			return (float) Math.max(0.07D, (speed - ((speed - 0.07D) / maxSpeed)));
		}
	}

	public class GroundController implements MovementController {
		private int jumpTicks = 0;
		private double speed = 0.07D;
		private boolean jumping = false;

		@Override
		public void leftClick(PlayerInteractEvent event) {
		}

		@Override
		public void rightClick(PlayerInteractEvent event) {
		}

		@Override
		public void rightClickEntity(NPCRightClickEvent event) {
			enterOrLeaveVehicle(event.getClicker());
		}
		
		public boolean isJumping() {
			return jumping;
		}

		@Override
		public void run(Player rider) {

			EntityLiving handle = getHandle();
			boolean onGround = handle.onGround;

			NavigatorParameters param = npc.getNavigator().getDefaultParameters();
			float speedMod = param.modifiedSpeed((onGround ? GROUND_SPEED : AIR_SPEED));
			this.speed = updateHorizontralSpeed(handle, this.speed, speedMod);

			boolean shouldJump = NMS.shouldJump(handle.passenger);
			if (shouldJump) {
				if (onGround && jumpTicks == 0) {

					float jumpPotion = 1.0f;
					MobEffect jumpEffect = handle.getEffect(MobEffectList.JUMP);
					if (jumpEffect != null && jumpEffect.getAmplifier() != 0) {
						jumpPotion += jumpEffect.getAmplifier();
					}

					getHandle().motY = JUMP_VELOCITY * jumpPotion;
					jumpTicks = 10;
					jumping = true;
				}
			} else {
				jumpTicks = 0;
			}
			jumpTicks = Math.max(0, jumpTicks - 1);
			if (jumpTicks == 0 && onGround) {
				jumping = false;
			}

			setMountedYaw(handle);
		}

		private static final float AIR_SPEED = 1.5F;
		private static final float GROUND_SPEED = 4F;
		private static final float JUMP_VELOCITY = 0.6F;
	}

	public class LookAirController implements MovementController {
		boolean paused = false;

		@Override
		public void leftClick(PlayerInteractEvent event) {
			paused = !paused;
		}

		@Override
		public void rightClick(PlayerInteractEvent event) {
			paused = !paused;
		}

		@Override
		public void rightClickEntity(NPCRightClickEvent event) {
			enterOrLeaveVehicle(event.getClicker());
		}

		@Override
		public void run(Player rider) {
			if (paused) {
				getHandle().motY = 0.001;
				return;
			}
			Vector dir = rider.getEyeLocation().getDirection();
			dir.multiply(npc.getNavigator().getDefaultParameters().speedModifier());
			EntityLiving handle = getHandle();
			handle.motX = dir.getX();
			handle.motY = dir.getY();
			handle.motZ = dir.getZ();
			setMountedYaw(handle);
		}

		@Override
		public boolean isJumping() {
			return false;
		}
	}

	public class PlayerInputAirController implements MovementController {
		boolean paused = false;
		private double speed;

		@Override
		public void leftClick(PlayerInteractEvent event) {
			paused = !paused;
		}

		@Override
		public void rightClick(PlayerInteractEvent event) {
			getHandle().motY = -0.3F;
		}

		@Override
		public void rightClickEntity(NPCRightClickEvent event) {
			enterOrLeaveVehicle(event.getClicker());
		}

		@Override
		public void run(Player rider) {
			if (paused) {
				getHandle().motY = 0.001;
				return;
			}
			EntityLiving handle = getHandle();
			this.speed = updateHorizontralSpeed(handle, this.speed, 1F);
			boolean shouldJump = NMS.shouldJump(handle.passenger);
			if (shouldJump) {
				handle.motY = 0.3F;
			}
			handle.motY *= 0.98F;
		}

		@Override
		public boolean isJumping() {
			return false;
		}
	}

	private static final Map<EntityType, Class<? extends MovementController>> controllerTypes = Maps.newEnumMap(EntityType.class);

	static {
		controllerTypes.put(EntityType.BAT, PlayerInputAirController.class);
		controllerTypes.put(EntityType.BLAZE, PlayerInputAirController.class);
		controllerTypes.put(EntityType.ENDER_DRAGON, PlayerInputAirController.class);
		controllerTypes.put(EntityType.GHAST, PlayerInputAirController.class);
		controllerTypes.put(EntityType.WITHER, PlayerInputAirController.class);
		controllerTypes.put(EntityType.UNKNOWN, LookAirController.class);
	}
}
