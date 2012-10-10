import java.util.ArrayList;
import java.util.Hashtable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public class SurvivorEntity extends MobileEntity {


	float fleeSpeed;
	float walkingSpeed;
	
	static float smellRadius;
	
	static int fleeTime;
	int remainingFleeTime;
	String state;	
	//holds a bunch of things the civ is fleeing from
	Hashtable<Integer, Shape> runningFrom;
	
	public SurvivorEntity(Entity parent, float x, float y) {
		super(parent, x, y);
		EntityManager.addSurvivorEntity(this);
		//from settings file
		smellRadius = new Float(GameplayState.prop.getProperty("civSmellRadius"));
		fleeSpeed = new Float(GameplayState.prop.getProperty("civFleeSpeed"));
		walkingSpeed = new Float(GameplayState.prop.getProperty("civWalkSpeed"));
		fleeTime = new Integer(GameplayState.prop.getProperty("civFleeTime"));
		remainingFleeTime = 0;
		runningFrom = new Hashtable<Integer, Shape>();
		state = "wait";
	}

	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);

		// A survivor becomes "rescued" when the player touches it
		if ( !state.equals("rescued") && getMask().intersects(GameplayState.player.getMask())) {
			becomeRescued(game, GameplayState.player);
		}		
		doMovement(delta, game);
		setMask();	
	}

	protected void doMovement(int delta, GameplayState game) {
		float speed = walkingSpeed;
		if (state.equals("flee")) {
			speed = fleeSpeed;
			facing = faceAwayFrom(runningFrom.values());
		} else if (state.equals("follow")) {
			//chase the player
			doPursueEntityMovement(delta, game, GameplayState.player);
		//waiting or rescued
		} else {
			speed = 0;
		}
		remainingFleeTime -= 1*delta;
		
		if (remainingFleeTime <= 0 && state.equals("flee")) {
			if (runningFrom.size() >= 1) {
				runningFrom = new Hashtable<Integer, Shape>();
			}
			remainingFleeTime = 0;
			speed = 0;
			state = "wait";
		}
		super.doMovement(delta, game, speed);
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		texture.draw(position.x, position.y, width, height);
	}

	@Override
	protected void initTextures() {
		texture = TextureManager.getTexture("textures/green_life_1.png");
	}

	public void setState(String state) {
		this.state = state;
	}
	public void fleeFrom(Entity scaryThing) {
		remainingFleeTime = fleeTime*GameplayState.pubDelta;
		runningFrom.put(scaryThing.id(), scaryThing.getMask());
		setState("flee");
	}
	public void becomeRescued(GameplayState game, PlayerEntity player) {
		setState("rescued");
		this.addChild(new SpotlightEntity(this, getCenterX(), getCenterY()));
		game.survivorsLeft--;
		player.score += scoreValue;
	}
	
	public void convertToZombie(GameplayState game, int delta) {
		delete();
		new ZombieEntity(null, position.x, position.y);
		game.survivorsLeft--;
	}
	
}
