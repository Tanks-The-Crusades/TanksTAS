package tanks.tank;

import tanks.*;
import tanks.bullet.Bullet;
import tanks.bullet.BulletBoost;
import tanks.bullet.BulletHealing;
import tanks.event.EventLayMine;
import tanks.event.EventShootBullet;
import tanks.event.EventTankUpdateColor;

/**
 * A tank which speeds up its allies and becomes explosive as a last stand
 */
public class TankGold extends TankAIControlled
{
	boolean suicidal = false;
	double timeUntilDeath = 500 + this.random.nextDouble() * 250;

	public TankGold(String name, double x, double y, double angle)
	{
		super(name, x, y, Game.tile_size, 255, 180, 0, angle, ShootAI.straight);

		this.enableMineLaying = false;
		this.enablePredictiveFiring = true;
		this.enableDefensiveFiring = true;
		this.cooldownBase = 40;
		this.cooldownRandom = 80;
		this.liveBulletMax = 5;
		this.aimTurretSpeed = 0.04;
		this.bulletBounces = 0;
		this.bulletEffect = Bullet.BulletEffect.ember;
		this.bulletDamage = 0;
		this.enablePathfinding = true;
		this.seekChance = 0.01;
		this.bulletSpeed = 25 / 4.0;
		this.dealsDamage = false;

		this.coinValue = 4;

		this.description = "A tank which speeds up---its allies and becomes---explosive as a last stand";
	}

	@Override
	public void postUpdate()
	{
		if (!this.suicidal)
		{
			boolean die = true;
			for (int i = 0; i < Game.movables.size(); i++)
			{
				Movable m = Game.movables.get(i);
				if (m != this && m.team == this.team && m.dealsDamage && !m.destroy)
				{
					die = false;
					break;
				}
			}

			if (die)
				this.suicidal = true;
		}

		if (this.suicidal && !this.disabled)
		{
			this.timeUntilDeath -= Panel.frameFrequency;
			this.maxSpeed = 4.5 - 3 * Math.min(this.timeUntilDeath, 500) / 500;
			this.enableBulletAvoidance = false;
			this.enableMineAvoidance = false;
		}

		if (this.timeUntilDeath < 500)
		{
			this.colorG = this.timeUntilDeath / 500 * 180;

			if (this.timeUntilDeath < 150 && ((int) this.timeUntilDeath % 16) / 8 == 1)
			{
				this.colorR = 255;
				this.colorG = 255;
				this.colorB = 0;
			}

			Game.eventsOut.add(new EventTankUpdateColor(this));
		}

		if (this.timeUntilDeath <= 0)
		{
			Explosion e = new Explosion(this.posX, this.posY, Mine.mine_radius * 1.5, 2, true, this);
			e.explode();
			this.destroy = true;
			this.health = 0;
		}
	}

	@Override
	public void shoot()
	{
		if (this.cooldown > 0 || this.suicidal || this.disabled || this.destroy || this.liveBullets >= this.liveBulletMax)
			return;

		Ray r = new Ray(this.posX, this.posY, this.angle, this.bulletBounces, this);
		r.moveOut(5);

		if (this.avoidTimer <= 0 && (!this.hasTarget || r.getTarget() != this.targetEnemy))
			return;

		Drawing.drawing.playGlobalSound("shoot.ogg", (float) (Bullet.bullet_size / this.bulletSize));
		BulletBoost b = new BulletBoost(this.posX, this.posY, this.bulletBounces, this);
		b.effect = this.bulletEffect;
		b.team = this.team;
		b.setPolarMotion(this.angle, this.bulletSpeed);
		b.moveOut(50 / this.bulletSpeed * this.size / Game.tile_size);

		Game.movables.add(b);
		Game.eventsOut.add(new EventShootBullet(b));

		this.cooldown = this.cooldownBase;
	}

	@Override
	public void updateTarget()
	{
		if (this.suicidal)
		{
			super.updateTarget();
			return;
		}

		double nearestDist = Double.MAX_VALUE;
		Movable nearest = null;
		this.hasTarget = false;

		for (int i = 0; i < Game.movables.size(); i++)
		{
			Movable m = Game.movables.get(i);

			if (m instanceof Tank && m != this && Team.isAllied(this, m) && ((Tank) m).targetable && !((Tank) m).hidden && !((Tank) m).invulnerable && ((Tank) m).health - ((Tank) m).baseHealth < 1)
			{
				Ray r = new Ray(this.posX, this.posY, this.getAngleInDirection(m.posX, m.posY), 0, this);
				r.moveOut(5);
				if (r.getTarget() != m)
					continue;

				double distance = Movable.distanceBetween(this, m);

				if (distance < nearestDist)
				{
					this.hasTarget = true;
					nearestDist = distance;
					nearest = m;
				}
			}
		}

		this.targetEnemy = nearest;
	}

	public void reactToTargetEnemySight()
	{
		if (this.suicidal && this.targetEnemy != null)
		{
			this.overrideDirection = true;
			this.setAccelerationInDirection(targetEnemy.posX, targetEnemy.posY, acceleration);
		}
		else
		{
			if (this.targetEnemy == null)
				return;

			this.overrideDirection = true;
			if (Movable.distanceBetween(this, this.targetEnemy) < Game.tile_size * 6)
				this.setAccelerationAwayFromDirection(targetEnemy.posX, targetEnemy.posY, this.acceleration);
			else
				this.setAccelerationInDirection(targetEnemy.posX, targetEnemy.posY, this.acceleration);

		}
	}

	public boolean isInterestingPathTarget(Movable m)
	{
		return m instanceof Tank && Team.isAllied(m, this) && m != this && ((Tank) m).health - ((Tank) m).baseHealth < 1 && !(m instanceof TankGold);
	}

}