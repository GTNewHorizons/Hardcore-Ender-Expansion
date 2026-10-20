package chylex.hee.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.dashboard.api.HudBounds;
import com.gtnewhorizons.dashboard.api.HudElement;
import com.gtnewhorizons.dashboard.api.HudRegistry;
import com.gtnewhorizons.dashboard.api.HudShift;

import chylex.hee.mechanics.energy.EnergyClusterHealth;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Lets players move, resize and hide the notes and the energy cluster info in Dashboard's editor. The only class using
 * Dashboard, so only call it while Dashboard is loaded. Both are drawn even when hidden, just invisibly, so the notes
 * keep fading and the cluster info keeps being cleared.
 */
@SideOnly(Side.CLIENT)
final class DashboardHud {

    /** In the small font, before the note scale. */
    private static final int NOTE_WIDTH = 120;
    private static final int TEXT_HEIGHT = 8;
    private static final int NOTE_START = 10;
    private static final int NOTE_FLOAT = 13;
    private static final int NOTE_MARGIN = 3;
    private static final int SAMPLE_NOTE_FLOAT = 6;
    private static final String SAMPLE_NOTE = "Found a new location";

    private static final int CLUSTER_WIDTH = 140;
    private static final int CLUSTER_TOP = 44;
    private static final int CLUSTER_HEIGHT = 38;
    private static final float SAMPLE_ENERGY = 12.5F;
    private static final float SAMPLE_MAX_ENERGY = 25F;

    private static final HudElement NOTIFICATIONS = new HudElement(
            "hardcoreenderexpansion:notifications",
            DashboardHud::getNotificationBounds);
    private static final HudElement ENERGY_CLUSTER = new HudElement(
            "hardcoreenderexpansion:energy_cluster",
            (screenWidth, screenHeight) -> new HudBounds(
                    screenWidth / 2 - CLUSTER_WIDTH / 2,
                    screenHeight / 2 - CLUSTER_TOP,
                    CLUSTER_WIDTH,
                    CLUSTER_HEIGHT));

    private static final HudShift SHIFT = new HudShift();

    private DashboardHud() {}

    static void register() {
        NOTIFICATIONS.setPreview(
                (screenWidth, screenHeight) -> HudRegistry
                        .render(NOTIFICATIONS, () -> drawNoteSample(screenWidth, screenHeight)));
        ENERGY_CLUSTER.setPreview(
                (screenWidth, screenHeight) -> HudRegistry.render(
                        ENERGY_CLUSTER,
                        () -> OverlayManager.drawClusterInfo(
                                screenWidth >> 1,
                                screenHeight >> 1,
                                SAMPLE_ENERGY,
                                SAMPLE_MAX_ENERGY,
                                EnergyClusterHealth.HEALTHY)));
        HudRegistry.register(NOTIFICATIONS);
        HudRegistry.register(ENERGY_CLUSTER);
    }

    static void beginNotifications() {
        SHIFT.beginOrHide(NOTIFICATIONS);
    }

    static void beginEnergyCluster() {
        SHIFT.beginOrHide(ENERGY_CLUSTER);
    }

    static void end() {
        SHIFT.end();
    }

    private static double getNoteScale() {
        return OverlayManager.getNoteScale(HudRegistry.getResolution().getScaleFactor());
    }

    /** Right aligned in the bottom right, as high as a note floats. */
    private static HudBounds getNotificationBounds(int screenWidth, int screenHeight) {
        double scale = getNoteScale();
        int width = (int) Math.ceil(NOTE_WIDTH * scale);
        int top = screenHeight - (int) Math.ceil((NOTE_START + NOTE_FLOAT) * scale);
        int bottom = screenHeight - (int) Math.floor((NOTE_START - TEXT_HEIGHT) * scale);
        return new HudBounds(screenWidth - (int) Math.ceil(NOTE_MARGIN * scale) - width, top, width, bottom - top);
    }

    /** A note halfway up, placed like the real ones. */
    private static void drawNoteSample(int screenWidth, int screenHeight) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        boolean prevUnicode = font.getUnicodeFlag();
        font.setUnicodeFlag(true);
        double scale = getNoteScale();
        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, scale);
        font.drawStringWithShadow(
                SAMPLE_NOTE,
                (int) (screenWidth / scale) - font.getStringWidth(SAMPLE_NOTE) - NOTE_MARGIN,
                (int) (screenHeight / scale) - NOTE_START - SAMPLE_NOTE_FLOAT,
                0xFFFFFFFF);
        GL11.glPopMatrix();
        font.setUnicodeFlag(prevUnicode);
    }
}
