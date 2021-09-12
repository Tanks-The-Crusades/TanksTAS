package tanks;

import basewindow.IWindowHandler;
import tanks.gui.screen.ScreenOptions;
import tanks.gui.screen.ScreenParty;
import tanks.gui.screen.ScreenPartyHost;
import tanks.gui.screen.ScreenPartyLobby;

public class GameWindowHandler implements IWindowHandler
{
	@Override
	public void onWindowClose() 
	{
		if (Game.steamNetworkHandler.initialized)
			Game.steamNetworkHandler.exit();

		ScreenOptions.saveOptions(Game.homedir);

		if (ScreenPartyHost.isServer)
		{
			ScreenPartyHost.server.close("The party host has closed their game");
		}

		try
		{
			if (Crusade.currentCrusade != null && !ScreenPartyHost.isServer && !ScreenPartyLobby.isClient)
			{
				Crusade.currentCrusade.crusadePlayers.get(Game.player).saveCrusade();
			}
		}
		catch (Exception e)
		{
			Game.exitToCrash(e);
		}
	}

}
