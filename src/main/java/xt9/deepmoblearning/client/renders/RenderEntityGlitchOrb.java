package xt9.deepmoblearning.client.renders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xt9.deepmoblearning.common.Registry;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by xt9 on 2018-04-18.
 */
public class RenderEntityGlitchOrb<T extends Entity> extends Render<T> {
    private Random rand;
    private Minecraft minecraft;

    public RenderEntityGlitchOrb(RenderManager manager) {
        super(manager);
        minecraft = Minecraft.getInstance();
        rand = new Random();
    }

    @SuppressWarnings("NullableProblems")
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        renderProjectile(x, y, z, 4.0F, new ItemStack(Registry.glitchHeart));

        super.doRender(entity, x * 1.0F, y * 1.0F, z * 1.0F, entityYaw, partialTicks);
    }

    public void renderProjectile(double x, double y, double z, float scale, ItemStack renderStack) {
        GlStateManager.pushMatrix();

        GlStateManager.translated((float) x, (float) y, (float) z);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float) (renderManager.options.thirdPersonView == 2 ? -1 : 1) * renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(180.0F, 0.0F, 3.0F, 0.0F);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        RenderHelper.enableGUIStandardItemLighting();
        minecraft.getItemRenderer().renderItem(renderStack, ItemCameraTransforms.TransformType.GROUND);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
