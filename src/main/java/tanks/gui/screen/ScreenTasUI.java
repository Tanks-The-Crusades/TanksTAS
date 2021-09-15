package tanks.gui.screen;

import basewindow.InputCodes;
import tanks.Drawing;
import tanks.Game;
import tanks.TASTools;
import tanks.gui.Button;
import tanks.gui.TextBoxSlider;

public class ScreenTasUI extends Screen {
    public Screen originalScreen;

    public static String title = "TAS Menu (" + TASTools.version + ")";

    TextBoxSlider gameSpeed = new TextBoxSlider(this.centerX, this.centerY - this.objYSpace * 4, this.objWidth, this.objHeight, "Game Speed", new Runnable() {
        @Override
        public void run() {
            if (gameSpeed.inputText.length() <= 0)
                gameSpeed.inputText = gameSpeed.previousInputText;

            TASTools.setGameSpeed(Double.parseDouble(gameSpeed.inputText));
        }
    }, 1D, 0D, 25D, 0.01D);

    TextBoxSlider lagAmount = new TextBoxSlider(this.centerX, this.centerY - this.objYSpace * 2.5, this.objWidth, this.objHeight, "Lag Amount", new Runnable() {
        @Override
        public void run() {
            if (lagAmount.inputText.length() <= 0)
                lagAmount.inputText = lagAmount.previousInputText;

            TASTools.lagTime = Double.parseDouble(lagAmount.inputText);
            TASTools.updateLagTime();
        }
    }, 1D, 0.01D, 1000D, 0.25D);

    Button close = new Button(Drawing.drawing.interfaceSizeX / 2, Drawing.drawing.interfaceSizeY / 2 + 210, this.objWidth, this.objHeight, "Close TAS Menu", new Runnable() {
        @Override
        public void run() {
            Game.screen = ((ScreenTasUI) Game.screen).originalScreen;
        }
    });

    public ScreenTasUI(Screen oldscreen) {
        super();
        this.originalScreen = oldscreen;

        this.gameSpeed.integer = false; // Tell the slider that this isn't only integers
        this.gameSpeed.allowDoubles = true; // Allow entering decimal points in the text box
        this.gameSpeed.inputText = Double.toString(TASTools.getGameSpeed()); // Set the textbox initial value to current game speed
        this.gameSpeed.previousInputText = this.gameSpeed.inputText; // Make sure the previous one is set correctly as wel
        this.gameSpeed.submit(); // Update the slider position based on the text

        this.lagAmount.integer = false;
        this.lagAmount.allowDoubles = true;
        this.lagAmount.inputText = Double.toString(TASTools.lagTime);
        this.lagAmount.previousInputText = this.lagAmount.inputText;
        this.lagAmount.submit();
    }

    @Override
    public void update() {
        if (Game.game.window.validPressedKeys.contains(InputCodes.KEY_ESCAPE)) {
            Game.game.window.validPressedKeys.remove(Game.game.window.validPressedKeys.indexOf(InputCodes.KEY_ESCAPE));
            Game.screen = ((ScreenTasUI) Game.screen).originalScreen;
            return;
        }

        gameSpeed.update();
        lagAmount.update();
        close.update();
    }

    @Override
    public void draw() {
        originalScreen.draw(); // No update on purpose

        Drawing.drawing.setColor(127, 127, 127, 127);
        Game.game.window.shapeRenderer.fillRect(0, 0, Game.game.window.absoluteWidth + 1, Game.game.window.absoluteHeight + 1);
        Drawing.drawing.setColor(0, 0, 0);
        Drawing.drawing.setInterfaceFontSize(this.titleSize * 2.5);
        Drawing.drawing.drawInterfaceText(this.centerX, this.centerY - this.objYSpace * 6, "TAS Menu");

        gameSpeed.draw();
        lagAmount.draw();
        close.draw();
    }
}
