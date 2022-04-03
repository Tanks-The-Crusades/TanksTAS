package tanks.tank;

import tanks.Game;
import tanks.bullet.Bullet;

/**
 * A small, primitive tank which shoots tiny, low damage bullets
 */
public class TankMini extends TankAIControlled
{
	public TankPink tank;
	public boolean previousDestroy = false;
	
	public TankMini(String name, double x, double y, double angle)
	{
		super(name, x, y, Game.tile_size / 2, 255, 127, 127, angle, ShootAI.straight);

		this.enableMovement = true;
		this.maxSpeed = 1.5;
		this.enableMineLaying = false;
		this.enablePredictiveFiring = false;
		this.liveBulletMax = 1;
		this.cooldownRandom = 60;
		this.cooldownBase = 120;
		this.aimTurretSpeed = 0.02;
		this.bullet.bounces = 0;
		this.bullet.effect = Bullet.BulletEffect.trail;
		this.bullet.damage = 0.25;
		this.bullet.size /= 2;
		this.bullet.name = "Mini bullet";
		this.enableLookingAtTargetEnemy = true;
		this.motionChangeChance = 0.001;
		this.enableBulletAvoidance = false;
		this.health = 0.25;
		this.baseHealth = 0.25;
		this.avoidSensitivity = 0.5;
		this.turret.length /= 2;

		this.description = "A small, primitive tank which---shoots tiny, low damage bullets";
	}
	
	public TankMini(String name, double x, double y, double angle, TankPink t)
	{
		this(name, x, y, angle);
		this.tank = t;
		t.spawnedMinis++;
		this.team = t.team;
	}
	
	@Override
	public void update()
	{
		if (this.destroy && !previousDestroy)
		{
			this.previousDestroy = true;
			
			if (tank != null)
				tank.spawnedMinis--;
		}
		
		super.update();	
	}
	
	@Override
	public void postUpdate()
	{
		if (this.tank != null && !seesTargetEnemy)
		{
			if (!this.tank.destroy && Math.sqrt(Math.pow(this.posX - this.tank.posX, 2) + Math.pow(this.posY - this.tank.posY, 2)) > 300)
			{
				this.overrideDirection = true;
				this.setAccelerationInDirection(this.tank.posX, this.tank.posY, this.acceleration);
			}
		}
	}
	
}
