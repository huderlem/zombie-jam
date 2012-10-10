import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public abstract class Entity {
	
	Entity parent;
	Hashtable<Integer, Entity> children = new Hashtable<Integer, Entity>();
	
	Vector2f position;
	protected Shape mask;
	
	private int id;
	float width;
	float height;
	double scale = 1.0;
	
	boolean deleted = false;
	
	
	public Entity(Entity parent)
	{
		this.parent = parent;
		id = EntityManager.registerEntity(this);
		
		initTextures();
	}
	
	public void update(GameContainer gc, int delta, GameplayState game) {
	}
	public void render(Graphics g) {
	}
	protected abstract void initTextures();
	public abstract Shape getMask();
	
	public void setParent(Entity parent) {
		this.parent = parent;
	}
	
	public void addChild(Entity child) {
		children.put(child.id(), child);
	}
	
	public int id()
	{
		return this.id;
	}
	
	public Vector2f getCenterPos() {
		return new Vector2f(position.x + width/2.0f, position.y + height/2.0f);
	}
	
	public float getCenterX(){
		return mask.getCenterX();
	}
	
	public float getCenterY(){
		return mask.getCenterY();
	}
	
	public void delete() {
		deleted = true;
		EntityManager.removeEntity(this);
	}
		
	
	/*
	 * Returns true if this entity collides with any wallEntity in the surrounding area.
	 */
	public GridSpace collideWithWall(World world, int delta) {
		Shape nextMask = getNextMask(delta);
		ArrayList<GridSpace> surroundingWalls = world.getSurroundingCellWalls(position.x, position.y);
		for (GridSpace wall : surroundingWalls) {
			Shape mask = wall.getMask();
			if (nextMask.intersects(mask)) return wall;
		}
		return null;
	}
	
	/*
	 * Checks for a collision with a spotlightEntity
	 */
	public SpotlightEntity collideWithSpotlight(Collection<SpotlightEntity> spotlights, int delta, Shape mask) {
		for (SpotlightEntity s : spotlights) {
			// Check the distance from spotlights for optimization here!
			if (s.getMask().intersects(mask) || s.getMask().contains(mask)) {
				return s;
			}
		}
		return null;
	}
	
	protected Shape getNextMask(int delta) {
		return mask;
	}
}
