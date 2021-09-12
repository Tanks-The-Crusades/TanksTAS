package tanks.gui.screen;

import tanks.*;
import tanks.obstacle.Obstacle;

public abstract class Screen
{
	public String music = null;
	public String musicID = null;

	public String screenHint = "";
	public boolean showDefaultMouse = true;

	public boolean enableMargins = true;
	public boolean drawDarkness = true;

	public double textSize = Drawing.drawing.textSize;
	public double titleSize = Drawing.drawing.titleSize;
	public double objWidth = Drawing.drawing.objWidth;
	public double objHeight = Drawing.drawing.objHeight;
	public double objXSpace = Drawing.drawing.objXSpace;
	public double objYSpace = Drawing.drawing.objYSpace;

	public double centerX = Drawing.drawing.interfaceSizeX / 2;
	public double centerY = Drawing.drawing.interfaceSizeY / 2;

	public Screen()
	{
		this.setupLayoutParameters();
	}

	public Screen(double objWidth, double objHeight, double objXSpace, double objYSpace)
	{
		//Game.game.window.setCursorLocked(false);

		this.objWidth = objWidth;
		this.objHeight = objHeight;
		this.objXSpace = objXSpace;
		this.objYSpace = objYSpace;

		this.textSize = this.objHeight * 0.6;
		this.titleSize = this.textSize * 1.25;

		this.setupLayoutParameters();
	}

	public abstract void update();

	public abstract void draw();

	public void drawPostMouse()
	{

	}

	public void drawDefaultBackground()
	{
		this.drawDefaultBackground(1);
	}

