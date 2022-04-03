package tanks.tank;

import tanks.Game;
import tanks.Movable;
import tanks.Panel;
import tanks.Team;
import tanks.bullet.Bullet;
import tanks.bullet.BulletHealing;
import tanks.event.EventLayMine;
import tanks.event.EventTankUpdateColor;

/**
 * A tank which adds extra health to its allies and becomes explosive as a last stand
 */
public class TankMedic extends TankAIControlled
{
	boolean suicidal = false;
	double timeUntilDeath = 500 + this.random.nextDouble() * 250;

	public TankMedic(String name, double x, double y, double angle)
	{
		super(name, x, y, Game.tile_size, 255, 255, 255, angle, ShootAI.straight);

		this.texture = "medic.png";
		this.enableMovement = true;
		this.maxSpeed = 0.75;
		this.enableMineLaying = false;
		this.enablePredictiveFiring = false;
		this.liveBulletMax = 1;
		this.cooldownRandom = 0;
		this.cooldownBase = 0;
		this.aimTurretSpeed = 0.02;
		this.bullet.bounces = 0;
		this.bullet.effect = Bullet.BulletEffect.none;
		this.bullet.damage = 0;
		this.motionChangeChance = 0.001;
		this.enablePathfinding = true;
		this.seekChance = 0.01;
		this.dealsDamage = false;

		this.coinValue = 4;

		this.description = "A tank which adds extra health---to its allies and becomes---explosive as a last stand";
	}

	@Override
	public void postUpdate()
	{
		if (!this.suicidal)
		{
			boolean die = true;
			for (Movable m: Game.movables)
			{
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
			this.maxSpeed = 3 - 2 * Math.min(this.timeUntilDeath, 500) / 500;
			this.enableBulletAvoidance = false;
			this.enableMineAvoidance = false;
		}

		if (this.timeUntilDeath < 500)
		{
			this.colorG = this.timeUntilDeath / 500 * 255;
			this.colorB = this.timeUntilDeath / 500 * 255;

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
			Explosion e = new Explosion(this.posX, this.posY, Mine.mine_radius, 2, true, this);
			e.explode();
			this.destroy = true;
			this.health = 0;
		}
	}

	@Override
	public void shoot()
	{
		if (this.cooldown > 0 || this.suicidal || this.disabled || this.destroy)
			return;

		Ray r = new Ray(this.posX, this.posY, this.angle, this.bullet.bounces, this);
		r.moveOut(5);
		if (!this.hasTarget || r.getTarget() != this.targetEnemy)
			return;

		BulletHealing b = new BulletHealing(this.posX, this.posY, this.bullet.bounces, this);
		b.frameDamageMultipler = Panel.frameFrequency;
		b.team = this.team;
		b.setPolarMotion(this.angle, 25.0/8);
		b.moveOut(16);
		Game.movables.add(b);

		//Drawing.drawing.playGlobalSound("heal.ogg", 0.75f);

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

	@Override
	public void reactToTargetEnemySight()
	{
		if (this.suicidal && this.targetEnemy != null)
		{
			this.overrideDirection = true;
			this.setAccelerationInDirection(targetEnemy.posX, targetEnemy.posY, acceleration);
		}
	}

	@Override
	public boolean isInterestingPathTarget(Movable m)
	{
		return m instanceof Tank && Team.isAllied(m, this) && m != this && ((Tank) m).health - ((Tank) m).baseHealth < 1 && !(m instanceof TankMedic);
	}

}