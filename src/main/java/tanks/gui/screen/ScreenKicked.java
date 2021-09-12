package tanks.gui.screen;

import tanks.Drawing;
import tanks.Game;
import tanks.Panel;
import tanks.gui.Button;

public class ScreenKicked extends Screen
{
	public String reason;
	
	public ScreenKicked(String reason)
	{
		this.music = "menu_1.ogg";
		this.musicID = "menu";

		Drawing.drawing.playSound("leave.ogg");

		Panel.forceRefreshMusic = true;

		this.reason = reason;
		ScreenPartyLobby.connections.clear();
	}
		
	Button back = new Button(this.centerX, this.centerY + this.objYSpace, this.objWidth, this.objHeight, "Ok", new Runnable()
	{
		@Override
		public void run() 
		{
			Game.screen = Game.lastOfflineScreen;
		}
	}
	);

	@Override
	public void update() 
	{
		back.update();
	}

	@Override
	public void draw() 
	{
		this.drawDefaultBackground();
		Drawing.drawing.setColor(0, 0, 0);
		Drawing.drawing.setInterfaceFontSize(this.textSize);
		Drawing.drawing.drawInterfaceText(this.centerX, this.centerY - this.objYSpace, this.reason);
	
		back.draw();
	}
	
}
