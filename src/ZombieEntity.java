import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public class ZombieEntity extends MobileEntity {
	float chasingSpeed;
	
	//number of frames left in a zombie's desire to chase after its target has stopped aggro-ing.
	int remainingChaseTime;
	static int chaseTime;
	public ZombieEntity(Entity parent, float x, float y) {
		super(parent, x, y);
		chaseTime = new Integer(GameplayState.prop.getProperty("zomChaseTime"));
		chasingSpeed = new Float(GameplayState.prop.getProperty("zomChaseSpeed"));
		walkingSpeed = new Float(GameplayState.prop.getProperty("zomWalkSpeed"));
		state = "wander";
	}

	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);		
		if (state == "dying") {
			die(delta, game.player);
		} else if (GameplayState.player.flashlight.illuminated(this) == null && collideWithSpotlight(EntityManager.spotlightEntities.values(), delta, getNextMask(delta)) == null) {
			remainingChaseTime -= 1 * delta;
			// If the zombie is within the smell radius of the player or in the flashlight, pursue the player.
			if (GameplayState.player.getCenterPos().distance(this.getCenterPos()) < GameplayState.player.getSmellRadius()) {
				remainingChaseTime = chaseTime * delta;
				state = "pursuePlayer";
			} 
			
			SurvivorEntity nearestSurvivor = getNearSurvivor(EntityManager.survivorEntities.values());
			if ( !state.equals("pursuePlayer") && nearestSurvivor != null) {
				remainingChaseTime = chaseTime * delta;
				nearestSurvivor.fleeFrom(this);
				state = "pursueSurvivor";
			} 
			
			if (state.equals("pursueSurvivor") && nearestSurvivor != null) {
				if (getMask().intersects(nearestSurvivor.getMask())) {
					remainingChaseTime = 0;
					nearestSurvivor.convertToZombie(game, delta);
					state = "wander";
				}
			}
			
			if (remainingChaseTime <= 0) {
				remainingChaseTime = 0;
				state = "wander";
			}
			
		//not dead, not in the dark - flee!
		} else {
			remainingChaseTime = 0;
			state = "fleeLight";
		}
		doMovement(delta, game);
		setMask();	
	}
		
	protected void doMovement(int delta, GameplayState game) {
		//don't check for dying because the object has been deleted by now if the zom's dead.
		float speed = walkingSpeed;
		if (state.equals("fleeLight")) {
			speed = (float)(chasingSpeed*1.3);
			doFleeLightMovement(delta, game);
		} else if (state.equals("pursueSurvivor")) {
			speed = chasingSpeed;
			doPursueSurvivorMovement(delta, game);
		} else if (state.equals("pursuePlayer")) {
			speed = chasingSpeed;
			doPursuePlayerMovement(delta, game);
		} else {
			doWanderMovement(delta, game);
		}
		super.doMovement(delta, game, speed);
		this.getAnimation().update(delta);
	}
	
	private void doWanderMovement(int delta, GameplayState game) {
		//send the merry zombie safely away from any nasty walls
		//he might be colliding with. with a little random.
		GridSpace wallCollision = collideWithWall(game.terrain, delta);
		if (wallCollision != null) {
			Vector2f safeHeading = faceAwayFrom(wallCollision);
			facing = facing.add(rand.nextInt(60) - 60);
		}
	}
	
	private void doPursuePlayerMovement(int delta, GameplayState game) {
		doPursueEntityMovement(delta, game, GameplayState.player);
		if (this.mask.intersects(GameplayState.player.getMask())) {
			System.out.println("MUNCH MUNCH EAT YOUR BRAINS");
		}
	}
	
	private void doPursueSurvivorMovement(int delta, GameplayState game) {
		SurvivorEntity target = getNearSurvivor(EntityManager.survivorEntities.values());
		if (target != null) {
			doPursueEntityMovement(delta, game, target);
		} else {
			doWanderMovement(delta, game);
		}
	}
	private void doFleeLightMovement(int delta, GameplayState game) {
		SpotlightEntity spotlight = collideWithSpotlight(EntityManager.spotlightEntities.values(), delta, getNextMask(delta));
		Shape flashlight = GameplayState.player.flashlight.illuminated(this);
		
		ArrayList<Shape> scaryThings = new ArrayList<Shape>();
		Shape scaryLight1 = (spotlight != null) ? spotlight.getMask() : flashlight;
		Shape scaryLight2 = (flashlight != null) ? flashlight : scaryLight1;
		scaryThings.add(scaryLight1);
		scaryThings.add(scaryLight2);
		scaryThings.add(GameplayState.player.getMask());
		facing = faceAwayFrom(scaryThings);
	}
	private SurvivorEntity getNearSurvivor(Collection<SurvivorEntity> survivors) {
		for (SurvivorEntity s : survivors) {
			if (getCenterPos().distance(s.getCenterPos()) < s.smellRadius) {
				return s;
			}
		}
		return null;
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		this.getAnimation().draw(position.x, position.y, width, height);
	}

	@Override
	protected void initTextures() {
		SpriteSheet ss = new SpriteSheet(TextureManager.getTexture("textures/zombie-anim.gif"), 16, 16);
		
		facingDown = new Animation(ss, new int[] {0, 0, 1, 0, 0, 0, 2, 0}, new int[] {130, 200, 130, 200});
		facingDown.setAutoUpdate(false);
		facingUp = new Animation(ss, new int[] {0, 1, 1, 1, 0, 1, 2, 1}, new int[] {130, 200, 130, 200});
		facingUp.setAutoUpdate(false);
	}

	private void die(int delta, PlayerEntity player) {
		delete();
		player.score += scoreValue;
	}
	
	/*
	 * Returns the appropriate Animation depending on the Zombie's current facing vector.
	 */
	private Animation getAnimation() {
		double theta = facing.getTheta();
		if (theta >= 180 && theta < 360) {
			return facingUp;
		} else {
			return facingDown;
		}
	}
	
	

}
