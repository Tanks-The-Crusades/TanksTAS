package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Crusade;
import tanks.Game;
import tanks.Panel;
import tanks.gui.screen.ScreenPartyCrusadeInterlevel;
import tanks.gui.screen.ScreenPartyInterlevel;
import tanks.gui.screen.ScreenPartyLobby;
import tanks.network.NetworkUtils;

public class EventReturnToCrusade extends PersonalEvent
{
	public String msg1;
	public String msg2;

	public EventReturnToCrusade()
	{

	}

	public EventReturnToCrusade(Crusade c)
	{
		if (c.win)
			msg1 = "You finished the crusade!";
		else if (c.lose)
			msg1 = "Game over!";
		else
		{
			if (Panel.levelPassed)
				msg1 = "Battle cleared!";
			else
				msg1 = "Battle failed!";
		}

		if (c.lifeGained)
			msg2 = "You gained a life for clearing Battle " + (c.currentLevel + 1) + "!";
		else
			msg2 = "";
	}

	@Override
	public void execute() 
	{
		if (this.clientID == null)
		{
			ScreenPartyCrusadeInterlevel s = new ScreenPartyCrusadeInterlevel();
			s.msg1 = this.msg1;
			s.msg2 = this.msg2;
			Game.screen = s;

			System.gc();
		}
	}

	@Override
	public void write(ByteBuf b) 
	{
		NetworkUtils.writeString(b, msg1);
		NetworkUtils.writeString(b, msg2);
	}

	@Override
	public void read(ByteBuf b) 
	{
		msg1 = NetworkUtils.readString(b);
		msg2 = NetworkUtils.readString(b);
	}
}
