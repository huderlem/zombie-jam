import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public class PlayerEntity extends Entity {

	Flashlight flashlight;
	Circle light;
	float lightRadius = 30.0f;
	
	float walkingSpeed = .080f;
	ArrayList<Integer> movementQueue = new ArrayList<Integer>();
	
	int score = 0;
	
	
	public PlayerEntity(Entity parent, float x, float y) {
		super(parent);
		position = new Vector2f(x, y);
		width = 16.0f;
		height = 16.0f;
		mask = new Rectangle(x, y, width, height);
		
		Vector2f center = this.getCenterPos();
		light = new Circle(center.x, center.y, lightRadius);
		flashlight = new Flashlight(this);
	}

	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);
		doMovement(delta, game);
		
		centerLight();
	}
	
	public void doMovement(int delta, GameplayState game) {
		if (movementQueue.size() > 0) {
			Vector2f movement = null;
			int i = 0;
			while (i < movementQueue.size()) {
				switch (movementQueue.get(i)) {
					case Input.KEY_A: 
						movement = new Vector2f(-walkingSpeed*delta, 0f);
						break;
					case Input.KEY_W:
						movement = new Vector2f(0f, -walkingSpeed*delta);
						break;
					case Input.KEY_D:
						movement = new Vector2f(walkingSpeed*delta, 0f);
						break;
					case Input.KEY_S:
						movement = new Vector2f(0f, walkingSpeed*delta);
						break;
					default:
						break;
				}
				
				if ( !collideWithWall(game.terrain, delta, movement)) {
					position.add(movement);
					break;
				}
				i++;
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
		texture.draw(position.x, position.y, width, height);
		//g.draw(light);
	}

	@Override
	protected void initTextures() {
		texture = TextureManager.getTexture("textures/bomb.png");
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
	
	public void handleKeyPress(int key, char c) {
		// Check for WASD movement keys
		if (key == Input.KEY_D || key == Input.KEY_W || key == Input.KEY_S || key == Input.KEY_A) {
			// Insert a new movement to the beginning of the movement queue
			if ( !movementQueue.contains(key) )
			{
				movementQueue.add(0, key);
			}
		}
	}
	
	public void handleKeyRelease(int key, char c) {
		// Check for WASD movement keys
		if (key == Input.KEY_D || key == Input.KEY_W || key == Input.KEY_S || key == Input.KEY_A) {
			// Insert a new movement to the beginning of the movement queue
			if ( movementQueue.contains(key) )
			{
				movementQueue.remove((Integer)key);
			}
		}
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
		return 50f;
	}
	
}
