package tanks.gui.screen;

import tanks.Drawing;
import tanks.Game;
import tanks.gui.InputSelector;

public class ScreenControlsTAS extends Screen {
    InputSelector openUI = new InputSelector(Drawing.drawing.interfaceSizeX * 2 / 3, Drawing.drawing.interfaceSizeY / 2 - 270, 700, 40, "Open TAS Menu", Game.game.input.tasUI);
    InputSelector lagSpike = new InputSelector(Drawing.drawing.interfaceSizeX * 2 / 3, Drawing.drawing.interfaceSizeY / 2 - 180, 700, 40, "Lag Spike", Game.game.input.tasLagSpike);

    public ScreenControlsTAS() {
        this.music = "menu_options.ogg";
        this.musicID = "menu";
    }

    @Override
    public void update() {
        openUI.update();
        lagSpike.update();

        ScreenOptionsInputDesktop.overlay.update();
    }

    @Override
    public void draw() {
        this.drawDefaultBackground();

        openUI.draw();
        lagSpike.draw();

        Drawing.drawing.setInterfaceFontSize(this.titleSize);
        Drawing.drawing.setColor(0, 0, 0);
        Drawing.drawing.drawInterfaceText(Drawing.drawing.interfaceSizeX * 2 / 3, Drawing.drawing.interfaceSizeY / 2 - 350, "TAS controls");

        ScreenOptionsInputDesktop.overlay.draw();
    }
}
