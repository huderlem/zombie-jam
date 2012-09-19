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


public class PlayerEntity extends Entity {

	Flashlight flashlight;
	Circle light;
	
	//get overwritten by config file
	float lightRadius = 30.0f;
	float walkingSpeed = .05f;
	
	
	Animation anim;
	//movement vectors (keys pressed by user)
	PriorityQueue<Vector2f> movementQueue = new PriorityQueue<Vector2f>();
	
	int score = 0;
	
	
	public PlayerEntity(Entity parent, float x, float y, Properties prop) {
		super(parent);
		position = new Vector2f(x, y);
		width = 16.0f;
		height = 16.0f;
		mask = new Rectangle(x, y, width, height);
		
		lightRadius = new Float(prop.getProperty("playerHaloRadius"));
		walkingSpeed = new Float(prop.getProperty("playerSpeed"));
		
		Vector2f center = this.getCenterPos();
		light = new Circle(center.x, center.y, lightRadius);
		flashlight = new Flashlight(this, prop);
	}

	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);
		handleKeys(delta, gc.getInput());
		doMovement(delta, game);		
		centerLight();
	}
	
	public void doMovement(int delta, GameplayState game) {
		Vector2f movement = movementQueue.poll();
		//just in case? it shouldn't ever be null though
		if (movement != null) {
			//moving!
			if (movement.equals(new Vector2f(0,0)) == false) {
				if (!collideWithWall(game.terrain, delta, movement)) {
					position.add(movement);
				}
				anim.update(delta);
			//not moving!
			} else {
				anim.setCurrentFrame(0);
			}
		} 
	}
	
	/*
	 * Returns true if this entity collides with any wallEntity in the surrounding area.
	 */
	public boolean collideWithWall(World world, int delta, Vector2f movement) {
		Shape nextMask = getNextMask(delta, movement);
		ArrayList<Integer> surroundingCells = world.getSurroundingCellContents(position.x, position.y);
		for (int id : surroundingCells) {
			// Only check collisions for occupied cells
			if (id != 0) {
				WallEntity other = (WallEntity)EntityManager.getEntity(id);
				if (nextMask.intersects(other.getMask())) {
					return true;
				}
			}
		}
		return false;
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
	
	private Shape getNextMask(int delta, Vector2f movement) {
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
		// Check for command to survivors to follow the player
		if (button == 0) {
			for (SurvivorEntity e : EntityManager.survivorEntities.values()) {
				if ( e.state.equals("wait") && flashlight.getMask().contains(e.getMask())) {
					e.setState("follow");
				}
			}
		}
	}
	
	private void centerLight() {
		Vector2f center = getCenterPos();
		light.setCenterX(center.x);
		light.setCenterY(center.y);
	}
	
	public float getSmellRadius() {
		return light.radius;
	}
	
}
