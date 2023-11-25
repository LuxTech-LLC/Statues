package party.lemons.statues.statue;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import party.lemons.statues.Statues;
import party.lemons.statues.block.BlockStatue;
import party.lemons.statues.block.entity.TileEntityStatue;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class EntityStatuePlayer extends EntityBadIdea
{
	static ResourceLocation STEVE = new ResourceLocation("textures/entity/steve.png");
	static ResourceLocation ALEX = new ResourceLocation("textures/entity/alex.png");

	private ResourceLocation skin;

	@SideOnly(Side.CLIENT)
	private ITextureObject dataSkin;
	public StatueInfo info;

	public EntityStatuePlayer(World world, String name, BlockPos pos)
	{
		super(world, new GameProfile(UUID.fromString("a9cb469c-f43d-4925-946d-c85a90e58a15"), name));

		this.setPosition(pos.getX(), pos.getY(), pos.getZ());
		this.setDead();
	}


	public TileEntityStatue getTE()
	{
		BlockPos ch = getPosition();
		if(ch.equals(BlockPos.ORIGIN))
			return null;

		if(!world.getBlockState(ch).getValue(BlockStatue.MAIN_PART))
			ch = ch.down();

		TileEntity statue = world.getTileEntity(ch);
		if(statue != null && statue instanceof TileEntityStatue)
		{
			return (TileEntityStatue) statue;
		}

		return null;
	}


	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
	{
		if (slotIn == EntityEquipmentSlot.MAINHAND)
		{
			return this.getHeldItemMainhand();
		}
		else if (slotIn == EntityEquipmentSlot.OFFHAND)
		{
			return this.getHeldItemOffhand();
		}
		else
		{
			TileEntityStatue statue = getTE();
			if(statue != null)
			{
				return statue.inventory.getStackInSlot(slotIn.getIndex());
			}

			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
	{

	}

	@Override
	public EnumHandSide getPrimaryHand()
	{
		return null;
	}


	public ItemStack getHeldItemMainhand()
	{
		TileEntity statue = getTE();
		if(statue != null)
		{
			return ((TileEntityStatue) statue).inventory.getStackInSlot(4);
		}

		return ItemStack.EMPTY;
	}

	public ItemStack getHeldItemOffhand()
	{
		TileEntity statue = getTE();
		if(statue != null)
		{
			return ((TileEntityStatue) statue).inventory.getStackInSlot(5);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList()
	{
		return null;
	}

	public EntityStatuePlayer(World world)
	{
		this(world, "Steve", new BlockPos(0,0,0));
	}

	@Override
	public boolean canUseCommand(int permLevel, String commandName)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void applySkin(String name, IBlockState state, byte facing)
	{
		
		if(!world.isRemote)
			return;

		if(state == null)
			state = Blocks.STONE.getDefaultState();

		ResourceLocation steveSkin = new ResourceLocation("textures/entity/steve.png|B" + state + "," + facing);
		AbstractTexture steveDataSkin = getDataForSteve(steveSkin, new ResourceLocation("textures/entity/steve.png"), state, facing);

		if(!name.isEmpty())
		{
			skin = new ResourceLocation("skins/" + StringUtils.stripControlCodes(name) + state + "," + facing);
			dataSkin = getTextureForSkin(skin, steveDataSkin, name, state, facing);
		}
		else
		{
			skin = steveSkin;
			dataSkin = steveDataSkin;
		}
	}

	@SideOnly(Side.CLIENT)
	public ITextureObject getTextureForSkin(ResourceLocation skin, AbstractTexture fallbackSkin, String name, IBlockState state, byte facing)
	{
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		AbstractTexture tex = (AbstractTexture) texturemanager.getTexture(skin);

		if (tex == null) {
			
			UUID uuid = null;
			
			Unirest.setTimeouts(0, 0);
			String response = null;
			try {
				response = Unirest.get("https://api.mojang.com/users/profiles/minecraft/"+name).header("Accept", "application/json").asString().getBody();
			} catch (UnirestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			JSONObject responseJson = new JSONObject(response);
			try{
				responseJson.get("id");
				response = responseJson.get("id").toString();
			}
			catch(JSONException e){
				response = "501d23e64f43389aa06ae1cc7e48ba1c";
			}
			
			
			response = new StringBuilder(response)
	        .insert(20, '-')
	        .insert(16, '-')
	        .insert(12, '-')
	        .insert(8, '-')
	        .toString();
					
				uuid = UUID.fromString(response);
			
			tex = new StatueTextureDownloaded(uuid, skin, Statues.skinServerLocation + uuid + ".png", fallbackSkin, new ImageStatueBufferDownload(this, state, facing, name+"."+state));
			texturemanager.loadTexture(skin, tex);
		}

		return tex;
	}

	@SideOnly(Side.CLIENT)
	public AbstractTexture getDataForSteve(ResourceLocation skin, ResourceLocation base, IBlockState state, byte facing) {
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		AbstractTexture tex = (AbstractTexture) texturemanager.getTexture(skin);

		if (tex == null) {
			tex = new StatueTextureStatic(base, new ImageStatueBufferDownload(this, state, facing,"steve."+state));
			texturemanager.loadTexture(skin, tex);
		}

		return tex;
	}

	@SideOnly(Side.CLIENT)
	public ITextureObject getTextureSkin() {
		return dataSkin;
	}

	public ResourceLocation getLocationSkin() {
		return skin;
	}
}
