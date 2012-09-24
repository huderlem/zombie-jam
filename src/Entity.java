import java.util.ArrayList;
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
	
	public void delete() {
		deleted = true;
		EntityManager.removeEntity(this);
	}
		
	
	/*
	 * Returns true if this entity collides with any wallEntity in the surrounding area.
	 */
	public boolean collideWithWall(World world, int delta) {
		Shape nextMask = getNextMask(delta);
		ArrayList<GridSpace> surroundingWalls = world.getSurroundingCellWalls(position.x, position.y);
		for (GridSpace wall : surroundingWalls) {
			Shape mask = wall.getMask();
			if ( !(nextMask.getMaxY() <= mask.getMinY() ||
					nextMask.getMinY() >= mask.getMaxY() ||
					nextMask.getMaxX() <= mask.getMinX() ||
					nextMask.getMinX() >= mask.getMaxX())) {
				System.out.println("nextMask.MAX-Y: "+nextMask.getMaxY()+", wall.MIN-Y: "+mask.getMinY()+"; "+(nextMask.getMaxY() < mask.getMinY()));
				System.out.println("nextMask.MIN-Y: "+nextMask.getMinY()+", wall.MAX-Y: "+mask.getMaxY()+"; "+(nextMask.getMinY() > mask.getMaxY()));
				System.out.println("nextMask.MAX-X: "+nextMask.getMaxX()+", wall.MIN-X: "+mask.getMinX()+"; "+(nextMask.getMaxX() < mask.getMinX()));
				System.out.println("nextMask.MIN-X: "+nextMask.getMinX()+", wall.MAX-X: "+mask.getMaxX()+"; "+(nextMask.getMinX() > mask.getMaxX()));
				return true;
			}
		}
		return false;
	}
	
	protected Shape getNextMask(int delta) {
		return mask;
	}
}
