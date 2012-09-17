import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public class SurvivorEntity extends Entity {

	String state = "wait";
	Vector2f facing = new Vector2f(1f, 0f);
	Image texture;
	
	float walkingSpeed = 0.1f;
	float smellRadius = 30.0f;
	int life = 1000;
	
	static int pointValue = 100;
	
	public SurvivorEntity(Entity parent, float x, float y) {
		super(parent);
		EntityManager.addSurvivorEntity(this);
		
		position = new Vector2f(x, y);
		width = 16.0f;
		height = 16.0f;
		mask = new Rectangle(position.x, position.y, width, height);
	}

	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);
		
		doMovement(delta, game);
		setMask();
		
		// A survivor becomes "rescued" when the player touches it
		if ( !state.equals("rescued") && getMask().intersects(GameplayState.player.getMask())) {
			becomeRescued(game, GameplayState.player);
		}
		
	}

	private void doMovement(int delta, GameplayState game) {
		if (state.equals("wait") || state.equals("rescued")) {
			return;
		} else if (state.equals("follow")) {
			doPursuePlayerMovement(delta, game);
		}
	}
	
	private void doPursuePlayerMovement(int delta, GameplayState game) {
		// First, obtain the vector towards the player
		facing = GameplayState.player.position.copy().sub(position);
		if (collideWithWall(game.terrain, delta, getNextMask(delta)))
		{
			// Try to walk towards the player without going into a wall
			facing = getVerticalAxisVectorTowardsPlayer(GameplayState.player);
			if (collideWithWall(game.terrain, delta, getNextMask(delta))) {
				facing = getHorizontalAxisVectorTowardsPlayer(GameplayState.player);
				if (collideWithWall(game.terrain, delta, getNextMask(delta))) {
					return;
				}
			}
		}
		
		position.add(facing.normalise().scale(walkingSpeed*delta));
	}
	
	/*
	 * Returns true if this entity collides with any wallEntity in the surrounding area.
	 */
	public boolean collideWithWall(World world, int delta, Shape mask) {
		ArrayList<Integer> surroundingCells = world.getSurroundingCellContents(position.x, position.y);
		for (int id : surroundingCells) {
			// Only check collisions for occupied cells
			if (id != 0) {
				WallEntity other = (WallEntity)EntityManager.getEntity(id);
				if (mask.intersects(other.getMask())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Vector2f getVerticalAxisVectorTowardsPlayer(PlayerEntity player) {
		if (player.position.y < position.y)
			return new Vector2f(0f, -1f);
		else 
			return new Vector2f(0f, 1f);
	}
	
	private Vector2f getHorizontalAxisVectorTowardsPlayer(PlayerEntity player) {
		if (player.position.x < position.x)
			return new Vector2f(-1f, 0f);
		else 
			return new Vector2f(1f, 0f);
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

	@Override
	public Shape getMask() {
		return mask;
	}
	
	private Shape getNextMask(int delta) {
		Vector2f nextPos = position.copy().add(facing.normalise().scale(walkingSpeed*delta));
		return new Rectangle(nextPos.x, nextPos.y, mask.getWidth(), mask.getHeight());
	}
	
	private void setMask() {
		mask.setLocation(position);
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public void becomeRescued(GameplayState game, PlayerEntity player) {
		setState("rescued");
		Vector2f center = getCenterPos();
		this.addChild(new SpotlightEntity(this, center.x, center.y));
		game.survivorsLeft--;
		player.score += SurvivorEntity.pointValue;
	}
	
	public void convertToZombie(GameplayState game, int delta) {
		life -= delta;
		if (life < 0) {
			delete();
			new ZombieEntity(null, position.x, position.y);
			game.survivorsLeft--;
		}
	}
	
}
