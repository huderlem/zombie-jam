import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Properties;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public class PlayerEntity extends MobileEntity {

	Flashlight flashlight;
	Circle light;
	
	//get loaded by config file
	float smellRadius;
	float lightRadius;
	
	Vector2f movement = new Vector2f(0f, 0f);
	
	
	Animation anim;
	//movement vectors (keys pressed by user)
	PriorityQueue<Vector2f> movementQueue = new PriorityQueue<Vector2f>();
	
	int score = 0;
	
	
	public PlayerEntity(Entity parent, float x, float y) {
		super(parent, x, y);
		
		lightRadius = new Float(GameplayState.prop.getProperty("playerHaloRadius"));
		walkingSpeed = new Float(GameplayState.prop.getProperty("playerSpeed"));
		smellRadius = new Float(GameplayState.prop.getProperty("playerSmellRadius"));
		
		light = new Circle(getCenterX(), getCenterY(), lightRadius);
		flashlight = new Flashlight(this);
	}

	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);
		handleKeys(delta, gc.getInput());
		doMovement(delta, game);		
		centerLight();
	}
	
	public void doMovement(int delta, GameplayState game) {
		movement = movementQueue.poll();
		Vector2f tempMovement = movement.copy();
		//just in case? it shouldn't ever be null though
		if (movement != null) {
			//moving!
			if (movement.equals(new Vector2f(0,0)) == false) {
				// First, see if the regular direction collides with anything
				
				if (collideWithWall(game.terrain, delta) != null) {
					// Now, see if the horizontal component of the movement vector collides with anything
					movement = new Vector2f(tempMovement.x, 0f);
					if (collideWithWall(game.terrain, delta) != null) {

						// Lastly, see if the vertical component of the movement vector collides with anything
						movement = new Vector2f(0f, tempMovement.y);
						if (collideWithWall(game.terrain, delta) == null) {
							position.add(movement);
						}
					} else {
						position.add(movement);
					}
				} else {
					position.add(movement);
				}
				
				anim.update(delta);
			} else {
				// not moving
				anim.setCurrentFrame(0);
			}
		} 
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		anim.draw(position.x, position.y, width, height);
	}

	@Override
	protected void initTextures() {
		SpriteSheet ss = new SpriteSheet(TextureManager.getTexture("textures/player-anim.png"), 16, 16);
		anim = new Animation(ss, 100);
		anim.setAutoUpdate(false);
	}

	@Override
	public Shape getMask() {
		mask.setLocation(position);
		return mask;
	}
	
	protected Shape getNextMask(int delta) {
		Vector2f nextPos = position.copy().add(movement);
		return new Rectangle(nextPos.x, nextPos.y, mask.getWidth(), mask.getHeight());
	}
	
	public void handleKeys(int delta, Input input) {
		Vector2f result = new Vector2f(0, 0);
		//this has to be a series of separate if statements because they're not exclusive
		if (input.isKeyDown(Input.KEY_W)) {
			result = result.add(new Vector2f(0f, -walkingSpeed*delta));
		} 
		if (input.isKeyDown(Input.KEY_A)){
			result = result.add(new Vector2f(-walkingSpeed*delta, 0f));
		}
		if (input.isKeyDown(Input.KEY_S)){
			result = result.add(new Vector2f(0f, walkingSpeed*delta));
		}
		if (input.isKeyDown(Input.KEY_D)){
			result = result.add(new Vector2f(walkingSpeed*delta, 0f));
		}
		movementQueue.add(result);
	}
	
	public void handleMouseClicked(int button, int x, int y, int clickCount) {
		// Check for command to survivors to follow the playercd 
		if (button == 0) {
			for (SurvivorEntity e : EntityManager.survivorEntities.values()) {
				if ( e.state.equals("wait") && flashlight.illuminated(e) != null) {
					e.setState("follow");
				}
			}
		}
	}
	
	private void centerLight() {
		light.setCenterX(getCenterX());
		light.setCenterY(getCenterY());
	}
	
	public float getSmellRadius() {
		return smellRadius;
	}
	
}
