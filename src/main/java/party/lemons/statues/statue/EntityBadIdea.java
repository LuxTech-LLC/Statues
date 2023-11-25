package party.lemons.statues.statue;

import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.CooldownTracker;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityBadIdea extends EntityLivingBase
{
	public float renderOffsetX;
	@SideOnly(Side.CLIENT)
	public float renderOffsetY;
	public float renderOffsetZ;

	protected CooldownTracker createCooldownTracker()
	{
		return new CooldownTracker();
	}

	public EntityBadIdea(World world)
	{
		super(world);

		this.setDead();
	}

	public EntityBadIdea(World worldIn, GameProfile gameProfileIn)
	{
		super(worldIn);
		this.setUniqueId(getUUID(gameProfileIn));
		this.setDead();

	}
	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{

	}

	/*
	 * Gets a players UUID given their GameProfie
	 */
	public static UUID getUUID(GameProfile profile)
	{
		UUID uuid = profile.getId();

		if (uuid == null)
		{
			uuid = getOfflineUUID(profile.getName());
		}

		return uuid;
	}

	public static UUID getOfflineUUID(String username)
	{
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
	}
}