import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public abstract class MobileEntity extends Entity {
	
	Vector2f facing;
	Image texture;
	
	Animation facingDown;
	Animation facingUp;

	String state;
	
	Random rand = new Random();
	float walkingSpeed;

	int scoreValue = 300;
	
	public MobileEntity(Entity parent, float x, float y) {
		super(parent);
		width = 13.0f;
		height = 13.0f;
		facing = new Vector2f(1f, 0f);
		position = new Vector2f(x, y);
		
		mask = new Rectangle(position.x, position.y, width+1, height+1);
	}

	@Override
	protected abstract void initTextures();

	
	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);
	}

	@Override
	public Shape getMask() {
		return mask;
	}
		
	protected void setMask() {
		mask.setLocation(position);
	}
	
	protected Shape getNextMask(int delta) {
		Vector2f nextPos = position.copy().add(facing.normalise().scale(0.05f*delta));
		return new Rectangle(nextPos.x, nextPos.y, mask.getWidth(), mask.getHeight());
	}
	
	protected void doMovement(int delta, GameplayState game, float speed) {
		Entity collision = collideWithWall(game.terrain, delta);
		if (collision != null) {
			facing = faceAwayFrom(collision);
		}
		position.add(facing.normalise().scale(speed*delta));	
	}
	protected void doPursueEntityMovement(int delta, GameplayState game, Entity target) {
		// First, obtain the vector towards the target
		facing = target.position.copy().sub(position);
		if (collideWithWall(game.terrain, delta) != null)
		{
			// Try to walk towards the target without going into a wall
			facing = getVerticalAxisVectorTowardsEntity(target);
			if (collideWithWall(game.terrain, delta) != null) {
				facing = getHorizontalAxisVectorTowardsEntity(target);
				if (collideWithWall(game.terrain, delta) != null) {
					return;
				}
			}
		}
	}
	protected Vector2f getVerticalAxisVectorTowardsEntity(Entity e) {
		if (e.position.y < position.y)
			return new Vector2f(0f, -1f);
		else 
			return new Vector2f(0f, 1f);
	}
	
	protected Vector2f getHorizontalAxisVectorTowardsEntity(Entity e) {
		if (e.position.x < position.x)
			return new Vector2f(-1f, 0f);
		else 
			return new Vector2f(1f, 0f);
	}
	//finds the midpoint of the scary things and finds
	//a vector away from said point. 
	protected Vector2f faceAwayFrom(Collection<Shape> scaryThings){
		Vector2f safeHeading;
		float myX = this.getCenterX();
		float myY = this.getCenterY();
		float avgX = 0;
		float avgY = 0;
		for (Shape scaryThing : scaryThings) {
			if (scaryThing != null) {
				avgX += scaryThing.getCenterX();
				avgY += scaryThing.getCenterY();
			}
		}
		avgX /= scaryThings.size();
		avgY /= scaryThings.size();
		safeHeading = new Vector2f(myX - avgX, myY-avgY);
		return safeHeading;		
	}
	
	//same, but when there's only one scary thing and its an entity
	protected Vector2f faceAwayFrom(Entity scaryThing) {
		Vector2f safeHeading;
		float myX = this.getCenterX();
		float myY = this.getCenterY();
		float scaryX = scaryThing.getCenterX();
		float scaryY = scaryThing.getCenterY(); 
		safeHeading = new Vector2f(myX - scaryX, myY-scaryY);
		return safeHeading;		
	}
}

