package xt9.deepmoblearning.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xt9.deepmoblearning.DeepConstants;
import xt9.deepmoblearning.DeepMobLearning;
import xt9.deepmoblearning.common.config.Config;
import xt9.deepmoblearning.common.items.ItemDeepLearner;
import xt9.deepmoblearning.common.util.Color;
import xt9.deepmoblearning.common.util.DataModel;
import xt9.deepmoblearning.common.util.PlayerHelper;

import java.text.DecimalFormat;

/**
 * Created by xt9 on 2017-06-14.
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class DataOverlay extends GuiScreen {
    private Minecraft mc;
    private ItemStack deepLearner;
    private NonNullList<ItemStack> dataModels;
    private PlayerHelper playerH;
    private int componentHeight = 26;
    private int barSpacing = 12;

    private static final ResourceLocation experienceBar = new ResourceLocation(DeepConstants.MODID, "textures/gui/experience_gui.png");

    public DataOverlay() {
        super();
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent(priority=EventPriority.NORMAL)
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }

        if(!mc.isGameFocused()) {
            return;
        }

        this.playerH = new PlayerHelper(mc.player);
        if(!playerH.isHoldingDeepLearner()) {
            return;
        } else {
            this.deepLearner = playerH.getHeldDeepLearner();
            this.dataModels = DataModel.getValidFromList(ItemDeepLearner.getContainedItems(deepLearner));
        }


        int x = Config.guiOverlayHorizontalSpacing;
        int y = Config.guiOverlayVerticalSpacing;
        String position = Config.guiOverlaySide;
        switch (position) {
            case "topleft":
                x = x + getLeftCornerX() + 18;
                y = y + 5;
                break;
            case "topright":
                x = x + getRightCornerX();
                y = y + 5;
                break;
            case "bottomleft":
                x = x + getLeftCornerX() + 18;
                y = y + getBottomY(dataModels.size()) - 5;
                break;
            case "bottomright":
                x = x + getRightCornerX();
                y = y + getBottomY(dataModels.size()) - 5;
                break;
            default:
                x = x + getLeftCornerX() + 18;
                y = y + 5;
                break;
        }

        for (int i = 0; i < dataModels.size(); i++) {
            ItemStack stack = dataModels.get(i);
            String tierName = DataModel.getTierName(stack, false);
            int tier = DataModel.getTier(stack);
            double k = DataModel.getKillsToNextTier(stack);
            double c = DataModel.getCurrentTierKillCountWithSims(stack);
            int roof = DataModel.getTierRoofAsKills(stack);
            drawExperienceBar(x, y, i, tierName, tier, k, c, roof, stack);
        }
    }

    private void drawExperienceBar(int x, int y, int index, String tierName, int tier, double killsToNextTier, double currenKillCount, int tierRoof, ItemStack stack) {
        DecimalFormat f = new DecimalFormat("0.#");

        drawItemStack(x - 18, y - 2 + barSpacing + (index * componentHeight), stack);
        drawString(mc.fontRenderer, tierName + " Model", x - 14, y + (index * componentHeight) + 2, Color.WHITE);

        // Draw the bar
        mc.getTextureManager().bindTexture(experienceBar);
        drawTexturedModalRect(x, y + barSpacing + (index * componentHeight), 0, 0, 89, 12);

        if(tier == DeepConstants.DATA_MODEL_MAXIMUM_TIER) {
            drawTexturedModalRect(x + 1,  y + 1 + barSpacing + (index * componentHeight), 0, 12, 89, 11);
        } else {
            drawTexturedModalRect(x + 1,  y + 1 + barSpacing + (index * componentHeight), 0, 12,
                    (int) (((float) currenKillCount / tierRoof * 89)), 11);
            drawString(mc.fontRenderer, f.format(killsToNextTier) + " to go", x + 3, y + 2 + barSpacing + (index * componentHeight), Color.WHITE);
        }
    }

    private int getLeftCornerX() {
        return 5;
    }

    private int getRightCornerX() {
        return Minecraft.getInstance().mainWindow.getScaledWidth() - width - 5;
    }

    private int getBottomY(int numberOfBars) {
        return Minecraft.getInstance().mainWindow.getScaledHeight() - (numberOfBars * componentHeight);
    }

    private void drawItemStack(int x, int y, ItemStack stack) {
        GlStateManager.translatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        DeepMobLearning.proxy.getClientItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
        this.zLevel = 0.0F;
    }
}