	public void drawDefaultBackground(double size)
	{
		if (!(Game.screen instanceof IDarkScreen))
			Panel.darkness = Math.max(Panel.darkness - Panel.frameFrequency * 3, 0);

		for (int i = 0; i < Game.currentSizeX; i++)
		{
			for (int j = 0; j < Game.currentSizeY; j++)
			{
				if (Game.game.heightGrid[i][j] <= -1000)
					Game.game.heightGrid[i][j] = 0;
			}
		}

		double frac = 0;

		if (Game.screen instanceof ScreenGame || Game.screen instanceof ILevelPreviewScreen || (Game.screen instanceof IOverlayScreen
				&& !(Game.screen instanceof IConditionalOverlayScreen && !((IConditionalOverlayScreen) Game.screen).isOverlayEnabled())))
			frac = Obstacle.draw_size / Game.tile_size;

		if (!(Game.screen instanceof ScreenExit) && size >= 1)
		{
			Drawing.drawing.setColor(174 * frac + (1 - frac) * Level.currentColorR, 92 * frac + (1 - frac) * Level.currentColorG,16 * frac + (1 - frac) * Level.currentColorB);

			double mul = 1;
			if (Game.angledView)
				mul = 2;

			Drawing.drawing.fillShadedInterfaceRect(Drawing.drawing.interfaceSizeX / 2, Drawing.drawing.interfaceSizeY / 2,
					mul * Game.game.window.absoluteWidth / Drawing.drawing.interfaceScale, mul * Game.game.window.absoluteHeight / Drawing.drawing.interfaceScale);

			Drawing.drawing.setColor(Level.currentColorR, Level.currentColorG, Level.currentColorB, 255.0 * size);
			Drawing.drawing.fillBackgroundRect(Drawing.drawing.sizeX / 2, Drawing.drawing.sizeY / 2, Drawing.drawing.sizeX, Drawing.drawing.sizeY);
		}

		if (Game.fancyTerrain)
		{
			Drawing.drawing.setColor(Level.currentColorR, Level.currentColorG, Level.currentColorB);

			if (Game.game.window.shapeRenderer.supportsBatching)
				Game.game.window.setBatchMode(true, true, true);

			if (Game.currentSizeX != Game.tilesR.length || Game.currentSizeY != Game.tilesR[0].length)
				Game.resetTiles();

			int width = (int) (Game.game.window.absoluteWidth / Drawing.drawing.unzoomedScale / Game.tile_size);
			int height = (int) ((Game.game.window.absoluteHeight - Drawing.drawing.statsHeight) / Drawing.drawing.unzoomedScale / Game.tile_size);

			for (int i1 = (Game.currentSizeX - width) / 2 - 1; i1 < width + 1; i1++)
			{
				int i = i1;
				while (i < 0)
					i += Game.currentSizeX;

				i = i % Game.currentSizeX;

				for (int j1 = (Game.currentSizeY - height) / 2 - 1; j1 < height + 1; j1++)
				{
					boolean inBounds = true;

					int j = j1;
					while (j < 0)
						j += Game.currentSizeY;

					j = j % Game.currentSizeY;

					double frac2 = 0;
					if (i1 >= 0 && i1 < Game.currentSizeX && j1 >= 0 && j1 < Game.currentSizeY)
						Drawing.drawing.setColor(Game.tilesR[i][j], Game.tilesG[i][j], Game.tilesB[i][j]);
					else
					{
						inBounds = false;
						frac2 = frac;

						if (frac == 1)
							continue;

						Drawing.drawing.setColor(174 * frac + (1 - frac) * Game.tilesR[i][j], 92 * frac + (1 - frac) * Game.tilesG[i][j],16 * frac + (1 - frac) * Game.tilesB[i][j]);
					}

					if (Game.enable3d)
					{
						double z1 = 0;

						byte o = 61;

						if (Game.enable3dBg && Drawing.drawing.scale > 0.25 * Drawing.drawing.interfaceScale)
						{
							z1 = Game.tilesDepth[i][j];
							o = 1;
						}

						if (Game.tileDrawables[i][j] != null && inBounds)
						{
							Game.tileDrawables[i][j].drawTile(Game.tilesR[i][j], Game.tilesG[i][j], Game.tilesB[i][j], z1);

							if (!Game.game.window.drawingShadow)
								Game.tileDrawables[i][j] = null;
						}
						else
						{
							double extra = 0;

							if (i > 0)
								extra = Math.max(extra, -Game.game.heightGrid[i - 1][j]);

							if (j > 0)
								extra = Math.max(extra, -Game.game.heightGrid[i][j - 1]);

							if (i < Game.currentSizeX - 1)
								extra = Math.max(extra, -Game.game.heightGrid[i + 1][j]);

							if (j < Game.currentSizeY - 1)
								extra = Math.max(extra, -Game.game.heightGrid[i][j + 1]);

							if (extra != 0)
								o = 1;

							if (size != 1)
								Drawing.drawing.fillBox(
										(i1 + 0.5) / Game.bgResMultiplier * Game.tile_size,
										(j1 + 0.5) / Game.bgResMultiplier * Game.tile_size,
										Math.max(0, 2000 - size * 2000 * (1 + Game.tilesDepth[i][j] / 10)) - Game.tile_size + z1,
										Game.tile_size / Game.bgResMultiplier,
										Game.tile_size / Game.bgResMultiplier,
										Game.tile_size);
							else
							{
								Drawing.drawing.fillBox(
										(i1 + 0.5) / Game.bgResMultiplier * Game.tile_size,
										(j1 + 0.5) / Game.bgResMultiplier * Game.tile_size,
										-extra,
										Game.tile_size / Game.bgResMultiplier,
										Game.tile_size / Game.bgResMultiplier,
										extra + z1 * (1 - frac2), o);
							}
						}
					}
					else
						Drawing.drawing.fillRect(
								(i1 + 0.5) / Game.bgResMultiplier * Game.tile_size,
								(j1 + 0.5) / Game.bgResMultiplier * Game.tile_size,
								Game.tile_size * size / Game.bgResMultiplier,
								Game.tile_size * size / Game.bgResMultiplier);
				}
			}

			if (Game.game.window.shapeRenderer.supportsBatching)
				Game.game.window.setBatchMode(false, true, true);
		}
		else if (Game.enable3d && !(Game.screen instanceof ScreenExit))
		{
			for (int i = 0; i < Game.currentSizeX; i++)
			{
				for (int j = 0; j < Game.currentSizeY; j++)
				{				
					Drawing.drawing.setColor(Level.currentColorR, Level.currentColorG, Level.currentColorB);

					if (Game.tileDrawables[i][j] != null)
					{
						Game.tileDrawables[i][j].drawTile(Level.currentColorR, Level.currentColorG, Level.currentColorB, 0);

						if (!Game.game.window.drawingShadow)
							Game.tileDrawables[i][j] = null;
					}
					else
					{
						Drawing.drawing.fillBox(
								(i + 0.5) / Game.bgResMultiplier * Game.tile_size,
								(j + 0.5) / Game.bgResMultiplier * Game.tile_size, 0,
								Game.tile_size * size / Game.bgResMultiplier,
								Game.tile_size * size / Game.bgResMultiplier, 0, (byte) 61);
					}
				}
			}
		}

		if (this.drawDarkness)
		{
			Drawing.drawing.setColor(0, 0, 0, Math.max(0, Panel.darkness));
			Game.game.window.shapeRenderer.fillRect(0, 0, Game.game.window.absoluteWidth, Game.game.window.absoluteHeight - Drawing.drawing.statsHeight);
		}
	}

	public double getOffsetX()
	{
		return 0;
	}

	public double getOffsetY()
	{
		return 0;
	}

	public double getScale()
	{
		return Drawing.drawing.unzoomedScale;
	}

	public void setupLayoutParameters()
	{

	}
}
