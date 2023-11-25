package party.lemons.statues.statue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class StatueTextureDownloaded extends AbstractTexture
{
	public String					imageUrl;
	public IImageBuffer				effect;
	public BufferedImage			bufferedImage;
	public AbstractTexture			fallbackTexture;
	public ThreadDownloadImageData	data;
	public boolean					textureUploaded;

	static HashMap<String,BufferedImage> playerTextures=new HashMap<String,BufferedImage>();

	public StatueTextureDownloaded(UUID id, ResourceLocation skinReshource, final String url, AbstractTexture fallback, IImageBuffer imageEffect) {
		imageUrl = url;
		fallbackTexture = fallback;
		effect = imageEffect;

		bufferedImage=playerTextures.get(url);
		
		if(bufferedImage!=null) return;

		ResourceLocation origLocation=new ResourceLocation("statues:skins/"+url);
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		data = (ThreadDownloadImageData) texturemanager.getTexture(origLocation);
		if (data == null) {
		
			data = new ThreadDownloadImageData(null, url, DefaultPlayerSkin.getDefaultSkin(id), new IImageBuffer() {
				@Override
				public BufferedImage parseUserSkin(BufferedImage image) {
					bufferedImage=image;
					playerTextures.put(url, image);
					return image;
				}

				@Override
				public void skinAvailable()
				{

				}

			});
			texturemanager.loadTexture(origLocation, data);
		}
	}

	@Override
	public int getGlTextureId() {
		int tex = super.getGlTextureId();

		if (bufferedImage==null)
			return fallbackTexture.getGlTextureId();

		if(! textureUploaded){
			bufferedImage=effect.parseUserSkin(bufferedImage);
			TextureUtil.uploadTextureImage(tex, bufferedImage);
			textureUploaded = true;
		}

		return tex;
	}

	@Override
	public void loadTexture(IResourceManager par1ResourceManager) {
		if (bufferedImage == null) {
			try {
				fallbackTexture.loadTexture(par1ResourceManager);
			} catch(Exception e) { e.printStackTrace(); }
		} else {
			TextureUtil.uploadTextureImage(getGlTextureId(), bufferedImage);
		}
	}
}
