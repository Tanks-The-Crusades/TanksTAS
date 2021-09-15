package tanks;

import tanks.gui.screen.ScreenInterlevel;
import tanks.gui.screen.ScreenParty;
import tanks.gui.screen.ScreenPartyInterlevel;

public class TASTools {
    public static final String version = "v1.0";
    public static String topLeftText = "TAS " + TASTools.version + " @ 100% Speed";
    public static String lagNoticeText = "Caused Lag! (x1)";
    public static String mouseText = "TAS " + TASTools.version;

    protected static double gameSpeed = 1;

    public static double lagTime = 1;
    public static double lagNotice = 0;

    public static double getGameSpeed() {
        return TASTools.gameSpeed;
    }

    public static void setGameSpeed(double gameSpeed) {
        TASTools.gameSpeed = gameSpeed;
        TASTools.topLeftText = "TAS " + TASTools.version + " @ " + TASTools.gameSpeed * 100 + "% Speed";
    }

    public static void updateLagTime() {
        TASTools.lagNoticeText = "Caused Lag! (x" + TASTools.lagTime + ")";
    }

    public static void draw_notice() {
        int color = 0;
        if (Level.isDark() || ((Game.screen instanceof ScreenInterlevel || Game.screen instanceof ScreenPartyInterlevel) && Panel.win && Game.effectsEnabled))
            color = 255;
        Drawing.drawing.setColor(color, color, color);
        Drawing.drawing.setInterfaceFontSize(Drawing.drawing.titleSize);
        Drawing.drawing.drawUncenteredInterfaceText(10, 10, TASTools.topLeftText);
        Drawing.drawing.setColor(color, color, color, 51 * lagNotice);
        if (Panel.lagFrame)
            lagNotice -= 0.03 * Panel.frameFrequency / TASTools.lagTime;
        else
            lagNotice -= 0.03 * Panel.frameFrequency;
        Drawing.drawing.drawUncenteredInterfaceText(10, 50, lagNoticeText);
    }

    public static void draw_mouse_notice(double x, double y) {
        Drawing.drawing.setInterfaceFontSize(Drawing.drawing.textSize * 0.5);
        Drawing.drawing.drawInterfaceText(x, y + 20, TASTools.mouseText);
    }
}
